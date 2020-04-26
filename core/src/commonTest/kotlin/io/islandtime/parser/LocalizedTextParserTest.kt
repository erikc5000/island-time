package io.islandtime.parser

import io.islandtime.base.DateProperty
import io.islandtime.format.TextStyle
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalizedTextParserTest : AbstractIslandTimeTest() {
    @Suppress("PropertyName")
    val en_US = localeOf("en-US")

    @Test
    fun `parses localized months`() {
        val parsers = listOf(
            dateTimeParser {
                localizedText(DateProperty.MonthOfYear, setOf(TextStyle.FULL, TextStyle.SHORT))
            },
            dateTimeParser {
                +' '
                localizedText(DateProperty.MonthOfYear, setOf(TextStyle.FULL, TextStyle.SHORT))
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
                assertEquals(1, result.size)
                assertEquals(it.second, result[DateProperty.MonthOfYear])
            }
        }
    }

    @Test
    fun `parses case-insensitive localized months`() {
        val parser = dateTimeParser {
            localizedText(DateProperty.MonthOfYear, setOf(TextStyle.FULL))
        }

        val result = parser.parse(
            "januarY",
            DateTimeParserSettings(locale = en_US, isCaseSensitive = false)
        )

        assertEquals(1, result.size)
        assertEquals(1L, result[DateProperty.MonthOfYear])
    }

    @Test
    fun `reports an error when no match is found`() {
        val parser = dateTimeParser {
            +'a'
            localizedText(DateProperty.MonthOfYear, setOf(TextStyle.SHORT))
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
            localizedText(DateProperty.MonthOfYear, setOf(TextStyle.NARROW))
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
            localizedText(DateProperty.Year, setOf(TextStyle.FULL))
        }

        val exception = assertFailsWith<DateTimeParseException> {
            parser.parse(" 2010", DateTimeParserSettings(locale = en_US))
        }
        assertEquals(1, exception.errorIndex)
        assertEquals(" 2010", exception.parsedString)
    }
}