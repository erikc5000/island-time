package io.islandtime

import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.parser.*

/**
 * A time of day in an arbitrary region.
 *
 * @constructor Create a [Time] from its individual components.
 * @param hour the hour of day
 * @param minute the minute of the hour
 * @param second the second of the minute
 * @param nanosecond the nanosecond of the second
 * @throws DateTimeException if the time is invalid
*/
class Time(
    /** The hour of the day. */
    val hour: Int,
    /** The minute of the hour. */
    val minute: Int,
    /** The second of the minute. */
    val second: Int = 0,
    /** The nanosecond of the second. */
    val nanosecond: Int = 0
) : Comparable<Time> {

    init {
        if (hour !in 0 until HOURS_PER_DAY) {
            throw DateTimeException("'$hour' is not a valid hour")
        }

        if (minute !in 0 until MINUTES_PER_HOUR) {
            throw DateTimeException("'$minute' is not a valid minute")
        }

        if (second !in 0 until SECONDS_PER_MINUTE) {
            throw DateTimeException("'$second' is not a valid second")
        }

        if (nanosecond !in 0 until NANOSECONDS_PER_SECOND) {
            throw DateTimeException("'$nanosecond' is not a valid nanosecond")
        }
    }

    /**
     * The second of the day.
     */
    val secondOfDay: Int
        get() = hour * SECONDS_PER_HOUR + minute * SECONDS_PER_MINUTE + second

    /**
     * The number of seconds since the start of the day.
     */
    inline val secondsSinceStartOfDay: IntSeconds
        get() = secondOfDay.seconds

    /**
     * The nanosecond of the day.
     */
    val nanosecondOfDay: Long
        get() {
            return hour.toLong() * NANOSECONDS_PER_HOUR +
                minute.toLong() * NANOSECONDS_PER_MINUTE +
                second.toLong() * NANOSECONDS_PER_SECOND +
                nanosecond
        }

    /**
     * The number of nanoseconds since the start of the day.
     */
    inline val nanosecondsSinceStartOfDay: LongNanoseconds
        get() = nanosecondOfDay.nanoseconds

    operator fun plus(duration: Duration): Time {
        return this + duration.seconds + duration.nanosecondAdjustment
    }

    operator fun plus(hours: LongHours): Time {
        val wrappedHours = (hours % HOURS_PER_DAY).toInt()

        return if (wrappedHours == 0) {
            this
        } else {
            val newHour = (wrappedHours + hour + HOURS_PER_DAY) % HOURS_PER_DAY
            return copy(hour = newHour)
        }
    }

    operator fun plus(hours: IntHours) = plus(hours.toLongHours())

    operator fun plus(minutes: LongMinutes): Time {
        return if (minutes.value == 0L) {
            this
        } else {
            val currentMinuteOfDay = hour * MINUTES_PER_HOUR + minute
            val wrappedMinutes = (minutes.value % MINUTES_PER_DAY).toInt()
            val newMinuteOfDay = (wrappedMinutes + currentMinuteOfDay + MINUTES_PER_DAY) % MINUTES_PER_DAY

            if (currentMinuteOfDay == newMinuteOfDay) {
                this
            } else {
                val newHour = newMinuteOfDay / MINUTES_PER_HOUR
                val newMinute = newMinuteOfDay % MINUTES_PER_HOUR
                copy(hour = newHour, minute = newMinute)
            }
        }
    }

    operator fun plus(minutes: IntMinutes) = plus(minutes.toLongMinutes())

    operator fun plus(seconds: LongSeconds): Time {
        return if (seconds.value == 0L) {
            this
        } else {
            val currentSecondOfDay = secondOfDay
            val wrappedSeconds = (seconds.value % SECONDS_PER_DAY).toInt()
            val newSecondOfDay = (wrappedSeconds + currentSecondOfDay + SECONDS_PER_DAY) % SECONDS_PER_DAY

            if (currentSecondOfDay == newSecondOfDay) {
                this
            } else {
                fromSecondOfDay(newSecondOfDay, nanosecond)
            }
        }
    }

    operator fun plus(seconds: IntSeconds) = plus(seconds.toLongSeconds())
    
    operator fun plus(milliseconds: LongMilliseconds): Time {
        return plusWrapped((milliseconds % MILLISECONDS_PER_DAY).inNanosecondsUnchecked)
    }
    
    operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.toLongMilliseconds())

    operator fun plus(microseconds: LongMicroseconds): Time {
        return plusWrapped((microseconds % MICROSECONDS_PER_DAY).inNanosecondsUnchecked)
    }

    operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.toLongMicroseconds())

    operator fun plus(nanoseconds: LongNanoseconds): Time {
        return plusWrapped(nanoseconds % NANOSECONDS_PER_DAY)
    }

    private fun plusWrapped(wrappedNanos: LongNanoseconds): Time {
        return if (wrappedNanos.value == 0L) {
            this
        } else {
            val currentNanoOfDay = nanosecondOfDay
            val newNanoOfDay = (wrappedNanos.value + currentNanoOfDay + NANOSECONDS_PER_DAY) % NANOSECONDS_PER_DAY

            if (currentNanoOfDay == newNanoOfDay) {
                this
            } else {
                fromNanosecondOfDay(newNanoOfDay)
            }
        }
    }

    operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds())

    operator fun minus(duration: Duration): Time {
        return this - duration.seconds - duration.nanosecondAdjustment
    }

    operator fun minus(hours: LongHours) = plus((hours % HOURS_PER_DAY).negateUnchecked())
    operator fun minus(hours: IntHours) = plus(hours.toLongHours().negateUnchecked())
    operator fun minus(minutes: LongMinutes) = plus((minutes % MINUTES_PER_DAY).negateUnchecked())
    operator fun minus(minutes: IntMinutes) = plus(minutes.toLongMinutes().negateUnchecked())
    operator fun minus(seconds: LongSeconds) = plus((seconds % SECONDS_PER_DAY).negateUnchecked())
    operator fun minus(seconds: IntSeconds) = plus(seconds.toLongSeconds().negateUnchecked())

    operator fun minus(milliseconds: LongMilliseconds) =
        plusWrapped((milliseconds % MILLISECONDS_PER_DAY).inNanosecondsUnchecked.negateUnchecked())

    operator fun minus(milliseconds: IntMilliseconds) = plus(milliseconds.toLongMilliseconds().negateUnchecked())

    operator fun minus(microseconds: LongMicroseconds) =
        plusWrapped((microseconds % MICROSECONDS_PER_DAY).inNanosecondsUnchecked.negateUnchecked())

    operator fun minus(microseconds: IntMicroseconds) = plus(microseconds.toLongMicroseconds().negateUnchecked())

    operator fun minus(nanoseconds: LongNanoseconds) =
        plusWrapped((nanoseconds % NANOSECONDS_PER_DAY).negateUnchecked())

    operator fun minus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds().negateUnchecked())

    operator fun component1() = hour
    operator fun component2() = minute
    operator fun component3() = second
    operator fun component4() = nanosecond

    override fun equals(other: Any?): Boolean {
        return this === other ||
            (other is Time &&
                hour == other.hour &&
                minute == other.minute &&
                second == other.second &&
                nanosecond == other.nanosecond)
    }

    override fun hashCode(): Int {
        var result = hour
        result = 31 * result + minute
        result = 31 * result + second
        result = 31 * result + nanosecond
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
                    nanosecond.compareTo(other.nanosecond)
                }
            }
        }
    }

    /**
     * Convert this time to a string in ISO-8601 extended format.
     */
    override fun toString() = buildString(MAX_TIME_STRING_LENGTH) { appendTime(this@Time) }

    /**
     * Return a copy of this [Time], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting time is invalid
     */
    fun copy(
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond
    ) = Time(hour, minute, second, nanosecond)

    /**
     * Return a copy of this time, truncated to the [hour] value. All smaller components will be replaced with zero.
     */
    fun truncatedToHours() = copy(minute = 0, second = 0, nanosecond = 0)

    /**
     * Return a copy of this time, truncated to the [minute] value. All smaller components will be replaced with zero.
     */
    fun truncatedToMinutes() = copy(second = 0, nanosecond = 0)

    /**
     * Return a copy of this time, truncated to the [second] value. All smaller components will be replaced with zero.
     */
    fun truncatedToSeconds() = copy(nanosecond = 0)

    /**
     * Return a copy of this time with the [nanosecond] value truncated to milliseconds.
     */
    fun truncatedToMilliseconds() =
        copy(nanosecond = this.nanosecond / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND)

    /**
     * Return a copy of this time with the [nanosecond] value truncated to microseconds.
     */
    fun truncatedToMicroseconds() =
        copy(nanosecond = this.nanosecond / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND)

    companion object {
        val MIN = Time(0, 0)
        val MAX = Time(23, 59, 59, 999_999_999)
        val MIDNIGHT = Time(0, 0)
        val NOON = Time(12, 0)

        /**
         * Create the [Time] representing a number of seconds since the start of the day and optionally, the number of
         * nanoseconds within that second.
         *
         * @throws DateTimeException if the time is invalid
         */
        fun fromSecondOfDay(secondOfDay: Int, nanosecond: Int = 0): Time {
            if (secondOfDay !in 0 until SECONDS_PER_DAY) {
                throw DateTimeException("'$secondOfDay' is not a valid second of the day")
            }

            val hour = secondOfDay / SECONDS_PER_HOUR
            val minute = (secondOfDay / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR
            val second = secondOfDay % SECONDS_PER_MINUTE
            return Time(hour, minute, second, nanosecond)
        }

        /**
         * Create the [Time] representing a number of nanoseconds since the start of the day.
         *
         * @throws DateTimeException if the time is invalid
         */
        fun fromNanosecondOfDay(nanosecondOfDay: Long): Time {
            if (nanosecondOfDay !in 0L until NANOSECONDS_PER_DAY) {
                throw DateTimeException("'$nanosecondOfDay' is not a valid nanosecond of the day")
            }

            val hour = (nanosecondOfDay / NANOSECONDS_PER_HOUR).toInt()
            val minute = ((nanosecondOfDay / NANOSECONDS_PER_MINUTE) % MINUTES_PER_HOUR).toInt()
            val second = ((nanosecondOfDay / NANOSECONDS_PER_SECOND) % SECONDS_PER_MINUTE).toInt()
            val nanosecond = (nanosecondOfDay % NANOSECONDS_PER_SECOND).toInt()
            return Time(hour, minute, second, nanosecond)
        }
    }
}

