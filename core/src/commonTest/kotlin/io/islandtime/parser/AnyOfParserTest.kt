package io.islandtime.parser

import io.islandtime.base.DateProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AnyOfParserTest {
    @Test
    fun `requires at least 2 arguments`() {
        assertFailsWith<IllegalArgumentException> {
            dateTimeParser {
                anyOf({ +' ' })
            }
        }

        assertFailsWith<IllegalArgumentException> {
            val childParser = dateTimeParser {}

            dateTimeParser {
                anyOf(childParser)
            }
        }
    }

    @Test
    fun `succeeds when the first child parser succeeds`() {
        val parser = dateTimeParser {
            anyOf({
                wholeNumber {
                    associateWith(DateProperty.Year)
                }
            }, {
                wholeNumber(4) {
                    associateWith(DateProperty.MonthOfYear)
                }
            })
        }

        val result = parser.parse("2301")
        assertEquals(1, result.size)
        assertEquals(2301L, result[DateProperty.Year])
    }

    @Test
    fun `empty child parsers are allowed`() {
        val parser = dateTimeParser {
            anyOf({ +' ' }, {})
        }

        val result = parser.parse("")
        assertTrue { result.isEmpty() }
    }
}