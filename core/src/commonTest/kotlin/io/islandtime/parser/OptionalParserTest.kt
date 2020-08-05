package io.islandtime.parser

import io.islandtime.base.DateTimeField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OptionalParserTest {
    @Test
    fun `optionally takes the child parser's result when successful`() {
        val parser = dateTimeParser {
            wholeNumber {
                associateWith(DateTimeField.MONTH_OF_YEAR)
            }
            optional {
                +'/'
                wholeNumber {
                    associateWith(DateTimeField.YEAR)
                }
            }
        }

        val result1 = parser.parse("13")
        assertEquals(1, result1.fields.size)
        assertEquals(13, result1.fields[DateTimeField.MONTH_OF_YEAR])

        val result2 = parser.parse("13/2012")
        assertEquals(2, result2.fields.size)
        assertEquals(13, result2.fields[DateTimeField.MONTH_OF_YEAR])
        assertEquals(2012, result2.fields[DateTimeField.YEAR])
    }

    @Test
    fun `marked as const when child parser is const`() {
        val parser = dateTimeParser {
            +' '
            optional {
                +' '
            }
            +'!'
        }

        assertTrue { parser.isConst }

        val result = parser.parse(" !")
        assertTrue { result.isEmpty() }
    }

    @Test
    fun `empty blocks are allowed`() {
        val result = dateTimeParser { optional {} }.parse("")
        assertTrue { result.isEmpty() }
    }
}
