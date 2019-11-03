package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.TimeZone
import io.islandtime.measures.milliseconds
import io.islandtime.toTimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ClockTest {
    @Test
    fun `UTC SystemClock`() {
        val clock = SystemClock.UTC

        assertTrue { clock.read() > 0L.milliseconds }
        assertTrue { clock.instant() > Instant.UNIX_EPOCH }
        assertEquals(TimeZone.UTC, clock.zone)
    }

    @Test
    fun `SystemClock() without zone`() {
        val clock = SystemClock()

        assertTrue { clock.read() > 0L.milliseconds }
        assertTrue { clock.instant() > Instant.UNIX_EPOCH }
        assertNotEquals("", clock.zone.id)
    }

    @Test
    fun `SystemClock() with zone`() {
        val clock = SystemClock("America/Denver".toTimeZone())

        assertTrue { clock.read() > 0L.milliseconds }
        assertTrue { clock.instant() > Instant.UNIX_EPOCH }
        assertEquals("America/Denver".toTimeZone(), clock.zone)
    }
}