package io.islandtime.measures

import io.islandtime.base.*
import io.islandtime.base.throwUnsupportedTemporalPropertyException
import io.islandtime.internal.*
import io.islandtime.internal.MICROSECONDS_PER_SECOND
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.internal.toZeroPaddedString
import io.islandtime.measures.Duration.Companion.create
import io.islandtime.measures.internal.plusWithOverflow
import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParser
import io.islandtime.parser.DateTimeParserSettings
import io.islandtime.parser.DateTimeParsers
import kotlin.math.absoluteValue

/**
 * A duration of time at nanosecond precision.
 *
 * For many applications, working with specific units like [IntHours] or [LongSeconds] is more efficient and plenty
 * adequate. However, when working with larger durations at sub-second precision, overflow is a very real possibility.
 * `Duration` is capable of representing fixed, nanosecond-precision durations that span the entire supported time
 * scale, making it more suitable for these use cases.
 */
class Duration private constructor(
    val seconds: LongSeconds,
    val nanosecondAdjustment: IntNanoseconds = 0.nanoseconds
) : Temporal,
    Comparable<Duration> {

    /**
     * Is this duration zero?
     */
    fun isZero(): Boolean = this == ZERO

    /**
     * Is this duration positive?
     */
    fun isPositive(): Boolean = seconds.value > 0L || nanosecondAdjustment.value > 0

    /**
     * Is this duration negative?
     */
    fun isNegative(): Boolean = seconds.value < 0L || nanosecondAdjustment.value < 0

    /**
     * Get the absolute value of this duration.
     */
    val absoluteValue: Duration
        get() = if (isNegative()) -this else this

    /**
     * Convert this duration into the number of 24-hour days represented by it.
     */
    inline val inDays: LongDays get() = seconds.inDays

    /**
     * Convert this duration into the number of whole hours represented by it.
     */
    inline val inHours: LongHours get() = seconds.inHours

    /**
     * Convert this duration into the number of whole minutes represented by it.
     */
    inline val inMinutes: LongMinutes get() = seconds.inMinutes

    /**
     * Return the number of whole seconds in this duration.
     * @see seconds
     */
    inline val inSeconds: LongSeconds get() = seconds

    /**
     * Convert this duration into the number of whole milliseconds represented by it.
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inMilliseconds
        get() = seconds.inMilliseconds + nanosecondAdjustment.inMilliseconds.toLongMilliseconds()

    /**
     * Convert this duration into the number of whole microseconds represented by it.
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inMicroseconds
        get() = seconds.inMicroseconds + nanosecondAdjustment.inMicroseconds.toLongMicroseconds()

    /**
     * Convert this duration into [LongNanoseconds].
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inNanoseconds
        get() = seconds.inNanoseconds + nanosecondAdjustment

    /**
     * Return this duration truncated to the number of 24-hour days.
     *
     * All unit components smaller than a day will be replaced with zero.
     */
    fun truncatedToDays() = create(seconds / SECONDS_PER_DAY * SECONDS_PER_DAY, 0.nanoseconds)

    /**
     * Return this duration truncated to the number of whole hours.
     *
     * All unit components smaller than an hour will be replaced with zero.
     */
    fun truncatedToHours() = create(seconds / SECONDS_PER_HOUR * SECONDS_PER_HOUR, 0.nanoseconds)

    /**
     * Return this duration truncated to the number of whole minutes.
     *
     * All unit components smaller than a minute will be replaced with zero.
     */
    fun truncatedToMinutes() = create(seconds / SECONDS_PER_MINUTE * SECONDS_PER_MINUTE, 0.nanoseconds)

    /**
     * Return this duration truncated to the number of whole seconds.
     *
     * All unit components smaller than a second will be replaced with zero.
     */
    fun truncatedToSeconds() = create(seconds, 0.nanoseconds)

    /**
     * Return this duration truncated to the number of whole milliseconds.
     *
     * All unit components smaller than a millisecond will be replaced with zero.
     */
    fun truncatedToMilliseconds() = create(
        seconds,
        (nanosecondAdjustment.value / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND).nanoseconds
    )

    /**
     * Return this duration truncated to the number of whole microseconds.
     *
     * All unit components smaller than a microsecond will be replaced with zero.
     */
    fun truncatedToMicroseconds() = create(
        seconds,
        (nanosecondAdjustment.value / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND).nanoseconds
    )

    operator fun unaryMinus() = create(-seconds, nanosecondAdjustment.negateUnchecked())

    operator fun plus(other: Duration): Duration {
        return when {
            other.isZero() -> this
            this.isZero() -> other
            else -> plus(other.seconds, other.nanosecondAdjustment)
        }
    }

    operator fun plus(days: IntDays) = plus(days.toLongDays().inSecondsUnchecked, 0.nanoseconds)
    operator fun plus(days: LongDays) = plus(days.inSeconds, 0.nanoseconds)

    operator fun plus(hours: IntHours) = plus(hours.toLongHours().inSecondsUnchecked, 0.nanoseconds)
    operator fun plus(hours: LongHours) = plus(hours.inSeconds, 0.nanoseconds)

    operator fun plus(minutes: IntMinutes) = plus(minutes.toLongMinutes().inSecondsUnchecked, 0.nanoseconds)
    operator fun plus(minutes: LongMinutes) = plus(minutes.inSeconds, 0.nanoseconds)

    operator fun plus(seconds: IntSeconds) = plus(seconds.toLongSeconds(), 0.nanoseconds)
    operator fun plus(seconds: LongSeconds) = plus(seconds, 0.nanoseconds)

    operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.inNanoseconds)

    operator fun plus(milliseconds: LongMilliseconds): Duration {
        return plus(
            milliseconds.inSeconds,
            ((milliseconds.value % MILLISECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MILLISECOND).nanoseconds
        )
    }

    operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.inNanoseconds)

    operator fun plus(microseconds: LongMicroseconds): Duration {
        return plus(
            microseconds.inSeconds,
            ((microseconds.value % MICROSECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MICROSECOND).nanoseconds
        )
    }

    operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds())

    operator fun plus(nanoseconds: LongNanoseconds): Duration {
        return plus(
            nanoseconds.inSeconds,
            (nanoseconds % NANOSECONDS_PER_SECOND).toIntNanosecondsUnchecked()
        )
    }

    operator fun minus(other: Duration): Duration {
        return if (other.seconds.value == Long.MIN_VALUE) {
            plus(
                Long.MAX_VALUE.seconds,
                other.nanosecondAdjustment.negateUnchecked() plusWithOverflow 1.seconds
            )
        } else {
            plus(other.seconds.negateUnchecked(), other.nanosecondAdjustment.negateUnchecked())
        }
    }

    operator fun minus(days: IntDays) = plus(days.toLongDays().negateUnchecked())

    operator fun minus(days: LongDays): Duration {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(days.negateUnchecked())
        }
    }

    operator fun minus(hours: IntHours) = plus(hours.toLongHours().negateUnchecked())

    operator fun minus(hours: LongHours): Duration {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(hours.negateUnchecked())
        }
    }

    operator fun minus(minutes: IntMinutes) = plus(minutes.toLongMinutes().negateUnchecked())

    operator fun minus(minutes: LongMinutes): Duration {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(minutes.negateUnchecked())
        }
    }

    operator fun minus(seconds: IntSeconds) = plus(seconds.toLongSeconds().negateUnchecked())

    operator fun minus(seconds: LongSeconds): Duration {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(seconds.negateUnchecked())
        }
    }

    operator fun minus(milliseconds: IntMilliseconds) = plus(milliseconds.toLongMilliseconds().negateUnchecked())

    operator fun minus(milliseconds: LongMilliseconds): Duration {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(milliseconds.negateUnchecked())
        }
    }

    operator fun minus(microseconds: IntMicroseconds) = plus(microseconds.toLongMicroseconds().negateUnchecked())

    operator fun minus(microseconds: LongMicroseconds): Duration {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(microseconds.negateUnchecked())
        }
    }

    operator fun minus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds().negateUnchecked())

    operator fun minus(nanoseconds: LongNanoseconds): Duration {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(nanoseconds.negateUnchecked())
        }
    }

    /**
     * Multiply by a scalar value.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun times(scalar: Int): Duration {
        return when (scalar) {
            0 -> ZERO
            1 -> this
            else -> {
                var newSeconds = seconds * scalar
                var newNanoseconds = nanosecondAdjustment * scalar
                newSeconds += newNanoseconds.inSeconds
                newNanoseconds %= NANOSECONDS_PER_SECOND
                create(newSeconds, newNanoseconds.toIntNanosecondsUnchecked())
            }
        }
    }

    /**
     * Divide by a scalar value.
     * @throws ArithmeticException if division by zero or overflow occurs
     */
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
     * Break this duration down into individual unit components, assuming a 24-hour day length.
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
        val days = seconds.inDays
        val hours = (seconds - days).inHours
        val minutes = (seconds - days - hours).inMinutes
        val seconds = seconds - days - hours - minutes

        return action(
            days,
            hours.toIntHoursUnchecked(),
            minutes.toIntMinutesUnchecked(),
            seconds.toIntSecondsUnchecked(),
            nanosecondAdjustment
        )
    }

    /**
     * Break this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            hours: LongHours,
            minutes: IntMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        val hours = seconds.inHours
        val minutes = (seconds - hours).inMinutes
        val seconds = seconds - hours - minutes

        return action(
            hours,
            minutes.toIntMinutesUnchecked(),
            seconds.toIntSecondsUnchecked(),
            nanosecondAdjustment
        )
    }

    /**
     * Break this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            minutes: LongMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        val minutes = seconds.inMinutes
        val seconds = seconds - minutes
        return action(minutes, seconds.toIntSecondsUnchecked(), nanosecondAdjustment)
    }

    /**
     * Break this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            seconds: LongSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        return action(seconds, nanosecondAdjustment)
    }

    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            DurationProperty.IsZero,
            DurationProperty.Seconds,
            DurationProperty.Nanoseconds -> true
            else -> false
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            DurationProperty.IsZero -> isZero()
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            DurationProperty.Seconds -> seconds.value
            DurationProperty.Nanoseconds -> nanosecondAdjustment.value.toLong()
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    @kotlin.time.ExperimentalTime
    fun toKotlinDuration(): kotlin.time.Duration {
        return seconds.toKotlinDuration() + nanosecondAdjustment.toKotlinDuration()
    }

    override fun equals(other: Any?): Boolean {
        return other === this ||
            (other is Duration &&
                other.seconds == seconds &&
                other.nanosecondAdjustment == nanosecondAdjustment)
    }

    override fun hashCode(): Int {
        return 31 * seconds.hashCode() + nanosecondAdjustment.hashCode()
    }

    override fun toString(): String {
        return if (isZero()) {
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
                seconds + secondsToAdd,
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

/**
 * Create a [Duration].
 *
 * @param seconds the number of seconds in the duration
 * @param nanoseconds the number of additional nanoseconds to be applied on top of [seconds]
 */