/**
 * Convert a string to a [Time].
 *
 * The string is assumed to be an ISO-8601 time representation in extended format. For example, `05`, `05:30`,
 * `05:30:00`, or `05:30:00.123456789`. The output of [Time.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toTime() = toTime(DateTimeParsers.Iso.Extended.TIME)

/**
 * Convert a string to a [Time] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toTime(parser: DateTimeParser): Time {
    val result = parser.parse(this)
    return result.toTime() ?: throwParserFieldResolutionException<Time>(this)
}

internal fun DateTimeParseResult.toTime(): Time? {
    val hour = fields[DateTimeField.HOUR_OF_DAY]

    // TODO: Add support for SECOND_OF_DAY, NANOSECOND_OF_DAY, and so forth
    if (hour != null) {
        return try {
            val minute = fields[DateTimeField.MINUTE_OF_HOUR]?.toIntExact() ?: 0
            val second = fields[DateTimeField.SECOND_OF_MINUTE]?.toIntExact() ?: 0
            val nanosecond = fields[DateTimeField.NANOSECOND_OF_SECOND]?.toIntExact() ?: 0
            Time(hour.toIntExact(), minute, second, nanosecond)
        } catch (e: ArithmeticException) {
            throw DateTimeException(e.message, e)
        }
    }

    return null
}

internal const val MAX_TIME_STRING_LENGTH = 18

internal fun StringBuilder.appendTime(time: Time): StringBuilder {
    with(time) {
        appendZeroPadded(hour, 2)
        append(':')
        appendZeroPadded(minute, 2)

        if (second > 0 || nanosecond > 0) {
            append(':')
            appendZeroPadded(second, 2)

            if (nanosecond > 0) {
                append('.')

                when {
                    nanosecond % 1_000_000 == 0 -> appendZeroPadded(nanosecond / 1_000_000, 3)
                    nanosecond % 1_000 == 0 -> appendZeroPadded(nanosecond / 1_000, 6)
                    else -> appendZeroPadded(nanosecond, 9)
                }
            }
        }
    }
    return this
}