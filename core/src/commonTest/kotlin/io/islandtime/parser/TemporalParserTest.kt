package io.islandtime.parser

import io.islandtime.parser.dsl.associateWith
import io.islandtime.parser.dsl.wholeNumber
import io.islandtime.properties.DateProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TemporalParserTest {
    @Test
    fun `parses empty strings when the parser is empty`() {
        val result = TemporalParser {}.parse("")
        assertTrue { result.isEmpty() }
    }

    @Test
    fun `throws an exception when there are unexpected characters after all parsers complete`() {
        val parser = TemporalParser {
            wholeNumber(length = 1) {
                associateWith(DateProperty.DayOfWeek)
            }
        }

        val exception = assertFailsWith<TemporalParseException> { parser.parse("1 ") }
        assertEquals(1, exception.errorIndex)
        assertEquals("1 ", exception.parsedString)
    }
}
