package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateProperty
import io.islandtime.measures.nanoseconds
import io.islandtime.parser.*
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.ranges.internal.random
import io.islandtime.ranges.internal.randomOrNull
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
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toInstantInterval() = toInstantInterval(DateTimeParsers.Iso.Extended.INSTANT_INTERVAL)

/**
 * Convert a string to an [InstantInterval] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed interval is invalid
 */
fun String.toInstantInterval(
    parser: GroupedTemporalParser,
    settings: TemporalParser.Settings = TemporalParser.Settings.DEFAULT
): InstantInterval {
    val results = parser.parse(this, settings).expectingGroupCount<InstantInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0][DateProperty.IsFarPast] == true -> InstantInterval.UNBOUNDED.start
        else -> results[0].toInstant() ?: throwParserPropertyResolutionException<InstantInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1][DateProperty.IsFarFuture] == true -> InstantInterval.UNBOUNDED.endExclusive
        else -> results[1].toInstant() ?: throwParserPropertyResolutionException<InstantInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> InstantInterval.EMPTY
        else -> throw TemporalParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Return a random instant within the interval using the default random number generator.
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see InstantInterval.randomOrNull
 */
fun InstantInterval.random(): Instant = random(Random)

/**
 * Return a random instant within the interval using the default random number generator or `null` if the interval is
 * empty or unbounded.
 * @see InstantInterval.random
 */
fun InstantInterval.randomOrNull(): Instant? = randomOrNull(Random)

/**
 * Return a random instant within the interval using the supplied random number generator.
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see InstantInterval.randomOrNull
 */
fun InstantInterval.random(random: Random): Instant {
    return random(random, Instant.Companion::fromSecondOfUnixEpoch)
}

/**
 * Return a random instant within the interval using the supplied random number generator or `null` if the interval is
 * empty or unbounded.
 * @see InstantInterval.random
 */
fun InstantInterval.randomOrNull(random: Random): Instant? {
    return randomOrNull(random, Instant.Companion::fromSecondOfUnixEpoch)
}

/**
 * Get an interval containing all of the instants up to, but not including [to].
 */
infix fun Instant.until(to: Instant) = InstantInterval(this, to)

@Deprecated(
    "Use toInstantInterval() instead.",
    ReplaceWith("this.toInstantInterval()"),
    DeprecationLevel.WARNING
)
fun OffsetDateTimeInterval.asInstantInterval(): InstantInterval = toInstantInterval()

@Deprecated(
    "Use toInstantInterval() instead.",
    ReplaceWith("this.toInstantInterval()"),
    DeprecationLevel.WARNING
)
fun ZonedDateTimeInterval.asInstantInterval(): InstantInterval = toInstantInterval()