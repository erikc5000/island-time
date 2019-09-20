package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.Duration
import dev.erikchristensen.islandtime.interval.days
import dev.erikchristensen.islandtime.interval.nanoseconds
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

/**
 * An inclusive range of instants
 */
class InstantRange(
    override val start: Instant,
    override val endInclusive: Instant
) : ClosedRange<Instant> {

    override fun isEmpty(): Boolean = start > endInclusive

    override fun toString() = "$start..$endInclusive"

    override fun equals(other: Any?): Boolean {
        return other is InstantRange && (isEmpty() && other.isEmpty() ||
            start == other.start && endInclusive == other.endInclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * start.hashCode() + endInclusive.hashCode())
    }

    companion object {
        /**
         * A range containing zero days
         */
        val EMPTY = InstantRange(Instant.fromUnixEpochSecond(1L), Instant.UNIX_EPOCH)
    }
}

/**
 * Return a random instant within the range using the default random number generator
 */
fun InstantRange.random(): Instant = random(Random)

/**
 * Return a random instant within the range using the supplied random number generator
 */
fun InstantRange.random(random: Random): Instant {
    try {
        val longSecondRange = start.unixEpochSecond..endInclusive.unixEpochSecond
        val intNanoRange = start.unixEpochNanoOfSecond..endInclusive.unixEpochNanoOfSecond
        return Instant.fromUnixEpochSecond(random.nextLong(longSecondRange), random.nextInt(intNanoRange))
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

/**
 * Get a range containing all of the days up to, but not including [to]
 */
infix fun Instant.until(to: Instant) = InstantRange(this, to - 1.nanoseconds)

/**
 * Convert a range of instants into a duration containing each nanosecond in the range. As a range is inclusive, if the
 * start and end instant are the same, the resulting duration will contain one nanosecond.
 */
fun InstantRange.asDuration(): Duration {
    return if (start > endInclusive) {
        Duration.ZERO
    } else {
        durationBetween(start, endInclusive + 1.nanoseconds)
    }
}

/**
 * Get the number of 24-hour days in a range of instants
 */
val InstantRange.days
    get() = if (start > endInclusive) 0L.days else daysBetween(start, endInclusive + 1.nanoseconds)

/**
 * Get the number of hours in a range of instants
 */
val InstantRange.hours
    get() = if (start > endInclusive) 0L.days else hoursBetween(start, endInclusive + 1.nanoseconds)

/**
 * Get the number of hours in a range of instants
 */
val InstantRange.minutes
    get() = if (start > endInclusive) 0L.days else minutesBetween(start, endInclusive + 1.nanoseconds)

/**
 * Get the number of seconds in a range of instants
*/
val InstantRange.seconds
    get() = if (start > endInclusive) 0L.days else secondsBetween(start, endInclusive + 1.nanoseconds)

/**
 * Get the number of milliseconds in a range of instants
 */
val InstantRange.milliseconds
    get() = if (start > endInclusive) 0L.days else millisecondsBetween(start, endInclusive + 1.nanoseconds)

/**
 * Get the number of microseconds in a range of instants
 */
val InstantRange.microseconds
    get() = if (start > endInclusive) 0L.days else microsecondsBetween(start, endInclusive + 1.nanoseconds)

/**
 * Get the number of nanoseconds in a range of instants
 */
val InstantRange.nanoseconds
    get() = if (start > endInclusive) 0L.days else nanosecondsBetween(start, endInclusive + 1.nanoseconds)