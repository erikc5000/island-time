package dev.erikchristensen.islandtime.clock

import dev.erikchristensen.islandtime.interval.milliseconds
import kotlin.test.Test
import kotlin.test.assertTrue

class IosPlatformSystemClock {
    @Test
    fun `read() returns a time after the unix epoch`() {
        assertTrue { PlatformSystemClock.read() > 0L.milliseconds }
    }
}