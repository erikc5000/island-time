package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.parser.TemporalParseException
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class InstantIntervalTest : AbstractIslandTimeTest() {
    @Test
    fun `EMPTY returns an empty interval`() {
        assertTrue { InstantInterval.EMPTY.isEmpty() }
        assertTrue { InstantInterval.EMPTY.isBounded() }
        assertTrue { InstantInterval.EMPTY.hasBoundedStart() }
        assertTrue { InstantInterval.EMPTY.hasBoundedEnd() }
        assertFalse { InstantInterval.EMPTY.isUnbounded() }
        assertFalse { InstantInterval.EMPTY.hasUnboundedStart() }
        assertFalse { InstantInterval.EMPTY.hasUnboundedEnd() }
    }

    @Test
    fun `UNBOUNDED returns an unbounded interval`() {
        assertFalse { InstantInterval.UNBOUNDED.isEmpty() }
        assertTrue { InstantInterval.UNBOUNDED.isUnbounded() }
        assertTrue { InstantInterval.UNBOUNDED.hasUnboundedStart() }
        assertTrue { InstantInterval.UNBOUNDED.hasUnboundedEnd() }
        assertFalse { InstantInterval.UNBOUNDED.isBounded() }
        assertFalse { InstantInterval.UNBOUNDED.hasBoundedStart() }
        assertFalse { InstantInterval.UNBOUNDED.hasBoundedEnd() }
    }

    @Test
    fun `inclusive end creation handles unbounded correctly`() {
        val start = Instant.UNIX_EPOCH
        val max = Instant.MAX

        assertTrue { (start..max).hasUnboundedEnd() }
        assertFailsWith<DateTimeException> { start..max - 1.nanoseconds }
        assertEquals(start until max - 1.nanoseconds, start..max - 2.nanoseconds)
    }

    @Test
    fun `contains() returns true for dates within bounded range`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(2L.days.inSeconds)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { Instant.UNIX_EPOCH in start..end }
        assertTrue { OffsetDateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO) in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded end`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant.MAX

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { Instant.UNIX_EPOCH in start..end }
        assertTrue { OffsetDateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO) in start..end }
    }

    @Test
    fun `contains() returns false for out of range dates`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(2L.days.inSeconds)

        assertFalse { Instant(3L.days.inSeconds, (-1).nanoseconds) in start..end }
        assertFalse { Instant((-3L).days.inSeconds, 1.nanoseconds) in start..end }
        assertFalse {
            OffsetDateTime.fromSecondsSinceUnixEpoch(2L.days.inSeconds, 1.nanoseconds, UtcOffset.ZERO) in start..end
        }
    }

    @Test
    fun `until infix operator constructs a range with non-inclusive end`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(2L.days.inSeconds)
        val range = start until end

        assertEquals(start, range.first)
        assertEquals(start, range.start)
        assertEquals(Instant(2L.days.inSeconds, (-1).nanoseconds), range.last)
        assertEquals(Instant(2L.days.inSeconds), range.endExclusive)
    }

    @Test
    fun `toString() returns an ISO-8601 time interval representation`() {
        assertEquals(
            "1969-12-31T00:00Z/1970-01-02T00:00Z",
            (Instant((-1L).days.inSeconds) until Instant(1L.days.inSeconds)).toString()
        )

        assertEquals(
            "../1970-01-02T00:00Z",
            (Instant.MIN until Instant(1L.days.inSeconds)).toString()
        )

        assertEquals(
            "1969-12-31T00:00Z/..",
            (Instant((-1L).days.inSeconds) until Instant.MAX).toString()
        )

        assertEquals(
            "1969-12-31T00:00Z/..",
            (Instant((-1L).days.inSeconds)..Instant.MAX).toString()
        )

        assertEquals(
            "../..",
            (Instant.MIN until Instant.MAX).toString()
        )

        assertEquals(
            "../..",
            InstantInterval.UNBOUNDED.toString()
        )

        assertEquals(
            "",
            InstantInterval.EMPTY.toString()
        )
    }

    @Test
    fun `String_toInstantInterval() converts an empty string to an empty interval`() {
        assertEquals(InstantInterval.EMPTY, "".toInstantInterval())
    }

    @Test
    fun `String_toInstantInterval() throws an exception when the format is invalid`() {
        assertFailsWith<TemporalParseException> { "1970-01-01/1970-01-01".toInstantInterval() }
        assertFailsWith<TemporalParseException> { "1970-01-01T00:00Z/19700101T00Z".toInstantInterval() }
    }

    @Test
    fun `String_toInstantInterval() parses ISO-8601 time interval strings in extended format by default`() {
        assertEquals(
            Instant((-1L).days.inSeconds) until Instant(1L.days.inSeconds),
            "1969-12-31T00:00Z/1970-01-02T00:00Z".toInstantInterval()
        )
        assertEquals(Instant.MIN until Instant(1L.days.inSeconds), "../1970-01-02T00:00Z".toInstantInterval())
        assertEquals(Instant((-1L).days.inSeconds) until Instant.MAX, "1969-12-31T00:00Z/..".toInstantInterval())
        assertEquals(Instant((-1L).days.inSeconds)..Instant.MAX, "1969-12-31T00:00Z/..".toInstantInterval())
        assertEquals(InstantInterval.UNBOUNDED, "../..".toInstantInterval())
    }

    @Test
    fun `random() returns an instant within range`() {
        val range = Instant((-2L).days.inSeconds)..Instant(2L.days.inSeconds)
        val randomInstant = range.random()
        assertTrue { randomInstant in range }
    }

    @Test
    fun `lengthIn* properties return zero when the range is empty`() {
        assertEquals(0L.days, InstantInterval.EMPTY.lengthInDays)
        assertEquals(0L.hours, InstantInterval.EMPTY.lengthInHours)
        assertEquals(0L.minutes, InstantInterval.EMPTY.lengthInMinutes)
        assertEquals(0L.seconds, InstantInterval.EMPTY.lengthInSeconds)
        assertEquals(0L.milliseconds, InstantInterval.EMPTY.lengthInMilliseconds)
        assertEquals(0L.microseconds, InstantInterval.EMPTY.lengthInMicroseconds)
        assertEquals(0L.nanoseconds, InstantInterval.EMPTY.lengthInNanoseconds)
    }

    @Test
    fun `lengthIn* properties throw an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.lengthInDays }
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.lengthInHours }
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.lengthInMinutes }
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.lengthInSeconds }
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.lengthInMilliseconds }
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.lengthInMicroseconds }
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.lengthInNanoseconds }
    }

    @Test
    fun `lengthInNanoseconds returns 1 in an inclusive interval where the start and end instant are the same`() {
        val instant = Instant(2L.days.inSeconds)
        assertEquals(1L.nanoseconds, (instant..instant).lengthInNanoseconds)
    }

    @Test
    fun `convert empty DateRange to InstantInterval`() {
        assertEquals(InstantInterval.EMPTY, DateRange.EMPTY.toInstantIntervalAt(TimeZone.UTC))
    }

    @Test
    fun `convert unbounded DateRange to InstantInterval`() {
        assertEquals(InstantInterval.UNBOUNDED, DateRange.UNBOUNDED.toInstantIntervalAt(TimeZone.UTC))
    }

    @Test
    fun `convert half-bounded DateRange to InstantInterval`() {
        val dateRange1 = Date(1968, 10, 5)..Date.MAX

        assertEquals(
            "1968-10-05T05:00Z".toInstant() until Instant.MAX,
            dateRange1.toInstantIntervalAt((-5).hours.asUtcOffset().asTimeZone())
        )

        val dateRange2 = Date.MIN..Date(2000, 1, 3)

        assertEquals(
            Instant.MIN until "2000-01-04T05:00Z".toInstant(),
            dateRange2.toInstantIntervalAt((-5).hours.asUtcOffset().asTimeZone())
        )
    }

    @Test
    fun `convert bounded DateRange to InstantInterval`() {
        val dateRange = Date(1968, 10, 5)..Date(2000, 1, 3)

        assertEquals(
            "1968-10-05T05:00Z".toInstant() until "2000-01-04T05:00Z".toInstant(),
            dateRange.toInstantIntervalAt((-5).hours.asUtcOffset().asTimeZone())
        )
    }

    @Test
    fun `convert empty OffsetDateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.EMPTY, OffsetDateTimeInterval.EMPTY.asInstantInterval())
    }

    @Test
    fun `convert unbounded OffsetDateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.UNBOUNDED, OffsetDateTimeInterval.UNBOUNDED.asInstantInterval())
    }

    @Test
    fun `convert half-bounded OffsetDateTimeInterval to InstantInterval`() {
        val offsetDateTimeInterval1 = "1968-10-05T05:00-05:00".toOffsetDateTime() until OffsetDateTime.MAX

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until Instant.MAX,
            offsetDateTimeInterval1.asInstantInterval()
        )

        val offsetDateTimeInterval2 = OffsetDateTime.MIN until "2000-01-03T10:00-05:00".toOffsetDateTime()

        assertEquals(
            Instant.MIN until "2000-01-03T15:00Z".toInstant(),
            offsetDateTimeInterval2.asInstantInterval()
        )
    }

    @Test
    fun `convert bounded OffsetDateTimeInterval to InstantInterval`() {
        val offsetDateTimeInterval =
            "1968-10-05T05:00-05:00".toOffsetDateTime() until "2000-01-03T10:00-05:00".toOffsetDateTime()

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until "2000-01-03T15:00Z".toInstant(),
            offsetDateTimeInterval.asInstantInterval()
        )
    }

    @Test
    fun `convert empty ZonedDateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.EMPTY, ZonedDateTimeInterval.EMPTY.asInstantInterval())
    }

    @Test
    fun `convert unbounded ZonedDateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.UNBOUNDED, ZonedDateTimeInterval.UNBOUNDED.asInstantInterval())
    }

    @Test
    fun `convert half-bounded ZonedDateTimeInterval to InstantInterval`() {
        val zonedDateTimeInterval1 =
            "1968-10-05T05:00-05:00".toZonedDateTime() until ZonedDateTimeInterval.UNBOUNDED.endExclusive

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until Instant.MAX,
            zonedDateTimeInterval1.asInstantInterval()
        )

        val zonedDateTimeInterval2 =
            ZonedDateTimeInterval.UNBOUNDED.start until "2000-01-03T10:00-05:00".toZonedDateTime()

        assertEquals(
            Instant.MIN until "2000-01-03T15:00Z".toInstant(),
            zonedDateTimeInterval2.asInstantInterval()
        )
    }

    @Test
    fun `convert bounded ZonedDateTimeInterval to InstantInterval`() {
        val zonedDateTimeInterval =
            "1968-10-05T05:00-05:00".toZonedDateTime() until "2000-01-03T10:00-05:00".toZonedDateTime()

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until "2000-01-03T15:00Z".toInstant(),
            zonedDateTimeInterval.asInstantInterval()
        )
    }

    @Test
    fun `durationBetween() returns the duration between two instants`() {
        assertEquals(
            0.milliseconds.asDuration(),
            durationBetween(Instant(1L.milliseconds), Instant(1L.milliseconds))
        )

        assertEquals(
            1.milliseconds.asDuration(),
            durationBetween(Instant.UNIX_EPOCH, Instant(1L.milliseconds))
        )

        assertEquals(
            (-1).milliseconds.asDuration(),
            durationBetween(Instant(1L.milliseconds), Instant.UNIX_EPOCH)
        )

        assertEquals(
            (-3).milliseconds.asDuration(),
            durationBetween(Instant(1L.milliseconds), Instant((-2L).milliseconds))
        )
    }

    @Test
    fun `daysBetween returns the number of 24-hour days between two instants`() {
        assertEquals(
            0L.days,
            daysBetween(Instant(0L.seconds, 1.nanoseconds), Instant(86400L.seconds))
        )

        assertEquals(
            0L.days,
            daysBetween(Instant(86400L.seconds), Instant(0L.seconds, 1.nanoseconds))
        )

        assertEquals(
            1L.days,
            daysBetween(Instant.UNIX_EPOCH, Instant(86400L.seconds))
        )
    }

    @Test
    fun `hoursBetween() returns the number of whole hours between two instants`() {
        assertEquals(
            0L.hours,
            hoursBetween(
                Instant(0L.seconds, 1.nanoseconds),
                Instant(3600L.seconds)
            )
        )

        assertEquals(
            0L.hours,
            hoursBetween(Instant(3600L.seconds), Instant(0L.seconds, 1.nanoseconds))
        )

        assertEquals(
            1L.hours,
            hoursBetween(Instant.UNIX_EPOCH, Instant(3600L.seconds))
        )
    }

    @Test
    fun `minutesBetween() returns the number of whole minutes between two instants`() {
        assertEquals(
            0L.minutes,
            minutesBetween(Instant(0L.seconds, 1.nanoseconds), Instant(60L.seconds))
        )

        assertEquals(
            0L.minutes,
            minutesBetween(Instant(60L.seconds), Instant(0L.seconds, 1.nanoseconds))
        )

        assertEquals(
            1L.minutes,
            minutesBetween(Instant.UNIX_EPOCH, Instant(60L.seconds))
        )
    }

    @Test
    fun `secondsBetween() returns the number of whole seconds between two instants`() {
        assertEquals(
            0L.seconds,
            secondsBetween(Instant(0L.seconds, 1.nanoseconds), Instant(1L.seconds))
        )

        assertEquals(
            0L.seconds,
            secondsBetween(Instant(1L.seconds), Instant(0L.seconds, 1.nanoseconds))
        )

        assertEquals(
            0L.seconds,
            secondsBetween(Instant(0L.seconds, 999_999_999.nanoseconds), Instant.UNIX_EPOCH)
        )

        assertEquals(
            0L.seconds,
            secondsBetween(Instant.UNIX_EPOCH, Instant(0L.seconds, 999_999_999.nanoseconds))
        )

        assertEquals(
            1L.seconds,
            secondsBetween(Instant.UNIX_EPOCH, Instant(1L.seconds))
        )

        assertEquals(
            (-1L).seconds,
            secondsBetween(Instant(1L.seconds), Instant.UNIX_EPOCH)
        )

        assertEquals(
            1L.seconds,
            secondsBetween(
                Instant(0L.seconds, 500_000_000.nanoseconds),
                Instant(1L.seconds, 500_000_000.nanoseconds)
            )
        )
        assertEquals(
            1L.seconds,
            secondsBetween(
                Instant(0L.seconds, 500_000_000.nanoseconds),
                Instant(2L.seconds, 499_999_999.nanoseconds)
            )
        )
    }

    @Test
    fun `millisecondsBetween() returns the number of whole milliseconds between two instants`() {
        assertEquals(
            0L.milliseconds,
            millisecondsBetween(
                Instant(0L.seconds, 1.nanoseconds),
                Instant(0L.seconds, 1_000_000.nanoseconds)
            )
        )

        assertEquals(
            0L.milliseconds,
            millisecondsBetween(
                Instant(0L.seconds, 1_000_000.nanoseconds),
                Instant(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1L.milliseconds,
            millisecondsBetween(
                Instant.UNIX_EPOCH,
                Instant(0L.seconds, 1_000_000.nanoseconds)
            )
        )
    }

    @Test
    fun `microsecondsBetween() returns the number of whole microseconds between two instants`() {
        assertEquals(
            0L.microseconds,
            microsecondsBetween(
                Instant(0L.seconds, 1.nanoseconds),
                Instant(0L.seconds, 1_000.nanoseconds)
            )
        )

        assertEquals(
            0L.microseconds,
            microsecondsBetween(
                Instant(0L.seconds, 1_000.nanoseconds),
                Instant(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1L.microseconds,
            microsecondsBetween(Instant.UNIX_EPOCH, Instant(0L.seconds, 1_000.nanoseconds))
        )
    }

    @Test
    fun `nanosecondsBetween() returns zero when both instants are the same`() {
        assertEquals(
            0L.nanoseconds,
            nanosecondsBetween(Instant(1L.seconds, 1.nanoseconds), Instant(1L.seconds, 1.nanoseconds))
        )
    }

    @Test
    fun `nanosecondsBetween() returns the number of nanoseconds between two instants`() {
        assertEquals(
            2L.nanoseconds,
            nanosecondsBetween(
                Instant(0L.seconds, (-1).nanoseconds),
                Instant(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            (-1_000_000_000L).nanoseconds,
            nanosecondsBetween(Instant.UNIX_EPOCH, Instant((-1L).seconds))
        )
    }

    @Test
    fun `nanosecondsBetween() throws an exception when the result overflows`() {
        assertFailsWith<ArithmeticException> { nanosecondsBetween(Instant.MIN, Instant.UNIX_EPOCH) }
    }
}