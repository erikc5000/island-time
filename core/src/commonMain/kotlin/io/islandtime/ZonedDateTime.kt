@file:Suppress("FunctionName")

package io.islandtime

import io.islandtime.base.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.ZonedDateTimeInterval

/**
 * A date and time of day in a particular region.
 *
 * `ZonedDateTime` takes time zone rules into account when performing calendrical calculations.
 */
class ZonedDateTime private constructor(
    /** The local date and time of day. */
    val dateTime: DateTime,
    /** The offset from UTC. */
    val offset: UtcOffset,
    /** The time zone. */
    val zone: TimeZone
) : TimePoint<ZonedDateTime> {

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

    @Deprecated(
        "Use toYearMonth() instead.",
        ReplaceWith("this.toYearMonth()"),
        DeprecationLevel.WARNING
    )
    inline val yearMonth: YearMonth
        get() = toYearMonth()

    /**
     * The combined time of day and offset.
     */
    @Deprecated(
        "Use toOffsetTime() instead.",
        ReplaceWith("this.toOffsetTime()"),
        DeprecationLevel.WARNING
    )
    inline val offsetTime: OffsetTime
        get() = toOffsetTime()

    @Deprecated(
        "Use toOffsetDateTime() instead.",
        ReplaceWith("this.toOffsetDateTime()"),
        DeprecationLevel.WARNING
    )
    inline val offsetDateTime: OffsetDateTime
        get() = toOffsetDateTime()

    /**
     * The [Instant] representing the same time point.
     */
    @Deprecated(
        "Use toInstant() instead.",
        ReplaceWith("this.toInstant()"),
        DeprecationLevel.WARNING
    )
    inline val instant: Instant
        get() = toInstant()

    override val secondsSinceUnixEpoch: LongSeconds
        get() = dateTime.secondsSinceUnixEpochAt(offset)

    override val additionalNanosecondsSinceUnixEpoch: IntNanoseconds
        get() = dateTime.additionalNanosecondsSinceUnixEpoch

    override val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = dateTime.millisecondsSinceUnixEpochAt(offset)

    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            is DateProperty,
            is TimeProperty,
            is UtcOffsetProperty,
            is TimeZoneProperty,
            is TimePointProperty -> true
            else -> false
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            is DateProperty, is TimeProperty -> dateTime.get(property)
            is UtcOffsetProperty -> offset.get(property)
            is TimeZoneProperty -> zone.get(property)
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            is DateProperty, is TimeProperty -> dateTime.get(property)
            is UtcOffsetProperty -> offset.get(property)
            is TimeZoneProperty -> zone.get(property)
            TimePointProperty.SecondOfUnixEpoch -> secondOfUnixEpoch
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    override fun <T> get(property: ObjectProperty<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (property) {
            is TimeZoneProperty -> zone.get(property)
            TimePointProperty.Instant -> toInstant() as T
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

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
    operator fun plus(weeks: IntWeeks) = copy(dateTime = dateTime + weeks)
    operator fun plus(weeks: LongWeeks) = copy(dateTime = dateTime + weeks)
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
    operator fun minus(weeks: IntWeeks) = copy(dateTime = dateTime - weeks)
    operator fun minus(weeks: LongWeeks) = copy(dateTime = dateTime - weeks)
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
    ) = fromLocal(dateTime, zone, offset)

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
    ) = fromLocal(dateTime.copy(date, time), zone, offset)

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
    ) = fromLocal(
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
    ) = fromLocal(
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
    ) = fromLocal(
        dateTime.copy(
            date.copy(year, monthNumber, dayOfMonth),
            time.copy(hour, minute, second, nanosecond)
        ),
        zone,
        offset
    )

    /**
     * If the local date-time falls during an overlap caused by a daylight savings transition, return a [ZonedDateTime]
     * with the same local date and time, but using the earlier of the two valid offsets.
     */
    fun withEarlierOffsetAtOverlap(): ZonedDateTime {
        val transition = zone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val earlierOffset = transition.offsetBefore

            if (earlierOffset != offset) {
                return create(dateTime, earlierOffset, zone)
            }
        }
        return this
    }

    /**
     * If the local date-time falls during an overlap caused by a daylight savings transition, return a [ZonedDateTime]
     * with the same local date and time, but using the later of the two valid offsets.
     */
    fun withLaterOffsetAtOverlap(): ZonedDateTime {
        val transition = zone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val laterOffset = transition.offsetAfter

            if (laterOffset != offset) {
                return create(dateTime, laterOffset, zone)
            }
        }
        return this
    }

    /**
     * If this date-time uses a region-based time zone, return a copy with a fixed offset. Otherwise, return this
     * date-time, unchanged.
     */
    fun withFixedOffsetZone(): ZonedDateTime {
        return if (zone is TimeZone.FixedOffset) this else create(dateTime, offset, offset.asTimeZone())
    }

    /**
     * Change the time zone of a [ZonedDateTime], adjusting the date, time, and offset such that the instant
     * represented by it remains the same.
     */
    fun adjustedTo(newTimeZone: TimeZone): ZonedDateTime {
        return if (newTimeZone == zone) {
            this
        } else {
            fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, newTimeZone)
        }
    }

    private fun resolveInstant(newDateTime: DateTime) = fromInstant(newDateTime, offset, zone)

    companion object {
        /**
         * Compare by instant, then date-time, then time zone. Using this `Comparator` guarantees a deterministic order
         * when sorting.
         */
        val DEFAULT_SORT_ORDER = compareBy<ZonedDateTime> { it.secondOfUnixEpoch }
            .thenBy { it.nanosecond }
            .thenBy { it.dateTime }
            .thenBy { it.zone }

        /**
         * Compare by timeline order only, ignoring any offset or time zone differences.
         */
        val TIMELINE_ORDER get() = TimePoint.TIMELINE_ORDER

        /**
         * Create a [ZonedDateTime] from a local date and time, optionally using a preferred offset. If the local date
         * and time fall during an overlap, [preferredOffset] will be used if it represents one of the two valid
         * offsets. If it is `null` or invalid, it will be ignored.
         */
        fun fromLocal(
            dateTime: DateTime,
            zone: TimeZone,
            preferredOffset: UtcOffset? = null
        ): ZonedDateTime {
            val rules = zone.rules
            val validOffsets = rules.validOffsetsAt(dateTime)

            return when (validOffsets.size) {
                1 -> create(dateTime, validOffsets[0], zone)
                0 -> {
                    val transition = rules.transitionAt(dateTime)
                    val adjustedDateTime = dateTime + transition!!.duration
                    create(adjustedDateTime, transition.offsetAfter, zone)
                }
                else -> {
                    val offset = if (preferredOffset != null && validOffsets.contains(preferredOffset)) {
                        preferredOffset
                    } else {
                        validOffsets[0]
                    }
                    create(dateTime, offset, zone)
                }
            }
        }

        /**
         * Create a [ZonedDateTime] from the instant represented by a local date-time and offset. The resulting
         * `ZonedDateTime` may have a different date-time and offset depending on the time zone rules, but the instant
         * will be the same.
         */
        fun fromInstant(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): ZonedDateTime {
            return fromSecondOfUnixEpoch(dateTime.secondOfUnixEpochAt(offset), dateTime.nanosecond, zone)
        }

        /**
         * Create a [ZonedDateTime] from a duration of milliseconds relative to the Unix epoch at [zone].
         */
        fun fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds, zone: TimeZone): ZonedDateTime {
            val offset = zone.rules.offsetAt(milliseconds)
            val dateTime = DateTime.fromMillisecondsSinceUnixEpoch(milliseconds, offset)
            return create(dateTime, offset, zone)
        }

        /**
         * Create a [ZonedDateTime] from a duration of seconds relative to the Unix epoch at [zone], optionally,
         * with some number of additional nanoseconds added to it.
         */
        fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds = 0.nanoseconds,
            zone: TimeZone
        ): ZonedDateTime {
            val offset = zone.rules.offsetAt(seconds, nanosecondAdjustment)
            val dateTime = DateTime.fromSecondsSinceUnixEpoch(seconds, nanosecondAdjustment, offset)
            return create(dateTime, offset, zone)
        }

        /**
         * Create a [ZonedDateTime] from the millisecond of the Unix epoch at [zone].
         */
        fun fromMillisecondOfUnixEpoch(millisecond: Long, zone: TimeZone): ZonedDateTime {
            return fromMillisecondsSinceUnixEpoch(millisecond.milliseconds, zone)
        }

        /**
         * Create a [ZonedDateTime] from the second of the Unix epoch at [zone].
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Int = 0, zone: TimeZone): ZonedDateTime {
            return fromSecondsSinceUnixEpoch(second.seconds, nanosecond.nanoseconds, zone)
        }

        @Deprecated(
            "Use fromMillisecondOfUnixEpoch() instead.",
            ReplaceWith("ZonedDateTime.fromMillisecondOfUnixEpoch(millisecond, zone)"),
            DeprecationLevel.WARNING
        )
        fun fromUnixEpochMillisecond(millisecond: Long, zone: TimeZone): ZonedDateTime {
            return fromMillisecondOfUnixEpoch(millisecond, zone)
        }

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("ZonedDateTime.fromSecondOfUnixEpoch(second, nanoOfSecond, zone)"),
            DeprecationLevel.WARNING
        )
        fun fromUnixEpochSecond(second: Long, nanoOfSecond: Int, zone: TimeZone): ZonedDateTime {
            return fromSecondOfUnixEpoch(second, nanoOfSecond, zone)
        }

        /**
         * Create a [ZonedDateTime] with no additional validation.
         */
        internal fun create(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): ZonedDateTime {
            return ZonedDateTime(dateTime, offset, zone)
        }
    }
}

