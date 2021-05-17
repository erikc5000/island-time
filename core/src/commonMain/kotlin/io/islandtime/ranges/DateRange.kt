package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateTimeField
import io.islandtime.internal.MONTHS_PER_YEAR
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.ranges.internal.throwUnboundedIntervalException

/**
 * An inclusive range of dates.
 *
 * [Date.MIN] and [Date.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end.
 */
class DateRange(
    override val start: Date = Date.MIN,
    override val endInclusive: Date = Date.MAX
) : DateDayProgression(),
    Interval<Date>,
    ClosedRange<Date> {

    override val endExclusive: Date get() = endInclusive + 1.days

    override fun hasUnboundedStart(): Boolean = start == Date.MIN
    override fun hasUnboundedEnd(): Boolean = endInclusive == Date.MAX
    override fun contains(value: Date): Boolean = super.contains(value)

    override val first: Date get() = start
    override val last: Date get() = endInclusive
    override val step: Days get() = 1.days

    override fun isEmpty(): Boolean {
        return start > endInclusive || endInclusive == Date.MIN || start == Date.MAX
    }

    /**
     * Converts this range to a string in ISO-8601 extended format.
     */
    override fun toString(): String = buildIsoString(
        maxElementSize = MAX_DATE_STRING_LENGTH,
        inclusive = true,
        appendFunction = StringBuilder::appendDate
    )

    override fun equals(other: Any?): Boolean {
        return other is DateRange &&
            ((isEmpty() && other.isEmpty()) ||
                (start == other.start && endInclusive == other.endInclusive))
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * start.hashCode() + endInclusive.hashCode())
    }

    /**
     * Converts this range into a [Period] of the same length. As a range is inclusive, if the start and end date are
     * the same, the resulting period will contain one day.
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
     * The number of whole years in this range.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInYears: Years
        get() = when {
            isEmpty() -> 0.years
            isBounded() -> yearsBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole months in this range.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInMonths: Months
        get() = when {
            isEmpty() -> 0.months
            isBounded() -> monthsBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole weeks in this range.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInWeeks: Weeks
        get() = when {
            isEmpty() -> 0.weeks
            isBounded() -> weeksBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of days in this range. As a range is inclusive, if the start and end date are the same, the result
     * will be one day.
     * @throws UnsupportedOperationException if the range isn't bounded
     */
    val lengthInDays: Days
        get() = when {
            isEmpty() -> 0.days
            isBounded() -> daysBetween(start, endInclusive + 1.days)
            else -> throwUnboundedIntervalException()
        }


    companion object {
        /**
         * An empty range.
         */
        val EMPTY = DateRange(Date.fromDayOfUnixEpoch(1L), Date.fromDayOfUnixEpoch(0L))

        /**
         * An unbounded (ie. infinite) range of dates.
         */
        val UNBOUNDED = DateRange(Date.MIN, Date.MAX)
    }
}

/**
 * Converts a string to a [DateRange].
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
fun String.toDateRange(): DateRange = toDateRange(DateTimeParsers.Iso.Extended.DATE_RANGE)

/**
 * Converts a string to a [DateRange] using a specific parser.
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
 * Creates a [DateRange] containing all of the days from this date up to, but not including [to].
 */
infix fun Date.until(to: Date): DateRange = DateRange(this, to - 1.days)

/**
 * Gets the [Period] between two dates.
 */
fun periodBetween(start: Date, endExclusive: Date): Period {
    var totalMonths = endExclusive.monthsSinceYear0 - start.monthsSinceYear0
    val dayDiff = endExclusive.dayOfMonth - start.dayOfMonth

    val days = when {
        totalMonths > 0 && dayDiff < 0 -> {
            totalMonths--
            val testDate = start + totalMonths.months
            daysBetween(testDate, endExclusive)
        }
        totalMonths < 0 && dayDiff > 0 -> {
            totalMonths++
            (dayDiff - endExclusive.lengthOfMonth.value).days
        }
        else -> dayDiff.days
    }
    val years = (totalMonths / MONTHS_PER_YEAR).years
    val months = (totalMonths % MONTHS_PER_YEAR).months

    return periodOf(years, months, days)
}

/**
 * Gets the number of whole years between two dates.
 */
fun yearsBetween(start: Date, endExclusive: Date): Years {
    return monthsBetween(start, endExclusive).inWholeYears
}

/**
 * Gets the number of whole months between two dates.
 */
fun monthsBetween(start: Date, endExclusive: Date): Months {
    val startDays = start.monthsSinceYear0 * 32L + start.dayOfMonth
    val endDays = endExclusive.monthsSinceYear0 * 32L + endExclusive.dayOfMonth
    return ((endDays - startDays) / 32).months
}

/**
 * Gets the number of whole weeks between two dates.
 */
fun weeksBetween(start: Date, endExclusive: Date): Weeks {
    return daysBetween(start, endExclusive).inWholeWeeks
}

/**
 * Gets the number of days between two dates.
 */
fun daysBetween(start: Date, endExclusive: Date): Days {
    return endExclusive.daysSinceUnixEpoch - start.daysSinceUnixEpoch
}
