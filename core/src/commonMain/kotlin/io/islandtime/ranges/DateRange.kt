package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.MAX_DATE_STRING_LENGTH
import io.islandtime.appendDate
import io.islandtime.internal.MONTHS_IN_YEAR
import io.islandtime.measures.*
import io.islandtime.monthsSinceYear0
import io.islandtime.parser.*
import io.islandtime.parser.expectingGroupCount
import io.islandtime.parser.throwParserFieldResolutionException
import io.islandtime.ranges.internal.throwUnboundedIntervalException
import kotlin.random.Random
import kotlin.random.nextLong

/**
 * An inclusive range of dates.
 */
class DateRange(
    start: Date = Date.MIN,
    endInclusive: Date = Date.MAX
) : DateDayProgression(start, endInclusive, 1.days),
    ClosedRange<Date> {

    val hasUnboundedStart: Boolean get() = start == Date.MIN
    val hasUnboundedEnd: Boolean get() = endInclusive == Date.MAX

    val hasBoundedStart: Boolean get() = !hasUnboundedStart
    val hasBoundedEnd: Boolean get() = !hasUnboundedEnd
    val isBounded: Boolean get() = hasBoundedStart && hasBoundedEnd
    val isUnbounded: Boolean get() = hasUnboundedStart && hasUnboundedEnd

    override val start: Date get() = first
    override val endInclusive: Date get() = last

    override fun isEmpty(): Boolean {
        return firstUnixEpochDay > lastUnixEpochDay || endInclusive == Date.MIN || start == Date.MAX
    }

    override fun toString(): String {
        return if (isEmpty()) {
            ""
        } else {
            buildString(2 * MAX_DATE_STRING_LENGTH + 1) {
                if (hasBoundedStart) {
                    appendDate(start)
                } else {
                    append("..")
                }

                append('/')

                if (hasBoundedEnd) {
                    appendDate(endInclusive)
                } else {
                    append("..")
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is DateRange && (isEmpty() && other.isEmpty() ||
            first == other.first && last == other.last)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * first.hashCode() + last.hashCode())
    }

    /**
     * Convert a range of dates into a period containing each day in the range. As a range is inclusive, if the start
     * and end date are the same, the resulting period will contain one day.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    fun asPeriod(): Period {
        return when {
            isEmpty() -> Period.ZERO
            isBounded -> periodBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of years in a range of dates. A year is considered to have passed if twelve full months have
     * passed between the start date and end date, according to the definition of 'month' in [lengthInMonths].
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInYears
        get() = when {
            isEmpty() -> 0.years
            isBounded -> yearsBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of months in a range of dates. A month is considered to have passed if the day of the end month is
     * greater than or equal to the day of the start month minus one (as a range is inclusive).
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInMonths
        get() = when {
            isEmpty() -> 0.months
            isBounded -> monthsBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of days in a range of dates. As a range is inclusive, if the start and end date are the same, the
     * result will be one day.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInDays
        get() = when {
            isEmpty() -> 0L.days
            isBounded -> daysBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }


    companion object {
        /**
         * A range containing zero days.
         */
        val EMPTY = DateRange(Date.fromUnixEpochDay(1L), Date.fromUnixEpochDay(0L))

        /**
         * An unbounded (ie. infinite) range of dates.
         */
        val UNBOUNDED = DateRange(Date.MIN, Date.MAX)
    }
}

fun String.toDateRange() = toDateRange(DateTimeParsers.Iso.Extended.DATE_RANGE)

fun String.toDateRange(parser: GroupedDateTimeParser): DateRange {
    val results = parser.parse(this).expectingGroupCount<DateTimeInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0].fields[DateTimeField.IS_UNBOUNDED] == 1L -> Date.MIN
        else -> results[0].toDate() ?: throwParserFieldResolutionException<DateRange>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1].fields[DateTimeField.IS_UNBOUNDED] == 1L -> Date.MAX
        else -> results[1].toDate() ?: throwParserFieldResolutionException<DateRange>(this)
    }

    return when {
        start != null && end != null -> start..end
        start == null && end == null -> DateRange.EMPTY
        else -> throw DateTimeParseException("Ranges with unknown start or end are not supported")
    }
}

/**
 * Return a random date within the range using the default random number generator.
 */
fun DateRange.random(): Date = random(Random)

/**
 * Return a random date within the range using the supplied random number generator.
 */
fun DateRange.random(random: Random): Date {
    try {
        val longRange = first.unixEpochDay..last.unixEpochDay
        return Date.fromUnixEpochDay(random.nextLong(longRange))
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

/**
 * Get a range containing all of the days up to, but not including [to]
 */
infix fun Date.until(to: Date) = DateRange(this, to - 1L.days)

/**
 * Get the [Period] between two dates.
 */
fun periodBetween(start: Date, endExclusive: Date): Period {
    var totalMonths = endExclusive.monthsSinceYear0.toLong() - start.monthsSinceYear0
    val dayDiff = (endExclusive.dayOfMonth - start.dayOfMonth).days

    val days = when {
        totalMonths > 0 && dayDiff.value < 0 -> {
            totalMonths--
            val testDate = start + totalMonths.months
            daysBetween(testDate, endExclusive).toInt()
        }
        totalMonths < 0 && dayDiff.value > 0 -> {
            totalMonths++
            dayDiff - endExclusive.lengthOfMonth
        }
        else -> dayDiff
    }
    val years = (totalMonths / MONTHS_IN_YEAR).years.toInt()
    val months = (totalMonths % MONTHS_IN_YEAR).months.toInt()

    return periodOf(years, months, days)
}

/**
 * Get the number of whole years between two dates.
 */
fun yearsBetween(start: Date, endExclusive: Date): IntYears {
    return monthsBetween(start, endExclusive).inYears
}

/**
 * Get the number of whole months between two dates.
 */
fun monthsBetween(start: Date, endExclusive: Date): IntMonths {
    val startDays = start.monthsSinceYear0 * 32L + start.dayOfMonth
    val endDays = endExclusive.monthsSinceYear0 * 32L + endExclusive.dayOfMonth
    return ((endDays - startDays) / 32).toInt().months
}

/**
 * Get the number of days between two dates.
 */
fun daysBetween(start: Date, endExclusive: Date): LongDays {
    return endExclusive.daysSinceUnixEpoch - start.daysSinceUnixEpoch
}