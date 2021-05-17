package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class UtcOffsetTest : AbstractIslandTimeTest() {
    @Test
    fun `UtcOffset() requires all values to have the same sign`() {
        assertFailsWith<DateTimeException> { UtcOffset((-2).hours, 30.minutes) }
        assertFailsWith<DateTimeException> { UtcOffset((-2).hours, (-30).minutes, 5.seconds) }
        assertFailsWith<DateTimeException> { UtcOffset((-2).hours, 30.minutes, (-5).seconds) }
        assertFailsWith<DateTimeException> { UtcOffset(2.hours, 0.minutes, (-5).seconds) }
        assertFailsWith<DateTimeException> { UtcOffset(2.hours, (-30).minutes, 5.seconds) }
        assertFailsWith<DateTimeException> { UtcOffset(2.hours, (-30).minutes, (-5).seconds) }
        assertFailsWith<DateTimeException> { UtcOffset(0.hours, (-30).minutes, 5.seconds) }
        assertFailsWith<DateTimeException> { UtcOffset(0.hours, 30.minutes, (-5).seconds) }
    }

    @Test
    fun `UtcOffset() requires each component to be valid individually`() {
        assertFailsWith<DateTimeException> { UtcOffset(19.hours) }
        assertFailsWith<DateTimeException> { UtcOffset(2.hours, 60.minutes) }
        assertFailsWith<DateTimeException> { UtcOffset((-2).hours, (-60).minutes) }
        assertFailsWith<DateTimeException> { UtcOffset(2.hours, 0.minutes, 60.seconds) }
        assertFailsWith<DateTimeException> { UtcOffset((-2).hours, 0.minutes, (-60).seconds) }
    }

    @Test
    fun `UtcOffset() creates an offset with the sum of all components`() {
        assertEquals(3_600.seconds, UtcOffset(1.hours).totalSeconds)
        assertEquals(3_660.seconds, UtcOffset(1.hours, 1.minutes).totalSeconds)
        assertEquals((-3_660).seconds, UtcOffset((-1).hours, (-1).minutes).totalSeconds)
        assertEquals(3_661.seconds, UtcOffset(1.hours, 1.minutes, 1.seconds).totalSeconds)
        assertEquals(3_601.seconds, UtcOffset(1.hours, 0.minutes, 1.seconds).totalSeconds)
        assertEquals((-3_601).seconds, UtcOffset((-1).hours, 0.minutes, (-1).seconds).totalSeconds)
        assertEquals(3_661.seconds, UtcOffset(1.hours, 1.minutes, 1.seconds).totalSeconds)
        assertEquals((-3_661).seconds, UtcOffset((-1).hours, (-1).minutes, (-1).seconds).totalSeconds)
        assertEquals(61.seconds, UtcOffset(0.hours, 1.minutes, 1.seconds).totalSeconds)
        assertEquals((-61).seconds, UtcOffset(0.hours, (-1).minutes, (-1).seconds).totalSeconds)
    }

    @Test
    fun `Hours_asUtcOffset() creates a UtcOffset from a duration of hours`() {
        assertEquals((-3600).seconds, (-1).hours.asUtcOffset().totalSeconds)
    }

    @Test
    fun `Hours_asUtcOffset() throws an exception on overflow`() {
        assertFailsWith<DateTimeException> { Int.MAX_VALUE.hours.asUtcOffset() }
    }

    @Test
    fun `Minutes_asUtcOffset() creates a UtcOffset from a duration of minutes`() {
        assertEquals((-12_000).seconds, (-200).minutes.asUtcOffset().totalSeconds)
    }

    @Test
    fun `Minutes_asUtcOffset() throws an exception on overflow`() {
        assertFailsWith<DateTimeException> { Int.MAX_VALUE.minutes.asUtcOffset() }
    }

    @Test
    fun `Seconds_asUtcOffset() creates a UtcOffset from a duration of seconds`() {
        assertEquals(1.seconds, 1.seconds.asUtcOffset().totalSeconds)
    }

    @Test
    fun `isZero() returns true only when the offset is zero`() {
        assertTrue { UtcOffset(0.seconds).isZero() }
        assertFalse { UtcOffset((-1).seconds).isZero() }
    }

    @Test
    fun `construction succeeds when the offset is within the valid range`() {
        assertEquals(UtcOffset.MAX_TOTAL_SECONDS, UtcOffset.MAX.totalSeconds)
        assertEquals(UtcOffset.MIN_TOTAL_SECONDS, UtcOffset.MIN.totalSeconds)
    }

    @Test
    fun `construction throws an exception if the offset is outside the valid range`() {
        assertFailsWith<DateTimeException> { UtcOffset(UtcOffset.MAX_TOTAL_SECONDS + 1.seconds) }
        assertFailsWith<DateTimeException> { UtcOffset(UtcOffset.MIN_TOTAL_SECONDS - 1.seconds) }
    }

    @Test
    fun `offsets can be compared`() {
        assertTrue { 30.minutes.asUtcOffset() > UtcOffset.ZERO }
        assertTrue { (-12).hours.asUtcOffset() < (-200).seconds.asUtcOffset() }
    }

    @Test
    fun `toComponents() breaks an offset down into hours, minutes, and seconds`() {
        UtcOffset((-1).hours, (-30).minutes).toComponents { hours, minutes, seconds ->
            assertEquals((-1).hours, hours)
            assertEquals((-30).minutes, minutes)
            assertEquals(0.seconds, seconds)
        }
    }

    @Test
    fun `toComponents() breaks a time offset down into sign, hours, minutes, and seconds`() {
        UtcOffset((-1).hours, (-30).minutes).toComponents { sign, hours, minutes, seconds ->
            assertEquals(-1, sign)
            assertEquals(1.hours, hours)
            assertEquals(30.minutes, minutes)
            assertEquals(0.seconds, seconds)
        }
    }

    @Test
    fun `toString() returns 'Z' when the offset is zero`() {
        assertEquals("Z", UtcOffset.ZERO.toString())
    }

    @Test
    fun `toString() returns an ISO-8601 time offset string for non-zero offsets`() {
        assertEquals("+02:00", UtcOffset(2.hours).toString())
        assertEquals("-02:00", UtcOffset((-2).hours).toString())
        assertEquals("+02:30", UtcOffset(2.hours, 30.minutes).toString())
        assertEquals("+02:30:05", UtcOffset(2.hours, 30.minutes, 5.seconds).toString())
    }

    @Test
    fun `String_toUtcOffset() throws an exception with empty strings`() {
        assertFailsWith<DateTimeParseException> { "".toUtcOffset() }
    }

    @Test
    fun `String_toUtcOffset() throws an exception with invalid ISO-8601 offsets`() {
        assertFailsWith<DateTimeParseException> { "01:00".toUtcOffset() }
        assertFailsWith<DateTimeParseException> { "--01:00".toUtcOffset() }
        assertFailsWith<DateTimeParseException> { "+1:00".toUtcOffset() }
        assertFailsWith<DateTimeParseException> { "+010:00".toUtcOffset() }
    }

    @Test
    fun `String_toUtcOffset() throws an exception when the parser can't supplied all required fields`() {
        assertFailsWith<DateTimeParseException> { "04".toUtcOffset(DateTimeParsers.Iso.TIME) }
    }

    @Test
    fun `String_toUtcOffset() parses zero offsets indicated by 'Z'`() {
        assertEquals(UtcOffset.ZERO, "Z".toUtcOffset())
    }

    @Test
    fun `String_toUtcOffset() parses valid ISO-8601 extended time offset strings`() {
        assertEquals(
            UtcOffset(1.hours),
            "+01".toUtcOffset()
        )

        assertEquals(
            UtcOffset(1.hours),
            "+01:00".toUtcOffset()
        )

        assertEquals(
            UtcOffset(1.hours),
            "+01:00:00".toUtcOffset()
        )

        assertEquals(
            UtcOffset((-4).hours, (-30).minutes),
            "-04:30".toUtcOffset()
        )
    }

    @Test
    fun `String_toUtcOffset() parses valid ISO-8601 basic time offsets with explicit parser`() {
        assertEquals(
            UtcOffset(1.hours),
            "+01".toUtcOffset(DateTimeParsers.Iso.UTC_OFFSET)
        )

        assertEquals(
            UtcOffset(1.hours),
            "+0100".toUtcOffset(DateTimeParsers.Iso.UTC_OFFSET)
        )

        assertEquals(
            UtcOffset(1.hours),
            "+010000".toUtcOffset(DateTimeParsers.Iso.UTC_OFFSET)
        )

        assertEquals(
            UtcOffset((-4).hours, (-30).minutes),
            "-0430".toUtcOffset(DateTimeParsers.Iso.UTC_OFFSET)
        )
    }
}
