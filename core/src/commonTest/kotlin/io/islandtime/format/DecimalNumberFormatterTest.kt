package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.base.TimeProperty
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DecimalNumberFormatterTest {
    @Suppress("EmptyRange")
    @Test
    fun `throws an exception when min whole length is greater than max`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    wholeLength = 2..1
                )
            }
        }
    }

    @Test
    fun `throws an exception when min whole length is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    wholeLength = -1..19
                )
            }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    wholeLength = 20..20
                )
            }
        }
    }

    @Test
    fun `throws an exception when max whole length is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    wholeLength = 0..0
                )
            }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    wholeLength = 1..20
                )
            }
        }
    }

    @Suppress("EmptyRange")
    @Test
    fun `throws an exception when min fraction length is greater than max`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    fractionLength = 2..1
                )
            }
        }
    }

    @Test
    fun `throws an exception when min fraction length is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    fractionLength = -1..9
                )
            }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    fractionLength = 10..10
                )
            }
        }
    }

    @Test
    fun `throws an exception when max fraction length is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    fractionLength = 0..10
                )
            }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    fractionLength = -1..-1
                )
            }
        }
    }

    @Test
    fun `throws an exception when the fraction scale is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    fractionScale = 0
                )
            }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter {
                decimalNumber(
                    TimeProperty.SecondOfMinute,
                    TimeProperty.NanosecondOfSecond,
                    fractionScale = 10
                )
            }
        }
    }

    @Test
    fun `throws an exception when formatting a temporal with an out-of-range fraction value`() {
        val formatter = temporalFormatter {
            decimalNumber(
                TimeProperty.SecondOfMinute,
                TimeProperty.MillisecondOfSecond,
                fractionScale = 3
            )
        }

        assertFailsWith<DateTimeException> {
            formatter.format(
                temporalWith(
                    TimeProperty.SecondOfMinute to 0L,
                    TimeProperty.MillisecondOfSecond to -1_000L
                )
            )
        }

        assertFailsWith<DateTimeException> {
            formatter.format(
                temporalWith(
                    TimeProperty.SecondOfMinute to 0L,
                    TimeProperty.MillisecondOfSecond to 1_000L
                )
            )
        }
    }

    @Test
    fun `formats whole numbers with variable length`() {
        val formatter = temporalFormatter {
            decimalNumber(TimeProperty.SecondOfMinute, TimeProperty.NanosecondOfSecond)
        }

        listOf(
            temporalWith(
                TimeProperty.SecondOfMinute to 1,
                TimeProperty.NanosecondOfSecond to 0
            ) to "1",
            temporalWith(
                TimeProperty.SecondOfMinute to -1,
                TimeProperty.NanosecondOfSecond to 0
            ) to "-1",
            temporalWith(
                TimeProperty.SecondOfMinute to 10,
                TimeProperty.NanosecondOfSecond to 0
            ) to "10",
            temporalWith(
                TimeProperty.SecondOfMinute to -10,
                TimeProperty.NanosecondOfSecond to 0
            ) to "-10",
            temporalWith(
                TimeProperty.SecondOfMinute to Long.MAX_VALUE,
                TimeProperty.NanosecondOfSecond to 0
            ) to "${Long.MAX_VALUE}",
            temporalWith(
                TimeProperty.SecondOfMinute to Long.MIN_VALUE,
                TimeProperty.NanosecondOfSecond to 0
            ) to "${Long.MIN_VALUE}"
        ).forEach { (temporal, result) ->
            assertEquals(result, formatter.format(temporal))
        }
    }

    @Test
    fun `pads whole numbers as needed to satisfy the minimum length`() {
        val formatter = temporalFormatter {
            decimalNumber(
                TimeProperty.SecondOfMinute,
                TimeProperty.NanosecondOfSecond,
                wholeLength = 2..2
            )
        }

        listOf(
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 100_000_000
            ) to "00.1",
            temporalWith(
                TimeProperty.SecondOfMinute to 1,
                TimeProperty.NanosecondOfSecond to 100_000_000
            ) to "01.1",
            temporalWith(
                TimeProperty.SecondOfMinute to -1,
                TimeProperty.NanosecondOfSecond to -100_000_000
            ) to "-01.1",
            temporalWith(
                TimeProperty.SecondOfMinute to 10,
                TimeProperty.NanosecondOfSecond to 100_000_000
            ) to "10.1",
            temporalWith(
                TimeProperty.SecondOfMinute to -10,
                TimeProperty.NanosecondOfSecond to 100_000_000
            ) to "-10.1"
        ).forEach { (temporal, result) ->
            assertEquals(result, formatter.format(temporal))
        }
    }

    @Test
    fun `throws an exception when the whole number's max length is exceeded`() {
        val formatter = temporalFormatter {
            decimalNumber(
                TimeProperty.SecondOfMinute,
                TimeProperty.NanosecondOfSecond,
                wholeLength = 2..2
            )
        }

        assertFailsWith<DateTimeException> {
            formatter.format(
                temporalWith(
                    TimeProperty.SecondOfMinute to 100,
                    TimeProperty.NanosecondOfSecond to 0
                )
            )
        }

        assertFailsWith<DateTimeException> {
            formatter.format(
                temporalWith(
                    TimeProperty.SecondOfMinute to -100,
                    TimeProperty.NanosecondOfSecond to 0
                )
            )
        }
    }

    @Test
    fun `formats fractions with variable length`() {
        val formatter = temporalFormatter {
            decimalNumber(TimeProperty.SecondOfMinute, TimeProperty.NanosecondOfSecond)
        }

        listOf(
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 123456780
            ) to "0.12345678",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 120000000
            ) to "0.12",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 100000000
            ) to "0.1",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 23456789
            ) to "0.023456789",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 23456780
            ) to "0.02345678",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 0
            ) to "0"
        ).forEach { (temporal, result) ->
            assertEquals(result, formatter.format(temporal))
        }
    }

    @Test
    fun `adds zero to the end of fractions to satisfy min length`() {
        val formatter = temporalFormatter {
            decimalNumber(
                TimeProperty.SecondOfMinute,
                TimeProperty.NanosecondOfSecond,
                fractionLength = 3..3
            )
        }

        listOf(
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 123456780
            ) to "0.123",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 120000000
            ) to "0.120",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 100000000
            ) to "0.100",
            temporalWith(
                TimeProperty.SecondOfMinute to 1,
                TimeProperty.NanosecondOfSecond to 23456789
            ) to "1.023",
            temporalWith(
                TimeProperty.SecondOfMinute to -1,
                TimeProperty.NanosecondOfSecond to 20000000
            ) to "-1.020",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 0
            ) to "0.000"
        ).forEach { (temporal, result) ->
            assertEquals(result, formatter.format(temporal))
        }
    }

    @Test
    fun `min whole length can be zero`() {
        val formatter = temporalFormatter {
            decimalNumber(
                TimeProperty.SecondOfMinute,
                TimeProperty.NanosecondOfSecond,
                wholeLength = 0..19
            )
        }

        listOf(
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 900_000_000
            ) to ".9",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to -900_000_000
            ) to "-.9",
            temporalWith(
                TimeProperty.SecondOfMinute to 0,
                TimeProperty.NanosecondOfSecond to 0
            ) to "0"
        ).forEach { (temporal, result) ->
            assertEquals(result, formatter.format(temporal))
        }
    }

    @Test
    fun `max fraction length can be zero`() {
        val formatter = temporalFormatter {
            decimalNumber(
                TimeProperty.SecondOfMinute,
                TimeProperty.NanosecondOfSecond,
                fractionLength = 0..0
            )
        }

        assertEquals(
            "1",
            formatter.format(
                temporalWith(
                    TimeProperty.SecondOfMinute to 1,
                    TimeProperty.NanosecondOfSecond to 900_000_000
                )
            )
        )
    }
}