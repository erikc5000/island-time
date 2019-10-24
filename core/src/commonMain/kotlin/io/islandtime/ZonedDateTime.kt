package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParser
import io.islandtime.parser.DateTimeParsers
import io.islandtime.parser.throwParserFieldResolutionException
import io.islandtime.ranges.ZonedDateTimeInterval

/**
 * A date and time of day in a particular region
 */
class ZonedDateTime private constructor(
    val dateTime: DateTime,
    val offset: UtcOffset,
    val zone: TimeZone
) : TimePoint<ZonedDateTime> {

    inline val date: Date get() = dateTime.date
    inline val time: Time get() = dateTime.time
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
     * Get the year and month of this date
     */
    inline val yearMonth: YearMonth get() = dateTime.yearMonth

    override val secondsSinceUnixEpoch: LongSeconds
        get() = dateTime.secondsSinceUnixEpochAt(offset)

    override val nanoOfSecondsSinceUnixEpoch: IntNanoseconds
        get() = dateTime.nanoOfSecondsSinceUnixEpoch

    override val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = dateTime.millisecondsSinceUnixEpochAt(offset)

    fun toInstant() = Instant.fromUnixEpochSecond(unixEpochSecond, nanosecond)

    override fun equals(other: Any?): Boolean {
        return this === other || (other is ZonedDateTime &&
            dateTime == other.dateTime &&
            zone == other.zone &&
            offset == other.offset)
    }

    override fun hashCode(): Int {
        var result = dateTime.hashCode()
        result = 31 * result + zone.hashCode()
        result = 31 * result + offset.hashCode()
        return result
    }

    override fun toString(): String {
        return buildString(MAX_ZONED_DATE_TIME_STRING_LENGTH) {
            appendZonedDateTime(this@ZonedDateTime)
        }
    }

    /**
     * Return a [ZonedDateTime] with [period] added to it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range. This behavior is consistent with the order of operations for period addition as
     * defined in ISO-8601-2.
     */
    operator fun plus(period: Period) = copy(dateTime = dateTime + period)

    operator fun plus(duration: Duration) = resolveInstant(dateTime + duration)

    operator fun plus(years: IntYears) = copy(dateTime = dateTime + years)
    operator fun plus(years: LongYears) = copy(dateTime = dateTime + years)
    operator fun plus(months: IntMonths) = copy(dateTime = dateTime + months)
    operator fun plus(months: LongMonths) = copy(dateTime = dateTime + months)
    operator fun plus(days: IntDays) = copy(dateTime = dateTime + days)
    operator fun plus(days: LongDays) = copy(dateTime = dateTime + days)
    override operator fun plus(hours: IntHours) = resolveInstant(dateTime + hours)
    override operator fun plus(hours: LongHours) = resolveInstant(dateTime + hours)
    override operator fun plus(minutes: IntMinutes) = resolveInstant(dateTime + minutes)
    override operator fun plus(minutes: LongMinutes) = resolveInstant(dateTime + minutes)
    override operator fun plus(seconds: IntSeconds) = resolveInstant(dateTime + seconds)
    override operator fun plus(seconds: LongSeconds) = resolveInstant(dateTime + seconds)
    override operator fun plus(milliseconds: IntMilliseconds) = resolveInstant(dateTime + milliseconds)
    override operator fun plus(milliseconds: LongMilliseconds) = resolveInstant(dateTime + milliseconds)
    override operator fun plus(microseconds: IntMicroseconds) = resolveInstant(dateTime + microseconds)
    override operator fun plus(microseconds: LongMicroseconds) = resolveInstant(dateTime + microseconds)
    override operator fun plus(nanoseconds: IntNanoseconds) = resolveInstant(dateTime + nanoseconds)
    override operator fun plus(nanoseconds: LongNanoseconds) = resolveInstant(dateTime + nanoseconds)

    /**
     * Return a [ZonedDateTime] with [period] subtracted from it.
     *
     * Years are subtracted first, then months, then days. If the day exceeds the maximum month length at any step, it
     * will be coerced into the valid range. This behavior is consistent with the order of operations for period
     * addition as defined in ISO-8601-2.
     */
    operator fun minus(period: Period) = copy(dateTime = dateTime - period)

    operator fun minus(duration: Duration) = resolveInstant(dateTime - duration)

    operator fun minus(years: IntYears) = copy(dateTime = dateTime - years)
    operator fun minus(years: LongYears) = copy(dateTime = dateTime - years)
    operator fun minus(months: IntMonths) = copy(dateTime = dateTime - months)
    operator fun minus(months: LongMonths) = copy(dateTime = dateTime - months)
    operator fun minus(days: IntDays) = copy(dateTime = dateTime - days)
    operator fun minus(days: LongDays) = copy(dateTime = dateTime - days)
    override operator fun minus(hours: IntHours) = resolveInstant(dateTime - hours)
    override operator fun minus(hours: LongHours) = resolveInstant(dateTime - hours)
    override operator fun minus(minutes: IntMinutes) = resolveInstant(dateTime - minutes)
    override operator fun minus(minutes: LongMinutes) = resolveInstant(dateTime - minutes)
    override operator fun minus(seconds: IntSeconds) = resolveInstant(dateTime - seconds)
    override operator fun minus(seconds: LongSeconds) = resolveInstant(dateTime - seconds)
    override operator fun minus(milliseconds: IntMilliseconds) = resolveInstant(dateTime - milliseconds)
    override operator fun minus(milliseconds: LongMilliseconds) = resolveInstant(dateTime - milliseconds)
    override operator fun minus(microseconds: IntMicroseconds) = resolveInstant(dateTime - microseconds)
    override operator fun minus(microseconds: LongMicroseconds) = resolveInstant(dateTime - microseconds)
    override operator fun minus(nanoseconds: IntNanoseconds) = resolveInstant(dateTime - nanoseconds)
    override operator fun minus(nanoseconds: LongNanoseconds) = resolveInstant(dateTime - nanoseconds)

    operator fun rangeTo(other: ZonedDateTime) = ZonedDateTimeInterval.withInclusiveEnd(this, other)

    /**
     * Return a new [ZonedDateTime], replacing any of the components with new values.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     */
    fun copy(
        dateTime: DateTime = this.dateTime,
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ) = ofLocal(dateTime, zone, offset)

    /**
     * Return a new [ZonedDateTime], replacing any of the components with new values.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     */
    fun copy(
        date: Date = this.date,
        time: Time = this.time,
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ) = ofLocal(dateTime.copy(date, time), zone, offset)

    /**
     * Return a new [ZonedDateTime], replacing any of the components with new values.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ) = ofLocal(
        dateTime.copy(
            date.copy(year, dayOfYear),
            time.copy(hour, minute, second, nanosecond)
        ),
        zone,
        offset
    )

    /**
     * Return a new [ZonedDateTime], replacing any of the components with new values.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     */
    fun copy(
        year: Int = this.year,
        month: Month = this.month,
        dayOfMonth: Int = this.dayOfMonth,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ) = ofLocal(
        dateTime.copy(
            date.copy(year, month, dayOfMonth),
            time.copy(hour, minute, second, nanosecond)
        ),
        zone,
        offset
    )

    /**
     * Return a new [ZonedDateTime], replacing any of the components with new values.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     */
    fun copy(
        year: Int = this.year,
        monthNumber: Int,
        dayOfMonth: Int = this.dayOfMonth,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond,
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ) = ofLocal(
        dateTime.copy(
            date.copy(year, monthNumber, dayOfMonth),
            time.copy(hour, minute, second, nanosecond)
        ),
        zone,
        offset
    )

    /**
     * Truncate to the [hour] value, replacing all smaller components with zero
     */
    fun truncatedToHours() = copy(dateTime = dateTime.truncatedToHours())

    /**
     * Truncate to the [minute] value, replacing all smaller components with zero
     */
    fun truncatedToMinutes() = copy(dateTime = dateTime.truncatedToMinutes())

    /**
     * Truncate to the [second] value, replacing all smaller components with zero
     */
    fun truncatedToSeconds() = copy(dateTime = dateTime.truncatedToSeconds())

    /**
     * Truncate the [nanosecond] value to milliseconds, replacing the rest with zero
     */
    fun truncatedToMilliseconds() = copy(dateTime = dateTime.truncatedToMilliseconds())

    /**
     * Truncate the [nanosecond] value to microseconds, replacing the rest with zero
     */
    fun truncatedToMicroseconds() = copy(dateTime = dateTime.truncatedToMicroseconds())

    fun withEarlierOffsetAtOverlap(): ZonedDateTime {
        val transition = zone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val earlierOffset = transition.offsetBefore

            if (earlierOffset != offset) {
                return ZonedDateTime(dateTime, earlierOffset, zone)
            }
        }
        return this
    }

    fun withLaterOffsetAtOverlap(): ZonedDateTime {
        val transition = zone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val laterOffset = transition.offsetAfter

            if (laterOffset != offset) {
                return ZonedDateTime(dateTime, laterOffset, zone)
            }
        }
        return this
    }

    /**
     * Change the time zone of a [ZonedDateTime], adjusting the date, time, and offset such that the instant
     * represented by it remains the same
     */
    fun adjustedTo(newTimeZone: TimeZone): ZonedDateTime {
        return if (newTimeZone == zone) {
            this
        } else {
            fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, newTimeZone)
        }
    }

    /**
     * Convert to an [OffsetDateTime] representing the current date, time, and offset
     */
    fun toOffsetDateTime() = OffsetDateTime(dateTime, offset)

    private fun resolveInstant(newDateTime: DateTime) = ofInstant(newDateTime, offset, zone)

    companion object {
        val DEFAULT_SORT_ORDER = compareBy<ZonedDateTime> { it.unixEpochSecond }
            .thenBy { it.unixEpochNanoOfSecond }
            .thenBy { it.dateTime }
            .thenBy { it.zone }

        val TIMELINE_ORDER = TimePoint.TIMELINE_ORDER

        fun ofLocal(
            dateTime: DateTime,
            zone: TimeZone,
            preferredOffset: UtcOffset? = null
        ): ZonedDateTime {
            val rules = zone.rules
            val validOffsets = rules.validOffsetsAt(dateTime)

            return when (validOffsets.size) {
                1 -> ZonedDateTime(dateTime, validOffsets[0], zone)
                0 -> {
                    val transition = rules.transitionAt(dateTime)
                    val adjustedDateTime = dateTime + transition!!.duration
                    ZonedDateTime(adjustedDateTime, transition.offsetAfter, zone)
                }
                else -> {
                    val offset = if (preferredOffset != null && validOffsets.contains(preferredOffset)) {
                        preferredOffset
                    } else {
                        validOffsets[0]
                    }
                    ZonedDateTime(dateTime, offset, zone)
                }
            }
        }

        fun ofInstant(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): ZonedDateTime {
            return fromUnixEpochSecond(
                dateTime.unixEpochSecondAt(offset),
                dateTime.nanosecond,
                zone
            )
        }

        fun fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds, zone: TimeZone): ZonedDateTime {
            val offset = zone.rules.offsetAt(milliseconds)
            val dateTime = DateTime.fromMillisecondsSinceUnixEpoch(milliseconds, offset)
            return ZonedDateTime(dateTime, offset, zone)
        }

        fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds,
            zone: TimeZone
        ): ZonedDateTime {
            val offset = zone.rules.offsetAt(seconds, nanosecondAdjustment)
            val dateTime = DateTime.fromSecondsSinceUnixEpoch(seconds, nanosecondAdjustment, offset)
            return ZonedDateTime(dateTime, offset, zone)
        }

        fun fromUnixEpochMillisecond(millisecond: Long, zone: TimeZone): ZonedDateTime {
            return fromMillisecondsSinceUnixEpoch(millisecond.milliseconds, zone)
        }

        fun fromUnixEpochSecond(second: Long, nanoOfSecond: Int, zone: TimeZone): ZonedDateTime {
            return fromSecondsSinceUnixEpoch(second.seconds, nanoOfSecond.nanoseconds, zone)
        }

        /**
         * Create a [ZonedDateTime] with no additional validation
         */
        /**
         * Create a [ZonedDateTime] with no additional validation
         */
        internal fun create(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): ZonedDateTime {
            return ZonedDateTime(dateTime, offset, zone)
        }
    }
}

