package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals

class MinutesTest {
    @Test
    fun `inSeconds converts to seconds`() {
        assertEquals((-300).seconds, (-5).minutes.inSeconds)
        assertEquals(60L.seconds, 1L.minutes.inSeconds)
    }

    @Test
    fun `inMilliseconds converts to Milliseconds`() {
        assertEquals((-300_000L).milliseconds, (-5).minutes.inMilliseconds)
        assertEquals(60_000L.milliseconds, 1L.minutes.inMilliseconds)
    }

    @Test
    fun `toComponents breaks the minutes up into days + hours + minutes`() {
        (1.days + 1.hours + 1.minutes).toComponents { days, hours, minutes ->
            assertEquals(1.days, days)
            assertEquals(1.hours, hours)
            assertEquals(1.minutes, minutes)
        }
    }

    @Test
    fun `toComponentValues breaks the minutes up into days + hours + minutes`() {
        (1.days + 1.hours + 1.minutes).toComponentValues { days, hours, minutes ->
            assertEquals(1L, days)
            assertEquals(1, hours)
            assertEquals(1, minutes)
        }
    }

    @Test
    fun `toComponents breaks the minutes up into hours + minutes`() {
        (1.days + 1.hours + 1.minutes).toComponents { hours, minutes ->
            assertEquals(25.hours, hours)
            assertEquals(1.minutes, minutes)
        }
    }

    @Test
    fun `toComponentValues breaks the minutes up into hours + minutes`() {
        (1.days + 1.hours + 1.minutes).toComponentValues { hours, minutes ->
            assertEquals(25L, hours)
            assertEquals(1, minutes)
        }
    }

    @Test
    fun `Minutes_toString converts zero minutes to 'PT0M'`() {
        assertEquals("PT0M", 0.minutes.toString())
    }

    @Test
    fun `Minutes_toString converts to ISO-8601 period representation`() {
        assertEquals("PT1M", 1.minutes.toString())
        assertEquals("-PT1M", (-1).minutes.toString())
    }
}
