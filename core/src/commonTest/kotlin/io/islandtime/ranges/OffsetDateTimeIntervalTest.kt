package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class OffsetDateTimeIntervalTest : AbstractIslandTimeTest() {
    @Test
    fun `EMPTY returns an empty interval`() {
        assertTrue { OffsetDateTimeInterval.EMPTY.isEmpty() }
        assertTrue { OffsetDateTimeInterval.EMPTY.isBounded }
        assertTrue { OffsetDateTimeInterval.EMPTY.hasBoundedStart }
        assertTrue { OffsetDateTimeInterval.EMPTY.hasBoundedEnd }
        assertFalse { OffsetDateTimeInterval.EMPTY.isUnbounded }
        assertFalse { OffsetDateTimeInterval.EMPTY.hasUnboundedStart }
        assertFalse { OffsetDateTimeInterval.EMPTY.hasUnboundedEnd }
    }

    @Test
    fun `UNBOUNDED returns an unbounded interval`() {
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.isEmpty() }
        assertTrue { OffsetDateTimeInterval.UNBOUNDED.isUnbounded }
        assertTrue { OffsetDateTimeInterval.UNBOUNDED.hasUnboundedStart }
        assertTrue { OffsetDateTimeInterval.UNBOUNDED.hasUnboundedEnd }
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.isBounded }
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.hasBoundedStart }
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.hasBoundedEnd }
    }

    @Test
    fun `contains() returns true for dates within bounded range`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 12) at Time.MIDNIGHT at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { "2019-03-11T22:00-06:00".toOffsetDateTime() in start..end }
        assertTrue { "2019-03-10T05:00Z".toInstant() in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded end`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at UtcOffset((-4).hours)
        val end = DateTime.MAX at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { DateTime.MAX at "Etc/UTC".toTimeZone() in start..end }
        assertTrue { DateTime.MAX at "America/Denver".toTimeZone() in start..end }
        assertTrue { Instant.MAX in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded start`() {
        val start = DateTime.MIN at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { DateTime.MIN at "Etc/UTC".toTimeZone() in start..end }
        assertTrue { DateTime.MIN at "America/Denver".toTimeZone() in start..end }
        assertTrue { Instant.MIN in start..end }
    }

    @Test
    fun `contains() returns false for out of range dates`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2019, Month.MARCH, 12) at Time.MIDNIGHT at UtcOffset((-4).hours)

        assertFalse { start - 1.nanoseconds in start..end }
        assertFalse { end + 1.nanoseconds in start..end }
        assertFalse { "2019-03-11T23:00-06:00".toOffsetDateTime() in start..end }
        assertFalse { "2019-03-10T04:59:59Z".toInstant() in start..end }
    }

    @Test
    fun `until infix operator constructs an interval with non-inclusive end`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 12) at Time.MIDNIGHT at UtcOffset((-4).hours)
        val range = start until end

        assertEquals(start, range.start)
        assertEquals(end - 1.nanoseconds, range.endInclusive)
        assertEquals(end, range.endExclusive)
    }

    @Test
    fun `random() returns a zoned date-time within range`() {
        val start = Date(2019, Month.NOVEMBER, 1) at Time.MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.NOVEMBER, 20) at Time.MIDNIGHT at UtcOffset((-5).hours)
        val range = start..end
        val randomInstant = range.random()
        assertTrue { randomInstant in range }
        assertEquals(UtcOffset((-4).hours), randomInstant.offset)
    }

    @Test
    fun `asPeriod() returns a zeroed out period when the range is empty`() {
        assertEquals(Period.ZERO, OffsetDateTimeInterval.EMPTY.asPeriod())
    }

    @Test
    fun `asPeriod() throws an exception when the range is unbounded`() {
        assertFailsWith<UnsupportedOperationException> {
            OffsetDateTimeInterval(endExclusive = "2018-09-10T09:15-06:00".toOffsetDateTime()).asPeriod()
        }
    }

    @Test
    fun `period is correct when bounded`() {
        assertEquals(
            periodOf(1.years, 1.months, 1.days),
            ("2018-09-10T09:15-06:00".toOffsetDateTime() until "2019-10-11T09:15-07:00".toOffsetDateTime()).asPeriod()
        )

        assertEquals(
            periodOf(1.years, 1.months, 1.days),
            periodBetween("2018-09-10T09:15-06:00".toOffsetDateTime(), "2019-10-11T09:15-07:00".toOffsetDateTime())
        )
    }

    @Test
    fun `lengthIn* properties return zero when the range is empty`() {
        assertEquals(0.years, OffsetDateTimeInterval.EMPTY.lengthInYears)
        assertEquals(0.months, OffsetDateTimeInterval.EMPTY.lengthInMonths)
        assertEquals(0L.weeks, OffsetDateTimeInterval.EMPTY.lengthInWeeks)
        assertEquals(0L.days, OffsetDateTimeInterval.EMPTY.lengthInDays)
        assertEquals(0L.hours, OffsetDateTimeInterval.EMPTY.lengthInHours)
        assertEquals(0L.minutes, OffsetDateTimeInterval.EMPTY.lengthInMinutes)
        assertEquals(0L.seconds, OffsetDateTimeInterval.EMPTY.lengthInSeconds)
        assertEquals(0L.milliseconds, OffsetDateTimeInterval.EMPTY.lengthInMilliseconds)
        assertEquals(0L.microseconds, OffsetDateTimeInterval.EMPTY.lengthInMicroseconds)
        assertEquals(0L.nanoseconds, OffsetDateTimeInterval.EMPTY.lengthInNanoseconds)
    }

    @Test
    fun `lengthIn* properties throw an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInYears }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInMonths }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInWeeks }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInDays }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInHours }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInMinutes }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInSeconds }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInMilliseconds }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInMicroseconds }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInNanoseconds }
    }

    @Test
    fun `lengthInNanoseconds returns 1 in an inclusive interval where the start and end instant are the same`() {
        val instant = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at UtcOffset((-4).hours)
        assertEquals(1L.nanoseconds, (instant..instant).lengthInNanoseconds)
    }
}