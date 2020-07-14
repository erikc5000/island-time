package io.islandtime.internal

import io.islandtime.TimeZone

// TODO: Benchmark and see if using a ConcurrentHashMap would be faster than parsing the ID each time
internal actual fun systemDefaultTimeZone(): TimeZone = java.util.TimeZone.getDefault().toIslandTimeZone()

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