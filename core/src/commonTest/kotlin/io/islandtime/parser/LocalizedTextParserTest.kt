package io.islandtime.parser

import io.islandtime.base.DateTimeField
import io.islandtime.format.TextStyle
import io.islandtime.locale.toLocale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalizedTextParserTest {
    @Suppress("PropertyName")
    val en_US = "en-US".toLocale()

    @Test
    fun `parses localized months`() {
        val parsers = listOf(
            dateTimeParser {
                localizedText(DateTimeField.MONTH_OF_YEAR, setOf(TextStyle.FULL, TextStyle.SHORT))
            },
            dateTimeParser {
                +' '
                localizedText(DateTimeField.MONTH_OF_YEAR, setOf(TextStyle.FULL, TextStyle.SHORT))
                +'a'
            }
        )

        listOf(
            "January" to 1L,
            "Jan" to 1L,
            "Feb" to 2L,
            "July" to 7L,
            "December" to 12L
        ).forEach {
            parsers.forEachIndexed { index, parser ->
                val textToParse = if (index == 1) " ${it.first}a" else it.first
                val result = parser.parse(textToParse, DateTimeParserSettings(locale = en_US))
                assertEquals(1, result.fields.size)
                assertEquals(it.second, result.fields[DateTimeField.MONTH_OF_YEAR])
            }
        }
    }

    @Test
    fun `parses case-insensitive localized months`() {
        val parser = dateTimeParser {
            localizedText(DateTimeField.MONTH_OF_YEAR, setOf(TextStyle.FULL))
        }

        val result = parser.parse("januarY", DateTimeParserSettings(locale = en_US, isCaseSensitive = false))
        assertEquals(1, result.fields.size)
        assertEquals(1L, result.fields[DateTimeField.MONTH_OF_YEAR])
    }

    @Test
    fun `reports an error when no match is found`() {
        val parser = dateTimeParser {
            +'a'
            localizedText(DateTimeField.MONTH_OF_YEAR, setOf(TextStyle.SHORT))
        }

        val exception = assertFailsWith<DateTimeParseException> {
            parser.parse("ajan", DateTimeParserSettings(locale = en_US))
        }
        assertEquals(1, exception.errorIndex)
        assertEquals("ajan", exception.parsedString)
    }

    @Test
    fun `reports an error when the end of the text is hit before parsing`() {
        val parser = dateTimeParser {
            +' '
            localizedText(DateTimeField.MONTH_OF_YEAR, setOf(TextStyle.NARROW))
        }

        val exception = assertFailsWith<DateTimeParseException> {
            parser.parse(" ", DateTimeParserSettings(locale = en_US))
        }
        assertEquals(1, exception.errorIndex)
        assertEquals(" ", exception.parsedString)
    }

    @Test
    fun `reports an error when no parsable text is available`() {
        val parser = dateTimeParser {
            +' '
            localizedText(DateTimeField.YEAR, setOf(TextStyle.FULL))
        }

        val exception = assertFailsWith<DateTimeParseException> {
            parser.parse(" 2010", DateTimeParserSettings(locale = en_US))
        }
        assertEquals(1, exception.errorIndex)
        assertEquals(" 2010", exception.parsedString)
    }
}