package io.islandtime.ranges

import io.islandtime.Date
import io.islandtime.Month
import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.random.Random
import kotlin.test.*

class DateRangeTest : AbstractIslandTimeTest() {
    @Test
    fun `contains() returns true for dates within range`() {
        val startDate = Date(2018, Month.DECEMBER, 25)
        val endDate = Date(2019, Month.JANUARY, 12)

        assertTrue { startDate in startDate..endDate }
        assertTrue { endDate in startDate..endDate }
        assertTrue { Date(2019, Month.JANUARY, 1) in startDate..endDate }
    }

    @Test
    fun `contains() returns false for out of range dates`() {
        val startDate = Date(2018, Month.DECEMBER, 25)
        val endDate = Date(2019, Month.JANUARY, 12)

        listOf(
            Date(2018, Month.DECEMBER, 24),
            Date(2019, Month.JANUARY, 13),
            Date.MIN,
            Date.MAX
        ).forEach {
            assertFalse { it in startDate..endDate }
        }
    }

    @Test
    fun `contains() returns false for null dates`() {
        val startDate = Date(2018, Month.DECEMBER, 25)
        val endDate = Date(2019, Month.JANUARY, 12)

        assertFalse { null in startDate..endDate }
    }

    @Test
    fun `isEmpty() returns true when the start is greater than the end`() {
        assertTrue { (Date(2018, 45)..Date(2018, 44)).isEmpty() }
    }

    @Test
    fun `isEmpty() returns true when the end is negative infinity or start is positive infinity`() {
        listOf(
            Date.MIN..Date.MIN,
            Date.MAX..Date.MAX
        ).forEach {
            assertTrue { it.isEmpty() }
        }
    }

    @Test
    fun `until infix operator constructs a range with non-inclusive end`() {
        val startDate = Date(2019, Month.JANUARY, 21)
        val endDate = Date(2019, Month.JANUARY, 28)
        val range = startDate until endDate

        assertEquals(startDate, range.first)
        assertEquals(startDate, range.start)
        assertEquals(Date(2019, Month.JANUARY, 27), range.last)
        assertEquals(Date(2019, Month.JANUARY, 27), range.endInclusive)
        assertEquals(1.days, range.step)
        assertEquals(7, range.count())
        assertTrue { range.isBounded() }
        assertFalse { range.isUnbounded() }
        assertTrue { range.hasBoundedStart() }
        assertTrue { range.hasBoundedEnd() }
        assertFalse { range.hasUnboundedStart() }
        assertFalse { range.hasUnboundedEnd() }
    }

    @Test
    fun `toString() returns an ISO-8601 time interval representation`() {
        assertEquals(
            "1969-12-03/1970-02-03",
            (Date(1969, 12, 3)..Date(1970, 2, 3)).toString()
        )

        assertEquals(
            "../1970-02-03",
            (Date.MIN..Date(1970, 2, 3)).toString()
        )

        assertEquals(
            "1969-12-03/..",
            (Date(1969, 12, 3)..Date.MAX).toString()
        )

        assertEquals(
            "../..",
            DateRange.UNBOUNDED.toString()
        )

        assertEquals(
            "",
            DateRange.EMPTY.toString()
        )

        assertEquals(
            "",
            (Date(1934, 1)..Date(1932, 234)).toString()
        )
    }

    @Test
    fun `String_toDateRange() returns an empty range when the string is empty`() {
        assertEquals(DateRange.EMPTY, "".toDateRange())
    }

    @Test
    fun `String_toDateRange() parses ISO-8601 time interval strings with dates in extended format`() {
        assertEquals(DateRange.UNBOUNDED, "../..".toDateRange())

        assertEquals(
            Date(1950, 11, 9)..Date(1989, 6, 2),
            "1950-11-09/1989-06-02".toDateRange()
        )

        assertEquals(
            Date(1950, 11, 9)..Date.MAX,
            "1950-11-09/..".toDateRange()
        )

        assertEquals(
            Date.MIN..Date(1989, 6, 2),
            "../1989-06-02".toDateRange()
        )
    }

