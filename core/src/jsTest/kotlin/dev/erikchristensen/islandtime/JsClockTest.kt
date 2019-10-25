package dev.erikchristensen.islandtime

import kotlin.test.Test
import kotlin.test.assertTrue

class JsClockTest {
    @Test
    fun instant() {
        assertTrue { systemClock().instant() > Instant.UNIX_EPOCH }
    }

    @Test
    fun timeZone() {
        assertTrue { systemClock().timeZone.regionId.isNotEmpty() }
    }
}