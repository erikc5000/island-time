package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.internal.*
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.*

/**
 * A time of day
 */
class Time private constructor(
    val hour: Int,
    val minute: Int,
    val second: Int,
    val nanoOfSecond: Int
) : Comparable<Time> {

    operator fun component1() = hour
    operator fun component2() = minute
    operator fun component3() = second
    operator fun component4() = nanoOfSecond

    override fun equals(other: Any?): Boolean {
        return this === other ||
            (other is Time &&
                hour == other.hour &&
                minute == other.minute &&
                second == other.second &&
                nanoOfSecond == other.nanoOfSecond)
    }

    override fun hashCode(): Int {
        var result = hour
        result = 31 * result + minute
        result = 31 * result + second
        result = 31 * result + nanoOfSecond
        return result
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

    /**
     * Return a [Time] that replaces components with new values, as desired. If unchanged, the returned object may be
     * the same.
     */
    fun copy(
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanoOfSecond: Int = this.nanoOfSecond
    ) = invoke(hour, minute, second, nanoOfSecond)

    companion object {
        private val HOURS = (0..23).map { hour -> (::Time)(hour, 0, 0, 0) }

        val MIN = HOURS[0]
        val MAX = Time(23, 59, 59, 999_999_999)
        val MIDNIGHT = HOURS[0]
        val NOON = HOURS[12]

        /**
         * Create a [Time] representing the provided components
         */
        operator fun invoke(
            hour: Int,
            minute: Int,
            second: Int = 0,
            nanoOfSecond: Int = 0
        ): Time {
            require(isValidHour(hour)) { "'$hour' is not a valid hour" }

            return if (minute or second or nanoOfSecond == 0) {
                HOURS[hour]
            } else {
                require(isValidMinute(minute)) { "'$minute' is not a valid minute" }
                require(isValidSecond(second)) { "'$second' is not a valid second" }
                require(isValidNanoOfSecond(nanoOfSecond)) { "'$nanoOfSecond' is not a valid nanoOfSecond" }

                Time(hour, minute, second, nanoOfSecond)
            }
        }

        /**
         * Create the [Time] representing a number of seconds since the start of the day and optionally, the number of
         * nanoseconds within that second
         */
        fun ofSecondOfDay(secondOfDay: Int, nanoOfSecond: Int = 0): Time {
            require(secondOfDay in 0 until SECONDS_PER_DAY) {
                "'$secondOfDay' is not a valid second of the day"
            }

            val hour = secondOfDay / SECONDS_PER_HOUR.toInt()
            val minute = (secondOfDay / SECONDS_PER_MINUTE.toInt()) % MINUTES_PER_HOUR.toInt()
            val second = secondOfDay % SECONDS_PER_MINUTE.toInt()
            return invoke(hour, minute, second, nanoOfSecond)
        }

        /**
         * Create the [Time] representing a number of nanoseconds since the start of the day
         */
        fun ofNanosecondOfDay(nanosecondOfDay: Long): Time {
            require(nanosecondOfDay in 0L until NANOSECONDS_PER_DAY) {
                "'$nanosecondOfDay' is not a valid nanosecond of the day"
            }

            val hour = (nanosecondOfDay / NANOSECONDS_PER_HOUR).toInt()
            val minute = ((nanosecondOfDay / NANOSECONDS_PER_MINUTE) % MINUTES_PER_HOUR).toInt()
            val second = ((nanosecondOfDay / NANOSECONDS_PER_SECOND) % SECONDS_PER_MINUTE).toInt()
            val nanoOfSecond = (nanosecondOfDay % NANOSECONDS_PER_SECOND).toInt()
            return invoke(hour, minute, second, nanoOfSecond)
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