fun durationOf(
    seconds: IntSeconds,
    nanoseconds: IntNanoseconds
) = durationOf(seconds.toLongSeconds(), nanoseconds.toLongNanoseconds())

/**
 * Create a [Duration].
 *
 * @param seconds the number of seconds in the duration
 * @param nanoseconds the number of additional nanoseconds to be applied on top of [seconds]
 */
fun durationOf(
    seconds: LongSeconds,
    nanoseconds: IntNanoseconds
) = durationOf(seconds, nanoseconds.toLongNanoseconds())

/**
 * Create a [Duration].
 *
 * @param seconds the number of seconds in the duration
 * @param nanoseconds the number of additional nanoseconds to be applied on top of [seconds]
 */
fun durationOf(
    seconds: IntSeconds,
    nanoseconds: LongNanoseconds
) = durationOf(seconds.toLongSeconds(), nanoseconds)

/**
 * Create a [Duration].
 *
 * @param seconds the number of seconds in the duration
 * @param nanoseconds the number of additional nanoseconds to be applied on top of [seconds]
 */
fun durationOf(seconds: LongSeconds, nanoseconds: LongNanoseconds): Duration {
    var adjustedSeconds = seconds + nanoseconds.inSeconds
    var newNanoOfSeconds = (nanoseconds % NANOSECONDS_PER_SECOND).toIntNanosecondsUnchecked()

    if (newNanoOfSeconds.value < 0 && adjustedSeconds.value > 0) {
        adjustedSeconds = (adjustedSeconds.value - 1L).seconds
        newNanoOfSeconds = (newNanoOfSeconds.value + NANOSECONDS_PER_SECOND).nanoseconds
    } else if (newNanoOfSeconds.value > 0 && adjustedSeconds.value < 0) {
        adjustedSeconds = (adjustedSeconds.value + 1L).seconds
        newNanoOfSeconds = (newNanoOfSeconds.value - NANOSECONDS_PER_SECOND).nanoseconds
    }

    return create(adjustedSeconds, newNanoOfSeconds)
}

