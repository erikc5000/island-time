package io.islandtime.measures

import io.islandtime.parser.DateTimeParseException
import kotlin.test.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds as kotlinSeconds
import kotlin.time.nanoseconds as kotlinNanoseconds

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
    fun `isZero() returns true for duration of no length`() {
        assertTrue { Duration.ZERO.isZero() }
    }

    @Test
    fun `isZero() returns false for non-zero durations`() {
        assertFalse { durationOf(1.seconds).isZero() }
        assertFalse { durationOf((-1).seconds).isZero() }
        assertFalse { durationOf(1.nanoseconds).isZero() }
    }

    @Test
    fun `isNegative() returns true if seconds is negative`() {
        assertTrue { durationOf((-1).seconds).isNegative() }
    }

    @Test
    fun `isNegative() returns true if nanosecondAdjustment is negative`() {
        assertTrue { durationOf((-1).nanoseconds).isNegative() }
    }

    @Test
    fun `isNegative() returns false if greater or equal to zero`() {
        assertFalse { Duration.ZERO.isNegative() }
        assertFalse { 1.seconds.asDuration().isNegative() }
        assertFalse { 1.nanoseconds.asDuration().isNegative() }
    }

    @Test
    fun `inDays property returns the number of whole days`() {
        assertEquals(1L.days, durationOf(1.days).inDays)
        assertEquals((-1L).days, durationOf((-1).days).inDays)
        assertEquals(0L.days, durationOf(1.days - 1.nanoseconds).inDays)
    }

    @Test
    fun `inHours property returns the number of whole hours`() {
        assertEquals(1L.hours, durationOf(1.hours).inHours)
        assertEquals((-1L).hours, durationOf((-1).hours).inHours)
        assertEquals(0L.hours, durationOf(1.hours - 1.nanoseconds).inHours)
    }

    @Test
    fun `inMinutes property returns the number of whole minutes`() {
        assertEquals(1L.minutes, durationOf(1.minutes).inMinutes)
        assertEquals((-1L).minutes, durationOf((-1).minutes).inMinutes)
        assertEquals(0L.minutes, durationOf(1.minutes - 1.nanoseconds).inMinutes)
    }

    @Test
    fun `inMilliseconds property returns the number of whole milliseconds`() {
        assertEquals(1L.milliseconds, durationOf(1.milliseconds).inMilliseconds)
        assertEquals((-1L).milliseconds, durationOf((-1).milliseconds).inMilliseconds)
        assertEquals(1L.milliseconds, durationOf(1.milliseconds + 1.nanoseconds).inMilliseconds)
        assertEquals(0L.milliseconds, durationOf(1.milliseconds - 1.nanoseconds).inMilliseconds)
    }

    @Test
    fun `inMilliseconds property throws an exception on overflow`() {
        assertFailsWith<ArithmeticException> { durationOf(Long.MAX_VALUE.seconds).inMilliseconds }
    }

    @Test
    fun `inMicroseconds property returns the number of whole microseconds`() {
        assertEquals(1L.microseconds, durationOf(1.microseconds).inMicroseconds)
        assertEquals((-1L).microseconds, durationOf((-1).microseconds).inMicroseconds)
        assertEquals(1L.microseconds, durationOf(1.microseconds + 1.nanoseconds).inMicroseconds)
        assertEquals(0L.microseconds, durationOf(1.microseconds - 1.nanoseconds).inMicroseconds)
    }

    @Test
    fun `inMicroseconds property throws an exception on overflow`() {
        assertFailsWith<ArithmeticException> { durationOf(365.days * 300_000).inMicroseconds }
    }

    @Test
    fun `inNanoseconds property returns the number of whole nanoseconds`() {
        assertEquals(1L.nanoseconds, durationOf(1.nanoseconds).inNanoseconds)
        assertEquals((-1L).nanoseconds, durationOf((-1).nanoseconds).inNanoseconds)
        assertEquals(1_000_000_001L.nanoseconds, durationOf(1.seconds, 1.nanoseconds).inNanoseconds)
        assertEquals(0L.nanoseconds, Duration.ZERO.inNanoseconds)
    }

    @Test
    fun `inNanoseconds property throws an exception on overflow`() {
        assertFailsWith<ArithmeticException> { durationOf(365.days * 300).inNanoseconds }
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
    fun `unary minus throws an exception on overflow`() {
        assertFailsWith<ArithmeticException> { -durationOf(Long.MIN_VALUE.seconds) }
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
    fun `subtraction of positive duration`() {
        assertEquals(durationOf((-1).nanoseconds), Duration.ZERO - durationOf(1.nanoseconds))
        assertEquals(durationOf(Long.MIN_VALUE.seconds), durationOf((-1).seconds) - Long.MAX_VALUE.seconds)
    }

    @Test
    fun `subtraction of negative duration`() {
        assertEquals(durationOf(1.nanoseconds), Duration.ZERO - durationOf((-1).nanoseconds))
        assertEquals(Duration.MAX, durationOf((-1).seconds) - Duration.MIN)
    }

    @Test
    fun `throws an exception when addition or subtraction of another duration causes overflow`() {
        assertFailsWith<ArithmeticException> { Duration.MAX + durationOf(1.nanoseconds) }
        assertFailsWith<ArithmeticException> { Duration.MAX - -durationOf(1.nanoseconds) }
        assertFailsWith<ArithmeticException> { Duration.MIN - durationOf(1.nanoseconds) }
        assertFailsWith<ArithmeticException> { Duration.MIN + -durationOf(1.nanoseconds) }
    }

    @Test
    fun `multiplying by zero returns ZERO`() {
        assertEquals(Duration.ZERO, 2.hours.asDuration() * 0)
    }

    @Test
    fun `multiplying by 1 returns the same duration`() {
        assertEquals(4.seconds.asDuration(), 4.seconds.asDuration() * 1)
        assertEquals((-4).seconds.asDuration(), (-4).seconds.asDuration() * 1)
    }

    @Test
    fun `multiplying by -1 reverses the sign of the duration`() {
        assertEquals((-4).seconds.asDuration(), 4.seconds.asDuration() * -1)
        assertEquals(4.seconds.asDuration(), (-4).seconds.asDuration() * -1)
    }

    @Test
    fun `throws an exception when multiplication causes overflow`() {
        assertFailsWith<ArithmeticException> { Long.MIN_VALUE.seconds.asDuration() * -1 }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE.hours.asDuration() * Int.MAX_VALUE }
    }

    @Test
    fun `multiplication by a positive scalar value`() {
        assertEquals(
            (25.hours + 2.seconds + 500.milliseconds).asDuration(),
            5 * (5.hours + 500.milliseconds).asDuration()
        )
    }

    @Test
    fun `multiplication by a negative scalar value`() {
        assertEquals(
            ((-25).hours - 2.seconds - 500.milliseconds).asDuration(),
            (5.hours + 500.milliseconds).asDuration() * -5
        )
    }

    @Test
    fun `dividing by zero causes an exception`() {
        assertFailsWith<ArithmeticException> { durationOf(5.hours) / 0 }
    }

    @Test
    fun `dividing by 1 returns the same duration`() {
        assertEquals(1.minutes.asDuration(), 1.minutes.asDuration() / 1)
    }

    @Test
    fun `dividing by -1 negates the duration`() {
        assertEquals(1.minutes.asDuration(), (-1).minutes.asDuration() / -1)
    }

    @Test
    fun `dividing by -1 causes an exception when the duration is at minimum`() {
        assertFailsWith<ArithmeticException> { Duration.MIN / -1 }
    }

    @Test
    fun `division by a positive scalar value`() {
        assertEquals(
            111_111_111.nanoseconds.asDuration(),
            1.seconds.asDuration() / 9
        )

        assertEquals(
            durationOf(12.minutes + 1.nanoseconds),
            durationOf(1.hours + 5.nanoseconds) / 5
        )
    }

    @Test
    fun `division by a negative scalar value`() {
        assertEquals(
            (-100).milliseconds.asDuration(),
            1.seconds.asDuration() / -10
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
    fun `truncatedToDays() truncates the precision to 24-hour days`() {
        assertEquals(
            1.days.asDuration(),
            durationOf(1.days + 1.hours + 1.nanoseconds).truncatedToDays()
        )
        assertEquals(
            (-1).days.asDuration(),
            durationOf((-1).days - 1.hours - 1.nanoseconds).truncatedToDays()
        )
        assertEquals(
            Duration.ZERO,
            durationOf(1.days - 1.nanoseconds).truncatedToDays()
        )
        assertEquals(
            Duration.ZERO,
            durationOf((-1).days + 1.nanoseconds).truncatedToDays()
        )
    }

    @Test
    fun `truncatedToHours() truncates the precision to hours`() {
        assertEquals(
            1.hours.asDuration(),
            durationOf(1.hours + 1.minutes + 1.nanoseconds).truncatedToHours()
        )
        assertEquals(
            (-1).hours.asDuration(),
            durationOf((-1).hours - 1.minutes - 1.nanoseconds).truncatedToHours()
        )
        assertEquals(
            Duration.ZERO,
            durationOf(1.hours - 1.nanoseconds).truncatedToHours()
        )
        assertEquals(
            Duration.ZERO,
            durationOf((-1).hours + 1.nanoseconds).truncatedToHours()
        )
    }

    @Test
    fun `truncatedToMinutes() truncates the precision to minutes`() {
        assertEquals(
            1.minutes.asDuration(),
            durationOf(1.minutes + 1.seconds + 1.nanoseconds).truncatedToMinutes()
        )
        assertEquals(
            (-1).minutes.asDuration(),
            durationOf((-1).minutes - 1.seconds - 1.nanoseconds).truncatedToMinutes()
        )
        assertEquals(
            Duration.ZERO,
            durationOf(1.minutes - 1.nanoseconds).truncatedToMinutes()
        )
        assertEquals(
            Duration.ZERO,
            durationOf((-1).minutes + 1.nanoseconds).truncatedToMinutes()
        )
    }

    @Test
    fun `truncatedToSeconds() truncates the precision to seconds`() {
        assertEquals(
            1.seconds.asDuration(),
            durationOf(1.seconds + 1.nanoseconds).truncatedToSeconds()
        )
        assertEquals(
            (-1).seconds.asDuration(),
            durationOf((-1).seconds - 1.nanoseconds).truncatedToSeconds()
        )
        assertEquals(
            Duration.ZERO,
            durationOf(1.seconds - 1.nanoseconds).truncatedToSeconds()
        )
        assertEquals(
            Duration.ZERO,
            durationOf((-1).seconds + 1.nanoseconds).truncatedToSeconds()
        )
    }

    @Test
    fun `truncatedToMilliseconds() truncates the precision to milliseconds`() {
        assertEquals(
            1.milliseconds.asDuration(),
            durationOf(1.milliseconds + 1.nanoseconds).truncatedToMilliseconds()
        )
        assertEquals(
            (-1).milliseconds.asDuration(),
            durationOf((-1).milliseconds - 1.nanoseconds).truncatedToMilliseconds()
        )
        assertEquals(
            Duration.ZERO,
            durationOf(1.milliseconds - 1.nanoseconds).truncatedToMilliseconds()
        )
        assertEquals(
            Duration.ZERO,
            durationOf((-1).milliseconds + 1.nanoseconds).truncatedToMilliseconds()
        )
    }

    @Test
    fun `truncatedToMicroseconds() truncates the precision to microseconds`() {
        assertEquals(
            1.microseconds.asDuration(),
            durationOf(1.microseconds + 1.nanoseconds).truncatedToMicroseconds()
        )
        assertEquals(
            (-1).microseconds.asDuration(),
            durationOf((-1).microseconds - 1.nanoseconds).truncatedToMicroseconds()
        )
        assertEquals(
            Duration.ZERO,
            durationOf(1.microseconds - 1.nanoseconds).truncatedToMicroseconds()
        )
        assertEquals(
            Duration.ZERO,
            durationOf((-1).microseconds + 1.nanoseconds).truncatedToMicroseconds()
        )
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

    @ExperimentalTime
    @Test
    fun `conversion to Kotlin Duration`() {
        assertEquals(
            1.000_000_100.kotlinSeconds,
            durationOf(1.seconds, 100.nanoseconds).toKotlinDuration()
        )

        assertEquals(
            (-1.000_000_100).kotlinSeconds,
            durationOf((-1).seconds, (-100).nanoseconds).toKotlinDuration()
        )
    }

    @ExperimentalTime
    @Test
    fun `conversion from Kotlin Duration`() {
        assertEquals(
            durationOf(5.seconds, 1.nanoseconds),
            (5.kotlinSeconds + 1.kotlinNanoseconds).toIslandDuration()
        )
    }
}