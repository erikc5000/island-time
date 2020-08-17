package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateTimeField
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.*
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
     * Converts this interval to a string in ISO-8601 extended format.
     */
    override fun toString() = buildIsoString(
        maxElementSize = MAX_OFFSET_DATE_TIME_STRING_LENGTH,
        inclusive = false,
        appendFunction = StringBuilder::appendOffsetDateTime
    )

    /**
     * Converts this interval into a [Period] of the same length.
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
     * Gets the number of whole years in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInYears
        get() = when {
            isEmpty() -> 0.years
            isBounded() -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Gets the number of whole months in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMonths
        get() = when {
            isEmpty() -> 0.months
            isBounded() -> monthsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Gets the number of whole weeks in this interval.
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
fun String.toOffsetDateTimeInterval() = toOffsetDateTimeInterval(DateTimeParsers.Iso.Extended.OFFSET_DATE_TIME_INTERVAL)

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
 * Returns a random date-time within this interval using the default random number generator. The offset of the start
 * date-time will be used.
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see OffsetDateTimeInterval.randomOrNull
 */
fun OffsetDateTimeInterval.random(): OffsetDateTime = random(Random)

/**
 * Returns a random date-time within this interval using the default random number generator or `null` if the interval
 * is empty or unbounded. The offset of the start date-time will be used.
 * @see OffsetDateTimeInterval.random
 */
fun OffsetDateTimeInterval.randomOrNull(): OffsetDateTime? = randomOrNull(Random)

/**
 * Returns a random date-time within this interval using the supplied random number generator. The offset of the start
 * date-time will be used.
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see OffsetDateTimeInterval.randomOrNull
 */
fun OffsetDateTimeInterval.random(random: Random): OffsetDateTime {
    return random(random) { second, nanosecond ->
        OffsetDateTime.fromSecondOfUnixEpoch(second, nanosecond, start.offset)
    }
}

/**
 * Returns a random date-time within this interval using the supplied random number generator or `null` if the interval
 * is empty or unbounded. The offset of the start date-time will be used.
 * @see OffsetDateTimeInterval.random
 */
fun OffsetDateTimeInterval.randomOrNull(random: Random): OffsetDateTime? {
    return randomOrNull(random) { second, nanosecond ->
        OffsetDateTime.fromSecondOfUnixEpoch(second, nanosecond, start.offset)
    }
}

/**
 * Creates an [OffsetDateTimeInterval] from this date-time up to, but not including [to].
 */
infix fun OffsetDateTime.until(to: OffsetDateTime) = OffsetDateTimeInterval(this, to)

/**
 * Gets the [Period] between two date-times, adjusting the offset of [endExclusive] if necessary to match the starting
 * date-time.
 */
fun periodBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): Period {
    return periodBetween(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Gets the number of whole years between two date-times, adjusting the offset of [endExclusive] if necessary to match
 * the starting date-time.
 */
fun yearsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): IntYears {
    return yearsBetween(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Gets the number of whole months between two date-times, adjusting the offset of [endExclusive] if necessary to match
 * the starting date-time.
 */
fun monthsBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): IntMonths {
    return monthsBetween(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Gets the number whole weeks between two date-times, adjusting the offset of [endExclusive] if necessary to match the
 * starting date-time.
 */
fun weeksBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): LongWeeks {
    return weeksBetween(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Gets the number whole days between two date-times, adjusting the offset of [endExclusive] if necessary to match the
 * starting date-time.
 */
fun daysBetween(start: OffsetDateTime, endExclusive: OffsetDateTime): LongDays {
    return daysBetween(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

private fun adjustedEndDateTime(start: OffsetDateTime, endExclusive: OffsetDateTime): DateTime {
    val offsetDelta = start.offset.totalSeconds - endExclusive.offset.totalSeconds
    return endExclusive.dateTime + offsetDelta
}
