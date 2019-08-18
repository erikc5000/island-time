package dev.erikchristensen.islandtime.interval

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
    fun `toLong() converts IntDays to LongDays`() {
        assertEquals(2L.days, 2.days.toLong())
    }

    @Test
    fun `toInt() converts LongDays to IntDays`() {
        assertEquals(2.days, 2L.days.toInt())
    }
}