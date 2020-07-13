package io.islandtime.ranges

import io.islandtime.Date
import io.islandtime.Month
import io.islandtime.measures.*
import io.islandtime.parser.TemporalParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class DateRangeTest : AbstractIslandTimeTest() {
    @Test
    fun `contains() returns true for dates within range`() {
        val startDate = Date(2018, Month.DECEMBER, 25)
        val endDate = Date(2019, Month.JANUARY, 12)

        assertTrue { startDate in startDate..endDate }
        assertTrue { endDate in startDate..endDate }
        assertTrue { Date(2019, Month.JANUARY, 1) in startDate..endDate }
    }

    @Test
    fun `contains() returns false for out of range dates`() {
        val startDate = Date(2018, Month.DECEMBER, 25)
        val endDate = Date(2019, Month.JANUARY, 12)

        listOf(
            Date(2018, Month.DECEMBER, 24),
            Date(2019, Month.JANUARY, 13),
            Date.MIN,
            Date.MAX
        ).forEach {
            assertFalse { it in startDate..endDate }
        }
    }

    @Test
    fun `contains() returns false for null dates`() {
        val startDate = Date(2018, Month.DECEMBER, 25)
        val endDate = Date(2019, Month.JANUARY, 12)

        assertFalse { null in startDate..endDate }
    }

    @Test
    fun `isEmpty() returns true when the start is greater than the end`() {
        assertTrue { (Date(2018, 45)..Date(2018, 44)).isEmpty() }
    }

    @Test
    fun `isEmpty() returns true when the end is negative infinity or start is positive infinity`() {
        listOf(
            Date.MIN..Date.MIN,
            Date.MAX..Date.MAX
        ).forEach {
            assertTrue { it.isEmpty() }
        }
    }

    @Test
    fun `until infix operator constructs a range with non-inclusive end`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.JANUARY, 28)
        val range = startDate until endDate

        assertEquals(startDate, range.first)
        assertEquals(startDate, range.start)
        assertEquals(Date(2019, Month.JANUARY, 27), range.last)
        assertEquals(Date(2019, Month.JANUARY, 27), range.endInclusive)
        assertEquals(1.days, range.step)
        assertEquals(7, range.count())
        assertTrue { range.isBounded() }
        assertFalse { range.isUnbounded() }
        assertTrue { range.hasBoundedStart() }
        assertTrue { range.hasBoundedEnd() }
        assertFalse { range.hasUnboundedStart() }
        assertFalse { range.hasUnboundedEnd() }
    }

    @Test
    fun `toString() returns an ISO-8601 time interval representation`() {
        assertEquals(
            "1969-12-03/1970-02-03",
            (Date(1969, 12, 3)..Date(1970, 2, 3)).toString()
        )

        assertEquals(
            "../1970-02-03",
            (Date.MIN..Date(1970, 2, 3)).toString()
        )

        assertEquals(
            "1969-12-03/..",
            (Date(1969, 12, 3)..Date.MAX).toString()
        )

        assertEquals(
            "../..",
            DateRange.UNBOUNDED.toString()
        )

        assertEquals(
            "",
            DateRange.EMPTY.toString()
        )

        assertEquals(
            "",
            (Date(1934, 1)..Date(1932, 234)).toString()
        )
    }

    @Test
    fun `String_toDateRange() returns an empty range when the string is empty`() {
        assertEquals(DateRange.EMPTY, "".toDateRange())
    }

    @Test
    fun `String_toDateRange() parses ISO-8601 time interval strings with dates in extended format`() {
        assertEquals(DateRange.UNBOUNDED, "../..".toDateRange())

        assertEquals(
            Date(1950, 11, 9)..Date(1989, 6, 2),
            "1950-11-09/1989-06-02".toDateRange()
        )

        assertEquals(
            Date(1950, 11, 9)..Date.MAX,
            "1950-11-09/..".toDateRange()
        )

        assertEquals(
            Date.MIN..Date(1989, 6, 2),
            "../1989-06-02".toDateRange()
        )
    }

    @Test
    fun `String_toDateRange throws an exception when parsing invalid formats`() {
        listOf(
            " ",
            "/",
            "2019-10-06/", // We don't support unknown end
            "/2019-10-05", // We don't support unknown start
            "2019-05-06--2019-05-06",
            "2015/2016-10-10",
            "2015-05-05/2016-10",
            "2015-05-05/2016-10-10 ",
            "2015-05-05/2016-10-10/",
            " 2015-05-05/2016-10-10",
            "/2015-05-05/2016-10-10",
            "2015-05-05/2016-10-10/2019-11-10",
            "1950-11-09/1989-056" // Ordinal dates not supported by default
        ).forEach {
            assertFailsWith<TemporalParseException> { it.toDateRange() }
        }
    }

    @Test
    fun `String_toDateRange throws an exception when parsing mixed basic and extended formats`() {
        listOf(
            " ",
            "/",
            "2019-10-06/", // We don't support unknown end
            "/2019-10-05", // We don't support unknown start
            "20190506/2019-05-06",
            "2015/2016-10-10",
            "2015-05-05/2016-10",
            "2015-05-05/2016-10-10 ",
            "2015-05-05/2016-10-10/",
            " 2015-05-05/2016-10-10",
            "/2015-05-05/2016-10-10",
            "2015-05-05/2016-10-10/2019-11-10"
        ).forEach {
            assertFailsWith<TemporalParseException> { it.toDateRange(DateTimeParsers.Iso.DATE_RANGE) }
        }
    }

    @Test
    fun `random() returns a date within range`() {
        val range = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 25)
        val randomDate = range.random()
        assertTrue { randomDate in range }
    }

    @Test
    fun `length properties throw an exception when the range isn't bounded`() {
        listOf(
            DateRange.UNBOUNDED,
            Date(2019, Month.JUNE, 1)..Date.MAX,
            Date.MIN..Date(2019, Month.JUNE, 1)
        ).forEach {
            assertFailsWith<UnsupportedOperationException> { it.asPeriod() }
            assertFailsWith<UnsupportedOperationException> { it.lengthInDays }
            assertFailsWith<UnsupportedOperationException> { it.lengthInMonths }
            assertFailsWith<UnsupportedOperationException> { it.lengthInYears }
        }
    }

    @Test
    fun `lengthInDays property returns 0 when range is empty`() {
        assertEquals(0L.days, DateRange.EMPTY.lengthInDays)
    }

    @Test
    fun `lengthInDays property returns 1 when the start and end date are the same`() {
        val date = Date(2019, Month.JUNE, 1)
        assertEquals(1L.days, (date..date).lengthInDays)
    }

    @Test
    fun `lengthInDays property returns the expected number of days in a non-empty range`() {
        val start = Date(2018, Month.FEBRUARY, 1)
        val end = Date(2018, Month.FEBRUARY, 28)
        assertEquals(28L.days, (start..end).lengthInDays)
    }

    @Test
    fun `lengthInWeeks property returns 0 when range is empty`() {
        assertEquals(0L.weeks, DateRange.EMPTY.lengthInWeeks)
    }

    @Test
    fun `lengthInWeeks property returns the expected number of weeks in a non-empty range`() {
        val range1 = Date(2018, Month.FEBRUARY, 1)..Date(2018, Month.FEBRUARY, 28)
        assertEquals(4L.weeks, range1.lengthInWeeks)

        val range2 = Date(2018, Month.FEBRUARY, 1)..Date(2018, Month.FEBRUARY, 27)
        assertEquals(3L.weeks, range2.lengthInWeeks)
    }

    @Test
    fun `lengthInMonths property returns 0 when range is empty`() {
        assertEquals(0.months, DateRange.EMPTY.lengthInMonths)
    }

    @Test
    fun `lengthInMonths property returns the expected number of months in a non-empty range`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 18)
        assertEquals(0.months, range1.lengthInMonths)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 19)
        assertEquals(1.months, range2.lengthInMonths)
    }

    @Test
    fun `lengthInYears property returns 0 when range is empty`() {
        assertEquals(0.years, DateRange.EMPTY.lengthInYears)
    }

    @Test
    fun `lengthInYears property returns the expected number of years in a non-empty range`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2019, Month.FEBRUARY, 18)
        assertEquals(0.years, range1.lengthInYears)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2019, Month.FEBRUARY, 19)
        assertEquals(1.years, range2.lengthInYears)
    }

    @Test
    fun `asPeriod() returns a period of zero when range is empty`() {
        assertEquals(Period.ZERO, DateRange.EMPTY.asPeriod())
    }

    @Test
    fun `asPeriod() returns a period of 1 day when the start and end date are equal`() {
        val date = Date(2019, Month.JUNE, 1)
        assertEquals(periodOf(1.days), (date..date).asPeriod())
    }

    @Test
    fun `asPeriod() returns the expected period for non-empty ranges`() {
        val start = Date(2018, Month.FEBRUARY, 20)
        val end = Date(2019, Month.MARCH, 20)
        assertEquals(periodOf(1.years, 1.months, 1.days), (start..end).asPeriod())
    }

    @Test
    fun `periodBetween() returns a zeroed period when the start and end dates are the same`() {
        assertEquals(
            Period.ZERO,
            periodBetween(Date(2019, Month.MAY, 1), Date(2019, Month.MAY, 1))
        )
    }

    @Test
    fun `periodBetween() returns the period between two dates in positive progression`() {
        assertEquals(
            periodOf(1.months, 2.days),
            periodBetween(Date(2019, Month.MAY, 1), Date(2019, Month.JUNE, 3))
        )

        assertEquals(
            periodOf(1.months, 8.days),
            periodBetween(Date(2019, Month.MAY, 25), Date(2019, Month.JULY, 3))
        )

        assertEquals(
            periodOf(2.years, 29.days),
            periodBetween(Date(2018, Month.JANUARY, 31), Date(2020, Month.FEBRUARY, 29))
        )
    }

    @Test
    fun `periodBetween() returns the period between two dates in negative progression`() {
        assertEquals(
            periodOf((-28).days),
            periodBetween(Date(2019, Month.MAY, 1), Date(2019, Month.APRIL, 3))
        )

        assertEquals(
            periodOf((-1).months),
            periodBetween(Date(2019, Month.MAY, 1), Date(2019, Month.APRIL, 1))
        )

        assertEquals(
            periodOf((-1).years, (-10).months, (-21).days),
            periodBetween(Date(2019, Month.MAY, 25), Date(2017, Month.JULY, 4))
        )
    }

    @Test
    fun `daysBetween() returns zero days when the start and end date are the same`() {
        assertEquals(
            0L.days,
            daysBetween(Date(2019, Month.MAY, 1), Date(2019, Month.MAY, 1))
        )

        assertEquals(
            0L.days,
            daysBetween(Date(1969, Month.MAY, 1), Date(1969, Month.MAY, 1))
        )
    }

    @Test
    fun `daysBetween() returns the number of days between two dates in positive progression`() {
        assertEquals(
            33L.days,
            daysBetween(Date(2019, Month.MAY, 1), Date(2019, Month.JUNE, 3))
        )

        assertEquals(
            33L.days,
            daysBetween(Date(1969, Month.MAY, 1), Date(1969, Month.JUNE, 3))
        )
    }

    @Test
    fun `daysBetween() returns the number of days between two dates in negative progression`() {
        assertEquals(
            (-16L).days,
            daysBetween(Date(2019, Month.MAY, 1), Date(2019, Month.APRIL, 15))
        )

        assertEquals(
            (-16L).days,
            daysBetween(Date(1969, Month.MAY, 1), Date(1969, Month.APRIL, 15))
        )

        assertEquals(
            (-20L).days,
            daysBetween(Date(1970, Month.JANUARY, 4), Date(1969, Month.DECEMBER, 15))
        )
    }

    @Test
    fun `weeksBetween() returns zero when the start and end date are the same`() {
        assertEquals(
            0L.weeks,
            weeksBetween(Date(2019, Month.AUGUST, 23), Date(2019, Month.AUGUST, 23))
        )
    }

    @Test
    fun `weeksBetween() returns the number of weeks between two dates in positive progression`() {
        assertEquals(
            4L.weeks,
            weeksBetween(Date(2019, Month.MAY, 1), Date(2019, Month.JUNE, 3))
        )

        assertEquals(
            5L.weeks,
            weeksBetween(Date(1969, Month.MAY, 1), Date(1969, Month.JUNE, 5))
        )
    }

    @Test
    fun `weeksBetween() returns the number of weeks between two dates in negative progression`() {
        assertEquals(
            (-4L).weeks,
            weeksBetween(Date(2019, Month.JUNE, 3), Date(2019, Month.MAY, 1))
        )

        assertEquals(
            (-5L).weeks,
            weeksBetween(Date(1969, Month.JUNE, 5), Date(1969, Month.MAY, 1))
        )
    }

    @Test
    fun `monthsBetween() returns zero when the start and end date are the same`() {
        assertEquals(
            0.months,
            monthsBetween(Date(2019, Month.JULY, 15), Date(2019, Month.JULY, 15))
        )
    }

    @Test
    fun `monthsBetween() returns the months between two dates in positive progression`() {
        assertEquals(
            0.months,
            monthsBetween(Date(2019, Month.JULY, 15), Date(2019, Month.AUGUST, 14))
        )

        assertEquals(
            1.months,
            monthsBetween(Date(2019, Month.JULY, 15), Date(2019, Month.AUGUST, 15))
        )

        assertEquals(
            13.months,
            monthsBetween(Date(2019, Month.JULY, 15), Date(2020, Month.AUGUST, 15))
        )
    }

    @Test
    fun `monthsBetween() returns the months between two dates in negative progression`() {
        assertEquals(
            0.months,
            monthsBetween(Date(2019, Month.AUGUST, 14), Date(2019, Month.JULY, 15))
        )

        assertEquals(
            (-1).months,
            monthsBetween(Date(2019, Month.AUGUST, 15), Date(2019, Month.JULY, 15))
        )

        assertEquals(
            (-13).months,
            monthsBetween(Date(2020, Month.AUGUST, 15), Date(2019, Month.JULY, 15))
        )
    }

    @Test
    fun `yearsBetween() returns zero when the start and end date are the same`() {
        assertEquals(
            0.years,
            yearsBetween(Date(2019, Month.JULY, 15), Date(2019, Month.JULY, 15))
        )
    }

    @Test
    fun `yearsBetween() returns the years between two dates in positive progression`() {
        assertEquals(
            0.years,
            yearsBetween(Date(2019, Month.JULY, 15), Date(2020, Month.JULY, 14))
        )

        assertEquals(
            1.years,
            yearsBetween(Date(2019, Month.JULY, 15), Date(2020, Month.JULY, 15))
        )
    }

    @Test
    fun `yearsBetween() returns the years between two dates in negative progression`() {
        assertEquals(
            0.years,
            yearsBetween(Date(2020, Month.JULY, 15), Date(2019, Month.JULY, 16))
        )

        assertEquals(
            (-1).years,
            yearsBetween(Date(2020, Month.AUGUST, 15), Date(2019, Month.JULY, 15))
        )
    }
}