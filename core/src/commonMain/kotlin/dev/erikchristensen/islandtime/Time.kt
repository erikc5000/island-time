package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.internal.*
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.*
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

data class Time @JvmOverloads constructor(
    val hour: Int,
    val minute: Int,
    val second: Int = 0,
    val nanoOfSecond: Int = 0
) : Comparable<Time> {

    init {
        require(isValidHour(hour)) { "'$hour' is not a valid hour" }
        require(isValidMinute(minute)) { "'$minute' is not a valid minute" }
        require(isValidSecond(second)) { "'$second' is not a valid second" }
        require(isValidNanoOfSecond(nanoOfSecond)) { "'$nanoOfSecond' is not a valid nanoOfSecond" }
    }

    override fun compareTo(other: Time): Int {
        val hourDiff = hour.compareTo(other.hour)

        return if (hourDiff != 0) {
            hourDiff
        } else {
            val minuteDiff = minute.compareTo(other.minute)

            if (minuteDiff != 0) {
                minuteDiff
            } else {
                val secondDiff = second.compareTo(other.second)

                if (secondDiff != 0) {
                    secondDiff
                } else {
                    nanoOfSecond.compareTo(other.nanoOfSecond)
                }
            }
        }
    }

    override fun toString() = buildString(MAX_TIME_STRING_LENGTH) { appendTime(this@Time) }

    companion object {
        @JvmField
        val MIN = Time(0, 0, 0, 0)

        @JvmField
        val MAX = Time(23, 59, 59, 999_999_999)

        @JvmField
        val MIDNIGHT = Time(0, 0, 0, 0)

        @JvmField
        val NOON = Time(12, 0, 0, 0)

        @JvmStatic
        @JvmOverloads
        fun ofSecondOfDay(secondOfDay: Int, nanoOfSecond: Int = 0): Time {
            require(secondOfDay in 0 until SECONDS_PER_DAY) {
                "'$secondOfDay' is not valid second of the day"
            }

            val hour = secondOfDay / SECONDS_PER_HOUR.toInt()
            val minute = (secondOfDay / SECONDS_PER_MINUTE.toInt()) % MINUTES_PER_HOUR.toInt()
            val second = secondOfDay % SECONDS_PER_MINUTE.toInt()
            return Time(hour, minute, second, nanoOfSecond)
        }

        @JvmStatic
        fun ofNanosecondOfDay(nanosecondOfDay: Long): Time {
            require(nanosecondOfDay in 0L until NANOSECONDS_PER_DAY) {
                "'$nanosecondOfDay' is not valid nanosecond of the day"
            }

            val hour = (nanosecondOfDay / NANOSECONDS_PER_HOUR).toInt()
            val minute = ((nanosecondOfDay / NANOSECONDS_PER_MINUTE) % MINUTES_PER_HOUR).toInt()
            val second = ((nanosecondOfDay / NANOSECONDS_PER_SECOND) % SECONDS_PER_MINUTE).toInt()
            val nanoOfSecond = (nanosecondOfDay % NANOSECONDS_PER_SECOND).toInt()
            return Time(hour, minute, second, nanoOfSecond)
        }
    }
}

val Time.secondOfDay: Int
    get() = hour * SECONDS_PER_HOUR.toInt() + minute * SECONDS_PER_MINUTE.toInt() + second

val Time.nanosecondOfDay: Long
    get() {
        return hour * NANOSECONDS_PER_HOUR +
            minute * NANOSECONDS_PER_MINUTE +
            second * NANOSECONDS_PER_SECOND +
            nanoOfSecond
    }

operator fun Time.plus(hoursToAdd: LongHours): Time {
    val wrappedHours = (hoursToAdd % HOURS_PER_DAY).toInt()

    return if (wrappedHours.value == 0) {
        this
    } else {
        val newHour = (wrappedHours.value + hour + HOURS_PER_DAY.toInt()) % HOURS_PER_DAY.toInt()
        return copy(hour = newHour)
    }
}

operator fun Time.plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

