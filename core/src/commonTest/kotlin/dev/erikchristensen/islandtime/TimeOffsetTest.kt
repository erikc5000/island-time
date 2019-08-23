package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.hours
import dev.erikchristensen.islandtime.interval.minutes
import dev.erikchristensen.islandtime.interval.seconds
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
    fun `timeOffsetOf() creates an offset with the sum of components`() {
        assertEquals(3_600.seconds, timeOffsetOf(1.hours).totalSeconds)
        assertEquals(3_661.seconds, timeOffsetOf(1.hours, 1.minutes, 1.seconds).totalSeconds)
        assertEquals((-3_661).seconds, timeOffsetOf((-1).hours, (-1).minutes, (-1).seconds).totalSeconds)
    }

    @Test
    fun `isValid property returns false if offset is outside of +-18_00`() {
        assertFalse { TimeOffset((TimeOffset.MAX_VALUE + 1).seconds).isValid }
        assertFalse { TimeOffset((TimeOffset.MIN_VALUE - 1).seconds).isValid }
    }

    @Test
    fun `time offsets can be compared`() {
        assertTrue { TimeOffset(400.seconds) > TimeOffset.UTC }
        assertTrue { TimeOffset((-1200).seconds) < TimeOffset((-200).seconds) }
    }

    @Test
    fun `toString() returns 'Z' when the offset is UTC`() {
        assertEquals("Z", TimeOffset.UTC.toString())
    }

    @Test
    fun `toString() returns an ISO-860 time offset string for non-UTC offsets`() {
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