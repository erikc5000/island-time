package io.islandtime.date

import io.islandtime.Month
import io.islandtime.interval.decades
import io.islandtime.interval.months
import io.islandtime.interval.years
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DateMonthProgressionTest {
    @Test
    fun `empty when end comes before start`() {
        val startDate = Date(2019, Month.JANUARY, 29)
        val endDate = Date(2018, Month.JANUARY, 28)
        val range = DateMonthProgression.fromClosedRange(startDate, endDate, 1.months)

        assertEquals(startDate, range.first)
        assertEquals(endDate, range.last)
        assertEquals(1.months, range.step)
        assertEquals(0, range.count())
    }

    @Test
    fun `negative step causes an exception`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.FEBRUARY, 28)
        assertFailsWith<IllegalArgumentException> { startDate..endDate step (-1).months }
    }

    @Test
    fun `steps month progression on first of month`() {
        val startOfThisYear = Date(2018, Month.JANUARY, 1)
        val startOfNextYear = startOfThisYear + 1.years
        val progression = startOfThisYear until startOfNextYear step 1.months

        assertEquals(startOfThisYear, progression.first)
        assertEquals(Date(2018, Month.DECEMBER, 1), progression.last)
        assertEquals(1.months, progression.step)
        assertEquals(12, progression.count())
    }

    @Test
    fun `month progression on last of month`() {
        val startOfThisYear = Date(2018, Month.JANUARY, 31)
        val febOfNextYear = startOfThisYear + 1.years + 1.months
        val progression = startOfThisYear..febOfNextYear step 1.months

        assertEquals(startOfThisYear, progression.first)
        assertEquals(febOfNextYear, progression.last)
        assertEquals(1.months, progression.step)
        assertEquals(14, progression.count())
    }

    @Test
    fun `month progression with positive step`() {
        val start = Date(2018, Month.JANUARY, 31)
        val end = Date(2018, Month.APRIL, 30)
        val progression = start..end step 2.months

        assertEquals(start, progression.first)
        assertEquals(Date(2018, Month.MARCH, 31), progression.last)
        assertEquals(2.months, progression.step)
        assertEquals(
            listOf(start, Date(2018, Month.MARCH, 31)),
            progression.toList()
        )
    }

    @Test
    fun `month progression with negative step`() {
        val start = Date(2018, Month.JANUARY, 31)
        val end = Date(2018, Month.APRIL, 30)
        val progression = end downTo start step 2.months

        assertEquals(end, progression.first)
        assertEquals(Date(2018, Month.FEBRUARY, 28), progression.last)
        assertEquals((-2).months, progression.step)
        assertEquals(
            listOf(end, Date(2018, Month.FEBRUARY, 28)),
            progression.toList()
        )
    }

    @Test
    fun `year progression with positive step`() {
        val start = Date(2000, Month.JANUARY, 31)
        val end = Date(2010, Month.APRIL, 30)
        val progression = start..end step 1.years

        assertEquals(start, progression.first)
        assertEquals(Date(2010, Month.JANUARY, 31), progression.last)
        assertEquals(12.months, progression.step)
        assertEquals(11, progression.count())
    }

    @Test
    fun `year progression with negative step`() {
        val start = Date(2000, Month.FEBRUARY, 29)
        val end = Date(1989, Month.APRIL, 30)
        val progression = start downTo end step 1.decades

        assertEquals(start, progression.first)
        assertEquals(Date(1990, Month.FEBRUARY, 28), progression.last)
        assertEquals((-120).months, progression.step)
        assertEquals(
            listOf(start, Date(1990, Month.FEBRUARY, 28)),
            progression.toList()
        )
    }

    @Test
    fun `reversed() creates a reserved progression`() {
        val start = Date(2018, Month.JANUARY, 31)
        val end = Date(2018, Month.APRIL, 30)
        val progression = (start..end step 1.months).reversed()

        assertEquals(end, progression.first)
        assertEquals(Date(2018, Month.FEBRUARY, 28), progression.last)
        assertEquals((-1).months, progression.step)
        assertEquals(
            listOf(
                end,
                Date(2018, Month.MARCH, 30),
                Date(2018, Month.FEBRUARY, 28)
            ),
            progression.toList()
        )
    }
}