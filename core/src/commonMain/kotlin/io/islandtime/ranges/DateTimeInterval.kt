package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateTimeField
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.MAX_INCLUSIVE_END_DATE_TIME
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.serialization.DateTimeIntervalIsoSerializer
import kotlinx.serialization.Serializable

/**
 * An interval between two date-times, assumed to be at the same offset from UTC.
 *
 * [DateTime.MIN] and [DateTime.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end.
 */
@Serializable(with = DateTimeIntervalIsoSerializer::class)
class DateTimeInterval(
    override val start: DateTime = UNBOUNDED.start,
    override val endExclusive: DateTime = UNBOUNDED.endExclusive
) : Interval<DateTime> {

    override val endInclusive: DateTime
        get() = if (hasUnboundedEnd()) endExclusive else endExclusive - 1.nanoseconds

    override fun hasUnboundedStart(): Boolean = start == DateTime.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive == DateTime.MAX

    /**
     * Checks if this interval contains [value].
     * @param value a date-time, assumed to be in the same time zone
     */
    override fun contains(value: DateTime): Boolean {
        return value >= start && (value < endExclusive || hasUnboundedEnd())
    }

    override fun isEmpty(): Boolean {
        return start >= endExclusive
    }

    override fun equals(other: Any?): Boolean {
        return other is DateTimeInterval && (isEmpty() && other.isEmpty() ||
            start == other.start && endExclusive == other.endExclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * start.hashCode() + endExclusive.hashCode())
    }

    /**
     * Converts this interval to a string in ISO-8601 extended format.
     */
    override fun toString(): String = buildIsoString(
        maxElementSize = MAX_DATE_TIME_STRING_LENGTH,
        inclusive = false,
        appendFunction = StringBuilder::appendDateTime
    )

    @Deprecated(
        message = "Replace with toDuration()",
        replaceWith = ReplaceWith("this.toDuration()", "io.islandtime.ranges.toDuration"),
        level = DeprecationLevel.WARNING
    )
    fun asDuration(): Duration = toDuration()

    @Deprecated(
        message = "Replace with toPeriod()",
        replaceWith = ReplaceWith("this.toPeriod()", "io.islandtime.ranges.toPeriod"),
        level = DeprecationLevel.WARNING
    )
    fun asPeriod(): Period = toPeriod()

    companion object {
        /**
         * An empty interval.
         */
        val EMPTY: DateTimeInterval = DateTimeInterval(
            DateTime.fromSecondOfUnixEpoch(0L, 0, UtcOffset.ZERO),
            DateTime.fromSecondOfUnixEpoch(0L, 0, UtcOffset.ZERO)
        )

        /**
         * An unbounded (ie. infinite) interval.
         */
        val UNBOUNDED: DateTimeInterval = DateTimeInterval(DateTime.MIN, DateTime.MAX)

        internal fun withInclusiveEnd(start: DateTime, endInclusive: DateTime): DateTimeInterval {
            val endExclusive = when {
                endInclusive == DateTime.MAX -> endInclusive
                endInclusive > MAX_INCLUSIVE_END_DATE_TIME ->
                    throw DateTimeException("The end of the interval can't be represented")
                else -> endInclusive + 1.nanoseconds
            }

            return DateTimeInterval(start, endExclusive)
        }
    }
}

/**
 * Converts a string to a [DateTimeInterval].
 *
 * The string is assumed to be an ISO-8601 time interval representation in extended format. The output of
 * [DateTimeInterval.toString] can be safely parsed using this method.
 *
 * Examples:
 * - `1990-01-04T03/1991-08-30T15:30:05.123`
 * - `../1991-08-30T15:30:05.123`
 * - `1990-01-04T03/..`
 * - `../..`
 * - (empty string)
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toDateTimeInterval(): DateTimeInterval = toDateTimeInterval(DateTimeParsers.Iso.Extended.DATE_TIME_INTERVAL)

/**
 * Converts a string to a [DateTimeInterval] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed interval is invalid
 */
