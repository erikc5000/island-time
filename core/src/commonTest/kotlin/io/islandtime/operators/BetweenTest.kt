@file:Suppress("PackageDirectoryMismatch")

package io.islandtime

import io.islandtime.measures.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BetweenTest : AbstractIslandTimeTest() {
    @Test
    fun `returns zero when the start and end date are the same`() {
        listOf(
            Date(2200, Month.JULY, 15),
            Date(2019, Month.MAY, 1),
            Date(1970, Month.JANUARY, 1),
            Date(1969, Month.MAY, 1)
        ).forEach { date ->
            assertEquals(0.centuries, Centuries.between(date, date), date.toString())
            assertEquals(0.decades, Decades.between(date, date), date.toString())
            assertEquals(0.years, Years.between(date, date), date.toString())
            assertEquals(0.months, Months.between(date, date), date.toString())
            assertEquals(0.weeks, Weeks.between(date, date), date.toString())
            assertEquals(0.days, Days.between(date, date), date.toString())
        }
    }

    @Test
    fun `years between dates in positive progression`() {
        assertEquals(
            0.years,
            Years.between(Date(2019, Month.JULY, 15), Date(2020, Month.JULY, 14))
        )

        assertEquals(
            1.years,
            Years.between(Date(2019, Month.JULY, 15), Date(2020, Month.JULY, 15))
        )
    }

    @Test
    fun `years between dates in negative progression`() {
        assertEquals(
            0.years,
            Years.between(Date(2020, Month.JULY, 15), Date(2019, Month.JULY, 16))
        )

        assertEquals(
            (-1).years,
            Years.between(Date(2020, Month.AUGUST, 15), Date(2019, Month.JULY, 15))
        )
    }

    @Test
    fun `months between dates in positive progression`() {
        assertEquals(
            0.months,
            Months.between(Date(2019, Month.JULY, 15), Date(2019, Month.AUGUST, 14))
        )

        assertEquals(
            1.months,
            Months.between(Date(2019, Month.JULY, 15), Date(2019, Month.AUGUST, 15))
        )

        assertEquals(
            13.months,
            Months.between(Date(2019, Month.JULY, 15), Date(2020, Month.AUGUST, 15))
        )
    }

    @Test
    fun `months between dates in negative progression`() {
        assertEquals(
            0.months,
            Months.between(Date(2019, Month.AUGUST, 14), Date(2019, Month.JULY, 15))
        )

        assertEquals(
            (-1).months,
            Months.between(Date(2019, Month.AUGUST, 15), Date(2019, Month.JULY, 15))
        )

        assertEquals(
            (-13).months,
            Months.between(Date(2020, Month.AUGUST, 15), Date(2019, Month.JULY, 15))
        )
    }

    @Test
    fun `weeks between dates in positive progression`() {
        assertEquals(
            4.weeks,
            Weeks.between(Date(2019, Month.MAY, 1), Date(2019, Month.JUNE, 3))
        )

        assertEquals(
            5.weeks,
            Weeks.between(Date(1969, Month.MAY, 1), Date(1969, Month.JUNE, 5))
        )
    }

    @Test
    fun `weeks between dates in negative progression`() {
        assertEquals(
            (-4L).weeks,
            Weeks.between(Date(2019, Month.JUNE, 3), Date(2019, Month.MAY, 1))
        )

        assertEquals(
            (-5L).weeks,
            Weeks.between(Date(1969, Month.JUNE, 5), Date(1969, Month.MAY, 1))
        )
    }

    @Test
    fun `days between dates in positive progression`() {
        assertEquals(
            33.days,
            Days.between(Date(2019, Month.MAY, 1), Date(2019, Month.JUNE, 3))
        )

        assertEquals(
            33.days,
            Days.between(Date(1969, Month.MAY, 1), Date(1969, Month.JUNE, 3))
        )
    }

    @Test
    fun `days between dates in negative progression`() {
        assertEquals(
            (-16L).days,
            Days.between(Date(2019, Month.MAY, 1), Date(2019, Month.APRIL, 15))
        )

        assertEquals(
            (-16L).days,
            Days.between(Date(1969, Month.MAY, 1), Date(1969, Month.APRIL, 15))
        )

        assertEquals(
            (-20L).days,
            Days.between(Date(1970, Month.JANUARY, 4), Date(1969, Month.DECEMBER, 15))
        )
    }

    @Test
    fun `hours between instants`() {
        assertEquals(
            0.hours,
            Hours.between(Instant(0.seconds, 1.nanoseconds), Instant(3600.seconds))
        )

        assertEquals(
            0.hours,
            Hours.between(Instant(3600.seconds), Instant(0.seconds, 1.nanoseconds))
        )

        assertEquals(
            1.hours,
            Hours.between(Instant.UNIX_EPOCH, Instant(3600.seconds))
        )
    }

    @Test
    fun `minutes between instants`() {
        assertEquals(
            0.minutes,
            Minutes.between(Instant(0.seconds, 1.nanoseconds), Instant(60.seconds))
        )

        assertEquals(
            0.minutes,
            Minutes.between(Instant(60.seconds), Instant(0.seconds, 1.nanoseconds))
        )

        assertEquals(
            1.minutes,
            Minutes.between(Instant.UNIX_EPOCH, Instant(60.seconds))
        )
    }

    @Test
    fun `seconds between instants`() {
        assertEquals(
            0.seconds,
            Seconds.between(Instant(0.seconds, 1.nanoseconds), Instant(1.seconds))
        )

        assertEquals(
            0.seconds,
            Seconds.between(Instant(1.seconds), Instant(0.seconds, 1.nanoseconds))
        )

        assertEquals(
            0.seconds,
            Seconds.between(Instant(0.seconds, 999_999_999.nanoseconds), Instant.UNIX_EPOCH)
        )

        assertEquals(
            0.seconds,
            Seconds.between(Instant.UNIX_EPOCH, Instant(0.seconds, 999_999_999.nanoseconds))
        )

        assertEquals(
            1.seconds,
            Seconds.between(Instant.UNIX_EPOCH, Instant(1.seconds))
        )

        assertEquals(
            (-1L).seconds,
            Seconds.between(Instant(1.seconds), Instant.UNIX_EPOCH)
        )

        assertEquals(
            1.seconds,
            Seconds.between(
                Instant(0.seconds, 500_000_000.nanoseconds),
                Instant(1.seconds, 500_000_000.nanoseconds)
            )
        )
        assertEquals(
            1.seconds,
            Seconds.between(
                Instant(0.seconds, 500_000_000.nanoseconds),
                Instant(2.seconds, 499_999_999.nanoseconds)
            )
        )
    }

    @Test
    fun `milliseconds between instants`() {
        assertEquals(
            0.milliseconds,
            Milliseconds.between(
                Instant(0.seconds, 1.nanoseconds),
                Instant(0.seconds, 1_000_000.nanoseconds)
            )
        )

        assertEquals(
            0.milliseconds,
            Milliseconds.between(
                Instant(0.seconds, 1_000_000.nanoseconds),
                Instant(0.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1.milliseconds,
            Milliseconds.between(
                Instant.UNIX_EPOCH,
                Instant(0.seconds, 1_000_000.nanoseconds)
            )
        )
    }

    @Test
    fun `microseconds between instants`() {
        assertEquals(
            0.microseconds,
            Microseconds.between(
                Instant(0.seconds, 1.nanoseconds),
                Instant(0.seconds, 1_000.nanoseconds)
            )
        )

        assertEquals(
            0.microseconds,
            Microseconds.between(
                Instant(0.seconds, 1_000.nanoseconds),
                Instant(0.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1.microseconds,
            Microseconds.between(Instant.UNIX_EPOCH, Instant(0.seconds, 1_000.nanoseconds))
        )
    }

    @Test
    fun `nanoseconds returns zero when both instants are the same`() {
        assertEquals(
            0.nanoseconds,
            Nanoseconds.between(
                Instant(1.seconds, 1.nanoseconds),
                Instant(1.seconds, 1.nanoseconds)
            )
        )
    }

    @Test
    fun `nanoseconds between instants`() {
        assertEquals(
            2.nanoseconds,
            Nanoseconds.between(
                Instant(0.seconds, (-1).nanoseconds),
                Instant(0.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            (-1_000_000_000L).nanoseconds,
            Nanoseconds.between(Instant.UNIX_EPOCH, Instant((-1L).seconds))
        )
    }

    @Test
    fun `throws an exception when the number of nanoseconds between time points results in overflow`() {
        assertFailsWith<ArithmeticException> { Nanoseconds.between(Instant.MIN, Instant.UNIX_EPOCH) }
        assertFailsWith<ArithmeticException> { Nanoseconds.between(Instant.UNIX_EPOCH, OffsetDateTime.MAX) }
    }

    @Test
    fun `centuries betwen years`() {
        assertEquals(0.centuries, Centuries.between(Year(2000), Year(2099)))
        assertEquals(1.centuries, Centuries.between(Year(2000), Year(2100)))
    }

    @Test
    fun `decades betwen years`() {
        assertEquals(0.decades, Decades.between(Year(2000), Year(2009)))
        assertEquals(1.decades, Decades.between(Year(2000), Year(2010)))
    }

    @Test
    fun `years betwen years`() {
        assertEquals(0.years, Years.between(Year(2000), Year(2000)))
        assertEquals(1.years, Years.between(Year(2000), Year(2001)))
        assertEquals((-1).years, Years.between(Year(2000), Year(1999)))
    }

    @Test
    fun `centuries between year-months`() {
        assertEquals(
            0.centuries,
            Centuries.between(YearMonth(2019, Month.JULY), YearMonth(2119, Month.JUNE))
        )

        assertEquals(
            1.centuries,
            Centuries.between(YearMonth(2019, Month.JULY), YearMonth(2119, Month.JULY))
        )
    }

    @Test
    fun `decades between year-months`() {
        assertEquals(
            0.decades,
            Decades.between(YearMonth(2019, Month.JULY), YearMonth(2029, Month.JUNE))
        )

        assertEquals(
            1.decades,
            Decades.between(YearMonth(2019, Month.JULY), YearMonth(2029, Month.JULY))
        )
    }

    @Test
    fun `years between year-months`() {
        assertEquals(
            0.years,
            Years.between(YearMonth(2019, Month.JULY), YearMonth(2020, Month.JUNE))
        )

        assertEquals(
            1.years,
            Years.between(YearMonth(2019, Month.JULY), YearMonth(2020, Month.JULY))
        )
    }

    @Test
    fun `months between the same year-month returns 0`() {
        listOf(
            YearMonth(2019, Month.JULY),
            YearMonth(0, Month.JANUARY),
            YearMonth(-1, Month.DECEMBER)
        ).forEach { yearMonth ->
            assertEquals(0.months, Months.between(yearMonth, yearMonth))
        }
    }

    @Test
    fun `months between year-months in positive progression`() {
        assertEquals(
            1.months,
            Months.between(YearMonth(2019, Month.JULY), YearMonth(2019, Month.AUGUST))
        )

        assertEquals(
            13.months,
            Months.between(YearMonth(2019, Month.JULY), YearMonth(2020, Month.AUGUST))
        )
    }

    @Test
    fun `months between year-months in negative progression`() {
        assertEquals(
            (-1).months,
            Months.between(YearMonth(2019, Month.AUGUST), YearMonth(2019, Month.JULY))
        )

        assertEquals(
            (-13).months,
            Months.between(YearMonth(2020, Month.AUGUST), YearMonth(2019, Month.JULY))
        )
    }
}
