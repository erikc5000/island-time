package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class DateTimeIntervalTest : AbstractIslandTimeTest() {
    @Test
    fun `EMPTY returns an empty interval`() {
        assertTrue { DateTimeInterval.EMPTY.isEmpty() }
        assertTrue { DateTimeInterval.EMPTY.isBounded() }
        assertTrue { DateTimeInterval.EMPTY.hasBoundedStart() }
        assertTrue { DateTimeInterval.EMPTY.hasBoundedEnd() }
        assertFalse { DateTimeInterval.EMPTY.isUnbounded() }
        assertFalse { DateTimeInterval.EMPTY.hasUnboundedStart() }
        assertFalse { DateTimeInterval.EMPTY.hasUnboundedEnd() }
    }

    @Test
    fun `UNBOUNDED returns an unbounded interval`() {
        assertFalse { DateTimeInterval.UNBOUNDED.isEmpty() }
        assertTrue { DateTimeInterval.UNBOUNDED.isUnbounded() }
        assertTrue { DateTimeInterval.UNBOUNDED.hasUnboundedStart() }
        assertTrue { DateTimeInterval.UNBOUNDED.hasUnboundedEnd() }
        assertFalse { DateTimeInterval.UNBOUNDED.isBounded() }
        assertFalse { DateTimeInterval.UNBOUNDED.hasBoundedStart() }
        assertFalse { DateTimeInterval.UNBOUNDED.hasBoundedEnd() }
    }

    @Test
    fun `equality works correctly`() {
        assertEquals(DateTimeInterval.EMPTY, DateTimeInterval.EMPTY)
        assertNotEquals(DateTimeInterval.UNBOUNDED, DateTimeInterval.EMPTY)
        assertNotEquals(DateTimeInterval.EMPTY, DateTimeInterval.UNBOUNDED)

        val date1 = DateTime(2017, 1, 3, 30, 0, 0)
        val date2 = DateTime(2017, 2, 3, 30, 0, 0)
        val date3 = DateTime(2017, 3, 3, 30, 0, 0)

        assertEquals(DateTimeInterval.EMPTY, date2..date1)
        assertNotEquals(date1..date2, date1..date3)
        assertNotEquals(date1..date3, date1..date2)
        assertNotEquals(date2..date3, date1..date3)
        assertNotEquals(date1..date3, date2..date3)
    }

    @Test
    fun `inclusive end creation handles unbounded correctly`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT
        val max = DateTime.MAX

        assertTrue { (start..max).hasUnboundedEnd() }
        assertFailsWith<DateTimeException> { start..max - 1.nanoseconds }
        assertEquals(start until max - 1.nanoseconds, start..max - 2.nanoseconds)
    }

    @Test
    fun `contains() returns true for dates within bounded range`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT
        val end = Date(2019, Month.MARCH, 12) at Time.MIDNIGHT

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { "2019-03-11T22:00".toDateTime() in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded end`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT
        val end = DateTime.MAX

        assertTrue { start in start..end }
        assertTrue { end in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded start`() {
        val start = DateTime.MIN
        val end = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT

        assertTrue { start in start..end }
        assertTrue { end in start..end }
    }

    @Test
    fun `contains() returns false for out of range dates`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT
        val end = Date(2019, Month.MARCH, 12) at Time.MIDNIGHT

        assertFalse { start - 1.nanoseconds in start..end }
        assertFalse { end + 1.nanoseconds in start..end }
        assertFalse { DateTime.MAX in start..end }
        assertFalse { DateTime.MIN in start..end }
    }

    @Test
    fun `until infix operator constructs an interval with non-inclusive end`() {
        val start = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT
        val end = Date(2019, Month.MARCH, 12) at Time.MIDNIGHT
        val range = start until end

        assertEquals(start, range.start)
//        assertEquals(end - 1.nanoseconds, range.endInclusive)
        assertEquals(end, range.endExclusive)
    }

    @Test
    fun `random() returns a zoned date-time within range`() {
        val start = Date(2019, Month.NOVEMBER, 1) at Time.MIDNIGHT
        val end = Date(2019, Month.NOVEMBER, 20) at Time.MIDNIGHT
        val range = start..end
        val randomDateTime = range.random()
        assertTrue { randomDateTime in range }
    }

    @Test
    fun `asPeriod() returns a zeroed out period when the range is empty`() {
        assertEquals(Period.ZERO, DateTimeInterval.EMPTY.asPeriod())
    }

    @Test
    fun `asPeriod() throws an exception when the range is unbounded`() {
        assertFailsWith<UnsupportedOperationException> {
            DateTimeInterval(endExclusive = "2018-09-10T09:15".toDateTime()).asPeriod()
        }
    }

    @Test
    fun `period is correct when bounded`() {
        assertEquals(
            periodOf(1.years, 1.months, 1.days),
            ("2018-09-10T09:15".toDateTime() until "2019-10-11T09:15".toDateTime()).asPeriod()
        )

        assertEquals(
            periodOf(1.years, 1.months, 1.days),
            periodBetween("2018-09-10T09:15".toDateTime(), "2019-10-11T09:15".toDateTime())
        )
    }

    @Test
    fun `lengthIn* properties return zero when the range is empty`() {
        assertEquals(0.years, DateTimeInterval.EMPTY.lengthInYears)
        assertEquals(0.months, DateTimeInterval.EMPTY.lengthInMonths)
        assertEquals(0L.weeks, DateTimeInterval.EMPTY.lengthInWeeks)
        assertEquals(0L.days, DateTimeInterval.EMPTY.lengthInDays)
        assertEquals(0L.hours, DateTimeInterval.EMPTY.lengthInHours)
        assertEquals(0L.minutes, DateTimeInterval.EMPTY.lengthInMinutes)
        assertEquals(0L.seconds, DateTimeInterval.EMPTY.lengthInSeconds)
        assertEquals(0L.milliseconds, DateTimeInterval.EMPTY.lengthInMilliseconds)
        assertEquals(0L.microseconds, DateTimeInterval.EMPTY.lengthInMicroseconds)
        assertEquals(0L.nanoseconds, DateTimeInterval.EMPTY.lengthInNanoseconds)
    }

    @Test
    fun `lengthIn* properties throw an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInYears }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInMonths }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInWeeks }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInDays }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInHours }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInMinutes }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInSeconds }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInMilliseconds }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInMicroseconds }
        assertFailsWith<UnsupportedOperationException> { DateTimeInterval.UNBOUNDED.lengthInNanoseconds }
    }

    @Test
    fun `lengthInNanoseconds returns 1 in an inclusive interval where the start and end date-times are the same`() {
        val dateTime = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT
        assertEquals(1L.nanoseconds, (dateTime..dateTime).lengthInNanoseconds)
    }
}