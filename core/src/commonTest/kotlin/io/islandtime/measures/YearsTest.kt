package io.islandtime.measures

import kotlin.math.absoluteValue
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
    fun `absoluteValue returns the same value for 0 or positive values`() {
        listOf(0, 1, Int.MAX_VALUE).forEach {
            assertEquals(it.years, it.years.absoluteValue)
            assertEquals(it.toLong().years, it.toLong().years.absoluteValue)
        }

        assertEquals(Long.MAX_VALUE.years, Long.MAX_VALUE.years.absoluteValue)
    }

    @Test
    fun `absoluteValue returns a positive value for negatives values`() {
        assertEquals(1.years, (-1).years.absoluteValue)
        assertEquals(1L.years, (-1L).years.absoluteValue)
    }

    @Test
    fun `absoluteValue throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE.years.absoluteValue }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years.absoluteValue }
    }

    @Test
    fun `unary minus negates the value`() {
        listOf(
            0 to 0,
            1 to -1,
            -1 to 1,
            Int.MAX_VALUE to -Int.MAX_VALUE
        ).forEach {
            assertEquals(it.second.years, -it.first.years)
            assertEquals(it.second.toLong().years, -it.first.toLong().years)
        }

        assertEquals((-Long.MAX_VALUE).years, -Long.MAX_VALUE.years)
    }

    @Test
    fun `unary minus throws an exception when value in MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { -Int.MIN_VALUE.years }
        assertFailsWith<ArithmeticException> { -Long.MIN_VALUE.years }
    }

    @Test
    fun `multiplying by a scalar value throws an exception when overflow occurs`() {
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE.years * 2 }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE.years * -1 }

        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.years * 2 }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.years * 2L }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years * -1 }
    }

    @Test
    fun `division by a scalar value`() {
        assertEquals(3.years, 9.years / 3)
        assertEquals(3L.years, 9.years / 3L)
        assertEquals((-3L).years, 9L.years / -3)
        assertEquals((-3L).years, 9L.years / -3L)
    }

    @Test
    fun `dividing by -1 throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE.years / -1 }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years / -1  }
    }

    @Test
    fun `adding months to years produces months`() {
        assertEquals(15.months, 1.years + 3.months)
        assertEquals(Int.MAX_VALUE.months, 1.years + (Int.MAX_VALUE - 12).months)
        assertEquals(Int.MAX_VALUE.toLong().months, 1.years + (Int.MAX_VALUE - 12L).months)
        assertEquals(Int.MIN_VALUE.months, (-1).years + (Int.MIN_VALUE + 12).months)
        assertEquals(Int.MIN_VALUE.toLong().months, (-1).years + (Int.MIN_VALUE + 12L).months)

        assertEquals(15L.months, 1L.years + 3L.months)
        assertEquals(Long.MAX_VALUE.months, 1L.years + (Long.MAX_VALUE - 12L).months)
        assertEquals(Long.MAX_VALUE.months, 1L.years + (Long.MAX_VALUE - 12).months)
        assertEquals(Long.MIN_VALUE.months, (-1L).years + (Long.MIN_VALUE + 12L).months)
        assertEquals(Long.MIN_VALUE.months, (-1L).years + (Long.MIN_VALUE + 12).months)
    }

    @Test
    fun `throws an exception when adding months to years causes overflow`() {
        assertFailsWith<ArithmeticException> { 1.years + (Int.MAX_VALUE - 11).months }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE.years + 1.months }
        assertFailsWith<ArithmeticException> { (-1).years + (Int.MIN_VALUE + 11).months }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE.years + (-1).months }
    }

    @Test
    fun `subtracting months from years produces months`() {
        assertEquals(21.months, 2.years - 3.months)
        assertEquals(21L.months, 2.years - 3L.months)
        assertEquals(21L.months, 2L.years - 3.months)
        assertEquals(21L.months, 2L.years - 3L.months)
    }

    @Test
    fun `rem operator works`() {
        assertEquals(1.years, 5.years % 2)
        assertEquals(1L.years, 5.years % 2L)
        assertEquals(1L.years, 5L.years % 2)
        assertEquals(1L.years, 5L.years % 2L)
    }

    @Test
    fun `inCenturies converts years to centuries`() {
        listOf(
            0 to 0,
            99 to 0,
            -99 to 0,
            100 to 1,
            -100 to -1
        ).forEach {
            assertEquals(it.second.centuries, it.first.years.inCenturies)
            assertEquals(it.second.toLong().centuries, it.first.toLong().years.inCenturies)
        }
    }

    @Test
    fun `inDecades converts years to decades`() {
        listOf(
            0 to 0,
            9 to 0,
            -9 to 0,
            10 to 1,
            -10 to -1
        ).forEach {
            assertEquals(it.second.decades, it.first.years.inDecades)
            assertEquals(it.second.toLong().decades, it.first.toLong().years.inDecades)
        }
    }

    @Test
    fun `inMonths converts years to months`() {
        listOf(
            0.years to 0.months,
            (-1).years to (-12).months,
            1.years to 12.months,
            178956970.years to 2147483640.months,
            (-178956970).years to (-2147483640).months
        ).forEach {
            assertEquals(it.second, it.first.inMonths)
        }

        listOf(
            0L.years to 0L.months,
            (-1L).years to (-12L).months,
            1L.years to 12L.months
        ).forEach {
            assertEquals(it.second, it.first.inMonths)
        }
    }

    @Test
    fun `inMonths throws an exception when overflow occurs`() {
        listOf(Int.MAX_VALUE, Int.MIN_VALUE, 178956971, -178956971).forEach {
            assertFailsWith<ArithmeticException> { it.years.inMonths }
        }

        listOf(Long.MAX_VALUE, Long.MIN_VALUE).forEach {
            assertFailsWith<ArithmeticException> { it.years.inMonths }
        }
    }

    @Test
    fun `toLong() and toLongYears() convert to Long`() {
        listOf(0, -1, 1, Int.MIN_VALUE, Int.MAX_VALUE).forEach {
            assertEquals(it.toLong(), it.years.toLong())
            assertEquals(it.toLong().years, it.years.toLongYears())
        }
    }

    @Test
    fun `toInt() and toIntYears() throw an exception if overflow occurs during conversion`() {
        listOf(Int.MAX_VALUE + 1L, Int.MIN_VALUE - 1L).forEach {
            assertFailsWith<ArithmeticException> { it.years.toInt() }
            assertFailsWith<ArithmeticException> { it.years.toIntYears() }
        }
    }

    @Test
    fun `toInt() and toIntYears() convert to Int`() {
        listOf(0, -1, 1, Int.MAX_VALUE, Int.MIN_VALUE).forEach {
            assertEquals(it.years, it.toLong().years.toIntYears())
            assertEquals(it, it.toLong().years.toInt())
        }
    }

    @Test
    fun `toString() returns an ISO duration string`() {
        listOf(
            0 to "P0Y",
            1 to "P1Y",
            -1 to "-P1Y",
            Int.MAX_VALUE to "P${Int.MAX_VALUE}Y",
            Int.MIN_VALUE + 1 to "-P${(Int.MIN_VALUE + 1L).absoluteValue}Y",
            Int.MIN_VALUE to "-P${Int.MIN_VALUE.toLong().absoluteValue}Y"
        ).forEach {
            assertEquals(it.second, it.first.years.toString())
            assertEquals(it.second, it.first.toLong().years.toString())
        }

        listOf(
            Long.MAX_VALUE to "P${Long.MAX_VALUE}Y",
            Long.MIN_VALUE to "-P9223372036854775808Y"
        ).forEach {
            assertEquals(it.second, it.first.years.toString())
        }
    }

    @Test
    fun `toComponents() with centuries and decades`() {
        listOf(
            0 to listOf(0, 0, 0),
            1 to listOf(0, 0, 1),
            -1 to listOf(0, 0, -1),
            11 to listOf(0, 1, 1),
            -11 to listOf(0, -1, -1),
            111 to listOf(1, 1, 1),
            -111 to listOf(-1, -1, -1)
        ).forEach {
            it.first.years.toComponents { centuries, decades, years ->
                assertEquals(it.second[0].centuries, centuries)
                assertEquals(it.second[1].decades, decades)
                assertEquals(it.second[2].years, years)
            }

            it.first.toLong().years.toComponents { centuries, decades, years ->
                assertEquals(it.second[0].toLong().centuries, centuries)
                assertEquals(it.second[1].decades, decades)
                assertEquals(it.second[2].years, years)
            }
        }
    }

    @Test
    fun `toComponents() with decades`() {
        listOf(
            0 to listOf(0, 0),
            1 to listOf(0, 1),
            -1 to listOf(0, -1),
            11 to listOf(1, 1),
            -11 to listOf(-1, -1)
        ).forEach {
            it.first.years.toComponents { decades, years ->
                assertEquals(it.second[0].decades, decades)
                assertEquals(it.second[1].years, years)
            }

            it.first.toLong().years.toComponents { decades, years ->
                assertEquals(it.second[0].toLong().decades, decades)
                assertEquals(it.second[1].years, years)
            }
        }
    }
}