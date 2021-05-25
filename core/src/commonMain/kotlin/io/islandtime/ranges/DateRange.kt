package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateTimeField
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.buildIsoString

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

    @Deprecated(
        message = "Replace with toPeriod()",
        replaceWith = ReplaceWith("this.toPeriod()", "io.islandtime.ranges.toPeriod"),
        level = DeprecationLevel.WARNING
    )
    fun asPeriod(): Period = toPeriod()

    companion object {
        /**
         * An empty range.
         */
        val EMPTY: DateRange = DateRange(Date.fromDayOfUnixEpoch(1L), Date.fromDayOfUnixEpoch(0L))

        /**
         * An unbounded (ie. infinite) range of dates.
         */
        val UNBOUNDED: DateRange = DateRange(Date.MIN, Date.MAX)
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

@Deprecated(
    message = "Replace with Period.between()",
    replaceWith = ReplaceWith(
        "Period.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Period"
    ),
    level = DeprecationLevel.WARNING
)
fun periodBetween(start: Date, endExclusive: Date): Period = Period.between(start, endExclusive)

@Deprecated(
    message = "Replace with Years.between()",
    replaceWith = ReplaceWith(
        "Years.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Years"
    ),
    level = DeprecationLevel.WARNING
)
fun yearsBetween(start: Date, endExclusive: Date): Years = Years.between(start, endExclusive)

@Deprecated(
    message = "Replace with Months.between()",
    replaceWith = ReplaceWith(
        "Months.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Months"
    ),
    level = DeprecationLevel.WARNING
)
fun monthsBetween(start: Date, endExclusive: Date): Months = Months.between(start, endExclusive)

@Deprecated(
    message = "Replace with Weeks.between()",
    replaceWith = ReplaceWith(
        "Weeks.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Weeks"
    ),
    level = DeprecationLevel.WARNING
)
fun weeksBetween(start: Date, endExclusive: Date): Weeks = Weeks.between(start, endExclusive)

@Deprecated(
    message = "Replace with Days.between()",
    replaceWith = ReplaceWith(
        "Days.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Days"
    ),
    level = DeprecationLevel.WARNING
)
fun daysBetween(start: Date, endExclusive: Date): Days = Days.between(start, endExclusive)
