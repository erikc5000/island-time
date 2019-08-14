package dev.erikchristensen.islandtime.interval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MonthSpanTest {
    @Test
    fun `MonthSpans can be compared to other MonthSpans`() {
        assertTrue { 0.months < 1.months }
        assertTrue { 0.months == 0.months }
        assertTrue { 5.months > (-1).months }
    }

    @Test
    fun `LongMonthSpans can be compared to other LongMonthSpans`() {
        assertTrue { 0L.months < 1L.months }
        assertTrue { 0L.months == 0L.months }
        assertTrue { 5L.months > (-1L).months }
    }

    @Test
    fun `adding years to months produces months`() {
        assertEquals(13.months, 1.months + 1.years)
        assertEquals(13L.months, 1L.months + 1L.years)
    }

    @Test
    fun `subtracting years from months produces months`() {
        assertEquals(0.months, 12.months - 1.years)
        assertEquals(0L.months, 12L.months - 1L.years)
    }

    @Test
    fun `asWholeYears() converts months to an equivalent number of full years`() {
        assertEquals(1.years, 13.months.asWholeYears())
        assertEquals(1L.years, 13L.months.asWholeYears())
    }

    @Test
    fun `toLong() converts to a MonthSpan to a LongMonthSpan`() {
        assertEquals(2L.months, 2.months.toLong())
    }

    @Test
    fun `toInt() converts to a LongMonthSpan to a MonthSpan`() {
        assertEquals(2.months, 2L.months.toInt())
    }
}