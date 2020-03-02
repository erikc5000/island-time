package io.islandtime

import io.islandtime.base.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.parser.throwParserPropertyResolutionException
import io.islandtime.ranges.OffsetDateTimeInterval

/**
 * A date and time of day with an offset from UTC.
 *
 * `OffsetDateTime` is intended to be used primarily for use cases involving persistence or network transfer where the
 * application of time zone rules may be undesirable. For most applications, [ZonedDateTime] is a better choice since
 * it takes time zone rules into account when performing calendrical calculations.
 *
 * @constructor Create an [OffsetDateTime] by combining a [DateTime] and [UtcOffset].
 * @param dateTime the local date and time of day
 * @param offset the offset from UTC
 * @throws DateTimeException if the offset is invalid
 */
class OffsetDateTime(
    /** The local date and time of day. */
    val dateTime: DateTime,
    /** The offset from UTC. */
    val offset: UtcOffset
) : TimePoint<OffsetDateTime> {

    init {
        offset.validate()
    }

    /**
     * Create an [OffsetDateTime].
     * @throws DateTimeException if the offset is invalid
     */
    constructor(date: Date, time: Time, offset: UtcOffset) : this(DateTime(date, time), offset)

    /**
     * Create an [OffsetDateTime].
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
     * Create an [OffsetDateTime].
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
     * Create an [OffsetDateTime].
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
    inline val nanosecond: Int get() = dateTime.nanosecond

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
     * The day of the year -- also known as the ordinal date in ISO-8601.
     */
    inline val dayOfYear: Int get() = dateTime.dayOfYear

    /**
     * The year.
     */
    inline val year: Int get() = dateTime.year

    /**
     * Check if this date falls within a leap year.
     */
    inline val isInLeapYear: Boolean get() = dateTime.isInLeapYear

    /**
     * Check if this is a leap day.
     */
    inline val isLeapDay: Boolean get() = dateTime.isLeapDay

    /**
     * The length of this date's month in days.
     */
    inline val lengthOfMonth: IntDays get() = dateTime.lengthOfMonth

    /**
     * The length of this date's year in days.
     */
    inline val lengthOfYear: IntDays get() = dateTime.lengthOfYear

    /**
     * The combined year and month.
     */
    inline val yearMonth: YearMonth get() = dateTime.yearMonth

    /**
     * The combined time of day and offset.
     */
    inline val offsetTime: OffsetTime get() = OffsetTime(time, offset)

    /**
     * The [Instant] representing the same time point.
     */
    inline val instant: Instant get() = Instant.fromUnixEpochSecond(unixEpochSecond, nanosecond)

    override val secondsSinceUnixEpoch: LongSeconds
        get() = dateTime.secondsSinceUnixEpochAt(offset)

    override val nanoOfSecondsSinceUnixEpoch: IntNanoseconds
        get() = dateTime.nanoOfSecondsSinceUnixEpoch

    override val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = dateTime.millisecondsSinceUnixEpochAt(offset)

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

    /**
     * Return an [OffsetDateTime] with [period] added to it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range. This behavior is consistent with the order of operations for period addition as
     * defined in ISO-8601-2.
     */
    operator fun plus(period: Period) = copy(dateTime = dateTime + period)

    operator fun plus(duration: Duration) = copy(dateTime = dateTime + duration)

    operator fun plus(years: LongYears) = copy(dateTime = dateTime + years)
    operator fun plus(years: IntYears) = copy(dateTime = dateTime + years)
    operator fun plus(months: LongMonths) = copy(dateTime = dateTime + months)
    operator fun plus(months: IntMonths) = copy(dateTime = dateTime + months)
    operator fun plus(weeks: LongWeeks) = copy(dateTime = dateTime + weeks)
    operator fun plus(weeks: IntWeeks) = copy(dateTime = dateTime + weeks)
    operator fun plus(days: LongDays) = copy(dateTime = dateTime + days)
    operator fun plus(days: IntDays) = copy(dateTime = dateTime + days)
    override operator fun plus(hours: LongHours) = copy(dateTime = dateTime + hours)
    override operator fun plus(hours: IntHours) = copy(dateTime = dateTime + hours)
    override operator fun plus(minutes: LongMinutes) = copy(dateTime = dateTime + minutes)
    override operator fun plus(minutes: IntMinutes) = copy(dateTime = dateTime + minutes)
    override operator fun plus(seconds: LongSeconds) = copy(dateTime = dateTime + seconds)
    override operator fun plus(seconds: IntSeconds) = copy(dateTime = dateTime + seconds)
    override operator fun plus(milliseconds: LongMilliseconds) = copy(dateTime = dateTime + milliseconds)
    override operator fun plus(milliseconds: IntMilliseconds) = copy(dateTime = dateTime + milliseconds)
    override operator fun plus(microseconds: LongMicroseconds) = copy(dateTime = dateTime + microseconds)
    override operator fun plus(microseconds: IntMicroseconds) = copy(dateTime = dateTime + microseconds)
    override operator fun plus(nanoseconds: LongNanoseconds) = copy(dateTime = dateTime + nanoseconds)
    override operator fun plus(nanoseconds: IntNanoseconds) = copy(dateTime = dateTime + nanoseconds)

    /**
     * Return an [OffsetDateTime] with [period] subtracted from it.
     *
     * Years are subtracted first, then months, then days. If the day exceeds the maximum month length at any step, it
     * will be coerced into the valid range. This behavior is consistent with the order of operations for period
     * addition as defined in ISO-8601-2.
     */
    operator fun minus(period: Period) = copy(dateTime = dateTime - period)

    operator fun minus(duration: Duration) = copy(dateTime = dateTime - duration)

    operator fun minus(years: LongYears) = copy(dateTime = dateTime - years)
    operator fun minus(years: IntYears) = copy(dateTime = dateTime - years)
    operator fun minus(months: LongMonths) = copy(dateTime = dateTime - months)
    operator fun minus(months: IntMonths) = copy(dateTime = dateTime - months)
    operator fun minus(weeks: LongWeeks) = copy(dateTime = dateTime - weeks)
    operator fun minus(weeks: IntWeeks) = copy(dateTime = dateTime - weeks)
    operator fun minus(days: LongDays) = copy(dateTime = dateTime - days)
    operator fun minus(days: IntDays) = copy(dateTime = dateTime - days)
    override operator fun minus(hours: LongHours) = copy(dateTime = dateTime - hours)
    override operator fun minus(hours: IntHours) = copy(dateTime = dateTime - hours)
    override operator fun minus(minutes: LongMinutes) = copy(dateTime = dateTime - minutes)
    override operator fun minus(minutes: IntMinutes) = copy(dateTime = dateTime - minutes)
    override operator fun minus(seconds: LongSeconds) = copy(dateTime = dateTime - seconds)
    override operator fun minus(seconds: IntSeconds) = copy(dateTime = dateTime - seconds)
    override operator fun minus(milliseconds: LongMilliseconds) = copy(dateTime = dateTime - milliseconds)
    override operator fun minus(milliseconds: IntMilliseconds) = copy(dateTime = dateTime - milliseconds)
    override operator fun minus(microseconds: LongMicroseconds) = copy(dateTime = dateTime - microseconds)
    override operator fun minus(microseconds: IntMicroseconds) = copy(dateTime = dateTime - microseconds)
    override operator fun minus(nanoseconds: LongNanoseconds) = copy(dateTime = dateTime - nanoseconds)
    override operator fun minus(nanoseconds: IntNanoseconds) = copy(dateTime = dateTime - nanoseconds)

    operator fun rangeTo(other: OffsetDateTime) = OffsetDateTimeInterval.withInclusiveEnd(this, other)

    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            is DateProperty,
            is TimeProperty,
            is UtcOffsetProperty -> true
            else -> super.has(property)
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            is DateProperty, is TimeProperty -> dateTime.get(property)
            is UtcOffsetProperty -> offset.get(property)
            else -> super.get(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            is DateProperty, is TimeProperty -> dateTime.get(property)
            is UtcOffsetProperty -> offset.get(property)
            else -> super.get(property)
        }
    }

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
     * Return a new [OffsetDateTime], replacing any of the components with new values.
     */
    fun copy(
        dateTime: DateTime = this.dateTime,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(dateTime, offset)

    /**
     * Return a new [OffsetDateTime], replacing any of the components with new values.
     */
    fun copy(
        date: Date = this.date,
        time: Time = this.time,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(date, time, offset)

    /**
     * Return a new [OffsetDateTime], replacing any of the components with new values.
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
     * Return a new [OffsetDateTime], replacing any of the components with new values.
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

    companion object {
        val MIN = DateTime.MIN at UtcOffset.MAX
        val MAX = DateTime.MAX at UtcOffset.MIN

        /**
         * Compare by instant, then date-time. Using this `Comparator` guarantees a deterministic order when sorting.
         */
        val DEFAULT_SORT_ORDER = compareBy<OffsetDateTime> { it.unixEpochSecond }
            .thenBy { it.unixEpochNanoOfSecond }
            .thenBy { it.dateTime }

        /**
         * Compare by timeline order only, ignoring any offset differences.
         */
        val TIMELINE_ORDER get() = TimePoint.TIMELINE_ORDER

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

/**
 * Convert to an [OffsetDateTime] with the same date, time of day, and offset.
 *
 * While similar to `ZonedDateTime`, an `OffsetDateTime` representation is unaffected by time zone rule changes or
 * database differences between systems, making it better suited for use cases involving persistence or network
 * transfer.
 */
fun ZonedDateTime.asOffsetDateTime() = OffsetDateTime(dateTime, offset)

fun String.toOffsetDateTime() = toOffsetDateTime(DateTimeParsers.Iso.Extended.OFFSET_DATE_TIME)

fun String.toOffsetDateTime(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): OffsetDateTime {
    val result = parser.parse(this, settings)
    return result.toOffsetDateTime() ?: throwParserPropertyResolutionException<OffsetDateTime>(this)
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