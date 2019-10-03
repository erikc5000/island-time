package io.islandtime.ranges

import io.islandtime.Instant
import io.islandtime.OffsetDateTime
import io.islandtime.UtcOffset
import io.islandtime.measures.*
import kotlin.test.*

class InstantIntervalTest {
    @Test
    fun `EMPTY returns an empty interval`() {
        assertTrue { InstantInterval.EMPTY.isEmpty() }
        assertTrue { InstantInterval.EMPTY.isBounded }
        assertTrue { InstantInterval.EMPTY.hasBoundedStart }
        assertTrue { InstantInterval.EMPTY.hasBoundedEnd }
        assertFalse { InstantInterval.EMPTY.isUnbounded }
        assertFalse { InstantInterval.EMPTY.hasUnboundedStart }
        assertFalse { InstantInterval.EMPTY.hasUnboundedEnd }
    }

    @Test
    fun `UNBOUNDED returns an unbounded interval`() {
        assertFalse { InstantInterval.UNBOUNDED.isEmpty() }
        assertTrue { InstantInterval.UNBOUNDED.isUnbounded }
        assertTrue { InstantInterval.UNBOUNDED.hasUnboundedStart }
        assertTrue { InstantInterval.UNBOUNDED.hasUnboundedEnd }
        assertFalse { InstantInterval.UNBOUNDED.isBounded }
        assertFalse { InstantInterval.UNBOUNDED.hasBoundedStart }
        assertFalse { InstantInterval.UNBOUNDED.hasBoundedEnd }
    }

    @Test
    fun `contains() returns true for dates within bounded range`() {
        val start = Instant((-2).days)
        val end = Instant(2.days)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { Instant.UNIX_EPOCH in start..end }
        assertTrue { OffsetDateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO) in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded end`() {
        val start = Instant((-2).days)
        val end = Instant.MAX

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { Instant.UNIX_EPOCH in start..end }
        assertTrue { OffsetDateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO) in start..end }
    }

    @Test
    fun `contains() returns false for out of range dates`() {
        val start = Instant((-2).days)
        val end = Instant(2.days)

        assertFalse { Instant(3.days, (-1).nanoseconds) in start..end }
        assertFalse { Instant((-3).days, 1.nanoseconds) in start..end }
        assertFalse {
            OffsetDateTime.fromSecondsSinceUnixEpoch(2L.days.inSeconds, 1.nanoseconds, UtcOffset.ZERO) in start..end
        }
    }

    @Test
    fun `until infix operator constructs a range with non-inclusive end`() {
        val start = Instant((-2).days)
        val end = Instant(2.days)
        val range = start until end

        assertEquals(start, range.first)
        assertEquals(start, range.start)
        assertEquals(Instant(2.days, (-1).nanoseconds), range.last)
        assertEquals(Instant(2.days), range.endExclusive)
    }

    @Test
    fun `random() returns an instant within range`() {
        val range = Instant((-2).days)..Instant(2.days)
        val randomInstant = range.random()
        assertTrue { randomInstant in range }
    }

    @Test
    fun `days property returns 0 when range is empty`() {
        assertEquals(0L.days, InstantInterval.EMPTY.days)
    }

    @Test
    fun `days property throws an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.days }
    }

    @Test
    fun `hours property returns 0 when range is empty`() {
        assertEquals(0L.hours, InstantInterval.EMPTY.hours)
    }

    @Test
    fun `hours property throws an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.hours }
    }

    @Test
    fun `minutes property returns 0 when range is empty`() {
        assertEquals(0L.minutes, InstantInterval.EMPTY.minutes)
    }

    @Test
    fun `minutes property throws an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.minutes }
    }

    @Test
    fun `seconds property returns 0 when range is empty`() {
        assertEquals(0L.seconds, InstantInterval.EMPTY.seconds)
    }

    @Test
    fun `seconds property throws an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.seconds }
    }

    @Test
    fun `milliseconds property returns 0 when range is empty`() {
        assertEquals(0L.milliseconds, InstantInterval.EMPTY.milliseconds)
    }

    @Test
    fun `milliseconds property throws an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.milliseconds }
    }

    @Test
    fun `microseconds property returns 0 when range is empty`() {
        assertEquals(0L.microseconds, InstantInterval.EMPTY.microseconds)
    }

    @Test
    fun `microseconds property throws an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.microseconds }
    }

    @Test
    fun `nanoseconds property returns 0 when range is empty`() {
        assertEquals(0L.nanoseconds, InstantInterval.EMPTY.nanoseconds)
    }

    @Test
    fun `nanoseconds property throws an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.nanoseconds }
    }

    @Test
    fun `nanoseconds property returns 1 when the start and end instant are the same`() {
        val instant = Instant(2.days)
        assertEquals(1L.nanoseconds, (instant..instant).nanoseconds)
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