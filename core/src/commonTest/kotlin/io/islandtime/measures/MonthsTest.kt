package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MonthsTest {
    @Test
    fun `IntMonths can be compared to other IntMonths`() {
        assertTrue { 0.months < 1.months }
        assertTrue { 0.months == 0.months }
        assertTrue { 5.months > (-1).months }
    }

    @Test
    fun `LongMonths can be compared to other LongMonths`() {
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
    fun `inYears converts months to an equivalent number of full years`() {
        assertEquals(1.years, 13.months.inYears)
        assertEquals(1L.years, 13L.months.inYears)
    }

    @Test
    fun `toLongMonths() converts IntMonths to LongMonths`() {
        assertEquals(2L.months, 2.months.toLongMonths())
    }

    @Test
    fun `toIntMonths() converts LongMonths to IntMonths`() {
        assertEquals(2.months, 2L.months.toIntMonths())
    }
}