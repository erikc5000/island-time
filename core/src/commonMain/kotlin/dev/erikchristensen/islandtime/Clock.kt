package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.*

interface Clock {
    val timeZone: TimeZone
    fun instant(): Instant
}

internal expect class SystemClock() : Clock {
    override val timeZone: TimeZone
    override fun instant(): Instant
}

fun systemClock(): Clock = SystemClock()

class FixedClock(
    private var unixEpochMilliseconds: LongMilliseconds,
    override val timeZone: TimeZone = TimeZone.UTC
) : Clock {

    operator fun plusAssign(milliseconds: LongMilliseconds) {
        unixEpochMilliseconds += milliseconds
    }

    operator fun plusAssign(milliseconds: IntMilliseconds) {
        unixEpochMilliseconds += milliseconds
    }

    operator fun plusAssign(seconds: LongSeconds) {
        unixEpochMilliseconds += seconds
    }

    operator fun plusAssign(seconds: IntSeconds) {
        unixEpochMilliseconds += seconds
    }

    operator fun plusAssign(minutes: LongMinutes) {
        unixEpochMilliseconds += minutes
    }

    operator fun plusAssign(minutes: IntMinutes) {
        unixEpochMilliseconds += minutes
    }

    operator fun plusAssign(hours: LongHours) {
        unixEpochMilliseconds += hours
    }

    operator fun plusAssign(hours: IntHours) {
        unixEpochMilliseconds += hours
    }

    operator fun plusAssign(days: LongDays) {
        unixEpochMilliseconds += days
    }

    operator fun plusAssign(days: IntDays) {
        unixEpochMilliseconds += days
    }

    operator fun minusAssign(milliseconds: LongMilliseconds) = plusAssign(-milliseconds)
    operator fun minusAssign(milliseconds: IntMilliseconds) = plusAssign(-milliseconds)

    override fun instant(): Instant {
        return Instant.fromMillisecondsSinceUnixEpoch(unixEpochMilliseconds)
    }
}

// fun Clock.localDateTime(): DateTime
// fun Clock.localTime(): Time
// fun Clock.localDate(): Date
// fun Clock.now(): RegionalDateTime
