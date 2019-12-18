package io.islandtime

import io.islandtime.format.TextStyle
import io.islandtime.locale.localeOf
import io.islandtime.measures.days
import io.islandtime.measures.months
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MonthTest : AbstractIslandTimeTest() {
    @Test
    fun `Int_toMonth() throws an exception when the value isn't a valid ISO month number`() {
        assertFailsWith<DateTimeException> { 0.toMonth() }
        assertFailsWith<DateTimeException> { 13.toMonth() }
    }

    @Test
    fun `Int_toMonth() gets a Month from an ISO month number when it's in range`() {
        assertEquals(Month.JANUARY, 1.toMonth())
        assertEquals(Month.APRIL, 4.toMonth())
        assertEquals(Month.DECEMBER, 12.toMonth())
    }

    @Test
    fun `month number property`() {
        assertEquals(1, Month.JANUARY.number)
        assertEquals(12, Month.DECEMBER.number)
    }

    @Test
    fun `length in common year`() {
        assertEquals(30.days, Month.APRIL.lengthInCommonYear)
        assertEquals(31.days, Month.OCTOBER.lengthInCommonYear)
        assertEquals(28.days, Month.FEBRUARY.lengthInCommonYear)
    }

    @Test
    fun `length in leap year`() {
        assertEquals(30.days, Month.APRIL.lengthInLeapYear)
        assertEquals(31.days, Month.OCTOBER.lengthInLeapYear)
        assertEquals(29.days, Month.FEBRUARY.lengthInLeapYear)
    }

    @Test
    fun `lengthIn() works correctly`() {
        assertEquals(31.days, Month.JANUARY.lengthIn(2019))
        assertEquals(28.days, Month.FEBRUARY.lengthIn(2019))
        assertEquals(29.days, Month.FEBRUARY.lengthIn(2020))
    }

    @Test
    fun `first day in common year`() {
        assertEquals(60, Month.MARCH.firstDayOfCommonYear)
    }

    @Test
    fun `first day in leap year`() {
        assertEquals(61, Month.MARCH.firstDayOfLeapYear)
    }

    @Test
    fun `firstDayOfYearIn() works correctly`() {
        assertEquals(1, Month.JANUARY.firstDayOfYearIn(1950))
        assertEquals(1, Month.JANUARY.firstDayOfYearIn(2000))
        assertEquals(244, Month.SEPTEMBER.firstDayOfYearIn(2001))
        assertEquals(245, Month.SEPTEMBER.firstDayOfYearIn(2000))
    }

    @Test
    fun `localizedName() and displayName() get localized text from the provider`() {
        val en_US = localeOf("en-US")
        assertEquals("April", Month.APRIL.localizedName(TextStyle.FULL_STANDALONE, en_US))
        assertEquals("Jun", Month.JUNE.displayName(TextStyle.SHORT_STANDALONE, en_US))
    }

    @Test
    fun `dayRangeIn() returns an appropriate range in leap years`() {
        val range = Month.FEBRUARY.dayRangeIn(2020)
        assertEquals(1, range.first)
        assertEquals(29, range.last)
    }

    @Test
    fun `dayRangeIn() returns an appropriate range in common years`() {
        val range = Month.FEBRUARY.dayRangeIn(2019)
        assertEquals(1, range.first)
        assertEquals(28, range.last)
    }

    @Test
    fun `adding zero months has no effect`() {
        assertEquals(Month.JUNE, Month.JUNE + 0.months)
        assertEquals(Month.JUNE, Month.JUNE + 0L.months)
    }

    @Test
    fun `add positive months`() {
        assertEquals(Month.JULY, Month.JUNE + 1.months)
        assertEquals(Month.FEBRUARY, Month.NOVEMBER + 3.months)
        assertEquals(Month.JANUARY, Month.JANUARY + 12.months)
        assertEquals(Month.MAY, Month.JANUARY + 16.months)
        assertEquals(Month.SEPTEMBER, Month.FEBRUARY + Int.MAX_VALUE.months)

        assertEquals(Month.JULY, Month.JUNE + 1L.months)
        assertEquals(Month.FEBRUARY, Month.NOVEMBER + 3L.months)
        assertEquals(Month.JANUARY, Month.JANUARY + 12L.months)
        assertEquals(Month.MAY, Month.JANUARY + 16L.months)
        assertEquals(Month.SEPTEMBER, Month.FEBRUARY + Long.MAX_VALUE.months)
    }

    @Test
    fun `add negative months`() {
        assertEquals(Month.DECEMBER, Month.JANUARY + (-1).months)
        assertEquals(Month.DECEMBER, Month.JANUARY + -(13.months))
        assertEquals(Month.JUNE, Month.FEBRUARY + Int.MIN_VALUE.months)

        assertEquals(Month.DECEMBER, Month.JANUARY + (-1L).months)
        assertEquals(Month.DECEMBER, Month.JANUARY + -(13L.months))
        assertEquals(Month.JUNE, Month.FEBRUARY + Long.MIN_VALUE.months)
    }

    @Test
    fun `subtracting zero months has no effect`() {
        assertEquals(Month.MAY, Month.MAY - 0.months)
        assertEquals(Month.MAY, Month.MAY - 0L.months)
    }

    @Test
    fun `subtract positive months`() {
        assertEquals(Month.NOVEMBER, Month.JANUARY - 2.months)
        assertEquals(Month.JANUARY, Month.MAY - 16.months)
        assertEquals(Month.JULY, Month.FEBRUARY - Int.MAX_VALUE.months)

        assertEquals(Month.NOVEMBER, Month.JANUARY - 2L.months)
        assertEquals(Month.JANUARY, Month.MAY - 16L.months)
        assertEquals(Month.JULY, Month.FEBRUARY - Long.MAX_VALUE.months)
    }

    @Test
    fun `subtract negative months`() {
        assertEquals(Month.JANUARY, Month.NOVEMBER - (-2).months)
        assertEquals(Month.MAY, Month.MAY - -(12.months))
        assertEquals(Month.OCTOBER, Month.FEBRUARY - Int.MIN_VALUE.months)

        assertEquals(Month.JANUARY, Month.NOVEMBER - (-2L).months)
        assertEquals(Month.MAY, Month.MAY - -(12L.months))
        assertEquals(Month.OCTOBER, Month.FEBRUARY - Long.MIN_VALUE.months)
    }
}