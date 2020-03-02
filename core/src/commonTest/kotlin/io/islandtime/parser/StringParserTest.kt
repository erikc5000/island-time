package io.islandtime.parser

import io.islandtime.base.TimeZoneProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StringParserTest {
    @Test
    fun `parser with no length restrictions`() {
        val expectedCharMap = mapOf(
            0 to 'T',
            1 to 'e',
            2 to 's',
            3 to 't'
        )

        val parser = dateTimeParser {
            +' '
            string {
                onEachChar { char, index ->
                    assertEquals(char, expectedCharMap[index]!!)
                    StringParseAction.ACCEPT_AND_CONTINUE
                }
                onParsed { this[TimeZoneProperty.Id] = it }
            }
        }

        val result = parser.parse(" Test")
        assertTrue { result.size == 1 }
        assertEquals("Test", result[TimeZoneProperty.Id])
    }

    @Test
    fun `parsing can be stopped with REJECT_AND_STOP`() {
        val parser = dateTimeParser {
            +' '
            string {
                onEachChar { char, index ->
                    assertEquals('.', char)
                    assertEquals(0, index)
                    StringParseAction.REJECT_AND_STOP
                }
                onParsed { this[TimeZoneProperty.Id] = it }
            }
            +'.'
        }

        val result = parser.parse(" .")
        assertTrue { result.size == 1 }
        assertTrue { result[TimeZoneProperty.Id]!!.isEmpty() }
    }

    @Test
    fun `reports an error when there are no characters to parse`() {
        val parser = dateTimeParser {
            +' '
            string {
                onEachChar { _, _ ->  StringParseAction.ACCEPT_AND_CONTINUE }
            }
        }

        val exception = assertFailsWith<DateTimeParseException> { parser.parse(" ") }
        assertEquals(1, exception.errorIndex)
        assertEquals(" ", exception.parsedString)
    }

    @Test
    fun `reports an error when the min length isn't satisfied`() {
        val parser = dateTimeParser {
            +' '
            string(2..10) {
                onEachChar { char, _ ->
                    if (char in 'A'..'Z') {
                        StringParseAction.ACCEPT_AND_CONTINUE
                    } else {
                        StringParseAction.REJECT_AND_STOP
                    }
                }
                onParsed { this[TimeZoneProperty.Id] = it }
            }
            +'.'
        }

        val exception = assertFailsWith<DateTimeParseException> { parser.parse(" T.") }
        assertEquals(1, exception.errorIndex)
        assertEquals(" T.", exception.parsedString)
    }

    @Test
    fun `reports an error when the max length isn't satisfied`() {
        val parser = dateTimeParser {
            +' '
            string(1..4) {
                onEachChar { char, _ ->
                    if (char in 'A'..'Z') {
                        StringParseAction.ACCEPT_AND_CONTINUE
                    } else {
                        StringParseAction.REJECT_AND_STOP
                    }
                }
                onParsed { this[TimeZoneProperty.Id] = it }
            }
            +'.'
        }

        val exception = assertFailsWith<DateTimeParseException> { parser.parse(" TESTS.") }
        assertEquals(1, exception.errorIndex)
        assertEquals(" TESTS.", exception.parsedString)
    }
}