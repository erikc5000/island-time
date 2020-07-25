package io.islandtime

import io.islandtime.calendar.WeekSettings
import io.islandtime.measures.weeks
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.TestData
import kotlin.test.Test
import kotlin.test.assertEquals

class DatePropertiesTest : AbstractIslandTimeTest() {
    @Test
    fun `Date_weekOfMonth with ISO week definition`() {
        listOf(
            Date(2008, 12, 31) to 5,
            Date(2009, 1, 1) to 1,
            Date(2009, 1, 4) to 1,
            Date(2009, 1, 5) to 2,
            Date(2020, 5, 31) to 4
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
            Date(2009, 1, 4) to 2,
            Date(2020, 5, 31) to 6
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
            Date(2009, 1, 5) to 1,
            Date(2020, 5, 31) to 4
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
        TestData.isoWeekDates.forEach { (date, weekDate) ->
            val (year, week) = weekDate

            assertEquals(
                Pair(year, week),
                Pair(date.weekBasedYear, date.weekOfWeekBasedYear),
                date.toString()
            )
        }
    }

    @Test
    fun `week date with Sunday start`() {
        val settings = WeekSettings.SUNDAY_START

        TestData.sundayStartWeekDates.forEach { (date, weekDate) ->
            val (year, week) = weekDate

            assertEquals(
                Pair(year, week),
                Pair(date.weekBasedYear(settings), date.weekOfWeekBasedYear(settings)),
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