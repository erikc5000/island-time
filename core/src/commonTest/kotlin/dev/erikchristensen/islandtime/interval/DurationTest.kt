package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.parser.DateTimeParseException
import kotlin.test.*

class DurationTest {
    @Test
    fun `ZERO returns a duration with a length of zero`() {
        val duration = Duration.ZERO
        assertEquals(0L.seconds, duration.seconds)
        assertEquals(0.nanoseconds, duration.nanosecondAdjustment)
    }

    @Test
    fun `durationOf() creates durations with positive hours`() {
        val duration = durationOf(1.hours)
        assertEquals(3600L.seconds, duration.seconds)
        assertEquals(0.nanoseconds, duration.nanosecondAdjustment)
    }

    @Test
    fun `durationOf() creates durations with negative hours`() {
        val duration = durationOf((-1).hours)
        assertEquals((-3600L).seconds, duration.seconds)
        assertEquals(0.nanoseconds, duration.nanosecondAdjustment)
    }

    @Test
    fun `durationOf() creates durations with positive nanoseconds`() {
        val duration1 = durationOf(5.nanoseconds)
        assertEquals(0L.seconds, duration1.seconds)
        assertEquals(5.nanoseconds, duration1.nanosecondAdjustment)

        val duration2 = durationOf(1_500_000_000.nanoseconds)
        assertEquals(1L.seconds, duration2.seconds)
        assertEquals(500_000_000.nanoseconds, duration2.nanosecondAdjustment)
    }

    @Test
    fun `durationOf() creates durations with negative nanoseconds`() {
        val duration1 = durationOf((-5).nanoseconds)
        assertEquals(0L.seconds, duration1.seconds)
        assertEquals((-5).nanoseconds, duration1.nanosecondAdjustment)

        val duration2 = durationOf((-1_500_000_000).nanoseconds)
        assertEquals((-1L).seconds, duration2.seconds)
        assertEquals((-500_000_000).nanoseconds, duration2.nanosecondAdjustment)
    }

    @Test
    fun `durationOf() creates durations from seconds and nanoseconds components`() {
        val duration1 = durationOf(1.seconds, (-5).nanoseconds)
        assertEquals(0L.seconds, duration1.seconds)
        assertEquals(999_999_995.nanoseconds, duration1.nanosecondAdjustment)

        val duration2 = durationOf((-2).seconds, 1_200_000_000.nanoseconds)
        assertEquals(0L.seconds, duration2.seconds)
        assertEquals((-800_000_000).nanoseconds, duration2.nanosecondAdjustment)

        val duration3 = durationOf((-1).seconds, 1_500_000_000.nanoseconds)
        assertEquals(0L.seconds, duration3.seconds)
        assertEquals(500_000_000.nanoseconds, duration3.nanosecondAdjustment)
    }

    @Test
    fun `isZero property returns true for duration of no length`() {
        assertTrue { Duration.ZERO.isZero }
    }

    @Test
    fun `isZero property returns false for non-zero durations`() {
        assertFalse { durationOf(1.seconds).isZero }
        assertFalse { durationOf((-1).seconds).isZero }
        assertFalse { durationOf(1.nanoseconds).isZero }
    }

    @Test
    fun `isNegative property returns true if seconds is negative`() {
        assertTrue { durationOf((-1).seconds).isNegative }
    }

    @Test
    fun `isNegative property returns true if nanosecondAdjustment is negative`() {
        assertTrue { durationOf((-1).nanoseconds).isNegative }
    }

    @Test
    fun `isNegative property returns false if greater or equal to zero`() {
        assertFalse { Duration.ZERO.isNegative }
        assertFalse { 1.seconds.asDuration().isNegative }
        assertFalse { 1.nanoseconds.asDuration().isNegative }
    }

    @Test
    fun `unary minus negates both the seconds and nanoOfSecond`() {
        assertEquals(
            durationOf((-1).seconds - 1.nanoseconds),
            -durationOf(1.seconds + 1.nanoseconds)
        )

        assertEquals(
            durationOf(1.seconds + 1.nanoseconds),
            -durationOf((-1).seconds - 1.nanoseconds)
        )
    }

    @Test
    fun `adding zero to a duration has no effect`() {
        assertEquals(durationOf(1.hours), durationOf(1.hours) + Duration.ZERO)
    }

    @Test
    fun `adding a duration to zero returns that duration`() {
        assertEquals(durationOf(1.hours), Duration.ZERO + durationOf(1.hours))
    }

