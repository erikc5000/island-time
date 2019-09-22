package dev.erikchristensen.islandtime.clock

import dev.erikchristensen.islandtime.TimeZone
import dev.erikchristensen.islandtime.interval.milliseconds

internal actual object PlatformSystemClock {
    actual fun currentZone() = TimeZone(java.util.TimeZone.getDefault().id)
    actual fun read() = System.currentTimeMillis().milliseconds
}