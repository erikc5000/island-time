package io.islandtime.parser

import io.islandtime.base.DateTimeField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GroupedDateTimeParserTest {
    @Test
    fun `parses empty strings when the parser is empty`() {
        assertTrue { groupedDateTimeParser { }.parse("").isEmpty() }
    }

    @Test
    fun `throws an exception when there are unexpected characters after all parsers complete`() {
        val parser = groupedDateTimeParser {
            group {
                wholeNumber(1) { associateWith(DateTimeField.DAY_OF_WEEK) }
            }
        }

        val exception = assertFailsWith<DateTimeParseException> { parser.parse("1 ") }
        assertEquals(1, exception.errorIndex)
        assertEquals("1 ", exception.parsedString)
    }

    @Test
    fun `number of results matches the number of groups defined, even when empty`() {
        val result1 = groupedDateTimeParser { group {} }.parse("")
        assertEquals(1, result1.size)
        assertTrue { result1[0].isEmpty() }

        val result2 = groupedDateTimeParser {
            group {}
            group {}
        }.parse("")

        assertEquals(2, result2.size)
        assertTrue { result2.all { it.isEmpty() } }
    }

    @Test
    fun `number of results matches the number of groups defined, ignoring literals`() {
        val result1 = groupedDateTimeParser {
            +' '
            group {}
        }.parse(" ")

        assertEquals(1, result1.size)
        assertTrue { result1[0].isEmpty() }

        val result2 = groupedDateTimeParser {
            group {}
            +' '
            group {}
        }.parse(" ")

        assertEquals(2, result2.size)
        assertTrue { result2.all { it.isEmpty() } }

        val result3 = groupedDateTimeParser {
            group {}
            group {}
            +' '
        }.parse(" ")

        assertEquals(2, result3.size)
        assertTrue { result3.all { it.isEmpty() } }
    }

    @Test
    fun `results from a single group are populated correctly`() {
        val parser = groupedDateTimeParser {
            group {
                wholeNumber { associateWith(DateTimeField.MONTH_OF_YEAR) }
                +" is the month"
            }
            +" of the year"
        }

        val result = parser.parse("5 is the month of the year")

        assertEquals(1, result.size)
        assertEquals(5L, result[0].fields[DateTimeField.MONTH_OF_YEAR])
    }

    @Test
    fun `results from a multiple groups are populated correctly`() {
        val parser = groupedDateTimeParser {
            group {
                wholeNumber { associateWith(DateTimeField.MONTH_OF_YEAR) }
                +'/'
                wholeNumber { associateWith(DateTimeField.YEAR) }
            }
            +" - "
            group {
                wholeNumber { associateWith(DateTimeField.MONTH_OF_YEAR) }
                +'/'
                wholeNumber { associateWith(DateTimeField.YEAR) }
            }
        }

        val result = parser.parse("12/2010 - 4/2011")

        assertEquals(2, result.size)

        assertEquals(
            listOf(
                DateTimeField.MONTH_OF_YEAR to 12L,
                DateTimeField.YEAR to 2010L
            ),
            result[0].fields.toList().sortedBy { it.second }
        )

        assertEquals(
            listOf(
                DateTimeField.MONTH_OF_YEAR to 4L,
                DateTimeField.YEAR to 2011L
            ),
            result[1].fields.toList().sortedBy { it.second }
        )
    }

    @Test
    fun `error position is reported correctly`() {
        val parser = groupedDateTimeParser {
            +"Month: "
            group {
                wholeNumber(2) { associateWith(DateTimeField.MONTH_OF_YEAR) }
            }
            +" DoW: "
            group {
                wholeNumber(1) { associateWith(DateTimeField.DAY_OF_WEEK) }
            }
        }

        val exception1 = assertFailsWith<DateTimeParseException> { parser.parse("Month: 1 DoW: 4") }
        assertEquals(8, exception1.errorIndex)

        val exception2 = assertFailsWith<DateTimeParseException> { parser.parse("Month: 10 DoW: A") }
        assertEquals(15, exception2.errorIndex)
    }

    @Test
    fun `anyOf() enables reuse of existing grouped parsers`() {
        val existingParser = groupedDateTimeParser {
            group {
                wholeNumber(2) { associateWith(DateTimeField.MONTH_OF_YEAR) }
            }
            +'/'
            group {
                wholeNumber(4) { associateWith(DateTimeField.YEAR) }
            }
        }

        val parser = groupedDateTimeParser {
            anyOf(existingParser)
            +" - "
            anyOf(existingParser)
        }

        val result = parser.parse("02/2000 - 03/2001")

        assertEquals(4, result.size)

        assertEquals(
            listOf(DateTimeField.MONTH_OF_YEAR to 2L),
            result[0].fields.toList()
        )

        assertEquals(
            listOf(DateTimeField.YEAR to 2000L),
            result[1].fields.toList()
        )

        assertEquals(
            listOf(DateTimeField.MONTH_OF_YEAR to 3L),
            result[2].fields.toList()
        )

        assertEquals(
            listOf(DateTimeField.YEAR to 2001L),
            result[3].fields.toList()
        )
    }

    @Test
    fun `anyOf() uses the the results of the first custom grouped parser to succeed`() {
        val parser = groupedDateTimeParser {
            anyOf({
                group {
                    wholeNumber(2) { associateWith(DateTimeField.MONTH_OF_YEAR) }
                }
                +'/'
                group {
                    wholeNumber(4) { associateWith(DateTimeField.YEAR) }
                }
            }, {
                group {
                    wholeNumber { associateWith(DateTimeField.MONTH_OF_YEAR) }
                }
                +'-'
                group {
                    wholeNumber { associateWith(DateTimeField.YEAR) }
                }
            })
            +" - "
            anyOf({
                group {
                    wholeNumber(2) { associateWith(DateTimeField.MONTH_OF_YEAR) }
                }
                +'/'
                group {
                    wholeNumber(4) { associateWith(DateTimeField.YEAR) }
                }
            }, {
                group {
                    wholeNumber { associateWith(DateTimeField.MONTH_OF_YEAR) }
                }
                +'-'
                group {
                    wholeNumber { associateWith(DateTimeField.YEAR) }
                }
            })
        }

        listOf(
            "02/2000 - 3-2001",
            "2-2000 - 03/2001",
            "02/2000 - 03/2001",
            "2-2000 - 3-2001"
        ).forEach { 
            val result = parser.parse(it)

            assertEquals(4, result.size)

            assertEquals(
                listOf(DateTimeField.MONTH_OF_YEAR to 2L),
                result[0].fields.toList()
            )

            assertEquals(
                listOf(DateTimeField.YEAR to 2000L),
                result[1].fields.toList()
            )

            assertEquals(
                listOf(DateTimeField.MONTH_OF_YEAR to 3L),
                result[2].fields.toList()
            )

            assertEquals(
                listOf(DateTimeField.YEAR to 2001L),
                result[3].fields.toList()
            )
        }
    }
}