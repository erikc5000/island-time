package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.serialization.OffsetTimeSerializer
import kotlinx.serialization.Serializable

/**
 * A time of day with an offset from UTC.
 * @constructor Creates an [OffsetTime] by combining a [Time] and [UtcOffset].
 * @throws DateTimeException if the offset is invalid
 */
@Serializable(with = OffsetTimeSerializer::class)
class OffsetTime(
    /** The time of day. */
    val time: Time,
    /** The offset from UTC. */
    val offset: UtcOffset
) {

    /**
     * Creates an [OffsetTime].
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
    val nanosecondsSinceStartOfUtcDay: Nanoseconds
        get() = (time.nanosecondsSinceStartOfDay.value - offset.totalSeconds.inNanoseconds.value).nanoseconds

    /**
     * Changes the offset of this [OffsetTime], adjusting the time component such that the instant represented by it
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

    operator fun plus(duration: Duration): OffsetTime = copy(time = time + duration)

    /**
     * Returns this time with [duration] added to it.
     */
    operator fun plus(duration: kotlin.time.Duration): OffsetTime = copy(time = time + duration)

    /**
     * Returns this time with [hours] added to it.
     */
    operator fun plus(hours: Hours): OffsetTime = copy(time = time + hours)

    /**
     * Returns this time with [minutes] added to it.
     */
    operator fun plus(minutes: Minutes): OffsetTime = copy(time = time + minutes)

    /**
     * Returns this time with [seconds] added to it.
     */
    operator fun plus(seconds: Seconds): OffsetTime = copy(time = time + seconds)

    /**
     * Returns this time with [milliseconds] added to it.
     */
    operator fun plus(milliseconds: Milliseconds): OffsetTime = copy(time = time + milliseconds)

    /**
     * Returns this time with [microseconds] added to it.
     */
    operator fun plus(microseconds: Microseconds): OffsetTime = copy(time = time + microseconds)

    /**
     * Returns this time with [nanoseconds] added to it.
     */
    operator fun plus(nanoseconds: Nanoseconds): OffsetTime = copy(time = time + nanoseconds)

    operator fun minus(duration: Duration): OffsetTime = copy(time = time - duration)

    /**
     * Returns this time with [duration] subtracted from it.
     */
    operator fun minus(duration: kotlin.time.Duration): OffsetTime = copy(time = time - duration)

    /**
     * Returns this time with [hours] subtracted from it.
     */
    operator fun minus(hours: Hours): OffsetTime = copy(time = time - hours)

    /**
     * Returns this time with [minutes] subtracted from it.
     */
    operator fun minus(minutes: Minutes): OffsetTime = copy(time = time - minutes)

    /**
     * Returns this time with [seconds] subtracted from it.
     */
    operator fun minus(seconds: Seconds): OffsetTime = copy(time = time - seconds)

    /**
     * Returns this time with [milliseconds] subtracted from it.
     */
    operator fun minus(milliseconds: Milliseconds): OffsetTime = copy(time = time - milliseconds)

    /**
     * Returns this time with [microseconds] subtracted from it.
     */
    operator fun minus(microseconds: Microseconds): OffsetTime = copy(time = time - microseconds)

    /**
     * Returns this time with [nanoseconds] subtracted from it.
     */
    operator fun minus(nanoseconds: Nanoseconds): OffsetTime = copy(time = time - nanoseconds)

    /**
     * Compares to another [OffsetTime] based on timeline order, ignoring offset differences.
     * @see DefaultSortOrder
     * @see TimelineOrder
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

    /**
     * Converts this time to a string in ISO-8601 extended format. For example, `17:31:45.923452091-04:00` or `02:30Z`.
     */
    override fun toString(): String {
        return buildString(MAX_OFFSET_TIME_STRING_LENGTH) { appendOffsetTime(this@OffsetTime) }
    }

    /**
     * Returns a copy of this time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting time or offset is invalid
     */
    fun copy(
        time: Time = this.time,
        offset: UtcOffset = this.offset
    ): OffsetTime = OffsetTime(time, offset)

    /**
     * Returns a copy of this time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting time or offset is invalid
     */
    fun copy(
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset
    ): OffsetTime = OffsetTime(time.copy(hour, minute, second, nanosecond), offset)

    companion object {
        /**
         * The smallest allowed [OffsetTime] -- `00:00+18:00`.
         */
        val MIN: OffsetTime = Time.MIN at UtcOffset.MAX

        /**
         * The largest allowed [OffsetTime] -- `23:59:59.999999999-18:00`.
         */
        val MAX: OffsetTime = Time.MAX at UtcOffset.MIN

        /**
         * A [Comparator] that compares by UTC equivalent instant, then time. Using this `Comparator` guarantees a
         * deterministic order when sorting.
         */
        val DefaultSortOrder: Comparator<OffsetTime> =
            compareBy<OffsetTime> { it.nanosecondsSinceStartOfUtcDay }.thenBy { it.time }

        /**
         * A [Comparator] that compares by timeline order only, ignoring any offset differences.
         */
        val TimelineOrder: Comparator<OffsetTime> = compareBy { it.nanosecondsSinceStartOfUtcDay }

        @Deprecated(
            message = "Replace with DefaultSortOrder",
            replaceWith = ReplaceWith("this.DefaultSortOrder"),
            level = DeprecationLevel.WARNING
        )
        val DEFAULT_SORT_ORDER: Comparator<OffsetTime> get() = DefaultSortOrder

        @Deprecated(
            message = "Replace with TimelineOrder",
            replaceWith = ReplaceWith("this.TimelineOrder"),
            level = DeprecationLevel.WARNING
        )
        val TIMELINE_ORDER: Comparator<OffsetTime> get() = TimelineOrder
    }
}

/**
 * Converts a string to an [OffsetTime].
 *
 * The string is assumed to be an ISO-8601 time with the UTC offset in extended format. For example, `02:30+01:00` or
 * `14:40:23Z`. The output of [OffsetTime.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time or offset is invalid
 */
fun String.toOffsetTime(): OffsetTime = toOffsetTime(DateTimeParsers.Iso.Extended.OFFSET_TIME)

/**
 * Converts a string to an [OffsetTime] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * Any custom parser must be capable of supplying the fields necessary to resolve both a [Time] and [UtcOffset].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time or offset is invalid
 */
fun String.toOffsetTime(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): OffsetTime {
    val result = parser.parse(this, settings)
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
