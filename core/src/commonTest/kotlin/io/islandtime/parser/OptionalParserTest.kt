package io.islandtime.parser

import io.islandtime.parser.dsl.associateWith
import io.islandtime.properties.DateProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OptionalParserTest {
    @Test
    fun `optionally takes the child parser's result when successful`() {
        val parser = TemporalParser {
            wholeNumber {
                associateWith(DateProperty.MonthOfYear)
            }
            optional {
                +'/'
                wholeNumber {
                    associateWith(DateProperty.Year)
                }
            }
        }

        val result1 = parser.parse("13")
        assertEquals(1, result1.propertyCount)
        assertEquals(13, result1[DateProperty.MonthOfYear])

        val result2 = parser.parse("13/2012")
        assertEquals(2, result2.propertyCount)
        assertEquals(13, result2[DateProperty.MonthOfYear])
        assertEquals(2012, result2[DateProperty.Year])
    }

    @Test
    fun `marked as const when child parser is const`() {
        val parser = TemporalParser {
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
        val result = TemporalParser { optional {} }.parse("")
        assertTrue { result.isEmpty() }
    }
}
