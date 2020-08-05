package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.TimeZone
import io.islandtime.measures.milliseconds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ClockTest {
    @Test
    fun `UTC SystemClock`() {
        val clock = SystemClock.UTC

        assertTrue { clock.readMilliseconds() > 0L.milliseconds }
        assertTrue { clock.readInstant() > Instant.UNIX_EPOCH }
        assertEquals(TimeZone.UTC, clock.zone)
    }

    @Test
    fun `SystemClock() without zone`() {
        val clock = SystemClock()

        assertTrue { clock.readMilliseconds() > 0L.milliseconds }
        assertTrue { clock.readInstant() > Instant.UNIX_EPOCH }
        assertNotEquals("", clock.zone.id)
    }

    @Test
    fun `SystemClock() with zone`() {
        val clock = SystemClock(TimeZone("America/Denver"))

        assertTrue { clock.readMilliseconds() > 0L.milliseconds }
        assertTrue { clock.readInstant() > Instant.UNIX_EPOCH }
        assertEquals(TimeZone("America/Denver"), clock.zone)
    }
}