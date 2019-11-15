package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.parser.dateTimeParser
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DateTimeTest : AbstractIslandTimeTest() {
    @Test
    fun `at infix combines date with time`() {
        val today = Date(2019, Month.JANUARY, 1)

        assertEquals(
            DateTime(today, Time(2, 30)),
            today at "02:30".toTime()
        )
    }

    @Test
    fun `Date_startOfDay returns the DateTime at midnight of same day`() {
        assertEquals(
            DateTime(
                Date(2019, Month.JULY, 1),
                Time.MIDNIGHT
            ),
            Date(2019, Month.JULY, 1).startOfDay
        )
    }

    @Test
    fun `Date_endOfDay returns the DateTime just before the end of the same day`() {
        assertEquals(
            DateTime(
                Date(2019, Month.JULY, 1),
                Time(23, 59, 59, 999_999_999)
            ),
            Date(2019, Month.JULY, 1).endOfDay
        )
    }

    @Test
    fun `copy() returns a new DateTime replacing the desired values`() {
        assertEquals(
            DateTime(2018, Month.MAY, 8, 12, 0),
            DateTime(2018, Month.MAY, 4, 18, 0).copy(hour = 12, dayOfMonth = 8)
        )

        assertEquals(
            DateTime(2018, Month.FEBRUARY, 2, 18, 0, 12),
            DateTime(2018, Month.MAY, 4, 18, 0).copy(second = 12, dayOfYear = 33)
        )

        assertEquals(
            DateTime(2018, Month.DECEMBER, 4, 18, 2),
            DateTime(2018, Month.MAY, 4, 18, 0).copy(minute = 2, monthNumber = 12)
        )
    }

    @Test
    fun `can be compared`() {
        assertTrue {
            (Date(1969, Month.DECEMBER, 1) at Time.NOON) <
                (Date(1970, Month.JANUARY, 1) at Time.MIDNIGHT)
        }
    }

    @Test
    fun `can be destructured into date and time components`() {
        val (date, time) = DateTime(2000, Month.JANUARY, 1, 9, 0)
        assertEquals(Date(2000, Month.JANUARY, 1), date)
        assertEquals(Time(9, 0), time)
    }

    @Test
    fun `add period of zero`() {
        assertEquals(
            DateTime(2016, Month.FEBRUARY, 29, 13, 0),
            DateTime(2016, Month.FEBRUARY, 29, 13, 0) + Period.ZERO
        )
    }

    @Test
    fun `adding a period first adds years, then months, then days`() {
        assertEquals(
            DateTime(2017, Month.MARCH, 29, 9, 0),
            DateTime(2016, Month.FEBRUARY, 29, 9, 0) +
                periodOf(1.years, 1.months, 1.days)
        )

        assertEquals(
            DateTime(2015, Month.JANUARY, 27, 9, 0),
            DateTime(2016, Month.FEBRUARY, 29, 9, 0) +
                periodOf((-1).years, (-1).months, (-1).days)
        )
    }

    @Test
    fun `subtract period of zero`() {
        assertEquals(
            DateTime(2016, Month.FEBRUARY, 29, 13, 0),
            DateTime(2016, Month.FEBRUARY, 29, 13, 0) - Period.ZERO
        )
    }

    @Test
    fun `subtracting a period first subtracts years, then months, then days`() {
        assertEquals(
            DateTime(2017, Month.MARCH, 29, 9, 0),
            DateTime(2016, Month.FEBRUARY, 29, 9, 0) -
                periodOf((-1).years, (-1).months, (-1).days)
        )

        assertEquals(
            DateTime(2015, Month.JANUARY, 27, 9, 0),
            DateTime(2016, Month.FEBRUARY, 29, 9, 0) -
                periodOf(1.years, 1.months, 1.days)
        )
    }

    @Test
    fun `add duration of zero`() {
        val dateTime = Date(2010, Month.JULY, 4) at Time(18, 0)
        assertEquals(dateTime, dateTime + Duration.ZERO)
    }

    @Test
    fun `add a duration`() {
        assertEquals(
            Date(2010, Month.JULY, 4) at Time(18, 0),
            (Date(2010, Month.JULY, 3) at Time.MAX) + durationOf(18.hours + 1.nanoseconds)
        )
    }

    @Test
    fun `subtract duration of zero`() {
        val dateTime = Date(2010, Month.JULY, 4) at Time(18, 0)
        assertEquals(dateTime, dateTime - Duration.ZERO)
    }

    @Test
    fun `subtract a duration`() {
        assertEquals(
            Date(2010, Month.JULY, 4) at Time(18, 0),
            (Date(2010, Month.JULY, 5) at Time(1, 0, 0, 1)) -
                durationOf(7.hours + 1.nanoseconds)
        )
    }

    @Test
    fun `adding or subtracting zero weeks has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.weeks)
        assertEquals(date, date + 0L.weeks)
        assertEquals(date, date - 0.weeks)
        assertEquals(date, date - 0L.weeks)
    }

    @Test
    fun `add positive weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1970, Month.JANUARY, 5) at Time.NOON,
            date + 5.weeks
        )

        assertEquals(
            Date(1970, Month.JANUARY, 5) at Time.NOON,
            date + 5L.weeks
        )
    }

    @Test
    fun `add negative weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.OCTOBER, 27) at Time.NOON,
            date + (-5).weeks
        )

        assertEquals(
            Date(1969, Month.OCTOBER, 27) at Time.NOON,
            date + (-5L).weeks
        )
    }

    @Test
    fun `subtract positive weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.OCTOBER, 27) at Time.NOON,
            date - 5.weeks
        )

        assertEquals(
            Date(1969, Month.OCTOBER, 27) at Time.NOON,
            date - 5L.weeks
        )
    }

    @Test
    fun `subtract negative weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1970, Month.JANUARY, 5) at Time.NOON,
            date - (-5).weeks
        )

        assertEquals(
            Date(1970, Month.JANUARY, 5) at Time.NOON,
            date - (-5L).weeks
        )
    }

    @Test
    fun `add zero hours`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.hours)
    }

    @Test
    fun `add hours`() {
        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time(1, 0),
            (Date(1969, Month.DECEMBER, 1) at Time.NOON) + 13.hours
        )

        assertEquals(
            Date(1969, Month.DECEMBER, 3) at Time(1, 0),
            (Date(1969, Month.DECEMBER, 1) at Time.NOON) + 37.hours
        )
    }

    @Test
    fun `subtract hours`() {
        assertEquals(
            Date(2020, Month.FEBRUARY, 28) at
                Time(22, 59, 59, 1),
            (Date(2020, Month.FEBRUARY, 29) at
                Time(3, 59, 59, 1)) - 5.hours
        )
    }

    @Test
    fun `add minutes`() {
        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time(0, 10),
            (Date(1969, Month.DECEMBER, 1) at Time(23, 40)) + 30.minutes
        )

        assertEquals(
            Date(1969, Month.DECEMBER, 2) at
                Time(1, 10, 1, 1),
            (Date(1969, Month.DECEMBER, 1) at
                Time(23, 40, 1, 1)) + 90.minutes
        )
    }

    @Test
    fun `subtract minutes`() {
        assertEquals(
            Date(2020, Month.FEBRUARY, 29) at
                Time(23, 50, 10, 1),
            (Date(2020, Month.MARCH, 1) at
                Time(0, 10, 10, 1)) - 20.minutes
        )
    }

    @Test
    fun `add seconds`() {
        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time.MIDNIGHT,
            (Date(1969, Month.DECEMBER, 1) at Time(23, 59, 30)) + 30.seconds
        )

        assertEquals(
            Date(1969, Month.DECEMBER, 1) at
                Time(23, 41, 31, 1),
            (Date(1969, Month.DECEMBER, 1) at
                Time(23, 40, 1, 1)) + 90.seconds
        )
    }

    @Test
    fun `subtract seconds`() {
        assertEquals(
            DateTime(2018, Month.MARCH, 11, 23, 59, 59, 1),
            DateTime(2018, Month.MARCH, 12, 0, 0, 1, 1) -
                (2.seconds)
        )

        assertEquals(
            DateTime(2018, Month.MARCH, 12, 0, 59, 50, 1),
            DateTime(2018, Month.MARCH, 12, 1, 1, 0, 1) -
                (70.seconds)
        )
    }

    @Test
    fun `add nanoseconds`() {
        assertEquals(
            Date(1970, Month.JANUARY, 1) at Time.MIDNIGHT,
            (Date(1969, Month.DECEMBER, 31) at Time.MAX) + 1.nanoseconds
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(0, 59, 59, 999_999_999),
            (Date(1969, Month.DECEMBER, 31) at Time.MAX) + 1.hours.inNanoseconds
        )
    }

    @Test
    fun `subtract nanoseconds`() {
        assertEquals(
            DateTime(2018, Month.MARCH, 11, 23, 59, 0, 999_999_999),
            DateTime(2018, Month.MARCH, 12, 0, 1, 1, 1) -
                (2.minutes + 2.nanoseconds)
        )
    }

    @Test
    fun `toString() returns an ISO-8601 extended calendar date time`() {
        assertEquals(
            "2019-08-01T00:01",
            DateTime(2019, Month.AUGUST, 1, 0, 1).toString()
        )
        assertEquals(
            "0001-10-10T01:01:01.000000001",
            DateTime(1, Month.OCTOBER, 10, 1, 1, 1, 1).toString()
        )
    }

    @Test
    fun `String_toDateTime() throws an exception when the format is mixed basic and extended`() {
        assertFailsWith<DateTimeParseException> { "20000101 00:23".toDateTime() }
        assertFailsWith<DateTimeParseException> { "2002-02-01T0023".toDateTime() }
    }

    @Test
    fun `String_toDateTime() throws an exception when given an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toDateTime() }
    }

    @Test
    fun `String_toDateTime() parses valid ISO-8601 extended calendar date strings by default`() {
        assertEquals(
            DateTime(2019, Month.MARCH, 23, 2, 30),
            "2019-03-23T02:30".toDateTime()
        )

        assertEquals(
            DateTime(2000, Month.FEBRUARY, 29, 23, 59, 59, 999_999_999),
            "2000-02-29 23:59:59.999999999".toDateTime()
        )
    }

    @Test
    fun `String_toDateTime() parses valid ISO-8601 basic calendar date strings`() {
        assertEquals(
            DateTime(2019, Month.MARCH, 23, 2, 30),
            "20190323T0230".toDateTime(DateTimeParsers.Iso.Basic.DATE_TIME)
        )
    }

    @Test
    fun `String_toDateTime() parses valid ISO-8601 ordinal date strings with custom parser`() {
        val parser = dateTimeParser {
            anyOf({
                childParser(DateTimeParsers.Iso.Basic.DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(DateTimeParsers.Iso.Basic.TIME)
            }, {
                childParser(DateTimeParsers.Iso.Extended.DATE)
                anyOf({ +'T' }, { +' ' })
                childParser(DateTimeParsers.Iso.Extended.TIME)
            })
        }

        assertEquals(
            DateTime(2019, Month.JANUARY, 1, 2, 30),
            "2019001 0230".toDateTime(parser)
        )

        assertEquals(
            DateTime(2019, Month.JANUARY, 1, 2, 30),
            "2019-001T02:30".toDateTime(parser)
        )
    }
}