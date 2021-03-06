package io.islandtime.measures

import io.islandtime.base.DateTimeField
import io.islandtime.internal.*
import io.islandtime.measures.Duration.Companion.create
import io.islandtime.measures.TimeUnit.*
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
) : Comparable<Duration> {

    /**
     * Checks if this duration is zero.
     */
    fun isZero(): Boolean = this == ZERO

    /**
     * Checks if this duration is positive.
     */
    fun isPositive(): Boolean = seconds.value > 0L || nanosecondAdjustment.value > 0

    /**
     * Checks if this duration is negative.
     */
    fun isNegative(): Boolean = seconds.value < 0L || nanosecondAdjustment.value < 0

    /**
     * The absolute value of this duration.
     */
    val absoluteValue: Duration
        get() = if (isNegative()) -this else this

    /**
     * Converts this duration into the number of 24-hour days represented by it.
     */
    inline val inDays: LongDays get() = seconds.inDays

    /**
     * Converts this duration into the number of whole hours represented by it.
     */
    inline val inHours: LongHours get() = seconds.inHours

    /**
     * Converts this duration into the number of whole minutes represented by it.
     */
    inline val inMinutes: LongMinutes get() = seconds.inMinutes

    /**
     * Returns the number of whole seconds in this duration.
     * @see seconds
     */
    inline val inSeconds: LongSeconds get() = seconds

    /**
     * Converts this duration into the number of whole milliseconds represented by it.
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inMilliseconds: LongMilliseconds
        get() = seconds.inMilliseconds + nanosecondAdjustment.inMilliseconds.toLongMilliseconds()

    /**
     * Converts this duration into the number of whole microseconds represented by it.
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inMicroseconds: LongMicroseconds
        get() = seconds.inMicroseconds + nanosecondAdjustment.inMicroseconds.toLongMicroseconds()

    /**
     * Converts this duration into [LongNanoseconds].
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inNanoseconds: LongNanoseconds
        get() = seconds.inNanoseconds + nanosecondAdjustment

    /**
     * Returns this duration, rounded down to match the precision of a given [unit].
     */
    fun truncatedTo(unit: TimeUnit): Duration {
        return when (unit) {
            DAYS -> create(seconds / SECONDS_PER_DAY * SECONDS_PER_DAY, 0.nanoseconds)
            HOURS -> create(seconds / SECONDS_PER_HOUR * SECONDS_PER_HOUR, 0.nanoseconds)
            MINUTES -> create(seconds / SECONDS_PER_MINUTE * SECONDS_PER_MINUTE, 0.nanoseconds)
            SECONDS -> create(seconds, 0.nanoseconds)
            MILLISECONDS -> create(
                seconds,
                (nanosecondAdjustment.value / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND).nanoseconds
            )
            MICROSECONDS -> create(
                seconds,
                (nanosecondAdjustment.value / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND).nanoseconds
            )
            NANOSECONDS -> this
        }
    }

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(DAYS)", "io.islandtime.measures.TimeUnit.DAYS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToDays() = truncatedTo(DAYS)

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToHours() = truncatedTo(HOURS)

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
        DeprecationLevel.ERROR
    )
    fun truncatedToMinutes() = truncatedTo(MINUTES)

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToSeconds() = truncatedTo(SECONDS)

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

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
     * Multiples this duration by a scalar value.
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
     * Divides this duration by a scalar value.
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
     * Breaks this duration down into individual unit components, assuming a 24-hour day length.
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
        return seconds.toComponents { days, hours, minutes, seconds ->
            action(days, hours, minutes, seconds, nanosecondAdjustment)
        }
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            hours: LongHours,
            minutes: IntMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        return seconds.toComponents { hours, minutes, seconds ->
            action(hours, minutes, seconds, nanosecondAdjustment)
        }
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            minutes: LongMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        return seconds.toComponents { minutes, seconds ->
            action(minutes, seconds, nanosecondAdjustment)
        }
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            seconds: LongSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        return action(seconds, nanosecondAdjustment)
    }

    /**
     * Converts this duration to a [kotlin.time.Duration]. Since Kotlin's `Duration` type is based on a floating-point
     * number, precision may be lost.
     */
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
        /**
         * The minimum supported [Duration].
         */
        val MIN = Duration(Long.MIN_VALUE.seconds, (-999_999_999).nanoseconds)

        /**
         * The maximum supported [Duration].
         */
        val MAX = Duration(Long.MAX_VALUE.seconds, 999_999_999.nanoseconds)

        /**
         * A [Duration] of zero length.
         */
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
 * Creates a [Duration].
 *
 * @param seconds the number of seconds in the duration
 * @param nanoseconds the number of additional nanoseconds to be applied on top of [seconds]
 */
