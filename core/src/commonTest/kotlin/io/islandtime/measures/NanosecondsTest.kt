package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds as kotlinNanoseconds

class NanosecondsTest {
    @Test
    fun `IntNanoseconds can be compared to other IntNanoseconds`() {
        assertTrue { 0.nanoseconds < 1.nanoseconds }
        assertTrue { 0.nanoseconds == 0.nanoseconds }
        assertTrue { 5.nanoseconds > (-1).nanoseconds }
    }

    @Test
    fun `LongNanoseconds can be compared to other LongNanoseconds`() {
        assertTrue { 0L.nanoseconds < 1L.nanoseconds }
        assertTrue { 0L.nanoseconds == 0L.nanoseconds }
        assertTrue { 5L.nanoseconds > (-1L).nanoseconds }
    }

    @Test
    fun `absoluteValue returns the same value for 0 or positive values`() {
        listOf(0, 1, Int.MAX_VALUE).forEach {
            assertEquals(it.nanoseconds, it.nanoseconds.absoluteValue)
            assertEquals(it.toLong().nanoseconds, it.toLong().nanoseconds.absoluteValue)
        }

        assertEquals(Long.MAX_VALUE.nanoseconds, Long.MAX_VALUE.nanoseconds.absoluteValue)
    }

    @Test
    fun `absoluteValue returns a positive value for negatives values`() {
        assertEquals(1.nanoseconds, (-1).nanoseconds.absoluteValue)
        assertEquals(1L.nanoseconds, (-1L).nanoseconds.absoluteValue)
    }

