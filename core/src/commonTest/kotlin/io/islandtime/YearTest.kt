package io.islandtime

import io.islandtime.measures.centuries
import io.islandtime.measures.days
import io.islandtime.measures.decades
import io.islandtime.measures.years
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.dateTimeParser
import io.islandtime.parser.monthNumber
import io.islandtime.parser.year
import io.islandtime.ranges.DateRange
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class YearTest : AbstractIslandTimeTest() {
    private val invalidIntYears = listOf(
        Year.MIN_VALUE - 1,
        Year.MAX_VALUE + 1,
        Int.MAX_VALUE,
        Int.MIN_VALUE
    )

    private val invalidLongYears = listOf(
        Year.MIN_VALUE - 1L,
        Year.MAX_VALUE + 1L,
        Long.MAX_VALUE,
        Long.MIN_VALUE
    )

    @Test
    fun `Int constructor throws an exception if the value is invalid`() {
        invalidIntYears.forEach {
            assertFailsWith<DateTimeException> { Year(it) }
        }
    }

    @Test
    fun `Long constructor throws an exception if the value is invalid`() {
        invalidLongYears.forEach {
            assertFailsWith<DateTimeException> { Year(it) }
        }
    }

    @Test
    fun `Int construction succeeds with valid years`() {
        assertEquals(2000, Year(2000).value)
        assertEquals(Year.MIN_VALUE, Year.MIN.value)
        assertEquals(Year.MAX_VALUE, Year.MAX.value)
    }

    @Test
    fun `Long construction succeeds with valid years`() {
        assertEquals(2000, Year(2000L).value)
        assertEquals(Year.MIN_VALUE, Year(Year.MIN_VALUE.toLong()).value)
        assertEquals(Year.MAX_VALUE, Year(Year.MAX_VALUE.toLong()).value)
    }

    @Test
    fun `isLeap property returns false for common years`() {
        assertFalse { Year(2001).isLeap }
        assertFalse { Year(1900).isLeap }
        assertFalse { Year(1965).isLeap }
    }

    @Test
    fun `isLeap property returns true for leap years`() {
        assertTrue { Year(2000).isLeap }
        assertTrue { Year(1964).isLeap }
    }

    @Test
    fun `length property returns days in year`() {
        assertEquals(366.days, Year(2000).length)
        assertEquals(365.days, Year(2001).length)
    }

    @Test
    fun `lastDay property returns the last day of the year`() {
        assertEquals(366, Year(2000).lastDay)
        assertEquals(365, Year(2001).lastDay)
    }

    @Test
    fun `dayRange property returns the range of days in the year`() {
        assertEquals(IntRange(1, 366), Year(2000).dayRange)
        assertEquals(IntRange(1, 365), Year(2001).dayRange)
    }

    @Test
    fun `dateRange property returns the range of dates in the year`() {
        assertEquals(
            DateRange(Date(2000, Month.JANUARY, 1), Date(2000, Month.DECEMBER, 31)),
            Year(2000).dateRange
        )
    }

    @Test
    fun `startDate property returns the first date of the year`() {
        assertEquals(Date(2000, Month.JANUARY, 1), Year(2000).startDate)
    }

    @Test
    fun `endDate property returns the last date of the year`() {
        assertEquals(Date(2000, Month.DECEMBER, 31), Year(2000).endDate)
    }

    @Test
    fun `adding or subtracting zero years has no effect`() {
        assertEquals(Year(2009), Year(2009) + 0.years)
        assertEquals(Year(2009), Year(2009) - 0.years)
    }

    @Test
    fun `add positive years`() {
        assertEquals(Year(1970), Year(1969) + 1.years)
        assertEquals(Year(2009), Year(2000) + 9.years)
        assertEquals(Year(2009), Year(2000) + 9L.years)
        assertEquals(Year(9999), Year(1) + 9998.years)
    }

    @Test
    fun `add negative years`() {
        assertEquals(Year(2000), Year(2010) + (-10).years)
        assertEquals(Year(1), Year(2) + (-1L).years)
        assertEquals(Year(1000), Year(9999) + (-8999).years)
    }

    @Test
    fun `throws an exception when adding years puts the year outside the supported range`() {
        listOf(
            { Year.MAX + 1.years },
            { Year.MAX + Int.MAX_VALUE.years },
            { Year.MAX + Long.MAX_VALUE.years },
            { Year.MIN + (-1).years },
            { Year.MIN + Int.MIN_VALUE.years },
            { Year.MIN + Long.MIN_VALUE.years }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `subtract positive years`() {
        assertEquals(Year(2000), Year(2010) - 10.years)
        assertEquals(Year(1), Year(2) - 1L.years)
        assertEquals(Year(1000), Year(9999) - 8999.years)
    }

    @Test
    fun `subtract negative years`() {
        assertEquals(Year(1970), Year(1969) - (-1).years)
        assertEquals(Year(2009), Year(2000) - (-9).years)
        assertEquals(Year(2009), Year(2000) - (-9L).years)
        assertEquals(Year(9999), Year(1) - (-9998).years)
    }

    @Test
    fun `throws an exception when subtracting years puts the year outside the supported range`() {
        listOf(
            { Year.MAX - (-1).years },
            { Year.MAX - Int.MIN_VALUE.years },
            { Year.MAX - Long.MIN_VALUE.years },
            { Year.MIN - 1.years },
            { Year.MIN - Int.MAX_VALUE.years },
            { Year.MIN - Long.MAX_VALUE.years }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `adding or subtracting zero decades has no effect`() {
        assertEquals(Year(2009), Year(2009) + 0.decades)
        assertEquals(Year(2009), Year(2009) - 0.decades)
    }

    @Test
    fun `add positive decades`() {
        assertEquals(Year(1979), Year(1969) + 1.decades)
    }

    @Test
    fun `add negative decades`() {
        assertEquals(Year(-8), Year(2) + (-1).decades)
    }

    @Test
    fun `throws an exception when adding decades puts the year outside the supported range`() {
        listOf(
            { Year.MAX + 1.decades },
            { Year.MIN + (-1).decades },
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when adding decades causes overflow`() {
        listOf(
            { Year.MAX + Long.MAX_VALUE.decades },
            { Year.MIN + Long.MIN_VALUE.decades }
        ).forEach {
            assertFailsWith<ArithmeticException> { it() }
        }
    }

    @Test
    fun `subtract positive decades`() {
        assertEquals(Year(2000), Year(2010) - 1.decades)
    }

    @Test
    fun `subtract negative decades`() {
        assertEquals(Year(1979), Year(1969) - (-1).decades)
    }

    @Test
    fun `throws an exception when subtracting decades puts the year outside the supported range`() {
        listOf(
            { Year.MAX - (-1).decades },
            { Year.MIN - 1.decades },
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when subtracting decades causes overflow`() {
        listOf(
            { Year.MAX - Long.MIN_VALUE.decades },
            { Year.MIN - Long.MAX_VALUE.decades }
        ).forEach {
            assertFailsWith<ArithmeticException> { it() }
        }
    }

    @Test
    fun `adding or subtracting zero centuries has no effect`() {
        assertEquals(Year(2009), Year(2009) + 0.centuries)
        assertEquals(Year(2009), Year(2009) - 0.centuries)
    }

    @Test
    fun `add positive centuries`() {
        assertEquals(Year(2069), Year(1969) + 1.centuries)
    }

    @Test
    fun `add negative centuries`() {
        assertEquals(Year(-98), Year(2) + (-1).centuries)
    }

    @Test
    fun `throws an exception when adding centuries puts the year outside the supported range`() {
        listOf(
            { Year.MAX + 1.centuries },
            { Year.MIN + (-1).centuries },
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when adding centuries causes overflow`() {
        listOf(
            { Year.MAX + Long.MAX_VALUE.centuries },
            { Year.MIN + Long.MIN_VALUE.centuries }
        ).forEach {
            assertFailsWith<ArithmeticException> { it() }
        }
    }

    @Test
    fun `subtract positive centuries`() {
        assertEquals(Year(1910), Year(2010) - 1.centuries)
    }

    @Test
    fun `subtract negative centuries`() {
        assertEquals(Year(2069), Year(1969) - (-1).centuries)
    }

    @Test
    fun `throws an exception when subtracting centuries puts the year outside the supported range`() {
        listOf(
            { Year.MAX - (-1).centuries },
            { Year.MIN - 1.centuries },
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when subtracting centuries causes overflow`() {
        listOf(
            { Year.MAX - Long.MIN_VALUE.centuries },
            { Year.MIN - Long.MAX_VALUE.centuries }
        ).forEach {
            assertFailsWith<ArithmeticException> { it() }
        }
    }

    @Test
    fun `contains YearMonth`() {
        assertTrue { YearMonth(2019, Month.JANUARY) in Year(2019) }
        assertTrue { YearMonth(2019, Month.DECEMBER) in Year(2019) }
        assertFalse { YearMonth(2020, Month.JANUARY) in Year(2019) }
        assertFalse { YearMonth(2018, Month.DECEMBER) in Year(2019) }
    }

    @Test
    fun `contains Date`() {
        assertTrue { Date(2019, Month.JANUARY, 1) in Year(2019) }
        assertTrue { Date(2019, Month.DECEMBER, 31) in Year(2019) }
        assertFalse { Date(2020, Month.JANUARY, 1) in Year(2019) }
        assertFalse { Date(2018, Month.DECEMBER, 31) in Year(2019) }
    }

    @Test
    fun `toString() returns the year with a minimum of 4 digits as required by ISO-8601`() {
        listOf(
            2008 to "2008",
            1 to "0001",
            0 to "0000",
            -1 to "-0001",
            9999 to "9999",
            -9999 to "-9999",
            10_000 to "+10000",
            -10_000 to "-10000",
            999_999_999 to "+999999999",
            -999_999_999 to "-999999999"
        ).forEach { (year, string) ->
            assertEquals(string, Year(year).toString())
        }
    }

    @Test
    fun `String_toYear() throws an exception when the string is empty`() {
        assertFailsWith<DateTimeParseException> { "".toYear() }
    }

    @Test
    fun `String_toYear() throws an exception when parsing an improperly formatted string`() {
        listOf(
            "1",
            "01",
            "001",
            " 2008",
            "2008-",
            "Y2008",
            "+01234567890",
            "+12345678901",
            "-01234567890",
            "-12345678901",
            "Y0123456789",
            "Y1234567890",
            "Y-0123456789",
            "Y-1234567890"
        ).forEach {
            assertFailsWith<DateTimeParseException> { it.toYear() }
        }
    }

    @Test
    fun `String_toYear() throws an exception when the year is outside the supported range`() {
        val customParser = dateTimeParser { year() }

        listOf(
            "${Year.MAX_VALUE + 1}",
            "${Year.MIN_VALUE - 1}",
            "${Long.MAX_VALUE}",
            "${Long.MIN_VALUE}"
        ).forEach {
            assertFailsWith<DateTimeException> { it.toYear(customParser) }
        }
    }

    @Test
    fun `String_toYear() converts an ISO-8601 year string to a Year`() {
        listOf(
            "2010" to 2010,
            "0001" to 1,
            "0000" to 0,
            "+0000" to 0,
            "-0000" to 0,
            "-0001" to -1,
            "-9999" to -9999,
            "+10000" to 10000,
            "Y10000" to 10000,
            "Y-10000" to -10000,
            "+999999999" to 999_999_999,
            "-999999999" to -999_999_999,
            "Y999999999" to 999_999_999,
            "Y-999999999" to -999_999_999
        ).forEach { (parsed, expected) ->
            assertEquals(Year(expected), parsed.toYear())
        }
    }

    @Test
    fun `String_toYear() throws an exception when the parser fails to supply the YEAR field`() {
        val customParser = dateTimeParser { monthNumber(2) }
        assertFailsWith<DateTimeParseException> { "12".toYear(customParser) }
    }
}
