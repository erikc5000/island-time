package io.islandtime.parser

import io.islandtime.base.DateTimeField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
        val dowParser = dateTimeParser {
            wholeNumber(1) {
                associateWith(DateTimeField.DAY_OF_WEEK)
            }
        }

        assertEquals(9, dowParser.parse("9").fields[DateTimeField.DAY_OF_WEEK])

        val dateParser = dateTimeParser {
            wholeNumber(3) {
                associateWith(DateTimeField.DAY_OF_YEAR)
            }
            wholeNumber(4) {
                associateWith(DateTimeField.YEAR)
            }
        }

        val result = dateParser.parse("0653000")
        assertEquals(65, result.fields[DateTimeField.DAY_OF_YEAR])
        assertEquals(3000, result.fields[DateTimeField.YEAR])
    }

    @Test
    fun `fixed length parser doesn't enforce sign style by default`() {
        val dowParser1 = dateTimeParser {
            wholeNumber(1) {
                associateWith(DateTimeField.DAY_OF_WEEK)
            }
        }

        assertEquals(9, dowParser1.parse("+9").fields[DateTimeField.DAY_OF_WEEK])

        val dowParser2 = dateTimeParser {
            wholeNumber(1) {
                associateWith(DateTimeField.DAY_OF_WEEK)
            }
        }
        assertEquals(-9, dowParser2.parse("-9").fields[DateTimeField.DAY_OF_WEEK])
    }

    @Test
    fun `enforces NEVER sign style`() {
        listOf(
            dateTimeParser {
                wholeNumber(1) {
                    associateWith(DateTimeField.DAY_OF_WEEK)
                    enforceSignStyle(SignStyle.NEVER)
                }
            },
            dateTimeParser {
                wholeNumber {
                    associateWith(DateTimeField.DAY_OF_WEEK)
                    enforceSignStyle(SignStyle.NEVER)
                }
            }
        ).forEach { parser ->
            assertFailsWith<DateTimeParseException> { parser.parse("+9") }
            assertFailsWith<DateTimeParseException> { parser.parse("-9") }
        }
    }

    @Test
    fun `enforces NEGATIVE_ONLY sign style`() {
        listOf(
            dateTimeParser {
                wholeNumber(1) {
                    associateWith(DateTimeField.DAY_OF_WEEK)
                    enforceSignStyle(SignStyle.NEGATIVE_ONLY)
                }
            },
            dateTimeParser {
                wholeNumber {
                    associateWith(DateTimeField.DAY_OF_WEEK)
                    enforceSignStyle(SignStyle.NEGATIVE_ONLY)
                }
            }
        ).forEach { parser ->
            assertFailsWith<DateTimeParseException> { parser.parse("+9") }
            assertEquals(9, parser.parse("9").fields[DateTimeField.DAY_OF_WEEK])
            assertEquals(-9, parser.parse("-9").fields[DateTimeField.DAY_OF_WEEK])
        }
    }

    @Test
    fun `enforces ALWAYS sign style`() {
        listOf(
            dateTimeParser {
                wholeNumber(1) {
                    associateWith(DateTimeField.DAY_OF_WEEK)
                    enforceSignStyle(SignStyle.ALWAYS)
                }
            },
            dateTimeParser {
                wholeNumber {
                    associateWith(DateTimeField.DAY_OF_WEEK)
                    enforceSignStyle(SignStyle.ALWAYS)
                }
            }
        ).forEach { parser ->
            assertEquals(9, parser.parse("+9").fields[DateTimeField.DAY_OF_WEEK])
            assertFailsWith<DateTimeParseException> { parser.parse("9") }
            assertEquals(-9, parser.parse("-9").fields[DateTimeField.DAY_OF_WEEK])
        }
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
            wholeNumber(1..3) {
                associateWith(DateTimeField.DAY_OF_YEAR)
            }
        }

        assertEquals(4, parser.parse("4").fields[DateTimeField.DAY_OF_YEAR])
        assertEquals(400, parser.parse("400").fields[DateTimeField.DAY_OF_YEAR])

        val parserWithLiteral = dateTimeParser {
            wholeNumber(1..3) {
                associateWith(DateTimeField.DAY_OF_YEAR)
            }
            +'M'
        }

        assertEquals(-2, parserWithLiteral.parse("-2M").fields[DateTimeField.DAY_OF_YEAR])
        assertEquals(-20, parserWithLiteral.parse("-20M").fields[DateTimeField.DAY_OF_YEAR])
    }

    @Test
    fun `variable length parser throws an exception if minimum number of digits can't be parsed`() {
        val parser = dateTimeParser {
            wholeNumber(2..3) {
                associateWith(DateTimeField.DAY_OF_YEAR)
            }
            +"DOY"
        }

        assertFailsWith<DateTimeParseException> { parser.parse("2DOY") }
        assertFailsWith<DateTimeParseException> { parser.parse("-2DOY") }
    }

    @Test
    fun `variable length parser throws an exception if consecutive digits exceed the maximum`() {
        val parser = dateTimeParser {
            wholeNumber(2..3) {
                associateWith(DateTimeField.DAY_OF_YEAR)
            }
            wholeNumber(1..4) {
                associateWith(DateTimeField.YEAR)
            }
        }

        assertFailsWith<DateTimeParseException> { parser.parse("2001975") }
        assertFailsWith<DateTimeParseException> { parser.parse("-1934-034") }
    }

    @Test
    fun `reports an error when there are no characters to parse`() {
        listOf(
            dateTimeParser {
                +' '
                wholeNumber(2)
            }, dateTimeParser {
                +' '
                wholeNumber()
            }
        ).forEach { parser ->
            val exception = assertFailsWith<DateTimeParseException> { parser.parse(" ") }
            assertEquals(1, exception.errorIndex)
            assertEquals(" ", exception.parsedString)
        }
    }

    @Test
    fun `throws an exception on overflow`() {
        val parsers = listOf(
            dateTimeParser {
                +' '
                wholeNumber(19) {
                    onParsed { fields[DateTimeField.DURATION_OF_HOURS] = it }
                }
            }, dateTimeParser {
                +' '
                wholeNumber {
                    onParsed { fields[DateTimeField.DURATION_OF_HOURS] = it }
                }
            }
        )

        listOf(
            " 9223372036854775808",
            " -9223372036854775809",
            " +9300000000000000000"
        ).forEach { text ->
            parsers.forEach { parser ->
                val exception = assertFailsWith<DateTimeParseException> { parser.parse(text) }
                assertEquals(1, exception.errorIndex)
                assertEquals(text, exception.parsedString)
                assertTrue { exception.cause is ArithmeticException }
            }
        }
    }
}
