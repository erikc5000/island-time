package dev.erikchristensen.islandtime.interval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YearsTest {
    @Test
    fun `IntYears can be compared to other IntYears`() {
        assertTrue { 0.years < 1.years }
        assertTrue { 0.years == 0.years }
        assertTrue { 5.years > (-1).years }
    }

    @Test
    fun `LongYears can be compared to other LongYears`() {
        assertTrue { 0L.years < 1L.years }
        assertTrue { 0L.years == 0L.years }
        assertTrue { 5L.years > (-1L).years }
    }

    @Test
    fun `adding months to years produces months`() {
        assertEquals(15.months, 1.years + 3.months)
        assertEquals(15L.months, 1L.years + 3L.months)
    }

    @Test
    fun `subtracting months from years produces months`() {
        assertEquals(21.months, 2.years - 3.months)
        assertEquals(21L.months, 2L.years - 3L.months)
    }

    @Test
    fun `asMonths() converts years to months`() {
        assertEquals(12.months, 1.years.asMonths())
        assertEquals(12L.months, 1L.years.asMonths())
    }

    @Test
    fun `toLong() converts IntYears to LongYears`() {
        assertEquals(2L.years, 2.years.toLong())
    }

    @Test
    fun `toInt() converts LongYears to IntYears`() {
        assertEquals(2.years, 2L.years.toInt())
    }
}