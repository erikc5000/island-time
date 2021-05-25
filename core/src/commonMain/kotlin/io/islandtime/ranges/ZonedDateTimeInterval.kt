package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateTimeField
import io.islandtime.internal.deprecatedToError
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.MAX_INCLUSIVE_END_DATE_TIME
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.serialization.ZonedDateTimeIntervalSerializer
import kotlinx.serialization.Serializable

/**
 * A half-open interval of zoned date-times based on timeline order.
 *
 * [DateTime.MIN] and [DateTime.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end. A
 * [ZonedDateTime] with either as the date-time component will be treated accordingly, regardless of the offset or
 * time zone.
 */
@Serializable(with = ZonedDateTimeIntervalSerializer::class)
class ZonedDateTimeInterval(
    start: ZonedDateTime = UNBOUNDED.start,
    endExclusive: ZonedDateTime = UNBOUNDED.endExclusive
) : TimePointInterval<ZonedDateTime>(start, endExclusive) {

    override fun hasUnboundedStart(): Boolean = start.dateTime == DateTime.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive.dateTime == DateTime.MAX

    /**
     * Converts this interval to a string in ISO-8601 extended format.
     */
    override fun toString(): String = buildIsoString(
        maxElementSize = MAX_ZONED_DATE_TIME_STRING_LENGTH,
        inclusive = false,
        appendFunction = StringBuilder::appendZonedDateTime
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
        val EMPTY: ZonedDateTimeInterval = ZonedDateTimeInterval(
            Instant.UNIX_EPOCH at TimeZone.UTC,
            Instant.UNIX_EPOCH at TimeZone.UTC
        )

        /**
         * An unbounded (ie. infinite) interval.
         */
        val UNBOUNDED: ZonedDateTimeInterval = ZonedDateTimeInterval(
            DateTime.MIN at TimeZone.UTC,
            DateTime.MAX at TimeZone.UTC
        )

        internal fun withInclusiveEnd(
            start: ZonedDateTime,
            endInclusive: ZonedDateTime
        ): ZonedDateTimeInterval {
            val endExclusive = when {
                endInclusive.dateTime == DateTime.MAX -> endInclusive
                endInclusive.dateTime > MAX_INCLUSIVE_END_DATE_TIME ->
                    throw DateTimeException("The end of the interval can't be represented")
                else -> endInclusive + 1.nanoseconds
            }

            return ZonedDateTimeInterval(start, endExclusive)
        }
    }
}

/**
 * Converts a string to a [ZonedDateTimeInterval].
 *
 * The string is assumed to be an ISO-8601 time interval representation in extended format. The output of
 * [ZonedDateTimeInterval.toString] can be safely parsed using this method.
 *
 * Examples:
 * - `1990-01-04T03-05[America/New_York]/1991-08-30T15:30:05.123-04:00`
 * - `../1991-08-30T15:30:05.123-04:00`
 * - `1990-01-04T03-05[Europe/London]/..`
 * - `../..`
 * - (empty string)
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toZonedDateTimeInterval(): ZonedDateTimeInterval {
    return toZonedDateTimeInterval(DateTimeParsers.Iso.Extended.ZONED_DATE_TIME_INTERVAL)
}

/**
 * Converts a string to a [ZonedDateTimeInterval] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed interval is invalid
 */
fun String.toZonedDateTimeInterval(
    parser: GroupedDateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): ZonedDateTimeInterval {
    val results = parser.parse(this, settings)
        .expectingGroupCount<ZonedDateTimeInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0].fields[DateTimeField.IS_UNBOUNDED] == 1L -> ZonedDateTimeInterval.UNBOUNDED.start
        else -> results[0].toZonedDateTime() ?: throwParserFieldResolutionException<ZonedDateTimeInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1].fields[DateTimeField.IS_UNBOUNDED] == 1L -> ZonedDateTimeInterval.UNBOUNDED.endExclusive
        else -> results[1].toZonedDateTime() ?: throwParserFieldResolutionException<ZonedDateTimeInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> ZonedDateTimeInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Creates a [ZonedDateTimeInterval] from this date-time up to, but not including [to].
 */
infix fun ZonedDateTime.until(to: ZonedDateTime): ZonedDateTimeInterval = ZonedDateTimeInterval(this, to)


@Deprecated(
    "Use 'at' instead.",
    ReplaceWith("this at zone"),
    DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER", "unused")
fun DateRange.toZonedDateTimeInterval(zone: TimeZone): ZonedDateTimeInterval = deprecatedToError()

@Deprecated(
    message = "Replace with Period.between()",
    replaceWith = ReplaceWith(
        "Period.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Period"
    ),
    level = DeprecationLevel.WARNING
)
fun periodBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): Period = Period.between(start, endExclusive)

@Deprecated(
    message = "Replace with Years.between()",
    replaceWith = ReplaceWith(
        "Years.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Years"
    ),
    level = DeprecationLevel.WARNING
)
fun yearsBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): Years = Years.between(start, endExclusive)

@Deprecated(
    message = "Replace with Months.between()",
    replaceWith = ReplaceWith(
        "Months.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Months"
    ),
    level = DeprecationLevel.WARNING
)
fun monthsBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): Months = Months.between(start, endExclusive)

@Deprecated(
    message = "Replace with Weeks.between()",
    replaceWith = ReplaceWith(
        "Weeks.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Weeks"
    ),
    level = DeprecationLevel.WARNING
)
fun weeksBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): Weeks = Weeks.between(start, endExclusive)

@Deprecated(
    message = "Replace with Days.between()",
    replaceWith = ReplaceWith(
        "Days.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Days"
    ),
    level = DeprecationLevel.WARNING
)
fun daysBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): Days = Days.between(start, endExclusive)
