package io.islandtime.operators

import io.islandtime.*
import io.islandtime.measures.hours
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviousNextTest : AbstractIslandTimeTest() {
    private val nyZone = "America/New_York".toTimeZone()

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
}