package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.Month
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.interval.months
import dev.erikchristensen.islandtime.interval.years
import kotlin.test.*

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
    fun `months property returns 0 when range is empty`() {
        assertEquals(0.months, DateRange.EMPTY.months)
    }

    @Test
    fun `months property returns the number of months in a range`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 19)
        assertEquals(0.months, range1.months)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 20)
        assertEquals(1.months, range2.months)
    }

    @Test
    fun `years property returns 0 when range is empty`() {
        assertEquals(0.years, DateRange.EMPTY.years)
    }

    @Test
    fun `years property returns the number of months in a range`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 20)
        assertEquals(0.years, range1.years)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2019, Month.FEBRUARY, 20)
        assertEquals(1.years, range2.years)
    }

    @Test
    fun `asPeriod() returns a period of zero when range is empty`() {
        assertEquals(Period.ZERO, DateRange.EMPTY.asPeriod())
    }

    @Test
    fun `asPeriod() returns a period for non-empty ranges`() {
        val start = Date(2018, Month.FEBRUARY, 20)
        val end = Date(2019, Month.MARCH, 20)
        assertEquals(periodOf(1.years, 1.months), (start..end).asPeriod())
    }
}