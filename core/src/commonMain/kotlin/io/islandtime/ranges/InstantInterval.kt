package io.islandtime.ranges

import io.islandtime.Instant
import io.islandtime.MAX_INSTANT_STRING_LENGTH
import io.islandtime.appendInstant
import io.islandtime.interval.*
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

        fun withInclusiveEnd(
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