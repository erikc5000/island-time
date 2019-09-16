package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.*

// TODO: Flush this out... possibly introduce EpochDay, EpochSecond, etc. classes to express "instants" with
// different granularity levels.  Really, an instant is just a duration relative to an epoch.

/**
 * An instant in time with millisecond precision
 */
inline class Instant(val millisecondsSinceUnixEpoch: LongMilliseconds) : Comparable<Instant> {

    override fun compareTo(other: Instant): Int {
        return millisecondsSinceUnixEpoch.compareTo(other.millisecondsSinceUnixEpoch)
    }

    override fun toString(): String {
        val dateTime = this.asUtcDateTime()

        return buildString(MAX_DATE_TIME_STRING_LENGTH + 1) {
            appendDateTime(dateTime)
            append('Z')
        }
    }

    companion object {
        val MAX = Instant(LongMilliseconds.MAX)
        val MIN = Instant(LongMilliseconds.MIN)
        val UNIX_EPOCH = Instant(0L.milliseconds)
    }
}

internal fun Instant.asUtcDateTime(): DateTime {
    return DateTime.fromMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch, UtcOffset.ZERO)
}

/**
 * An instant in time with nanosecond precision
 */
data class NanoInstant(
    val unixEpochSeconds: LongSeconds,
    val nanoOfSeconds: IntNanoseconds = 0.nanoseconds
)