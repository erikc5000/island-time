package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DateTest : AbstractIslandTimeTest() {
    @Test
    fun `throws an exception when constructed with an invalid year`() {
        listOf(
            { Date(-1_000_000_000, Month.JANUARY, 1) },
            { Date(1_000_000_000, Month.DECEMBER, 31) },
            { Date(Int.MIN_VALUE, Month.JANUARY, 1) },
            { Date(Int.MAX_VALUE, Month.DECEMBER, 31) }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when constructed with an invalid day`() {
        assertFailsWith<DateTimeException> { Date(2000, Month.JANUARY, 32) }
        assertFailsWith<DateTimeException> { Date(2001, Month.FEBRUARY, 29) }
        assertFailsWith<DateTimeException> { Date(2001, Month.FEBRUARY, 0) }
    }

    @Test
    fun `throws an exception when creating from a day of year that's impossible`() {
        assertFailsWith<DateTimeException> { Date(2019, -1) }
        assertFailsWith<DateTimeException> { Date(2019, 0) }
        assertFailsWith<DateTimeException> { Date(2019, 367) }
        assertFailsWith<DateTimeException> { Date(2019, 366) }
    }

    @Test
    fun `dates can be constructed from year and day of year`() {
        assertEquals(Date(2019, Month.DECEMBER, 1), Date(2019, 335))
        assertEquals(Date(2020, Month.DECEMBER, 1), Date(2020, 336))
        assertEquals(Date(20200, Month.MARCH, 15), Date(20200, 74))
    }

    @Test
    fun `copy creates a new Date replacing any combination of values`() {
        assertEquals(
            Date(2017, Month.NOVEMBER, 19),
            Date(2018, Month.NOVEMBER, 19).copy(year = 2017)
        )

        assertEquals(
            Date(20200, Month.MARCH, 15),
            Date(2020, Month.MARCH, 15).copy(year = 20200)
        )

        assertEquals(
            Date(2018, Month.DECEMBER, 19),
            Date(2018, Month.NOVEMBER, 19).copy(month = Month.DECEMBER)
        )

        assertEquals(
            Date(2018, Month.NOVEMBER, 3),
            Date(2018, Month.NOVEMBER, 19).copy(dayOfMonth = 3)
        )

        assertEquals(
            Date(2018, Month.DECEMBER, 31),
            Date(2018, Month.NOVEMBER, 19).copy(dayOfYear = 365)
        )
    }

    @Test
    fun `dayOfWeek returns the expected day`() {
        assertEquals(
            DayOfWeek.THURSDAY, Date(1970, Month.JANUARY, 1).dayOfWeek
        )
        assertEquals(
            DayOfWeek.FRIDAY, Date(1970, Month.JANUARY, 2).dayOfWeek
        )
        assertEquals(
            DayOfWeek.WEDNESDAY, Date(1969, Month.DECEMBER, 31).dayOfWeek
        )
        assertEquals(
            DayOfWeek.SATURDAY, Date(2019, Month.JULY, 27).dayOfWeek
        )
    }

    @Test
    fun `dayOfMonth returns the expected day`() {
        assertEquals(1, Date(2019, Month.JANUARY, 1).dayOfMonth)
    }

    @Test
    fun `dayOfYear works correctly in common years`() {
        assertEquals(1, Date(2019, Month.JANUARY, 1).dayOfYear)
        assertEquals(365, Date(2019, Month.DECEMBER, 31).dayOfYear)
        assertEquals(59, Date(2019, Month.FEBRUARY, 28).dayOfYear)
    }

    @Test
    fun `dayOfYear works correctly in leap years`() {
        assertEquals(1, Date(2020, Month.JANUARY, 1).dayOfYear)
        assertEquals(60, Date(2020, Month.FEBRUARY, 29).dayOfYear)
        assertEquals(366, Date(2020, Month.DECEMBER, 31).dayOfYear)
    }

    @Test
    fun `can be broken down into components`() {
        val (year, month, day) = Date(2019, Month.AUGUST, 4)
        assertEquals(2019, year)
        assertEquals(Month.AUGUST, month)
        assertEquals(4, day)
    }

    @Test
    fun `date equality`() {
        val testDate = Date(2019, Month.JUNE, 21)
        assertTrue { testDate == testDate }
        assertTrue { testDate == Date(2019, Month.JUNE, 21) }
    }

    @Test
    fun `date inequality`() {
        assertTrue { Date(2018, Month.JUNE, 21) != Date(2019, Month.JUNE, 21) }
        assertTrue { Date(2019, Month.JULY, 21) != Date(2019, Month.JUNE, 21) }
        assertTrue { Date(2019, Month.JUNE, 22) != Date(2019, Month.JUNE, 21) }
    }

    @Test
    fun `date less than`() {
        assertTrue { Date(2019, Month.JUNE, 20) < Date(2019, Month.JUNE, 21) }
        assertTrue { Date(2019, Month.JUNE, 21) < Date(2019, Month.JULY, 21) }
    }

    @Test
    fun `date greater than`() {
        assertTrue { Date(2019, Month.JULY, 21) > Date(2019, Month.JUNE, 21) }
        assertTrue { Date(2020, Month.JUNE, 21) > Date(2019, Month.JUNE, 21) }
    }

    @Test
    fun `add period of zero`() {
        assertEquals(
            Date(2016, Month.FEBRUARY, 29),
            Date(2016, Month.FEBRUARY, 29) + Period.ZERO
        )
    }

    @Test
    fun `adding a period adds years then months then days`() {
        assertEquals(
            Date(2017, Month.MARCH, 29),
            Date(2016, Month.FEBRUARY, 29) + periodOf(1.years, 1.months, 1.days)
        )

        assertEquals(
            Date(2015, Month.JANUARY, 27),
            Date(2016, Month.FEBRUARY, 29) + periodOf((-1).years, (-1).months, (-1).days)
        )
    }

    @Test
    fun `subtract period of zero`() {
        assertEquals(
            Date(2016, Month.FEBRUARY, 29),
            Date(2016, Month.FEBRUARY, 29) - Period.ZERO
        )
    }

    @Test
    fun `subtracting a period subtracts years then months then days`() {
        assertEquals(
            Date(2017, Month.MARCH, 29),
            Date(2016, Month.FEBRUARY, 29) - periodOf((-1).years, (-1).months, (-1).days)
        )

        assertEquals(
            Date(2015, Month.JANUARY, 27),
            Date(2016, Month.FEBRUARY, 29) - periodOf(1.years, 1.months, 1.days)
        )
    }

    @Test
    fun `add zero days`() {
        val date = Date(2019, Month.JANUARY, 1)
        assertEquals(date, date + 0.days)
    }

    @Test
    fun `add positive days`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1),
            Date(2018, Month.DECEMBER, 31) + 1.days
        )

        assertEquals(
            Date(2020, Month.MARCH, 1),
            Date(2018, Month.DECEMBER, 31) + 426.days
        )

        assertEquals(
            Date(1970, Month.MARCH, 1),
            Date(1969, Month.MARCH, 1) + 365.days
        )
    }

    @Test
    fun `add negative days`() {
        assertEquals(
            Date(2018, Month.DECEMBER, 31),
            Date(2019, Month.JANUARY, 1) + (-1).days
        )

        assertEquals(
            Date(2018, Month.DECEMBER, 31),
            Date(2020, Month.MARCH, 1) + -(426.days)
        )

        assertEquals(
            Date(1969, Month.MARCH, 1),
            Date(1970, Month.MARCH, 1) + (-365).days
        )
    }

    @Test
    fun `subtract zero days`() {
        val date = Date(2019, Month.JANUARY, 1)
        assertEquals(date, date - 0.days)
    }

    @Test
    fun `throws an exception when adding or subtracting days causes overflow`() {
        listOf(
            { Date.MAX + Long.MAX_VALUE.days },
            { Date.MIN + Long.MIN_VALUE.days },
            { Date(9999, Month.JANUARY, 28) + Long.MAX_VALUE.days },
            { Date(1, Month.JANUARY, 28) + Long.MIN_VALUE.days },
            { Date.MAX - Long.MIN_VALUE.days },
            { Date.MIN - Long.MAX_VALUE.days },
            { Date(1, Month.JANUARY, 28) - Long.MAX_VALUE.days },
            { Date(9999, Month.JANUARY, 28) - Long.MIN_VALUE.days }
        ).forEach {
            assertFailsWith<ArithmeticException> { it() }
        }
    }

    @Test
    fun `throws an exception when adding or subtracting days puts the date out of range`() {
        listOf(
            { Date.MAX + 1.days },
            { Date.MIN - 1.days },
            { Date(Year.MAX_VALUE, Month.JANUARY, 28) + Int.MAX_VALUE.days },
            { Date(Year.MIN_VALUE, Month.JANUARY, 28) + Int.MIN_VALUE.days },
            { Date(Year.MIN_VALUE, Month.JANUARY, 28) - Int.MAX_VALUE.days },
            { Date(Year.MAX_VALUE, Month.JANUARY, 28) - Int.MIN_VALUE.days },
            { Date(9999, Month.JANUARY, 28) - Long.MAX_VALUE.days },
            { Date(1, Month.JANUARY, 28) - Long.MIN_VALUE.days }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `subtract positive days`() {
        assertEquals(
            Date(2018, Month.DECEMBER, 31),
            Date(2019, Month.JANUARY, 1) - 1.days
        )

        assertEquals(
            Date(2018, Month.DECEMBER, 31),
            Date(2020, Month.MARCH, 1) - 426.days
        )

        assertEquals(
            Date(1969, Month.MARCH, 1),
            Date(1970, Month.MARCH, 1) - 365.days
        )
    }

    @Test
    fun `subtract negative days`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1),
            Date(2018, Month.DECEMBER, 31) - (-1).days
        )

        assertEquals(
            Date(2020, Month.MARCH, 1),
            Date(2018, Month.DECEMBER, 31) - -(426.days)
        )

        assertEquals(
            Date(1970, Month.MARCH, 1),
            Date(1969, Month.MARCH, 1) - (-365).days
        )
    }

    @Test
    fun `adding zero weeks has no effect`() {
        assertEquals(
            Date(1930, Month.JANUARY, 28),
            Date(1930, Month.JANUARY, 28) + 0.weeks
        )
    }

    @Test
    fun `subtracting zero weeks has no effect`() {
        assertEquals(
            Date(1930, Month.JANUARY, 28),
            Date(1930, Month.JANUARY, 28) - 0.weeks
        )
    }

    @Test
    fun `throws an exception when adding or subtracting weeks causes overflow`() {
        assertFailsWith<ArithmeticException> { Date(2019, Month.NOVEMBER, 10) + Long.MAX_VALUE.weeks }
        assertFailsWith<ArithmeticException> { Date(2019, Month.NOVEMBER, 10) + Long.MIN_VALUE.weeks }
        assertFailsWith<ArithmeticException> { Date(2019, Month.NOVEMBER, 10) - Long.MAX_VALUE.weeks }
        assertFailsWith<ArithmeticException> { Date(2019, Month.NOVEMBER, 10) - Long.MIN_VALUE.weeks }
    }

    @Test
    fun `throws an exception when adding or subtracting weeks puts the date out of range`() {
        assertFailsWith<DateTimeException> { Date.MAX + 1.weeks }
        assertFailsWith<DateTimeException> { Date.MIN - 1.weeks }
        assertFailsWith<DateTimeException> { Date(999_991_000, Month.JANUARY, 28) + Int.MAX_VALUE.weeks }
        assertFailsWith<DateTimeException> { Date(-999_000_000, Month.JANUARY, 28) + Int.MIN_VALUE.weeks }
        assertFailsWith<DateTimeException> { Date(-999_000_000, Month.JANUARY, 28) - Int.MAX_VALUE.weeks }
        assertFailsWith<DateTimeException> { Date(999_000_000, Month.JANUARY, 28) - Int.MIN_VALUE.weeks }
    }

    @Test
    fun `add positive weeks`() {
        assertEquals(
            Date(2019, Month.FEBRUARY, 4),
            Date(2019, Month.JANUARY, 28) + 1.weeks
        )
    }

    @Test
    fun `add negative weeks`() {
        assertEquals(
            Date(2019, Month.JANUARY, 21),
            Date(2019, Month.JANUARY, 28) + (-1).weeks
        )
    }

    @Test
    fun `subtract positive weeks`() {
        assertEquals(
            Date(2019, Month.JANUARY, 21),
            Date(2019, Month.JANUARY, 28) - 1.weeks
        )
    }

    @Test
    fun `subtract negative weeks`() {
        assertEquals(
            Date(2019, Month.FEBRUARY, 4),
            Date(2019, Month.JANUARY, 28) - (-1).weeks
        )
    }

    @Test
    fun `throws an exception when adding or subtracting months puts the date out of range`() {
        assertFailsWith<DateTimeException> { Date.MAX + 1.months }
        assertFailsWith<DateTimeException> { Date.MIN - 1.months }
        assertFailsWith<DateTimeException> { Date(Year.MAX_VALUE, Month.JANUARY, 28) + Int.MAX_VALUE.months }
        assertFailsWith<DateTimeException> { Date(Year.MIN_VALUE, Month.JANUARY, 28) + Int.MIN_VALUE.months }
        assertFailsWith<DateTimeException> { Date(Year.MAX_VALUE, Month.JANUARY, 28) + Long.MAX_VALUE.months }
        assertFailsWith<DateTimeException> { Date(Year.MIN_VALUE, Month.JANUARY, 28) + Long.MIN_VALUE.months }
        assertFailsWith<DateTimeException> { Date(Year.MIN_VALUE, Month.JANUARY, 28) - Int.MAX_VALUE.months }
        assertFailsWith<DateTimeException> { Date(Year.MAX_VALUE, Month.JANUARY, 28) - Int.MIN_VALUE.months }
        assertFailsWith<DateTimeException> { Date(Year.MAX_VALUE, Month.JANUARY, 28) - Long.MAX_VALUE.months }
        assertFailsWith<DateTimeException> { Date(Year.MIN_VALUE, Month.JANUARY, 28) - Long.MIN_VALUE.months }
    }

    @Test
    fun `add zero months`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date + 0.months)
    }

    @Test
    fun `add positive months`() {
        assertEquals(
            Date(2019, Month.FEBRUARY, 28),
            Date(2019, Month.JANUARY, 28) + 1.months
        )

        assertEquals(
            Date(2019, Month.FEBRUARY, 28),
            Date(2019, Month.JANUARY, 31) + 1.months
        )

        assertEquals(
            Date(2020, Month.FEBRUARY, 29),
            Date(2019, Month.JANUARY, 31) + 13.months
        )

        assertEquals(
            Date(1904, Month.FEBRUARY, 29),
            Date(1903, Month.JANUARY, 31) + 13.months
        )
    }

    @Test
    fun `add negative months`() {
        assertEquals(
            Date(2018, Month.DECEMBER, 31),
            Date(2019, Month.JANUARY, 31) + (-1).months
        )

        assertEquals(
            Date(2019, Month.FEBRUARY, 28),
            Date(2019, Month.MARCH, 31) + -(1.months)
        )

        assertEquals(
            Date(2017, Month.NOVEMBER, 30),
            Date(2019, Month.JANUARY, 31) + (-14).months
        )

        assertEquals(
            Date(1910, Month.NOVEMBER, 30),
            Date(1912, Month.JANUARY, 31) + (-14).months
        )
    }

    @Test
    fun `subtract zero months`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date - 0.months)
    }

    @Test
    fun `subtract positive months`() {
        assertEquals(
            Date(2018, Month.DECEMBER, 31),
            Date(2019, Month.JANUARY, 31) - 1.months
        )

        assertEquals(
            Date(2019, Month.FEBRUARY, 28),
            Date(2019, Month.MARCH, 31) - 1.months
        )

        assertEquals(
            Date(2017, Month.NOVEMBER, 30),
            Date(2019, Month.JANUARY, 31) - 14.months
        )
    }

    @Test
    fun `subtract negative months`() {
        assertEquals(
            Date(2019, Month.FEBRUARY, 28),
            Date(2019, Month.JANUARY, 28) - (-1).months
        )

        assertEquals(
            Date(2019, Month.FEBRUARY, 28),
            Date(2019, Month.JANUARY, 31) - -(1.months)
        )

        assertEquals(
            Date(2020, Month.FEBRUARY, 29),
            Date(2019, Month.JANUARY, 31) - (-13).months
        )
    }

    @Test
    fun `throws an exception when adding or subtracting years puts the date out of range`() {
        assertFailsWith<DateTimeException> { Date.MAX + 1.years }
        assertFailsWith<DateTimeException> { Date.MIN - 1.years }
        assertFailsWith<DateTimeException> { Date(9999, Month.JANUARY, 28) + Int.MAX_VALUE.years }
        assertFailsWith<DateTimeException> { Date(1, Month.JANUARY, 28) + Int.MIN_VALUE.years }
        assertFailsWith<DateTimeException> { Date(9999, Month.JANUARY, 28) + Long.MAX_VALUE.years }
        assertFailsWith<DateTimeException> { Date(1, Month.JANUARY, 28) + Long.MIN_VALUE.years }
        assertFailsWith<DateTimeException> { Date(9999, Month.JANUARY, 28) - Int.MAX_VALUE.years }
        assertFailsWith<DateTimeException> { Date(1, Month.JANUARY, 28) - Int.MIN_VALUE.years }
        assertFailsWith<DateTimeException> { Date(9999, Month.JANUARY, 28) - Long.MAX_VALUE.years }
        assertFailsWith<DateTimeException> { Date(1, Month.JANUARY, 28) - Long.MIN_VALUE.years }
    }

    @Test
    fun `add zero years`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date + 0.years)
    }

    @Test
    fun `add positive years`() {
        assertEquals(
            Date(2020, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) + 1.years
        )

        assertEquals(
            Date(2021, Month.FEBRUARY, 28),
            Date(2020, Month.FEBRUARY, 29) + 1.years
        )

        assertEquals(
            Date(2024, Month.FEBRUARY, 29),
            Date(2020, Month.FEBRUARY, 29) + 4.years
        )
    }

    @Test
    fun `add negative years`() {
        assertEquals(
            Date(2018, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) + (-1).years
        )

        assertEquals(
            Date(2020, Month.FEBRUARY, 28),
            Date(2021, Month.FEBRUARY, 28) + -(1.years)
        )

        assertEquals(
            Date(2016, Month.FEBRUARY, 29),
            Date(2020, Month.FEBRUARY, 29) + (-4).years
        )
    }

    @Test
    fun `subtract zero years`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date - 0.years)
    }

    @Test
    fun `subtract positive years`() {
        assertEquals(
            Date(2018, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) - 1.years
        )

        assertEquals(
            Date(2020, Month.FEBRUARY, 28),
            Date(2021, Month.FEBRUARY, 28) - 1.years
        )

        assertEquals(
            Date(2016, Month.FEBRUARY, 29),
            Date(2020, Month.FEBRUARY, 29) - 4.years
        )
    }

    @Test
    fun `subtract negative years`() {
        assertEquals(
            Date(2020, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) - (-1).years
        )

        assertEquals(
            Date(2021, Month.FEBRUARY, 28),
            Date(2020, Month.FEBRUARY, 29) - -(1.years)
        )

        assertEquals(
            Date(2024, Month.FEBRUARY, 29),
            Date(2020, Month.FEBRUARY, 29) - (-4).years
        )
    }

    @Test
    fun `throws an exception when adding or subtracting decades puts the date out of range`() {
        assertFailsWith<DateTimeException> { Date.MAX + 1.decades }
        assertFailsWith<DateTimeException> { Date.MIN - 1.decades }
    }

    @Test
    fun `throws an exception when adding or subtracting decades causes overflow`() {
        assertFailsWith<ArithmeticException> { Date(9999, Month.JANUARY, 28) + Long.MAX_VALUE.decades }
        assertFailsWith<ArithmeticException> { Date(1, Month.JANUARY, 28) + Long.MIN_VALUE.decades }
        assertFailsWith<ArithmeticException> { Date(9999, Month.JANUARY, 28) - Long.MAX_VALUE.decades }
        assertFailsWith<ArithmeticException> { Date(1, Month.JANUARY, 28) - Long.MIN_VALUE.decades }
    }

    @Test
    fun `add zero decades`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date + 0.decades)
    }

    @Test
    fun `add positive decades`() {
        assertEquals(
            Date(2030, Month.FEBRUARY, 28),
            Date(2020, Month.FEBRUARY, 29) + 1.decades
        )
    }

    @Test
    fun `add negative decades`() {
        assertEquals(
            Date(2009, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) + (-1).decades
        )
    }

    @Test
    fun `subtract zero decades`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date - 0.decades)
    }

    @Test
    fun `subtract positive decades`() {
        assertEquals(
            Date(2009, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) - 1.decades
        )
    }

    @Test
    fun `subtract negative decades`() {
        assertEquals(
            Date(2029, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) - (-1).decades
        )
    }

    @Test
    fun `throws an exception when adding or subtracting centuries puts the date out of range`() {
        assertFailsWith<DateTimeException> { Date.MAX + 1.centuries }
        assertFailsWith<DateTimeException> { Date.MIN - 1.centuries }
    }

    @Test
    fun `throws an exception when adding or subtracting centuries causes overflow`() {
        assertFailsWith<ArithmeticException> { Date(9999, Month.JANUARY, 28) + Long.MAX_VALUE.centuries }
        assertFailsWith<ArithmeticException> { Date(1, Month.JANUARY, 28) + Long.MIN_VALUE.centuries }
        assertFailsWith<ArithmeticException> { Date(9999, Month.JANUARY, 28) - Long.MAX_VALUE.centuries }
        assertFailsWith<ArithmeticException> { Date(1, Month.JANUARY, 28) - Long.MIN_VALUE.centuries }
    }

    @Test
    fun `add zero centuries`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date + 0.centuries)
    }

    @Test
    fun `add positive centuries`() {
        assertEquals(
            Date(2119, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) + 1.centuries
        )
    }

    @Test
    fun `add negative centuries`() {
        assertEquals(
            Date(1919, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) + (-1).centuries
        )
    }

    @Test
    fun `subtract zero centuries`() {
        val date = Date(2019, Month.JANUARY, 28)
        assertEquals(date, date - 0.centuries)
    }

    @Test
    fun `subtract positive centuries`() {
        assertEquals(
            Date(1919, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) - 1.centuries
        )
    }

    @Test
    fun `subtract negative centuries`() {
        assertEquals(
            Date(2119, Month.JANUARY, 31),
            Date(2019, Month.JANUARY, 31) - (-1).centuries
        )
    }

    @Test
    fun `daysSinceUnixEpoch property works correctly`() {
        assertEquals(0L.days, Date(1970, Month.JANUARY, 1).daysSinceUnixEpoch)
        assertEquals(1L.days, Date(1970, Month.JANUARY, 2).daysSinceUnixEpoch)
        assertEquals((-1L).days, Date(1969, Month.DECEMBER, 31).daysSinceUnixEpoch)
        assertEquals(18_105L.days, Date(2019, Month.JULY, 28).daysSinceUnixEpoch)
        assertEquals((-4_472L).days, Date(1957, Month.OCTOBER, 4).daysSinceUnixEpoch)
    }

    @Test
    fun `toString returns an ISO-8601 extended calendar date`() {
        assertEquals("2019-08-01", Date(2019, Month.AUGUST, 1).toString())
        assertEquals("0001-10-10", Date(1, Month.OCTOBER, 10).toString())
    }

    @Test
    fun `String_toDate throws an exception when given an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toDate() }
    }

    @Test
    fun `String_toDate throws an exception when the format is not an ISO-8601 extended date`() {
        listOf(
            "1",
            "--",
            "2010",
            "2010-",
            "2010--",
            "2010-10-",
            "2010-10-2",
            "999-10-20",
            "2010/10/20",
            "2010-10-20-",
            "2010-10-20 "
        ).forEach {
            assertFailsWith<DateTimeParseException> { it.toDate() }
        }
    }

    @Test
    fun `String_toDate throws an exception when unexpected characters exist before a valid string`() {
        assertFailsWith<DateTimeParseException> { " 2010-02-20".toDate() }
        assertFailsWith<DateTimeParseException> { "T2010-10-20".toDate() }
    }

    @Test
    fun `String_toDate throws an exception when unexpected characters exist after a valid string`() {
        assertFailsWith<DateTimeParseException> { "2010-2-20 ".toDate() }
        assertFailsWith<DateTimeParseException> { "2010-10-200".toDate() }
        assertFailsWith<DateTimeParseException> { "2010-10-20T".toDate() }
    }

    @Test
    fun `String_toDate throws an exception when the parser cannot supply the required fields`() {
        assertFailsWith<DateTimeParseException> { "05:06".toDate(DateTimeParsers.Iso.TIME) }
        assertFailsWith<DateTimeParseException> { "2010".toDate(DateTimeParsers.Iso.YEAR) }
        assertFailsWith<DateTimeParseException> { "2010-05".toDate(DateTimeParsers.Iso.YEAR_MONTH) }
    }

    @Test
    fun `String_toDate throws an exception when fields overflow`() {
        val customParser = dateTimeParser {
            year()
            +' '
            monthNumber()
            +' '
            dayOfYear()
        }

        assertFailsWith<DateTimeException> { "10000000001 5 4".toDate(customParser) }
    }

    @Test
    fun `String_toDate parses valid ISO-8601 extended date strings by default`() {
        listOf(
            "2000-02-29" to Date(2000, Month.FEBRUARY, 29),
            "+999999999-12-31" to Date(999_999_999, Month.DECEMBER, 31),
            "-999999999-01-01" to Date(-999_999_999, Month.JANUARY, 1)
        ).forEach { (string, result) ->
            assertEquals(result, string.toDate())
        }
    }

    @Test
    fun `String_toDate parses valid strings with explicit parser`() {
        listOf(
            "2000-02-29",
            "20000229",
            "2000-060",
            "+2000-060",
            "2000060"
        ).forEach {
            assertEquals(Date(2000, Month.FEBRUARY, 29), it.toDate(DateTimeParsers.Iso.DATE))
        }
    }
}