@Suppress("FunctionName")
fun ZonedDateTime(
    year: Int,
    month: Month,
    day: Int,
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int,
    zone: TimeZone
) = ZonedDateTime.ofLocal(DateTime(year, month, day, hour, minute, second, nanosecond), zone)

@Suppress("FunctionName")
fun ZonedDateTime(
    year: Int,
    monthNumber: Int,
    day: Int,
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int,
    zone: TimeZone
) = ZonedDateTime.ofLocal(DateTime(year, monthNumber, day, hour, minute, second, nanosecond), zone)

@Suppress("FunctionName")
fun ZonedDateTime(
    year: Int,
    dayOfYear: Int,
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int,
    zone: TimeZone
) = ZonedDateTime.ofLocal(DateTime(year, dayOfYear, hour, minute, second, nanosecond), zone)

@Suppress("FunctionName")
fun ZonedDateTime(date: Date, time: Time, zone: TimeZone) = ZonedDateTime.ofLocal(DateTime(date, time), zone)

@Suppress("FunctionName")
fun ZonedDateTime(dateTime: DateTime, zone: TimeZone) = ZonedDateTime.ofLocal(dateTime, zone)

/**
 * Get the [ZonedDateTime] corresponding to the local date and time in a particular time zone.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap, it will adjusted forward by the length of the gap. If it falls within an overlap, the earlier offset will be
 * used.
 */
