package io.islandtime

import io.islandtime.DayOfWeek.*
import io.islandtime.Month.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.calendar.WeekSettings
import io.islandtime.calendar.WeekSettings.Companion.ISO
import io.islandtime.calendar.WeekSettings.Companion.SUNDAY_START
import io.islandtime.locale.toLocale
import io.islandtime.measures.days
import io.islandtime.measures.hours
import io.islandtime.measures.weeks
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.TestData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePropertiesTest : AbstractIslandTimeTest() {
    @Suppress("PrivatePropertyName")
    private val en_US = "en-US".toLocale()

    private val nyZone = TimeZone("America/New_York")

    @Test
    fun `Date_isInLeapYear returns true in leap year`() {
        assertTrue { Date(2020, JANUARY, 1).isInLeapYear }
    }

    @Test
    fun `Date_isInLeapYear returns false in common year`() {
        assertFalse { Date(2019, JANUARY, 1).isInLeapYear }
    }

    @Test
    fun `Date_isLeapDay returns true only on February 29`() {
        assertTrue { Date(2020, FEBRUARY, 29).isLeapDay }
        assertFalse { Date(2019, FEBRUARY, 28).isLeapDay }
        assertFalse { Date(2019, MARCH, 29).isLeapDay }
    }

    @Test
    fun `Date_lengthOfMonth returns the length in days of a date's month`() {
        assertEquals(29.days, Date(2020, FEBRUARY, 29).lengthOfMonth)
        assertEquals(28.days, Date(2019, FEBRUARY, 28).lengthOfMonth)
    }

    @Test
    fun `Date_lengthOfYear returns the length in days of a date's year`() {
        assertEquals(366.days, Date(2020, FEBRUARY, 29).lengthOfYear)
        assertEquals(365.days, Date(2010, MAY, 20).lengthOfYear)
    }

    @Test
    fun `Date_week with ISO start`() {
        val date = Date(2020, MARCH, 6)
        val expected = Date(2020, MARCH, 2)..Date(2020, MARCH, 8)

        assertEquals(expected, date.week)
        assertEquals(expected, date.week(ISO))
    }

    @Test
    fun `Date_week with Sunday start`() {
        val date = Date(2020, MARCH, 6)
        val expected = Date(2020, MARCH, 1)..Date(2020, MARCH, 7)

        assertEquals(expected, date.week(SUNDAY_START))
        assertEquals(expected, date.week(en_US))
    }

    @Test
    fun `DateTime_week with ISO start`() {
        val dateTime = DateTime(2020, MARCH, 6, 13, 30)
        val start = Date(2020, MARCH, 2) at MIDNIGHT
        val end = Date(2020, MARCH, 8) at Time.MAX

        assertEquals(start..end, dateTime.week)
        assertEquals(start..end, dateTime.week(ISO))
    }

    @Test
    fun `DateTime_week with Sunday start`() {
        val dateTime = DateTime(2020, MARCH, 6, 13, 30)
        val start = Date(2020, MARCH, 1) at MIDNIGHT
        val end = Date(2020, MARCH, 7) at Time.MAX

        assertEquals(start..end, dateTime.week(SUNDAY_START))
        assertEquals(start..end, dateTime.week(en_US))
    }

    @Test
    fun `OffsetDateTime_week with ISO start`() {
        // Note: DST transition occurs at 2AM on March 8
        val offsetDateTime = DateTime(2020, MARCH, 6, 13, 30) at UtcOffset((-5).hours)
        val start = Date(2020, MARCH, 2) at MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2020, MARCH, 8) at Time.MAX at UtcOffset((-5).hours)

        assertEquals(start..end, offsetDateTime.week)
        assertEquals(start..end, offsetDateTime.week(ISO))
    }

    @Test
    fun `OffsetDateTime_week with Sunday start`() {
        val offsetDateTime = DateTime(2020, MARCH, 6, 13, 30) at UtcOffset((-5).hours)
        val start = Date(2020, MARCH, 1) at MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2020, MARCH, 7) at Time.MAX at UtcOffset((-5).hours)

        assertEquals(start..end, offsetDateTime.week(en_US))
        assertEquals(start..end, offsetDateTime.week(SUNDAY_START))
    }

    @Test
    fun `ZonedDateTime_week with ISO start`() {
        // Note: DST transition occurs at 2AM on March 8
        val zonedDateTime = DateTime(2020, MARCH, 6, 13, 30) at nyZone
        val start = Date(2020, MARCH, 2) at MIDNIGHT at nyZone
        val end = Date(2020, MARCH, 8) at Time.MAX at nyZone

        assertEquals(start..end, zonedDateTime.week)
        assertEquals(start..end, zonedDateTime.week(ISO))
    }

    @Test
    fun `ZonedDateTime_week with Sunday start`() {
        val zonedDateTime = DateTime(2020, MARCH, 6, 13, 30) at nyZone
        val start = Date(2020, MARCH, 1) at MIDNIGHT at nyZone
        val end = Date(2020, MARCH, 7) at Time.MAX at nyZone

        assertEquals(start..end, zonedDateTime.week(en_US))
        assertEquals(start..end, zonedDateTime.week(SUNDAY_START))
    }

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
    fun `Date_weekOfMonth with Sunday start`() {
        listOf(
            Date(2008, 12, 27) to 4,
            Date(2008, 12, 28) to 5,
            Date(2008, 12, 31) to 5,
            Date(2009, 1, 1) to 1,
            Date(2009, 1, 3) to 1,
            Date(2009, 1, 4) to 2,
            Date(2020, 5, 31) to 6
        ).forEach { (date, week) ->
            assertEquals(week, date.weekOfMonth(SUNDAY_START), message = "$date (SUNDAY_START)")
            assertEquals(week, date.weekOfMonth(en_US), message = "$date (en-US)")
        }
    }

    @Test
    fun `Date_weekOfMonth with custom week definition`() {
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
                date.weekOfMonth(WeekSettings(MONDAY, 5)),
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
    fun `Date_weekOfYear with Sunday start`() {
        listOf(
            Date(2008, 12, 27) to 52,
            Date(2008, 12, 28) to 53,
            Date(2008, 12, 31) to 53,
            Date(2009, 1, 1) to 1,
            Date(2009, 1, 3) to 1,
            Date(2009, 1, 4) to 2
        ).forEach { (date, week) ->
            assertEquals(week, date.weekOfYear(SUNDAY_START), message = "$date (SUNDAY_START)")
            assertEquals(week, date.weekOfYear(en_US), message = "$date (en-US)")
        }
    }

    @Test
    fun `Date_weekOfYear with custom week definition`() {
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
                date.weekOfYear(WeekSettings(MONDAY, 5)),
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
        TestData.sundayStartWeekDates.forEach { (date, weekDate) ->
            val (year, week) = weekDate

            assertEquals(
                Pair(year, week),
                Pair(date.weekBasedYear(SUNDAY_START), date.weekOfWeekBasedYear(SUNDAY_START)),
                message = "$date (SUNDAY_START)"
            )

            assertEquals(
                Pair(year, week),
                Pair(date.weekBasedYear(en_US), date.weekOfWeekBasedYear(en_US)),
                message = "$date (en-US)"
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

    @Test
    fun `Date_next returns the next date with a particular day of week`() {
        assertEquals(
            Date(2019, OCTOBER, 11),
            Date(2019, OCTOBER, 10).next(FRIDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 17),
            Date(2019, OCTOBER, 10).next(THURSDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 16),
            Date(2019, OCTOBER, 10).next(WEDNESDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 14),
            Date(2019, OCTOBER, 10).next(MONDAY)
        )
    }

    @Test
    fun `Date_nextOrSame returns the next date with a particular day of week or this one if it's the same`() {
        assertEquals(
            Date(2019, OCTOBER, 11),
            Date(2019, OCTOBER, 10).nextOrSame(FRIDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 10),
            Date(2019, OCTOBER, 10).nextOrSame(THURSDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 16),
            Date(2019, OCTOBER, 10).nextOrSame(WEDNESDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 14),
            Date(2019, OCTOBER, 10).nextOrSame(MONDAY)
        )
    }

    @Test
    fun `Date_previous returns the last date with a particular day of week`() {
        assertEquals(
            Date(2019, OCTOBER, 9),
            Date(2019, OCTOBER, 10).previous(WEDNESDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 3),
            Date(2019, OCTOBER, 10).previous(THURSDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 4),
            Date(2019, OCTOBER, 10).previous(FRIDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 7),
            Date(2019, OCTOBER, 10).previous(MONDAY)
        )
    }

    @Test
    fun `Date_previousOrSame returns the last date with a particular day of week or this one if it's the same`() {
        assertEquals(
            Date(2019, OCTOBER, 9),
            Date(2019, OCTOBER, 10).previousOrSame(WEDNESDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 10),
            Date(2019, OCTOBER, 10).previousOrSame(THURSDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 4),
            Date(2019, OCTOBER, 10).previousOrSame(FRIDAY)
        )

        assertEquals(
            Date(2019, OCTOBER, 7),
            Date(2019, OCTOBER, 10).previousOrSame(MONDAY)
        )
    }

    @Test
    fun `DateTime_next returns the next date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, OCTOBER, 17, 1, 1, 1, 1),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .next(THURSDAY)
        )
    }

    @Test
    fun `DateTime_nextOrSame returns the next date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .nextOrSame(THURSDAY)
        )
    }

    @Test
    fun `DateTime_previous returns the last date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, OCTOBER, 3, 1, 1, 1, 1),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .previous(THURSDAY)
        )
    }

    @Test
    fun `DateTime_previousOrSame returns the last date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .previousOrSame(THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_next returns the next date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, OCTOBER, 17, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .next(THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_nextOrSame returns the next date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .nextOrSame(THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_previous returns the last date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, OCTOBER, 3, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .previous(THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_previousOrSame returns the last date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .previousOrSame(THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_next returns the next date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, OCTOBER, 17, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .next(THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_nextOrSame returns the next date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .nextOrSame(THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_previous returns the last date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, OCTOBER, 3, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .previous(THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_previousOrSame returns the last date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .previousOrSame(THURSDAY)
        )
    }
}
