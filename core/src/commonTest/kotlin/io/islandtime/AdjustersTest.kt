package io.islandtime

import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.measures.hours
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdjustersTest {
    private val nyZone = TimeZone("America/New_York")

    @BeforeTest
    fun setUp() {
        IslandTime.initializeWith(PlatformTimeZoneRulesProvider)
    }

    @AfterTest
    fun tearDown() {
        IslandTime.reset()
    }

    @Test
    fun `Date_next() returns the next date with a particular day of week`() {
        assertEquals(
            Date(2019, Month.OCTOBER, 11),
            Date(2019, Month.OCTOBER, 10).next(DayOfWeek.FRIDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 17),
            Date(2019, Month.OCTOBER, 10).next(DayOfWeek.THURSDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 16),
            Date(2019, Month.OCTOBER, 10).next(DayOfWeek.WEDNESDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 14),
            Date(2019, Month.OCTOBER, 10).next(DayOfWeek.MONDAY)
        )
    }

    @Test
    fun `Date_nextOrSame() returns the next date with a particular day of week or this one if it's the same`() {
        assertEquals(
            Date(2019, Month.OCTOBER, 11),
            Date(2019, Month.OCTOBER, 10).nextOrSame(DayOfWeek.FRIDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 10),
            Date(2019, Month.OCTOBER, 10).nextOrSame(DayOfWeek.THURSDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 16),
            Date(2019, Month.OCTOBER, 10).nextOrSame(DayOfWeek.WEDNESDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 14),
            Date(2019, Month.OCTOBER, 10).nextOrSame(DayOfWeek.MONDAY)
        )
    }

    @Test
    fun `Date_previous() returns the last date with a particular day of week`() {
        assertEquals(
            Date(2019, Month.OCTOBER, 9),
            Date(2019, Month.OCTOBER, 10).previous(DayOfWeek.WEDNESDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 3),
            Date(2019, Month.OCTOBER, 10).previous(DayOfWeek.THURSDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 4),
            Date(2019, Month.OCTOBER, 10).previous(DayOfWeek.FRIDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 7),
            Date(2019, Month.OCTOBER, 10).previous(DayOfWeek.MONDAY)
        )
    }

    @Test
    fun `Date_previousOrSame() returns the last date with a particular day of week or this one if it's the same`() {
        assertEquals(
            Date(2019, Month.OCTOBER, 9),
            Date(2019, Month.OCTOBER, 10).previousOrSame(DayOfWeek.WEDNESDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 10),
            Date(2019, Month.OCTOBER, 10).previousOrSame(DayOfWeek.THURSDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 4),
            Date(2019, Month.OCTOBER, 10).previousOrSame(DayOfWeek.FRIDAY)
        )

        assertEquals(
            Date(2019, Month.OCTOBER, 7),
            Date(2019, Month.OCTOBER, 10).previousOrSame(DayOfWeek.MONDAY)
        )
    }

    @Test
    fun `DateTime_next() returns the next date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 17, 1, 1, 1, 1),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .next(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `DateTime_nextOrSame() returns the next date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .nextOrSame(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `DateTime_previous() returns the last date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 3, 1, 1, 1, 1),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .previous(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `DateTime_previousOrSame() returns the last date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .previousOrSame(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_next() returns the next date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 17, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .next(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_nextOrSame() returns the next date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .nextOrSame(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_previous() returns the last date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 3, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .previous(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `OffsetDateTime_previousOrSame() returns the last date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset()),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .previousOrSame(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_next() returns the next date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 17, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .next(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_nextOrSame() returns the next date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .nextOrSame(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_previous() returns the last date-time with a particular day of week`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 3, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .previous(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `ZonedDateTime_previousOrSame() returns the last date-time with a particular day of week or the same`() {
        assertEquals(
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1).at(nyZone),
            DateTime(2019, Month.OCTOBER, 10, 1, 1, 1, 1)
                .at(nyZone)
                .previousOrSame(DayOfWeek.THURSDAY)
        )
    }

    @Test
    fun `Date_startOfYear() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1),
            Date(2019, Month.AUGUST, 27).startOfYear()
        )
    }

    @Test
    fun `DateTime_startOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            DateTime(2019, Month.JANUARY, 1, 0, 0),
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).startOfYear()
        )
    }

    @Test
    fun `OffsetDateTime_startOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1) at MIDNIGHT at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .startOfYear()
        )
    }

    @Test
    fun `ZonedDateTime_startOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.JANUARY, 1) at MIDNIGHT at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(nyZone)
                .startOfYear()
        )
    }

    @Test
    fun `Date_startOfMonth() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 1),
            Date(2019, Month.AUGUST, 27).startOfMonth()
        )
    }

    @Test
    fun `DateTime_startOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            DateTime(2019, Month.AUGUST, 1, 0, 0),
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).startOfMonth()
        )
    }

    @Test
    fun `OffsetDateTime_startOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 1) at MIDNIGHT at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .startOfMonth()
        )
    }

    @Test
    fun `ZonedDateTime_startOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 1) at MIDNIGHT at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(nyZone)
                .startOfMonth()
        )
    }

    @Test
    fun `Date_startOfWeek() returns the date at the start of this date's ISO week`() {
        assertEquals(
            Date(2019, Month.AUGUST, 26),
            Date(2019, Month.AUGUST, 27).startOfWeek()
        )
    }

    @Test
    fun `DateTime_startOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            DateTime(2019, Month.AUGUST, 26, 0, 0),
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).startOfWeek()
        )
    }

    @Test
    fun `OffsetDateTime_startOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.AUGUST, 26) at MIDNIGHT at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .startOfWeek()
        )
    }

    @Test
    fun `ZonedDateTime_startOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.AUGUST, 26) at MIDNIGHT at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(nyZone)
                .startOfWeek()
        )
    }

    @Test
    fun `Date_endOfYear() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31),
            Date(2019, Month.AUGUST, 27).endOfYear()
        )
    }

    @Test
    fun `DateTime_endOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31) at Time.MAX,
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).endOfYear()
        )
    }

    @Test
    fun `OffsetDateTime_endOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31) at Time.MAX at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .endOfYear()
        )
    }

    @Test
    fun `ZonedDateTime_endOfYear() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.DECEMBER, 31) at Time.MAX at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(nyZone)
                .endOfYear()
        )
    }

    @Test
    fun `Date_endOfMonth() returns the date at the start of this date's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31),
            Date(2019, Month.AUGUST, 27).endOfMonth()
        )
    }

    @Test
    fun `DateTime_endOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31) at Time.MAX,
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).endOfMonth()
        )
    }

    @Test
    fun `OffsetDateTime_endOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31) at Time.MAX at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .endOfMonth()
        )
    }

    @Test
    fun `ZonedDateTime_endOfMonth() returns the date-time at the start of this date-time's year`() {
        assertEquals(
            Date(2019, Month.AUGUST, 31) at Time.MAX at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(nyZone)
                .endOfMonth()
        )
    }

    @Test
    fun `Date_endOfWeek() returns the date at the start of this date's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1),
            Date(2019, Month.AUGUST, 27).endOfWeek()
        )
    }

    @Test
    fun `DateTime_endOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1) at Time.MAX,
            DateTime(2019, Month.AUGUST, 27, 1, 1, 1, 1).endOfWeek()
        )
    }

    @Test
    fun `OffsetDateTime_endOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1) at Time.MAX at 1.hours.asUtcOffset(),
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(1.hours.asUtcOffset())
                .endOfWeek()
        )
    }

    @Test
    fun `ZonedDateTime_endOfWeek() returns the date-time at the start of this date-time's ISO week`() {
        assertEquals(
            Date(2019, Month.SEPTEMBER, 1) at Time.MAX at nyZone,
            Date(2019, Month.AUGUST, 27)
                .atTime( 1, 1, 1, 1)
                .at(nyZone)
                .endOfWeek()
        )
    }
}