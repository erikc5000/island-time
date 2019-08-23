package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.LongMilliseconds

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
    private val unixEpochMilliseconds: LongMilliseconds,
    override val timeZone: TimeZone = TimeZone.UTC
) : Clock {

    override fun instant(): Instant {
        return Instant(unixEpochMilliseconds)
    }
}

// fun Clock.localDateTime(): DateTime
// fun Clock.localTime(): Time
// fun Clock.localDate(): Date
// fun Clock.now(): RegionalDateTime
