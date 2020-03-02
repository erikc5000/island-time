package io.islandtime.parser

import io.islandtime.base.DateProperty
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
                wholeNumber(1) { associateWith(DateProperty.DayOfWeek) }
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
                wholeNumber { associateWith(DateProperty.MonthOfYear) }
                +" is the month"
            }
            +" of the year"
        }

        val result = parser.parse("5 is the month of the year")

        assertEquals(1, result.size)
        assertEquals(5L, result[0][DateProperty.MonthOfYear])
    }

    @Test
    fun `results from a multiple groups are populated correctly`() {
        val parser = groupedDateTimeParser {
            group {
                wholeNumber { associateWith(DateProperty.MonthOfYear) }
                +'/'
                wholeNumber { associateWith(DateProperty.Year) }
            }
            +" - "
            group {
                wholeNumber { associateWith(DateProperty.MonthOfYear) }
                +'/'
                wholeNumber { associateWith(DateProperty.Year) }
            }
        }

        val result = parser.parse("12/2010 - 4/2011")

        assertEquals(2, result.size)

        assertEquals(12L, result[0][DateProperty.MonthOfYear])
        assertEquals(2010L, result[0][DateProperty.Year])

        assertEquals(4L, result[1][DateProperty.MonthOfYear])
        assertEquals(2011L, result[1][DateProperty.Year])
    }

    @Test
    fun `error position is reported correctly`() {
        val parser = groupedDateTimeParser {
            +"Month: "
            group {
                wholeNumber(2) { associateWith(DateProperty.MonthOfYear) }
            }
            +" DoW: "
            group {
                wholeNumber(1) { associateWith(DateProperty.DayOfWeek) }
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
                wholeNumber(2) { associateWith(DateProperty.MonthOfYear) }
            }
            +'/'
            group {
                wholeNumber(4) { associateWith(DateProperty.Year) }
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
            listOf(DateProperty.MonthOfYear to 2L),
            result[0].properties.toList()
        )

        assertEquals(
            listOf(DateProperty.Year to 2000L),
            result[1].properties.toList()
        )

        assertEquals(
            listOf(DateProperty.MonthOfYear to 3L),
            result[2].properties.toList()
        )

        assertEquals(
            listOf(DateProperty.Year to 2001L),
            result[3].properties.toList()
        )
    }

    @Test
    fun `anyOf() uses the the results of the first custom grouped parser to succeed`() {
        val parser = groupedDateTimeParser {
            anyOf({
                group {
                    wholeNumber(2) { associateWith(DateProperty.MonthOfYear) }
                }
                +'/'
                group {
                    wholeNumber(4) { associateWith(DateProperty.Year) }
                }
            }, {
                group {
                    wholeNumber { associateWith(DateProperty.MonthOfYear) }
                }
                +'-'
                group {
                    wholeNumber { associateWith(DateProperty.Year) }
                }
            })
            +" - "
            anyOf({
                group {
                    wholeNumber(2) { associateWith(DateProperty.MonthOfYear) }
                }
                +'/'
                group {
                    wholeNumber(4) { associateWith(DateProperty.Year) }
                }
            }, {
                group {
                    wholeNumber { associateWith(DateProperty.MonthOfYear) }
                }
                +'-'
                group {
                    wholeNumber { associateWith(DateProperty.Year) }
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
                listOf(DateProperty.MonthOfYear to 2L),
                result[0].properties.toList()
            )

            assertEquals(
                listOf(DateProperty.Year to 2000L),
                result[1].properties.toList()
            )

            assertEquals(
                listOf(DateProperty.MonthOfYear to 3L),
                result[2].properties.toList()
            )

            assertEquals(
                listOf(DateProperty.Year to 2001L),
                result[3].properties.toList()
            )
        }
    }
}