package io.islandtime.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CaseSensitivityTest {
    @Test
    fun `blocks can be made case sensitive regardless of parser setting`() {
        val parser = dateTimeParser {
            +'T'
            caseSensitive {
                +'T'
            }
            +'T'
        }

        val exception = assertFailsWith<DateTimeParseException> {
            parser.parse("ttt", DateTimeParserSettings(isCaseSensitive = false))
        }
        assertEquals(1, exception.errorIndex)

        parser.parse("tTt", DateTimeParserSettings(isCaseSensitive = false))
    }

    @Test
    fun `blocks can be made case insensitive regardless of parser setting`() {
        val parser = dateTimeParser {
            +'T'
            caseInsensitive {
                +'T'
            }
            +'T'
        }

        parser.parse("TTT")
        parser.parse("TtT")

        val exception = assertFailsWith<DateTimeParseException> { parser.parse("Ttt") }
        assertEquals(2, exception.errorIndex)
    }

    @Test
    fun `nested case sensitivity blocks`() {
        val parser = dateTimeParser {
            caseInsensitive {
                +"T"
                caseSensitive {
                    +"T"
                }
            }
        }

        parser.parse("tT")
        parser.parse("TT")

        val exception = assertFailsWith<DateTimeParseException> { parser.parse("tt") }
        assertEquals(1, exception.errorIndex)
    }
}