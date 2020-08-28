package io.islandtime.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CaseSensitivityTest {
    @Test
    fun `blocks can be made case sensitive regardless of parser setting`() {
        val parser = TemporalParser {
            +'T'
            caseSensitive {
                +'T'
            }
            +'T'
        }

        val exception = assertFailsWith<TemporalParseException> {
            parser.parse("ttt", TemporalParser.Settings(isCaseSensitive = false))
        }
        assertEquals(1, exception.errorIndex)

        parser.parse("tTt", TemporalParser.Settings(isCaseSensitive = false))
    }

    @Test
    fun `blocks can be made case insensitive regardless of parser setting`() {
        val parser = TemporalParser {
            +'T'
            caseInsensitive {
                +'T'
            }
            +'T'
        }

        parser.parse("TTT")
        parser.parse("TtT")

        val exception = assertFailsWith<TemporalParseException> { parser.parse("Ttt") }
        assertEquals(2, exception.errorIndex)
    }

    @Test
    fun `nested case sensitivity blocks`() {
        val parser = TemporalParser {
            caseInsensitive {
                +"T"
                caseSensitive {
                    +"T"
                }
            }
        }

        parser.parse("tT")
        parser.parse("TT")

        val exception = assertFailsWith<TemporalParseException> { parser.parse("tt") }
        assertEquals(1, exception.errorIndex)
    }

    @Test
    fun `empty blocks are allowed`() {
        val parser = TemporalParser {
            caseSensitive {}
            caseInsensitive {}
        }

        val result = parser.parse("")
        assertTrue { result.isEmpty() }
    }
}
