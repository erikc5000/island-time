package io.islandtime.clock

import io.islandtime.TimeZone
import io.islandtime.measures.milliseconds

internal actual object PlatformSystemClock {
    actual fun currentZone() = TimeZone(java.util.TimeZone.getDefault().id)
    actual fun read() = System.currentTimeMillis().milliseconds
}