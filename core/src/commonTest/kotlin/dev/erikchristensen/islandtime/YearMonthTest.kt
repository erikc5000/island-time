package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.date.DateRange
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseException
import kotlin.test.*

class YearMonthTest {
    @Test
    fun `isValid can be used to check if the YearMonth was initialized with an invalid value`() {
        assertFalse { YearMonth(0.months).isValid }
        assertFalse { YearMonth(100.centuries.asMonths()).isValid }
    }

    @Test
    fun `throws an exception when initialized with an invalid year`() {
        assertFailsWith<DateTimeException> { YearMonth(10000, Month.JANUARY) }
        assertFailsWith<DateTimeException> { YearMonth(0, Month.DECEMBER) }
        assertFailsWith<DateTimeException> { YearMonth(10000, 1) }
        assertFailsWith<DateTimeException> { YearMonth(0, 12) }
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
        assertTrue { yearMonth1.isValid }
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
        assertEquals(Date(1970, Month.JANUARY, 1), yearMonth1.firstDate)
        assertEquals(Date(1970, Month.JANUARY, 31), yearMonth1.lastDate)

        val yearMonth2 = YearMonth(2000, Month.JUNE)
        assertEquals(2000, yearMonth2.year)
        assertEquals(Month.JUNE, yearMonth2.month)
        assertTrue { yearMonth2.isValid }
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
        assertEquals(Date(2000, Month.JUNE, 1), yearMonth2.firstDate)
        assertEquals(Date(2000, Month.JUNE, 30), yearMonth2.lastDate)
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
            YearMonth(2010, Month.DECEMBER).copy(year = 0)
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2010, Month.DECEMBER).copy(year = 10_000)
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
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.JANUARY) + 8000.years.asMonths()
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.DECEMBER) + (-2000).years.asMonths()
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.DECEMBER) + (Int.MAX_VALUE + 1L).months
        }
        assertFailsWith<DateTimeException> {
            YearMonth(9999, Month.DECEMBER) + Long.MAX_VALUE.months
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
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.JANUARY) - 2000.years.asMonths()
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.DECEMBER) - (-8000).years.asMonths()
        }
        assertFailsWith<DateTimeException> {
            YearMonth(1, Month.DECEMBER) - (Int.MAX_VALUE + 1L).months
        }
        assertFailsWith<DateTimeException> {
            YearMonth(1, Month.DECEMBER) - Long.MAX_VALUE.months
        }
        assertFailsWith<DateTimeException> {
            YearMonth(9999, Month.DECEMBER) - Long.MIN_VALUE.months
        }
    }

    @Test
    fun `adding zero years doesn't change the YearMonth`() {
        assertEquals(
            YearMonth(2010, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 0.years
        )

        assertEquals(
            YearMonth(2010, Month.APRIL),
            YearMonth(2010, Month.APRIL) + 0L.years
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
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.JANUARY) + 8000.years
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.DECEMBER) + (-2000).years
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.DECEMBER) + (Int.MAX_VALUE + 1L).years
        }
        // TODO: Consider throwing exceptions on overflow
//        assertFailsWith<DateTimeException> {
//            YearMonth(9999, Month.DECEMBER) + Long.MAX_VALUE.years
//        }
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
            YearMonth(2010, Month.APRIL) - 1L.centuries
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
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.JANUARY) - 2000.years
        }
        assertFailsWith<DateTimeException> {
            YearMonth(2000, Month.DECEMBER) - (-8000).years
        }
        assertFailsWith<DateTimeException> {
            YearMonth(1, Month.DECEMBER) - (Int.MAX_VALUE + 1L).years
        }
        // TODO: Consider throwing exceptions on overflow
//        assertFailsWith<DateTimeException> {
//            YearMonth(1, Month.DECEMBER) - Long.MAX_VALUE.years
//        }
//        assertFailsWith<DateTimeException> {
//            YearMonth(9999, Month.DECEMBER) - Long.MIN_VALUE.years
//        }
    }

    @Test
    fun `toString() returns an ISO-8601 extended year month representation`() {
        assertEquals(
            "2000-02",
            YearMonth(2000, Month.FEBRUARY).toString()
        )

        assertEquals(
            "0001-01",
            YearMonth(1, Month.JANUARY).toString()
        )

        assertEquals(
            "9999-12",
            YearMonth(9999, Month.DECEMBER).toString()
        )
    }

    @Test
    fun `String_toYearMonth() throws an exception when parsing an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toYearMonth() }
    }

    @Test
    fun `String_toYearMonth() throws an exception when parsing an invalid ISO-8601 extended string`() {
        assertFailsWith<DateTimeParseException> { "2012-05-01".toYearMonth() }
        assertFailsWith<DateTimeParseException> { "201205".toYearMonth() }
        assertFailsWith<DateTimeParseException> { " 2012-05".toYearMonth() }
        assertFailsWith<DateTimeParseException> { "2012-05-".toYearMonth() }
        assertFailsWith<DateTimeParseException> { "12-05".toYearMonth() }
        assertFailsWith<DateTimeParseException> { "10000-01".toYearMonth() }
        assertFailsWith<DateTimeException> { "0000-01".toYearMonth() }
    }

    @Test
    fun `String_toYearMonth() throws an exception when the year is out of range`() {
        assertFailsWith<DateTimeException> { "0000-01".toYearMonth() }
    }

    @Test
    fun `String_toYearMonth() throws an exception when the month is out of range`() {
        assertFailsWith<DateTimeException> { "0001-13".toYearMonth() }
    }

    @Test
    fun `String_toYearMonth() parses ISO-8601 extended format year months by default`() {
        assertEquals(YearMonth(2012, Month.MAY), "2012-05".toYearMonth())
        assertEquals(YearMonth(1, Month.JANUARY), "0001-01".toYearMonth())
        assertEquals(YearMonth(9999, Month.DECEMBER), "9999-12".toYearMonth())
    }
}