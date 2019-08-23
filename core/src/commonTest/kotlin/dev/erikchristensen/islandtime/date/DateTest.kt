package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseException
import dev.erikchristensen.islandtime.parser.Iso8601
import kotlin.test.*

class DateTest {
    @Test
    fun `throws an exception when constructed with an invalid year`() {
        assertFailsWith<IllegalArgumentException> { Date(-1, Month.JANUARY, 1) }
        assertFailsWith<IllegalArgumentException> { Date(10000, Month.JANUARY, 1) }
    }

    @Test
    fun `throws an exception when constructed with an invalid day`() {
        assertFailsWith<IllegalArgumentException> { Date(2000, Month.JANUARY, 32) }
        assertFailsWith<IllegalArgumentException> { Date(2001, Month.FEBRUARY, 29) }
        assertFailsWith<IllegalArgumentException> { Date(2001, Month.FEBRUARY, 0) }
    }

    @Test
    fun `throws an exception when creating from a day of year that's impossible`() {
        assertFailsWith<IllegalArgumentException> { Date(2019, -1) }
        assertFailsWith<IllegalArgumentException> { Date(2019, 367) }
        assertFailsWith<DateTimeException> { Date(2019, 366) }
    }

    @Test
    fun `creates dates from year and day of year`() {
        assertEquals(Date(2019, Month.DECEMBER, 1), Date(2019, 335))
        assertEquals(Date(2020, Month.DECEMBER, 1), Date(2020, 336))
    }

