package dev.erikchristensen.islandtime

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ClockTest {
    @Test
    fun `systemClock()_instant()`() {
        assertTrue { systemClock().instant() > Instant.UNIX_EPOCH }
    }

    @Test
    fun `systemClock()_timeZone`() {
        assertNotEquals("", systemClock().timeZone.regionId)
    }
}