/**
 * Create a [ZonedDateTime] from a local date and time.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
fun ZonedDateTime(
    year: Int,
    month: Month,
    day: Int,
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int,
    zone: TimeZone
) = ZonedDateTime.fromLocal(DateTime(year, month, day, hour, minute, second, nanosecond), zone)

/**
 * Create a [ZonedDateTime] from a local date and time.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
fun ZonedDateTime(
    year: Int,
    monthNumber: Int,
    day: Int,
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int,
    zone: TimeZone
) = ZonedDateTime.fromLocal(DateTime(year, monthNumber, day, hour, minute, second, nanosecond), zone)

/**
 * Create a [ZonedDateTime] from a local date and time.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
fun ZonedDateTime(
    year: Int,
    dayOfYear: Int,
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int,
    zone: TimeZone
) = ZonedDateTime.fromLocal(DateTime(year, dayOfYear, hour, minute, second, nanosecond), zone)

/**
 * Create a [ZonedDateTime] from a local date and time.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
fun ZonedDateTime(date: Date, time: Time, zone: TimeZone) = ZonedDateTime.fromLocal(DateTime(date, time), zone)

/**
 * Create a [ZonedDateTime] from a local date and time.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
fun ZonedDateTime(dateTime: DateTime, zone: TimeZone) = ZonedDateTime.fromLocal(dateTime, zone)

/**
 * Combine an instant with a time zone to create a [ZonedDateTime].
 */
