package io.islandtime.parser

import io.islandtime.base.DateTimeField
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
                    associateWith(DateTimeField.YEAR)
                }
            }, {
                wholeNumber(4) {
                    associateWith(DateTimeField.MONTH_OF_YEAR)
                }
            })
        }

        val result = parser.parse("2301")
        assertEquals(1, result.fields.size)
        assertEquals(2301, result.fields[DateTimeField.YEAR])
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
