package io.islandtime.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WholeNumberParserTest {
    @Test
    fun `fixed length parser throws an exception if length is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            dateTimeParser { wholeNumber(0) }
        }

        assertFailsWith<IllegalArgumentException> {
            dateTimeParser { wholeNumber(20) }
        }
    }

    @Test
    fun `fixed length parser parses exact number of digits`() {
        val dowParser = dateTimeParser { wholeNumber(1, DateTimeField.DAY_OF_WEEK) }
        assertEquals(9, dowParser.parse("9")[DateTimeField.DAY_OF_WEEK])

        val dateParser = dateTimeParser {
            wholeNumber(3, DateTimeField.DAY_OF_YEAR)
            wholeNumber(4, DateTimeField.YEAR)
        }

        val result = dateParser.parse("0653000")
        assertEquals(65, result[DateTimeField.DAY_OF_YEAR])
        assertEquals(3000, result[DateTimeField.YEAR])
    }

    @Test
    fun `fixed length parser doesn't enforce sign style by default`() {
        val dowParser1 = dateTimeParser { wholeNumber(1, DateTimeField.DAY_OF_WEEK) }
        assertEquals(9, dowParser1.parse("+9")[DateTimeField.DAY_OF_WEEK])

        val dowParser2 = dateTimeParser { wholeNumber(1, DateTimeField.DAY_OF_WEEK) }
        assertEquals(-9, dowParser2.parse("-9")[DateTimeField.DAY_OF_WEEK])
    }

    @Test
    fun `fixed length parser enforces NEVER sign style`() {
        val dowParser = dateTimeParser {
            wholeNumber(1, DateTimeField.DAY_OF_WEEK) {
                enforceSignStyle(SignStyle.NEVER)
            }
        }

        assertFailsWith<DateTimeParseException> { dowParser.parse("+9") }
        assertFailsWith<DateTimeParseException> { dowParser.parse("-9") }
    }

    @Test
    fun `fixed length parser enforces NEGATIVE_ONLY sign style`() {
        val dowParser = dateTimeParser {
            wholeNumber(1, DateTimeField.DAY_OF_WEEK) {
                enforceSignStyle(SignStyle.NEGATIVE_ONLY)
            }
        }

        assertFailsWith<DateTimeParseException> { dowParser.parse("+9") }
        assertEquals(9, dowParser.parse("9")[DateTimeField.DAY_OF_WEEK])
        assertEquals(-9, dowParser.parse("-9")[DateTimeField.DAY_OF_WEEK])
    }

    @Test
    fun `fixed length parser enforces ALWAYS sign style`() {
        val dowParser = dateTimeParser {
            wholeNumber(1, DateTimeField.DAY_OF_WEEK) {
                enforceSignStyle(SignStyle.ALWAYS)
            }
        }

        assertEquals(9, dowParser.parse("+9")[DateTimeField.DAY_OF_WEEK])
        assertFailsWith<DateTimeParseException> { dowParser.parse("9") }
        assertEquals(-9, dowParser.parse("-9")[DateTimeField.DAY_OF_WEEK])
    }

    @Test
    fun `variable length parser throws an exception if range is empty`() {
        assertFailsWith<IllegalArgumentException> {
            dateTimeParser { wholeNumber(IntRange.EMPTY) }
        }
    }

    @Test
    fun `variable length parser throws an exception if range is outside of 1-19`() {
        assertFailsWith<IllegalArgumentException> {
            dateTimeParser { wholeNumber(0..4) }
        }

        assertFailsWith<IllegalArgumentException> {
            dateTimeParser { wholeNumber(5..20) }
        }
    }

    @Test
    fun `variable length parser parses digits within length`() {
        val parser = dateTimeParser {
            wholeNumber(1..3, DateTimeField.DAY_OF_YEAR)
        }

        assertEquals(4, parser.parse("4")[DateTimeField.DAY_OF_YEAR])
        assertEquals(400, parser.parse("400")[DateTimeField.DAY_OF_YEAR])

        val parserWithLiteral = dateTimeParser {
            wholeNumber(1..3, DateTimeField.DAY_OF_YEAR)
            +'M'
        }

        assertEquals(-2, parserWithLiteral.parse("-2M")[DateTimeField.DAY_OF_YEAR])
        assertEquals(-20, parserWithLiteral.parse("-20M")[DateTimeField.DAY_OF_YEAR])
    }

    @Test
    fun `variable length parser throws an exception if minimum number of digits can't be parsed`() {
        val parser = dateTimeParser {
            wholeNumber(2..3, DateTimeField.DAY_OF_YEAR)
            +"DOY"
        }

        assertFailsWith<DateTimeParseException> { parser.parse("2DOY") }
        assertFailsWith<DateTimeParseException> { parser.parse("-2DOY") }
    }

    @Test
    fun `variable length parser throws an exception if consecutive digits exceed the maximum`() {
        val parser = dateTimeParser {
            wholeNumber(2..3, DateTimeField.DAY_OF_YEAR)
            wholeNumber(1..4, DateTimeField.YEAR)
        }

        assertFailsWith<DateTimeParseException> { parser.parse("2001975") }
        assertFailsWith<DateTimeParseException> { parser.parse("-1934-034") }
    }
}