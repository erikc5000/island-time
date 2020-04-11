package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.parser.groupedDateTimeParser
import io.islandtime.test.AbstractIslandTimeTest
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
    fun `contains() returns true for dates within bounded range`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 12) at MIDNIGHT at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { "2019-03-11T22:00-06:00".toOffsetDateTime() in start..end }
        assertTrue { "2019-03-10T05:00Z".toInstant() in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded end`() {
        val start = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)
        val end = DateTime.MAX at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { DateTime.MAX at "Etc/UTC".toTimeZone() in start..end }
        assertTrue { DateTime.MAX at "America/Denver".toTimeZone() in start..end }
        assertTrue { Instant.MAX in start..end }
    }

    @Test
    fun `contains() returns true for dates within range with unbounded start`() {
        val start = DateTime.MIN at UtcOffset((-4).hours)
        val end = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)

        assertTrue { start in start..end }
        assertTrue { end in start..end }
        assertTrue { DateTime.MIN at "Etc/UTC".toTimeZone() in start..end }
        assertTrue { DateTime.MIN at "America/Denver".toTimeZone() in start..end }
        assertTrue { Instant.MIN in start..end }
    }

    @Test
    fun `contains() returns false for out of range dates`() {
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
    fun `random() returns a zoned date-time within range`() {
        val start = Date(2019, Month.NOVEMBER, 1) at MIDNIGHT at UtcOffset((-4).hours)
        val end = Date(2019, Month.NOVEMBER, 20) at MIDNIGHT at UtcOffset((-5).hours)
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
    fun `years between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2019, Month.MARCH, 1, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.years, (start until end1).lengthInYears)
        assertEquals(1.years, yearsBetween(start, end1))

        assertEquals(0.years, (start until end2).lengthInYears)
        assertEquals(0.years, yearsBetween(start, end2))
    }

    @Test
    fun `months between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.FEBRUARY, 1, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1.months, (start until end1).lengthInMonths)
        assertEquals(1.months, monthsBetween(start, end1))

        assertEquals(0.months, (start until end2).lengthInMonths)
        assertEquals(0.months, monthsBetween(start, end2))
    }

    @Test
    fun `weeks between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.FEBRUARY, 29, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 7, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 7) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1L.weeks, (start until end1).lengthInWeeks)
        assertEquals(1L.weeks, weeksBetween(start, end1))

        assertEquals(0L.weeks, (start until end2).lengthInWeeks)
        assertEquals(0L.weeks, weeksBetween(start, end2))
    }

    @Test
    fun `days between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.FEBRUARY, 29, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 14, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_999) at
            offset2

        assertEquals(1L.days, (start until end1).lengthInDays)
        assertEquals(1L.days, daysBetween(start, end1))

        assertEquals(0L.days, (start until end2).lengthInDays)
        assertEquals(0L.days, daysBetween(start, end2))
    }

    @Test
    fun `hours between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.MARCH, 1, 13, 0) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1L.hours, (start until end1).lengthInHours)
        assertEquals(1L.hours, hoursBetween(start, end1))

        assertEquals(0L.hours, (start until end2).lengthInHours)
        assertEquals(0L.hours, hoursBetween(start, end2))
    }

    @Test
    fun `minutes between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.MARCH, 1, 13, 59) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1L.minutes, (start until end1).lengthInMinutes)
        assertEquals(1L.minutes, minutesBetween(start, end1))

        assertEquals(0L.minutes, (start until end2).lengthInMinutes)
        assertEquals(0L.minutes, minutesBetween(start, end2))
    }

    @Test
    fun `seconds between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = DateTime(2020, Month.MARCH, 1, 13, 59, 59) at offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1L.seconds, (start until end1).lengthInSeconds)
        assertEquals(1L.seconds, secondsBetween(start, end1))

        assertEquals(0L.seconds, (start until end2).lengthInSeconds)
        assertEquals(0L.seconds, secondsBetween(start, end2))
    }

    @Test
    fun `milliseconds between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_000_000) at
            offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1L.milliseconds, (start until end1).lengthInMilliseconds)
        assertEquals(1L.milliseconds, millisecondsBetween(start, end1))

        assertEquals(0L.milliseconds, (start until end2).lengthInMilliseconds)
        assertEquals(0L.milliseconds, millisecondsBetween(start, end2))
    }

    @Test
    fun `microseconds between two date-times`() {
        val offset1 = UtcOffset(1.hours)
        val offset2 = UtcOffset(2.hours)
        val start = Date(2020, Month.MARCH, 1) at
            Time(13, 59, 59, 999_999_000) at
            offset1
        val end1 = DateTime(2020, Month.MARCH, 1, 15, 0) at offset2
        val end2 = Date(2020, Month.MARCH, 1) at
            Time(14, 59, 59, 999_999_999) at
            offset2

        assertEquals(1L.microseconds, (start until end1).lengthInMicroseconds)
        assertEquals(1L.microseconds, microsecondsBetween(start, end1))

        assertEquals(0L.microseconds, (start until end2).lengthInMicroseconds)
        assertEquals(0L.microseconds, microsecondsBetween(start, end2))
    }

    @Test
    fun `lengthInNanoseconds returns 1 in an inclusive interval where the start and end instant are the same`() {
        val instant = Date(2019, Month.MARCH, 10) at MIDNIGHT at UtcOffset((-4).hours)
        assertEquals(1L.nanoseconds, (instant..instant).lengthInNanoseconds)
    }

    @Test
    fun `toString() returns an ISO-8601 time interval representation`() {
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
    fun `String_toOffsetDateTimeInterval() converts an empty string to an empty interval`() {
        assertEquals(OffsetDateTimeInterval.EMPTY, "".toOffsetDateTimeInterval())
    }

    @Test
    fun `String_toOffsetDateTimeInterval() throws an exception when the format is invalid`() {
        assertFailsWith<DateTimeParseException> { "2000-01-01/2000-01-01".toOffsetDateTimeInterval() }
        assertFailsWith<DateTimeParseException> { "2000-01-01T00:00/2000-01-01T01:00".toOffsetDateTimeInterval() }
        assertFailsWith<DateTimeParseException> { "2000-01-01T00:00+04/20000101T01-01".toOffsetDateTimeInterval() }
    }

    @Test
    fun `String_toOffsetDateTimeInterval() parses ISO-8601 time interval strings in extended format by default`() {
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
    fun `String_toOffsetDateTimeInterval() throws an exception when required properties are missing`() {
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

        assertFailsWith<DateTimeParseException> { "2001/2002-11-04T13:23+01".toOffsetDateTimeInterval(customParser) }
        assertFailsWith<DateTimeParseException> { "2001-10-03T00:01-04/2002".toOffsetDateTimeInterval(customParser) }
        assertFailsWith<DateTimeParseException> { "/2002-11-04T13:23-04".toOffsetDateTimeInterval(customParser) }
        assertFailsWith<DateTimeParseException> { "2001-10-03T00:01-07/".toOffsetDateTimeInterval(customParser) }
    }
}