/**
 * Create a [Duration] of 24-hour days.
 */
fun durationOf(days: IntDays) = create(days.toLongDays().inSecondsUnchecked)

/**
 * Create a [Duration] of 24-hour days.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(days: LongDays) = create(days.inSeconds)

/**
 * Create a [Duration] of hours.
 */
fun durationOf(hours: IntHours) = create(hours.toLongHours().inSecondsUnchecked)

/**
 * Create a [Duration] of hours.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(hours: LongHours) = create(hours.inSeconds)

/**
 * Create a [Duration] of minutes.
 */
fun durationOf(minutes: IntMinutes) = create(minutes.toLongMinutes().inSecondsUnchecked)

/**
 * Create a [Duration] of minutes.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(minutes: LongMinutes) = create(minutes.inSeconds)

/**
 * Create a [Duration] of seconds.
 */
fun durationOf(seconds: IntSeconds) = create(seconds.toLongSeconds())

/**
 * Create a [Duration] of seconds.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(seconds: LongSeconds) = create(seconds)

/**
 * Create a [Duration] of milliseconds.
 */
fun durationOf(milliseconds: IntMilliseconds) = durationOf(milliseconds.toLongMilliseconds())

/**
 * Create a [Duration] of milliseconds.
 */
fun durationOf(milliseconds: LongMilliseconds): Duration {
    val seconds = milliseconds.inSeconds
    val nanoOfSeconds = (milliseconds % MILLISECONDS_PER_SECOND).inNanoseconds.toIntNanosecondsUnchecked()

    return create(seconds, nanoOfSeconds)
}

