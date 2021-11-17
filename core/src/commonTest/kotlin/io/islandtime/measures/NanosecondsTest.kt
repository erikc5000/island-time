package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.nanoseconds as kotlinNanoseconds

class NanosecondsTest {
    @Test
    fun `Nanoseconds can be compared to other Nanoseconds`() {
        assertTrue { 0.nanoseconds < 1.nanoseconds }
        assertTrue { 0.nanoseconds == 0.nanoseconds }
        assertTrue { 5.nanoseconds > (-1).nanoseconds }
    }

    @Test
    fun `absoluteValue returns the same value for 0 or positive values`() {
        listOf(0, 1, Long.MAX_VALUE).forEach {
            assertEquals(it.nanoseconds, it.nanoseconds.absoluteValue)
        }
    }

    @Test
    fun `absoluteValue returns a positive value for negatives values`() {
        assertEquals(1.nanoseconds, (-1).nanoseconds.absoluteValue)
    }

    @Test
    fun `absoluteValue throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.nanoseconds.absoluteValue }
    }

    @Test
    fun `unary minus negates the value`() {
        listOf(
            0L to 0L,
            1L to -1L,
            -1L to 1L,
            Long.MAX_VALUE to -Long.MAX_VALUE
        ).forEach {
            assertEquals(it.second.nanoseconds, -it.first.nanoseconds)
        }
    }

    @Test
    fun `unary minus throws an exception when value in MIN_VALUE`() {
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
        assertEquals(3.nanoseconds, 9.nanoseconds / 3L)
        assertEquals((-3).nanoseconds, 9.nanoseconds / -3)
        assertEquals((-3).nanoseconds, 9.nanoseconds / -3L)
    }

    @Test
    fun `dividing by -1 throws an exception when value is MIN_VALUE`() {
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.nanoseconds / -1 }
    }

    @Test
    fun `rem operator works`() {
        assertEquals(1.nanoseconds, 5.nanoseconds % 2)
        assertEquals(1.nanoseconds, 5.nanoseconds % 2L)
    }

    @Test
    fun `inWholeSeconds converts Nanoseconds to Seconds`() {
        listOf(
            0 to 0,
            999_999_999 to 0,
            -999_999_999 to 0,
            1_000_000_000 to 1,
            -1_000_000_000 to -1
        ).forEach {
            assertEquals(it.second.seconds, it.first.nanoseconds.inWholeSeconds)
        }
    }

    @Test
    fun `inWholeMicroseconds converts Nanoseconds to Microseconds`() {
        listOf(
            0 to 0,
            999 to 0,
            -999 to 0,
            1_000 to 1,
            -1_000 to -1
        ).forEach {
            assertEquals(it.second.microseconds, it.first.nanoseconds.inWholeMicroseconds)
        }
    }

    @Test
    fun `toLong() converts to Long`() {
        listOf(0, -1, 1, Long.MIN_VALUE, Long.MAX_VALUE).forEach {
            assertEquals(it, it.nanoseconds.toLong())
        }
    }

    @Test
    fun `toInt() throws an exception if overflow occurs during conversion`() {
        listOf(Int.MAX_VALUE + 1L, Int.MIN_VALUE - 1L).forEach {
            assertFailsWith<ArithmeticException> { it.nanoseconds.toInt() }
        }
    }

    @Test
    fun `toInt() converts to Int`() {
        listOf(0, -1, 1, Int.MAX_VALUE, Int.MIN_VALUE).forEach {
            assertEquals(it, it.nanoseconds.toInt())
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
            Int.MIN_VALUE to "-PT2.147483648S",
        ).forEach {
            assertEquals(it.second, it.first.nanoseconds.toString())
        }

        listOf(
            Long.MAX_VALUE to "PT9223372036.854775807S",
            Long.MIN_VALUE to "-PT9223372036.854775808S"
        ).forEach {
            assertEquals(it.second, it.first.nanoseconds.toString())
        }
    }

    @Test
    fun `toComponents() and toComponentValues() with days, hours, minutes, seconds, milliseconds, and microseconds`() {
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

            it.first.nanoseconds
                .toComponentValues { days, hours, minutes, seconds, milliseconds, microseconds, nanoseconds ->
                    assertEquals(it.second[0].toLong(), days)
                    assertEquals(it.second[1], hours)
                    assertEquals(it.second[2], minutes)
                    assertEquals(it.second[3], seconds)
                    assertEquals(it.second[4], milliseconds)
                    assertEquals(it.second[5], microseconds)
                    assertEquals(it.second[6], nanoseconds)
                }
        }
    }

    @Test
    fun `toComponents() and toComponentValues() with microseconds`() {
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

            it.first.nanoseconds.toComponentValues { microseconds, nanoseconds ->
                assertEquals(it.second[0].toLong(), microseconds)
                assertEquals(it.second[1], nanoseconds)
            }
        }
    }

    @Test
    fun `conversion to Kotlin Duration`() {
        assertEquals(0.kotlinNanoseconds, 0.nanoseconds.toKotlinDuration())
        assertEquals(1.kotlinNanoseconds, 1.nanoseconds.toKotlinDuration())
        assertEquals((-1).kotlinNanoseconds, (-1).nanoseconds.toKotlinDuration())
        assertEquals(Long.MIN_VALUE.kotlinNanoseconds, Long.MIN_VALUE.nanoseconds.toKotlinDuration())
    }

    @Test
    fun `conversion from Kotlin Duration`() {
        assertEquals(0.nanoseconds, 0.kotlinNanoseconds.toIslandNanoseconds())
        assertEquals(1.nanoseconds, 1.kotlinNanoseconds.toIslandNanoseconds())
        assertEquals((-1).nanoseconds, (-1).kotlinNanoseconds.toIslandNanoseconds())

        assertEquals(
            Long.MIN_VALUE.nanoseconds.inWholeMilliseconds.inNanoseconds,
            Long.MIN_VALUE.kotlinNanoseconds.toIslandNanoseconds()
        )
    }
}
