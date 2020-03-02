package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.MAX_INSTANT_STRING_LENGTH
import io.islandtime.appendInstant
import io.islandtime.base.DateProperty
import io.islandtime.measures.*
import io.islandtime.endOfDayAt
import io.islandtime.startOfDayAt
import io.islandtime.parser.*
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.toInstant
import kotlin.random.Random

/**
 * A half-open interval between two instants.
 *
 * [Instant.MIN] and [Instant.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end.
 */
class InstantInterval(
    start: Instant = Instant.MIN,
    endExclusive: Instant = Instant.MAX
) : TimePointInterval<Instant>(start, endExclusive),
    TimePointProgressionBuilder<Instant> {

    override fun hasUnboundedStart(): Boolean = start == Instant.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive == Instant.MAX

    override val first: Instant get() = start
    override val last: Instant get() = endInclusive

    /**
     * Convert this interval to a string in ISO-8601 extended format.
     */
    override fun toString() = buildIsoString(MAX_INSTANT_STRING_LENGTH, StringBuilder::appendInstant)

    companion object {
        /**
         * An empty interval.
         */
        val EMPTY = InstantInterval(Instant.UNIX_EPOCH, Instant.UNIX_EPOCH)

        /**
         * An unbounded (ie. infinite) interval.
         */
        val UNBOUNDED = InstantInterval(Instant.MIN, Instant.MAX)

        private val MAX_INCLUSIVE_END = Instant.MAX - 2.nanoseconds

        internal fun withInclusiveEnd(
            start: Instant,
            endInclusive: Instant
        ): InstantInterval {
            val endExclusive = when {
                endInclusive == Instant.MAX -> endInclusive
                endInclusive > MAX_INCLUSIVE_END ->
                    throw DateTimeException("The end of the interval can't be represented")
                else -> endInclusive + 1.nanoseconds
            }

            return InstantInterval(start, endExclusive)
        }
    }
}

/**
 * Convert a string to an [InstantInterval].
 *
 * The string is assumed to be an ISO-8601 time interval representation in extended format. The output of
 * [InstantInterval.toString] can be safely parsed using this method.
 *
 * Examples:
 * - `1990-01-04T03Z/1991-08-30T15:30:05.123Z`
 * - `../1991-08-30T15:30:05.123Z`
 * - `1990-01-04T03Z/..`
 * - `../..`
 * - (empty string)
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toInstantInterval() = toInstantInterval(DateTimeParsers.Iso.Extended.INSTANT_INTERVAL)

/**
 * Convert a string to an [InstantInterval] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed interval is invalid
 */
fun String.toInstantInterval(
    parser: GroupedDateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): InstantInterval {
    val results = parser.parse(this, settings).expectingGroupCount<InstantInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0][DateProperty.IsFarPast] == true -> InstantInterval.UNBOUNDED.start
        else -> results[0].toInstant() ?: throwParserFieldResolutionException<InstantInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1][DateProperty.IsFarFuture] == true -> InstantInterval.UNBOUNDED.endExclusive
        else -> results[1].toInstant() ?: throwParserFieldResolutionException<InstantInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> InstantInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Return a random instant within the interval using the default random number generator.
 */
fun InstantInterval.random(): Instant = random(Random)

/**
 * Return a random instant within the interval using the supplied random number generator.
 */
fun InstantInterval.random(random: Random): Instant {
    try {
        return Instant.fromUnixEpochSecond(
            random.nextLong(start.unixEpochSecond, endExclusive.unixEpochSecond),
            random.nextInt(start.unixEpochNanoOfSecond, endExclusive.unixEpochNanoOfSecond)
        )
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

/**
 * Get an interval containing all of the instants up to, but not including [to].
 */
infix fun Instant.until(to: Instant) = InstantInterval(this, to)

/**
 * Convert a range of dates into an [InstantInterval] between the starting and ending instants in a particular time
 * zone.
 */
fun DateRange.toInstantIntervalAt(zone: TimeZone): InstantInterval {
    return when {
        isEmpty() -> InstantInterval.EMPTY
        isUnbounded() -> InstantInterval.UNBOUNDED
        else -> {
            val start = if (hasUnboundedStart()) Instant.MIN else start.startOfDayAt(zone).instant
            val end = if (hasUnboundedEnd()) Instant.MAX else endInclusive.endOfDayAt(zone).instant
            start..end
        }
    }
}

/**
 * Convert an [OffsetDateTimeInterval] into an [InstantInterval].
 */
fun OffsetDateTimeInterval.asInstantInterval(): InstantInterval {
    return when {
        isEmpty() -> InstantInterval.EMPTY
        isUnbounded() -> InstantInterval.UNBOUNDED
        else -> {
            val startInstant = if (hasUnboundedStart()) Instant.MIN else start.instant
            val endInstant = if (hasUnboundedEnd()) Instant.MAX else endExclusive.instant
            startInstant until endInstant
        }
    }
}

/**
 * Convert a [ZonedDateTimeInterval] until an [InstantInterval].
 */
fun ZonedDateTimeInterval.asInstantInterval(): InstantInterval {
    return when {
        isEmpty() -> InstantInterval.EMPTY
        isUnbounded() -> InstantInterval.UNBOUNDED
        else -> {
            val startInstant = if (hasUnboundedStart()) Instant.MIN else start.instant
            val endInstant = if (hasUnboundedEnd()) Instant.MAX else endExclusive.instant
            startInstant until endInstant
        }
    }
}