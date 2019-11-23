package io.islandtime.internal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.todo

class MathTest {
    @Test
    fun `plusExact infix throws an exception when adding Longs results in overflow`() {
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE plusExact 1 }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE plusExact Long.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE plusExact (-1) }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE plusExact Long.MIN_VALUE }
    }

    @Test
    fun `plusExact infix adds Longs when the result fits in a Long`() {
        assertEquals(Long.MAX_VALUE, Long.MAX_VALUE - 1 plusExact 1)
        assertEquals(Long.MIN_VALUE, Long.MIN_VALUE + 1 plusExact (-1))
    }
    
    @Test
    fun `plusExact infix throws an exception when adding Ints results in overflow`() {
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE plusExact 1 }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE plusExact Int.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE plusExact (-1) }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE plusExact Int.MIN_VALUE }
    }
    
    @Test
    fun `plusExact infix adds Ints when the result fits in an Int`() {
        assertEquals(Int.MAX_VALUE, Int.MAX_VALUE - 1 plusExact 1)
        assertEquals(Int.MIN_VALUE, Int.MIN_VALUE + 1 plusExact (-1))
    }

    @Test
    fun `minusExact infix throws an exception when subtracting Longs results in overflow`() {
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE minusExact 1 }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE minusExact Long.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE minusExact (-1) }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE minusExact Long.MIN_VALUE }
    }

    @Test
    fun `minusExact infix subtracts Longs when the result fits in a Long`() {
        assertEquals(Long.MIN_VALUE, Long.MIN_VALUE + 1 minusExact 1)
        assertEquals(Long.MAX_VALUE, Long.MAX_VALUE - 1 minusExact (-1))
    }

    @Test
    fun `minusExact infix throws an exception when subtracting Ints results in overflow`() {
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE minusExact 1 }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE minusExact Int.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE minusExact (-1) }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE minusExact Int.MIN_VALUE }
    }

    @Test
    fun `minusExact infix subtracts Ints when the result fits in a Int`() {
        assertEquals(Int.MIN_VALUE, Int.MIN_VALUE + 1 minusExact 1)
        assertEquals(Int.MAX_VALUE, Int.MAX_VALUE - 1 minusExact (-1))
    }

    @Test
    fun `timesExact infix throws an exception when multiplying Longs results in overflow`() {
        assertFailsWith<ArithmeticException> { (Long.MAX_VALUE / 2 + 1) timesExact  2 }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE timesExact Long.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE timesExact Long.MIN_VALUE }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE timesExact Long.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE timesExact (-1) }
    }

    @Test
    fun `timesExact infix multiplies Longs when the result fits in a Long`() {
        assertEquals(Long.MAX_VALUE, Long.MAX_VALUE timesExact 1)
        assertEquals(Long.MAX_VALUE - 1, Long.MAX_VALUE / 2 timesExact 2)
        assertEquals(Long.MIN_VALUE, Long.MIN_VALUE timesExact 1)
        assertEquals(Long.MIN_VALUE, Long.MIN_VALUE / 2 timesExact 2)
    }

    @Test
    fun `timesExact infix throws an exception when multiplying Ints results in overflow`() {
        assertFailsWith<ArithmeticException> { (Int.MAX_VALUE / 2 + 1) timesExact  2 }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE timesExact Int.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE timesExact Int.MIN_VALUE }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE timesExact Int.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Int.MIN_VALUE timesExact (-1) }
    }

    @Test
    fun `timesExact infix multiplies Ints when the result fits in a Int`() {
        assertEquals(Int.MAX_VALUE, Int.MAX_VALUE timesExact 1)
        assertEquals(Int.MAX_VALUE - 1, Int.MAX_VALUE / 2 timesExact 2)
        assertEquals(Int.MIN_VALUE, Int.MIN_VALUE timesExact 1)
        assertEquals(Int.MIN_VALUE, Int.MIN_VALUE / 2 timesExact 2)
    }

    @Test
    fun `Long_toIntExact() throws an exception when the conversion overflows`() {
        assertFailsWith<ArithmeticException> { (Int.MAX_VALUE + 1L).toIntExact() }
        assertFailsWith<ArithmeticException> { (Int.MIN_VALUE - 1L).toIntExact() }
    }

    @Test
    fun `Long_toIntExact() converts a Long to an Int when it fits without overflow`() {
        assertEquals(Int.MAX_VALUE, (Int.MAX_VALUE.toLong()).toIntExact())
        assertEquals(Int.MIN_VALUE, (Int.MIN_VALUE.toLong()).toIntExact())
    }
}