/**
 * Create a [Duration] of microseconds.
 */
fun durationOf(microseconds: IntMicroseconds) = durationOf(microseconds.toLongMicroseconds())

/**
 * Create a [Duration] of microseconds.
 */
fun durationOf(microseconds: LongMicroseconds): Duration {
    val seconds = microseconds.inSeconds
    val nanoOfSeconds = (microseconds % MICROSECONDS_PER_SECOND).inNanoseconds.toIntNanosecondsUnchecked()

    return create(seconds, nanoOfSeconds)
}

/**
 * Create a [Duration] of nanoseconds.
 */
fun durationOf(nanoseconds: IntNanoseconds) = durationOf(nanoseconds.toLongNanoseconds())

/**
 * Create a [Duration] of nanoseconds.
 */
fun durationOf(nanoseconds: LongNanoseconds): Duration {
    val seconds = nanoseconds.inSeconds
    val nanoOfSeconds = (nanoseconds % NANOSECONDS_PER_SECOND).toIntNanosecondsUnchecked()

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

@kotlin.time.ExperimentalTime
fun kotlin.time.Duration.toIslandDuration(): Duration {
    return toComponents { seconds, nanoseconds ->
        durationOf(seconds.seconds, nanoseconds.nanoseconds)
    }
}

internal fun StringBuilder.appendDuration(duration: Duration): StringBuilder {
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
        append("PT")

        if (!hours.isZero()) {
            append(hours.value)
            append('H')
        }

        if (!minutes.isZero()) {
            append(minutes.value)
            append('M')
        }

        if (!seconds.isZero() || !nanoseconds.isZero()) {
            if (seconds.value == 0 && nanoseconds.value < 0) {
                append('-')
            }

            append(seconds.value)

            if (!nanoseconds.isZero()) {
                append('.')
                append(
                    nanoseconds.value.absoluteValue
                        .toZeroPaddedString(9)
                        .dropLastWhile { it == '0' }
                )
            }

            append('S')
        }
    }
    return this
}

/**
 * Multiply by a duration.
 * @throws ArithmeticException if overflow occurs
 */
operator fun Int.times(duration: Duration) = duration * this

/**
 * Convert an ISO-8601 duration string into a [Duration].
 */
fun String.toDuration() = toDuration(DateTimeParsers.Iso.DURATION)

/**
 * Convert a string to a [Duration] using [parser].
 */
fun String.toDuration(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): Duration {
    val result = parser.parse(this, settings)
    return result.toDuration()
}

internal fun DateTimeParseResult.toDuration(): Duration {
    // TODO: Make sure we have at least one component
    val days = (this[DurationProperty.Days] ?: 0L).days
    val hours = (this[DurationProperty.Hours] ?: 0L).hours
    val minutes = (this[DurationProperty.Minutes] ?: 0L).minutes
    val seconds = (this[DurationProperty.Seconds] ?: 0L).seconds
    val nanoseconds = (this[DurationProperty.Nanoseconds] ?: 0L).nanoseconds

    return durationOf(
        days + hours + minutes + seconds,
        nanoseconds
    )
}