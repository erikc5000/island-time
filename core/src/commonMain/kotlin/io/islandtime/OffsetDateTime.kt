package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParser
import io.islandtime.parser.Iso8601
import io.islandtime.parser.raiseParserFieldResolutionException
import io.islandtime.ranges.OffsetDateTimeInterval

/**
 * A date and time of day combined with a fixed UTC offset
 */
class OffsetDateTime(
    val dateTime: DateTime,
    val offset: UtcOffset
) : TimePoint<OffsetDateTime> {

    /**
     * Create an [OffsetDateTime]
     */
    constructor(date: Date, time: Time, offset: UtcOffset) : this(DateTime(date, time), offset)

    /**
     * Create an [OffsetDateTime]
     */
    constructor(
        year: Int,
        month: Month,
        dayOfMonth: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nanosecond: Int,
        offset: UtcOffset
    ) : this(DateTime(year, month, dayOfMonth, hour, minute, second, nanosecond), offset)

    /**
     * Create an [OffsetDateTime]
     */
    constructor(
        year: Int,
        monthNumber: Int,
        dayOfMonth: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nanosecond: Int,
        offset: UtcOffset
    ) : this(DateTime(year, Month(monthNumber), dayOfMonth, hour, minute, second, nanosecond), offset)

    inline val date: Date get() = dateTime.date
    inline val time: Time get() = dateTime.time

    /**
     * Get the [OffsetTime] representing the current time and offset
     */
    inline val offsetTime: OffsetTime get() = OffsetTime(time, offset)

    inline val hour: Int get() = dateTime.hour
    inline val minute: Int get() = dateTime.minute
    inline val second: Int get() = dateTime.second
    inline val nanosecond: Int get() = dateTime.nanosecond
    inline val month: Month get() = dateTime.month

    /**
     * The ISO month number
     */
    inline val monthNumber: Int get() = month.number

    inline val dayOfWeek: DayOfWeek get() = dateTime.dayOfWeek
    inline val dayOfMonth: Int get() = dateTime.dayOfMonth
    inline val dayOfYear: Int get() = dateTime.dayOfYear
    inline val year: Int get() = dateTime.year
    inline val isInLeapYear: Boolean get() = dateTime.isInLeapYear
    inline val isLeapDay: Boolean get() = dateTime.isLeapDay
    inline val lengthOfMonth: IntDays get() = dateTime.lengthOfMonth
    inline val lengthOfYear: IntDays get() = dateTime.lengthOfYear

    /**
     * Get the year month of this date
     */
    inline val yearMonth: YearMonth get() = dateTime.yearMonth

    override val secondsSinceUnixEpoch: LongSeconds
        get() = dateTime.secondsSinceUnixEpochAt(offset)

    override val nanoOfSecondsSinceUnixEpoch: IntNanoseconds
        get() = nanosecond.nanoseconds

    override val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = dateTime.millisecondsSinceUnixEpochAt(offset)

    inline val instant: Instant get() = Instant.fromUnixEpochSecond(unixEpochSecond, nanosecond)

    /**
     * Change the offset of an [OffsetDateTime], adjusting the date and time components such that the instant
     * represented by it remains the same
     */
    fun adjustedTo(newOffset: UtcOffset): OffsetDateTime {
        return if (newOffset == offset) {
            this
        } else {
            val newDateTime = dateTime + (newOffset.totalSeconds - offset.totalSeconds)
            OffsetDateTime(newDateTime, newOffset)
        }
    }

    operator fun plus(years: LongYears) = copy(dateTime = dateTime + years)
    operator fun plus(years: IntYears) = plus(years.toLong())
    operator fun plus(months: LongMonths) = copy(dateTime = dateTime + months)
    operator fun plus(months: IntMonths) = plus(months.toLong())
    operator fun plus(days: LongDays) = copy(dateTime = dateTime + days)
    operator fun plus(days: IntDays) = plus(days.toLong())
    override operator fun plus(hours: LongHours) = copy(dateTime = dateTime + hours)
    override operator fun plus(hours: IntHours) = plus(hours.toLong())
    override operator fun plus(minutes: LongMinutes) = copy(dateTime = dateTime + minutes)
    override operator fun plus(minutes: IntMinutes) = plus(minutes.toLong())
    override operator fun plus(seconds: LongSeconds) = copy(dateTime = dateTime + seconds)
    override operator fun plus(seconds: IntSeconds) = plus(seconds.toLong())
    override operator fun plus(milliseconds: LongMilliseconds) = copy(dateTime = dateTime + milliseconds)
    override operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.toLong())
    override operator fun plus(microseconds: LongMicroseconds) = copy(dateTime = dateTime + microseconds)
    override operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.toLong())
    override operator fun plus(nanoseconds: LongNanoseconds) = copy(dateTime = dateTime + nanoseconds)
    override operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLong())

    operator fun minus(years: LongYears) = copy(dateTime = dateTime - years)
    operator fun minus(years: IntYears) = minus(years.toLong())
    operator fun minus(months: LongMonths) = copy(dateTime = dateTime - months)
    operator fun minus(months: IntMonths) = minus(months.toLong())
    operator fun minus(days: LongDays) = copy(dateTime = dateTime - days)
    operator fun minus(days: IntDays) = minus(days.toLong())
    override operator fun minus(hours: LongHours) = copy(dateTime = dateTime - hours)
    override operator fun minus(hours: IntHours) = minus(hours.toLong())
    override operator fun minus(minutes: LongMinutes) = copy(dateTime = dateTime - minutes)
    override operator fun minus(minutes: IntMinutes) = minus(minutes.toLong())
    override operator fun minus(seconds: LongSeconds) = copy(dateTime = dateTime - seconds)
    override operator fun minus(seconds: IntSeconds) = minus(seconds.toLong())
    override operator fun minus(milliseconds: LongMilliseconds) = copy(dateTime = dateTime - milliseconds)
    override operator fun minus(milliseconds: IntMilliseconds) = minus(milliseconds.toLong())
    override operator fun minus(microseconds: LongMicroseconds) = copy(dateTime = dateTime - microseconds)
    override operator fun minus(microseconds: IntMicroseconds) = minus(microseconds.toLong())
    override operator fun minus(nanoseconds: LongNanoseconds) = copy(dateTime = dateTime - nanoseconds)
    override operator fun minus(nanoseconds: IntNanoseconds) = minus(nanoseconds.toLong())

    operator fun rangeTo(other: OffsetDateTime) = OffsetDateTimeInterval.withInclusiveEnd(this, other)

    operator fun component1() = dateTime
    operator fun component2() = offset

    override fun toString() = buildString(MAX_OFFSET_DATE_TIME_STRING_LENGTH) {
        appendOffsetDateTime(this@OffsetDateTime)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is OffsetDateTime && dateTime == other.dateTime && offset == other.offset)
    }

    override fun hashCode(): Int {
        return 31 * dateTime.hashCode() + offset.hashCode()
    }

    /**
     * Return a new [OffsetDateTime], replacing any of the components with new values
     */
    fun copy(
        dateTime: DateTime = this.dateTime,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(dateTime, offset)

    /**
     * Return a new [OffsetDateTime], replacing any of the components with new values
     */
    fun copy(
        date: Date = this.date,
        time: Time = this.time,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(date, time, offset)

    /**
     * Return a new [OffsetDateTime], replacing any of the components with new values
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(date.copy(year, dayOfYear), time.copy(hour, minute, second, nanosecond), offset)

    /**
     * Return a new OffsetDateTime, replacing any of the components with new values
     */
    fun copy(
        year: Int = this.year,
        month: Month = this.month,
        dayOfMonth: Int = this.dayOfMonth,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanosecond), offset)

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
        val MIN = DateTime.MIN at UtcOffset.MAX
        val MAX = DateTime.MAX at UtcOffset.MIN

        val DEFAULT_SORT_ORDER = compareBy<OffsetDateTime> { it.unixEpochSecond }
            .thenBy { it.unixEpochNanoOfSecond }
            .thenBy { it.dateTime }

        val TIMELINE_ORDER = TimePoint.TIMELINE_ORDER

        fun fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds, offset: UtcOffset): OffsetDateTime {
            return OffsetDateTime(
                DateTime.fromMillisecondsSinceUnixEpoch(milliseconds, offset),
                offset
            )
        }

        fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds,
            offset: UtcOffset
        ): OffsetDateTime {
            return OffsetDateTime(
                DateTime.fromSecondsSinceUnixEpoch(seconds, nanosecondAdjustment, offset),
                offset
            )
        }

        fun fromUnixEpochMillisecond(millisecond: Long, offset: UtcOffset): OffsetDateTime {
            return OffsetDateTime(
                DateTime.fromUnixEpochMillisecond(millisecond, offset),
                offset
            )
        }

        fun fromUnixEpochSecond(second: Long, nanoOfSecond: Int, offset: UtcOffset): OffsetDateTime {
            return OffsetDateTime(
                DateTime.fromUnixEpochSecond(second, nanoOfSecond, offset),
                offset
            )
        }
    }
}

