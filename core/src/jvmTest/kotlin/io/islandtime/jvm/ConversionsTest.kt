package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.measures.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionsTest {
    @Test
    fun `converts Java Instant to Instant`() {
        listOf(
            java.time.Instant.EPOCH to Instant.UNIX_EPOCH,
            java.time.Instant.ofEpochSecond(1, 999_999_999) to
                Instant(1L.seconds, 999_999_999.nanoseconds),
            java.time.Instant.ofEpochSecond(-1) to Instant((-1L).seconds),
            java.time.Instant.ofEpochSecond(0, -999_999_999) to
                Instant(0L.seconds, (-999_999_999).nanoseconds)
        ).forEach { (javaInstant, islandInstant) ->
            assertEquals(islandInstant, javaInstant.toIslandInstant())
        }
    }

    @Test
    fun `converts Instant to Java Instant`() {
        listOf(
            Instant.UNIX_EPOCH to java.time.Instant.EPOCH,
            Instant(1L.seconds, 999_999_999.nanoseconds) to
                java.time.Instant.ofEpochSecond(1, 999_999_999),
            Instant((-1L).seconds) to java.time.Instant.ofEpochSecond(-1),
            Instant(0L.seconds, (-999_999_999).nanoseconds) to
                java.time.Instant.ofEpochSecond(0, -999_999_999)
        ).forEach { (islandInstant, javaInstant) ->
            assertEquals(javaInstant, islandInstant.toJavaInstant())
        }
    }

    @Test
    fun `converts Date to Java LocalDate`() {
        val islandDate = Date(2019, Month.MAY, 3)
        val javaDate = islandDate.toJavaLocalDate()

        assertEquals(islandDate.year, javaDate.year)
        assertEquals(islandDate.monthNumber, javaDate.monthValue)
        assertEquals(islandDate.dayOfMonth, javaDate.dayOfMonth)
    }

    @Test
    fun `converts Java LocalDate to Date`() {
        val javaDate = java.time.LocalDate.of(2019, java.time.Month.MAY, 3)
        val islandDate = javaDate.toIslandDate()

        assertEquals(javaDate.year, islandDate.year)
        assertEquals(javaDate.monthValue, islandDate.monthNumber)
        assertEquals(javaDate.dayOfMonth, islandDate.dayOfMonth)
    }

    @Test
    fun `converts Time to Java Time`() {
        listOf(
            Time.MIDNIGHT to java.time.LocalTime.MIDNIGHT,
            Time.NOON to java.time.LocalTime.NOON,
            Time.MAX to java.time.LocalTime.MAX,
            Time(1, 2, 3, 4) to
                java.time.LocalTime.of(1, 2, 3, 4)
        ).forEach { (islandTime, javaTime) ->
            assertEquals(javaTime, islandTime.toJavaLocalTime())
        }
    }

    @Test
    fun `converts Java Time to Time`() {
        listOf(
            java.time.LocalTime.MIDNIGHT to Time.MIDNIGHT,
            java.time.LocalTime.NOON to Time.NOON,
            java.time.LocalTime.MAX to Time.MAX,
            java.time.LocalTime.of(1, 2, 3, 4) to
                Time(1, 2, 3, 4)
        ).forEach { (javaTime, islandTime) ->
            assertEquals(islandTime, javaTime.toIslandTime())
        }
    }

    @Test
    fun `converts Java ZoneOffset to UtcOffset`() {
        assertEquals(
            UtcOffset(1.hours, 2.minutes, 3.seconds),
            java.time.ZoneOffset.ofHoursMinutesSeconds(1, 2, 3).toIslandUtcOffset()
        )

        assertEquals(
            UtcOffset((-1).hours, (-2).minutes, (-3).seconds),
            java.time.ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3).toIslandUtcOffset()
        )
    }

    @Test
    fun `converts UtcOffset to Java ZoneOffset`() {
        assertEquals(
            java.time.ZoneOffset.ofHoursMinutesSeconds(1, 2, 3),
            UtcOffset(1.hours, 2.minutes, 3.seconds).toJavaZoneOffset()
        )

        assertEquals(
            java.time.ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3),
            UtcOffset((-1).hours, (-2).minutes, (-3).seconds).toJavaZoneOffset()
        )
    }

    @Test
    fun `converts TimeZone to Java ZoneId`() {
        assertEquals(
            java.time.ZoneId.of("America/New_York"),
            TimeZone("America/New_York").toJavaZoneId()
        )

        assertEquals(
            java.time.ZoneId.of("GMT"),
            TimeZone("GMT").toJavaZoneId()
        )

        assertEquals(
            java.time.ZoneId.of("+01:00"),
            TimeZone("+01:00").toJavaZoneId()
        )
    }

    @Test
    fun `converts Java ZoneId to TimeZone`() {
        assertEquals(
            TimeZone("America/New_York"),
            java.time.ZoneId.of("America/New_York").toIslandTimeZone()
        )

        assertEquals(
            TimeZone("GMT"),
            java.time.ZoneId.of("GMT").toIslandTimeZone()
        )

        assertEquals(
            TimeZone("+01:00"),
            java.time.ZoneId.of("+01:00").toIslandTimeZone()
        )
    }

    @Test
    fun `converts Java Duration to Duration`() {
        assertEquals(
            durationOf((-1).seconds, (-1).nanoseconds),
            java.time.Duration.ofSeconds(-1, -1).toIslandDuration()
        )
    }

    @Test
    fun `converts Duration to Java Duration`() {
        assertEquals(
            java.time.Duration.ofSeconds(-1, -1),
            durationOf((-1).seconds, (-1).nanoseconds).toJavaDuration()
        )
    }

    @Test
    fun `converts Java Period to Period`() {
        assertEquals(
            periodOf(1.years, 2.months, 3.days),
            java.time.Period.of(1, 2, 3).toIslandPeriod()
        )
    }

    @Test
    fun `converts Period to Java Period`() {
        assertEquals(
            java.time.Period.of(1, 2, 3),
            periodOf(1.years, 2.months, 3.days).toJavaPeriod()
        )
    }

    @Test
    fun `converts centuries to Java Period`() {
        assertEquals(java.time.Period.ZERO, 0.centuries.toJavaPeriod())
        assertEquals(java.time.Period.ZERO, 0L.centuries.toJavaPeriod())

        val period = 1.centuries.toJavaPeriod()
        assertEquals(100, period.years)
        assertEquals(0, period.months)
        assertEquals(0, period.days)
    }

    @Test
    fun `converts decades to Java Period`() {
        assertEquals(java.time.Period.ZERO, 0.decades.toJavaPeriod())
        assertEquals(java.time.Period.ZERO, 0L.decades.toJavaPeriod())

        val period = 1.decades.toJavaPeriod()
        assertEquals(10, period.years)
        assertEquals(0, period.months)
        assertEquals(0, period.days)
    }

    @Test
    fun `converts years to Java Period`() {
        assertEquals(java.time.Period.ZERO, 0.years.toJavaPeriod())
        assertEquals(java.time.Period.ZERO, 0L.years.toJavaPeriod())

        val period = 1.years.toJavaPeriod()
        assertEquals(1, period.years)
        assertEquals(0, period.months)
        assertEquals(0, period.days)
    }

    @Test
    fun `converts months to Java Period`() {
        assertEquals(java.time.Period.ZERO, 0.months.toJavaPeriod())
        assertEquals(java.time.Period.ZERO, 0L.months.toJavaPeriod())

        val period = 1.months.toJavaPeriod()
        assertEquals(0, period.years)
        assertEquals(1, period.months)
        assertEquals(0, period.days)
    }

    @Test
    fun `converts weeks to Java Period`() {
        assertEquals(java.time.Period.ZERO, 0.weeks.toJavaPeriod())
        assertEquals(java.time.Period.ZERO, 0L.weeks.toJavaPeriod())

        val period = 1.weeks.toJavaPeriod()
        assertEquals(0, period.years)
        assertEquals(0, period.months)
        assertEquals(7, period.days)
    }

    @Test
    fun `converts days to Java Period`() {
        assertEquals(java.time.Period.ZERO, 0.days.toJavaPeriod())
        assertEquals(java.time.Period.ZERO, 0L.days.toJavaPeriod())

        val period = 1.days.toJavaPeriod()
        assertEquals(0, period.years)
        assertEquals(0, period.months)
        assertEquals(1, period.days)
    }

    @Test
    fun `converts days to Java Duration`() {
        assertEquals(java.time.Duration.ZERO, 0.days.toJavaDuration())
        assertEquals(java.time.Duration.ZERO, 0L.days.toJavaDuration())

        assertEquals(1L, 1.days.toJavaDuration().toDays())
        assertEquals(-1L, (-1L).days.toJavaDuration().toDays())
    }

    @Test
    fun `converts hours to Java Duration`() {
        assertEquals(java.time.Duration.ZERO, 0.hours.toJavaDuration())
        assertEquals(java.time.Duration.ZERO, 0L.hours.toJavaDuration())
        assertEquals(1L, 1.hours.toJavaDuration().toHours())
        assertEquals(-1L, (-1L).hours.toJavaDuration().toHours())
    }

    @Test
    fun `converts minutes to Java Duration`() {
        assertEquals(java.time.Duration.ZERO, 0.minutes.toJavaDuration())
        assertEquals(java.time.Duration.ZERO, 0L.minutes.toJavaDuration())
        assertEquals(1L, 1.minutes.toJavaDuration().toMinutes())
        assertEquals(-1L, (-1L).minutes.toJavaDuration().toMinutes())
    }

    @Test
    fun `converts seconds to Java Duration`() {
        assertEquals(java.time.Duration.ZERO, 0.seconds.toJavaDuration())
        assertEquals(java.time.Duration.ZERO, 0L.seconds.toJavaDuration())
        assertEquals(1L, 1.seconds.toJavaDuration().seconds)
        assertEquals(-1L, (-1L).seconds.toJavaDuration().seconds)
    }

    @Test
    fun `converts milliseconds to Java Duration`() {
        assertEquals(java.time.Duration.ZERO, 0.milliseconds.toJavaDuration())
        assertEquals(java.time.Duration.ZERO, 0L.milliseconds.toJavaDuration())
        assertEquals(1_000_000_000L, 1_000_000_000.milliseconds.toJavaDuration().toMillis())
        assertEquals(-1L, (-1L).milliseconds.toJavaDuration().toMillis())
    }

    @Test
    fun `converts microseconds to Java Duration`() {
        assertEquals(java.time.Duration.ZERO, 0.microseconds.toJavaDuration())
        assertEquals(java.time.Duration.ZERO, 0L.microseconds.toJavaDuration())
        assertEquals(1_000_000_000L, 1_000_000_000.microseconds.toJavaDuration().toNanos() / 1000)
        assertEquals(-1L, (-1L).microseconds.toJavaDuration().toNanos() / 1000)
    }

    @Test
    fun `converts nanoseconds to Java Duration`() {
        assertEquals(java.time.Duration.ZERO, 0.nanoseconds.toJavaDuration())
        assertEquals(java.time.Duration.ZERO, 0L.nanoseconds.toJavaDuration())
        assertEquals(1_000_000_000L, 1_000_000_000.nanoseconds.toJavaDuration().toNanos())
        assertEquals(-1L, (-1L).nanoseconds.toJavaDuration().toNanos())
    }

    @Test
    fun `converts a Java Clock to an Island Time Clock`() {
        val javaClock = java.time.Clock.fixed(
            java.time.Instant.ofEpochSecond(234_678_901L, 123456789),
            java.time.ZoneId.of("America/New_York")
        )

        val islandClock = javaClock.asIslandClock()

        assertEquals(
            java.time.Instant.ofEpochSecond(234_678_901L, 123456789),
            islandClock.readPlatformInstant()
        )
        assertEquals(
            Instant.fromSecondOfUnixEpoch(234_678_901L, 123456789),
            islandClock.readInstant()
        )
        assertEquals(
            Instant.fromSecondOfUnixEpoch(234_678_901L, 123456789).millisecondsSinceUnixEpoch,
            islandClock.readMilliseconds()
        )
        assertEquals(TimeZone("America/New_York"), islandClock.zone)
    }
}
