package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.MAX_DATE_STRING_LENGTH
import io.islandtime.appendDate
import io.islandtime.internal.MONTHS_IN_YEAR
import io.islandtime.measures.*
import io.islandtime.monthsSinceYear0
import io.islandtime.parser.DateTimeParsers
import io.islandtime.parser.GroupedDateTimeParser
import io.islandtime.parser.throwParserFieldResolutionException
import kotlin.random.Random
import kotlin.random.nextLong

/**
 * An inclusive range of dates
 */
class DateRange(
    start: Date,
    endInclusive: Date
) : DateDayProgression(start, endInclusive, 1.days),
    ClosedRange<Date> {

    override val start: Date get() = first
    override val endInclusive: Date get() = last

    override fun isEmpty(): Boolean = firstUnixEpochDay > lastUnixEpochDay

    override fun toString(): String {
        return if (isEmpty()) {
            ""
        } else {
            buildString(2 * MAX_DATE_STRING_LENGTH + 1) {
                appendDate(start)
                append('/')
                appendDate(endInclusive)
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
     */
    fun asPeriod(): Period {
        return if (isEmpty()) {
            Period.ZERO
        } else {
            periodBetween(start, endInclusive + 1.days)
        }
    }

    /**
     * Get the number of years in a range of dates. A year is considered to have passed if twelve full months have
     * passed between the start date and end date, according to the definition of 'month' in [lengthInMonths].
     */
    val lengthInYears
        get() = if (isEmpty()) {
            0.years
        } else {
            yearsBetween(start, endInclusive + 1.days)
        }

    /**
     * Get the number of months in a range of dates. A month is considered to have passed if the day of the end month is
     * greater than or equal to the day of the start month minus one (as a range is inclusive).
     */
    val lengthInMonths
        get() = if (isEmpty()) {
            0.months
        } else {
            monthsBetween(start, endInclusive + 1.days)
        }

    /**
     * Get the number of days in a range of dates. As a range is inclusive, if the start and end date are the same, the
     * result will be one day.
     */
    val lengthInDays
        get() = if (isEmpty()) {
            0L.days
        } else {
            daysBetween(start, endInclusive + 1.days)
        }


    companion object {
        /**
         * A range containing zero days
         */
        val EMPTY = DateRange(Date.fromUnixEpochDay(1L), Date.fromUnixEpochDay(0L))
    }
}

fun String.toDateRange() = toDateRange(DateTimeParsers.Iso.Extended.DATE_RANGE)

fun String.toDateRange(parser: GroupedDateTimeParser): DateRange {
    val results = parser.parse(this)
    val start = results[0].toDate()
    val end = results[1].toDate()

    return if (start != null && end != null) {
        start..end
    } else {
        throwParserFieldResolutionException<DateRange>(this)
    }
}

/**
 * Return a random date within the range using the default random number generator
 */
fun DateRange.random(): Date = random(Random)

/**
 * Return a random date within the range using the supplied random number generator
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

fun yearsBetween(start: Date, endExclusive: Date): IntYears {
    return monthsBetween(start, endExclusive).inYears
}

fun monthsBetween(start: Date, endExclusive: Date): IntMonths {
    val startDays = start.monthsSinceYear0 * 32L + start.dayOfMonth
    val endDays = endExclusive.monthsSinceYear0 * 32L + endExclusive.dayOfMonth
    return ((endDays - startDays) / 32).toInt().months
}

fun daysBetween(start: Date, endExclusive: Date): LongDays {
    return endExclusive.daysSinceUnixEpoch - start.daysSinceUnixEpoch
}