package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.*

// TODO: Flush this out... possibly introduce EpochDay, EpochSecond, etc. classes to express "instants" with
// different granularity levels.  Really, an instant is just a duration relative to an epoch.

/**
 * An instant in time with millisecond precision
 */
inline class Instant(val unixEpochMilliseconds: LongMilliseconds) : Comparable<Instant> {

    override fun compareTo(other: Instant): Int {
        return unixEpochMilliseconds.compareTo(other.unixEpochMilliseconds)
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
    val days = unixEpochMilliseconds.toWholeDays()
    val remainingMilliseconds = unixEpochMilliseconds - days
    val nanosecondOfDay = remainingMilliseconds.asNanoseconds()
    return DateTime(Date.ofUnixEpochDays(days), Time.ofNanosecondOfDay(nanosecondOfDay.value))
}

/**
 * An instant in time with nanosecond precision
 */
data class NanoInstant(
    val unixEpochSeconds: LongSeconds,
    val nanoOfSeconds: IntNanoseconds = 0.nanoseconds
)