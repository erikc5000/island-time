package io.islandtime

import io.islandtime.base.TimePoint
import io.islandtime.internal.deprecatedToError
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.OffsetDateTimeInterval
import io.islandtime.serialization.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable

/**
 * A date and time of day with an offset from UTC.
 *
 * `OffsetDateTime` is intended to be used primarily for use cases involving persistence or network transfer where the
 * application of time zone rules may be undesirable. For most applications, [ZonedDateTime] is a better choice since
 * it takes time zone rules into account when performing calendrical calculations.
 *
 * @constructor Creates an [OffsetDateTime] by combining a [DateTime] and [UtcOffset].
 * @param dateTime the local date and time of day
 * @param offset the offset from UTC
 * @throws DateTimeException if the offset is invalid
 */
@Serializable(with = OffsetDateTimeSerializer::class)
class OffsetDateTime(
    /** The local date and time of day. */
    val dateTime: DateTime,
    /** The offset from UTC. */
    val offset: UtcOffset
) : TimePoint<OffsetDateTime> {

    /**
     * Creates an [OffsetDateTime].
     * @throws DateTimeException if the offset is invalid
     */
    constructor(date: Date, time: Time, offset: UtcOffset) : this(DateTime(date, time), offset)

    /**
     * Creates an [OffsetDateTime].
     * @throws DateTimeException if the date-time or offset is invalid
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
     * Creates an [OffsetDateTime].
     * @throws DateTimeException if the date-time or offset is invalid
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
    ) : this(DateTime(year, monthNumber.toMonth(), dayOfMonth, hour, minute, second, nanosecond), offset)

    /**
     * Creates an [OffsetDateTime].
     * @throws DateTimeException if the date-time or offset is invalid
     */
    constructor(
        year: Int,
        dayOfYear: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nanosecond: Int,
        offset: UtcOffset
    ) : this(DateTime(year, dayOfYear, hour, minute, second, nanosecond), offset)

    /**
     * The local date.
     */
    inline val date: Date get() = dateTime.date

    /**
     * The local time of day.
     */
    inline val time: Time get() = dateTime.time

    /**
     * The hour of the day.
     */
    inline val hour: Int get() = dateTime.hour

    /**
     * The minute of the hour.
     */
    inline val minute: Int get() = dateTime.minute

    /**
     * The second of the minute.
     */
    inline val second: Int get() = dateTime.second

    /**
     * The nanosecond of the second.
     */
    override val nanosecond: Int get() = dateTime.nanosecond

    /**
     * The month of the year.
     */
    inline val month: Month get() = dateTime.month

    /**
     * The ISO month number, from 1-12.
     */
    inline val monthNumber: Int get() = month.number

    /**
     * The day of the week.
     */
    inline val dayOfWeek: DayOfWeek get() = dateTime.dayOfWeek

    /**
     * The day of the month.
     */
    inline val dayOfMonth: Int get() = dateTime.dayOfMonth

    /**
     * The day of the year.
     */
    inline val dayOfYear: Int get() = dateTime.dayOfYear

    /**
     * The year.
     */
    inline val year: Int get() = dateTime.year

    @Deprecated(
        "Use toYearMonth() instead.",
        ReplaceWith("this.toYearMonth()"),
        DeprecationLevel.ERROR
    )
    inline val yearMonth: YearMonth
        get() = toYearMonth()

    @Deprecated(
        "Use toOffsetTime() instead.",
        ReplaceWith("this.toOffsetTime()"),
        DeprecationLevel.ERROR
    )
    inline val offsetTime: OffsetTime
        get() = toOffsetTime()

    @Deprecated(
        "Use toInstant() instead.",
        ReplaceWith("this.toInstant()"),
        DeprecationLevel.ERROR
    )
    inline val instant: Instant
        get() = toInstant()

    override val secondOfUnixEpoch: Long
        get() = dateTime.secondOfUnixEpochAt(offset)

    override val millisecondOfUnixEpoch: Long
        get() = dateTime.millisecondOfUnixEpochAt(offset)

    /**
     * Returns this date-time with a new UTC offset, adjusting the date and time components such that the instant
     * represented by it remains the same.
     */
    fun adjustedTo(newOffset: UtcOffset): OffsetDateTime {
        return if (newOffset == offset) {
            this
        } else {
            val newDateTime = dateTime + (newOffset.totalSeconds - offset.totalSeconds)
            OffsetDateTime(newDateTime, newOffset)
        }
    }

    /**
     * Returns this date-time with [period] added to it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range.
     */
    operator fun plus(period: Period): OffsetDateTime = copy(dateTime = dateTime + period)

    operator fun plus(duration: Duration): OffsetDateTime = copy(dateTime = dateTime + duration)

    /**
     * Returns this date-time with [duration] added to it.
     */
    @kotlin.time.ExperimentalTime
    operator fun plus(duration: kotlin.time.Duration): OffsetDateTime = copy(dateTime = dateTime + duration)

    /**
     * Returns this date-tme with [centuries] added to it.
     */
    operator fun plus(centuries: Centuries): OffsetDateTime = copy(dateTime = dateTime + centuries)

    /**
     * Returns this date-time with [decades] added to it.
     */
    operator fun plus(decades: Decades): OffsetDateTime = copy(dateTime = dateTime + decades)

    /**
     * Returns this date-time with [years] added to it.
     */
    operator fun plus(years: Years): OffsetDateTime = copy(dateTime = dateTime + years)

    /**
     * Returns this date-time with [months] added to it.
     */
    operator fun plus(months: Months): OffsetDateTime = copy(dateTime = dateTime + months)

    /**
     * Returns this date-time with [weeks] added to it.
     */
    operator fun plus(weeks: Weeks): OffsetDateTime = copy(dateTime = dateTime + weeks)

    /**
     * Returns this date-time with [days] added to it.
     */
    operator fun plus(days: Days): OffsetDateTime = copy(dateTime = dateTime + days)

    /**
     * Returns this date-time with [hours] added to it.
     */
    override operator fun plus(hours: Hours): OffsetDateTime = copy(dateTime = dateTime + hours)

    /**
     * Returns this date-time with [minutes] added to it.
     */
    override operator fun plus(minutes: Minutes): OffsetDateTime = copy(dateTime = dateTime + minutes)

    /**
     * Returns this date-time with [seconds] added to it.
     */
    override operator fun plus(seconds: Seconds): OffsetDateTime = copy(dateTime = dateTime + seconds)

    /**
     * Returns this date-time with [milliseconds] added to it.
     */
    override operator fun plus(milliseconds: Milliseconds): OffsetDateTime = copy(dateTime = dateTime + milliseconds)

    /**
     * Returns this date-time with [microseconds] added to it.
     */
    override operator fun plus(microseconds: Microseconds): OffsetDateTime = copy(dateTime = dateTime + microseconds)

    /**
     * Returns this date-time with [nanoseconds] added to it.
     */
    override operator fun plus(nanoseconds: Nanoseconds): OffsetDateTime = copy(dateTime = dateTime + nanoseconds)

    /**
     * Returns this date-time with [period] subtracted from it.
     *
     * Years are subtracted first, then months, then days. If the day exceeds the maximum month length at any step, it
     * will be coerced into the valid range.
     */
    operator fun minus(period: Period): OffsetDateTime = copy(dateTime = dateTime - period)

    operator fun minus(duration: Duration): OffsetDateTime = copy(dateTime = dateTime - duration)

    /**
     * Returns this date-time with [duration] subtracted from it.
     */
    @kotlin.time.ExperimentalTime
    operator fun minus(duration: kotlin.time.Duration): OffsetDateTime = copy(dateTime = dateTime - duration)

    /**
     * Returns this date-time with [centuries] subtracted from it.
     */
    operator fun minus(centuries: Centuries): OffsetDateTime = copy(dateTime = dateTime - centuries)

    /**
     * Returns this date-time with [decades] subtracted from it.
     */
    operator fun minus(decades: Decades): OffsetDateTime = copy(dateTime = dateTime - decades)

    /**
     * Returns this date-time with [years] subtracted from it.
     */
    operator fun minus(years: Years): OffsetDateTime = copy(dateTime = dateTime - years)

    /**
     * Returns this date-time with [months] subtracted from it.
     */
    operator fun minus(months: Months): OffsetDateTime = copy(dateTime = dateTime - months)

    /**
     * Returns this date-time with [weeks] subtracted from it.
     */
    operator fun minus(weeks: Weeks): OffsetDateTime = copy(dateTime = dateTime - weeks)

    /**
     * Returns this date-time with [days] subtracted from it.
     */
    operator fun minus(days: Days): OffsetDateTime = copy(dateTime = dateTime - days)

    /**
     * Returns this date-time with [hours] subtracted from it.
     */
    override operator fun minus(hours: Hours): OffsetDateTime = copy(dateTime = dateTime - hours)

    /**
     * Returns this date-time with [minutes] subtracted from it.
     */
    override operator fun minus(minutes: Minutes): OffsetDateTime = copy(dateTime = dateTime - minutes)

    /**
     * Returns this date-time with [seconds] subtracted from it.
     */
    override operator fun minus(seconds: Seconds): OffsetDateTime = copy(dateTime = dateTime - seconds)

    /**
     * Returns this date-time with [milliseconds] subtracted from it.
     */
    override operator fun minus(milliseconds: Milliseconds): OffsetDateTime = copy(dateTime = dateTime - milliseconds)

    /**
     * Returns this date-time with [microseconds] subtracted from it.
     */
    override operator fun minus(microseconds: Microseconds): OffsetDateTime = copy(dateTime = dateTime - microseconds)

    /**
     * Returns this date-time with [nanoseconds] subtracted from it.
     */
    override operator fun minus(nanoseconds: Nanoseconds): OffsetDateTime = copy(dateTime = dateTime - nanoseconds)

    operator fun rangeTo(other: OffsetDateTime): OffsetDateTimeInterval =
        OffsetDateTimeInterval.withInclusiveEnd(this, other)

    /**
     * Converts this date-time to a string in ISO-8601 extended format. For example,
     * `2012-04-15T17:31:45.923452091-04:00` or `2020-02-13T02:30Z`.
     */
    override fun toString(): String = buildString(MAX_OFFSET_DATE_TIME_STRING_LENGTH) {
        appendOffsetDateTime(this@OffsetDateTime)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is OffsetDateTime && dateTime == other.dateTime && offset == other.offset)
    }

    override fun hashCode(): Int {
        return 31 * dateTime.hashCode() + offset.hashCode()
    }

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        dateTime: DateTime = this.dateTime,
        offset: UtcOffset = this.offset
    ): OffsetDateTime = OffsetDateTime(dateTime, offset)

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        date: Date = this.date,
        time: Time = this.time,
        offset: UtcOffset = this.offset
    ): OffsetDateTime = OffsetDateTime(date, time, offset)

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset
    ): OffsetDateTime = OffsetDateTime(date.copy(year, dayOfYear), time.copy(hour, minute, second, nanosecond), offset)

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting date-time is invalid
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
    ): OffsetDateTime = OffsetDateTime(
        date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanosecond),
        offset
    )

    companion object {
        /**
         * The earliest supported [OffsetDateTime], which can be used as a "far past" sentinel.
         */
        val MIN: OffsetDateTime = DateTime.MIN at UtcOffset.MAX

        /**
         * The latest supported [OffsetDateTime], which can be used as a "far future" sentinel.
         */
        val MAX: OffsetDateTime = DateTime.MAX at UtcOffset.MIN

        /**
         * A [Comparator] that compares by instant, then date-time. Using this `Comparator` guarantees a deterministic
         * order when sorting.
         */
        val DefaultSortOrder: Comparator<OffsetDateTime> = compareBy<OffsetDateTime> { it.secondOfUnixEpoch }
            .thenBy { it.nanosecond }
            .thenBy { it.dateTime }

        /**
         * A [Comparator] that compares by timeline order only, ignoring any offset differences.
         */
        val TimelineOrder: Comparator<TimePoint<*>> get() = TimePoint.TimelineOrder

        @Deprecated(
            message = "Replace with DefaultSortOrder",
            replaceWith = ReplaceWith("this.DefaultSortOrder"),
            level = DeprecationLevel.WARNING
        )
        val DEFAULT_SORT_ORDER: Comparator<OffsetDateTime> get() = DefaultSortOrder

        @Deprecated(
            message = "Replace with TimelineOrder",
            replaceWith = ReplaceWith("this.TimelineOrder"),
            level = DeprecationLevel.WARNING
        )
        val TIMELINE_ORDER: Comparator<TimePoint<*>> get() = TimelineOrder

        /**
         * Creates an [OffsetDateTime] from a duration of milliseconds relative to the Unix epoch at [offset].
         */
        fun fromMillisecondsSinceUnixEpoch(milliseconds: Milliseconds, offset: UtcOffset): OffsetDateTime {
            return OffsetDateTime(DateTime.fromMillisecondsSinceUnixEpoch(milliseconds, offset), offset)
        }

        /**
         * Creates an [OffsetDateTime] from a duration of seconds relative to the Unix epoch at [offset], optionally,
         * with some number of additional nanoseconds added to it.
         */
        fun fromSecondsSinceUnixEpoch(
            seconds: Seconds,
            nanosecondAdjustment: Nanoseconds = 0.nanoseconds,
            offset: UtcOffset
        ): OffsetDateTime {
            return OffsetDateTime(DateTime.fromSecondsSinceUnixEpoch(seconds, nanosecondAdjustment, offset), offset)
        }

        /**
         * Creates an [OffsetDateTime] from the millisecond of the Unix epoch at [offset].
         */
        fun fromMillisecondOfUnixEpoch(millisecond: Long, offset: UtcOffset): OffsetDateTime {
            return OffsetDateTime(DateTime.fromMillisecondOfUnixEpoch(millisecond, offset), offset)
        }

        /**
         * Creates an [OffsetDateTime] from the second of the Unix epoch at [offset] and optionally, the nanosecond of
         * the second.
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Int = 0, offset: UtcOffset): OffsetDateTime {
            return OffsetDateTime(DateTime.fromSecondOfUnixEpoch(second, nanosecond, offset), offset)
        }

        @Deprecated(
            "Use fromMillisecondOfUnixEpoch() instead.",
            ReplaceWith("OffsetDateTime.fromMillisecondOfUnixEpoch(millisecond, offset)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER", "unused")
        fun fromUnixEpochMillisecond(millisecond: Long, offset: UtcOffset): OffsetDateTime = deprecatedToError()

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("OffsetDateTime.fromSecondOfUnixEpoch(second, nanoOfSecond, offset)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER", "unused")
        fun fromUnixEpochSecond(second: Long, nanoOfSecond: Int, offset: UtcOffset): OffsetDateTime =
            deprecatedToError()
    }
}

/**
 * Converts a string to an [OffsetDateTime].
 *
 * The string is assumed to be an ISO-8601 date-time with the UTC offset in extended format. For example,
 * `2019-05-30T02:30+01:00`. The output of [OffsetDateTime.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date-time or offset is invalid
 */
fun String.toOffsetDateTime(): OffsetDateTime = toOffsetDateTime(DateTimeParsers.Iso.Extended.OFFSET_DATE_TIME)

/**
 * Converts a string to an [OffsetDateTime] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * Any custom parser must be capable of supplying the fields necessary to resolve a [Date], [Time] and [UtcOffset].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date-time or offset is invalid
 */
fun String.toOffsetDateTime(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): OffsetDateTime {
    val result = parser.parse(this, settings)
    return result.toOffsetDateTime() ?: throwParserFieldResolutionException<OffsetDateTime>(this)
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

@Deprecated(
    "Use 'toOffsetDateTime()' instead.",
    ReplaceWith("this.toOffsetDateTime()"),
    DeprecationLevel.ERROR
)
@Suppress("unused")
fun ZonedDateTime.asOffsetDateTime(): OffsetDateTime = deprecatedToError()
