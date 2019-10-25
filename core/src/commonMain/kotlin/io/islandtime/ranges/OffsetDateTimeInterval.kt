package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.parser.expectingGroupCount
import io.islandtime.parser.throwParserFieldResolutionException
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.ranges.internal.throwUnboundedIntervalException
import kotlin.random.Random

/**
 * A half-open interval of offset date-times based on timeline order
 */
class OffsetDateTimeInterval(
    start: OffsetDateTime = UNBOUNDED.start,
    endExclusive: OffsetDateTime = UNBOUNDED.endExclusive
) : TimePointInterval<OffsetDateTime>(start, endExclusive) {

    override val hasUnboundedStart: Boolean get() = start.dateTime == DateTime.MIN
    override val hasUnboundedEnd: Boolean get() = endExclusive.dateTime == DateTime.MAX

    override fun toString() = buildIsoString(MAX_OFFSET_DATE_TIME_STRING_LENGTH, StringBuilder::appendOffsetDateTime)

    /**
     * Convert the range into a period containing each day in the range. As a range is inclusive, if the start and end
     * date are the same, the resulting period will contain one day.
     */
    fun asPeriod(): Period {
        return when {
            isEmpty() -> Period.ZERO
            isBounded -> periodBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of years the range. A year is considered to have passed if twelve full months have passed between
     * the start date and end date, according to the definition of 'month' in [lengthInMonths].
     */
    val lengthInYears
        get() = when {
            isEmpty() -> 0.years
            isBounded -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of months in the range. A month is considered to have passed if the day of the end month is
     * greater than or equal to the day of the start month minus one (as a range is inclusive).
     */
    val lengthInMonths
        get() = when {
            isEmpty() -> 0.months
            isBounded -> monthsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    companion object {
        /**
         * An empty interval
         */
        val EMPTY = OffsetDateTimeInterval(
            Instant.UNIX_EPOCH at UtcOffset.ZERO,
            Instant.UNIX_EPOCH at UtcOffset.ZERO
        )

        /**
         * An unbounded (ie. infinite) interval
         */
        val UNBOUNDED = OffsetDateTimeInterval(
            DateTime.MIN at UtcOffset.ZERO,
            DateTime.MAX at UtcOffset.ZERO
        )

        fun withInclusiveEnd(
            start: OffsetDateTime,
            endInclusive: OffsetDateTime
        ): OffsetDateTimeInterval {
            val endExclusive = if (endInclusive.dateTime == DateTime.MAX) {
                endInclusive
            } else {
                endInclusive + 1.nanoseconds
            }

            return OffsetDateTimeInterval(start, endExclusive)
        }
    }
}

fun String.toOffsetDateTimeInterval() = toOffsetDateTimeInterval(DateTimeParsers.Iso.Extended.OFFSET_DATE_TIME_INTERVAL)

fun String.toOffsetDateTimeInterval(parser: GroupedDateTimeParser): OffsetDateTimeInterval {
    val results = parser.parse(this).expectingGroupCount<OffsetDateTimeInterval>(2, this)

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
 * Return a random date-time within the range using the default random number generator
 */
fun OffsetDateTimeInterval.random(): OffsetDateTime = random(Random)

/**
 * Return a random date-time within the range using the supplied random number generator
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
 * Get a range containing all of the representable date-times up to, but not including [to]. If the start and end
 * date-times have different offsets, the end will be adjusted to match the starting offset while preserving the
 * instant.
 */
infix fun OffsetDateTime.until(to: OffsetDateTime) = OffsetDateTimeInterval(this, to)

/**
 * Get the [Period] between two offset date-times. If the start and end have different offsets, the end will be
 * adjusted to match the starting offset while preserving the instant.
 */
fun periodBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Period {
    return periodBetween(start.dateTime, endExclusive.adjustedTo(start.offset).dateTime)
}

/**
 * Get the number of whole years between two offset date-times. If the start and end have different offsets, the end
 * will be adjusted to match the starting offset while preserving the instant.
 */
fun yearsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): IntYears {
    return yearsBetween(start.dateTime, endExclusive.adjustedTo(start.offset).dateTime)
}

/**
 * Get the number of whole months between two offset date-times. If the start and end have different offsets, the end
 * will be adjusted to match the starting offset while preserving the instant.
 */
fun monthsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): IntMonths {
    return monthsBetween(start.dateTime, endExclusive.adjustedTo(start.offset).dateTime)
}