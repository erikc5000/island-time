package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class YearsTest {
    @Test
    fun `Years can be compared to other Years`() {
        assertTrue { 0.years < 1.years }
        assertTrue { 0.years == 0.years }
        assertTrue { 5.years > (-1).years }
    }

    @Test
    fun `absoluteValue returns the same value for 0 or positive values`() {
        listOf(0, 1, Long.MAX_VALUE).forEach {
            assertEquals(it.years, it.years.absoluteValue)
        }
    }

    @Test
    fun `absoluteValue returns a positive value for negative values`() {
        assertEquals(1.years, (-1).years.absoluteValue)
    }

    @Test
    fun `absoluteValue throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years.absoluteValue }
    }

    @Test
    fun `unary minus negates the value`() {
        listOf(
            0L to 0L,
            1L to -1L,
            -1L to 1L,
            Long.MAX_VALUE to -Long.MAX_VALUE
        ).forEach {
            assertEquals(it.second.years, -it.first.years)
        }
    }

    @Test
    fun `unary minus throws an exception when value in MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { -Long.MIN_VALUE.years }
    }

    @Test
    fun `multiplying by a scalar value throws an exception when overflow occurs`() {
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.years * 2 }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.years * 2L }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years * -1 }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years * -1L }
    }

    @Test
    fun `division by a scalar value`() {
        assertEquals(3.years, 9.years / 3)
        assertEquals(3.years, 9.years / 3L)
        assertEquals((-3).years, 9.years / -3)
        assertEquals((-3).years, 9.years / -3L)
    }

    @Test
    fun `dividing by -1 throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years / -1 }
    }

    @Test
    fun `adding months to years produces months`() {
        assertEquals(15.months, 1.years + 3.months)
        assertEquals(Long.MAX_VALUE.months, 1.years + (Long.MAX_VALUE - 12).months)
        assertEquals(Long.MIN_VALUE.months, (-1).years + (Long.MIN_VALUE + 12).months)
    }

    @Test
    fun `throws an exception when adding months to years causes overflow`() {
        assertFailsWith<ArithmeticException> { 1.years + (Long.MAX_VALUE - 11).months }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.years + 1.months }
        assertFailsWith<ArithmeticException> { (-1).years + (Long.MIN_VALUE + 11).months }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.years + (-1).months }
    }

    @Test
    fun `subtracting months from years produces months`() {
        assertEquals(21.months, 2.years - 3.months)
    }

    @Test
    fun `rem operator works`() {
        assertEquals(1.years, 5.years % 2)
        assertEquals(1.years, 5.years % 2L)
    }

    @Test
    fun `inWholeCenturies converts years to centuries`() {
        listOf(
            0 to 0,
            99 to 0,
            -99 to 0,
            100 to 1,
            -100 to -1
        ).forEach {
            assertEquals(it.second.centuries, it.first.years.inWholeCenturies)
        }
    }

    @Test
    fun `inWholeDecades converts years to decades`() {
        listOf(
            0 to 0,
            9 to 0,
            -9 to 0,
            10 to 1,
            -10 to -1
        ).forEach {
            assertEquals(it.second.decades, it.first.years.inWholeDecades)
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
    }

    @Test
    fun `inMonths throws an exception when overflow occurs`() {
        listOf(Long.MAX_VALUE, Long.MIN_VALUE).forEach {
            assertFailsWith<ArithmeticException> { it.years.inMonths }
        }
    }

    @Test
    fun `toLong converts to Long`() {
        listOf(0, -1, 1, Long.MIN_VALUE, Long.MAX_VALUE).forEach {
            assertEquals(it, it.years.toLong())
        }
    }

    @Test
    fun `toInt throws an exception if overflow occurs during conversion`() {
        listOf(Int.MAX_VALUE + 1L, Int.MIN_VALUE - 1L).forEach {
            assertFailsWith<ArithmeticException> { it.years.toInt() }
        }
    }

    @Test
    fun `toInt converts to Int`() {
        listOf(0, -1, 1, Int.MAX_VALUE, Int.MIN_VALUE).forEach {
            assertEquals(it, it.years.toInt())
        }
    }

    @Test
    fun `toString returns an ISO duration string`() {
        listOf(
            0L to "P0Y",
            1L to "P1Y",
            -1L to "-P1Y",
            Long.MAX_VALUE to "P${Long.MAX_VALUE}Y",
            Long.MIN_VALUE to "-P9223372036854775808Y"
        ).forEach {
            assertEquals(it.second, it.first.years.toString())
        }
    }

    @Test
    fun `toComponents and toComponentValues with centuries and decades`() {
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

            it.first.years.toComponentValues { centuries, decades, years ->
                assertEquals(it.second[0].toLong(), centuries)
                assertEquals(it.second[1], decades)
                assertEquals(it.second[2], years)
            }
        }
    }

    @Test
    fun `toComponents and toComponentValues with decades`() {
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

            it.first.years.toComponentValues { decades, years ->
                assertEquals(it.second[0].toLong(), decades)
                assertEquals(it.second[1], years)
            }
        }
    }
}
