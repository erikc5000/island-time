@file:OptIn(ExperimentalContracts::class)

package io.islandtime.measures

import dev.erikchristensen.javamath2kmp.negateExact
import io.islandtime.base.DateTimeField
import io.islandtime.internal.*
import io.islandtime.measures.Duration.Companion.create
import io.islandtime.measures.TimeUnit.*
import io.islandtime.measures.internal.plusUnchecked
import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParser
import io.islandtime.parser.DateTimeParserSettings
import io.islandtime.parser.DateTimeParsers
import io.islandtime.serialization.DurationSerializer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.absoluteValue
import kotlinx.serialization.Serializable

/**
 * A duration of time at nanosecond precision.
 *
 * For many applications, working with specific units like [Hours] or [Seconds] is more efficient and plenty adequate.
 * However, when working with larger durations at sub-second precision, overflow is a very real possibility. `Duration`
 * is capable of representing fixed, nanosecond-precision durations that span the entire supported time scale, making it
 * more suitable for these use cases.
 */
@Serializable(with = DurationSerializer::class)
class Duration private constructor(
    @PublishedApi internal val secondValue: Long,
    @PublishedApi internal val nanosecondValue: Int = 0
) : Comparable<Duration> {

    val seconds: Seconds get() = secondValue.seconds
    val nanosecondAdjustment: Nanoseconds get() = nanosecondValue.nanoseconds

    /**
     * Checks if this duration is zero.
     */
    fun isZero(): Boolean = this == ZERO

    /**
     * Checks if this duration is positive.
     */
    fun isPositive(): Boolean = secondValue > 0L || nanosecondValue > 0

    /**
     * Checks if this duration is negative.
     */
    fun isNegative(): Boolean = secondValue < 0L || nanosecondValue < 0

    /**
     * The absolute value of this duration.
     */
    val absoluteValue: Duration
        get() = if (isNegative()) -this else this

    /**
     * Converts this duration into the number of 24-hour days represented by it.
     */
    inline val inDays: Days get() = seconds.inWholeDays

    /**
     * Converts this duration into the number of whole hours represented by it.
     */
    inline val inHours: Hours get() = seconds.inWholeHours

    /**
     * Converts this duration into the number of whole minutes represented by it.
     */
    inline val inMinutes: Minutes get() = seconds.inWholeMinutes

    /**
     * Returns the number of whole seconds in this duration.
     * @see seconds
     */
    inline val inSeconds: Seconds get() = seconds

    /**
     * Converts this duration into the number of whole milliseconds represented by it.
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inMilliseconds: Milliseconds
        get() = seconds + nanosecondAdjustment.inWholeMilliseconds

    /**
     * Converts this duration into the number of whole microseconds represented by it.
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inMicroseconds: Microseconds
        get() = seconds + nanosecondAdjustment.inWholeMicroseconds

    /**
     * Converts this duration into [Nanoseconds].
     * @throws ArithmeticException if the duration cannot be represented without overflow
     */
    val inNanoseconds: Nanoseconds
        get() = seconds + nanosecondAdjustment

    @PublishedApi
    internal val hoursComponent: Int
        get() = ((secondValue % SECONDS_PER_DAY) / SECONDS_PER_HOUR).toInt()

    @PublishedApi
    internal val minutesComponent: Int
        get() = ((secondValue % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE).toInt()

    @PublishedApi
    internal val secondsComponent: Int
        get() = (secondValue % SECONDS_PER_MINUTE).toInt()

    /**
     * Returns this duration, rounded down to match the precision of a given [unit].
     */
    fun truncatedTo(unit: TimeUnit): Duration {
        return when (unit) {
            DAYS -> create(secondValue / SECONDS_PER_DAY * SECONDS_PER_DAY, 0)
            HOURS -> create(secondValue / SECONDS_PER_HOUR * SECONDS_PER_HOUR, 0)
            MINUTES -> create(secondValue / SECONDS_PER_MINUTE * SECONDS_PER_MINUTE, 0)
            SECONDS -> create(secondValue, 0)
            MILLISECONDS -> create(
                secondValue,
                nanosecondValue / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND
            )
            MICROSECONDS -> create(
                secondValue,
                nanosecondValue / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND
            )
            NANOSECONDS -> this
        }
    }

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(DAYS)", "io.islandtime.measures.TimeUnit.DAYS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToDays(): Duration = deprecatedToError()

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToHours(): Duration = deprecatedToError()

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
        DeprecationLevel.ERROR
    )
    fun truncatedToMinutes(): Duration = deprecatedToError()

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToSeconds(): Duration = deprecatedToError()

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToMilliseconds(): Duration = deprecatedToError()

    @Deprecated(
        "Use truncatedTo().",
        ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
        DeprecationLevel.ERROR
    )
    fun truncatedToMicroseconds(): Duration = deprecatedToError()

    operator fun unaryMinus(): Duration = create(secondValue.negateExact(), -nanosecondValue)

    operator fun plus(other: Duration): Duration {
        return when {
            other.isZero() -> this
            this.isZero() -> other
            else -> plus(other.seconds, other.nanosecondAdjustment)
        }
    }

    operator fun plus(days: Days): Duration = plus(days.inSeconds, 0.nanoseconds)
    operator fun plus(hours: Hours): Duration = plus(hours.inSeconds, 0.nanoseconds)
    operator fun plus(minutes: Minutes): Duration = plus(minutes.inSeconds, 0.nanoseconds)
    operator fun plus(seconds: Seconds): Duration = plus(seconds, 0.nanoseconds)

    operator fun plus(milliseconds: Milliseconds): Duration {
        return plus(
            milliseconds.inWholeSeconds,
            ((milliseconds.value % MILLISECONDS_PER_SECOND) * NANOSECONDS_PER_MILLISECOND).nanoseconds
        )
    }

    operator fun plus(microseconds: Microseconds): Duration {
        return plus(
            microseconds.inWholeSeconds,
            ((microseconds.value % MICROSECONDS_PER_SECOND) * NANOSECONDS_PER_MICROSECOND).nanoseconds
        )
    }

    operator fun plus(nanoseconds: Nanoseconds): Duration {
        return plus(
            nanoseconds.inWholeSeconds,
            nanoseconds % NANOSECONDS_PER_SECOND
        )
    }

    operator fun minus(other: Duration): Duration {
        return if (other.secondValue == Long.MIN_VALUE) {
            plus(
                Long.MAX_VALUE.seconds,
                other.nanosecondAdjustment.negateUnchecked() plusUnchecked 1.seconds
            )
        } else {
            plus(other.seconds.negateUnchecked(), other.nanosecondAdjustment.negateUnchecked())
        }
    }

    operator fun minus(days: Days): Duration {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(days.negateUnchecked())
        }
    }

    operator fun minus(hours: Hours): Duration {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(hours.negateUnchecked())
        }
    }

    operator fun minus(minutes: Minutes): Duration {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(minutes.negateUnchecked())
        }
    }

    operator fun minus(seconds: Seconds): Duration {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(seconds.negateUnchecked())
        }
    }

    operator fun minus(milliseconds: Milliseconds): Duration {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(milliseconds.negateUnchecked())
        }
    }

    operator fun minus(microseconds: Microseconds): Duration {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(microseconds.negateUnchecked())
        }
    }

    operator fun minus(nanoseconds: Nanoseconds): Duration {
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
                newSeconds += newNanoseconds.inWholeSeconds
                newNanoseconds %= NANOSECONDS_PER_SECOND
                create(newSeconds.value, newNanoseconds.toIntUnchecked())
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
                val fractionalSeconds = secondValue.toDouble() / scalar
                val newSecondValue = fractionalSeconds.toLong()
                val newNanosecondValue = nanosecondValue / scalar +
                    ((fractionalSeconds - newSecondValue) * NANOSECONDS_PER_SECOND).toInt()
                create(newSecondValue, newNanosecondValue)
            }
        }
    }

    /**
     * Breaks this duration down into individual unit components, assuming a 24-hour day length.
     */
    inline fun <T> toComponentValues(
        action: (
            days: Long,
            hours: Int,
            minutes: Int,
            seconds: Int,
            nanoseconds: Int
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return action(seconds.inWholeDays.value, hoursComponent, minutesComponent, secondsComponent, nanosecondValue)
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponentValues(
        action: (
            hours: Long,
            minutes: Int,
            seconds: Int,
            nanoseconds: Int
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return action(seconds.inWholeHours.value, minutesComponent, secondsComponent, nanosecondValue)
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponentValues(
        action: (
            minutes: Long,
            seconds: Int,
            nanoseconds: Int
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return action(seconds.inWholeMinutes.value, secondsComponent, nanosecondValue)
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponentValues(
        action: (
            seconds: Long,
            nanoseconds: Int
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return action(secondValue, nanosecondValue)
    }

    /**
     * Breaks this duration down into individual unit components, assuming a 24-hour day length.
     */
    inline fun <T> toComponents(
        action: (
            days: Days,
            hours: Hours,
            minutes: Minutes,
            seconds: Seconds,
            nanoseconds: Nanoseconds
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }

        return seconds.toComponents { days, hours, minutes, seconds ->
            action(days, hours, minutes, seconds, nanosecondAdjustment)
        }
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            hours: Hours,
            minutes: Minutes,
            seconds: Seconds,
            nanoseconds: Nanoseconds
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }

        return seconds.toComponents { hours, minutes, seconds ->
            action(hours, minutes, seconds, nanosecondAdjustment)
        }
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            minutes: Minutes,
            seconds: Seconds,
            nanoseconds: Nanoseconds
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }

        return seconds.toComponents { minutes, seconds ->
            action(minutes, seconds, nanosecondAdjustment)
        }
    }

    /**
     * Breaks this duration down into individual unit components.
     */
    inline fun <T> toComponents(
        action: (
            seconds: Seconds,
            nanoseconds: Nanoseconds
        ) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return action(seconds, nanosecondAdjustment)
    }

    /**
     * Converts this duration to a [kotlin.time.Duration]. Since Kotlin's `Duration` type is based on a floating-point
     * number, precision may be lost.
     */
    fun toKotlinDuration(): kotlin.time.Duration {
        return seconds.toKotlinDuration() + nanosecondAdjustment.toKotlinDuration()
    }

    override fun equals(other: Any?): Boolean {
        return other === this ||
            (other is Duration &&
                other.secondValue == secondValue &&
                other.nanosecondValue == nanosecondValue)
    }

    override fun hashCode(): Int {
        return 31 * secondValue.hashCode() + nanosecondValue
    }

    override fun toString(): String {
        return if (isZero()) {
            "PT0S"
        } else {
            buildString { appendDuration(this@Duration) }
        }
    }

    override fun compareTo(other: Duration): Int {
        val secondsDiff = secondValue.compareTo(other.secondValue)

        return if (secondsDiff != 0) {
            secondsDiff
        } else {
            nanosecondValue - other.nanosecondValue
        }
    }

    private fun plus(secondsToAdd: Seconds, nanosecondsToAdd: Nanoseconds): Duration {
        return if (secondsToAdd.value == 0L && nanosecondsToAdd.value == 0L) {
            this
        } else {
            durationOf(
                seconds + secondsToAdd,
                (nanosecondValue + nanosecondsToAdd.value).nanoseconds
            )
        }
    }

    companion object {
        /**
         * The minimum supported [Duration].
         */
        val MIN: Duration = Duration(Long.MIN_VALUE, -999_999_999)

        /**
         * The maximum supported [Duration].
         */
        val MAX: Duration = Duration(Long.MAX_VALUE, 999_999_999)

        /**
         * A [Duration] of zero length.
         */
        val ZERO: Duration = Duration(0L)

        internal fun create(secondValue: Long, nanosecondValue: Int = 0): Duration {
            return if (secondValue == 0L && nanosecondValue == 0) {
                ZERO
            } else {
                Duration(secondValue, nanosecondValue)
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
fun durationOf(seconds: Seconds, nanoseconds: Nanoseconds): Duration {
    var secondValue = (seconds + nanoseconds.inWholeSeconds).value
    var nanosecondValue = (nanoseconds % NANOSECONDS_PER_SECOND).toIntUnchecked()

    if (nanosecondValue < 0 && secondValue > 0) {
        secondValue -= 1L
        nanosecondValue += NANOSECONDS_PER_SECOND
    } else if (nanosecondValue > 0 && secondValue < 0) {
        secondValue += 1L
        nanosecondValue -= NANOSECONDS_PER_SECOND
    }

    return create(secondValue, nanosecondValue)
}

/**
 * Creates a [Duration] of 24-hour days.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(days: Days): Duration = create(days.inSeconds.value)

/**
 * Creates a [Duration] of hours.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(hours: Hours): Duration = create(hours.inSeconds.value)

/**
 * Creates a [Duration] of minutes.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(minutes: Minutes): Duration = create(minutes.inSeconds.value)

/**
 * Creates a [Duration] of seconds.
 * @throws ArithmeticException if overflow occurs
 */
fun durationOf(seconds: Seconds): Duration = create(seconds.value)

/**
 * Creates a [Duration] of milliseconds.
 */
fun durationOf(milliseconds: Milliseconds): Duration {
    val secondValue = milliseconds.inWholeSeconds.value
    val nanosecondValue = (milliseconds % MILLISECONDS_PER_SECOND).inNanosecondsUnchecked.toIntUnchecked()
    return create(secondValue, nanosecondValue)
}

/**
 * Creates a [Duration] of microseconds.
 */
fun durationOf(microseconds: Microseconds): Duration {
    val secondValue = microseconds.inWholeSeconds.value
    val nanosecondValue = (microseconds % MICROSECONDS_PER_SECOND).inNanosecondsUnchecked.toIntUnchecked()
    return create(secondValue, nanosecondValue)
}

/**
 * Creates a [Duration] of nanoseconds.
 */
fun durationOf(nanoseconds: Nanoseconds): Duration {
    val secondValue = nanoseconds.inWholeSeconds.value
    val nanosecondValue = (nanoseconds % NANOSECONDS_PER_SECOND).toIntUnchecked()
    return create(secondValue, nanosecondValue)
}

/**
 * Returns the absolute value of a [duration].
 */
fun abs(duration: Duration) = duration.absoluteValue

fun Days.asDuration(): Duration = durationOf(this)
fun Hours.asDuration(): Duration = durationOf(this)
fun Minutes.asDuration(): Duration = durationOf(this)
fun Seconds.asDuration(): Duration = durationOf(this)
fun Milliseconds.asDuration(): Duration = durationOf(this)
fun Microseconds.asDuration(): Duration = durationOf(this)
fun Nanoseconds.asDuration(): Duration = durationOf(this)

/**
 * Converts this duration to an equivalent Island Time [Duration].
 */
fun kotlin.time.Duration.toIslandDuration(): Duration {
    return toComponents { seconds, nanoseconds -> durationOf(seconds.seconds, nanoseconds.nanoseconds) }
}

internal fun StringBuilder.appendDuration(duration: Duration): StringBuilder {
    duration.toComponentValues { hours, minutes, seconds, nanoseconds ->
        append("PT")

        if (hours != 0L) {
            append(hours)
            append('H')
        }

        if (minutes != 0) {
            append(minutes)
            append('M')
        }

        if (seconds != 0 || nanoseconds != 0) {
            if (seconds == 0 && nanoseconds < 0) {
                append('-')
            }

            append(seconds)

            if (nanoseconds != 0) {
                append('.')
                append(nanoseconds.absoluteValue.toZeroPaddedString(9).dropLastWhile { it == '0' })
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
operator fun Int.times(duration: Duration): Duration = duration * this

/**
 * Converts an ISO-8601 duration string to a [Duration].
 */
fun String.toDuration(): Duration = toDuration(DateTimeParsers.Iso.DURATION)

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
