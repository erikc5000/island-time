package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseException
import dev.erikchristensen.islandtime.parser.Iso8601
import kotlin.test.*

class TimeOffsetTest {
    @Test
    fun `timeOffsetOf() requires all values to have the same sign`() {
        assertFailsWith<DateTimeException> { timeOffsetOf((-2).hours, 30.minutes) }
        assertFailsWith<DateTimeException> { timeOffsetOf(2.hours, 0.minutes, (-5).seconds) }
    }

    @Test
    fun `timeOffsetOf() requires each component to be valid individually`() {
        assertFailsWith<DateTimeException> { timeOffsetOf(19.hours) }
        assertFailsWith<DateTimeException> { timeOffsetOf(2.hours, 60.minutes) }
        assertFailsWith<DateTimeException> { timeOffsetOf((-2).hours, 0.minutes, (-60).seconds) }
    }

    @Test
    fun `timeOffsetOf() creates an offset with the sum of all components`() {
        assertEquals(3_600.seconds, timeOffsetOf(1.hours).totalSeconds)
        assertEquals(3_661.seconds, timeOffsetOf(1.hours, 1.minutes, 1.seconds).totalSeconds)
        assertEquals((-3_661).seconds, timeOffsetOf((-1).hours, (-1).minutes, (-1).seconds).totalSeconds)
    }

    @Test
    fun `IntHours_asTimeOffset() creates a time offset from a duration of hours`() {
        assertEquals((-3600).seconds, (-1).hours.asTimeOffset().totalSeconds)
    }

    @Test
    fun `IntMinutes_asTimeOffset() creates a time offset from a duration of minutes`() {
        assertEquals((-12_000).seconds, (-200).minutes.asTimeOffset().totalSeconds)
    }

    @Test
    fun `IntSeconds_asTimeOffset() creates a time offset from a duration of seconds`() {
        assertEquals(1.seconds, 1.seconds.asTimeOffset().totalSeconds)
    }

    @Test
    fun `isValid property returns true is offset is inside the valid range`() {
        assertTrue { TimeOffset.MAX.isValid }
        assertTrue { TimeOffset.MIN.isValid }
    }

    @Test
    fun `isValid property returns false if offset is outside of +-18_00`() {
        assertFalse { TimeOffset(TimeOffset.MAX_TOTAL_SECONDS + 1.seconds).isValid }
        assertFalse { TimeOffset(TimeOffset.MIN_TOTAL_SECONDS - 1.seconds).isValid }
    }

    @Test
    fun `time offsets can be compared`() {
        assertTrue { 30.minutes.asTimeOffset() > TimeOffset.UTC }
        assertTrue { (-12).hours.asTimeOffset() < (-200).seconds.asTimeOffset() }
    }

    @Test
    fun `toComponents() breaks a time offset down into hours, minutes, and seconds`() {
        timeOffsetOf((-1).hours, (-30).minutes).toComponents { hours, minutes, seconds ->
            assertEquals((-1).hours, hours)
            assertEquals((-30).minutes, minutes)
            assertEquals(0.seconds, seconds)
        }
    }

    @Test
    fun `toComponents() breaks a time offset down into sign, hours, minutes, and seconds`() {
        timeOffsetOf((-1).hours, (-30).minutes).toComponents { sign, hours, minutes, seconds ->
            assertEquals(-1, sign)
            assertEquals(1.hours, hours)
            assertEquals(30.minutes, minutes)
            assertEquals(0.seconds, seconds)
        }
    }

    @Test
    fun `toString() returns 'Z' when the offset is UTC`() {
        assertEquals("Z", TimeOffset.UTC.toString())
    }

    @Test
    fun `toString() returns an ISO-8601 time offset string for non-UTC offsets`() {
        assertEquals("+02:00", timeOffsetOf(2.hours).toString())
        assertEquals("-02:00", timeOffsetOf((-2).hours).toString())
        assertEquals("+02:30", timeOffsetOf(2.hours, 30.minutes).toString())
        assertEquals("+02:30:05", timeOffsetOf(2.hours, 30.minutes, 5.seconds).toString())
    }

    @Test
    fun `String_toTimeOffset() throws an exception with empty strings`() {
        assertFailsWith<DateTimeParseException> { "".toTimeOffset() }
    }

    @Test
    fun `String_toTimeOffset() throws an exception with invalid ISO-8601 offsets`() {
        assertFailsWith<DateTimeParseException> { "01:00".toTimeOffset() }
        assertFailsWith<DateTimeParseException> { "--01:00".toTimeOffset() }
        assertFailsWith<DateTimeParseException> { "+1:00".toTimeOffset() }
        assertFailsWith<DateTimeParseException> { "+010:00".toTimeOffset() }
    }

    @Test
    fun `String_toTimeOffset() parses UTC offsets indicated by 'Z'`() {
        assertEquals(TimeOffset.UTC, "Z".toTimeOffset())
    }

    @Test
    fun `String_toTimeOffset() parses valid ISO-8601 extended time offset strings`() {
        assertEquals(
            timeOffsetOf(1.hours),
            "+01".toTimeOffset()
        )

        assertEquals(
            timeOffsetOf(1.hours),
            "+01:00".toTimeOffset()
        )

        assertEquals(
            timeOffsetOf(1.hours),
            "+01:00:00".toTimeOffset()
        )

        assertEquals(
            timeOffsetOf((-4).hours, (-30).minutes),
            "-04:30".toTimeOffset()
        )
    }

    @Test
    fun `String_toTimeOffset() parses valid ISO-8601 basic time offsets with explicit parser`() {
        assertEquals(
            timeOffsetOf(1.hours),
            "+01".toTimeOffset(Iso8601.TIME_OFFSET_PARSER)
        )

        assertEquals(
            timeOffsetOf(1.hours),
            "+0100".toTimeOffset(Iso8601.TIME_OFFSET_PARSER)
        )

        assertEquals(
            timeOffsetOf(1.hours),
            "+010000".toTimeOffset(Iso8601.TIME_OFFSET_PARSER)
        )

        assertEquals(
            timeOffsetOf((-4).hours, (-30).minutes),
            "-0430".toTimeOffset(Iso8601.TIME_OFFSET_PARSER)
        )
    }
}