    @Test
    fun `absoluteValue throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE.nanoseconds.absoluteValue }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.nanoseconds.absoluteValue }
    }

    @Test
    fun `unary minus negates the value`() {
        listOf(
            0 to 0,
            1 to -1,
            -1 to 1,
            Int.MAX_VALUE to -Int.MAX_VALUE
        ).forEach {
            assertEquals(it.second.nanoseconds, -it.first.nanoseconds)
            assertEquals(it.second.toLong().nanoseconds, -it.first.toLong().nanoseconds)
        }

        assertEquals((-Long.MAX_VALUE).nanoseconds, -Long.MAX_VALUE.nanoseconds)
    }

    @Test
    fun `unary minus throws an exception when value in MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { -Int.MIN_VALUE.nanoseconds }
        assertFailsWith<ArithmeticException> { -Long.MIN_VALUE.nanoseconds }
    }

    @Test
    fun `multiplying by a scalar value throws an exception when overflow occurs`() {
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE.nanoseconds * Long.MIN_VALUE }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.nanoseconds * 2 }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.nanoseconds * 2L }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.nanoseconds * -1 }

        assertFailsWith<ArithmeticException> { Long.MIN_VALUE * Int.MIN_VALUE.nanoseconds }
        assertFailsWith<ArithmeticException> { 2 * Long.MAX_VALUE.nanoseconds }
        assertFailsWith<ArithmeticException> { 2L * Long.MAX_VALUE.nanoseconds }
        assertFailsWith<ArithmeticException> { -1 * Long.MIN_VALUE.nanoseconds }
    }

    @Test
    fun `division by a scalar value`() {
        assertEquals(3.nanoseconds, 9.nanoseconds / 3)
        assertEquals(3L.nanoseconds, 9.nanoseconds / 3L)
        assertEquals((-3L).nanoseconds, 9L.nanoseconds / -3)
        assertEquals((-3L).nanoseconds, 9L.nanoseconds / -3L)
    }

    @Test
    fun `dividing by -1 throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE.nanoseconds / -1 }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.nanoseconds / -1 }
    }

    @Test
    fun `adding or subtracting IntNanoseconds forces lengthening to Long`() {
        assertEquals(1L.nanoseconds, 0.nanoseconds + 1.nanoseconds)
        assertEquals((-1L).nanoseconds, 0.nanoseconds - 1.nanoseconds)
    }

    @Test
    fun `rem operator works`() {
        assertEquals(1.nanoseconds, 5.nanoseconds % 2)
        assertEquals(1L.nanoseconds, 5.nanoseconds % 2L)
        assertEquals(1L.nanoseconds, 5L.nanoseconds % 2)
        assertEquals(1L.nanoseconds, 5L.nanoseconds % 2L)
    }

    @Test
    fun `inSeconds converts nanoseconds to seconds`() {
        listOf(
            0 to 0,
            999_999_999 to 0,
            -999_999_999 to 0,
            1_000_000_000 to 1,
            -1_000_000_000 to -1
        ).forEach {
            assertEquals(it.second.seconds, it.first.nanoseconds.inSeconds)
            assertEquals(it.second.toLong().seconds, it.first.toLong().nanoseconds.inSeconds)
        }
    }

    @Test
    fun `inMicroseconds converts nanoseconds to Microseconds`() {
        listOf(
            0 to 0,
            999 to 0,
            -999 to 0,
            1_000 to 1,
            -1_000 to -1
        ).forEach {
            assertEquals(it.second.microseconds, it.first.nanoseconds.inMicroseconds)
            assertEquals(it.second.toLong().microseconds, it.first.toLong().nanoseconds.inMicroseconds)
        }
    }

    @Test
    fun `toLong() and toLongNanoseconds() convert to Long`() {
        listOf(0, -1, 1, Int.MIN_VALUE, Int.MAX_VALUE).forEach {
            assertEquals(it.toLong(), it.nanoseconds.toLong())
            assertEquals(it.toLong().nanoseconds, it.nanoseconds.toLongNanoseconds())
        }
    }

    @Test
    fun `toInt() and toIntNanoseconds() throw an exception if overflow occurs during conversion`() {
        listOf(Int.MAX_VALUE + 1L, Int.MIN_VALUE - 1L).forEach {
            assertFailsWith<ArithmeticException> { it.nanoseconds.toInt() }
            assertFailsWith<ArithmeticException> { it.nanoseconds.toIntNanoseconds() }
        }
    }

    @Test
    fun `toInt() and toIntNanoseconds() convert to Int`() {
        listOf(0, -1, 1, Int.MAX_VALUE, Int.MIN_VALUE).forEach {
            assertEquals(it.nanoseconds, it.toLong().nanoseconds.toIntNanoseconds())
            assertEquals(it, it.toLong().nanoseconds.toInt())
        }
    }

    @Test
    fun `toString() returns an ISO duration string`() {
        listOf(
            0 to "PT0S",
            1 to "PT0.000000001S",
            -1 to "-PT0.000000001S",
            999_000_000 to "PT0.999S",
            -999_000_000 to "-PT0.999S",
            1_000_000_000 to "PT1S",
            -1_000_000_000 to "-PT1S",
            Int.MAX_VALUE to "PT2.147483647S",
            Int.MIN_VALUE + 1 to "-PT2.147483647S",
            Int.MIN_VALUE to "-PT2.147483648S"
        ).forEach {
            assertEquals(it.second, it.first.nanoseconds.toString())
            assertEquals(it.second, it.first.toLong().nanoseconds.toString())
        }

        listOf(
            Long.MAX_VALUE to "PT9223372036.854775807S",
            Long.MIN_VALUE to "-PT9223372036.854775808S"
        ).forEach {
            assertEquals(it.second, it.first.nanoseconds.toString())
        }
    }

    @Test
    fun `toComponents() with days, hours, minutes, seconds, milliseconds, and microseconds`() {
        listOf(
            0 to listOf(0, 0, 0, 0, 0, 0, 0),
            1 to listOf(0, 0, 0, 0, 0, 0, 1),
            -1 to listOf(0, 0, 0, 0, 0, 0, -1)
        ).forEach {
            it.first.nanoseconds
                .toComponents { days, hours, minutes, seconds, milliseconds, microseconds, nanoseconds ->
                    assertEquals(it.second[0].days, days)
                    assertEquals(it.second[1].hours, hours)
                    assertEquals(it.second[2].minutes, minutes)
                    assertEquals(it.second[3].seconds, seconds)
                    assertEquals(it.second[4].milliseconds, milliseconds)
                    assertEquals(it.second[5].microseconds, microseconds)
                    assertEquals(it.second[6].nanoseconds, nanoseconds)
                }

            it.first.toLong().nanoseconds
                .toComponents { days, hours, minutes, seconds, milliseconds, microseconds, nanoseconds ->
                    assertEquals(it.second[0].toLong().days, days)
                    assertEquals(it.second[1].hours, hours)
                    assertEquals(it.second[2].minutes, minutes)
                    assertEquals(it.second[3].seconds, seconds)
                    assertEquals(it.second[4].milliseconds, milliseconds)
                    assertEquals(it.second[5].microseconds, microseconds)
                    assertEquals(it.second[6].nanoseconds, nanoseconds)
                }
        }
    }

    @Test
    fun `toComponents() with microseconds`() {
        listOf(
            0 to listOf(0, 0),
            1 to listOf(0, 1),
            -1 to listOf(0, -1),
            1_001 to listOf(1, 1),
            -1_001 to listOf(-1, -1)
        ).forEach {
            it.first.nanoseconds.toComponents { microseconds, nanoseconds ->
                assertEquals(it.second[0].microseconds, microseconds)
                assertEquals(it.second[1].nanoseconds, nanoseconds)
            }

            it.first.toLong().nanoseconds.toComponents { microseconds, nanoseconds ->
                assertEquals(it.second[0].toLong().microseconds, microseconds)
                assertEquals(it.second[1].nanoseconds, nanoseconds)
            }
        }
    }

    @ExperimentalTime
    @Test
    fun `conversion to Kotlin Duration`() {
        assertEquals(0.kotlinNanoseconds, 0.nanoseconds.toKotlinDuration())
        assertEquals(1.kotlinNanoseconds, 1.nanoseconds.toKotlinDuration())
        assertEquals((-1).kotlinNanoseconds, (-1L).nanoseconds.toKotlinDuration())
        assertEquals(Long.MIN_VALUE.kotlinNanoseconds, Long.MIN_VALUE.nanoseconds.toKotlinDuration())
    }

    @ExperimentalTime
    @Test
    fun `conversion from Kotlin Duration`() {
        assertEquals(0L.nanoseconds, 0.kotlinNanoseconds.toIslandNanoseconds())
        assertEquals(1L.nanoseconds, 1.kotlinNanoseconds.toIslandNanoseconds())
        assertEquals((-1L).nanoseconds, (-1L).kotlinNanoseconds.toIslandNanoseconds())
        assertEquals(Long.MIN_VALUE.nanoseconds, Long.MIN_VALUE.kotlinNanoseconds.toIslandNanoseconds())
    }
}