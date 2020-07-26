package io.islandtime.clock

import io.islandtime.measures.milliseconds

internal actual object PlatformSystemClock {
    actual fun read() = System.currentTimeMillis().milliseconds
}