infix fun DateTime.at(zone: TimeZone) = ZonedDateTime.ofLocal(this, zone, null)

/**
 * Get the [ZonedDateTime] corresponding to a local date, time, and offset in a particular time zone. The offset
 * will be preserved if it is valid based on the rules of the time zone.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap, it will adjusted forward by the length of the gap. If it falls within an overlap, the earlier offset will be
 * used.
 */
fun OffsetDateTime.atSimilarLocalTimeIn(zone: TimeZone): ZonedDateTime {
    return ZonedDateTime.ofLocal(dateTime, zone, offset)
}

/**
 * Get the [ZonedDateTime] corresponding to the same instant represented by an [OffsetDateTime] in a particular
 * time zone
 */
fun OffsetDateTime.atSameInstantIn(zone: TimeZone): ZonedDateTime {
    return ZonedDateTime.ofInstant(dateTime, offset, zone)
}

/**
 * Get the [ZonedDateTime] corresponding to an instant in a particular time zone
 */
infix fun Instant.at(zone: TimeZone) = ZonedDateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, zone)

fun Date.startOfDayAt(zone: TimeZone): ZonedDateTime {
    val dateTime = this at Time.MIDNIGHT
    val transition = zone.rules.transitionAt(dateTime)

    return if (transition?.isGap == true) {
        transition.dateTimeAfter at zone
    } else {
        dateTime at zone
    }
}

