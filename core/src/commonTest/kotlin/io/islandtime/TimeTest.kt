package io.islandtime

import io.islandtime.internal.NANOSECONDS_PER_DAY
import io.islandtime.internal.SECONDS_PER_DAY
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TimeTest : AbstractIslandTimeTest() {
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
    fun `Time_fromSecondOfDay() throws an exception when the second value is invalid`() {
        assertFailsWith<DateTimeException> { Time.fromSecondOfDay(-1) }
        assertFailsWith<DateTimeException> { Time.fromSecondOfDay(SECONDS_PER_DAY) }
    }

    @Test
    fun `Time_fromSecondOfDay() throws an exception when the nanosecond value is invalid`() {
        assertFailsWith<DateTimeException> { Time.fromSecondOfDay(0, -1) }
        assertFailsWith<DateTimeException> { Time.fromSecondOfDay(0, 1_000_000_000) }
    }

    @Test
    fun `Time_fromSecondOfDay() creates a Time from a second of the day value`() {
        assertEquals(
            Time(1, 1, 1, 1),
            Time.fromSecondOfDay(3661, 1)
        )
    }

    @Test
    fun `Time_fromNanosecondOfDay() throws an exception when the nanosecond value is invalid`() {
        assertFailsWith<DateTimeException> { Time.fromNanosecondOfDay(-1L) }
        assertFailsWith<DateTimeException> { Time.fromNanosecondOfDay(NANOSECONDS_PER_DAY) }
    }

    @Test
    fun `Time_fromNanosecondOfDay() creates a Time from a nanosecond of the day value`() {
        assertEquals(
            Time(0, 0, 1, 1),
            Time.fromNanosecondOfDay(1_000_000_001L)
        )
    }

    @Test
    fun `can be compared`() {
        assertTrue { Time(0, 0) < Time(1, 0) }
        assertTrue { Time(0, 1) < Time(0, 2) }
        assertTrue { Time(0, 0, 1) < Time(0, 0, 2) }
    }

    @Test
    fun `secondOfDay property`() {
        assertEquals(0, Time(0, 0, 0).secondOfDay)
        assertEquals(86_399, Time(23, 59, 59).secondOfDay)
    }

    @Test
    fun `nanosecondOfDay property`() {
        assertEquals(0, Time(0, 0, 0, 0).nanosecondOfDay)
        assertEquals(
            86_399_999_999_999L,
            Time(23, 59, 59, 999_999_999).nanosecondOfDay
        )
    }

    @Test
    fun `secondsSinceStartOfDay property`() {
        assertEquals(1.seconds, Time(0, 0, 1, 1).secondsSinceStartOfDay)
    }

    @Test
    fun `nanosecondsSinceStartOfDay property`() {
        assertEquals(1L.nanoseconds, Time(0, 0, 0, 1).nanosecondsSinceStartOfDay)
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
    fun `adding a duration of zero doesn't affect the time`() {
        val time = Time(1, 1, 1, 1)
        assertEquals(time, time + Duration.ZERO)
    }

    @Test
    fun `add a duration`() {
        assertEquals(
            Time(0, 0, 0, 2),
            Time(23, 0, 0, 1) +
                durationOf(2.days + 1.hours + 1.nanoseconds)
        )
    }

    @Test
    fun `subtracting a duration of zero doesn't affect the time`() {
        val time = Time(1, 1, 1, 1)
        assertEquals(time, time - Duration.ZERO)
    }

    @Test
    fun `subtract a duration`() {
        assertEquals(
            Time(22, 0, 0, 0),
            Time(23, 0, 0, 1) -
                durationOf(2.days + 1.hours + 1.nanoseconds)
        )
    }

    @Test
    fun `adding zero of any unit doesn't affect the time`() {
        val time = Time(1, 1, 1, 1)

        assertEquals(
            time,
            time + 0.hours + 0.minutes + 0.seconds + 0.milliseconds + 0.microseconds + 0.nanoseconds
        )

        assertEquals(
            time,
            time + 0L.hours + 0L.minutes + 0L.seconds + 0L.milliseconds + 0L.microseconds + 0L.nanoseconds
        )
    }

    @Test
    fun `subtracting zero of any unit doesn't affect the time`() {
        val time = Time(1, 1, 1, 1)

        assertEquals(
            time,
            time - 0.hours - 0.minutes - 0.seconds - 0.milliseconds - 0.microseconds - 0.nanoseconds
        )

        assertEquals(
            time,
            time - 0L.hours - 0L.minutes - 0L.seconds - 0L.milliseconds - 0L.microseconds - 0L.nanoseconds
        )
    }

    @Test
    fun `add positive hours`() {
        assertEquals(Time(1, 0), Time(23, 0) + 2.hours)
        assertEquals(Time(8, 0), Time(1, 0) + Long.MAX_VALUE.hours)
    }

    @Test
    fun `add negative hours`() {
        assertEquals(Time(23, 30, 12), Time(2, 30, 12) + (-3).hours)
        assertEquals(Time.MIDNIGHT, Time(8, 0) + Long.MIN_VALUE.hours)
    }

    @Test
    fun `subtract positive hours`() {
        assertEquals(Time(23, 30, 12), Time(2, 30, 12) - 3.hours)
        assertEquals(Time.MIDNIGHT, Time(7, 0) - Long.MAX_VALUE.hours)
    }

    @Test
    fun `subtract negative hours`() {
        assertEquals(Time(1, 0), Time(23, 0) - (-2).hours)
        assertEquals(Time(9, 0), Time(1, 0) - Long.MIN_VALUE.hours)
    }

    @Test
    fun `add positive minutes`() {
        assertEquals(
            Time(0, 1),
            Time(23, 59) + 2.minutes
        )
        assertEquals(Time.NOON, Time.NOON + 24.hours.inMinutes)
    }

    @Test
    fun `add negative minutes`() {
        assertEquals(
            Time(23, 59),
            Time(0, 1) + (-2).minutes
        )
    }

    @Test
    fun `subtract positive minutes`() {
        assertEquals(
            Time(23, 59),
            Time(0, 1) - 2.minutes
        )
    }

    @Test
    fun `subtract negative minutes`() {
        assertEquals(
            Time(0, 1),
            Time(23, 59) - (-2).minutes
        )
    }

    @Test
    fun `add positive seconds`() {
        assertEquals(
            Time(0, 0, 1),
            Time(23, 59, 59) + 2.seconds
        )
        assertEquals(Time.NOON, Time.NOON + 24.hours.inSeconds)
    }

    @Test
    fun `add negative seconds`() {
        assertEquals(
            Time(23, 59, 59),
            Time(0, 0, 1) + (-2).seconds
        )
    }

    @Test
    fun `subtract positive seconds`() {
        assertEquals(
            Time(23, 59, 59),
            Time(0, 0, 1) - 2.seconds
        )
    }

    @Test
    fun `subtract negative seconds`() {
        assertEquals(
            Time(0, 0, 1),
            Time(23, 59, 59) - (-2).seconds
        )
    }

    @Test
    fun `add positive milliseconds`() {
        assertEquals(
            Time(0, 0, 0, 100_111_111),
            Time(23, 59, 59, 900_111_111) + 200.milliseconds
        )

        assertEquals(
            Time.fromNanosecondOfDay(25_975_807_000_000L),
            Time.MIDNIGHT + Long.MAX_VALUE.milliseconds
        )

        assertEquals(Time.NOON, Time.NOON + 24.hours.inMilliseconds)
    }

    @Test
    fun `add negative milliseconds`() {
        assertEquals(
            Time(23, 59, 59, 900_111_111),
            Time(0, 0, 0, 100_111_111) + (-200).milliseconds
        )

        assertEquals(
            Time.fromNanosecondOfDay(60_424_192_000_000L),
            Time.MIDNIGHT + Long.MIN_VALUE.milliseconds
        )
    }

    @Test
    fun `subtract positive milliseconds`() {
        assertEquals(
            Time(23, 59, 59, 900_111_111),
            Time(0, 0, 0, 100_111_111) - 200.milliseconds
        )

        assertEquals(
            Time.fromNanosecondOfDay(60_424_193_000_000L),
            Time.MIDNIGHT - Long.MAX_VALUE.milliseconds
        )
    }

    @Test
    fun `subtract negative milliseconds`() {
        assertEquals(
            Time(0, 0, 0, 100_111_111),
            Time(23, 59, 59, 900_111_111) - (-200).milliseconds
        )

        assertEquals(
            Time.fromNanosecondOfDay(25_975_808_000_000L),
            Time.MIDNIGHT - Long.MIN_VALUE.milliseconds
        )
    }

    @Test
    fun `add positive microseconds`() {
        assertEquals(
            Time(0, 0, 0, 100_111),
            Time(23, 59, 59, 999_900_111) + 200.microseconds
        )

        assertEquals(Time.NOON, Time.NOON + 24.hours.inMicroseconds)
    }

    @Test
    fun `add negative microseconds`() {
        assertEquals(
            Time(23, 59, 59, 999_900_111),
            Time(0, 0, 0, 100_111) + (-200).microseconds
        )
    }

    @Test
    fun `subtract positive microseconds`() {
        assertEquals(
            Time(23, 59, 59, 999_900_111),
            Time(0, 0, 0, 100_111) - 200.microseconds
        )
    }

    @Test
    fun `subtract negative microseconds`() {
        assertEquals(
            Time(0, 0, 0, 100_111),
            Time(23, 59, 59, 999_900_111) - (-200).microseconds
        )
    }

    @Test
    fun `add positive nanoseconds`() {
        assertEquals(
            Time(0, 0, 0, 100),
            Time(23, 59, 59, 999_999_900) + 200.nanoseconds
        )

        assertEquals(Time.NOON, Time.NOON + 24.hours.inNanoseconds)
    }

    @Test
    fun `add negative nanoseconds`() {
        assertEquals(
            Time(23, 59, 59, 999_999_900),
            Time(0, 0, 0, 100) + (-200).nanoseconds
        )
    }

    @Test
    fun `subtract positive nanoseconds`() {
        assertEquals(
            Time(23, 59, 59, 999_999_900),
            Time(0, 0, 0, 100) - 200.nanoseconds
        )
    }

    @Test
    fun `subtract negative nanoseconds`() {
        assertEquals(
            Time(0, 0, 0, 100),
            Time(23, 59, 59, 999_999_900) - (-200).nanoseconds
        )
    }

    @Test
    fun `String_toTime() throws an exception when given an empty string`() {
        assertFailsWith<TemporalParseException> { "".toTime() }
        assertFailsWith<TemporalParseException> { "".toTime(DateTimeParsers.Iso.TIME) }
    }

    @Test
    fun `String_toTime() throws an exception when the parser can't supply all required properties`() {
        assertFailsWith<TemporalParseException> { "14".toTime(temporalParser { minuteOfHour(2) }) }
    }

    @Test
    fun `String_toTime() throws an exception if parsed values cause overflow`() {
        val parser = temporalParser {
            hourOfDay()
            +':'
            minuteOfHour()
            +':'
            fractionalSecondOfMinute()
        }

        assertFailsWith<DateTimeException> { "50000000000:00:00.0".toTime(parser) }
        assertFailsWith<DateTimeException> { "0:50000000000:00.0".toTime(parser) }
        assertFailsWith<DateTimeException> { "0:00:50000000000.0".toTime(parser) }
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
        assertEquals(Time(2, 0), "02".toTime(DateTimeParsers.Iso.Basic.TIME))
        assertEquals(Time(23, 0), "2300".toTime(DateTimeParsers.Iso.Basic.TIME))
        assertEquals(Time(23, 30, 5), "233005".toTime(DateTimeParsers.Iso.Basic.TIME))
        assertEquals(
            Time(23, 30, 5, 100_000),
            "233005.0001".toTime(DateTimeParsers.Iso.Basic.TIME)
        )
    }
}