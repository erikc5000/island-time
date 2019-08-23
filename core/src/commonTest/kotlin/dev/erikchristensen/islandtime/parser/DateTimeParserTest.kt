package dev.erikchristensen.islandtime.parser

import kotlin.test.Test
import kotlin.test.assertFailsWith

class DateTimeParserTest {
    @Test
    fun `throws an illegal state exception when parser is empty`() {
        assertFailsWith<IllegalStateException> { dateTimeParser { } }
    }

    @Test
    fun `throws an exception when there are unexpected characters after all parsers complete`() {
        val parser = dateTimeParser { wholeNumber(1, DateTimeField.DAY_OF_WEEK) }
        assertFailsWith<DateTimeParseException> { parser.parse("1 ") }
    }
}