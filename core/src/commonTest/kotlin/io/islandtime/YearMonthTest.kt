package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.ranges.DateRange
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class YearMonthTest : AbstractIslandTimeTest() {
    @Test
    fun `throws an exception when initialized with an invalid year`() {
        assertFailsWith<DateTimeException> { YearMonth(1_000_000_000, Month.JANUARY) }
        assertFailsWith<DateTimeException> { YearMonth(-1_000_000_000, Month.DECEMBER) }
        assertFailsWith<DateTimeException> { YearMonth(1_000_000_000, 1) }
        assertFailsWith<DateTimeException> { YearMonth(-1_000_000_000, 12) }
    }

    @Test
    fun `throws an exception when initialized with an invalid month`() {
        assertFailsWith<DateTimeException> { YearMonth(2000, 0) }
        assertFailsWith<DateTimeException> { YearMonth(2000, 13) }
    }

    @Test
    fun `properties return expected values`() {
        val yearMonth1 = YearMonth(1970, Month.JANUARY)
        assertEquals(1970, yearMonth1.year)
        assertEquals(Month.JANUARY, yearMonth1.month)
        assertFalse { yearMonth1.isInLeapYear }
        assertEquals(IntRange(1, 31), yearMonth1.dayRange)
        assertEquals(
            DateRange(Date(1970, Month.JANUARY, 1), Date(1970, Month.JANUARY, 31)),
            yearMonth1.dateRange
        )
        assertEquals(31.days, yearMonth1.lengthOfMonth)
        assertEquals(365.days, yearMonth1.lengthOfYear)
        assertEquals(31, yearMonth1.lastDay)
        assertEquals(1, yearMonth1.firstDayOfYear)
        assertEquals(31, yearMonth1.lastDayOfYear)
        assertEquals(Date(1970, Month.JANUARY, 1), yearMonth1.startDate)
        assertEquals(Date(1970, Month.JANUARY, 31), yearMonth1.endDate)

        val yearMonth2 = YearMonth(2000, Month.JUNE)
        assertEquals(2000, yearMonth2.year)
        assertEquals(Month.JUNE, yearMonth2.month)
        assertTrue { yearMonth2.isInLeapYear }
        assertEquals(IntRange(1, 30), yearMonth2.dayRange)
        assertEquals(
            DateRange(Date(2000, Month.JUNE, 1), Date(2000, Month.JUNE, 30)),
            yearMonth2.dateRange
        )
        assertEquals(30.days, yearMonth2.lengthOfMonth)
        assertEquals(366.days, yearMonth2.lengthOfYear)
        assertEquals(30, yearMonth2.lastDay)
        assertEquals(153, yearMonth2.firstDayOfYear)
        assertEquals(182, yearMonth2.lastDayOfYear)
        assertEquals(Date(2000, Month.JUNE, 1), yearMonth2.startDate)
        assertEquals(Date(2000, Month.JUNE, 30), yearMonth2.endDate)
    }

    @Test
    fun `can be compared to other YearMonths`() {
        assertTrue { YearMonth(1969, Month.DECEMBER) < YearMonth(1970, Month.JANUARY) }
        assertTrue { YearMonth(1970, Month.FEBRUARY) > YearMonth(1970, Month.JANUARY) }
        assertTrue { YearMonth(2000, Month.JUNE) > YearMonth(1920, Month.JULY) }
    }

    @Test
    fun `equality is based on year and month`() {
        assertTrue { YearMonth(2000, Month.FEBRUARY) == YearMonth(2000, Month.FEBRUARY) }
        assertFalse { YearMonth.MIN == YearMonth.MAX }
    }

    @Test
    fun `copy() can be used to replace the year or month component`() {
        assertEquals(
            YearMonth(1999, Month.DECEMBER),
            YearMonth(2000, Month.DECEMBER).copy(year = 1999)
        )

        assertEquals(
            YearMonth(2000, Month.NOVEMBER),
            YearMonth(2000, Month.DECEMBER).copy(month = Month.NOVEMBER)
        )

        assertEquals(
            YearMonth(2000, Month.NOVEMBER),
            YearMonth(2000, Month.DECEMBER).copy(monthNumber = 11)
        )
    }

    @Test
    fun `copy() throws an exception if the new year is invalid`() {
        assertFailsWith<DateTimeException> {
            YearMonth(2010, Month.DECEMBER).copy(year = -1_000_000_000)
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2010, Month.DECEMBER).copy(year = 1_000_000_000)
        }
    }

    @Test
    fun `adding zero months doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2010, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 0.months
        )

        assertEquals(
            YearMonth(2010, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 0L.months
        )
    }

    @Test
    fun `adds positive months`() {
        assertEquals(
            YearMonth(2011, Month.MAY),
            YearMonth(2010, Month.APRIL) + 13.months
        )

        assertEquals(
            YearMonth(2010, Month.MAY),
            YearMonth(2010, Month.APRIL) + 1L.months
        )
    }

    @Test
    fun `adds negative months`() {
        assertEquals(
            YearMonth(2009, Month.MARCH),
            YearMonth(2010, Month.APRIL) + (-13).months
        )

        assertEquals(
            YearMonth(2010, Month.MARCH),
            YearMonth(2010, Month.APRIL) + (-1L).months
        )
    }

    @Test
    fun `throws an exception when adding months puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MAX + 1.months },
            { YearMonth.MIN + (-1).months },
            { YearMonth(999_992_000, Month.JANUARY) + 8000.years.inMonths },
            { YearMonth(-999_998_000, Month.DECEMBER) + (-2000).years.inMonths },
            { YearMonth(999_992_000, Month.DECEMBER) + (Int.MAX_VALUE + 1L).months },
            { YearMonth(9999, Month.DECEMBER) + Long.MAX_VALUE.months }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `subtracting zero months doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2012, Month.APRIL),
            YearMonth(2012, Month.APRIL) - 0.months
        )
    }

    @Test
    fun `subtracts positive months`() {
        assertEquals(
            YearMonth(2009, Month.MARCH),
            YearMonth(2010, Month.APRIL) - 13.months
        )

        assertEquals(
            YearMonth(2010, Month.MARCH),
            YearMonth(2010, Month.APRIL) - 1L.months
        )
    }

    @Test
    fun `subtracts negative months`() {
        assertEquals(
            YearMonth(2011, Month.MAY),
            YearMonth(2010, Month.APRIL) - (-13).months
        )

        assertEquals(
            YearMonth(2010, Month.MAY),
            YearMonth(2010, Month.APRIL) - (-1L).months
        )
    }

    @Test
    fun `throws an exception when subtracting months puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MIN - 1.months },
            { YearMonth.MAX - (-1).months },
            { YearMonth(-999_998_000, Month.JANUARY) - 2000.years.inMonths },
            { YearMonth(999_992_000, Month.DECEMBER) - (-8000).years.inMonths },
            { YearMonth(-999_000_000, Month.DECEMBER) - (Int.MAX_VALUE + 1L).months },
            { YearMonth(1, Month.DECEMBER) - Long.MAX_VALUE.months },
            { YearMonth(9999, Month.DECEMBER) - Long.MIN_VALUE.months }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `adding zero years doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2010, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 0.years
        )
    }

    @Test
    fun `adds positive years`() {
        assertEquals(
            YearMonth(2011, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 1.years
        )

        assertEquals(
            YearMonth(2035, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 25L.years
        )
    }

    @Test
    fun `adds negative years`() {
        assertEquals(
            YearMonth(2009, Month.APRIL),
            YearMonth(2010, Month.APRIL) + (-1).years
        )

        assertEquals(
            YearMonth(1995, Month.MARCH),
            YearMonth(2010, Month.MARCH) + (-15L).years
        )
    }

    @Test
    fun `throws an exception when adding years puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MAX + 1.years },
            { YearMonth.MIN + (-1).years },
            { YearMonth(999_992_000, Month.JANUARY) + 8000.years },
            { YearMonth(-999_998_000, Month.DECEMBER) + (-2000).years },
            { YearMonth(999_992_000, Month.DECEMBER) + (Int.MAX_VALUE + 1L).years },
            { YearMonth(9999, Month.DECEMBER) + Long.MAX_VALUE.years }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `subtracting zero years doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2012, Month.APRIL),
            YearMonth(2012, Month.APRIL) - 0.years
        )
    }

    @Test
    fun `subtracts positive years`() {
        assertEquals(
            YearMonth(2009, Month.APRIL),
            YearMonth(2010, Month.APRIL) - 1.years
        )

        assertEquals(
            YearMonth(1910, Month.APRIL),
            YearMonth(2010, Month.APRIL) - 100L.years
        )
    }

    @Test
    fun `subtracts negative years`() {
        assertEquals(
            YearMonth(2011, Month.APRIL),
            YearMonth(2010, Month.APRIL) - (-1).years
        )

        assertEquals(
            YearMonth(2012, Month.APRIL),
            YearMonth(2010, Month.APRIL) - (-2L).years
        )
    }

    @Test
    fun `throws an exception when subtracting years puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MIN - 1.years },
            { YearMonth.MAX - (-1).years },
            { YearMonth(-999_998_000, Month.JANUARY) - 2000.years },
            { YearMonth(999_992_000, Month.DECEMBER) - (-8000).years },
            { YearMonth(1, Month.DECEMBER) - (Int.MAX_VALUE + 1L).years },
            { YearMonth(1, Month.DECEMBER) - Long.MAX_VALUE.years },
            { YearMonth(9999, Month.DECEMBER) - Long.MIN_VALUE.years }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `adding zero decades doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2010, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 0.decades
        )
    }

    @Test
    fun `adds positive decades`() {
        assertEquals(
            YearMonth(2020, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 1.decades
        )
    }

    @Test
    fun `adds negative decades`() {
        assertEquals(
            YearMonth(2000, Month.APRIL),
            YearMonth(2010, Month.APRIL) + (-1).decades
        )
    }

    @Test
    fun `throws an exception when adding decades puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MAX + 1.decades },
            { YearMonth.MIN + (-1).decades }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when adding decades causes overflow`() {
        assertFailsWith<ArithmeticException> { YearMonth(9999, Month.DECEMBER) + Long.MAX_VALUE.decades }
    }

    @Test
    fun `subtracting zero decades doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2012, Month.APRIL),
            YearMonth(2012, Month.APRIL) - 0.decades
        )
    }

    @Test
    fun `subtracts positive decades`() {
        assertEquals(
            YearMonth(2000, Month.APRIL),
            YearMonth(2010, Month.APRIL) - 1.decades
        )
    }

    @Test
    fun `subtracts negative decades`() {
        assertEquals(
            YearMonth(2020, Month.APRIL),
            YearMonth(2010, Month.APRIL) - (-1).decades
        )
    }

    @Test
    fun `throws an exception when subtracting decades puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MIN - 1.decades },
            { YearMonth.MAX - (-1).decades }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when subtracting decades causes overflow`() {
        assertFailsWith<ArithmeticException> { YearMonth(9999, Month.DECEMBER) - Long.MAX_VALUE.decades }
    }

    @Test
    fun `adding zero centuries doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2010, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 0.centuries
        )
    }

    @Test
    fun `adds positive centuries`() {
        assertEquals(
            YearMonth(2110, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 1.centuries
        )
    }

    @Test
    fun `adds negative centuries`() {
        assertEquals(
            YearMonth(1910, Month.APRIL),
            YearMonth(2010, Month.APRIL) + (-1).centuries
        )
    }

    @Test
    fun `throws an exception when adding centuries puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MAX + 1.centuries },
            { YearMonth.MIN + (-1).centuries }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when adding centuries causes overflow`() {
        assertFailsWith<ArithmeticException> { YearMonth(9999, Month.DECEMBER) + Long.MAX_VALUE.centuries }
    }

    @Test
    fun `subtracting zero centuries doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2012, Month.APRIL),
            YearMonth(2012, Month.APRIL) - 0.centuries
        )
    }

    @Test
    fun `subtracts positive centuries`() {
        assertEquals(
            YearMonth(1910, Month.APRIL),
            YearMonth(2010, Month.APRIL) - 1.centuries
        )
    }

    @Test
    fun `subtracts negative centuries`() {
        assertEquals(
            YearMonth(2110, Month.APRIL),
            YearMonth(2010, Month.APRIL) - (-1).centuries
        )
    }

    @Test
    fun `throws an exception when subtracting centuries puts the YearMonth out of range`() {
        listOf(
            { YearMonth.MIN - 1.centuries },
            { YearMonth.MAX - (-1).centuries }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when subtracting centuries causes overflow`() {
        assertFailsWith<ArithmeticException> { YearMonth(9999, Month.DECEMBER) - Long.MAX_VALUE.centuries }
    }

    @Test
    fun `contains Date`() {
        val yearMonth = YearMonth(2019, Month.JANUARY)

        assertTrue { Date(2019, Month.JANUARY, 1) in yearMonth }
        assertTrue { Date(2019, Month.JANUARY, 31) in yearMonth }
        assertFalse { Date(2019, Month.FEBRUARY, 1) in yearMonth }
        assertFalse { Date(2020, Month.JANUARY, 1) in yearMonth }
        assertFalse { Date(2018, Month.DECEMBER, 31) in yearMonth }
    }

    @Test
    fun `toString() returns an ISO-8601 year month representation`() {
        listOf(
            YearMonth(2000, Month.FEBRUARY) to "2000-02",
            YearMonth(9999, Month.DECEMBER) to "9999-12",
            YearMonth(1, Month.JANUARY) to "0001-01",
            YearMonth(0, Month.JANUARY) to "0000-01",
            YearMonth(-1, Month.JANUARY) to "-0001-01",
            YearMonth(-9999, Month.JANUARY) to "-9999-01",
            YearMonth(10_000, Month.JANUARY) to "+10000-01",
            YearMonth(-10_000, Month.DECEMBER) to "-10000-12",
            YearMonth(999_999_999, Month.DECEMBER) to "+999999999-12",
            YearMonth(-999_999_999, Month.JANUARY) to "-999999999-01"
        ).forEach { (yearMonth, string) ->
            assertEquals(string, yearMonth.toString())
        }
    }

    @Test
    fun `String_toYearMonth() throws an exception when parsing an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toYearMonth() }
    }

    @Test
    fun `String_toYearMonth() throws an exception when parsing an invalid ISO-8601 extended string`() {
        listOf(
            "2012-05-01",
            "201205",
            " 2012-05",
            "2012-05-",
            "12-05",
            "Y10000-05",
            "Y-10000-05"
        ).forEach {
            assertFailsWith<DateTimeParseException> { it.toYearMonth() }
        }
    }

    @Test
    fun `String_toYearMonth() throws an exception when the year is out of range`() {
        listOf("+1000000000-01", "-1000000000-12").forEach {
            assertFailsWith<DateTimeException> { it.toYearMonth() }
        }
    }

    @Test
    fun `String_toYearMonth() throws an exception when the month is out of range`() {
        listOf("0001-13", "0001-00").forEach {
            assertFailsWith<DateTimeException> { it.toYearMonth() }
        }
    }

    @Test
    fun `String_toYearMonth() parses ISO-8601 year months by default`() {
        listOf(
            "2012-05" to YearMonth(2012, Month.MAY),
            "9999-12" to YearMonth(9999, Month.DECEMBER),
            "0001-01" to YearMonth(1, Month.JANUARY),
            "0000-01" to YearMonth(0, Month.JANUARY),
            "+0000-01" to YearMonth(0, Month.JANUARY),
            "-0000-01" to YearMonth(0, Month.JANUARY),
            "-0001-01" to YearMonth(-1, Month.JANUARY),
            "+10000-01" to YearMonth(10_000, Month.JANUARY),
            "-10000-01" to YearMonth(-10_000, Month.JANUARY),
            "+${Year.MAX_VALUE}-12" to YearMonth(Year.MAX_VALUE, Month.DECEMBER),
            "${Year.MIN_VALUE}-01" to YearMonth(Year.MIN_VALUE, Month.JANUARY)
        ).forEach { (string, expected) ->
            assertEquals(expected, string.toYearMonth())
        }
    }
}
