package io.islandtime.interval

import kotlin.test.Test
import kotlin.test.assertEquals

class HoursTest {
    @Test
    fun `IntHours_toComponents() breaks the hours up into days and hours`() {
        36.hours.toComponents { days, hours ->
            assertEquals(1.days, days)
            assertEquals(12.hours, hours)
        }
    }

    @Test
    fun `LongHours_toComponents() breaks the hours up into days and hours`() {
        36L.hours.toComponents { days, hours ->
            assertEquals(1L.days, days)
            assertEquals(12.hours, hours)
        }
    }

    @Test
    fun `IntHours_toString() converts zero hours to 'PT0S'`() {
        assertEquals("PT0S", 0.hours.toString())
    }

    @Test
    fun `IntHours_toString() converts to ISO-8601 period representation`() {
        assertEquals("PT1H", 1.hours.toString())
        assertEquals("PT-1H", (-1).hours.toString())
    }

    @Test
    fun `LongHours_toString() converts zero hours to 'PT0S'`() {
        assertEquals("PT0S", 0L.hours.toString())
    }

    @Test
    fun `LongHours_toString() converts to ISO-8601 period representation`() {
        assertEquals("PT1H", 1L.hours.toString())
        assertEquals("PT-1H", (-1L).hours.toString())
    }
}