infix fun Instant.at(zone: TimeZone) = ZonedDateTime.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, zone)

/**
 * Combine a local date and time with a time zone to create a [ZonedDateTime].
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
infix fun DateTime.at(zone: TimeZone) = ZonedDateTime.fromLocal(this, zone)

/**
 * The [ZonedDateTime] at the start of this date in a particular time zone, taking into account any daylight savings
 * transitions.
 */
fun Date.startOfDayAt(zone: TimeZone): ZonedDateTime {
    val dateTime = this at Time.MIDNIGHT
    val transition = zone.rules.transitionAt(dateTime)

    return if (transition?.isGap == true) {
        transition.dateTimeAfter at zone
    } else {
        dateTime at zone
    }
}

/**
 * The [ZonedDateTime] at the last representable instant of this date in a particular time zone, taking into account any
 * daylight savings transitions.
 */
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

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_LOCAL_TIME)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_LOCAL_TIME"
    ),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.similarLocalTimeAt(zone: TimeZone): ZonedDateTime {
    return toZonedDateTime(zone, OffsetConversionStrategy.PRESERVE_LOCAL_TIME)
}

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_LOCAL_TIME)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_LOCAL_TIME"
    ),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.dateTimeAt(zone: TimeZone): ZonedDateTime {
    return toZonedDateTime(zone, OffsetConversionStrategy.PRESERVE_LOCAL_TIME)
}

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_INSTANT)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_INSTANT"
    ),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.sameInstantAt(zone: TimeZone): ZonedDateTime {
    return toZonedDateTime(zone, OffsetConversionStrategy.PRESERVE_INSTANT)
}

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_INSTANT)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_INSTANT"
    ),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.instantAt(zone: TimeZone): ZonedDateTime {
    return toZonedDateTime(zone, OffsetConversionStrategy.PRESERVE_INSTANT)
}

/**
 * Converts this [OffsetDateTime] to an equivalent [ZonedDateTime] using a fixed-offset time zone.
 *
 * This comes with the caveat that a fixed-offset zone lacks knowledge of any region and will not respond to daylight
 * savings time changes. To convert to a region-based zone, use [toZonedDateTime] instead.
 *
 * @see toZonedDateTime
 */
fun OffsetDateTime.asZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.fromLocal(dateTime, offset.asTimeZone(), offset)
}

/**
 * Convert a string to a [ZonedDateTime].
 *
 * The string is assumed to be a complete ISO-8601 date and time representation in extended format, optionally including
 * a non-standard region ID. For example, `2005-05-06T23:30+01` or `2005-05-06T23:30-04:00[America/New_York]`.
 *
 * The output of [ZonedDateTime.toString] can be safely parsed using this method.
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed date-time is invalid
 */
fun String.toZonedDateTime() = toZonedDateTime(DateTimeParsers.Iso.Extended.ZONED_DATE_TIME)

/**
 * Convert a string to a [ZonedDateTime] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed date-time is invalid
 */
fun String.toZonedDateTime(
    parser: TemporalParser,
    settings: TemporalParser.Settings = TemporalParser.Settings.DEFAULT
): ZonedDateTime {
    val result = parser.parse(this, settings)
    return result.toZonedDateTime() ?: throwParserPropertyResolutionException<ZonedDateTime>(this)
}

internal fun TemporalParseResult.toZonedDateTime(): ZonedDateTime? {
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()

    return if (dateTime != null && offset != null) {
        val zone = this[TimeZoneProperty.Id]?.toTimeZone() ?: offset.asTimeZone()

        // Check if the offset is valid for the time zone as we understand it and if not, adjust the date-time and
        // offset to valid values while preserving the instant of the parsed value
        if (!zone.rules.isValidOffset(dateTime, offset)) {
            ZonedDateTime.fromInstant(dateTime, offset, zone)
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

        if (zone !is TimeZone.FixedOffset) {
            append('[')
            append(zone)
            append(']')
        }
    }
    return this
}