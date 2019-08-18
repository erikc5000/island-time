package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.LongSeconds
import dev.erikchristensen.islandtime.interval.IntNanoseconds
import dev.erikchristensen.islandtime.interval.nanoseconds

/**
 * An instant in time with millisecond precision
 */
inline class Instant(val unixEpochMilliseconds: Long) : Comparable<Instant> {

    override fun compareTo(other: Instant): Int {
        return unixEpochMilliseconds .compareTo(other.unixEpochMilliseconds)
    }

    companion object {
        val MAX = Instant(Long.MAX_VALUE)
        val MIN = Instant(Long.MIN_VALUE)
        val UNIX_EPOCH = Instant(0)
    }
}

/**
 * An instant in time with nanosecond precision
 */
data class NanoInstant(
    val unixEpochSeconds: LongSeconds,
    val nanoOfSeconds: IntNanoseconds = 0.nanoseconds
)