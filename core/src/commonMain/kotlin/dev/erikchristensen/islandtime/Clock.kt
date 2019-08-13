package dev.erikchristensen.islandtime

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface Clock {
    val timeZone: TimeZone
    fun instant(): Instant

    companion object {
        @JvmStatic
        fun system(): Clock {
            return SystemClock()
        }
    }
}

internal expect class SystemClock() : Clock {
    override val timeZone: TimeZone
    override fun instant(): Instant
}

fun systemClock() = Clock.system()

class FixedClock @JvmOverloads constructor(
    private val unixEpochMilliseconds: Long,
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
