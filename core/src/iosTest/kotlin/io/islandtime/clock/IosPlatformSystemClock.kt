package io.islandtime.clock

import io.islandtime.measures.milliseconds
import kotlin.test.Test
import kotlin.test.assertTrue

class IosPlatformSystemClock {
    @Test
    fun `read() returns a time after the unix epoch`() {
        assertTrue { PlatformSystemClock.read() > 0L.milliseconds }
    }
}