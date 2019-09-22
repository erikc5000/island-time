package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.TimeZone
import io.islandtime.toTimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ClockTest {
    @Test
    fun `SystemClock_Utc instant()`() {
        assertTrue { SystemClock.UTC.instant() > Instant.UNIX_EPOCH }
    }

    @Test
    fun `SystemClock_Utc timeZone`() {
        assertEquals(TimeZone.UTC, SystemClock.UTC.zone)
    }

    @Test
    fun `SystemClock_Default() timeZone`() {
        assertNotEquals("", SystemClock().zone.regionId)
    }

    @Test
    fun `SystemClock_Zoned() timeZone`() {
        assertEquals("America/Denver".toTimeZone(), SystemClock("America/Denver".toTimeZone()).zone)
    }
}