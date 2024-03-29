package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.measures.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.random.Random
import kotlin.test.*

class ZonedDateTimeIntervalTest : AbstractIslandTimeTest() {
    private val nyZone = TimeZone("America/New_York")

    @Test
    fun `EMPTY returns an empty interval`() {
        assertTrue { ZonedDateTimeInterval.EMPTY.isEmpty() }
        assertTrue { ZonedDateTimeInterval.EMPTY.isBounded() }
        assertTrue { ZonedDateTimeInterval.EMPTY.hasBoundedStart() }
        assertTrue { ZonedDateTimeInterval.EMPTY.hasBoundedEnd() }
        assertFalse { ZonedDateTimeInterval.EMPTY.isUnbounded() }
        assertFalse { ZonedDateTimeInterval.EMPTY.hasUnboundedStart() }
        assertFalse { ZonedDateTimeInterval.EMPTY.hasUnboundedEnd() }
    }

    @Test
    fun `UNBOUNDED returns an unbounded interval`() {
        assertFalse { ZonedDateTimeInterval.UNBOUNDED.isEmpty() }
        assertTrue { ZonedDateTimeInterval.UNBOUNDED.isUnbounded() }
        assertTrue { ZonedDateTimeInterval.UNBOUNDED.hasUnboundedStart() }
        assertTrue { ZonedDateTimeInterval.UNBOUNDED.hasUnboundedEnd() }
        assertFalse { ZonedDateTimeInterval.UNBOUNDED.isBounded() }
        assertFalse { ZonedDateTimeInterval.UNBOUNDED.hasBoundedStart() }
        assertFalse { ZonedDateTimeInterval.UNBOUNDED.hasBoundedEnd() }
    }

    @Test
    fun `inclusive end creation handles unbounded correctly`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at nyZone
        val max = DateTime.MAX at nyZone

