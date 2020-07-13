package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.parser.*
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
        assertNotEquals<TimeInterval<*>>(DateTimeInterval.UNBOUNDED, InstantInterval.UNBOUNDED)
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
    fun `asDuration() returns a zeroed out duration when the range is empty`() {
        assertEquals(Duration.ZERO, DateTimeInterval.EMPTY.asDuration())
    }

    @Test
    fun `asDuration() throws an exception when the range is unbounded`() {
        assertFailsWith<UnsupportedOperationException> {
            DateTimeInterval(endExclusive = "2018-09-10T09:15".toDateTime()).asDuration()
        }
    }

    @Test
    fun `duration is correct when bounded`() {
        assertEquals(
            10.minutes.asDuration(),
            ("2019-10-11T09:15".toDateTime() until "2019-10-11T09:25".toDateTime()).asDuration()
        )

        assertEquals(
            10.minutes.asDuration(),
            durationBetween("2019-10-11T09:15".toDateTime(), "2019-10-11T09:25".toDateTime())
        )
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
    fun `years between two date-times`() {
        val start = DateTime(2019, Month.MARCH, 1, 13, 0)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1.years, (start until end1).lengthInYears)
        assertEquals(1.years, yearsBetween(start, end1))

        assertEquals(0.years, (start until end2).lengthInYears)
        assertEquals(0.years, yearsBetween(start, end2))
    }

    @Test
    fun `months between two date-times`() {
        val start = DateTime(2020, Month.FEBRUARY, 1, 13, 0)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1.months, (start until end1).lengthInMonths)
        assertEquals(1.months, monthsBetween(start, end1))

        assertEquals(0.months, (start until end2).lengthInMonths)
        assertEquals(0.months, monthsBetween(start, end2))
    }

    @Test
    fun `weeks between two date-times`() {
        val start = DateTime(2020, Month.FEBRUARY, 29, 13, 0)
        val end1 = DateTime(2020, Month.MARCH, 7, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 7, 12, 59, 59, 999_999_999)

        assertEquals(1L.weeks, (start until end1).lengthInWeeks)
        assertEquals(1L.weeks, weeksBetween(start, end1))

        assertEquals(0L.weeks, (start until end2).lengthInWeeks)
        assertEquals(0L.weeks, weeksBetween(start, end2))
    }

    @Test
    fun `days between two date-times`() {
        val start = DateTime(2020, Month.FEBRUARY, 29, 13, 0)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1L.days, (start until end1).lengthInDays)
        assertEquals(1L.days, daysBetween(start, end1))

        assertEquals(0L.days, (start until end2).lengthInDays)
        assertEquals(0L.days, daysBetween(start, end2))
    }

    @Test
    fun `hours between two date-times`() {
        val start = DateTime(2020, Month.MARCH, 1, 12, 0)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1L.hours, (start until end1).lengthInHours)
        assertEquals(1L.hours, hoursBetween(start, end1))

        assertEquals(0L.hours, (start until end2).lengthInHours)
        assertEquals(0L.hours, hoursBetween(start, end2))
    }

    @Test
    fun `minutes between two date-times`() {
        val start = DateTime(2020, Month.MARCH, 1, 12, 59)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1L.minutes, (start until end1).lengthInMinutes)
        assertEquals(1L.minutes, minutesBetween(start, end1))

        assertEquals(0L.minutes, (start until end2).lengthInMinutes)
        assertEquals(0L.minutes, minutesBetween(start, end2))
    }

    @Test
    fun `seconds between two date-times`() {
        val start = DateTime(2020, Month.MARCH, 1, 12, 59, 59)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1L.seconds, (start until end1).lengthInSeconds)
        assertEquals(1L.seconds, secondsBetween(start, end1))

        assertEquals(0L.seconds, (start until end2).lengthInSeconds)
        assertEquals(0L.seconds, secondsBetween(start, end2))
    }

    @Test
    fun `milliseconds between two date-times`() {
        val start = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_000_000)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1L.milliseconds, (start until end1).lengthInMilliseconds)
        assertEquals(1L.milliseconds, millisecondsBetween(start, end1))

        assertEquals(0L.milliseconds, (start until end2).lengthInMilliseconds)
        assertEquals(0L.milliseconds, millisecondsBetween(start, end2))
    }

    @Test
    fun `microseconds between two date-times`() {
        val start = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_000)
        val end1 = DateTime(2020, Month.MARCH, 1, 13, 0)
        val end2 = DateTime(2020, Month.MARCH, 1, 12, 59, 59, 999_999_999)

        assertEquals(1L.microseconds, (start until end1).lengthInMicroseconds)
        assertEquals(1L.microseconds, microsecondsBetween(start, end1))

        assertEquals(0L.microseconds, (start until end2).lengthInMicroseconds)
        assertEquals(0L.microseconds, microsecondsBetween(start, end2))
    }

    @Test
    fun `lengthInNanoseconds returns 1 in an inclusive interval where the start and end date-times are the same`() {
        val dateTime = Date(2019, Month.MARCH, 10) at Time.MIDNIGHT
        assertEquals(1L.nanoseconds, (dateTime..dateTime).lengthInNanoseconds)
    }

    @Test
    fun `toString() returns an ISO-8601 time interval representation`() {
        assertEquals(
            "2000-12-31T00:00/2001-01-02T00:00",
            (DateTime(2000, 12, 31, 0, 0) until
                DateTime(2001, 1, 2, 0, 0)).toString()
        )

        assertEquals(
            "../2001-01-02T00:00",
            (DateTime.MIN until DateTime(2001, 1, 2, 0, 0)).toString()
        )

        assertEquals(
            "2000-12-31T00:00/..",
            (DateTime(2000, 12, 31, 0, 0) until DateTime.MAX).toString()
        )

        assertEquals(
            "../..",
            (DateTime.MIN until DateTime.MAX).toString()
        )

        assertEquals(
            "../..",
            DateTimeInterval.UNBOUNDED.toString()
        )

        assertEquals(
            "",
            DateTimeInterval.EMPTY.toString()
        )
    }

    @Test
    fun `String_toDateTimeInterval() converts an empty string to an empty interval`() {
        assertEquals(DateTimeInterval.EMPTY, "".toDateTimeInterval())
    }

    @Test
    fun `String_toDateTimeInterval() throws an exception when the format is invalid`() {
        assertFailsWith<TemporalParseException> { "2000-01-01/2000-01-01".toDateTimeInterval() }
        assertFailsWith<TemporalParseException> { "2000-01-01T00:00/20000101T01".toDateTimeInterval() }
    }

    @Test
    fun `String_toDateTimeInterval() parses ISO-8601 time interval strings in extended format by default`() {
        assertEquals(
            DateTime(1969, 12, 31, 0, 0) until
                DateTime(1970, 1, 2, 0, 0),
            "1969-12-31T00:00/1970-01-02T00:00".toDateTimeInterval()
        )
        assertEquals(
            DateTime.MIN until DateTime(1970, 1, 2, 0, 0),
            "../1970-01-02T00:00".toDateTimeInterval()
        )
        assertEquals(
            DateTime(1969, 12, 31, 0, 0) until DateTime.MAX,
            "1969-12-31T00:00/..".toDateTimeInterval()
        )
        assertEquals(
            DateTime(1969, 12, 31, 0, 0)..DateTime.MAX,
            "1969-12-31T00:00/..".toDateTimeInterval()
        )
        assertEquals(DateTimeInterval.UNBOUNDED, "../..".toDateTimeInterval())
    }

    @Test
    fun `String_toDateTimeInterval() throws an exception when required properties are missing`() {
        val customParser = groupedTemporalParser {
            group {
                optional {
                    anyOf(DateTimeParsers.Iso.DATE_TIME, DateTimeParsers.Iso.YEAR)
                }
            }
            +'/'
            group {
                optional {
                    anyOf(DateTimeParsers.Iso.DATE_TIME, DateTimeParsers.Iso.YEAR)
                }
            }
        }

        assertFailsWith<TemporalParseException> { "2001/2002-11-04T13:23".toDateTimeInterval(customParser) }
        assertFailsWith<TemporalParseException> { "2001-10-03T00:01/2002".toDateTimeInterval(customParser) }
        assertFailsWith<TemporalParseException> { "/2002-11-04T13:23".toDateTimeInterval(customParser) }
        assertFailsWith<TemporalParseException> { "2001-10-03T00:01/".toDateTimeInterval(customParser) }
    }
}