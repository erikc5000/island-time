package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.MAX_INSTANT_STRING_LENGTH
import io.islandtime.appendInstant
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.toInstant
import kotlin.random.Random

/**
 * A half-open interval of instants
 */
class InstantInterval(
    start: Instant = UNBOUNDED.start,
    endExclusive: Instant = UNBOUNDED.endExclusive
) : TimePointInterval<Instant>(start, endExclusive) {

    override val hasUnboundedStart: Boolean get() = start == Instant.MIN
    override val hasUnboundedEnd: Boolean get() = endExclusive == Instant.MAX

    override fun toString() = buildIsoString(MAX_INSTANT_STRING_LENGTH, StringBuilder::appendInstant)

    companion object {
        /**
         * An empty interval
         */
        val EMPTY = InstantInterval(Instant.UNIX_EPOCH, Instant.UNIX_EPOCH)

        /**
         * An unbounded (ie. infinite) interval
         */
        val UNBOUNDED = InstantInterval(Instant.MIN, Instant.MAX)

        internal fun withInclusiveEnd(
            start: Instant,
            endInclusive: Instant
        ): InstantInterval {
            val endExclusive = if (endInclusive == Instant.MAX) {
                endInclusive
            } else {
                endInclusive + 1.nanoseconds
            }

            return InstantInterval(start, endExclusive)
        }
    }
}

fun emptyInstantInterval() = InstantInterval.EMPTY
fun unboundedInstantInterval() = InstantInterval.UNBOUNDED

fun String.toInstantInterval() = toInstantInterval(DateTimeParsers.Iso.Extended.INSTANT_INTERVAL)

fun String.toInstantInterval(parser: GroupedDateTimeParser): InstantInterval {
    val results = parser.parse(this).expectingGroupCount<InstantInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0].fields[DateTimeField.IS_UNBOUNDED] == 1L -> InstantInterval.UNBOUNDED.start
        else -> results[0].toInstant() ?: throwParserFieldResolutionException<InstantInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1].fields[DateTimeField.IS_UNBOUNDED] == 1L -> InstantInterval.UNBOUNDED.endExclusive
        else -> results[1].toInstant() ?: throwParserFieldResolutionException<InstantInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> InstantInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Return a random instant within the range using the default random number generator
 */
fun InstantInterval.random(): Instant = random(Random)

/**
 * Return a random instant within the range using the supplied random number generator
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
 * Get a range containing all of the representable instants up to, but not including [to]
 */
infix fun Instant.until(to: Instant) = InstantInterval(this, to)