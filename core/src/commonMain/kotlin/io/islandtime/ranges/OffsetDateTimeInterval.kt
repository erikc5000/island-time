package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateTimeField
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.MAX_INCLUSIVE_END_DATE_TIME
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.serialization.OffsetDateTimeIntervalIsoSerializer
import kotlinx.serialization.Serializable

/**
 * A half-open interval between two offset date-times based on timeline order.
 *
 * [DateTime.MIN] and [DateTime.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end. An
 * [OffsetDateTime] with either as the date-time component will be treated accordingly, regardless of the offset.
 */
@Serializable(with = OffsetDateTimeIntervalIsoSerializer::class)
class OffsetDateTimeInterval(
    start: OffsetDateTime = UNBOUNDED.start,
    endExclusive: OffsetDateTime = UNBOUNDED.endExclusive
) : TimePointInterval<OffsetDateTime>(start, endExclusive) {

    override fun hasUnboundedStart(): Boolean = start.dateTime == DateTime.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive.dateTime == DateTime.MAX

    /**
     * Converts this interval to a string in ISO-8601 extended format.
     */
    override fun toString(): String = buildIsoString(
        maxElementSize = MAX_OFFSET_DATE_TIME_STRING_LENGTH,
        inclusive = false,
        appendFunction = StringBuilder::appendOffsetDateTime
    )

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
        val EMPTY = OffsetDateTimeInterval(
            Instant.UNIX_EPOCH at UtcOffset.ZERO,
            Instant.UNIX_EPOCH at UtcOffset.ZERO
        )

        /**
         * An unbounded (ie. infinite) interval.
         */
        val UNBOUNDED = OffsetDateTimeInterval(
            DateTime.MIN at UtcOffset.ZERO,
            DateTime.MAX at UtcOffset.ZERO
        )

        internal fun withInclusiveEnd(
            start: OffsetDateTime,
            endInclusive: OffsetDateTime
        ): OffsetDateTimeInterval {
            val endExclusive = when {
                endInclusive.dateTime == DateTime.MAX -> endInclusive
                endInclusive.dateTime > MAX_INCLUSIVE_END_DATE_TIME ->
                    throw DateTimeException("The end of the interval can't be represented")
                else -> endInclusive + 1.nanoseconds
            }

            return OffsetDateTimeInterval(start, endExclusive)
        }
    }
}

/**
 * Converts a string to an [OffsetDateTimeInterval].
 *
 * The string is assumed to be an ISO-8601 time interval representation in extended format. The output of
 * [OffsetDateTimeInterval.toString] can be safely parsed using this method.
 *
 * Examples:
 * - `1990-01-04T03-05/1991-08-30T15:30:05.123-04:00`
 * - `../1991-08-30T15:30:05.123-04:00`
 * - `1990-01-04T03-05/..`
 * - `../..`
 * - (empty string)
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toOffsetDateTimeInterval(): OffsetDateTimeInterval {
    return toOffsetDateTimeInterval(DateTimeParsers.Iso.Extended.OFFSET_DATE_TIME_INTERVAL)
}

/**
 * Converts a string to an [OffsetDateTimeInterval] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed interval is invalid
 */
fun String.toOffsetDateTimeInterval(
    parser: GroupedDateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): OffsetDateTimeInterval {
    val results = parser.parse(this, settings)
        .expectingGroupCount<OffsetDateTimeInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0].fields[DateTimeField.IS_UNBOUNDED] == 1L -> OffsetDateTimeInterval.UNBOUNDED.start
        else -> results[0].toOffsetDateTime() ?: throwParserFieldResolutionException<OffsetDateTimeInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1].fields[DateTimeField.IS_UNBOUNDED] == 1L -> OffsetDateTimeInterval.UNBOUNDED.endExclusive
        else -> results[1].toOffsetDateTime() ?: throwParserFieldResolutionException<OffsetDateTimeInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> OffsetDateTimeInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Creates an [OffsetDateTimeInterval] from this date-time up to, but not including [to].
 */
infix fun OffsetDateTime.until(to: OffsetDateTime): OffsetDateTimeInterval = OffsetDateTimeInterval(this, to)

@Deprecated(
    message = "Replace with Period.between()",
    replaceWith = ReplaceWith(
        "Period.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Period"
    ),
    level = DeprecationLevel.WARNING
)
fun periodBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Period = Period.between(start, endExclusive)

@Deprecated(
    message = "Replace with Years.between()",
    replaceWith = ReplaceWith(
        "Years.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Years"
    ),
    level = DeprecationLevel.WARNING
)
fun yearsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Years = Years.between(start, endExclusive)

@Deprecated(
    message = "Replace with Months.between()",
    replaceWith = ReplaceWith(
        "Months.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Months"
    ),
    level = DeprecationLevel.WARNING
)
fun monthsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Months = Months.between(start, endExclusive)

@Deprecated(
    message = "Replace with Weeks.between()",
    replaceWith = ReplaceWith(
        "Weeks.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Weeks"
    ),
    level = DeprecationLevel.WARNING
)
fun weeksBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Weeks = Weeks.between(start, endExclusive)

@Deprecated(
    message = "Replace with Days.between()",
    replaceWith = ReplaceWith(
        "Days.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Days"
    ),
    level = DeprecationLevel.WARNING
)
fun daysBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Days = Days.between(start, endExclusive)
