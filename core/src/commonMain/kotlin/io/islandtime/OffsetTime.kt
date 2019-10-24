package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParser
import io.islandtime.parser.DateTimeParsers
import io.islandtime.parser.throwParserFieldResolutionException

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

    operator fun plus(duration: Duration) = copy(time = time + duration)
    operator fun plus(hours: LongHours) = copy(time = time + hours)
    operator fun plus(hours: IntHours) = plus(hours.toLong())
    operator fun plus(minutes: LongMinutes) = copy(time = time + minutes)
    operator fun plus(minutes: IntMinutes) = plus(minutes.toLong())
    operator fun plus(seconds: LongSeconds) = copy(time = time + seconds)
    operator fun plus(seconds: IntSeconds) = plus(seconds.toLong())
    operator fun plus(milliseconds: LongMilliseconds) = copy(time = time + milliseconds)
    operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.toLong())
    operator fun plus(microseconds: LongMicroseconds) = copy(time = time + microseconds)
    operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.toLong())
    operator fun plus(nanoseconds: LongNanoseconds) = copy(time = time + nanoseconds)
    operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLong())

    operator fun minus(duration: Duration) = copy(time = time - duration)
    operator fun minus(hours: LongHours) = copy(time = time - hours)
    operator fun minus(hours: IntHours) = minus(hours.toLong())
    operator fun minus(minutes: LongMinutes) = copy(time = time - minutes)
    operator fun minus(minutes: IntMinutes) = minus(minutes.toLong())
    operator fun minus(seconds: LongSeconds) = copy(time = time - seconds)
    operator fun minus(seconds: IntSeconds) = minus(seconds.toLong())
    operator fun minus(milliseconds: LongMilliseconds) = copy(time = time - milliseconds)
    operator fun minus(milliseconds: IntMilliseconds) = minus(milliseconds.toLong())
    operator fun minus(microseconds: LongMicroseconds) = copy(time = time - microseconds)
    operator fun minus(microseconds: IntMicroseconds) = minus(microseconds.toLong())
    operator fun minus(nanoseconds: LongNanoseconds) = copy(time = time - nanoseconds)
    operator fun minus(nanoseconds: IntNanoseconds) = minus(nanoseconds.toLong())

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

    /**
     * Truncate to the [hour] value, replacing all smaller components with zero
     */
    fun truncatedToHours() = copy(time = time.truncatedToHours())

    /**
     * Truncate to the [minute] value, replacing all smaller components with zero
     */
    fun truncatedToMinutes() = copy(time = time.truncatedToMinutes())

    /**
     * Truncate to the [second] value, replacing all smaller components with zero
     */
    fun truncatedToSeconds() = copy(time = time.truncatedToSeconds())

    /**
     * Truncate the [nanosecond] value to milliseconds, replacing the rest with zero
     */
    fun truncatedToMilliseconds() = copy(time = time.truncatedToMilliseconds())

    /**
     * Truncate the [nanosecond] value to microseconds, replacing the rest with zero
     */
    fun truncatedToMicroseconds() = copy(time = time.truncatedToMicroseconds())

    companion object {
        val MIN = Time.MIN at UtcOffset.MAX
        val MAX = Time.MAX at UtcOffset.MIN
    }
}

/**
 * Create an [OffsetTime] by combining a [Time] with a [UtcOffset]
 */
infix fun Time.at(offset: UtcOffset) = OffsetTime(this, offset)

fun String.toOffsetTime() = toOffsetTime(DateTimeParsers.Iso.Extended.OFFSET_TIME)

fun String.toOffsetTime(parser: DateTimeParser): OffsetTime {
    val result = parser.parse(this)
    return result.toOffsetTime() ?: throwParserFieldResolutionException<OffsetTime>(this)
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