package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MICROSECONDS_PER_SECOND
import dev.erikchristensen.islandtime.internal.MILLISECONDS_PER_SECOND
import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_SECOND
import dev.erikchristensen.islandtime.internal.toZeroPaddedString
import dev.erikchristensen.islandtime.interval.Duration.Companion.create
import dev.erikchristensen.islandtime.parser.DateTimeField
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import kotlin.math.abs

/**
 * An exact measurement of time
 */
class Duration private constructor(
    val seconds: LongSeconds,
    val nanosecondAdjustment: IntNanoseconds = 0.nanoseconds
) : Comparable<Duration> {

    init {
        // TODO: Remove this since ranges should be enforced before getting here
        require(nanosecondAdjustment.value in -999_999_999..999_999_999) {
            "nanosecondAdjustment is out of range"
        }
    }

    inline val isZero: Boolean get() = this == ZERO

    val isPositive: Boolean
        get() = seconds.value > 0L || nanosecondAdjustment.value > 0

    val isNegative: Boolean
        get() = seconds.value < 0L || nanosecondAdjustment.value < 0

    /**
     * Return the absolute value of this duration
     */
    val absoluteValue: Duration
        get() = if (isNegative) -this else this

    operator fun unaryMinus() = create(-seconds, -nanosecondAdjustment)

    operator fun plus(other: Duration): Duration {
        return when {
            other.isZero -> this
            this.isZero -> other
            else -> plus(other.seconds, other.nanosecondAdjustment)
        }
    }

    operator fun plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())
    operator fun plus(hoursToAdd: LongHours) = plus(hoursToAdd.asSeconds())

    operator fun plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())
    operator fun plus(minutesToAdd: LongMinutes) = plus(minutesToAdd.asSeconds())

    operator fun plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

    operator fun plus(secondsToAdd: LongSeconds): Duration {
        return if (secondsToAdd.value == 0L) {
            this
        } else {
            val newSeconds = seconds + secondsToAdd
            plus(newSeconds, 0.nanoseconds)
        }
    }

    operator fun plus(nanosecondsToAdd: IntNanoseconds) = plus(nanosecondsToAdd.toLong())

    operator fun plus(nanosecondsToAdd: LongNanoseconds): Duration {
        return if (nanosecondsToAdd.value == 0L) {
            this
        } else {
            val secondsToAdd = nanosecondsToAdd.toWholeSeconds()
            val remainingNanoseconds = (nanosecondsToAdd % NANOSECONDS_PER_SECOND).toInt()
            plus(secondsToAdd, remainingNanoseconds)
        }
    }

    operator fun minus(other: Duration) = plus(-other)
    operator fun minus(hoursToSubtract: IntHours) = plus(-hoursToSubtract)
    operator fun minus(hoursToSubtract: LongHours) = plus(-hoursToSubtract)
    operator fun minus(minutesToSubtracts: IntMinutes) = plus(-minutesToSubtracts)
    operator fun minus(minutesToSubtracts: LongMinutes) = plus(-minutesToSubtracts)
    operator fun minus(secondsToSubtract: IntSeconds) = plus(-secondsToSubtract)
    operator fun minus(secondsToSubtract: LongSeconds) = plus(-secondsToSubtract)
    operator fun minus(nanosecondsToSubtract: IntNanoseconds) = plus(-nanosecondsToSubtract)
    operator fun minus(nanosecondsToSubtract: LongNanoseconds) = plus(-nanosecondsToSubtract)

    // These may require a multiplatform BigDecimal implementation
    // operator fun times(scalar: Int): Duration
    // operator fun div(scalar: Int): Duration

    inline fun <T> toComponents(
        action: (
            hours: IntHours,
            minutes: IntMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        val hours = seconds.toWholeHours()
        val minutes = (seconds - hours).toWholeMinutes()
        val seconds = seconds - hours - minutes
        return action(hours.toInt(), minutes.toInt(), seconds.toInt(), nanosecondAdjustment)
    }

    inline fun <T> toComponents(
        action: (
            minutes: LongMinutes,
            seconds: IntSeconds,
            nanoseconds: IntNanoseconds
        ) -> T
    ): T {
        val minutes = seconds.toWholeMinutes()
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
        var newSeconds = seconds + secondsToAdd
        var newNanoAdjustment = nanosecondAdjustment plusWithOverflow nanosecondsToAdd

        if (newNanoAdjustment.value >= NANOSECONDS_PER_SECOND) {
            newSeconds += 1L.seconds
            newNanoAdjustment = newNanoAdjustment minusWithOverflow 1.seconds
        } else if (newNanoAdjustment.value <= -NANOSECONDS_PER_SECOND) {
            newSeconds -= 1L.seconds
            newNanoAdjustment = newNanoAdjustment plusWithOverflow 1.seconds
        }

        if (newNanoAdjustment.value < 0 && newSeconds.value > 0) {
            newSeconds -= 1L.seconds
            newNanoAdjustment = (NANOSECONDS_PER_SECOND - newNanoAdjustment.value).nanoseconds
        } else if (newNanoAdjustment.value > 0 && newSeconds.value < 0) {
            newSeconds += 1L.seconds
            newNanoAdjustment = (newNanoAdjustment.value - NANOSECONDS_PER_SECOND).nanoseconds
        }

        return create(newSeconds, newNanoAdjustment)
    }

    companion object {
        val ZERO = Duration(0L.seconds)

        internal fun create(
            seconds: LongSeconds,
            nanoOfSeconds: IntNanoseconds = 0.nanoseconds
        ): Duration {
            return if (seconds.value == 0L && nanoOfSeconds.value == 0) {
                ZERO
            } else {
                Duration(seconds, nanoOfSeconds)
            }
        }
    }
}

