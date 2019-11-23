package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DaysTest {
    @Test
    fun `IntDays can be compared to other IntDays`() {
        assertTrue { 0.days < 1.days }
        assertTrue { 0.days == 0.days }
        assertTrue { 5.days > (-1).days }
    }

    @Test
    fun `LongDays can be compared to other LongDays`() {
        assertTrue { 0L.days < 1L.days }
        assertTrue { 0L.days == 0L.days }
        assertTrue { 5L.days > (-1L).days }
    }

    @Test
    fun `toLongDays() converts IntDays to LongDays`() {
        assertEquals(2L.days, 2.days.toLongDays())
    }

    @Test
    fun `toIntDays() converts LongDays to IntDays`() {
        assertEquals(2.days, 2L.days.toIntDays())
    }

    @Test
    fun `IntDays_toString() converts zero days to 'P0D'`() {
        assertEquals("P0D", 0.days.toString())
    }

    @Test
    fun `IntDays_toString() converts to ISO-8601 period representation`() {
        assertEquals("P1D", 1.days.toString())
        assertEquals("-P1D", (-1).days.toString())
    }

    @Test
    fun `LongDays_toString() converts zero days to 'P0D'`() {
        assertEquals("P0D", 0L.days.toString())
    }

    @Test
    fun `LongDays_toString() converts to ISO-8601 period representation`() {
        assertEquals("P1D", 1L.days.toString())
        assertEquals("-P1D", (-1L).days.toString())
    }
}