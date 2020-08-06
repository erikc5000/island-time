package io.islandtime.clock.jvm

import io.islandtime.*
import io.islandtime.measures.hours
import io.islandtime.test.AbstractIslandTimeTest
import org.junit.Test
import kotlin.test.assertEquals
import java.time.Clock as JavaClock
import java.time.Instant as JavaInstant
import java.time.ZoneId as JavaZoneId
import java.time.ZoneOffset as JavaZoneOffset

class JvmNowTest : AbstractIslandTimeTest() {
    @Test
    fun `Year_now()`() {
        val clock = JavaClock.fixed(JavaInstant.ofEpochMilli(-1L), JavaZoneOffset.UTC)
        assertEquals(Year(1969), Year.now(clock))
    }

    @Test
    fun `YearMonth_now()`() {
        val clock = JavaClock.fixed(JavaInstant.ofEpochMilli(-1L), JavaZoneOffset.UTC)
        assertEquals(YearMonth(1969, Month.DECEMBER), YearMonth.now(clock))
    }

    @Test
    fun `Date_now() in UTC`() {
        val clock = JavaClock.fixed(JavaInstant.ofEpochMilli(-1L), JavaZoneOffset.UTC)
        assertEquals(Date(1969, Month.DECEMBER, 31), Date.now(clock))
    }

    @Test
    fun `Date_now() with offset`() {
        val clock = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT+1"))
        assertEquals(Date(1969, Month.DECEMBER, 30), Date.now(clock))
    }

    @Test
    fun `DateTime_now() in UTC`() {
        val clock = JavaClock.fixed(JavaInstant.ofEpochMilli(-1L), JavaZoneOffset.UTC)
        assertEquals(
            DateTime(1969, Month.DECEMBER, 31, 23, 59, 59, 999_000_000),
            DateTime.now(clock)
        )
    }

    @Test
    fun `DateTime_now() with offset`() {
        val clock = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT+1"))
        assertEquals(
            DateTime(1969, Month.DECEMBER, 30, 23, 0),
            DateTime.now(clock)
        )
    }

    @Test
    fun `Time_now() in UTC`() {
        val clock = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneOffset.UTC)
        assertEquals(Time(0, 0), Time.now(clock))
    }

    @Test
    fun `Time_now() with offset`() {
        val clock1 = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT+4"))
        assertEquals(Time(20, 0), Time.now(clock1))

        val clock2 = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT-4"))
        assertEquals(Time(4, 0), Time.now(clock2))
    }

    @Test
    fun `OffsetTime_now()`() {
        val clock1 = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT+4"))
        assertEquals(OffsetTime(Time(20, 0), (-4).hours.asUtcOffset()), OffsetTime.now(clock1))

        val clock2 = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT-4"))
        assertEquals(OffsetTime(Time(4, 0), 4.hours.asUtcOffset()), OffsetTime.now(clock2))
    }

    @Test
    fun `OffsetDateTime_now()`() {
        val clock = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT+1"))
        assertEquals(
            OffsetDateTime(
                DateTime(1969, Month.DECEMBER, 30, 23, 0),
                (-1).hours.asUtcOffset()
            ),
            OffsetDateTime.now(clock)
        )
    }

    @Test
    fun `ZonedDateTime_now()`() {
        val clock = JavaClock.fixed(JavaInstant.parse("1969-12-31T00:00:00Z"), JavaZoneId.of("Etc/GMT+1"))
        assertEquals(
            ZonedDateTime(
                DateTime(1969, Month.DECEMBER, 30, 23, 0),
                TimeZone("Etc/GMT+1")
            ),
            ZonedDateTime.now(clock)
        )
    }
}
