package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.parser.raiseParserFieldResolutionException

/**
 * A time of day combined with a specific UTC offset
 */
class OffsetTime(
    val time: Time,
    val offset: UtcOffset
) : Comparable<OffsetTime> {

    /**
     * Create an [OffsetTime]
     */
    constructor(
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanosecond: Int = 0,
        offset: UtcOffset
    ) : this(Time(hour, minute, second, nanosecond), offset)

    inline val hour: Int get() = time.hour
    inline val minute: Int get() = time.minute
    inline val second: Int get() = time.second
    inline val nanosecond: Int get() = time.nanosecond

    /**
     * The number of nanoseconds since the start of the day, but normalized to a UTC offset of zero, allowing
     * [OffsetTime] objects with different offsets to be compared
     */
    val nanosecondsSinceStartOfUtcDay: LongNanoseconds
        get() = time.nanosecondsSinceStartOfDay - offset.totalSeconds

    /**
     * Change the offset of an [OffsetTime], adjusting the time component such that the instant represented by it
     * remains the same
     */
    fun adjustedTo(newOffset: UtcOffset): OffsetTime {
        return if (newOffset == offset) {
            this
        } else {
            val newTime = time + (newOffset.totalSeconds - offset.totalSeconds)
            OffsetTime(newTime, newOffset)
        }
    }

    operator fun plus(hoursToAdd: LongHours): OffsetTime {
        return if (hoursToAdd == 0L.hours) {
            this
        } else {
            copy(time = time + hoursToAdd)
        }
    }

    operator fun plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

    operator fun plus(minutesToAdd: LongMinutes): OffsetTime {
        return if (minutesToAdd == 0L.minutes) {
            this
        } else {
            copy(time = time + minutesToAdd)
        }
    }

    operator fun plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

    operator fun plus(secondsToAdd: LongSeconds): OffsetTime {
        return if (secondsToAdd == 0L.seconds) {
            this
        } else {
            copy(time = time + secondsToAdd)
        }
    }

    operator fun plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

    operator fun plus(nanosecondsToAdd: LongNanoseconds): OffsetTime {
        return if (nanosecondsToAdd == 0L.nanoseconds) {
            this
        } else {
            copy(time = time + nanosecondsToAdd)
        }
    }

    operator fun plus(nanosecondsToAdd: IntNanoseconds) = plus(nanosecondsToAdd.toLong())

    operator fun minus(hoursToSubtract: LongHours) = plus(-hoursToSubtract)
    operator fun minus(hoursToSubtract: IntHours) = plus(-hoursToSubtract)
    operator fun minus(minutesToSubtract: LongMinutes) = plus(-minutesToSubtract)
    operator fun minus(minutesToSubtract: IntMinutes) = plus(-minutesToSubtract)
    operator fun minus(secondsToSubtract: LongSeconds) = plus(-secondsToSubtract)
    operator fun minus(secondsToSubtract: IntSeconds) = plus(-secondsToSubtract)
    operator fun minus(nanosecondsToSubtract: LongNanoseconds) = plus(-nanosecondsToSubtract)
    operator fun minus(nanosecondsToSubtract: IntNanoseconds) = plus(-nanosecondsToSubtract)

    operator fun component1() = time
    operator fun component2() = offset

    override fun compareTo(other: OffsetTime): Int {
        return if (offset == other.offset) {
            time.compareTo(other.time)
        } else {
            val nanoDiff = nanosecondsSinceStartOfUtcDay.compareTo(other.nanosecondsSinceStartOfUtcDay)

            if (nanoDiff != 0) {
                nanoDiff
            } else {
                time.compareTo(other.time)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is OffsetTime && time == other.time && offset == other.offset)
    }

    override fun hashCode(): Int {
        return 31 * time.hashCode() + offset.hashCode()
    }

    override fun toString(): String {
        return buildString(MAX_OFFSET_TIME_STRING_LENGTH) { appendOffsetTime(this@OffsetTime) }
    }

    /**
     * Return an [OffsetTime] that replaces components with new values, as desired
     */
    fun copy(
        time: Time = this.time,
        offset: UtcOffset = this.offset
    ) = OffsetTime(time, offset)

    /**
     * Return an [OffsetTime] that replaces components with new values, as desired
     */
    fun copy(
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset
    ) = OffsetTime(time.copy(hour, minute, second, nanosecond), offset)

    companion object {
        val MIN = Time.MIN at UtcOffset.MAX
        val MAX = Time.MAX at UtcOffset.MIN
    }
}

/**
 * Create an [OffsetTime] by combining a [Time] with a [UtcOffset]
 */
infix fun Time.at(offset: UtcOffset) = OffsetTime(this, offset)

fun String.toOffsetTime() = toOffsetTime(Iso8601.Extended.OFFSET_TIME_PARSER)

fun String.toOffsetTime(parser: DateTimeParser): OffsetTime {
    val result = parser.parse(this)
    return result.toOffsetTime() ?: raiseParserFieldResolutionException("OffsetTime", this)
}

internal fun DateTimeParseResult.toOffsetTime(): OffsetTime? {
    val time = this.toTime()
    val utcOffset = this.toUtcOffset()

    return if (time != null && utcOffset != null) {
        OffsetTime(time, utcOffset)
    } else {
        null
    }
}

internal const val MAX_OFFSET_TIME_STRING_LENGTH = MAX_TIME_STRING_LENGTH + MAX_UTC_OFFSET_STRING_LENGTH

internal fun StringBuilder.appendOffsetTime(offsetTime: OffsetTime): StringBuilder {
    with(offsetTime) {
        appendTime(time)
        appendUtcOffset(offset)
    }
    return this
}