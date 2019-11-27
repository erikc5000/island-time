package io.islandtime.operators

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.measures.hours
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StartEndTest : AbstractIslandTimeTest() {
    private val nyZone = "America/New_York".toTimeZone()

    @Test
    fun `Date_startOfYear() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1),
            Date(2019, Month.AUGUST, 27).startOfYear
        )
    }

    @Test
    fun `DateTime_startOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            DateTime(2019, Month.JANUARY, 1, 0, 0),
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).startOfYear
        )
    }

    @Test
    fun `OffsetDateTime_startOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1) at MIDNIGHT at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .startOfYear
        )
    }

    @Test
    fun `ZonedDateTime_startOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1) at MIDNIGHT at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(nyZone)
                .startOfYear
        )
    }

    @Test
    fun `Date_startOfMonth() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 1),
            Date(2019, Month.AUGUST, 27).startOfMonth
        )
    }

    @Test
    fun `DateTime_startOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            DateTime(2019, Month.AUGUST, 1, 0, 0),
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).startOfMonth
        )
    }

    @Test
    fun `OffsetDateTime_startOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 1) at MIDNIGHT at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .startOfMonth
        )
    }

    @Test
    fun `ZonedDateTime_startOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 1) at MIDNIGHT at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(nyZone)
                .startOfMonth
        )
    }

    @Test
    fun `Date_startOfWeek() returns the date at the start of this date's ISO week`() {
        assertEquals(
            Date(2019, Month.AUGUST, 26),
            Date(2019, Month.AUGUST, 27).startOfWeek
        )
    }

    @Test
    fun `DateTime_startOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            DateTime(2019, Month.AUGUST, 26, 0, 0),
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).startOfWeek
        )
    }

    @Test
    fun `OffsetDateTime_startOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.AUGUST, 26) at MIDNIGHT at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .startOfWeek
        )
    }

    @Test
    fun `ZonedDateTime_startOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.AUGUST, 26) at MIDNIGHT at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(nyZone)
                .startOfWeek
        )
    }

    @Test
    fun `Date_endOfYear() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31),
            Date(2019, Month.AUGUST, 27).endOfYear
        )
    }

    @Test
    fun `DateTime_endOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31) at Time.MAX,
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).endOfYear
        )
    }

    @Test
    fun `OffsetDateTime_endOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31) at Time.MAX at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .endOfYear
        )
    }

    @Test
    fun `ZonedDateTime_endOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31) at Time.MAX at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(nyZone)
                .endOfYear
        )
    }

    @Test
    fun `Date_endOfMonth() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31),
            Date(2019, Month.AUGUST, 27).endOfMonth
        )
    }

    @Test
    fun `DateTime_endOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31) at Time.MAX,
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).endOfMonth
        )
    }

    @Test
    fun `OffsetDateTime_endOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31) at Time.MAX at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .endOfMonth
        )
    }

    @Test
    fun `ZonedDateTime_endOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31) at Time.MAX at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(nyZone)
                .endOfMonth
        )
    }

    @Test
    fun `Date_endOfWeek() returns the date at the start of this date's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1),
            Date(2019, Month.AUGUST, 27).endOfWeek
        )
    }

    @Test
    fun `DateTime_endOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1) at Time.MAX,
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).endOfWeek
        )
    }

    @Test
    fun `OffsetDateTime_endOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1) at Time.MAX at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .endOfWeek
        )
    }

    @Test
    fun `ZonedDateTime_endOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1) at Time.MAX at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime(1, 1, 1, 1)
                .at(nyZone)
                .endOfWeek
        )
    }

    @Test
    fun `Date_startOfDay returns the DateTime at midnight of same day`() {
        assertEquals(
            DateTime(
                Date(2019, Month.JULY, 1),
                Time.MIDNIGHT
            ),
            Date(2019, Month.JULY, 1).startOfDay
        )
    }

    @Test
    fun `Date_endOfDay returns the DateTime just before the end of the same day`() {
        assertEquals(
            DateTime(
                Date(2019, Month.JULY, 1),
                Time(23, 59, 59, 999_999_999)
            ),
            Date(2019, Month.JULY, 1).endOfDay
        )
    }

    @Test
    fun `Date_startOfDayAt() creates a ZonedDateTime at the start of the day in a particular time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 0, 0),
                nyZone
            ),
            Date(2019, 5, 20).startOfDayAt(nyZone)
        )

        // TODO: Add tests where transitions occur during midnight
    }

    @Test
    fun `Date_endOfDayAt() creates a ZonedDateTime at the end of the day in a particular time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 23, 59, 59, 999_999_999),
                nyZone
            ),
            Date(2019, 5, 20).endOfDayAt(nyZone)
        )

        // TODO: Add tests where transitions occur during midnight
    }
}