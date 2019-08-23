package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.parser.DateTimeParseException
import dev.erikchristensen.islandtime.parser.Iso8601
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DateTimeTest {
    @Test
    fun `toString() returns an ISO-8601 extended calendar date time`() {
        assertEquals(
            "2019-08-01T00:01",
            DateTime(Date(2019, Month.AUGUST, 1), Time(0, 1)).toString()
        )
        assertEquals(
            "0001-10-10T01:01:01.000000001",
            DateTime(Date(1, Month.OCTOBER, 10), Time(1,1,1,1)).toString()
        )
    }

    @Test
    fun `String_toDateTime() throws an exception when the format is mixed basic and extended`() {
        assertFailsWith<DateTimeParseException> { "20000101 00:23".toDateTime() }
        assertFailsWith<DateTimeParseException> { "2002-02-01T0023".toDateTime() }
    }

    @Test
    fun `String_toDateTime() throws an exception when given an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toDateTime() }
    }

    @Test
    fun `String_toDateTime() parses valid ISO-8601 extended calendar date strings by default`() {
        assertEquals(
            DateTime(Date(2019, Month.MARCH, 23), Time(2, 30)),
            "2019-03-23T02:30".toDateTime()
        )

        assertEquals(
            DateTime(
                Date(2000, Month.FEBRUARY, 29),
                Time(23, 59, 59, 999_999_999)
            ),
            "2000-02-29 23:59:59.999999999".toDateTime()
        )
    }

    @Test
    fun `String_toDateTime() parses valid ISO-8601 basic calendar date strings`() {
        assertEquals(
            DateTime(Date(2019, Month.MARCH, 23), Time(2, 30)),
            "20190323T0230".toDateTime(Iso8601.Basic.CALENDAR_DATE_TIME_PARSER)
        )
    }

    @Test
    fun `String_toDateTime() parses valid ISO-8601 ordinal date strings`() {
        assertEquals(
            DateTime(Date(2019, Month.JANUARY, 1), Time(2, 30)),
            "2019001 0230".toDateTime(Iso8601.DATE_TIME_PARSER)
        )

        assertEquals(
            DateTime(Date(2019, Month.JANUARY, 1), Time(2, 30)),
            "2019-001T02:30".toDateTime(Iso8601.DATE_TIME_PARSER)
        )
    }
}