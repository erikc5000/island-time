package io.islandtime.measures

import io.islandtime.internal.*
import io.islandtime.internal.MICROSECONDS_PER_SECOND
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.internal.toZeroPaddedString
import io.islandtime.measures.Duration.Companion.create
import io.islandtime.parser.DateTimeField
import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParser
import io.islandtime.parser.Iso8601
import kotlin.math.abs

/**
 * A potentially large duration of time at nanosecond precision
 */
class Duration private constructor(
    val seconds: LongSeconds,
    val nanosecondAdjustment: IntNanoseconds = 0.nanoseconds
) : Comparable<Duration> {

    inline val isZero: Boolean get() = this == ZERO

    val isPositive: Boolean
        get() = seconds.value > 0L || nanosecondAdjustment.value > 0

    val isNegative: Boolean
        get() = seconds.value < 0L || nanosecondAdjustment.value < 0

    /**
     * The absolute value of this duration
     */
    val absoluteValue: Duration
        get() = if (isNegative) -this else this

    inline val inDays: LongDays get() = seconds.inWholeDays
    inline val inHours: LongHours get() = seconds.inWholeHours
    inline val inMinutes: LongMinutes get() = seconds.inWholeMinutes

    val inMilliseconds
        get() = seconds.inMillisecondsExact() plusExact nanosecondAdjustment.inWholeMilliseconds.toLong()

    val inMicroseconds
        get() = seconds.inMicrosecondsExact() plusExact nanosecondAdjustment.inWholeMicroseconds.toLong()

    val inNanoseconds
        get() = seconds.inNanosecondsExact() plusExact nanosecondAdjustment.toLong()

    /**
     * Truncate to the number of 24-hour days, replacing all smaller components with zero
     */
    fun truncatedToDays() = create(seconds / SECONDS_PER_DAY * SECONDS_PER_DAY, 0.nanoseconds)

    /**
     * Truncate to the number of whole hours, replacing all smaller components with zero
     */
    fun truncatedToHours() = create(seconds / SECONDS_PER_HOUR * SECONDS_PER_HOUR, 0.nanoseconds)

    /**
     * Truncate to the number of whole minutes, replacing all smaller components with zero
     */
    fun truncatedToMinutes() = create(seconds / SECONDS_PER_MINUTE * SECONDS_PER_MINUTE, 0.nanoseconds)

    /**
     * Truncate to the [second] value, replacing all smaller components with zero
     */
    fun truncatedToSeconds() = create(seconds, 0.nanoseconds)

    /**
     * Truncate the [nanosecond] value to milliseconds, replacing the rest with zero
     */
    fun truncatedToMilliseconds() = create(
        seconds,
        (nanosecondAdjustment.value / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND).nanoseconds
    )

    /**
     * Truncate the [nanosecond] value to microseconds, replacing the rest with zero
     */
    fun truncatedToMicroseconds() = create(
        seconds,
        (nanosecondAdjustment.value / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND).nanoseconds
    )

    operator fun unaryMinus() = create(seconds.value.negateExact().seconds, -nanosecondAdjustment)

    operator fun plus(other: Duration): Duration {
        return when {
            other.isZero -> this
            this.isZero -> other
            else -> plus(other.seconds, other.nanosecondAdjustment)
        }
    }

    operator fun plus(days: IntDays) = plus(days.toLong().inSeconds, 0.nanoseconds)
    operator fun plus(days: LongDays) = plus(days.inSecondsExact(), 0.nanoseconds)

    operator fun plus(hours: IntHours) = plus(hours.toLong().inSeconds, 0.nanoseconds)
    operator fun plus(hours: LongHours) = plus(hours.inSecondsExact(), 0.nanoseconds)

    operator fun plus(minutes: IntMinutes) = plus(minutes.toLong().inSeconds, 0.nanoseconds)
    operator fun plus(minutes: LongMinutes) = plus(minutes.inSecondsExact(), 0.nanoseconds)

    operator fun plus(seconds: IntSeconds) = plus(seconds.toLong(), 0.nanoseconds)
    operator fun plus(seconds: LongSeconds) = plus(seconds, 0.nanoseconds)

    operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.inNanoseconds)

    operator fun plus(milliseconds: LongMilliseconds): Duration {
        return plus(
            milliseconds.inWholeSeconds,
            ((milliseconds.value % MILLISECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MILLISECOND).nanoseconds
        )
    }

    operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.inNanoseconds)

    operator fun plus(microseconds: LongMicroseconds): Duration {
        return plus(
            microseconds.inWholeSeconds,
            ((microseconds.value % MICROSECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MICROSECOND).nanoseconds
        )
    }

    operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLong())

    operator fun plus(nanoseconds: LongNanoseconds): Duration {
        return plus(
            nanoseconds.inWholeSeconds,
            (nanoseconds % NANOSECONDS_PER_SECOND).toInt()
        )
    }

    operator fun minus(other: Duration): Duration {
        return if (other.seconds.value == Long.MIN_VALUE) {
            plus(Long.MAX_VALUE.seconds, -other.nanosecondAdjustment)
        } else {
            plus(-other.seconds, -other.nanosecondAdjustment)
        }
    }

    operator fun minus(days: IntDays) = plus(-days.toLong())

    operator fun minus(days: LongDays): Duration {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(-days)
        }
    }

    operator fun minus(hours: IntHours) = plus(-hours.toLong())

    operator fun minus(hours: LongHours): Duration {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(-hours)
        }
    }

    operator fun minus(minutes: IntMinutes) = plus(-minutes.toLong())

    operator fun minus(minutes: LongMinutes): Duration {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(-minutes)
        }
    }

    operator fun minus(seconds: IntSeconds) = plus(-seconds.toLong())

    operator fun minus(seconds: LongSeconds): Duration {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(-seconds)
        }
    }

    operator fun minus(milliseconds: IntMilliseconds) = plus(-milliseconds.toLong())

    operator fun minus(milliseconds: LongMilliseconds): Duration {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(-milliseconds)
        }
    }

    operator fun minus(microseconds: IntMicroseconds) = plus(-microseconds.toLong())

    operator fun minus(microseconds: LongMicroseconds): Duration {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(-microseconds)
        }
    }

    operator fun minus(nanoseconds: IntNanoseconds) = plus(-nanoseconds.toLong())

    operator fun minus(nanoseconds: LongNanoseconds): Duration {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(-nanoseconds)
        }
    }

    operator fun times(scalar: Int): Duration {
        return when (scalar) {
            0 -> ZERO
            1 -> this
            else -> {
                // TODO: Revisit this if and when overflow safe operations are fully added to the duration units
                var newSeconds = seconds.value timesExact scalar
                var newNanoseconds = nanosecondAdjustment.toLong() * scalar
                newSeconds = newSeconds plusExact newNanoseconds.inWholeSeconds.value
                newNanoseconds %= NANOSECONDS_PER_SECOND
                create(newSeconds.seconds, newNanoseconds.toInt())
            }
        }
    }

    operator fun div(scalar: Int): Duration {
        return when (scalar) {
            0 -> throw ArithmeticException("Division by zero")
            1 -> this
            -1 -> -this
            else -> {
                val fractionalSeconds = seconds.value.toDouble() / scalar
                val newSeconds = fractionalSeconds.toLong()
                val newNanoseconds = nanosecondAdjustment.value / scalar +
                    ((fractionalSeconds - newSeconds) * NANOSECONDS_PER_SECOND).toInt()
                create(newSeconds.seconds, newNanoseconds.nanoseconds)
            }
        }
    }

    /**
     * Break this duration down into components, assuming a 24 hour day
     */
    inline fun <T> toComponents(
        action: (
            days: LongDays,
            hours: IntHours,
            minutes: IntMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        val days = seconds.inWholeDays
        val hours = (seconds - days).inWholeHours
        val minutes = (seconds - days - hours).inWholeMinutes
        val seconds = seconds - days - hours - minutes
        return action(days, hours.toInt(), minutes.toInt(), seconds.toInt(), nanosecondAdjustment)
    }

    inline fun <T> toComponents(
        action: (
            hours: LongHours,
            minutes: IntMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        val hours = seconds.inWholeHours
        val minutes = (seconds - hours).inWholeMinutes
        val seconds = seconds - hours - minutes
        return action(hours, minutes.toInt(), seconds.toInt(), nanosecondAdjustment)
    }

    inline fun <T> toComponents(
        action: (
            minutes: LongMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        val minutes = seconds.inWholeMinutes
        val seconds = seconds - minutes
        return action(minutes, seconds.toInt(), nanosecondAdjustment)
    }

    inline fun <T> toComponents(
        action: (
            seconds: LongSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        return action(seconds, nanosecondAdjustment)
    }

    override fun equals(other: Any?): Boolean {
        return other === this ||
            (other is Duration &&
                other.seconds.value == seconds.value &&
                other.nanosecondAdjustment.value == nanosecondAdjustment.value)
    }

    override fun hashCode(): Int {
        return 31 * seconds.hashCode() + nanosecondAdjustment.hashCode()
    }

    override fun toString(): String {
        return if (isZero) {
            "PT0S"
        } else {
            buildString { appendDuration(this@Duration) }
        }
    }

    override fun compareTo(other: Duration): Int {
        val secondsDiff = seconds.value.compareTo(other.seconds.value)

        return if (secondsDiff != 0) {
            secondsDiff
        } else {
            nanosecondAdjustment.value - other.nanosecondAdjustment.value
        }
    }

    private fun plus(secondsToAdd: LongSeconds, nanosecondsToAdd: IntNanoseconds): Duration {
        return if (secondsToAdd.value == 0L && nanosecondsToAdd.value == 0) {
            this
        } else {
            durationOf(
                seconds plusExact secondsToAdd,
                nanosecondAdjustment plusWithOverflow nanosecondsToAdd
            )
        }
    }

    companion object {
        val MIN = Duration(Long.MIN_VALUE.seconds, (-999_999_999).nanoseconds)
        val MAX = Duration(Long.MAX_VALUE.seconds, 999_999_999.nanoseconds)
        val ZERO = Duration(0L.seconds)

        internal fun create(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds = 0.nanoseconds
        ): Duration {
            return if (seconds.value == 0L && nanosecondAdjustment.value == 0) {
                ZERO
            } else {
                Duration(seconds, nanosecondAdjustment)
            }
        }
    }
}

fun durationOf(seconds: IntSeconds, nanoseconds: IntNanoseconds) = durationOf(seconds.toLong(), nanoseconds.toLong())
fun durationOf(seconds: LongSeconds, nanoseconds: IntNanoseconds) = durationOf(seconds, nanoseconds.toLong())
fun durationOf(seconds: IntSeconds, nanoseconds: LongNanoseconds) = durationOf(seconds.toLong(), nanoseconds)

fun durationOf(seconds: LongSeconds, nanoseconds: LongNanoseconds): Duration {
    var adjustedSeconds = seconds plusExact nanoseconds.inWholeSeconds
    var newNanoOfSeconds = (nanoseconds % NANOSECONDS_PER_SECOND).toInt()

    if (newNanoOfSeconds.value < 0 && adjustedSeconds.value > 0) {
        adjustedSeconds -= 1L.seconds
        newNanoOfSeconds = (newNanoOfSeconds.value + NANOSECONDS_PER_SECOND).nanoseconds
    } else if (newNanoOfSeconds.value > 0 && adjustedSeconds.value < 0) {
        adjustedSeconds += 1L.seconds
        newNanoOfSeconds = (newNanoOfSeconds.value - NANOSECONDS_PER_SECOND).nanoseconds
    }

    return create(adjustedSeconds, newNanoOfSeconds)
}

/**
 * Create a [Duration] of 24-hour days
 */
fun durationOf(days: IntDays) = create(days.toLong().inSeconds)

/**
 * Create a [Duration] of 24-hour days
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(days: LongDays) = create(days.inSecondsExact())

/**
 * Create a [Duration] of hours
 */
fun durationOf(hours: IntHours) = create(hours.toLong().inSeconds)

/**
 * Create a [Duration] of hours
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(hours: LongHours) = create(hours.inSecondsExact())

/**
 * Create a [Duration] of minutes
 */
fun durationOf(minutes: IntMinutes) = create(minutes.toLong().inSeconds)

/**
 * Create a [Duration] of minutes
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(minutes: LongMinutes) = create(minutes.inSecondsExact())

/**
 * Create a [Duration] of seconds
 */
fun durationOf(seconds: IntSeconds) = create(seconds.toLong())

/**
 * Create a [Duration] of seconds
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(seconds: LongSeconds) = create(seconds)

fun durationOf(milliseconds: IntMilliseconds) = durationOf(milliseconds.toLong())

fun durationOf(milliseconds: LongMilliseconds): Duration {
    val seconds = milliseconds.inWholeSeconds
    val nanoOfSeconds = (milliseconds % MILLISECONDS_PER_SECOND).inNanoseconds.toInt()

    return create(seconds, nanoOfSeconds)
}

fun durationOf(microseconds: IntMicroseconds) = durationOf(microseconds.toLong())

fun durationOf(microseconds: LongMicroseconds): Duration {
    val seconds = microseconds.inWholeSeconds
    val nanoOfSeconds = (microseconds % MICROSECONDS_PER_SECOND).inNanoseconds.toInt()

    return create(seconds, nanoOfSeconds)
}

fun durationOf(nanoseconds: IntNanoseconds) = durationOf(nanoseconds.toLong())

fun durationOf(nanoseconds: LongNanoseconds): Duration {
    val seconds = nanoseconds.inWholeSeconds
    val nanoOfSeconds = (nanoseconds % NANOSECONDS_PER_SECOND).toInt()

    return create(seconds, nanoOfSeconds)
}

/**
 * Return the absolute value of a duration
 */
fun abs(duration: Duration) = duration.absoluteValue

fun LongDays.asDuration() = durationOf(this)
fun LongHours.asDuration() = durationOf(this)
fun LongMinutes.asDuration() = durationOf(this)
fun LongSeconds.asDuration() = durationOf(this)
fun LongMilliseconds.asDuration() = durationOf(this)
fun LongMicroseconds.asDuration() = durationOf(this)
fun LongNanoseconds.asDuration() = durationOf(this)

fun IntDays.asDuration() = durationOf(this)
fun IntHours.asDuration() = durationOf(this)
fun IntMinutes.asDuration() = durationOf(this)
fun IntSeconds.asDuration() = durationOf(this)
fun IntMilliseconds.asDuration() = durationOf(this)
fun IntMicroseconds.asDuration() = durationOf(this)
fun IntNanoseconds.asDuration() = durationOf(this)

internal fun StringBuilder.appendDuration(duration: Duration): StringBuilder {
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
        append("PT")

        if (!hours.isZero) {
            append(hours.value)
            append('H')
        }

        if (!minutes.isZero) {
            append(minutes.value)
            append('M')
        }

        if (!seconds.isZero || !nanoseconds.isZero) {
            if (seconds.value == 0 && nanoseconds.value < 0) {
                append('-')
            }

            append(seconds.value)

            if (!nanoseconds.isZero) {
                append('.')
                append(
                    abs(nanoseconds.value)
                        .toZeroPaddedString(9)
                        .dropLastWhile { it == '0' }
                )
            }

            append('S')
        }
    }
    return this
}

fun String.toDuration() = toDuration(Iso8601.DURATION_PARSER)

fun String.toDuration(parser: DateTimeParser): Duration {
    val result = parser.parse(this)
    return result.toDuration()
}

internal fun DateTimeParseResult.toDuration(): Duration {
    val days = (this[DateTimeField.PERIOD_OF_DAYS] ?: 0L).days
    val hours = (this[DateTimeField.DURATION_OF_HOURS] ?: 0L).hours
    val minutes = (this[DateTimeField.DURATION_OF_MINUTES] ?: 0L).minutes
    val seconds = (this[DateTimeField.DURATION_OF_SECONDS] ?: 0L).seconds
    val nanoseconds = (this[DateTimeField.NANOSECOND_OF_SECOND] ?: 0L).nanoseconds

    return durationOf(
        days + hours + minutes + seconds,
        nanoseconds
    )
}