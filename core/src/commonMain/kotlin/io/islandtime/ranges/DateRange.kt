package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.MAX_DATE_STRING_LENGTH
import io.islandtime.appendDate
import io.islandtime.base.DateProperty
import io.islandtime.internal.MONTHS_PER_YEAR
import io.islandtime.measures.*
import io.islandtime.monthsSinceYear0
import io.islandtime.parser.*
import io.islandtime.parser.expectingGroupCount
import io.islandtime.parser.throwParserPropertyResolutionException
import io.islandtime.ranges.internal.throwUnboundedIntervalException
import kotlin.random.Random
import kotlin.random.nextLong

/**
 * An inclusive range of dates.
 *
 * [Date.MIN] and [Date.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end.
 */
class DateRange(
    start: Date = Date.MIN,
    endInclusive: Date = Date.MAX
) : DateDayProgression(start, endInclusive, 1.days),
    ClosedRange<Date> {

    fun hasUnboundedStart(): Boolean = start == Date.MIN
    fun hasUnboundedEnd(): Boolean = endInclusive == Date.MAX

    fun hasBoundedStart(): Boolean = !hasUnboundedStart()
    fun hasBoundedEnd(): Boolean = !hasUnboundedEnd()
    fun isBounded(): Boolean = hasBoundedStart() && hasBoundedEnd()
    fun isUnbounded(): Boolean = hasUnboundedStart() && hasUnboundedEnd()

    override val start: Date get() = first
    override val endInclusive: Date get() = last

    override fun isEmpty(): Boolean {
        return firstUnixEpochDay > lastUnixEpochDay || endInclusive == Date.MIN || start == Date.MAX
    }

    /**
     * Convert this interval to a string in ISO-8601 extended format.
     */
    override fun toString(): String {
        return if (isEmpty()) {
            ""
        } else {
            buildString(2 * MAX_DATE_STRING_LENGTH + 1) {
                if (hasBoundedStart()) {
                    appendDate(start)
                } else {
                    append("..")
                }

                append('/')

                if (hasBoundedEnd()) {
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
            isBounded() -> periodBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of years in the range. A year is considered to have passed if twelve full months have passed
     * between the start date and end date, according to the definition of 'month' in [lengthInMonths].
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInYears
        get() = when {
            isEmpty() -> 0.years
            isBounded() -> yearsBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of months in the range. A month is considered to have passed if the day of the end month is
     * greater than or equal to the day of the start month minus one (as a range is inclusive).
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInMonths
        get() = when {
            isEmpty() -> 0.months
            isBounded() -> monthsBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of weeks in the range.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInWeeks
        get() = when {
            isEmpty() -> 0L.weeks
            isBounded() -> weeksBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of days in the range. As a range is inclusive, if the start and end date are the same, the result
     * will be one day.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInDays
        get() = when {
            isEmpty() -> 0L.days
            isBounded() -> daysBetween(start, endInclusive + 1.days)
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

/**
 * Convert a string to a [DateRange].
 *
 * The string is assumed to be an ISO-8601 time interval representation in extended format. The output of
 * [DateRange.toString] can be safely parsed using this method.
 *
 * Examples:
 * - `1990-01-04/1991-08-30`
 * - `../1991-08-30`
 * - `1990-01-04/..`
 * - `../..`
 * - (empty string)
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toDateRange() = toDateRange(DateTimeParsers.Iso.Extended.DATE_RANGE)

/**
 * Convert a string to a [DateRange] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed range is invalid
 */
fun String.toDateRange(
    parser: GroupedDateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): DateRange {
    val results = parser.parse(this, settings).expectingGroupCount<DateTimeInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0][DateProperty.IsFarPast] == true -> Date.MIN
        else -> results[0].toDate() ?: throwParserPropertyResolutionException<DateRange>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1][DateProperty.IsFarFuture] == true -> Date.MAX
        else -> results[1].toDate() ?: throwParserPropertyResolutionException<DateRange>(this)
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
    var totalMonths = endExclusive.monthsSinceYear0 - start.monthsSinceYear0
    val dayDiff = (endExclusive.dayOfMonth - start.dayOfMonth).days

    val days = when {
        totalMonths > 0 && dayDiff.value < 0 -> {
            totalMonths--
            val testDate = start + totalMonths.months
            daysBetween(testDate, endExclusive).toIntDaysUnchecked()
        }
        totalMonths < 0 && dayDiff.value > 0 -> {
            totalMonths++
            (dayDiff.value - endExclusive.lengthOfMonth.value).days
        }
        else -> dayDiff
    }
    val years = (totalMonths / MONTHS_PER_YEAR).toInt().years
    val months = (totalMonths % MONTHS_PER_YEAR).toInt().months

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
 * Get the number of whole weeks between two dates.
 */
fun weeksBetween(start: Date, endExclusive: Date): LongWeeks {
    return daysBetween(start, endExclusive).inWeeks
}

/**
 * Get the number of days between two dates.
 */
fun daysBetween(start: Date, endExclusive: Date): LongDays {
    return endExclusive.daysSinceUnixEpoch - start.daysSinceUnixEpoch
}