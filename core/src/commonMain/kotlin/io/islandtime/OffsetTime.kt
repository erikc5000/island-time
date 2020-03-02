package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.parser.throwParserPropertyResolutionException

/**
 * A time of day with an offset from UTC.
 */
class OffsetTime(
    /** The time of day. */
    val time: Time,
    /** The offset from UTC. */
    val offset: UtcOffset
) {

    init {
        offset.validate()
    }

    /**
     * Create an [OffsetTime].
     * @throws DateTimeException if the time or offset is invalid
     */
    constructor(
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanosecond: Int = 0,
        offset: UtcOffset
    ) : this(Time(hour, minute, second, nanosecond), offset)

    /**
     * The hour of the day.
     */
    inline val hour: Int get() = time.hour

    /**
     * The minute of the hour.
     */
    inline val minute: Int get() = time.minute

    /**
     * The second of the minute.
     */
    inline val second: Int get() = time.second

    /**
     * The nanosecond of the second.
     */
    inline val nanosecond: Int get() = time.nanosecond

    /**
     * The number of nanoseconds since the start of the day, but normalized to a UTC offset of zero, allowing
     * [OffsetTime] objects with different offsets to be compared.
     */
    val nanosecondsSinceStartOfUtcDay: LongNanoseconds
        get() = (time.nanosecondsSinceStartOfDay.value - offset.totalSeconds.inNanoseconds.value).nanoseconds

    /**
     * Return an [OffsetTime] with the offset changed to [newOffset], adjusting the time component such that the instant
     * remains the same.
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
    operator fun plus(hours: IntHours) = copy(time = time + hours)
    operator fun plus(minutes: LongMinutes) = copy(time = time + minutes)
    operator fun plus(minutes: IntMinutes) = copy(time = time + minutes)
    operator fun plus(seconds: LongSeconds) = copy(time = time + seconds)
    operator fun plus(seconds: IntSeconds) = copy(time = time + seconds)
    operator fun plus(milliseconds: LongMilliseconds) = copy(time = time + milliseconds)
    operator fun plus(milliseconds: IntMilliseconds) = copy(time = time + milliseconds)
    operator fun plus(microseconds: LongMicroseconds) = copy(time = time + microseconds)
    operator fun plus(microseconds: IntMicroseconds) = copy(time = time + microseconds)
    operator fun plus(nanoseconds: LongNanoseconds) = copy(time = time + nanoseconds)
    operator fun plus(nanoseconds: IntNanoseconds) = copy(time = time + nanoseconds)

    operator fun minus(duration: Duration) = copy(time = time - duration)
    operator fun minus(hours: LongHours) = copy(time = time - hours)
    operator fun minus(hours: IntHours) = copy(time = time - hours)
    operator fun minus(minutes: LongMinutes) = copy(time = time - minutes)
    operator fun minus(minutes: IntMinutes) = copy(time = time - minutes)
    operator fun minus(seconds: LongSeconds) = copy(time = time - seconds)
    operator fun minus(seconds: IntSeconds) = copy(time = time - seconds)
    operator fun minus(milliseconds: LongMilliseconds) = copy(time = time - milliseconds)
    operator fun minus(milliseconds: IntMilliseconds) = copy(time = time - milliseconds)
    operator fun minus(microseconds: LongMicroseconds) = copy(time = time - microseconds)
    operator fun minus(microseconds: IntMicroseconds) = copy(time = time - microseconds)
    operator fun minus(nanoseconds: LongNanoseconds) = copy(time = time - nanoseconds)
    operator fun minus(nanoseconds: IntNanoseconds) = copy(time = time - nanoseconds)

    /**
     * Compare to another [OffsetTime] based on timeline order, ignoring offset differences.
     * @see DEFAULT_SORT_ORDER
     * @see TIMELINE_ORDER
     */
    operator fun compareTo(other: OffsetTime): Int {
        return nanosecondsSinceStartOfUtcDay.compareTo(other.nanosecondsSinceStartOfUtcDay)
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
     * Return a copy of this [OffsetTime], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting time or offset is invalid
     */
    fun copy(
        time: Time = this.time,
        offset: UtcOffset = this.offset
    ) = OffsetTime(time, offset)

    /**
     * Return a copy of this [OffsetTime], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting time or offset is invalid
     */
    fun copy(
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset
    ) = OffsetTime(time.copy(hour, minute, second, nanosecond), offset)

    companion object {
        /**
         * The smallest allowed [OffsetTime] -- `00:00+18:00`.
         */
        val MIN = Time.MIN at UtcOffset.MAX

        /**
         * The largest allowed [OffsetTime] -- `23:59:59.999999999-18:00`.
         */
        val MAX = Time.MAX at UtcOffset.MIN

        /**
         * Compare by UTC equivalent instant, then time. Using this `Comparator` guarantees a deterministic order when
         * sorting.
         */
        val DEFAULT_SORT_ORDER = compareBy<OffsetTime> { it.nanosecondsSinceStartOfUtcDay }.thenBy { it.time }

        /**
         * Compare by timeline order only, ignoring any offset differences.
         */
        val TIMELINE_ORDER = compareBy<OffsetTime> { it.nanosecondsSinceStartOfUtcDay }
    }
}

/**
 * Create an [OffsetTime] by combining a [Time] with a [UtcOffset].
 */
infix fun Time.at(offset: UtcOffset) = OffsetTime(this, offset)

fun String.toOffsetTime() = toOffsetTime(DateTimeParsers.Iso.Extended.OFFSET_TIME)

fun String.toOffsetTime(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): OffsetTime {
    val result = parser.parse(this, settings)
    return result.toOffsetTime() ?: throwParserPropertyResolutionException<OffsetTime>(this)
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