        assertTrue { (start..max).hasUnboundedEnd() }
        assertFailsWith<DateTimeException> { start..max - 1.nanoseconds }
        assertEquals(start until max - 1.nanoseconds, start..max - 2.nanoseconds)
    }

    @Test
    fun `contains returns true for dates within bounded range`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at nyZone
        val end = Date(2019, Month.MARCH, 12) at MIDNIGHT at nyZone

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { "2019-03-11T22:00-06:00[America/Denver]".toZonedDateTime() in start..end }
        assertTrue { "2019-03-10T05:00Z".toInstant() in start..end }
    }

    @Test
    fun `contains returns true for dates within range with unbounded end`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at nyZone
        val end = DateTime.MAX at nyZone

        assertTrue { start in start..end }
        assertTrue { DateTime.MAX at TimeZone("Etc/UTC") in start..end }
        assertTrue { DateTime.MAX at TimeZone("America/Denver") in start..end }
        assertTrue { Instant.MAX in start..end }
    }

    @Test
    fun `contains returns true for dates within range with unbounded start`() {
        val start = DateTime.MIN at nyZone
        val end = Date(2019, Month.MARCH, 10) at MIDNIGHT at nyZone

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { DateTime.MIN at TimeZone("Etc/UTC") in start..end }
        assertTrue { DateTime.MIN at TimeZone("America/Denver") in start..end }
        assertTrue { Instant.MIN in start..end }
    }

    @Test
    fun `contains returns false for out of range dates`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at nyZone
        val end = Date(2019, Month.MARCH, 12) at MIDNIGHT at nyZone

        assertFalse { start - 1.nanoseconds in start..end }
        assertFalse { end + 1.nanoseconds in start..end }
        assertFalse { "2019-03-11T23:00-06:00[America/Denver]".toZonedDateTime() in start..end }
        assertFalse { "2019-03-10T04:59:59Z".toInstant() in start..end }
    }

    @Test
    fun `until infix operator constructs an interval with non-inclusive end`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at nyZone
        val end = Date(2019, Month.MARCH, 12) at MIDNIGHT at nyZone
        val range = start until end

        assertEquals(start, range.start)
        assertEquals(end - 1.nanoseconds, range.endInclusive)
        assertEquals(end, range.endExclusive)
    }

    @Test
    fun `random returns a date-time within the interval`() {
        val start = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at nyZone
        val end = Date(2019, Month.NOVEMBER, 2) at MIDNIGHT at nyZone
        val interval = start until end
        val randomZonedDateTime = interval.random()
        assertTrue { randomZonedDateTime in interval }
        assertEquals(nyZone, randomZonedDateTime.zone)
    }

    @Test
    fun `random throws an exception when the interval is empty`() {
        assertFailsWith<NoSuchElementException> { ZonedDateTimeInterval.EMPTY.random() }
        assertFailsWith<NoSuchElementException> { ZonedDateTimeInterval.EMPTY.random(Random) }
    }

    @Test
    fun `random throws an exception when the interval is not bounded`() {
        val dateTime = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at nyZone
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.random() }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.random(Random) }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval(start = dateTime).random() }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval(endExclusive = dateTime).random() }
    }

    @Test
    fun `randomOrNull returns null when the interval is empty`() {
        assertNull(ZonedDateTimeInterval.EMPTY.randomOrNull())
        assertNull(ZonedDateTimeInterval.EMPTY.randomOrNull(Random))
    }

    @Test
    fun `randomOrNull returns null when the interval is not bounded`() {
        val dateTime = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at nyZone
        assertNull(ZonedDateTimeInterval.UNBOUNDED.randomOrNull())
        assertNull(ZonedDateTimeInterval.UNBOUNDED.randomOrNull(Random))
        assertNull(ZonedDateTimeInterval(start = dateTime).randomOrNull())
        assertNull(ZonedDateTimeInterval(endExclusive = dateTime).randomOrNull())
    }

    @Test
    fun `randomOrNull returns a date-time within the interval`() {
        val start = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at nyZone
        val end = (start + 1.nanoseconds).adjustedTo(TimeZone("Europe/London"))
        val interval = start until end
        val randomZonedDateTime = interval.randomOrNull()!!
        assertTrue { randomZonedDateTime in interval }
        assertEquals(nyZone, randomZonedDateTime.zone)
    }

    @Test
    fun `lengthIn properties return zero when the range is empty`() {
        assertEquals(0.centuries, ZonedDateTimeInterval.EMPTY.lengthInCenturies)
        assertEquals(0.decades, ZonedDateTimeInterval.EMPTY.lengthInDecades)
        assertEquals(0.years, ZonedDateTimeInterval.EMPTY.lengthInYears)
        assertEquals(0.months, ZonedDateTimeInterval.EMPTY.lengthInMonths)
        assertEquals(0.weeks, ZonedDateTimeInterval.EMPTY.lengthInWeeks)
        assertEquals(0.days, ZonedDateTimeInterval.EMPTY.lengthInDays)
        assertEquals(0.hours, ZonedDateTimeInterval.EMPTY.lengthInHours)
        assertEquals(0.minutes, ZonedDateTimeInterval.EMPTY.lengthInMinutes)
        assertEquals(0.seconds, ZonedDateTimeInterval.EMPTY.lengthInSeconds)
        assertEquals(0.milliseconds, ZonedDateTimeInterval.EMPTY.lengthInMilliseconds)
        assertEquals(0.microseconds, ZonedDateTimeInterval.EMPTY.lengthInMicroseconds)
        assertEquals(0.nanoseconds, ZonedDateTimeInterval.EMPTY.lengthInNanoseconds)
    }

    @Test
    fun `lengthIn properties throw an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInCenturies }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInDecades }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInYears }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInMonths }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInWeeks }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInDays }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInHours }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInMinutes }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInSeconds }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInMilliseconds }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInMicroseconds }
        assertFailsWith<UnsupportedOperationException> { ZonedDateTimeInterval.UNBOUNDED.lengthInNanoseconds }
    }

    @Test
    fun `lengthInNanoseconds returns 1 in an inclusive interval where the start and end instant are the same`() {
        val instant = Date(2019, Month.MARCH, 10) at MIDNIGHT at nyZone
        assertEquals(1L.nanoseconds, (instant..instant).lengthInNanoseconds)
    }

    @Test
    fun `period of months during daylight savings gap`() {
        val zone = nyZone
        val then = Date(2019, 3, 10) at Time(1, 0) at zone
        val now = Date(2019, 4, 10) at Time(1, 0) at zone

        assertEquals(periodOf(1.months), Period.between(then, now))
        assertEquals(periodOf((-1).months), Period.between(now, then))
        assertEquals(periodOf(30.days), Period.between(then, now - 1.nanoseconds))
        assertEquals(periodOf((-30).days), Period.between(now, then + 1.nanoseconds))
        assertEquals(periodOf(1.months), (then until now).toPeriod())
        assertEquals(periodOf(30.days), (then until now - 1.nanoseconds).toPeriod())
    }

    @Test
    fun `period of days during daylight savings gap`() {
        val zone = nyZone
        val then = Date(2019, 3, 10) at Time(1, 0) at zone
        val now = Date(2019, 3, 11) at Time(1, 0) at zone

        assertEquals(periodOf(1.days), Period.between(then, now))
        assertEquals(periodOf((-1).days), Period.between(now, then))
        assertEquals(Period.ZERO, Period.between(then, now - 1.nanoseconds))
        assertEquals(Period.ZERO, Period.between(now, then + 1.nanoseconds))
        assertEquals(periodOf(1.days), (then until now).toPeriod())
        assertEquals(Period.ZERO, (then until now - 1.nanoseconds).toPeriod())
    }

    @Test
    fun `duration during daylight savings gap`() {
        val zone = nyZone
        val then = Date(2019, 3, 10) at Time(1, 0) at zone
        val now = Date(2019, 3, 11) at Time(1, 0) at zone

        assertEquals(durationOf(23.hours), Duration.between(then, now))
        assertEquals(durationOf((-23).hours), Duration.between(now, then))
        assertEquals(durationOf(23.hours), (then until now).toDuration())
        assertEquals(durationOf(23.hours.inSeconds, 1.nanoseconds), (then..now).toDuration())
    }

    @Test
    fun `period of days during daylight savings overlap`() {
        val zone = nyZone
        val then = Date(2019, 11, 3) at Time(1, 0) at zone
        val now = Date(2019, 11, 4) at Time(1, 0) at zone

        assertEquals(periodOf(1.days), Period.between(then, now))
        assertEquals(Period.ZERO, Period.between(then, now - 1.nanoseconds))
        assertEquals(periodOf(1.days), (then until now).toPeriod())
        assertEquals(Period.ZERO, (then until now - 1.nanoseconds).toPeriod())
    }

    @Test
    fun `duration during daylight savings overlap`() {
        val zone = nyZone
        val then = Date(2019, 11, 3) at Time(1, 0) at zone
        val now = Date(2019, 11, 4) at Time(1, 0) at zone

        assertEquals(durationOf(25.hours), Duration.between(then, now))
        assertEquals(durationOf(25.hours), (then until now).toDuration())
        assertEquals(durationOf(25.hours.inSeconds, 1.nanoseconds), (then..now).toDuration())
    }

    @Test
    fun `lengthInCenturies property`() {
        val zone = nyZone
        val then = Date(2019, 3, 10).startOfDayAt(zone)
        val now = Date(2119, 3, 10).startOfDayAt(zone)

        assertEquals(1.centuries, (then until now).lengthInCenturies)
        assertEquals(0.centuries, (then until now - 1.nanoseconds).lengthInCenturies)
    }

    @Test
    fun `lengthInDecades property`() {
        val zone = nyZone
        val then = Date(2019, 3, 10).startOfDayAt(zone)
        val now = Date(2029, 3, 10).startOfDayAt(zone)

        assertEquals(1.decades, (then until now).lengthInDecades)
        assertEquals(0.decades, (then until now - 1.nanoseconds).lengthInDecades)
    }

    @Test
    fun `lengthInYears property`() {
        val zone = nyZone
        val then = Date(2019, 3, 10).startOfDayAt(zone)
        val now = Date(2020, 3, 10).startOfDayAt(zone)

        assertEquals(1.years, (then until now).lengthInYears)
        assertEquals(0.years, (then until now - 1.nanoseconds).lengthInYears)
    }

    @Test
    fun `lengthInMonths property during daylight savings gap`() {
        val zone = nyZone
        val then = Date(2019, 3, 10).startOfDayAt(zone)
        val now = Date(2019, 4, 10).startOfDayAt(zone)

        assertEquals(1.months, (then until now).lengthInMonths)
        assertEquals(0.months, (then until now - 1.nanoseconds).lengthInMonths)
    }

    @Test
    fun `lengthInWeeks property during daylight savings gap`() {
        val zone = nyZone
        val then = Date(2019, 3, 10).startOfDayAt(zone)
        val now = Date(2019, 3, 17).startOfDayAt(zone)

        assertEquals(1.weeks, (then until now).lengthInWeeks)
        assertEquals(0.weeks, (then until now - 1.nanoseconds).lengthInWeeks)
    }

    @Test
    fun `lengthInDays property during daylight savings gap`() {
        val zone = nyZone
        val then = Date(2019, 3, 10).startOfDayAt(zone)
        val now = Date(2019, 3, 11).startOfDayAt(zone)

        assertEquals(1.days, (then until now).lengthInDays)
        assertEquals(0.days, (then until now - 1.nanoseconds).lengthInDays)
    }

    @Test
    fun `lengthInHours property during daylight savings gap`() {
        val zone = nyZone
        val then = Date(2019, 3, 10).startOfDayAt(zone)
        val now = Date(2019, 3, 10) at Time(5, 0) at zone

        assertEquals(4.hours, (then until now).lengthInHours)
        assertEquals(3.hours, (then until now - 1.nanoseconds).lengthInHours)
    }
}
