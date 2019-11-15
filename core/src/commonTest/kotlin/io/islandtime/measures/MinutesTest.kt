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
    fun `inMilliseconds always converts to LongMilliseconds`() {
        assertEquals((-300_000L).milliseconds, (-5).minutes.inMilliseconds)
        assertEquals(60_000L.milliseconds, 1L.minutes.inMilliseconds)
    }

    @Test
    fun `IntHours_toComponents() breaks the minutes up into days, hours, and minutes`() {
        (1.days + 1.hours + 1.minutes).toComponents { days, hours, minutes ->
            assertEquals(1.days, days)
            assertEquals(1.hours, hours)
            assertEquals(1.minutes, minutes)
        }
    }

    @Test
    fun `IntHours_toComponents() breaks the minutes up into hours, and minutes`() {
        (1.days + 1.hours + 1.minutes).toComponents { hours, minutes ->
            assertEquals(25.hours, hours)
            assertEquals(1.minutes, minutes)
        }
    }

    @Test
    fun `LongHours_toComponents() breaks the minutes up into days, hours, and minutes`() {
        (1L.days + 1.hours + 1.minutes).toComponents { days, hours, minutes ->
            assertEquals(1L.days, days)
            assertEquals(1.hours, hours)
            assertEquals(1.minutes, minutes)
        }
    }

    @Test
    fun `LongHours_toComponents() breaks the minutes up into hours, and minutes`() {
        (1L.days + 1.hours + 1.minutes).toComponents { hours, minutes ->
            assertEquals(25L.hours, hours)
            assertEquals(1.minutes, minutes)
        }
    }

    @Test
    fun `IntMinutes_toString() converts zero minutes to 'PT0M'`() {
        assertEquals("PT0M", 0.minutes.toString())
    }

    @Test
    fun `IntMinutes_toString() converts to ISO-8601 period representation`() {
        assertEquals("PT1M", 1.minutes.toString())
        assertEquals("-PT1M", (-1).minutes.toString())
    }

    @Test
    fun `LongMinutes_toString() converts zero minutes to 'PT0M'`() {
        assertEquals("PT0M", 0L.minutes.toString())
    }

    @Test
    fun `LongMinutes_toString() converts to ISO-8601 period representation`() {
        assertEquals("PT1M", 1L.minutes.toString())
        assertEquals("-PT1M", (-1L).minutes.toString())
    }
}