fun durationOf(seconds: IntSeconds, nanoseconds: IntNanoseconds) = durationOf(seconds.toLong(), nanoseconds.toLong())
fun durationOf(seconds: LongSeconds, nanoseconds: IntNanoseconds) = durationOf(seconds, nanoseconds.toLong())
fun durationOf(seconds: IntSeconds, nanoseconds: LongNanoseconds) = durationOf(seconds.toLong(), nanoseconds)

fun durationOf(seconds: LongSeconds, nanoseconds: LongNanoseconds): Duration {
    var adjustedSeconds = seconds + nanoseconds.toWholeSeconds()
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

fun durationOf(days: IntDays) = durationOf(days.toLong())
fun durationOf(days: LongDays) = durationOf(days.asSeconds())
fun durationOf(hours: IntHours) = durationOf(hours.toLong())
fun durationOf(hours: LongHours) = create(hours.asSeconds())
fun durationOf(minutes: IntMinutes) = durationOf(minutes.toLong())
fun durationOf(minutes: LongMinutes) = create(minutes.asSeconds())

fun durationOf(seconds: IntSeconds) = durationOf(seconds.toLong())
fun durationOf(seconds: LongSeconds) = create(seconds)

fun durationOf(milliseconds: IntMilliseconds) = durationOf(milliseconds.toLong())

fun durationOf(milliseconds: LongMilliseconds): Duration {
    val seconds = milliseconds.toWholeSeconds()
    val nanoOfSeconds = (milliseconds % MILLISECONDS_PER_SECOND).asNanoseconds().toInt()

    return create(seconds, nanoOfSeconds)
}

fun durationOf(microseconds: IntMicroseconds) = durationOf(microseconds.toLong())

fun durationOf(microseconds: LongMicroseconds): Duration {
    val seconds = microseconds.toWholeSeconds()
    val nanoOfSeconds = (microseconds % MICROSECONDS_PER_SECOND).asNanoseconds().toInt()

    return create(seconds, nanoOfSeconds)
}

fun durationOf(nanoseconds: IntNanoseconds) = durationOf(nanoseconds.toLong())

fun durationOf(nanoseconds: LongNanoseconds): Duration {
    val seconds = nanoseconds.toWholeSeconds()
    val nanoOfSeconds = (nanoseconds % NANOSECONDS_PER_SECOND).toInt()

    return create(seconds, nanoOfSeconds)
}

/**
 * Return the absolute value of a duration
 */
fun abs(duration: Duration) = duration.absoluteValue

fun LongHours.asDuration() = durationOf(this)
fun LongMinutes.asDuration() = durationOf(this)
fun LongSeconds.asDuration() = durationOf(this)
fun LongMilliseconds.asDuration() = durationOf(this)
fun LongMicroseconds.asDuration() = durationOf(this)
fun LongNanoseconds.asDuration() = durationOf(this)

fun IntHours.asDuration() = durationOf(this.toLong())
fun IntMinutes.asDuration() = durationOf(this.toLong())
fun IntSeconds.asDuration() = durationOf(this.toLong())
fun IntMilliseconds.asDuration() = durationOf(this.toLong())
fun IntMicroseconds.asDuration() = durationOf(this.toLong())
fun IntNanoseconds.asDuration() = durationOf(this.toLong())

internal fun StringBuilder.appendDuration(duration: Duration): StringBuilder {
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
        append("PT")

        if (hours.value != 0) {
            append(hours.value)
            append('H')
        }

        if (minutes.value != 0) {
            append(minutes.value)
            append('M')
        }

        if (seconds.value != 0 || nanoseconds.value != 0) {
            if (seconds.value == 0 && nanoseconds.value < 0) {
                append('-')
            }

            append(seconds.value)

            if (nanoseconds.value != 0) {
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