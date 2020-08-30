package io.islandtime.parser

import io.islandtime.properties.DateProperty
import io.islandtime.format.TextStyle
import io.islandtime.locale.toLocale
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalizedTextParserTest : AbstractIslandTimeTest() {
    @Suppress("PropertyName")
    val en_US = "en-US".toLocale()

    @Test
    fun `parses localized months`() {
        val parsers = listOf(
            TemporalParser {
                localizedText(DateProperty.MonthOfYear, setOf(TextStyle.FULL, TextStyle.SHORT))
            },
            TemporalParser {
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
                val result = parser.parse(textToParse, TemporalParser.Settings(locale = en_US))
                assertEquals(1, result.size)
                assertEquals(it.second, result[DateProperty.MonthOfYear])
            }
        }
    }

    @Test
    fun `parses case-insensitive localized months`() {
        val parser = TemporalParser {
            localizedText(DateProperty.MonthOfYear, setOf(TextStyle.FULL))
        }

        val result = parser.parse(
            "januarY",
            TemporalParser.Settings(locale = en_US, isCaseSensitive = false)
        )

        assertEquals(1, result.size)
        assertEquals(1L, result[DateProperty.MonthOfYear])
    }

    @Test
    fun `reports an error when no match is found`() {
        val parser = TemporalParser {
            +'a'
            localizedText(DateProperty.MonthOfYear, setOf(TextStyle.SHORT))
        }

        val exception = assertFailsWith<TemporalParseException> {
            parser.parse("ajan", TemporalParser.Settings(locale = en_US))
        }
        assertEquals(1, exception.errorIndex)
        assertEquals("ajan", exception.parsedString)
    }

    @Test
    fun `reports an error when the end of the text is hit before parsing`() {
        val parser = TemporalParser {
            +' '
            localizedText(DateProperty.MonthOfYear, setOf(TextStyle.NARROW))
        }

        val exception = assertFailsWith<TemporalParseException> {
            parser.parse(" ", TemporalParser.Settings(locale = en_US))
        }
        assertEquals(1, exception.errorIndex)
        assertEquals(" ", exception.parsedString)
    }

    @Test
    fun `reports an error when no parsable text is available`() {
        val parser = TemporalParser {
            +' '
            localizedText(DateProperty.Year, setOf(TextStyle.FULL))
        }

        val exception = assertFailsWith<TemporalParseException> {
            parser.parse(" 2010", TemporalParser.Settings(locale = en_US))
        }
        assertEquals(1, exception.errorIndex)
        assertEquals(" 2010", exception.parsedString)
    }
}