fun Date.endOfDayAt(zone: TimeZone): ZonedDateTime {
    val dateTime = this at Time.MAX
    val rules = zone.rules
    val validOffsets = rules.validOffsetsAt(dateTime)

    return if (validOffsets.size == 1) {
        ZonedDateTime.create(dateTime, validOffsets[0], zone)
    } else {
        val transition = rules.transitionAt(dateTime)

        if (validOffsets.isEmpty()) {
            ZonedDateTime.create(transition!!.dateTimeBefore, transition.offsetBefore, zone)
        } else {
            ZonedDateTime.create(dateTime, transition!!.offsetAfter, zone)
        }
    }
}

fun String.toZonedDateTime() = toZonedDateTime(DateTimeParsers.Iso.Extended.ZONED_DATE_TIME)

fun String.toZonedDateTime(parser: DateTimeParser): ZonedDateTime {
    val result = parser.parse(this)
    return result.toZonedDateTime() ?: throwParserFieldResolutionException<ZonedDateTime>(this)
}

internal fun DateTimeParseResult.toZonedDateTime(): ZonedDateTime? {
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()
    val regionId = timeZoneId

    return if (dateTime != null && offset != null && regionId != null) {
        val zone = regionId.toTimeZone().validated()

        // Check if the offset is valid for the time zone as we understand it and if not, interpret it as an instant
        if (!zone.rules.isValidOffset(dateTime, offset)) {
            dateTime.toInstantAt(offset).at(zone)
        } else {
            ZonedDateTime.create(dateTime, offset, zone)
        }
    } else {
        null
    }
}

internal const val MAX_ZONED_DATE_TIME_STRING_LENGTH =
    MAX_DATE_TIME_STRING_LENGTH + MAX_UTC_OFFSET_STRING_LENGTH + MAX_TIME_ZONE_STRING_LENGTH + 2

internal fun StringBuilder.appendZonedDateTime(zonedDateTime: ZonedDateTime): StringBuilder {
    with(zonedDateTime) {
        appendDateTime(dateTime)
        appendUtcOffset(offset)
        append('[')
        append(zone)
        append(']')
    }
    return this
}