    @Test
    fun `adding a positive duration to a non-zero duration sums them`() {
        assertEquals(
            durationOf(270L.seconds, 50.nanoseconds),
            (4.minutes + 30.seconds).asDuration() + 50.nanoseconds.asDuration()
        )

        assertEquals(
            durationOf(30.seconds),
            ((-1).minutes - 30.seconds).asDuration() + 2.minutes.asDuration()
        )

        assertEquals(
            durationOf(0.seconds, (-800_000_000).nanoseconds),
            durationOf((-1).seconds, (-600_000_000).nanoseconds) + durationOf(800_000_000.nanoseconds)
        )

        assertEquals(
            durationOf(1.seconds, 999_999_998.nanoseconds),
            durationOf(999_999_999.nanoseconds) + durationOf(999_999_999.nanoseconds)
        )

        assertEquals(
            durationOf((-2).seconds),
            durationOf((-2).seconds, (-500_000_000).nanoseconds) + durationOf(500_000_000.nanoseconds)
        )
    }

    @Test
    fun `toComponents() executes function with expected arguments`() {
        durationOf(5.hours + 30.minutes + 30.seconds + 500.milliseconds)
            .toComponents { hours, minutes, seconds, nanoseconds ->
                assertEquals(5L.hours, hours)
                assertEquals(30.minutes, minutes)
                assertEquals(30.seconds, seconds)
                assertEquals(500_000_000.nanoseconds, nanoseconds)
            }

        durationOf((-1).days - 1.hours - 1.minutes - 1.seconds - 800.milliseconds - 50.nanoseconds)
            .toComponents { days, hours, minutes, seconds, nanoseconds ->
                assertEquals((-1L).days, days)
                assertEquals((-1).hours, hours)
                assertEquals((-1).minutes, minutes)
                assertEquals((-1).seconds, seconds)
                assertEquals((-800_000_050).nanoseconds, nanoseconds)
            }
    }

    @Test
    fun `absoluteValue returns the absolute value of a duration`() {
        assertEquals(durationOf(30.seconds), durationOf(30.seconds).absoluteValue)
        assertEquals(durationOf(30.seconds), durationOf((-30).seconds).absoluteValue)
    }

    @Test
    fun `abs() returns the absolute value of a duration`() {
        assertEquals(durationOf(30.seconds), abs(30.seconds.asDuration()))
        assertEquals(durationOf(30.seconds), abs((-30).seconds.asDuration()))
    }

    @Test
    fun `toString() converts zero durations to 'PT0S'`() {
        assertEquals("PT0S", Duration.ZERO.toString())
    }

    @Test
    fun `toString() converts positive durations to ISO-8601 duration representation`() {
        assertEquals(
            "PT25H30M30.5S",
            durationOf(1.days + 1.hours + 30.minutes + 30.seconds + 500.milliseconds).toString()
        )

        assertEquals(
            "PT0.005S",
            durationOf(5.milliseconds).toString()
        )
    }

    @Test
    fun `toString() converts negative durations to ISO-8601 duration representation`() {
        assertEquals(
            "PT-25H-30M-30.5S",
            durationOf((-1).days - 1.hours - 30.minutes - 30.seconds - 500.milliseconds).toString()
        )

        assertEquals(
            "PT-0.005S",
            durationOf((-5).milliseconds).toString()
        )
    }

    @Test
    fun `String_toDuration() throws an exception when string is empty`() {
        assertFailsWith<DateTimeParseException> { "".toDuration() }
    }

    @Test
    fun `String_toDuration() throws an exception when string is invalid`() {
        assertFailsWith<DateTimeParseException> { "P4Y".toDuration() }
        assertFailsWith<DateTimeParseException> { "P4M".toDuration() }
        assertFailsWith<DateTimeParseException> { "PT4H ".toDuration() }
        assertFailsWith<DateTimeParseException> { " PT4H".toDuration() }
        assertFailsWith<DateTimeParseException> { "PT4S4H".toDuration() }
        assertFailsWith<DateTimeParseException> { "PT9Y".toDuration() }
    }

    @Test
    fun `String_toDuration() parses durations of zero`() {
        assertEquals(Duration.ZERO, "P0D".toDuration())
        assertEquals(Duration.ZERO, "PT0S".toDuration())
    }

    @Test
    fun `String_toDuration() converts ISO-8601 period strings to a Duration`() {
        assertEquals(
            durationOf(1.seconds),
            "PT1S".toDuration()
        )

        assertEquals(
            durationOf(1.days),
            "P1D".toDuration()
        )

        assertEquals(
            durationOf(0.seconds, 1.nanoseconds),
            "PT0.000000001S".toDuration()
        )

        assertEquals(
            durationOf(1.hours + 1.seconds),
            "PT1H1S".toDuration()
        )

        assertEquals(
            durationOf(1.hours - 5.seconds - 200.milliseconds),
            "PT1H-5.2S".toDuration()
        )
    }
}