    @Test
    fun `dayOfWeek returns the expected day`() {
        assertEquals(
            DayOfWeek.THURSDAY, Date(
                1970,
                Month.JANUARY, 1
            ).dayOfWeek
        )
        assertEquals(
            DayOfWeek.FRIDAY, Date(
                1970,
                Month.JANUARY, 2
            ).dayOfWeek
        )
        assertEquals(
            DayOfWeek.WEDNESDAY, Date(
                1969,
                Month.DECEMBER, 31
            ).dayOfWeek
        )
        assertEquals(
            DayOfWeek.SATURDAY, Date(
                2019,
                Month.JULY, 27
            ).dayOfWeek
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
    fun `isInLeapYear returns true in leap year`() {
        assertTrue { Date(2020, Month.JANUARY, 1).isInLeapYear }
    }

    @Test
    fun `isInLeapYear returns false in common year`() {
        assertFalse { Date(2019, Month.JANUARY, 1).isInLeapYear }
    }

    @Test
    fun `isLeapDay property returns true only on February 29`() {
        assertTrue { Date(2020, Month.FEBRUARY, 29).isLeapDay }
        assertFalse { Date(2019, Month.FEBRUARY, 28).isLeapDay }
    }

    @Test
    fun `lengthOfMonth property returns the length in days of a date's month`() {
        assertEquals(29.days, Date(2020, Month.FEBRUARY, 29).lengthOfMonth)
        assertEquals(28.days, Date(2019, Month.FEBRUARY, 28).lengthOfMonth)
    }

    @Test
    fun `date equality`() {
        assertTrue { Date(2019, Month.JUNE, 21) == Date(2019, Month.JUNE, 21) }
    }

    @Test
    fun `date inequality`() {
        assertTrue { Date(2018, Month.JUNE, 21) != Date(2019, Month.JUNE, 21) }
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
    fun `add zero days`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1),
            Date(2019, Month.JANUARY, 1) + 0.days
        )
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
        assertEquals(
            Date(2019, Month.JANUARY, 1),
            Date(2019, Month.JANUARY, 1) - 0.days
        )
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
    fun `add zero months`() {
        assertEquals(
            Date(2019, Month.JANUARY, 28),
            Date(2019, Month.JANUARY, 28) + 0.months
        )
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
        assertEquals(
            Date(2019, Month.JANUARY, 28),
            Date(2019, Month.JANUARY, 28) - 0.months
        )
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
    fun `add zero years`() {
        assertEquals(
            Date(2019, Month.JANUARY, 28),
            Date(2019, Month.JANUARY, 28) + 0.years
        )
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
        assertEquals(
            Date(2019, Month.JANUARY, 28),
            Date(2019, Month.JANUARY, 28) - 0.years
        )
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
    fun `atStartOfDay() returns DateTime at midnight of same day`() {
        assertEquals(
            DateTime(
                Date(2019, Month.JULY, 1),
                Time.MIDNIGHT
            ),
            Date(2019, Month.JULY, 1).atStartOfDay()
        )
    }

    @Test
    fun `asUnixEpochDays() works correctly`() {
        assertEquals(0L.days, Date(1970, Month.JANUARY, 1).asUnixEpochDays())
        assertEquals(1L.days, Date(1970, Month.JANUARY, 2).asUnixEpochDays())
        assertEquals((-1L).days, Date(1969, Month.DECEMBER, 31).asUnixEpochDays())
        assertEquals(18_105L.days, Date(2019, Month.JULY, 28).asUnixEpochDays())
        assertEquals((-4_472L).days, Date(1957, Month.OCTOBER, 4).asUnixEpochDays())
    }

    @Test
    fun `toString() returns an ISO-8601 extended calendar date`() {
        assertEquals("2019-08-01", Date(2019, Month.AUGUST, 1).toString())
        assertEquals("0001-10-10", Date(1, Month.OCTOBER, 10).toString())
    }

    @Test
    fun `String_toDate() throws an exception when given an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toDate() }
    }

    @Test
    fun `String_toDate() throws an exception when the format is not an ISO-8601 extended date`() {
        assertFailsWith<DateTimeParseException> { "1".toDate() }
        assertFailsWith<DateTimeParseException> { "--".toDate() }
        assertFailsWith<DateTimeParseException> { "2010".toDate() }
        assertFailsWith<DateTimeParseException> { "2010-".toDate() }
        assertFailsWith<DateTimeParseException> { "2010--".toDate() }
        assertFailsWith<DateTimeParseException> { "2010-10-".toDate() }
        assertFailsWith<DateTimeParseException> { "2010-10-2".toDate() }
        assertFailsWith<DateTimeParseException> { "999-10-20".toDate() }
        assertFailsWith<DateTimeParseException> { "2010/10/20".toDate() }
    }

    @Test
    fun `String_toDate() throws an exception when unexpected characters exist before a valid string`() {
        assertFailsWith<DateTimeParseException> { " 2010-02-20".toDate() }
        assertFailsWith<DateTimeParseException> { "T2010-10-20".toDate() }
        assertFailsWith<DateTimeParseException> { "-0001-10-20".toDate() }
        assertFailsWith<DateTimeParseException> { "+0000-10-20".toDate() }
    }

    @Test
    fun `String_toDate() throws an exception when unexpected characters exist after a valid string`() {
        assertFailsWith<DateTimeParseException> { "2010-2-20 ".toDate() }
        assertFailsWith<DateTimeParseException> { "2010-10-200".toDate() }
        assertFailsWith<DateTimeParseException> { "2010-10-20T".toDate() }
    }

    @Test
    fun `String_toDate() parses valid ISO-8601 extended date strings by default`() {
        assertEquals(Date(2000, Month.FEBRUARY, 29), "2000-02-29".toDate())
    }

    @Test
    fun `String_toDate() parses valid strings with explicit parser`() {
        assertEquals(Date(2000, Month.FEBRUARY, 29), "2000-02-29".toDate(Iso8601.DATE_PARSER))
        assertEquals(Date(2000, Month.FEBRUARY, 29), "20000229".toDate(Iso8601.DATE_PARSER))
        assertEquals(Date(2000, Month.FEBRUARY, 29), "2000-060".toDate(Iso8601.DATE_PARSER))
        assertEquals(Date(2000, Month.FEBRUARY, 29), "2000060".toDate(Iso8601.DATE_PARSER))
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
    }

    @Test
    fun `daysBetween() returns the number of days between two dates in positive progression`() {
        assertEquals(
            33L.days,
            daysBetween(Date(2019, Month.MAY, 1), Date(2019, Month.JUNE, 3))
        )
    }

    @Test
    fun `daysBetween() returns the number of days between two dates in negative progression`() {
        assertEquals(
            (-16L).days,
            daysBetween(Date(2019, Month.MAY, 1), Date(2019, Month.APRIL, 15))
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