infix fun DateTime.at(offset: UtcOffset) = OffsetDateTime(this, offset)
infix fun Date.at(offsetTime: OffsetTime) = OffsetDateTime(this, offsetTime.time, offsetTime.offset)
infix fun Instant.at(offset: UtcOffset) = OffsetDateTime(this.toDateTimeAt(offset), offset)

fun String.toOffsetDateTime() = toOffsetDateTime(Iso8601.Extended.OFFSET_DATE_TIME_PARSER)

fun String.toOffsetDateTime(parser: DateTimeParser): OffsetDateTime {
    val result = parser.parse(this)
    return result.toOffsetDateTime() ?: raiseParserFieldResolutionException("OffsetDateTime", this)
}

internal fun DateTimeParseResult.toOffsetDateTime(): OffsetDateTime? {
    val dateTime = this.toDateTime()
    val utcOffset = this.toUtcOffset()

    return if (dateTime != null && utcOffset != null) {
        OffsetDateTime(dateTime, utcOffset)
    } else {
        null
    }
}

internal const val MAX_OFFSET_DATE_TIME_STRING_LENGTH = MAX_DATE_TIME_STRING_LENGTH + MAX_UTC_OFFSET_STRING_LENGTH

internal fun StringBuilder.appendOffsetDateTime(offsetDateTime: OffsetDateTime): StringBuilder {
    with(offsetDateTime) {
        appendDateTime(dateTime)
        appendUtcOffset(offset)
    }
    return this
}