    @Test
    fun `String_toDateRange throws an exception when parsing invalid formats`() {
        listOf(
            " ",
            "/",
            "2019-10-06/", // We don't support unknown end
            "/2019-10-05", // We don't support unknown start
            "2019-05-06--2019-05-06",
            "2015/2016-10-10",
            "2015-05-05/2016-10",
            "2015-05-05/2016-10-10 ",
            "2015-05-05/2016-10-10/",
            " 2015-05-05/2016-10-10",
            "/2015-05-05/2016-10-10",
            "2015-05-05/2016-10-10/2019-11-10",
            "1950-11-09/1989-056" // Ordinal dates not supported by default
        ).forEach {
            assertFailsWith<DateTimeParseException> { it.toDateRange() }
        }
    }

    @Test
    fun `String_toDateRange throws an exception when parsing mixed basic and extended formats`() {
        listOf(
            " ",
            "/",
            "2019-10-06/", // We don't support unknown end
            "/2019-10-05", // We don't support unknown start
            "20190506/2019-05-06",
            "2015/2016-10-10",
            "2015-05-05/2016-10",
            "2015-05-05/2016-10-10 ",
            "2015-05-05/2016-10-10/",
            " 2015-05-05/2016-10-10",
            "/2015-05-05/2016-10-10",
            "2015-05-05/2016-10-10/2019-11-10"
        ).forEach {
            assertFailsWith<DateTimeParseException> { it.toDateRange(DateTimeParsers.Iso.DATE_RANGE) }
        }
    }

