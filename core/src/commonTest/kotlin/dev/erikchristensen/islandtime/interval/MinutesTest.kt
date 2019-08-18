package dev.erikchristensen.islandtime.interval

import kotlin.test.Test
import kotlin.test.assertEquals

class MinutesTest {
    @Test
    fun `asSeconds() converts to seconds`() {
        assertEquals((-300).seconds, (-5).minutes.asSeconds())
        assertEquals(60L.seconds, 1L.minutes.asSeconds())
    }

    @Test
    fun `asMilliseconds() always converts to LongMilliseconds`() {
        assertEquals((-300_000L).milliseconds, (-5).minutes.asMilliseconds())
        assertEquals(60_000L.milliseconds, 1L.minutes.asMilliseconds())
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
}