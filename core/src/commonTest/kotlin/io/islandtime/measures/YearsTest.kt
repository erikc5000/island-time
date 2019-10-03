package io.islandtime.measures

import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
    fun `LongYears can be compared to IntYears`() {
        assertTrue { (Int.MAX_VALUE + 1L).years > Int.MAX_VALUE.years }
        assertTrue { (Int.MIN_VALUE - 1L).years < Int.MIN_VALUE.years }
        assertTrue { Int.MAX_VALUE.years < (Int.MAX_VALUE + 1L).years }
        assertTrue { Int.MIN_VALUE.years > (Int.MIN_VALUE - 1L).years }

        // Can't override equals() yet
//        assertTrue { 0L.years == 0.years }
    }

    @Test
    fun `IntYears can be compared to IntMonths`() {
        assertTrue { 1.years > 11.months }
        assertTrue { 0.years < 1.months }
        assertTrue { (Int.MAX_VALUE / 12.0).roundToLong().years > Int.MAX_VALUE.months }
        assertTrue { (Int.MIN_VALUE / 12.0).roundToLong().years < Int.MIN_VALUE.months }
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
    fun `inMonths converts years to months`() {
        assertEquals(12.months, 1.years.inMonths)
        assertEquals(12L.months, 1L.years.inMonths)
    }

    @Test
    fun `toLong() converts IntYears to LongYears`() {
        assertEquals(2L.years, 2.years.toLong())
    }

    @Test
    fun `toInt() converts LongYears to IntYears`() {
        assertEquals(2.years, 2L.years.toInt())
    }

    @Test
    fun `toIntExact() throws an exception if LongYears can't be converted to IntYears without overflow`() {
        assertFailsWith<ArithmeticException> { (Int.MAX_VALUE + 1L).years.toIntExact() }
        assertFailsWith<ArithmeticException> { (Int.MIN_VALUE - 1L).years.toIntExact() }
    }

    @Test
    fun `toIntExact() converts LongYears to IntYears when there's no overflow`() {
        assertEquals(Int.MAX_VALUE.years, Int.MAX_VALUE.toLong().years.toIntExact())
        assertEquals(Int.MIN_VALUE.years, Int.MIN_VALUE.toLong().years.toIntExact())
    }
}