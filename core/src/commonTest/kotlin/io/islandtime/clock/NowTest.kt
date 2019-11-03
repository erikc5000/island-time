package io.islandtime.clock

import io.islandtime.*
import io.islandtime.measures.days
import io.islandtime.measures.hours
import io.islandtime.measures.milliseconds
import kotlin.test.Test
import kotlin.test.assertEquals

class NowTest : AbstractIslandTimeTest() {
    @Test
    fun `Year_now()`() {
        val clock = FixedClock((-1L).milliseconds)
        assertEquals(Year(1969), Year.now(clock))

        clock += 1.milliseconds
        assertEquals(Year(1970), Year.now(clock))
    }

    @Test
    fun `YearMonth_now()`() {
        val clock = FixedClock((-1L).milliseconds)
        assertEquals(YearMonth(1969, Month.DECEMBER), YearMonth.now(clock))

        clock += 1.milliseconds
        assertEquals(YearMonth(1970, Month.JANUARY), YearMonth.now(clock))
    }

    @Test
    fun `Date_now() in UTC`() {
        val clock = FixedClock((-1L).milliseconds)
        assertEquals(Date(1969, Month.DECEMBER, 31), Date.now(clock))

        clock += 1.milliseconds
        assertEquals(Date(1970, Month.JANUARY, 1), Date.now(clock))
    }

    @Test
    fun `Date_now() with offset`() {
        val clock = FixedClock((-1).days, "Etc/GMT+1".toTimeZone())
        assertEquals(Date(1969, Month.DECEMBER, 30), Date.now(clock))
    }

    @Test
    fun `DateTime_now() in UTC`() {
        val clock = FixedClock((-1L).milliseconds)
        assertEquals(
            DateTime(1969, Month.DECEMBER, 31, 23, 59, 59, 999_000_000),
            DateTime.now(clock)
        )

        clock += 1.milliseconds
        assertEquals(DateTime(1970, Month.JANUARY, 1, 0, 0), DateTime.now(clock))

        clock += 1.days - 1.milliseconds
        assertEquals(
            DateTime(1970, Month.JANUARY, 1, 23, 59, 59, 999_000_000),
            DateTime.now(clock)
        )

        clock += 1.milliseconds
        assertEquals(DateTime(1970, Month.JANUARY, 2, 0, 0), DateTime.now(clock))
    }

    @Test
    fun `DateTime_now() with offset`() {
        val clock = FixedClock((-1).days, "Etc/GMT+1".toTimeZone())
        assertEquals(
            DateTime(1969, Month.DECEMBER, 30, 23, 0),
            DateTime.now(clock)
        )
    }

    @Test
    fun `Time_now() in UTC`() {
        val clock = FixedClock((-1).days)
        assertEquals(Time(0, 0), Time.now(clock))

        clock += 1.milliseconds
        assertEquals(Time(0, 0, 0, 1_000_000), Time.now(clock))

        clock += 1.days - 2.milliseconds
        assertEquals(Time(23, 59, 59, 999_000_000), Time.now(clock))

        clock += 1.milliseconds
        assertEquals(Time(0, 0), Time.now(clock))

        clock += 1.days - 1.milliseconds
        assertEquals(Time(23, 59, 59, 999_000_000), Time.now(clock))
    }

    @Test
    fun `Time_now() with offset`() {
        val clock1 = FixedClock((-1).days, "Etc/GMT+4".toTimeZone())
        assertEquals(Time(20, 0), Time.now(clock1))

        val clock2 = FixedClock((-1).days, "Etc/GMT-4".toTimeZone())
        assertEquals(Time(4, 0), Time.now(clock2))
    }

    @Test
    fun `OffsetTime_now()`() {
        val clock1 = FixedClock((-1).days, "Etc/GMT+4".toTimeZone())
        assertEquals(OffsetTime(Time(20, 0), (-4).hours.asUtcOffset()), OffsetTime.now(clock1))

        val clock2 = FixedClock((-1).days, "Etc/GMT-4".toTimeZone())
        assertEquals(OffsetTime(Time(4, 0), 4.hours.asUtcOffset()), OffsetTime.now(clock2))
    }

    @Test
    fun `OffsetDateTime_now()`() {
        val clock = FixedClock((-1).days, "Etc/GMT+1".toTimeZone())
        assertEquals(
            OffsetDateTime(
                DateTime(1969, Month.DECEMBER, 30, 23, 0),
                (-1).hours.asUtcOffset()
            ),
            OffsetDateTime.now(clock)
        )

        clock += 1.hours
        assertEquals(
            OffsetDateTime(
                DateTime(1969, Month.DECEMBER, 31, 0, 0),
                (-1).hours.asUtcOffset()
            ),
            OffsetDateTime.now(clock)
        )
    }

    @Test
    fun `ZonedDateTime_now()`() {
        val clock = FixedClock((-1).days, "Etc/GMT+1".toTimeZone())
        assertEquals(
            ZonedDateTime(
                DateTime(1969, Month.DECEMBER, 30, 23, 0),
                "Etc/GMT+1".toTimeZone()
            ),
            ZonedDateTime.now(clock)
        )

        clock += 1.hours
        assertEquals(
            ZonedDateTime(
                DateTime(1969, Month.DECEMBER, 31, 0, 0),
                "Etc/GMT+1".toTimeZone()
            ),
            ZonedDateTime.now(clock)
        )
    }
}