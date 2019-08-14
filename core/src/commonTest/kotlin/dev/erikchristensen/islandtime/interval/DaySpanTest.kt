package dev.erikchristensen.islandtime.interval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DaySpanTest {
    @Test
    fun `DaySpans can be compared to other DaySpans`() {
        assertTrue { 0.days < 1.days }
        assertTrue { 0.days == 0.days }
        assertTrue { 5.days > (-1).days }
    }

    @Test
    fun `LongDaySpans can be compared to other LongDaySpans`() {
        assertTrue { 0L.days < 1L.days }
        assertTrue { 0L.days == 0L.days }
        assertTrue { 5L.days > (-1L).days }
    }

    @Test
    fun `toLong() converts to a DaySpan to a LongDaySpan`() {
        assertEquals(2L.days, 2.days.toLong())
    }

    @Test
    fun `toInt() converts to a LongDaySpan to a DaySpan`() {
        assertEquals(2.days, 2L.days.toInt())
    }
}