fun durationOf(
    seconds: IntSeconds,
    nanoseconds: IntNanoseconds
) = durationOf(seconds.toLongSeconds(), nanoseconds.toLongNanoseconds())

/**
 * Creates a [Duration].
 *
 * @param seconds the number of seconds in the duration
 * @param nanoseconds the number of additional nanoseconds to be applied on top of [seconds]
 */
fun durationOf(
    seconds: LongSeconds,
    nanoseconds: IntNanoseconds
) = durationOf(seconds, nanoseconds.toLongNanoseconds())

/**
 * Creates a [Duration].
 *
 * @param seconds the number of seconds in the duration
 * @param nanoseconds the number of additional nanoseconds to be applied on top of [seconds]
 */
fun durationOf(
    seconds: IntSeconds,
    nanoseconds: LongNanoseconds
) = durationOf(seconds.toLongSeconds(), nanoseconds)

/**
 * Creates a [Duration].
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
 * Creates a [Duration] of 24-hour days.
 */
fun durationOf(days: IntDays) = create(days.toLongDays().inSecondsUnchecked)

/**
 * Creates a [Duration] of 24-hour days.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(days: LongDays) = create(days.inSeconds)

/**
 * Creates a [Duration] of hours.
 */
fun durationOf(hours: IntHours) = create(hours.toLongHours().inSecondsUnchecked)

/**
 * Creates a [Duration] of hours.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(hours: LongHours) = create(hours.inSeconds)

/**
 * Creates a [Duration] of minutes.
 */
fun durationOf(minutes: IntMinutes) = create(minutes.toLongMinutes().inSecondsUnchecked)

/**
 * Creates a [Duration] of minutes.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(minutes: LongMinutes) = create(minutes.inSeconds)

/**
 * Creates a [Duration] of seconds.
 */
fun durationOf(seconds: IntSeconds) = create(seconds.toLongSeconds())

/**
 * Creates a [Duration] of seconds.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(seconds: LongSeconds) = create(seconds)

/**
 * Creates a [Duration] of milliseconds.
 */
fun durationOf(milliseconds: IntMilliseconds) = durationOf(milliseconds.toLongMilliseconds())

/**
 * Creates a [Duration] of milliseconds.
 */
fun durationOf(milliseconds: LongMilliseconds): Duration {
    val seconds = milliseconds.inSeconds
    val nanoOfSeconds = (milliseconds % MILLISECONDS_PER_SECOND).inNanoseconds.toIntNanosecondsUnchecked()

    return create(seconds, nanoOfSeconds)
}

/**
 * Creates a [Duration] of microseconds.
 */
fun durationOf(microseconds: IntMicroseconds) = durationOf(microseconds.toLongMicroseconds())

/**
 * Creates a [Duration] of microseconds.
 */
fun durationOf(microseconds: LongMicroseconds): Duration {
    val seconds = microseconds.inSeconds
    val nanoOfSeconds = (microseconds % MICROSECONDS_PER_SECOND).inNanoseconds.toIntNanosecondsUnchecked()

    return create(seconds, nanoOfSeconds)
}

/**
 * Creates a [Duration] of nanoseconds.
 */
fun durationOf(nanoseconds: IntNanoseconds) = durationOf(nanoseconds.toLongNanoseconds())

/**
 * Creates a [Duration] of nanoseconds.
 */
fun durationOf(nanoseconds: LongNanoseconds): Duration {
    val seconds = nanoseconds.inSeconds
    val nanoOfSeconds = (nanoseconds % NANOSECONDS_PER_SECOND).toIntNanosecondsUnchecked()

    return create(seconds, nanoOfSeconds)
}

/**
 * Returns the absolute value of [duration].
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

/**
 * Converts this duration to an equivalent Island Time [Duration].
 */
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
 * Multiplies this value by a duration.
 * @throws ArithmeticException if overflow occurs
 */
operator fun Int.times(duration: Duration) = duration * this

/**
 * Converts an ISO-8601 duration string to a [Duration].
 */
fun String.toDuration() = toDuration(DateTimeParsers.Iso.DURATION)

/**
 * Converts a string to a [Duration] using [parser].
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
    val days = (fields[DateTimeField.PERIOD_OF_DAYS] ?: 0L).days
    val hours = (fields[DateTimeField.DURATION_OF_HOURS] ?: 0L).hours
    val minutes = (fields[DateTimeField.DURATION_OF_MINUTES] ?: 0L).minutes
    val seconds = (fields[DateTimeField.DURATION_OF_SECONDS] ?: 0L).seconds
    val nanoseconds = (fields[DateTimeField.NANOSECOND_OF_SECOND] ?: 0L).nanoseconds

    return durationOf(
        days + hours + minutes + seconds,
        nanoseconds
    )
}
