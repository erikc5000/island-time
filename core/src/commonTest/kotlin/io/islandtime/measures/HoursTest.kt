package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals

class HoursTest {
    @Test
    fun `toComponents breaks the hours up into days and hours`() {
        36.hours.toComponents { days, hours ->
            assertEquals(1.days, days)
            assertEquals(12.hours, hours)
        }
    }

    @Test
    fun `toComponentValues breaks the hours up into days and hours`() {
        36.hours.toComponentValues { days, hours ->
            assertEquals(1L, days)
            assertEquals(12, hours)
        }
    }

    @Test
    fun `toString converts zero hours to 'PT0H'`() {
        assertEquals("PT0H", 0.hours.toString())
    }

    @Test
    fun `toString converts to ISO-8601 period representation`() {
        assertEquals("PT1H", 1.hours.toString())
        assertEquals("-PT1H", (-1).hours.toString())
    }
}