fun String.toDateTimeInterval(
    parser: GroupedDateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): DateTimeInterval {
    val results = parser.parse(this, settings).expectingGroupCount<DateTimeInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0].fields[DateTimeField.IS_UNBOUNDED] == 1L -> DateTimeInterval.UNBOUNDED.start
        else -> results[0].toDateTime() ?: throwParserFieldResolutionException<DateTimeInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1].fields[DateTimeField.IS_UNBOUNDED] == 1L -> DateTimeInterval.UNBOUNDED.endExclusive
        else -> results[1].toDateTime() ?: throwParserFieldResolutionException<DateTimeInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> DateTimeInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Creates a [DateTimeInterval] from this date-time up to, but not including the nanosecond represented by [to].
 */
infix fun DateTime.until(to: DateTime): DateTimeInterval = DateTimeInterval(this, to)

@Deprecated(
    message = "Replace with Period.between()",
    replaceWith = ReplaceWith(
        "Period.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Period"
    ),
    level = DeprecationLevel.WARNING
)
fun periodBetween(start: DateTime, endExclusive: DateTime): Period = Period.between(start, endExclusive)

@Deprecated(
    message = "Replace with Years.between()",
    replaceWith = ReplaceWith(
        "Years.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Years"
    ),
    level = DeprecationLevel.WARNING
)
fun yearsBetween(start: DateTime, endExclusive: DateTime): Years = Years.between(start, endExclusive)

@Deprecated(
    message = "Replace with Months.between()",
    replaceWith = ReplaceWith(
        "Months.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Months"
    ),
    level = DeprecationLevel.WARNING
)
fun monthsBetween(start: DateTime, endExclusive: DateTime): Months = Months.between(start, endExclusive)

@Deprecated(
    message = "Replace with Weeks.between()",
    replaceWith = ReplaceWith(
        "Weeks.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Weeks"
    ),
    level = DeprecationLevel.WARNING
)
fun weeksBetween(start: DateTime, endExclusive: DateTime): Weeks = Weeks.between(start, endExclusive)


@Deprecated(
    message = "Replace with Days.between()",
    replaceWith = ReplaceWith(
        "Days.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Days"
    ),
    level = DeprecationLevel.WARNING
)
fun daysBetween(start: DateTime, endExclusive: DateTime): Days = Days.between(start, endExclusive)

@Deprecated(
    message = "Replace with Duration.between()",
    replaceWith = ReplaceWith(
        "Duration.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Duration"
    ),
    level = DeprecationLevel.WARNING
)
fun durationBetween(start: DateTime, endExclusive: DateTime): Duration = Duration.between(start, endExclusive)

@Deprecated(
    message = "Replace with Hours.between()",
    replaceWith = ReplaceWith(
        "Hours.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Hours"
    ),
    level = DeprecationLevel.WARNING
)
fun hoursBetween(start: DateTime, endExclusive: DateTime): Hours = Hours.between(start, endExclusive)

@Deprecated(
    message = "Replace with Minutes.between()",
    replaceWith = ReplaceWith(
        "Minutes.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Minutes"
    ),
    level = DeprecationLevel.WARNING
)
fun minutesBetween(start: DateTime, endExclusive: DateTime): Minutes = Minutes.between(start, endExclusive)

@Deprecated(
    message = "Replace with Seconds.between()",
    replaceWith = ReplaceWith(
        "Seconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Seconds"
    ),
    level = DeprecationLevel.WARNING
)
fun secondsBetween(start: DateTime, endExclusive: DateTime): Seconds = Seconds.between(start, endExclusive)

@Deprecated(
    message = "Replace with Milliseconds.between()",
    replaceWith = ReplaceWith(
        "Milliseconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Milliseconds"
    ),
    level = DeprecationLevel.WARNING
)
fun millisecondsBetween(start: DateTime, endExclusive: DateTime): Milliseconds =
    Milliseconds.between(start, endExclusive)

@Deprecated(
    message = "Replace with Microseconds.between()",
    replaceWith = ReplaceWith(
        "Microseconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Microseconds"
    ),
    level = DeprecationLevel.WARNING
)
fun microsecondsBetween(start: DateTime, endExclusive: DateTime): Microseconds =
    Microseconds.between(start, endExclusive)

@Deprecated(
    message = "Replace with Nanoseconds.between()",
    replaceWith = ReplaceWith(
        "Nanoseconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Nanoseconds"
    ),
    level = DeprecationLevel.WARNING
)
fun nanosecondsBetween(start: DateTime, endExclusive: DateTime): Nanoseconds = Nanoseconds.between(start, endExclusive)
