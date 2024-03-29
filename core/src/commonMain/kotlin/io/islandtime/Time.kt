package io.islandtime

import dev.erikchristensen.javamath2kmp.toIntExact
import io.islandtime.base.DateTimeField
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.serialization.TimeIsoSerializer
import kotlinx.serialization.Serializable

/**
 * A time of day in an ambiguous region.
 *
 * @constructor Creates a [Time] from its individual components.
 * @param hour the hour of day
 * @param minute the minute of the hour
 * @param second the second of the minute
 * @param nanosecond the nanosecond of the second
 * @throws DateTimeException if the time is invalid
 */
@Serializable(with = TimeIsoSerializer::class)
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
    val secondsSinceStartOfDay: Seconds
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
    val nanosecondsSinceStartOfDay: Nanoseconds
        get() = nanosecondOfDay.nanoseconds

    operator fun plus(duration: Duration): Time {
        return this + duration.seconds + duration.nanosecondAdjustment
    }

    /**
     * Returns this time with [duration] added to it.
     */
    operator fun plus(duration: kotlin.time.Duration): Time {
        require(duration.isFinite()) { "The duration must be finite" }
        return duration.toComponents { seconds, nanoseconds -> this + Seconds(seconds) + Nanoseconds(nanoseconds) }
    }

    /**
     * Returns this time with [hours] added to it.
     */
    operator fun plus(hours: Hours): Time {
        val wrappedHours = (hours % HOURS_PER_DAY).toInt()

        return if (wrappedHours == 0) {
            this
        } else {
            val newHour = (wrappedHours + hour + HOURS_PER_DAY) % HOURS_PER_DAY
            return copy(hour = newHour)
        }
    }

    /**
     * Returns this time with [minutes] added to it.
     */
    operator fun plus(minutes: Minutes): Time {
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

    /**
     * Returns this time with [seconds] added to it.
     */
    operator fun plus(seconds: Seconds): Time {
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

    /**
     * Returns this time with [milliseconds] added to it.
     */
    operator fun plus(milliseconds: Milliseconds): Time {
        return plusWrapped((milliseconds % MILLISECONDS_PER_DAY).inNanosecondsUnchecked)
    }

    /**
     * Returns this time with [microseconds] added to it.
     */
    operator fun plus(microseconds: Microseconds): Time {
        return plusWrapped((microseconds % MICROSECONDS_PER_DAY).inNanosecondsUnchecked)
    }

    /**
     * Returns this time with [nanoseconds] added to it.
     */
    operator fun plus(nanoseconds: Nanoseconds): Time = plusWrapped(nanoseconds % NANOSECONDS_PER_DAY)

    private fun plusWrapped(wrappedNanos: Nanoseconds): Time {
        return if (wrappedNanos.value == 0L) {
            this
        } else {
            val newNanoOfDay = (wrappedNanos.value + nanosecondOfDay + NANOSECONDS_PER_DAY) % NANOSECONDS_PER_DAY
            fromNanosecondOfDay(newNanoOfDay)
        }
    }

    operator fun minus(duration: Duration): Time {
        return this - duration.seconds - duration.nanosecondAdjustment
    }

    /**
     * Returns this time with [duration] subtracted from it.
     */
    operator fun minus(duration: kotlin.time.Duration): Time {
        require(duration.isFinite()) { "The duration must be finite" }
        return duration.toComponents { seconds, nanoseconds -> this - Seconds(seconds) - Nanoseconds(nanoseconds) }
    }

    /**
     * Returns this time with [hours] subtracted from it.
     */
    operator fun minus(hours: Hours): Time = plus((hours % HOURS_PER_DAY).negateUnchecked())

    /**
     * Returns this time with [minutes] subtracted from it.
     */
    operator fun minus(minutes: Minutes): Time = plus((minutes % MINUTES_PER_DAY).negateUnchecked())

    /**
     * Returns this time with [seconds] subtracted from it.
     */
    operator fun minus(seconds: Seconds): Time = plus((seconds % SECONDS_PER_DAY).negateUnchecked())

    /**
     * Returns this time with [milliseconds] subtracted from it.
     */
    operator fun minus(milliseconds: Milliseconds): Time {
        return plusWrapped((milliseconds % MILLISECONDS_PER_DAY).inNanosecondsUnchecked.negateUnchecked())
    }

    /**
     * Returns this time with [microseconds] subtracted from it.
     */
    operator fun minus(microseconds: Microseconds): Time {
        return plusWrapped((microseconds % MICROSECONDS_PER_DAY).inNanosecondsUnchecked.negateUnchecked())
    }

    /**
     * Returns this time with [nanoseconds] subtracted from it.
     */
    operator fun minus(nanoseconds: Nanoseconds): Time {
        return plusWrapped((nanoseconds % NANOSECONDS_PER_DAY).negateUnchecked())
    }

    operator fun component1(): Int = hour
    operator fun component2(): Int = minute
    operator fun component3(): Int = second
    operator fun component4(): Int = nanosecond

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
     * Converts this time to a string in ISO-8601 extended format. For example, `17:31:45.923452091` or `02:30`.
     */
    override fun toString(): String = buildString(MAX_TIME_STRING_LENGTH) { appendTime(this@Time) }

    /**
     * Returns a copy of this time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting time is invalid
     */
    fun copy(
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond
    ): Time = Time(hour, minute, second, nanosecond)

    companion object {
        val MIN: Time = Time(0, 0)
        val MAX: Time = Time(23, 59, 59, 999_999_999)
        val MIDNIGHT: Time = Time(0, 0)
        val NOON: Time = Time(12, 0)

        /**
         * Creates a [Time] from the second of the day and optionally, the number of nanoseconds within that second.
         *
         * @param secondOfDay the second of the day
         * @param nanosecond the nanosecond of the second, from 0 to 999,999,999
         * @return a new [Time]
         * @throws DateTimeException if the time is invalid
         */
        fun fromSecondOfDay(secondOfDay: Int, nanosecond: Int = 0): Time {
            if (secondOfDay !in 0 until SECONDS_PER_DAY) {
                throw DateTimeException("'$secondOfDay' is not a valid second of the day")
            }

            val hour = secondOfDay / SECONDS_PER_HOUR
            val minute = (secondOfDay % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
            val second = secondOfDay % SECONDS_PER_MINUTE
            return Time(hour, minute, second, nanosecond)
        }

        /**
         * Creates the [Time] at a number of seconds since the start of the day and optionally, a number of additional
         * nanoseconds.
         *
         * @param seconds the number of seconds since the start of the day
         * @param nanosecondAdjustment the number of additional nanoseconds, from 0 - 999,999,999
         * @return a new [Time]
         * @throws DateTimeException if the time is invalid
         */
        fun fromSecondsSinceStartOfDay(
            seconds: Seconds,
            nanosecondAdjustment: Nanoseconds = 0.nanoseconds
        ): Time {
            return try {
                fromSecondOfDay(seconds.toInt(), nanosecondAdjustment.toInt())
            } catch (e: ArithmeticException) {
                throw DateTimeException("'$seconds' + '$nanosecondAdjustment' overflows an Int", e)
            }
        }

        /**
         * Creates a [Time] from the nanosecond of the day.
         *
         * @param nanosecondOfDay the nanosecond of the day
         * @return a new [Time]
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

        /**
         * Creates the [Time] at a number of nanoseconds since the start of the day.
         *
         * @param nanoseconds the number of nanoseconds since the start of the day
         * @return a new [Time]
         * @throws DateTimeException if the time is invalid
         */
        fun fromNanosecondsSinceStartOfDay(nanoseconds: Nanoseconds): Time {
            return fromNanosecondOfDay(nanoseconds.value)
        }
    }
}

/**
 * Converts a string to a [Time].
 *
 * The string is assumed to be an ISO-8601 time representation in extended format. For example, `05`, `05:30`,
 * `05:30:00`, or `05:30:00.123456789`. The output of [Time.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toTime(): Time = toTime(DateTimeParsers.Iso.Extended.TIME)

/**
 * Converts a string to a [Time] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toTime(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): Time {
    val result = parser.parse(this, settings)
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
    return with(time) {
        appendTime(hour, minute, second, nanosecond)
    }
}

internal fun StringBuilder.appendTime(
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int
): StringBuilder {
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

    return this
}
