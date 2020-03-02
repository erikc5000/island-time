package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateProperty
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.parser.expectingGroupCount
import io.islandtime.parser.throwParserFieldResolutionException
import io.islandtime.ranges.internal.MAX_INCLUSIVE_END_DATE_TIME
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.ranges.internal.throwUnboundedIntervalException
import kotlin.random.Random

/**
 * A half-open interval between two offset date-times based on timeline order.
 *
 * [DateTime.MIN] and [DateTime.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end. An
 * [OffsetDateTime] with either as the date-time component will be treated accordingly, regardless of the offset.
 */
class OffsetDateTimeInterval(
    start: OffsetDateTime = UNBOUNDED.start,
    endExclusive: OffsetDateTime = UNBOUNDED.endExclusive
) : TimePointInterval<OffsetDateTime>(start, endExclusive) {

    override fun hasUnboundedStart(): Boolean = start.dateTime == DateTime.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive.dateTime == DateTime.MAX

    /**
     * Convert this interval to a string in ISO-8601 extended format.
     */
    override fun toString() = buildIsoString(MAX_OFFSET_DATE_TIME_STRING_LENGTH, StringBuilder::appendOffsetDateTime)

    /**
     * Convert the interval into a [Period] of the same length.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    fun asPeriod(): Period {
        return when {
            isEmpty() -> Period.ZERO
            isBounded() -> periodBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of years between the start and end of the interval. A year is considered to have passed if twelve
     * full months have passed between the start date and end date.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInYears
        get() = when {
            isEmpty() -> 0.years
            isBounded() -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of months between the start and end of the interval. A month is considered to have passed if the
     * day of the end month is greater than or equal to the day of the start month.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMonths
        get() = when {
            isEmpty() -> 0.months
            isBounded() -> monthsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole weeks in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInWeeks
        get() = when {
            isEmpty() -> 0L.weeks
            isBounded() -> weeksBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

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
 * Convert a string to an [OffsetDateTimeInterval].
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
fun String.toOffsetDateTimeInterval() = toOffsetDateTimeInterval(DateTimeParsers.Iso.Extended.OFFSET_DATE_TIME_INTERVAL)

/**
 * Convert a string to an [OffsetDateTimeInterval] using a specific parser.
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
        results[0][DateProperty.IsFarPast] == true -> OffsetDateTimeInterval.UNBOUNDED.start
        else -> results[0].toOffsetDateTime() ?: throwParserFieldResolutionException<OffsetDateTimeInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1][DateProperty.IsFarFuture] == true -> OffsetDateTimeInterval.UNBOUNDED.endExclusive
        else -> results[1].toOffsetDateTime() ?: throwParserFieldResolutionException<OffsetDateTimeInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> OffsetDateTimeInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Return a random date-time within the range using the default random number generator.
 */
fun OffsetDateTimeInterval.random(): OffsetDateTime = random(Random)

/**
 * Return a random date-time within the range using the supplied random number generator.
 */
fun OffsetDateTimeInterval.random(random: Random): OffsetDateTime {
    try {
        return OffsetDateTime.fromUnixEpochSecond(
            random.nextLong(start.unixEpochSecond, endExclusive.unixEpochSecond),
            random.nextInt(start.unixEpochNanoOfSecond, endExclusive.unixEpochNanoOfSecond),
            start.offset
        )
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

/**
 * Create an interval containing all of the representable date-times from up to, but not including [to].
 */
infix fun OffsetDateTime.until(to: OffsetDateTime) = OffsetDateTimeInterval(this, to)

/**
 * Get the [Period] between two date-times, ignoring the offsets.
 */
fun periodBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Period {
    return periodBetween(start.dateTime, endExclusive.dateTime)
}

/**
 * Get the number of whole years between two date-times, ignoring the offsets.
 */
fun yearsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): IntYears {
    return yearsBetween(start.dateTime, endExclusive.dateTime)
}

/**
 * Get the number of whole months between two date-times, ignoring the offsets.
 */
fun monthsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): IntMonths {
    return monthsBetween(start.dateTime, endExclusive.dateTime)
}

/**
 * Get the number whole weeks between two date-times, ignoring the offsets.
 */
fun weeksBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): LongWeeks {
    return daysBetween(start, endExclusive).inWeeks
}