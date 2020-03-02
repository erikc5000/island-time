package io.islandtime.parser

import io.islandtime.base.DateProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DateTimeParserTest {
    @Test
    fun `parses empty strings when the parser is empty`() {
        val result = dateTimeParser {}.parse("")
        assertTrue { result.isEmpty() }
    }

    @Test
    fun `throws an exception when there are unexpected characters after all parsers complete`() {
        val parser = dateTimeParser {
            wholeNumber(1) {
                associateWith(DateProperty.DayOfWeek)
            }
        }

        val exception = assertFailsWith<DateTimeParseException> { parser.parse("1 ") }
        assertEquals(1, exception.errorIndex)
        assertEquals("1 ", exception.parsedString)
    }
}