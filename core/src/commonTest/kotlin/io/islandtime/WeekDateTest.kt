package io.islandtime

import io.islandtime.calendar.WeekSettings.Companion.SUNDAY_START
import io.islandtime.locale.toLocale
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.TestData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WeekDateTest : AbstractIslandTimeTest() {
    @Suppress("PrivatePropertyName")
    private val en_US = "en-US".toLocale()

    @Test
    fun `Date_toWeekDate() converts to ISO week date`() {
        TestData.isoWeekDates.forEach { (date, weekDate) ->
            assertEquals(weekDate, date.toWeekDate(::Triple), date.toString())
        }
    }

    @Test
    fun `Date_toWeekDate() converts to week date with Sunday start week definition`() {
        TestData.sundayStartWeekDates.forEach { (date, weekDate) ->
            assertEquals(weekDate, date.toWeekDate(SUNDAY_START, ::Triple), message = "$date (SUNDAY_START)")
            assertEquals(weekDate, date.toWeekDate(en_US, ::Triple), message = "$date (en-US)")
        }
    }

    @Test
    fun `Date_fromWeekDate() throws an exception when year is out of range`() {
        assertFailsWith<DateTimeException> { Date.fromWeekDate(Year.MIN_VALUE - 1, 52, 1) }
        assertFailsWith<DateTimeException> { Date.fromWeekDate(Year.MAX_VALUE + 1, 1, 1) }
    }

    @Test
    fun `Date_fromWeekDate(settings) throws an exception when year is out of range`() {
        assertFailsWith<DateTimeException> {
            Date.fromWeekDate(Year.MIN_VALUE - 1, 52, 1, SUNDAY_START)
        }
        assertFailsWith<DateTimeException> {
            Date.fromWeekDate(Year.MAX_VALUE + 1, 1, 1, SUNDAY_START)
        }
    }

    @Test
    fun `Date_fromWeekDate() throws an exception when week is out of range`() {
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2000, 0, 1) }
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2010, 53, 1) }
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2010, 54, 1) }
    }

    @Test
    fun `Date_fromWeekDate(settings) throws an exception when week is out of range`() {
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2000, 0, 1, SUNDAY_START) }
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2010, 54, 1, SUNDAY_START) }
    }

    @Test
    fun `Date_fromWeekDate() throws an exception when day is out of range`() {
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2000, 23, 0) }
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2010, 35, 8) }
    }

    @Test
    fun `Date_fromWeekDate(settings) throws an exception when day is out of range`() {
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2000, 23, 0, SUNDAY_START) }
        assertFailsWith<DateTimeException> { Date.fromWeekDate(2010, 35, 8, SUNDAY_START) }
    }

    @Test
    fun `Date_fromWeekDate() creates a Date from an ISO week date`() {
        TestData.isoWeekDates.forEach { (date, weekDate) ->
            val (year, week, day) = weekDate
            assertEquals(date, Date.fromWeekDate(year, week, day), date.toString())
        }
    }

    @Test
    fun `Date_fromWeekDate() creates a Date from a Sunday start week date`() {
        TestData.sundayStartWeekDates.filter { it.first != Date.MAX }.forEach { (date, weekDate) ->
            val (year, week, day) = weekDate
            assertEquals(date, Date.fromWeekDate(year, week, day, SUNDAY_START), message = "$date (SUNDAY_START)")
            assertEquals(date, Date.fromWeekDate(year, week, day, en_US), message = "$date (en-US)")
        }
    }
}