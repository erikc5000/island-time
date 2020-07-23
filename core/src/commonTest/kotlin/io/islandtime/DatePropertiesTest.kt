package io.islandtime

import io.islandtime.calendar.WeekSettings
import io.islandtime.measures.days
import io.islandtime.measures.weeks
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePropertiesTest : AbstractIslandTimeTest() {
    @Test
    fun `Date_isInLeapYear returns true in leap year`() {
        assertTrue { Date(2020, Month.JANUARY, 1).isInLeapYear }
    }

    @Test
    fun `Date_isInLeapYear returns false in common year`() {
        assertFalse { Date(2019, Month.JANUARY, 1).isInLeapYear }
    }

    @Test
    fun `Date_isLeapDay returns true only on February 29`() {
        assertTrue { Date(2020, Month.FEBRUARY, 29).isLeapDay }
        assertFalse { Date(2019, Month.FEBRUARY, 28).isLeapDay }
        assertFalse { Date(2019, Month.MARCH, 29).isLeapDay }
    }

    @Test
    fun `Date_lengthOfMonth returns the length in days of a date's month`() {
        assertEquals(29.days, Date(2020, Month.FEBRUARY, 29).lengthOfMonth)
        assertEquals(28.days, Date(2019, Month.FEBRUARY, 28).lengthOfMonth)
    }

    @Test
    fun `Date_lengthOfYear returns the length in days of a date's year`() {
        assertEquals(366.days, Date(2020, Month.FEBRUARY, 29).lengthOfYear)
        assertEquals(365.days, Date(2010, Month.MAY, 20).lengthOfYear)
    }

    @Test
    fun `Date_weekOfMonth with ISO week definition`() {
        listOf(
            Date(2008, 12, 31) to 5,
            Date(2009, 1, 1) to 1,
            Date(2009, 1, 4) to 1,
            Date(2009, 1, 5) to 2
        ).forEach { (date, week) ->
            assertEquals(week, date.weekOfMonth, date.toString())
        }
    }

    @Test
    fun `Date_weekOfMonth() with Sunday start`() {
        listOf(
            Date(2008, 12, 27) to 4,
            Date(2008, 12, 28) to 5,
            Date(2008, 12, 31) to 5,
            Date(2009, 1, 1) to 1,
            Date(2009, 1, 3) to 1,
            Date(2009, 1, 4) to 2
        ).forEach { (date, week) ->
            assertEquals(week, date.weekOfMonth(WeekSettings.SUNDAY_START), date.toString())
        }
    }

    @Test
    fun `Date_weekOfMonth() with custom week definition`() {
        listOf(
            Date(2008, 12, 28) to 4,
            Date(2008, 12, 29) to 5,
            Date(2008, 12, 31) to 5,
            Date(2009, 1, 1) to 0,
            Date(2009, 1, 4) to 0,
            Date(2009, 1, 5) to 1
        ).forEach { (date, week) ->
            assertEquals(
                week,
                date.weekOfMonth(WeekSettings(DayOfWeek.MONDAY, 5)),
                date.toString()
            )
        }
    }

    @Test
    fun `Date_weekOfYear with ISO week definition`() {
        listOf(
            Date(2008, 12, 31) to 53,
            Date(2009, 1, 1) to 1,
            Date(2009, 1, 4) to 1,
            Date(2009, 1, 5) to 2
        ).forEach { (date, week) ->
            assertEquals(week, date.weekOfYear, date.toString())
        }
    }

    @Test
    fun `Date_weekOfYear() with Sunday start`() {
        listOf(
            Date(2008, 12, 27) to 52,
            Date(2008, 12, 28) to 53,
            Date(2008, 12, 31) to 53,
            Date(2009, 1, 1) to 1,
            Date(2009, 1, 3) to 1,
            Date(2009, 1, 4) to 2
        ).forEach { (date, week) ->
            assertEquals(week, date.weekOfYear(WeekSettings.SUNDAY_START), date.toString())
        }
    }

    @Test
    fun `Date_weekOfYear() with custom week definition`() {
        listOf(
            Date(2008, 12, 28) to 52,
            Date(2008, 12, 29) to 53,
            Date(2008, 12, 31) to 53,
            Date(2009, 1, 1) to 0,
            Date(2009, 1, 4) to 0,
            Date(2009, 1, 5) to 1
        ).forEach { (date, week) ->
            assertEquals(
                week,
                date.weekOfYear(WeekSettings(DayOfWeek.MONDAY, 5)),
                date.toString()
            )
        }
    }

    @Test
    fun `ISO week date`() {
        listOf(
            Date(2005, 1, 1) to Triple(2004, 53, 6),
            Date(2005, 1, 2) to Triple(2004, 53, 7),
            Date(2005, 12, 31) to Triple(2005, 52, 6),
            Date(2006, 1, 1) to Triple(2005, 52, 7),
            Date(2006, 1, 2) to Triple(2006, 1, 1),
            Date(2006, 12, 31) to Triple(2006, 52, 7),
            Date(2007, 1, 1) to Triple(2007, 1, 1),
            Date(2007, 12, 30) to Triple(2007, 52, 7),
            Date(2007, 12, 31) to Triple(2008, 1, 1),
            Date(2008, 1, 1) to Triple(2008, 1, 2),
            Date(2008, 12, 28) to Triple(2008, 52, 7),
            Date(2008, 12, 29) to Triple(2009, 1, 1),
            Date(2008, 12, 30) to Triple(2009, 1, 2),
            Date(2008, 12, 31) to Triple(2009, 1, 3),
            Date(2009, 1, 1) to Triple(2009, 1, 4),
            Date(2009, 12, 31) to Triple(2009, 53, 4),
            Date(2010, 1, 1) to Triple(2009, 53, 5),
            Date(2010, 1, 2) to Triple(2009, 53, 6),
            Date(2010, 1, 3) to Triple(2009, 53, 7),
            Date(2010, 1, 4) to Triple(2010, 1, 1)
        ).forEach { (date, weekDate) ->
            assertEquals(weekDate, date.toWeekDate(::Triple), "toWeekDate(): $date")

            val (year, week) = weekDate
            assertEquals(year, date.weekBasedYear, "weekBasedYear: $date")
            assertEquals(week, date.weekOfWeekBasedYear, "weekOfWeekBasedYear: $date")
        }
    }

    @Test
    fun `week date with Sunday start`() {
        listOf(
            Date(2016, 12, 30) to Pair(2016, 53),
            Date(2016, 12, 31) to Pair(2016, 53),
            Date(2017, 1, 1) to Pair(2017, 1),
            Date(2017, 1, 2) to Pair(2017, 1),
            Date(2017, 1, 7) to Pair(2017, 1),
            Date(2017, 1, 8) to Pair(2017, 2),
            Date(2017, 12, 30) to Pair(2017, 52),
            Date(2017, 12, 31) to Pair(2018, 1),
            Date(2018, 1, 6) to Pair(2018, 1),
            Date(2018, 12, 29) to Pair(2018, 52),
            Date(2018, 12, 30) to Pair(2019, 1),
            Date(2019, 1, 5) to Pair(2019, 1),
            Date(2019, 1, 6) to Pair(2019, 2),
            Date(2019, 12, 28) to Pair(2019, 52),
            Date(2019, 12, 29) to Pair(2020, 1),
            Date(2020, 1, 5) to Pair(2020, 2)
        ).forEach { (date, yearWeek) ->
            assertEquals(
                yearWeek,
                Pair(
                    date.weekBasedYear(WeekSettings.SUNDAY_START),
                    date.weekOfWeekBasedYear(WeekSettings.SUNDAY_START)
                ),
                date.toString()
            )
        }
    }

    @Test
    fun `length of week-based year`() {
        listOf(
            Date(2004, 1, 1) to 53.weeks,
            Date(2005, 1, 1) to 53.weeks,
            Date(2005, 1, 3) to 52.weeks,
            Date(2006, 1, 2) to 52.weeks,
            Date(2007, 1, 1) to 52.weeks,
            Date(2007, 12, 31) to 52.weeks,
            Date(2009, 1, 1) to 53.weeks,
            Date(2010, 1, 1) to 53.weeks
        ).forEach { (date, expectedLength) ->
            assertEquals(expectedLength, date.lengthOfWeekBasedYear, date.toString())
        }
    }
}