package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.Month
import dev.erikchristensen.islandtime.interval.days
import dev.erikchristensen.islandtime.interval.weeks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DateDayProgressionTest {
    @Test
    fun `empty when end comes before start`() {
        val startDate = Date(2019, Month.JANUARY, 29)
        val endDate = Date(2019, Month.JANUARY, 28)
        val range = startDate..endDate

        assertEquals(startDate, range.first)
        assertEquals(startDate, range.start)
        assertEquals(endDate, range.last)
        assertEquals(endDate, range.endInclusive)
        assertEquals(1.days, range.step)
        assertEquals(0, range.count())
    }

    @Test
    fun `negative step causes an exception`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.JANUARY, 28)
        assertFailsWith<IllegalArgumentException> { startDate..endDate step (-1).days }
    }

    @Test
    fun `steps in increments of 1 day by default`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.JANUARY, 28)
        val range = startDate..endDate

        assertEquals(startDate, range.first)
        assertEquals(startDate, range.start)
        assertEquals(endDate, range.last)
        assertEquals(endDate, range.endInclusive)
        assertEquals(1.days, range.step)
        assertEquals(8, range.count())
    }

    @Test
    fun `downTo creates a reversed range`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.JANUARY, 28)
        val range = endDate downTo startDate

        assertEquals(endDate, range.first)
        assertEquals(startDate, range.last)
        assertEquals((-1).days, range.step)
        assertEquals(8, range.count())
    }

    @Test
    fun `reversed() creates a reserved progression`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.JANUARY, 28)
        val range = (startDate..endDate).reversed()

        assertEquals(endDate, range.first)
        assertEquals(startDate, range.last)
        assertEquals((-1).days, range.step)
        assertEquals(8, range.count())
    }

    @Test
    fun `steps in positive increments with dates after the unix epoch`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.JANUARY, 28)
        val progression = startDate..endDate step 2.days

        assertEquals(startDate, progression.first)
        assertEquals(Date(2019, Month.JANUARY, 27), progression.last)
        assertEquals(2.days, progression.step)
        assertEquals(4, progression.count())
    }

    @Test
    fun `steps in positive increments with dates prior to the unix epoch`() {
        val startDate = Date(1950, Month.JANUARY, 21)
        val endDate = Date(1950, Month.JANUARY, 28)
        val progression = startDate..endDate step 2.days

        assertEquals(startDate, progression.first)
        assertEquals(Date(1950, Month.JANUARY, 27), progression.last)
        assertEquals(2.days, progression.step)
        assertEquals(4, progression.count())
    }

    @Test
    fun `steps in positive increments with dates that pass through the unix epoch`() {
        val startDate = Date(1969, Month.DECEMBER, 31)
        val endDate = Date(1970, Month.JANUARY, 31)
        val progression = startDate..endDate step 1.weeks

        assertEquals(startDate, progression.first)
        assertEquals(Date(1970, Month.JANUARY, 28), progression.last)
        assertEquals(7.days, progression.step)
        assertEquals(5, progression.count())
    }
}