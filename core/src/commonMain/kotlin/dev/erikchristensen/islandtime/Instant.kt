package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.LongSecondSpan
import dev.erikchristensen.islandtime.interval.NanosecondSpan
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
    val unixEpochSeconds: LongSecondSpan,
    val nanoOfSeconds: NanosecondSpan = 0.nanoseconds
)