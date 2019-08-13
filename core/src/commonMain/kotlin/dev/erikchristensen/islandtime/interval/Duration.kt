package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_SECOND
import dev.erikchristensen.islandtime.internal.isValidNanoOfSecond
import dev.erikchristensen.islandtime.interval.Duration.Companion.create
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * An exact measurement of time
 */
class Duration private constructor(
    val seconds: LongSecondSpan,
    val nanoOfSeconds: NanosecondSpan = 0.nanoseconds
) : Comparable<Duration> {

    init {
        require(isValidNanoOfSecond(nanoOfSeconds.value)) { "nanoOfSeconds is out of range" }
    }

    override fun equals(other: Any?): Boolean {
        return other is Duration &&
            other.seconds.value == seconds.value &&
            other.nanoOfSeconds.value == nanoOfSeconds.value
    }

    override fun hashCode(): Int {
        // TODO: Implement
        return super.hashCode()
    }

    override fun toString(): String {
        // TODO: Implement
        return super.toString()
    }

    override fun compareTo(other: Duration): Int {
        val secondsDiff = seconds.value.compareTo(other.seconds.value)

        return if (secondsDiff != 0) {
            secondsDiff
        } else {
            nanoOfSeconds.value - other.nanoOfSeconds.value
        }
    }

    companion object {
        @JvmField
        val ZERO = Duration(0L.seconds)

        @JvmStatic
        internal fun create(
            seconds: LongSecondSpan,
            nanoOfSeconds: NanosecondSpan = 0.nanoseconds
        ): Duration {
            return if (seconds == 0L.seconds && nanoOfSeconds == 0.nanoseconds) {
                ZERO
            } else {
                Duration(seconds, nanoOfSeconds)
            }
        }
    }
}

inline val Duration.isZero: Boolean get() = this == Duration.ZERO
inline val Duration.isNegative: Boolean get() = seconds.value < 0L

operator fun Duration.unaryPlus() = create(+seconds, nanoOfSeconds)
operator fun Duration.unaryMinus() = create(-seconds, nanoOfSeconds)

operator fun Duration.plus(other: Duration): Duration {
    return when {
        other.isZero -> this
        this.isZero -> other
        else -> if (other.nanoOfSeconds.value == 0) {
            create(seconds + other.seconds, nanoOfSeconds)
        } else {
            // TODO: Re-write this to handle negatives and nanoOfSecond properly
            val totalNanos = nanoOfSeconds + other.nanoOfSeconds
            val newSeconds = seconds + other.seconds + (totalNanos.value / NANOSECONDS_PER_SECOND).seconds
            val wrappedNanos = totalNanos % NANOSECONDS_PER_SECOND.toInt()
            create(newSeconds, wrappedNanos)
        }
    }
}

operator fun Duration.plus(hoursToAdd: HourSpan) = plus(hoursToAdd.toLong())

operator fun Duration.plus(hoursToAdd: LongHourSpan): Duration {
    return if (hoursToAdd.value == 0L) {
        this
    } else {
        create(seconds + hoursToAdd.asSeconds(), nanoOfSeconds)
    }
}

operator fun Duration.plus(minutesToAdd: MinuteSpan) = plus(minutesToAdd.toLong())

operator fun Duration.plus(minutesToAdd: LongMinuteSpan): Duration {
    return if (minutesToAdd.value == 0L) {
        this
    } else {
        create(seconds + minutesToAdd.asSeconds(), nanoOfSeconds)
    }
}

operator fun Duration.plus(secondsToAdd: SecondSpan) = plus(secondsToAdd.toLong())

operator fun Duration.plus(secondsToAdd: LongSecondSpan): Duration {
    return if (secondsToAdd.value == 0L) {
        this
    } else {
        create(seconds + secondsToAdd, nanoOfSeconds)
    }
}

