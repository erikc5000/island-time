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
import kotlin.time.Duration.Companion.hours as kotlinHours
import kotlin.time.Duration.Companion.nanoseconds as kotlinNanoseconds

class DateTimeTest : AbstractIslandTimeTest() {
    @Test
    fun `can be constructed with day of year`() {
        DateTime(2019, 18, 1, 2, 3, 4).run {
            assertEquals(2019, year)
            assertEquals(18, dayOfYear)
            assertEquals(1, hour)
            assertEquals(2, minute)
            assertEquals(3, second)
            assertEquals(4, nanosecond)
        }
    }

    @Test
    fun `can be constructed from millisecond of unix epoch`() {
        assertEquals(
            Date(1970, Month.JANUARY, 1) at Time(1, 0),
            DateTime.fromMillisecondOfUnixEpoch(0L, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at Time(1, 0),
            DateTime.fromMillisecondsSinceUnixEpoch(0L.milliseconds, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(1, 0, 0, 1_000_000),
            DateTime.fromMillisecondOfUnixEpoch(1L, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(1, 0, 0, 1_000_000),
            DateTime.fromMillisecondsSinceUnixEpoch(1L.milliseconds, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(0, 59, 59, 999_000_000),
            DateTime.fromMillisecondOfUnixEpoch(-1L, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(0, 59, 59, 999_000_000),
            DateTime.fromMillisecondsSinceUnixEpoch((-1L).milliseconds, 1.hours.asUtcOffset())
        )
    }

    @Test
    fun `can be constructed from second of unix epoch`() {
        assertEquals(
            Date(1970, Month.JANUARY, 1) at Time(1, 0),
            DateTime.fromSecondOfUnixEpoch(0L, 0, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at Time(1, 0),
            DateTime.fromSecondsSinceUnixEpoch(0L.seconds, 0.nanoseconds, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(1, 0, 0, 1),
            DateTime.fromSecondOfUnixEpoch(0L, 1, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(1, 0, 0, 1),
            DateTime.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(0, 59, 59, 999_999_999),
            DateTime.fromSecondOfUnixEpoch(0L, -1, 1.hours.asUtcOffset())
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(0, 59, 59, 999_999_999),
            DateTime.fromSecondsSinceUnixEpoch(0L.seconds, (-1).nanoseconds, 1.hours.asUtcOffset())
        )
    }

    @Test
    fun `copy returns a new DateTime replacing the desired values`() {
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
    fun `secondsSinceUnixEpochAt returns the number of seconds since the unix epoch`() {
        assertEquals(
            1L.seconds,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 1))
                .secondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            0L.seconds,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 0, 999_999_999))
                .secondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            0L.seconds,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0))
                .secondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            (-1L).seconds,
            (Date(1970, Month.JANUARY, 1) at Time(0, 59, 59, 999_999_999))
                .secondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )
    }

    @Test
    fun `millisecondsSinceUnixEpochAt returns the number of milliseconds since the unix epoch`() {
        assertEquals(
            1L.milliseconds,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 0, 1_999_999))
                .millisecondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            1L.milliseconds,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 0, 1_000_000))
                .millisecondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            0L.milliseconds,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 0, 999_999))
                .millisecondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            0L.milliseconds,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0))
                .millisecondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            (-1L).milliseconds,
            (Date(1970, Month.JANUARY, 1) at Time(0, 59, 59, 999_999_999))
                .millisecondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )
    }

    @Test
    fun `millisecondOfUnixEpochAt returns the millisecond of the unix epoch`() {
        assertEquals(
            1L,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 0, 1_999_999))
                .millisecondOfUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            1L,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 0, 1_000_000))
                .millisecondOfUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            0L,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0, 0, 999_999))
                .millisecondOfUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            0L,
            (Date(1970, Month.JANUARY, 1) at Time(1, 0))
                .millisecondOfUnixEpochAt(1.hours.asUtcOffset())
        )

        assertEquals(
            (-1L).milliseconds,
            (Date(1970, Month.JANUARY, 1) at Time(0, 59, 59, 999_999_999))
                .millisecondsSinceUnixEpochAt(1.hours.asUtcOffset())
        )
    }

    @Test
    fun `add period of zero`() {
        assertEquals(
            DateTime(2016, Month.FEBRUARY, 29, 13, 0),
            DateTime(2016, Month.FEBRUARY, 29, 13, 0) + Period.ZERO
        )
    }

    @Test
    fun `adding a period adds years then months then days`() {
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
    fun `subtracting a period subtracts years then months then days`() {
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
    fun `adding a kotlin duration of zero doesn't affect the time`() {
        val dateTime = Date(2010, Month.JULY, 4) at Time(18, 0)
        assertEquals(dateTime, dateTime + kotlin.time.Duration.ZERO)
    }

    @Test
    fun `throws an exception when adding an infinite kotlin duration`() {
        val dateTime = Date(2010, Month.JULY, 4) at Time(18, 0)
        assertFailsWith<IllegalArgumentException> { dateTime + kotlin.time.Duration.INFINITE }
    }

    @Test
    fun `add a kotlin duration`() {
        assertEquals(
            Date(2010, Month.JULY, 4) at Time(18, 0),
            (Date(2010, Month.JULY, 3) at Time.MAX) + (18.kotlinHours + 1.kotlinNanoseconds)
        )
    }

    @Test
    fun `subtracting a kotlin duration of zero doesn't affect the time`() {
        val dateTime = Date(2010, Month.JULY, 4) at Time(18, 0)
        assertEquals(dateTime, dateTime - kotlin.time.Duration.ZERO)
    }

    @Test
    fun `throws an exception when subtracting an infinite kotlin duration`() {
        val dateTime = Date(2010, Month.JULY, 4) at Time(18, 0)
        assertFailsWith<IllegalArgumentException> { dateTime - kotlin.time.Duration.INFINITE }
    }

    @Test
    fun `subtract a kotlin duration`() {
        assertEquals(
            Date(2010, Month.JULY, 4) at Time(18, 0),
            (Date(2010, Month.JULY, 5) at Time(1, 0, 0, 1)) -
                (7.kotlinHours + 1.kotlinNanoseconds)
        )
    }

    @Test
    fun `throws an exception when addition puts the date-time out of range`() {
        listOf(
            { DateTime.MIN + (-1).centuries },
            { DateTime.MIN + (-1).decades },
            { DateTime.MIN + (-1).years },
            { DateTime.MIN + (-1).months },
            { DateTime.MIN + (-1).weeks },
            { DateTime.MIN + (-1).days },
            { DateTime.MIN + (-1).hours },
            { DateTime.MIN + (-1).minutes },
            { DateTime.MIN + (-1).seconds },
            { DateTime.MIN + (-1).milliseconds },
            { DateTime.MIN + (-1).microseconds },
            { DateTime.MIN + (-1).nanoseconds },
            { DateTime.MAX + 1.centuries },
            { DateTime.MAX + 1.decades },
            { DateTime.MAX + 1.years },
            { DateTime.MAX + 1.months },
            { DateTime.MAX + 1.weeks },
            { DateTime.MAX + 1.days },
            { DateTime.MAX + 1.hours },
            { DateTime.MAX + 1.minutes },
            { DateTime.MAX + 1.seconds },
            { DateTime.MAX + 1.milliseconds },
            { DateTime.MAX + 1.microseconds },
            { DateTime.MAX + 1.nanoseconds },
            { DateTime.MIN + Long.MIN_VALUE.hours },
            { DateTime.MIN + Long.MIN_VALUE.minutes },
            { DateTime.MIN + Long.MIN_VALUE.seconds },
            { DateTime.MIN + Long.MIN_VALUE.milliseconds },
            { DateTime.MIN + Long.MIN_VALUE.microseconds },
            { DateTime.MIN + Long.MIN_VALUE.nanoseconds }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `throws an exception when subtraction puts the date-time out of range`() {
        listOf(
            { DateTime.MIN - 1.centuries },
            { DateTime.MIN - 1.decades },
            { DateTime.MIN - 1.years },
            { DateTime.MIN - 1.months },
            { DateTime.MIN - 1.weeks },
            { DateTime.MIN - 1.days },
            { DateTime.MIN - 1.hours },
            { DateTime.MIN - 1.minutes },
            { DateTime.MIN - 1.seconds },
            { DateTime.MIN - 1.milliseconds },
            { DateTime.MIN - 1.microseconds },
            { DateTime.MIN - 1.nanoseconds },
            { DateTime.MAX - (-1).centuries },
            { DateTime.MAX - (-1).decades },
            { DateTime.MAX - (-1).years },
            { DateTime.MAX - (-1).months },
            { DateTime.MAX - (-1).weeks },
            { DateTime.MAX - (-1).days },
            { DateTime.MAX - (-1).hours },
            { DateTime.MAX - (-1).minutes },
            { DateTime.MAX - (-1).seconds },
            { DateTime.MAX - (-1).milliseconds },
            { DateTime.MAX - (-1).microseconds },
            { DateTime.MAX - (-1).nanoseconds },
            { DateTime.MAX - Long.MIN_VALUE.hours },
            { DateTime.MAX - Long.MIN_VALUE.minutes },
            { DateTime.MAX - Long.MIN_VALUE.seconds },
            { DateTime.MAX - Long.MIN_VALUE.milliseconds },
            { DateTime.MAX - Long.MIN_VALUE.microseconds },
            { DateTime.MAX - Long.MIN_VALUE.nanoseconds }
        ).forEach {
            assertFailsWith<DateTimeException> { it() }
        }
    }

    @Test
    fun `adding or subtracting zero centuries has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.centuries)
        assertEquals(date, date - 0.centuries)
    }

    @Test
    fun `add positive centuries`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(2469, Month.DECEMBER, 1) at Time.NOON,
            date + 5.centuries
        )
    }

    @Test
    fun `add negative centuries`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1469, Month.DECEMBER, 1) at Time.NOON,
            date + (-5).centuries
        )
    }

    @Test
    fun `subtract positive centuries`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1469, Month.DECEMBER, 1) at Time.NOON,
            date - 5.centuries
        )
    }

    @Test
    fun `subtract negative centuries`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(2469, Month.DECEMBER, 1) at Time.NOON,
            date - (-5).centuries
        )
    }

    @Test
    fun `adding or subtracting zero decades has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.decades)
        assertEquals(date, date - 0.decades)
    }

    @Test
    fun `add positive decades`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(2019, Month.DECEMBER, 1) at Time.NOON,
            date + 5.decades
        )
    }

    @Test
    fun `add negative decades`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1919, Month.DECEMBER, 1) at Time.NOON,
            date + (-5).decades
        )
    }

    @Test
    fun `subtract positive decades`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1919, Month.DECEMBER, 1) at Time.NOON,
            date - 5.decades
        )
    }

    @Test
    fun `subtract negative decades`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(2019, Month.DECEMBER, 1) at Time.NOON,
            date - (-5).decades
        )
    }

    @Test
    fun `adding or subtracting zero years has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.years)
        assertEquals(date, date - 0.years)
    }

    @Test
    fun `add positive years`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1974, Month.DECEMBER, 1) at Time.NOON,
            date + 5.years
        )
    }

    @Test
    fun `add negative years`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1964, Month.DECEMBER, 1) at Time.NOON,
            date + (-5).years
        )
    }

    @Test
    fun `subtract positive years`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1964, Month.DECEMBER, 1) at Time.NOON,
            date - 5.years
        )
    }

    @Test
    fun `subtract negative years`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1974, Month.DECEMBER, 1) at Time.NOON,
            date - (-5).years
        )
    }

    @Test
    fun `adding or subtracting zero months has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.months)
        assertEquals(date, date - 0.months)
    }

    @Test
    fun `add positive months`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1970, Month.MAY, 1) at Time.NOON,
            date + 5.months
        )
    }

    @Test
    fun `add negative months`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.JULY, 1) at Time.NOON,
            date + (-5).months
        )
    }

    @Test
    fun `subtract positive months`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.JULY, 1) at Time.NOON,
            date - 5.months
        )
    }

    @Test
    fun `subtract negative months`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1970, Month.MAY, 1) at Time.NOON,
            date - (-5).months
        )
    }

    @Test
    fun `adding or subtracting zero weeks has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.weeks)
        assertEquals(date, date - 0.weeks)
    }

    @Test
    fun `add positive weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1970, Month.JANUARY, 5) at Time.NOON,
            date + 5.weeks
        )
    }

    @Test
    fun `add negative weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.OCTOBER, 27) at Time.NOON,
            date + (-5).weeks
        )
    }

    @Test
    fun `subtract positive weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.OCTOBER, 27) at Time.NOON,
            date - 5.weeks
        )
    }

    @Test
    fun `subtract negative weeks`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1970, Month.JANUARY, 5) at Time.NOON,
            date - (-5).weeks
        )
    }

    @Test
    fun `adding or subtracting zero days has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.days)
        assertEquals(date, date - 0.days)
    }

    @Test
    fun `add positive days`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.DECEMBER, 6) at Time.NOON,
            date + 5.days
        )
    }

    @Test
    fun `add negative days`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.NOVEMBER, 26) at Time.NOON,
            date + (-5).days
        )
    }

    @Test
    fun `subtract positive days`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.NOVEMBER, 26) at Time.NOON,
            date - 5.days
        )
    }

    @Test
    fun `subtract negative days`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON

        assertEquals(
            Date(1969, Month.DECEMBER, 6) at Time.NOON,
            date - (-5).days
        )
    }

    @Test
    fun `adding or subtracting zero hours has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.hours)
        assertEquals(date, date - 0.hours)
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
    fun `adding or subtracting zero minutes has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.minutes)
        assertEquals(date, date - 0.minutes)
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

        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time(23, 40),
            (Date(1969, Month.DECEMBER, 1) at Time(23, 40)) + 1.days.inMinutes
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
    fun `adding or subtracting zero seconds has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.seconds)
        assertEquals(date, date - 0.seconds)
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

        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time(23, 40),
            (Date(1969, Month.DECEMBER, 1) at Time(23, 40)) + 1.days.inSeconds
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
    fun `adding or subtracting zero milliseconds has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.milliseconds)
        assertEquals(date, date - 0.milliseconds)
    }

    @Test
    fun `add milliseconds`() {
        assertEquals(
            Date(1970, Month.JANUARY, 1) at Time.MIDNIGHT,
            (Date(1969, Month.DECEMBER, 31) at
                Time(23, 59, 59, 999_000_000)) + 1.milliseconds
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(0, 59, 59, 999_999_999),
            (Date(1969, Month.DECEMBER, 31) at Time.MAX) + 1.hours.inMilliseconds
        )

        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time(23, 40),
            (Date(1969, Month.DECEMBER, 1) at Time(23, 40)) + 1.days.inMilliseconds
        )
    }

    @Test
    fun `subtract milliseconds`() {
        assertEquals(
            DateTime(2018, Month.MARCH, 11, 23, 59, 0, 999_000_000),
            DateTime(2018, Month.MARCH, 12, 0, 1, 1, 1_000_000) -
                (2.minutes + 2.milliseconds)
        )
    }

    @Test
    fun `adding or subtracting zero microseconds has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.microseconds)
        assertEquals(date, date - 0.microseconds)
    }

    @Test
    fun `add microseconds`() {
        assertEquals(
            Date(1970, Month.JANUARY, 1) at Time.MIDNIGHT,
            (Date(1969, Month.DECEMBER, 31) at
                Time(23, 59, 59, 999_999_000)) + 1.microseconds
        )

        assertEquals(
            Date(1970, Month.JANUARY, 1) at
                Time(0, 59, 59, 999_999_999),
            (Date(1969, Month.DECEMBER, 31) at Time.MAX) + 1.hours.inMicroseconds
        )

        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time(23, 40),
            (Date(1969, Month.DECEMBER, 1) at Time(23, 40)) + 1.days.inMicroseconds
        )
    }

    @Test
    fun `subtract microseconds`() {
        assertEquals(
            DateTime(2018, Month.MARCH, 11, 23, 59, 0, 999_999_000),
            DateTime(2018, Month.MARCH, 12, 0, 1, 1, 1_000) -
                (2.minutes + 2.microseconds)
        )
    }

    @Test
    fun `adding or subtracting zero nanoseconds has no effect`() {
        val date = Date(1969, Month.DECEMBER, 1) at Time.NOON
        assertEquals(date, date + 0.nanoseconds)
        assertEquals(date, date - 0.nanoseconds)
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

        assertEquals(
            Date(1969, Month.DECEMBER, 2) at Time(23, 40),
            (Date(1969, Month.DECEMBER, 1) at Time(23, 40)) + 1.days.inNanoseconds
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
    fun `toString returns an ISO-8601 extended calendar date time`() {
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
    fun `String_toDateTime throws an exception when the format is mixed basic and extended`() {
        assertFailsWith<DateTimeParseException> { "20000101 00:23".toDateTime() }
        assertFailsWith<DateTimeParseException> { "2002-02-01T0023".toDateTime() }
    }

    @Test
    fun `String_toDateTime throws an exception when given an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toDateTime() }
    }

    @Test
    fun `String_toDateTime throws an exception when the parser can't supply required fields`() {
        assertFailsWith<DateTimeParseException> { "08:00".toDateTime(DateTimeParsers.Iso.UTC_OFFSET) }
        assertFailsWith<DateTimeParseException> { "08:00".toDateTime(DateTimeParsers.Iso.TIME) }
        assertFailsWith<DateTimeParseException> { "2009-10-08".toDateTime(DateTimeParsers.Iso.DATE) }
    }

    @Test
    fun `String_toDateTime parses valid ISO-8601 extended calendar date strings by default`() {
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
    fun `String_toDateTime parses valid ISO-8601 basic calendar date strings`() {
        assertEquals(
            DateTime(2019, Month.MARCH, 23, 2, 30),
            "20190323T0230".toDateTime(DateTimeParsers.Iso.Basic.DATE_TIME)
        )
    }

    @Test
    fun `String_toDateTime parses valid ISO-8601 ordinal date strings with custom parser`() {
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
