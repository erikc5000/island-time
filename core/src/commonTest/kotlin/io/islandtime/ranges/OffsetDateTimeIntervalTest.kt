package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.parser.groupedDateTimeParser
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.random.Random
import kotlin.test.*

class OffsetDateTimeIntervalTest : AbstractIslandTimeTest() {
    @Test
    fun `EMPTY returns an empty interval`() {
        assertTrue { OffsetDateTimeInterval.EMPTY.isEmpty() }
        assertTrue { OffsetDateTimeInterval.EMPTY.isBounded() }
        assertTrue { OffsetDateTimeInterval.EMPTY.hasBoundedStart() }
        assertTrue { OffsetDateTimeInterval.EMPTY.hasBoundedEnd() }
        assertFalse { OffsetDateTimeInterval.EMPTY.isUnbounded() }
        assertFalse { OffsetDateTimeInterval.EMPTY.hasUnboundedStart() }
        assertFalse { OffsetDateTimeInterval.EMPTY.hasUnboundedEnd() }
    }

    @Test
    fun `UNBOUNDED returns an unbounded interval`() {
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.isEmpty() }
        assertTrue { OffsetDateTimeInterval.UNBOUNDED.isUnbounded() }
        assertTrue { OffsetDateTimeInterval.UNBOUNDED.hasUnboundedStart() }
        assertTrue { OffsetDateTimeInterval.UNBOUNDED.hasUnboundedEnd() }
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.isBounded() }
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.hasBoundedStart() }
        assertFalse { OffsetDateTimeInterval.UNBOUNDED.hasBoundedEnd() }
    }

    @Test
    fun `inclusive end creation handles unbounded correctly`() {
        val offset = UtcOffset((-4).hours)
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at offset
        val max = DateTime.MAX at offset

        assertTrue { (start..max).hasUnboundedEnd() }
        assertFailsWith<DateTimeException> { start..max - 1.nanoseconds }
        assertEquals(start until max - 1.nanoseconds, start..max - 2.nanoseconds)
    }

    @Test
    fun `contains returns true for dates within bounded range`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 12) at MIDNIGHT at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { "2019-03-11T22:00-06:00".toOffsetDateTime() in start..end }
        assertTrue { "2019-03-10T05:00Z".toInstant() in start..end }
    }

    @Test
    fun `contains returns true for dates within range with unbounded end`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)
        val end = DateTime.MAX at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { DateTime.MAX at TimeZone("Etc/UTC") in start..end }
        assertTrue { DateTime.MAX at TimeZone("America/Denver") in start..end }
        assertTrue { Instant.MAX in start..end }
    }

    @Test
    fun `contains returns true for dates within range with unbounded start`() {
        val start = DateTime.MIN at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { DateTime.MIN at TimeZone("Etc/UTC") in start..end }
        assertTrue { DateTime.MIN at TimeZone("America/Denver") in start..end }
        assertTrue { Instant.MIN in start..end }
    }

    @Test
    fun `contains returns false for out of range dates`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2019, Month.MARCH, 12) at MIDNIGHT at UtcOffset((-4).hours)

        assertFalse { start - 1.nanoseconds in start..end }
        assertFalse { end + 1.nanoseconds in start..end }
        assertFalse { "2019-03-11T23:00-06:00".toOffsetDateTime() in start..end }
        assertFalse { "2019-03-10T04:59:59Z".toInstant() in start..end }
    }

    @Test
    fun `until infix operator constructs an interval with non-inclusive end`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 12) at MIDNIGHT at UtcOffset((-4).hours)
        val range = start until end

        assertEquals(start, range.start)
        assertEquals(end - 1.nanoseconds, range.endInclusive)
        assertEquals(end, range.endExclusive)
    }

    @Test
    fun `random returns a date-time within the interval`() {
        val start = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.NOVEMBER, 20) at MIDNIGHT at UtcOffset((-5).hours)
        val interval = start until end
        val randomOffsetDateTime = interval.random()
        assertTrue { randomOffsetDateTime in interval }
        assertEquals(UtcOffset((-4).hours), randomOffsetDateTime.offset)
    }

    @Test
    fun `random throws an exception when the interval is empty`() {
        assertFailsWith<NoSuchElementException> { OffsetDateTimeInterval.EMPTY.random() }
        assertFailsWith<NoSuchElementException> { OffsetDateTimeInterval.EMPTY.random(Random) }
    }

    @Test
    fun `random throws an exception when the interval is not bounded`() {
        val dateTime = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at UtcOffset((-4).hours)
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.random() }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.random(Random) }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval(start = dateTime).random() }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval(endExclusive = dateTime).random() }
    }

    @Test
    fun `randomOrNull returns null when the interval is empty`() {
        assertNull(OffsetDateTimeInterval.EMPTY.randomOrNull())
        assertNull(OffsetDateTimeInterval.EMPTY.randomOrNull(Random))
    }

    @Test
    fun `randomOrNull returns null when the interval is not bounded`() {
        val dateTime = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at UtcOffset((-4).hours)
        assertNull(OffsetDateTimeInterval.UNBOUNDED.randomOrNull())
        assertNull(OffsetDateTimeInterval.UNBOUNDED.randomOrNull(Random))
        assertNull(OffsetDateTimeInterval(start = dateTime).randomOrNull())
        assertNull(OffsetDateTimeInterval(endExclusive = dateTime).randomOrNull())
    }

    @Test
    fun `randomOrNull returns a date-time within the interval`() {
        val start = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at UtcOffset((-4).hours)
        val end = (start + 1.nanoseconds).adjustedTo(UtcOffset((-5).hours))
        val interval = start until end
        val randomOffsetDateTime = interval.randomOrNull()!!
        assertTrue { randomOffsetDateTime in interval }
        assertEquals(UtcOffset((-4).hours), randomOffsetDateTime.offset)
    }

    @Test
    fun `toPeriod returns a zeroed out period when the range is empty`() {
        assertEquals(Period.ZERO, OffsetDateTimeInterval.EMPTY.toPeriod())
    }

    @Test
    fun `toPeriod throws an exception when the range is unbounded`() {
        assertFailsWith<UnsupportedOperationException> {
            OffsetDateTimeInterval(endExclusive = "2018-09-10T09:15-06:00".toOffsetDateTime()).toPeriod()
        }
    }

    @Test
    fun `period is correct when bounded`() {
        assertEquals(
            periodOf(1.years, 1.months, 1.days),
            ("2018-09-10T09:15-06:00".toOffsetDateTime() until "2019-10-11T09:15-07:00".toOffsetDateTime()).toPeriod()
        )
    }

    @Test
    fun `lengthIn properties return zero when the range is empty`() {
        assertEquals(0.centuries, OffsetDateTimeInterval.EMPTY.lengthInCenturies)
        assertEquals(0.decades, OffsetDateTimeInterval.EMPTY.lengthInDecades)
        assertEquals(0.years, OffsetDateTimeInterval.EMPTY.lengthInYears)
        assertEquals(0.months, OffsetDateTimeInterval.EMPTY.lengthInMonths)
        assertEquals(0.weeks, OffsetDateTimeInterval.EMPTY.lengthInWeeks)
        assertEquals(0.days, OffsetDateTimeInterval.EMPTY.lengthInDays)
        assertEquals(0.hours, OffsetDateTimeInterval.EMPTY.lengthInHours)
        assertEquals(0.minutes, OffsetDateTimeInterval.EMPTY.lengthInMinutes)
        assertEquals(0.seconds, OffsetDateTimeInterval.EMPTY.lengthInSeconds)
        assertEquals(0.milliseconds, OffsetDateTimeInterval.EMPTY.lengthInMilliseconds)
        assertEquals(0.microseconds, OffsetDateTimeInterval.EMPTY.lengthInMicroseconds)
        assertEquals(0.nanoseconds, OffsetDateTimeInterval.EMPTY.lengthInNanoseconds)
    }

    @Test
    fun `lengthIn properties throw an exception when the interval is unbounded`() {
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInCenturies }
        assertFailsWith<UnsupportedOperationException> { OffsetDateTimeInterval.UNBOUNDED.lengthInDecades }
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
    fun `lengthInCenturies property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2019, Month.MARCH, 1, 13, 0) at offset1
        val end1 = DateTime(2119, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2119, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.centuries, (start until end1).lengthInCenturies)
        assertEquals(0.centuries, (start until end2).lengthInCenturies)
    }

    @Test
    fun `lengthInDecades property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2019, Month.MARCH, 1, 13, 0) at offset1
        val end1 = DateTime(2029, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2029, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.decades, (start until end1).lengthInDecades)
        assertEquals(0.decades, (start until end2).lengthInDecades)
    }

    @Test
    fun `lengthInYears property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2019, Month.MARCH, 1, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.years, (start until end1).lengthInYears)
        assertEquals(0.years, (start until end2).lengthInYears)
    }

    @Test
    fun `lengthInMonths property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.FEBRUARY, 1, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.months, (start until end1).lengthInMonths)
        assertEquals(0.months, (start until end2).lengthInMonths)
    }

    @Test
    fun `lengthInWeeks property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.FEBRUARY, 29, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 7, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 7) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.weeks, (start until end1).lengthInWeeks)
        assertEquals(0.weeks, (start until end2).lengthInWeeks)
    }

    @Test
    fun `lengthInDays property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.FEBRUARY, 29, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.days, (start until end1).lengthInDays)
        assertEquals(0.days, (start until end2).lengthInDays)
    }

    @Test
    fun `lengthInHours property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.MARCH, 1, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.hours, (start until end1).lengthInHours)
        assertEquals(0.hours, (start until end2).lengthInHours)
    }

    @Test
    fun `lengthInMinutes property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.MARCH, 1, 13, 59) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.minutes, (start until end1).lengthInMinutes)
        assertEquals(0.minutes, (start until end2).lengthInMinutes)
    }

    @Test
    fun `lengthInSeconds property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.MARCH, 1, 13, 59, 59) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.seconds, (start until end1).lengthInSeconds)
        assertEquals(0.seconds, (start until end2).lengthInSeconds)
    }

    @Test
    fun `lengthInMilliseconds property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_000_000) at
            offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.milliseconds, (start until end1).lengthInMilliseconds)
        assertEquals(0.milliseconds, (start until end2).lengthInMilliseconds)
    }

    @Test
    fun `lengthInMicroseconds property returns expected length when bounded`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_000) at
            offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.microseconds, (start until end1).lengthInMicroseconds)
        assertEquals(0.microseconds, (start until end2).lengthInMicroseconds)
    }

    @Test
    fun `lengthInNanoseconds returns 1 in an inclusive interval where the start and end instant are the same`() {
        val instant = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)
        assertEquals(1L.nanoseconds, (instant..instant).lengthInNanoseconds)
    }

    @Test
    fun `toString returns an ISO-8601 time interval representation`() {
        val offset = UtcOffset((-5).hours)
        val start = Date(2000, 12, 31) at MIDNIGHT at offset
        val end = Date(2001, 1, 2) at MIDNIGHT at offset

        assertEquals(
            "2000-12-31T00:00-05:00/2001-01-02T00:00-05:00",
            (start until end).toString()
        )

        assertEquals(
            "../2001-01-02T00:00-05:00",
            (DateTime.MIN at offset until end).toString()
        )

        assertEquals(
            "2000-12-31T00:00-05:00/..",
            (start until (DateTime.MAX at offset)).toString()
        )

        assertEquals(
            "../..",
            (DateTime.MIN until DateTime.MAX).toString()
        )

        assertEquals(
            "../..",
            OffsetDateTimeInterval.UNBOUNDED.toString()
        )

        assertEquals(
            "",
            OffsetDateTimeInterval.EMPTY.toString()
        )
    }

    @Test
    fun `String_toOffsetDateTimeInterval converts an empty string to an empty interval`() {
        assertEquals(OffsetDateTimeInterval.EMPTY, "".toOffsetDateTimeInterval())
    }

    @Test
    fun `String_toOffsetDateTimeInterval throws an exception when the format is invalid`() {
        assertFailsWith<DateTimeParseException> { "2000-01-01/2000-01-01".toOffsetDateTimeInterval() }
        assertFailsWith<DateTimeParseException> { "2000-01-01T00:00/2000-01-01T01:00".toOffsetDateTimeInterval() }
        assertFailsWith<DateTimeParseException> { "2000-01-01T00:00+04/20000101T01-01".toOffsetDateTimeInterval() }
    }

    @Test
    fun `String_toOffsetDateTimeInterval parses ISO-8601 time interval strings in extended format by default`() {
        val offset = UtcOffset((-5).hours)
        val start = Date(1969, 12, 31) at MIDNIGHT at offset
        val end = Date(1970, 1, 2) at MIDNIGHT at offset

        assertEquals(
            start until end,
            "1969-12-31T00:00-05:00/1970-01-02T00:00-05:00".toOffsetDateTimeInterval()
        )
        assertEquals(
            OffsetDateTime.MIN until end,
            "../1970-01-02T00:00-05:00".toOffsetDateTimeInterval()
        )
        assertEquals(
            start until OffsetDateTime.MAX,
            "1969-12-31T00:00-05:00/..".toOffsetDateTimeInterval()
        )
        assertEquals(OffsetDateTimeInterval.UNBOUNDED, "../..".toOffsetDateTimeInterval())
    }

    @Test
    fun `String_toOffsetDateTimeInterval throws an exception when required properties are missing`() {
        val customParser = groupedDateTimeParser {
            group {
                optional {
                    anyOf(DateTimeParsers.Iso.OFFSET_DATE_TIME, DateTimeParsers.Iso.YEAR)
                }
            }
            +'/'
            group {
                optional {
                    anyOf(DateTimeParsers.Iso.OFFSET_DATE_TIME, DateTimeParsers.Iso.YEAR)
                }
            }
        }

        assertFailsWith<DateTimeParseException> {
            "2001/2002-11-04T13:23+01".toOffsetDateTimeInterval(
                customParser
            )
        }
        assertFailsWith<DateTimeParseException> {
            "2001-10-03T00:01-04/2002".toOffsetDateTimeInterval(
                customParser
            )
        }
        assertFailsWith<DateTimeParseException> {
            "/2002-11-04T13:23-04".toOffsetDateTimeInterval(
                customParser
            )
        }
        assertFailsWith<DateTimeParseException> {
            "2001-10-03T00:01-07/".toOffsetDateTimeInterval(
                customParser
            )
        }
    }
}
