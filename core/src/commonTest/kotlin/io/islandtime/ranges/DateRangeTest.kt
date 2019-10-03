package io.islandtime.ranges

import io.islandtime.Date
import io.islandtime.Month
import io.islandtime.measures.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateRangeTest {
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

        assertFalse { Date(2018, Month.DECEMBER, 24) in startDate..endDate }
        assertFalse { Date(2019, Month.JANUARY, 13) in startDate..endDate }
    }

    @Test
    fun `contains() returns false for null dates`() {
        val startDate = Date(2018, Month.DECEMBER, 25)
        val endDate = Date(2019, Month.JANUARY, 12)

        assertFalse { null in startDate..endDate }
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
    }

    @Test
    fun `random() returns a date within range`() {
        val range = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 25)
        val randomDate = range.random()
        assertTrue { randomDate in range }
    }

    @Test
    fun `days property returns 0 when range is empty`() {
        assertEquals(0L.days, DateRange.EMPTY.days)
    }

    @Test
    fun `days property returns 1 when the start and end date are the same`() {
        val date = Date(2019, Month.JUNE, 1)
        assertEquals(1L.days, (date..date).days)
    }

    @Test
    fun `days property returns the expected number of days in a non-empty range`() {
        val start = Date(2018, Month.FEBRUARY, 1)
        val end = Date(2018, Month.FEBRUARY, 28)
        assertEquals(28L.days, (start..end).days)
    }

    @Test
    fun `months property returns 0 when range is empty`() {
        assertEquals(0.months, DateRange.EMPTY.months)
    }

    @Test
    fun `months property returns the expected number of months in a non-empty range`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 18)
        assertEquals(0.months, range1.months)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 19)
        assertEquals(1.months, range2.months)
    }

    @Test
    fun `years property returns 0 when range is empty`() {
        assertEquals(0.years, DateRange.EMPTY.years)
    }

    @Test
    fun `years property returns the expected number of years in a non-empty range`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2019, Month.FEBRUARY, 18)
        assertEquals(0.years, range1.years)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2019, Month.FEBRUARY, 19)
        assertEquals(1.years, range2.years)
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