    @Test
    fun `random() returns a date within range`() {
        val range = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 25)
        val randomDate = range.random()
        assertTrue { randomDate in range }
    }

    @Test
    fun `random() throws an exception when the range is empty`() {
        assertFailsWith<NoSuchElementException> { DateRange.EMPTY.random() }
        assertFailsWith<NoSuchElementException> { DateRange.EMPTY.random(Random) }
    }

    @Test
    fun `random() throws an exception when the range is not bounded`() {
        assertFailsWith<UnsupportedOperationException> { DateRange.UNBOUNDED.random() }
        assertFailsWith<UnsupportedOperationException> { DateRange.UNBOUNDED.random(Random) }

        assertFailsWith<UnsupportedOperationException> {
            DateRange(start = Date(2020, Month.APRIL, 1)).random()
        }

        assertFailsWith<UnsupportedOperationException> {
            DateRange(endInclusive = Date(2020, Month.APRIL, 1)).random()
        }
    }

    @Test
    fun `randomOrNull() returns null when the range is empty`() {
        assertNull(DateRange.EMPTY.randomOrNull())
        assertNull(DateRange.EMPTY.randomOrNull(Random))
    }

    @Test
    fun `randomOrNull() returns null when the range is not bounded`() {
        assertNull(DateRange.UNBOUNDED.randomOrNull())
        assertNull(DateRange.UNBOUNDED.randomOrNull(Random))
        assertNull(DateRange(start = Date(2020, Month.APRIL, 1)).randomOrNull())
        assertNull(DateRange(endInclusive = Date(2020, Month.APRIL, 1)).randomOrNull())
    }

    @Test
    fun `randomOrNull() returns a date within range`() {
        val range = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.FEBRUARY, 20)
        assertTrue { range.randomOrNull()!! in range }
        assertTrue { range.randomOrNull(Random)!! in range }
    }

    @Test
    fun `length properties throw an exception when the range isn't bounded`() {
        listOf(
            DateRange.UNBOUNDED,
            Date(2019, Month.JUNE, 1)..Date.MAX,
            Date.MIN..Date(2019, Month.JUNE, 1)
        ).forEach {
            assertFailsWith<UnsupportedOperationException> { it.toPeriod() }
            assertFailsWith<UnsupportedOperationException> { it.lengthInCenturies }
            assertFailsWith<UnsupportedOperationException> { it.lengthInDecades }
            assertFailsWith<UnsupportedOperationException> { it.lengthInYears }
            assertFailsWith<UnsupportedOperationException> { it.lengthInMonths }
            assertFailsWith<UnsupportedOperationException> { it.lengthInWeeks }
            assertFailsWith<UnsupportedOperationException> { it.lengthInDays }
        }
    }

    @Test
    fun `length properties return 0 when the range is empty`() {
        assertEquals(Period.ZERO, DateRange.EMPTY.toPeriod())
        assertEquals(0.centuries, DateRange.EMPTY.lengthInCenturies)
        assertEquals(0.decades, DateRange.EMPTY.lengthInDecades)
        assertEquals(0.years, DateRange.EMPTY.lengthInYears)
        assertEquals(0.months, DateRange.EMPTY.lengthInMonths)
        assertEquals(0.weeks, DateRange.EMPTY.lengthInWeeks)
        assertEquals(0.days, DateRange.EMPTY.lengthInDays)
    }

    @Test
    fun `lengthInDays property returns 1 when the start and end date are the same`() {
        val date = Date(2019, Month.JUNE, 1)
        assertEquals(1.days, (date..date).lengthInDays)
    }

    @Test
    fun `lengthInDays property returns expected length when bounded`() {
        val start = Date(2018, Month.FEBRUARY, 1)
        val end = Date(2018, Month.FEBRUARY, 28)
        assertEquals(28.days, (start..end).lengthInDays)
    }

    @Test
    fun `lengthInWeeks property returns expected length when bounded`() {
        val range1 = Date(2018, Month.FEBRUARY, 1)..Date(2018, Month.FEBRUARY, 28)
        assertEquals(4.weeks, range1.lengthInWeeks)

        val range2 = Date(2018, Month.FEBRUARY, 1)..Date(2018, Month.FEBRUARY, 27)
        assertEquals(3.weeks, range2.lengthInWeeks)
    }

    @Test
    fun `lengthInMonths property returns expected length when bounded`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 18)
        assertEquals(0.months, range1.lengthInMonths)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2018, Month.MARCH, 19)
        assertEquals(1.months, range2.lengthInMonths)
    }

    @Test
    fun `lengthInYears property returns expected length when bounded`() {
        val range1 = Date(2018, Month.FEBRUARY, 20)..Date(2019, Month.FEBRUARY, 18)
        assertEquals(0.years, range1.lengthInYears)

        val range2 = Date(2018, Month.FEBRUARY, 20)..Date(2019, Month.FEBRUARY, 19)
        assertEquals(1.years, range2.lengthInYears)
    }

    @Test
    fun `lengthInDecades property returns expected length when bounded`() {
        val start = Date(2019, Month.JULY, 15)
        val end1 = Date(2029, Month.JULY, 14)
        val end2 = Date(2029, Month.JULY, 15)

        assertEquals(0.decades, (start until end1).lengthInDecades)
        assertEquals(1.decades, (start until end2).lengthInDecades)
    }

    @Test
    fun `lengthInCenturies property returns expected length when bounded`() {
        val start = Date(2019, Month.JULY, 15)
        val end1 = Date(2119, Month.JULY, 14)
        val end2 = Date(2119, Month.JULY, 15)

        assertEquals(0.centuries, (start until end1).lengthInCenturies)
        assertEquals(1.centuries, (start until end2).lengthInCenturies)
    }

    @Test
    fun `toPeriod() returns a period of 1 day when the start and end date are equal`() {
        val date = Date(2019, Month.JUNE, 1)
        assertEquals(periodOf(1.days), (date..date).toPeriod())
    }

    @Test
    fun `toPeriod() returns the expected period for non-empty ranges`() {
        val start = Date(2018, Month.FEBRUARY, 20)
        val end = Date(2019, Month.MARCH, 20)
        assertEquals(periodOf(1.years, 1.months, 1.days), (start..end).toPeriod())
    }
}