operator fun Time.plus(minutesToAdd: LongMinutes): Time {
    return if (minutesToAdd.value == 0L) {
        this
    } else {
        val currentMinuteOfDay = hour * MINUTES_PER_HOUR.toInt() + minute
        val wrappedMinutes = (minutesToAdd.value % MINUTES_PER_DAY).toInt()
        val newMinuteOfDay = (wrappedMinutes + currentMinuteOfDay + MINUTES_PER_DAY.toInt()) % MINUTES_PER_DAY.toInt()

        if (currentMinuteOfDay == newMinuteOfDay) {
            this
        } else {
            val newHour = newMinuteOfDay / MINUTES_PER_HOUR.toInt()
            val newMinute = newMinuteOfDay % MINUTES_PER_HOUR.toInt()
            copy(hour = newHour, minute = newMinute)
        }
    }
}

operator fun Time.plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

operator fun Time.plus(secondsToAdd: LongSeconds): Time {
    return if (secondsToAdd.value == 0L) {
        this
    } else {
        val currentSecondOfDay = secondOfDay
        val wrappedSeconds = (secondsToAdd.value % SECONDS_PER_DAY).toInt()
        val newSecondOfDay = (wrappedSeconds + currentSecondOfDay + SECONDS_PER_DAY.toInt()) % SECONDS_PER_DAY.toInt()

        if (currentSecondOfDay == newSecondOfDay) {
            this
        } else {
            Time.ofSecondOfDay(newSecondOfDay, nanoOfSecond)
        }
    }
}

operator fun Time.plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

operator fun Time.plus(nanosecondsToAdd: LongNanoseconds): Time {
    return if (nanosecondsToAdd.value == 0L) {
        this
    } else {
        val currentNanoOfDay = nanosecondOfDay
        val wrappedNanos = nanosecondsToAdd.value % NANOSECONDS_PER_DAY
        val newNanoOfDay = (wrappedNanos + currentNanoOfDay + NANOSECONDS_PER_DAY) % NANOSECONDS_PER_DAY

        if (currentNanoOfDay == newNanoOfDay) {
            this
        } else {
            Time.ofNanosecondOfDay(newNanoOfDay)
        }
    }
}

operator fun Time.plus(nanosecondsToAdd: IntNanoseconds) = plus(nanosecondsToAdd.toLong())

operator fun Time.minus(hoursToSubtract: IntHours) = plus(-hoursToSubtract)
operator fun Time.minus(minutesToSubtract: IntMinutes) = plus(-minutesToSubtract)
operator fun Time.minus(secondsToSubtract: IntSeconds) = plus(-secondsToSubtract)
operator fun Time.minus(nanosecondsToSubtract: LongNanoseconds) = plus(-nanosecondsToSubtract)

fun String.toTime() = toTime(Iso8601.Extended.TIME_PARSER)

fun String.toTime(parser: DateTimeParser): Time {
    val result = parser.parse(this)
    return result.toTime() ?: raiseParserFieldResolutionException("Time", this)
}

internal fun DateTimeParseResult.toTime(): Time? {
    val hour = this[DateTimeField.HOUR_OF_DAY]

    if (hour != null) {
        val minute = this[DateTimeField.MINUTE_OF_HOUR]?.toInt() ?: 0
        val second = this[DateTimeField.SECOND_OF_MINUTE]?.toInt() ?: 0
        val nanoOfSecond = this[DateTimeField.NANO_OF_SECOND]?.toInt() ?: 0
        return Time(hour.toInt(), minute, second, nanoOfSecond)
    }

    return null
}

internal const val MAX_TIME_STRING_LENGTH = 18

internal fun StringBuilder.appendTime(time: Time): StringBuilder {
    with(time) {
        appendZeroPadded(hour, 2)
        append(':')
        appendZeroPadded(minute, 2)

        if (second > 0 || nanoOfSecond > 0) {
            append(':')
            appendZeroPadded(second, 2)

            if (nanoOfSecond > 0) {
                append('.')

                when {
                    nanoOfSecond % 1_000_000 == 0 -> appendZeroPadded(nanoOfSecond / 1_000_000, 3)
                    nanoOfSecond % 1_000 == 0 -> appendZeroPadded(nanoOfSecond / 1_000, 6)
                    else -> appendZeroPadded(nanoOfSecond, 9)
                }
            }
        }
    }
    return this
}