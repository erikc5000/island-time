package io.islandtime.clock

import io.islandtime.*
import io.islandtime.measures.milliseconds

internal actual object PlatformSystemClock {
    // TODO: Benchmark and see if using a ConcurrentHashMap would be faster than parsing the ID each time
    actual fun currentZone() = java.util.TimeZone.getDefault().toIslandTimeZone()

    actual fun read() = System.currentTimeMillis().milliseconds
}

internal fun java.util.TimeZone.toIslandTimeZone(): TimeZone {
    return if (id.startsWith("GMT")) {
        if (id.length == 3) {
            TimeZone.Region(id)
        } else {
            TimeZone.FixedOffset(id.substring(3))
        }
    } else {
        TimeZone.Region(id)
    }
}