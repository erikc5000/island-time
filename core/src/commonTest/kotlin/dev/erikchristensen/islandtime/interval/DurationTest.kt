package dev.erikchristensen.islandtime.interval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DurationTest {
    @Test
    fun `ZERO returns a duration with a length of zero`() {
        val duration = Duration.ZERO
        assertEquals(0L.seconds, duration.seconds)
        assertEquals(0.nanoseconds, duration.nanoOfSeconds)
    }

    @Test
    fun `durationOf() creates durations with positive hours`() {
        val duration = durationOf(1.hours)
        assertEquals(3600L.seconds, duration.seconds)
        assertEquals(0.nanoseconds, duration.nanoOfSeconds)
    }

    @Test
    fun `durationOf() creates durations with negative hours`() {
        val duration = durationOf((-1).hours)
        assertEquals((-3600L).seconds, duration.seconds)
        assertEquals(0.nanoseconds, duration.nanoOfSeconds)
    }

    @Test
    fun `durationOf() creates durations with positive nanoseconds`() {
        val duration1 = durationOf(5.nanoseconds)
        assertEquals(0L.seconds, duration1.seconds)
        assertEquals(5.nanoseconds, duration1.nanoOfSeconds)

        val duration2 = durationOf(1_500_000_000.nanoseconds)
        assertEquals(1L.seconds, duration2.seconds)
        assertEquals(500_000_000.nanoseconds, duration2.nanoOfSeconds)
    }

    @Test
    fun `durationOf() creates durations with negative nanoseconds`() {
        val duration1 = durationOf((-5).nanoseconds)
        assertEquals(0L.seconds, duration1.seconds)
        assertEquals((-5).nanoseconds, duration1.nanoOfSeconds)

        val duration2 = durationOf((-1_500_000_000).nanoseconds)
        assertEquals((-1L).seconds, duration2.seconds)
        assertEquals((-500_000_000).nanoseconds, duration2.nanoOfSeconds)
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
    fun `isNegative property returns true if nanoOfSeconds is negative`() {
        assertTrue { durationOf((-1).nanoseconds).isNegative }
    }

    @Test
    fun `isNegative property returns false if greater or equal to zero`() {
        assertFalse{ Duration.ZERO.isNegative }
        assertFalse{ 1.seconds.asDuration().isNegative }
        assertFalse{ 1.nanoseconds.asDuration().isNegative }
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
                assertEquals(5.hours, hours)
                assertEquals(30.minutes, minutes)
                assertEquals(30.seconds, seconds)
                assertEquals(500_000_000.nanoseconds, nanoseconds)
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
}