//    operator fun Duration.plus(nanosecondsToAdd: NanosecondSpan): Duration {
//        return if (nanosecondsToAdd == 0.nanoseconds) {
//            this
//        } else {
//            val newSeconds = (nanosecondsToAdd.value / NANOSECONDS_PER_SECOND).seconds
//            val newNanoOfSeconds = (nanosecondsToAdd.value - seconds.value * NANOSECONDS_PER_SECOND).toInt()
//        }
//    }

operator fun Duration.minus(other: Duration) = plus(-other)
operator fun Duration.minus(hoursToSubtract: HourSpan) = plus(-hoursToSubtract)
operator fun Duration.minus(hoursToSubtract: LongHourSpan) = plus(-hoursToSubtract)
operator fun Duration.minus(minutesToSubtracts: MinuteSpan) = plus(-minutesToSubtracts)
operator fun Duration.minus(minutesToSubtracts: LongMinuteSpan) = plus(-minutesToSubtracts)
operator fun Duration.minus(secondsToSubtract: SecondSpan) = plus(-secondsToSubtract)
operator fun Duration.minus(secondsToSubtract: LongSecondSpan) = plus(-secondsToSubtract)

// These may require a multiplatform BigDecimal implementation
// operator fun Duration.times(scalar: Int): Duration
// operator fun div(scalar: Int): Duration

fun <T> Duration.toComponents(
    block: (
        hours: HourSpan,
        minutes: MinuteSpan,
        seconds: SecondSpan,
        nanoseconds: NanosecondSpan
    ) -> T
): T {
    val hours = seconds.asWholeHours()
    val minutes = (seconds - hours).asWholeMinutes()
    val seconds = seconds - hours - minutes
    return block(hours.toInt(), minutes.toInt(), seconds.toInt(), nanoOfSeconds)
}

fun durationOf(hours: HourSpan) = durationOf(hours.toLong())
fun durationOf(hours: LongHourSpan) = create(hours.asSeconds())

fun durationOf(
    hours: LongHourSpan,
    minutes: LongMinuteSpan
) = create(hours.asSeconds() + minutes.asSeconds())

fun durationOf(
    hours: LongHourSpan,
    seconds: LongSecondSpan
) = create(hours.asSeconds() + seconds)

fun durationOf(
    hours: LongHourSpan,
    minutes: LongMinuteSpan,
    seconds: LongSecondSpan
) = create(hours.asSeconds() + minutes.asSeconds() + seconds)

fun durationOf(minutes: MinuteSpan) = durationOf(minutes.toLong())
fun durationOf(minutes: LongMinuteSpan) = create(minutes.asSeconds())
fun durationOf(minutes: LongMinuteSpan, seconds: LongSecondSpan) = create(minutes.asSeconds() + seconds)

fun durationOf(seconds: SecondSpan) = durationOf(seconds.toLong())
fun durationOf(seconds: LongSecondSpan) = create(seconds)

fun durationOf(nanoseconds: NanosecondSpan) = durationOf(nanoseconds.toLong())

fun durationOf(nanoseconds: LongNanosecondSpan): Duration {
    // TODO: This probably won't work right with negatives
    val seconds = (nanoseconds.value / NANOSECONDS_PER_SECOND).seconds
    val nanoOfSeconds = (nanoseconds - seconds.asNanoseconds()).toInt()
    return create(seconds, nanoOfSeconds)
}

/**
 * Return the absolute value of a duration. Any negative duration will become positive.
 */
fun abs(duration: Duration) = if (duration.isNegative) -duration else duration

fun LongHourSpan.asDuration() = durationOf(this)
fun LongMinuteSpan.asDuration() = durationOf(this)
fun LongSecondSpan.asDuration() = durationOf(this)
fun LongNanosecondSpan.asDuration() = durationOf(this)

fun HourSpan.asDuration() = durationOf(this.toLong())
fun MinuteSpan.asDuration() = durationOf(this.toLong())
fun SecondSpan.asDuration() = durationOf(this.toLong())
fun NanosecondSpan.asDuration() = durationOf(this.toLong())