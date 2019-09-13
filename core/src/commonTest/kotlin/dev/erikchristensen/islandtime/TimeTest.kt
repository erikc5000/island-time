package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.hours
import dev.erikchristensen.islandtime.parser.DateTimeParseException
import dev.erikchristensen.islandtime.parser.Iso8601
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimeTest {
    @Test
    fun `throws an exception when constructed with an invalid hour`() {
        assertFailsWith<DateTimeException> { Time(-1, 0) }
        assertFailsWith<DateTimeException> { Time(24, 0) }
    }

    @Test
    fun `throws an exception when constructed with an invalid minute`() {
        assertFailsWith<DateTimeException> { Time(0, -1) }
        assertFailsWith<DateTimeException> { Time(0, 60) }
    }

    @Test
    fun `throws an exception when constructed with an invalid second`() {
        assertFailsWith<DateTimeException> { Time(0, 0, -1) }
        assertFailsWith<DateTimeException> { Time(0, 0, 60) }
    }

    @Test
    fun `throws an exception when constructed with an invalid nanosecond`() {
        assertFailsWith<DateTimeException> { Time(0, 0, 0, -1) }
        assertFailsWith<DateTimeException> { Time(0, 0, 0, 1_000_000_000) }
    }

    @Test
    fun `secondOfDay works correctly`() {
        assertEquals(0, Time(0, 0, 0).secondOfDay)
        assertEquals(86_399, Time(23, 59, 59).secondOfDay)
    }

    @Test
    fun `nanosecondOfDay works correctly`() {
        assertEquals(0, Time(0, 0, 0, 0).nanosecondOfDay)
        assertEquals(
            86_399_999_999_999L,
            Time(23, 59, 59, 999_999_999).nanosecondOfDay
        )
    }

    @Test
    fun `toString() returns an ISO-8601 extended time with minute precision`() {
        assertEquals("00:00", Time(0, 0, 0, 0).toString())
    }

    @Test
    fun `toString() returns an ISO-8601 extended time with second precision`() {
        assertEquals("01:01:01", Time(1, 1, 1, 0).toString())
    }

    @Test
    fun `toString() returns an ISO-8601 extended time with millisecond precision`() {
        assertEquals("01:01:01.100", Time(1, 1, 1, 100_000_000).toString())
    }

    @Test
    fun `toString() returns an ISO-8601 extended time with microsecond precision`() {
        assertEquals("23:59:59.000990", Time(23, 59, 59, 990_000).toString())
    }

    @Test
    fun `toString() returns an ISO-8601 extended time with nanosecond precision`() {
        assertEquals(
            "23:59:59.000000900",
            Time(23, 59, 59, 900).toString()
        )
    }

    @Test
    fun `can be broken down into components`() {
        val (hour, minute, second, nano) = Time(1, 2, 3, 4)
        assertEquals(1, hour)
        assertEquals(2, minute)
        assertEquals(3, second)
        assertEquals(4, nano)
    }

    @Test
    fun `copy() returns a Time with altered values`() {
        assertEquals(Time(3, 30), Time(9, 30).copy(hour = 3))
        assertEquals(Time(9, 1), Time(9, 30).copy(minute = 1))
        assertEquals(Time(9, 30, 3), Time(9, 30).copy(second = 3))
        assertEquals(
            Time(9, 30, 0, 3),
            Time(9, 30).copy(nanosecond = 3)
        )
    }

    @Test
    fun `adds zero hours`() {
        assertEquals(Time(9, 0), Time(9, 0) + 0.hours)
    }

    @Test
    fun `adds positive hours`() {
        assertEquals(Time(1, 0), Time(23, 0) + 2.hours)
    }

    @Test
    fun `adds negative hours`() {
        assertEquals(Time(23, 30, 12), Time(2, 30, 12) + (-3).hours)
    }

    @Test
    fun `subtracts zero hours`() {
        assertEquals(Time(9, 0), Time(9, 0) - 0.hours)
    }

    @Test
    fun `subtracts positive hours`() {
        assertEquals(Time(23, 30, 12), Time(2, 30, 12) - 3.hours)
    }

    @Test
    fun `subtracts negative hours`() {
        assertEquals(Time(1, 0), Time(23, 0) - (-2).hours)
    }

    @Test
    fun `String_toTime() throws an exception when given an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toTime() }
        assertFailsWith<DateTimeParseException> { "".toTime(Iso8601.TIME_PARSER) }
    }

    @Test
    fun `String_toTime() parses valid ISO-8601 extended time strings by default`() {
        assertEquals(Time(2, 0), "02".toTime())
        assertEquals(Time(23, 0), "23:00".toTime())
        assertEquals(Time(23, 30, 5), "23:30:05".toTime())
        assertEquals(Time(23, 30, 5, 100_000), "23:30:05.0001".toTime())
    }

    @Test
    fun `String_toTime() parses valid strings with explicit parser`() {
        assertEquals(Time(2, 0), "02".toTime(Iso8601.Basic.TIME_PARSER))
        assertEquals(Time(23, 0), "2300".toTime(Iso8601.Basic.TIME_PARSER))
        assertEquals(Time(23, 30, 5), "233005".toTime(Iso8601.Basic.TIME_PARSER))
        assertEquals(
            Time(23, 30, 5, 100_000),
            "233005.0001".toTime(Iso8601.Basic.TIME_PARSER)
        )
    }
}