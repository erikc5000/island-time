package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
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
    fun `contains returns true for dates within bounded range`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(2L.days.inSeconds)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { Instant.UNIX_EPOCH in start..end }
        assertTrue { OffsetDateTime.fromSecondOfUnixEpoch(0L, 0, UtcOffset.ZERO) in start..end }
    }

    @Test
    fun `contains returns true for dates within range with unbounded end`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant.MAX

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { Instant.UNIX_EPOCH in start..end }
        assertTrue { OffsetDateTime.fromSecondOfUnixEpoch(0L, 0, UtcOffset.ZERO) in start..end }
    }

    @Test
    fun `contains returns false for out of range dates`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(2L.days.inSeconds)

        assertFalse { Instant(3L.days.inSeconds, (-1).nanoseconds) in start..end }
        assertFalse { Instant((-3L).days.inSeconds, 1.nanoseconds) in start..end }
        assertFalse {
            OffsetDateTime.fromSecondsSinceUnixEpoch(
                2L.days.inSeconds,
                1.nanoseconds,
                UtcOffset.ZERO
            ) in start..end
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
    fun `toString returns an ISO-8601 time interval representation`() {
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
    fun `String_toInstantInterval converts an empty string to an empty interval`() {
        assertEquals(InstantInterval.EMPTY, "".toInstantInterval())
    }

    @Test
    fun `String_toInstantInterval throws an exception when the format is invalid`() {
        assertFailsWith<DateTimeParseException> { "1970-01-01/1970-01-01".toInstantInterval() }
        assertFailsWith<DateTimeParseException> { "1970-01-01T00:00Z/19700101T00Z".toInstantInterval() }
    }

    @Test
    fun `String_toInstantInterval parses ISO-8601 time interval strings in extended format by default`() {
        assertEquals(
            Instant((-1L).days.inSeconds) until Instant(1L.days.inSeconds),
            "1969-12-31T00:00Z/1970-01-02T00:00Z".toInstantInterval()
        )
        assertEquals(
            Instant.MIN until Instant(1L.days.inSeconds),
            "../1970-01-02T00:00Z".toInstantInterval()
        )
        assertEquals(
            Instant((-1L).days.inSeconds) until Instant.MAX,
            "1969-12-31T00:00Z/..".toInstantInterval()
        )
        assertEquals(
            Instant((-1L).days.inSeconds)..Instant.MAX,
            "1969-12-31T00:00Z/..".toInstantInterval()
        )
        assertEquals(InstantInterval.UNBOUNDED, "../..".toInstantInterval())
    }

    @Test
    fun `random returns an instant within range`() {
        val range = Instant((-2L).days.inSeconds)..Instant(2L.days.inSeconds)
        val randomInstant = range.random()
        assertTrue { randomInstant in range }
    }

    @Test
    fun `random returns a instant within the interval`() {
        val interval = Instant((-2L).days.inSeconds) until Instant(2L.days.inSeconds)
        assertTrue { interval.random() in interval }
    }

    @Test
    fun `random throws an exception when the interval is empty`() {
        assertFailsWith<NoSuchElementException> { InstantInterval.EMPTY.random() }
    }

    @Test
    fun `random throws an exception when the interval is not bounded`() {
        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.random() }
        assertFailsWith<UnsupportedOperationException> { InstantInterval(start = Instant.UNIX_EPOCH).random() }
        assertFailsWith<UnsupportedOperationException> { InstantInterval(endExclusive = Instant.UNIX_EPOCH).random() }
    }

    @Test
    fun `randomOrNull returns null when the interval is empty`() {
        assertNull(InstantInterval.EMPTY.randomOrNull())
    }

    @Test
    fun `randomOrNull returns null when the interval is not bounded`() {
        assertNull(InstantInterval.UNBOUNDED.randomOrNull())
        assertNull(InstantInterval(start = Instant.UNIX_EPOCH).randomOrNull())
        assertNull(InstantInterval(endExclusive = Instant.UNIX_EPOCH).randomOrNull())
    }

    @Test
    fun `randomOrNull returns a date within the interval`() {
        val interval = Instant((2L).days.inSeconds)..Instant(2L.days.inSeconds)
        assertTrue { interval.randomOrNull()!! in interval }
    }

    @Test
    fun `lengthIn properties return zero when the range is empty`() {
        assertEquals(0L.hours, InstantInterval.EMPTY.lengthInHours)
        assertEquals(0L.minutes, InstantInterval.EMPTY.lengthInMinutes)
        assertEquals(0L.seconds, InstantInterval.EMPTY.lengthInSeconds)
        assertEquals(0L.milliseconds, InstantInterval.EMPTY.lengthInMilliseconds)
        assertEquals(0L.microseconds, InstantInterval.EMPTY.lengthInMicroseconds)
        assertEquals(0L.nanoseconds, InstantInterval.EMPTY.lengthInNanoseconds)
    }

    @Test
    fun `lengthIn properties throw an exception when the interval is unbounded`() {
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
}
