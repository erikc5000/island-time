package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.base.TimeProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FractionFormatterTest : AbstractIslandTimeTest() {
    @Suppress("EmptyRange")
    @Test
    fun `throws an exception when min length is greater than max`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { fraction(TimeProperty.NanosecondOfSecond, 2..1) }
        }
    }

    @Test
    fun `throws an exception when min length is less than 1`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { fraction(TimeProperty.NanosecondOfSecond, -1) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { fraction(TimeProperty.NanosecondOfSecond, 0..9) }
        }
    }

    @Test
    fun `throws an exception when max length is greater than 9`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { fraction(TimeProperty.NanosecondOfSecond, 10) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { fraction(TimeProperty.NanosecondOfSecond, 1..10) }
        }
    }

    @Test
    fun `throws an exception when formatting a temporal with an out-of-range value`() {
        val formatter = temporalFormatter { fraction(TimeProperty.MillisecondOfSecond, scale = 3) }

        assertFailsWith<DateTimeException> {
            formatter.format(temporalWith(TimeProperty.MillisecondOfSecond to -1_000L))
        }

        assertFailsWith<DateTimeException> {
            formatter.format(temporalWith(TimeProperty.MillisecondOfSecond to 1_000L))
        }
    }

    @Test
    fun `formats fractions with variable length`() {
        val formatter = temporalFormatter {
            fraction(TimeProperty.NanosecondOfSecond, 1..9)
        }

        listOf(
            temporalWith(TimeProperty.NanosecondOfSecond to 123456789) to "123456789",
            temporalWith(TimeProperty.NanosecondOfSecond to 123456780) to "12345678",
            temporalWith(TimeProperty.NanosecondOfSecond to 120000000) to "12",
            temporalWith(TimeProperty.NanosecondOfSecond to 100000000) to "1",
            temporalWith(TimeProperty.NanosecondOfSecond to 23456789) to "023456789",
            temporalWith(TimeProperty.NanosecondOfSecond to 23456780) to "02345678",
            temporalWith(TimeProperty.NanosecondOfSecond to 0) to "0"
        ).forEach { (temporal, result) ->
            assertEquals(result, formatter.format(temporal))
        }
    }
}