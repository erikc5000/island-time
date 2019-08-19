package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.milliseconds

internal actual class SystemClock : Clock {
    actual override val timeZone: TimeZone = TimeZone(java.util.TimeZone.getDefault().id)

    actual override fun instant(): Instant {
        return Instant(System.currentTimeMillis().milliseconds)
    }
}