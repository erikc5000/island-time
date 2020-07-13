package io.islandtime.parser

import io.islandtime.base.DurationProperty
import io.islandtime.base.TimeProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DecimalNumberParserTest {
    @Test
    fun `throws an exception if whole range is empty`() {
        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(wholeLength = IntRange.EMPTY) }
        }
    }

    @Test
    fun `throws an exception if fraction range is empty`() {
        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(fractionLength = IntRange.EMPTY) }
        }
    }

    @Test
    fun `throws an exception if whole range is outside of 0-19`() {
        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(wholeLength = -1..4) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(wholeLength = 5..20) }
        }
    }

    @Test
    fun `throws an exception if fraction range is outside of 0-9`() {
        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(fractionLength = -1..4) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(fractionLength = 5..10) }
        }
    }

    @Test
    fun `throws an exception if fraction scale is outside of 1-9`() {
        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(fractionScale = 0) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalParser { decimalNumber(fractionScale = 10) }
        }
    }

    @Test
    fun `parses decimal numbers into two components`() {
        val parser = temporalParser {
            decimalNumber {
                onParsed { whole, fraction ->
                    set(TimeProperty.SecondOfMinute, whole)
                    set(TimeProperty.NanosecondOfSecond, fraction)
                }
            }
            +' '
        }

        val result1 = parser.parse("0.1 ")
        assertEquals(0L, result1[TimeProperty.SecondOfMinute])
        assertEquals(100_000_000L, result1[TimeProperty.NanosecondOfSecond])

        val result2 = parser.parse("-5.000000001 ")
        assertEquals(-5L, result2[TimeProperty.SecondOfMinute])
        assertEquals(-1L, result2[TimeProperty.NanosecondOfSecond])

        val result3 = parser.parse("10 ")
        assertEquals(10L, result3[TimeProperty.SecondOfMinute])
        assertEquals(0L, result3[TimeProperty.NanosecondOfSecond])
    }

    @Test
    fun `allows decimal numbers with a zero length whole component`() {
        val parser = temporalParser {
            decimalNumber(0..19) {
                onParsed { whole, fraction ->
                    set(TimeProperty.SecondOfMinute, whole)
                    set(TimeProperty.NanosecondOfSecond, fraction)
                }
            }
        }

        val result1 = parser.parse(".1")
        assertEquals(0L, result1[TimeProperty.SecondOfMinute])
        assertEquals(100_000_000L, result1[TimeProperty.NanosecondOfSecond])

        val result2 = parser.parse("-.000000001")
        assertEquals(0L, result2[TimeProperty.SecondOfMinute])
        assertEquals(-1L, result2[TimeProperty.NanosecondOfSecond])
    }

    @Test
    fun `enforces whole length`() {
        val parser = temporalParser {
            decimalNumber(wholeLength = 2..3)
        }

        listOf(
            "4",
            "0.3",
            "4000.02",
            "-1.0004"
        ).forEach {
            assertFailsWith<TemporalParseException> { parser.parse(it) }
        }
    }

    @Test
    fun `enforces fraction length`() {
        val parser1 = temporalParser {
            decimalNumber(fractionLength = 1..3)
        }

        listOf(
            "45",
            "-34",
            "0.0004",
            "-1.0001",
            "100-",
            "0.",
            "0/0"
        ).forEach {
            assertFailsWith<TemporalParseException> { parser1.parse(it) }
        }

        val parser2 = temporalParser {
            decimalNumber(fractionLength = 2..4)
            +' '
        }

        listOf(
            "0.1 ",
            "0.12345 ",
            "0.1234567890 "
        ).forEach {
            assertFailsWith<TemporalParseException> { parser2.parse(it) }
        }

        val parser3 = temporalParser {
            decimalNumber(fractionLength = 0..0)
            +' '
        }

        val exception = assertFailsWith<TemporalParseException> { parser3.parse("0.1 ") }
        assertEquals(1, exception.errorIndex)
    }

    @Test
    fun `fractionScale controls the magnitude of the fractional part`() {
        val parser = temporalParser {
            decimalNumber(fractionScale = 3) {
                onParsed { _, fraction -> set(TimeProperty.MillisecondOfSecond, fraction) }
            }
        }

        assertEquals(300L, parser.parse("0.3")[TimeProperty.MillisecondOfSecond])
    }

    @Test
    fun `reports an error when there are no characters to parse`() {
        val parser = temporalParser {
            +' '
            decimalNumber()
        }

        val exception = assertFailsWith<TemporalParseException> { parser.parse(" ") }
        assertEquals(1, exception.errorIndex)
        assertEquals(" ", exception.parsedString)
    }

    @Test
    fun `reports an error if the whole and fractional parts are both absent`() {
        val parser1 = temporalParser {
            decimalNumber(0..19) {
                onParsed { whole, fraction ->
                    set(TimeProperty.SecondOfMinute, whole)
                    set(TimeProperty.NanosecondOfSecond, fraction)
                }
            }
        }

        listOf(
            "-",
            "+",
            "-.",
            "+.",
            "."
        ).forEach {
            val exception = assertFailsWith<TemporalParseException> { parser1.parse(it) }
            assertEquals(it.length, exception.errorIndex)
        }

        val parser2 = temporalParser {
            childParser(parser1)
            +' '
        }

        listOf(
            "- ",
            "+ ",
            "-. ",
            "+. ",
            ". "
        ).forEach {
            val exception = assertFailsWith<TemporalParseException> { parser2.parse(it) }
            assertEquals(it.length - 1, exception.errorIndex)
        }
    }

    @Test
    fun `reports an error when a trailing decimal separator is found`() {
        val parser = temporalParser { decimalNumber() }
        val exception = assertFailsWith<TemporalParseException> { parser.parse("-4.") }
        assertEquals(3, exception.errorIndex)
    }

    @Test
    fun `throws an exception on overflow`() {
        val parser = temporalParser {
            +' '
            decimalNumber {
                onParsed { whole, _ ->
                    set(DurationProperty.Hours, whole)
                }
            }
        }

        listOf(
            " 9223372036854775808",
            " -9223372036854775809",
            " +9300000000000000000"
        ).forEach {
            val exception = assertFailsWith<TemporalParseException> { parser.parse(it) }
            assertEquals(1, exception.errorIndex)
            assertEquals(it, exception.parsedString)
            assertTrue { exception.cause is ArithmeticException }
        }
    }
}