@file:Suppress("FunctionName")

package io.islandtime

import io.islandtime.base.TimePoint
import io.islandtime.internal.deprecatedToError
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
        "Use toOffsetDateTime() instead.",
        ReplaceWith("this.toOffsetDateTime()"),
        DeprecationLevel.ERROR
    )
    inline val offsetDateTime: OffsetDateTime
        get() = toOffsetDateTime()

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

    /**
     * Converts this date-time to a string in ISO-8601 extended format. For example,
     * `2012-04-15T17:31:45.923452091-04:00[America/New_York]` or `2020-02-13T02:30Z`.
     */
    override fun toString(): String {
        return buildString(MAX_ZONED_DATE_TIME_STRING_LENGTH) {
            appendZonedDateTime(this@ZonedDateTime)
        }
    }

    /**
     * Returns this date-time with [period] added to it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range.
     */
    operator fun plus(period: Period): ZonedDateTime = copy(dateTime = dateTime + period)

    operator fun plus(duration: Duration): ZonedDateTime = resolveInstant(dateTime + duration)

    /**
     * Returns this date-time with [duration] added to it.
     */
    @kotlin.time.ExperimentalTime
    operator fun plus(duration: kotlin.time.Duration): ZonedDateTime = resolveInstant(dateTime + duration)

    /**
     * Returns this date-tme with [centuries] added to it.
     */
    operator fun plus(centuries: Centuries): ZonedDateTime = copy(dateTime = dateTime + centuries)

    /**
     * Returns this date-time with [decades] added to it.
     */
    operator fun plus(decades: Decades): ZonedDateTime = copy(dateTime = dateTime + decades)

    /**
     * Returns this date-time with [years] added to it.
     */
    operator fun plus(years: Years): ZonedDateTime = copy(dateTime = dateTime + years)

    /**
     * Returns this date-time with [months] added to it.
     */
    operator fun plus(months: Months): ZonedDateTime = copy(dateTime = dateTime + months)

    /**
     * Returns this date-time with [weeks] added to it.
     */
    operator fun plus(weeks: Weeks): ZonedDateTime = copy(dateTime = dateTime + weeks)

    /**
     * Returns this date-time with [days] added to it.
     */
    operator fun plus(days: Days): ZonedDateTime = copy(dateTime = dateTime + days)

    /**
     * Returns this date-time with [hours] added to it.
     */
    override operator fun plus(hours: Hours): ZonedDateTime = resolveInstant(dateTime + hours)

    /**
     * Returns this date-time with [minutes] added to it.
     */
    override operator fun plus(minutes: Minutes): ZonedDateTime = resolveInstant(dateTime + minutes)

    /**
     * Returns this date-time with [seconds] added to it.
     */
    override operator fun plus(seconds: Seconds): ZonedDateTime = resolveInstant(dateTime + seconds)

    /**
     * Returns this date-time with [milliseconds] added to it.
     */
    override operator fun plus(milliseconds: Milliseconds): ZonedDateTime =
        resolveInstant(dateTime + milliseconds)

    /**
     * Returns this date-time with [microseconds] added to it.
     */
    override operator fun plus(microseconds: Microseconds): ZonedDateTime =
        resolveInstant(dateTime + microseconds)

    /**
     * Returns this date-time with [nanoseconds] added to it.
     */
    override operator fun plus(nanoseconds: Nanoseconds): ZonedDateTime =
        resolveInstant(dateTime + nanoseconds)

    /**
     * Returns this date-time with [period] subtracted from it.
     *
     * Years are subtracted first, then months, then days. If the day exceeds the maximum month length at any step, it
     * will be coerced into the valid range.
     */
    operator fun minus(period: Period): ZonedDateTime = copy(dateTime = dateTime - period)

    operator fun minus(duration: Duration): ZonedDateTime = resolveInstant(dateTime - duration)

    /**
     * Returns this date-time with [duration] subtracted from it.
     */
    @kotlin.time.ExperimentalTime
    operator fun minus(duration: kotlin.time.Duration): ZonedDateTime = resolveInstant(dateTime - duration)

    /**
     * Returns this date-time with [centuries] subtracted from it.
     */
    operator fun minus(centuries: Centuries): ZonedDateTime = copy(dateTime = dateTime - centuries)

    /**
     * Returns this date-time with [decades] subtracted from it.
     */
    operator fun minus(decades: Decades): ZonedDateTime = copy(dateTime = dateTime - decades)

    /**
     * Returns this date-time with [years] subtracted from it.
     */
    operator fun minus(years: Years): ZonedDateTime = copy(dateTime = dateTime - years)

    /**
     * Returns this date-time with [months] subtracted from it.
     */
    operator fun minus(months: Months): ZonedDateTime = copy(dateTime = dateTime - months)

    /**
     * Returns this date-time with [weeks] subtracted from it.
     */
    operator fun minus(weeks: Weeks): ZonedDateTime = copy(dateTime = dateTime - weeks)

    /**
     * Returns this date-time with [days] subtracted from it.
     */
    operator fun minus(days: Days): ZonedDateTime = copy(dateTime = dateTime - days)

    /**
     * Returns this date-time with [hours] subtracted from it.
     */
    override operator fun minus(hours: Hours): ZonedDateTime = resolveInstant(dateTime - hours)

    /**
     * Returns this date-time with [minutes] subtracted from it.
     */
    override operator fun minus(minutes: Minutes): ZonedDateTime = resolveInstant(dateTime - minutes)

    /**
     * Returns this date-time with [seconds] subtracted from it.
     */
    override operator fun minus(seconds: Seconds): ZonedDateTime = resolveInstant(dateTime - seconds)

    /**
     * Returns this date-time with [milliseconds] subtracted from it.
     */
    override operator fun minus(milliseconds: Milliseconds): ZonedDateTime =
        resolveInstant(dateTime - milliseconds)

    /**
     * Returns this date-time with [microseconds] subtracted from it.
     */
    override operator fun minus(microseconds: Microseconds): ZonedDateTime =
        resolveInstant(dateTime - microseconds)

    /**
     * Returns this date-time with [nanoseconds] subtracted from it.
     */
    override operator fun minus(nanoseconds: Nanoseconds): ZonedDateTime =
        resolveInstant(dateTime - nanoseconds)

    operator fun rangeTo(other: ZonedDateTime): ZonedDateTimeInterval =
        ZonedDateTimeInterval.withInclusiveEnd(this, other)

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     *
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        dateTime: DateTime = this.dateTime,
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ): ZonedDateTime = fromLocal(dateTime, zone, offset)

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     *
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        date: Date = this.date,
        time: Time = this.time,
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ): ZonedDateTime = fromLocal(dateTime.copy(date, time), zone, offset)

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     *
     * @throws DateTimeException if the resulting date-time is invalid
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
    ): ZonedDateTime = fromLocal(
        dateTime.copy(
            date.copy(year, dayOfYear),
            time.copy(hour, minute, second, nanosecond)
        ),
        zone,
        offset
    )

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     *
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
        offset: UtcOffset = this.offset,
        zone: TimeZone = this.zone
    ): ZonedDateTime = fromLocal(
        dateTime.copy(
            date.copy(year, month, dayOfMonth),
            time.copy(hour, minute, second, nanosecond)
        ),
        zone,
        offset
    )

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     *
     * If the new date falls within a daylight savings time gap, it will be adjusted forward by the length of the gap.
     * If it falls within an overlap, the [offset] value will be used if possible. The time zone takes precedence over
     * the offset, so any provided [offset] value will be ignored if it is invalid within the current region.
     *
     * @throws DateTimeException if the resulting date-time is invalid
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
    ): ZonedDateTime = fromLocal(
        dateTime.copy(
            date.copy(year, monthNumber, dayOfMonth),
            time.copy(hour, minute, second, nanosecond)
        ),
        zone,
        offset
    )

    /**
     * If the local date-time falls during an overlap caused by a daylight savings transition, a [ZonedDateTime] with
     * the same local date and time will be returned, but using the earlier of the two valid offsets.
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
     * If the local date-time falls during an overlap caused by a daylight savings transition, a [ZonedDateTime] with
     * the same local date and time will be returned, but using the later of the two valid offsets.
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
     * If this date-time uses a region-based time zone, a copy with a fixed offset will be returned. Otherwise, this
     * date-time will be returned unchanged.
     */
    fun withFixedOffsetZone(): ZonedDateTime {
        return if (zone is TimeZone.FixedOffset) this else create(dateTime, offset, offset.asTimeZone())
    }

    /**
     * Changes the time zone of a [ZonedDateTime], adjusting the date, time, and offset such that the instant
     * represented by it remains the same.
     */
    fun adjustedTo(newTimeZone: TimeZone): ZonedDateTime {
        return if (newTimeZone == zone) {
            this
        } else {
            fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, newTimeZone)
        }
    }

    private fun resolveInstant(newDateTime: DateTime): ZonedDateTime = fromInstant(newDateTime, offset, zone)

    companion object {
        /**
         * A [Comparator] that compares by instant, then date-time, then time zone. Using this `Comparator` guarantees a
         * deterministic order when sorting.
         */
        val DEFAULT_SORT_ORDER: Comparator<ZonedDateTime> = compareBy<ZonedDateTime> { it.secondOfUnixEpoch }
            .thenBy { it.nanosecond }
            .thenBy { it.dateTime }
            .thenBy { it.zone }

        /**
         * A [Comparator] that compares by timeline order only, ignoring any offset or time zone differences.
         */
        val TIMELINE_ORDER: Comparator<TimePoint<*>> get() = TimePoint.TIMELINE_ORDER

        /**
         * Creates a [ZonedDateTime] from a local date and time, optionally using a preferred offset. If the local date
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
         * Creates a [ZonedDateTime] from the instant represented by a local date-time and offset. The resulting
         * `ZonedDateTime` may have a different date-time and offset depending on the time zone rules, but the instant
         * will be the same.
         */
        fun fromInstant(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): ZonedDateTime {
            return fromSecondOfUnixEpoch(dateTime.secondOfUnixEpochAt(offset), dateTime.nanosecond, zone)
        }

        /**
         * Creates a [ZonedDateTime] from a duration of milliseconds relative to the Unix epoch at [zone].
         */
        fun fromMillisecondsSinceUnixEpoch(milliseconds: Milliseconds, zone: TimeZone): ZonedDateTime {
            val offset = zone.rules.offsetAt(milliseconds)
            val dateTime = DateTime.fromMillisecondsSinceUnixEpoch(milliseconds, offset)
            return create(dateTime, offset, zone)
        }

        /**
         * Creates a [ZonedDateTime] from a duration of seconds relative to the Unix epoch at [zone], optionally,
         * with some number of additional nanoseconds added to it.
         */
        fun fromSecondsSinceUnixEpoch(
            seconds: Seconds,
            nanosecondAdjustment: Nanoseconds = 0.nanoseconds,
            zone: TimeZone
        ): ZonedDateTime {
            val offset = zone.rules.offsetAt(seconds, nanosecondAdjustment)
            val dateTime = DateTime.fromSecondsSinceUnixEpoch(seconds, nanosecondAdjustment, offset)
            return create(dateTime, offset, zone)
        }

        /**
         * Creates a [ZonedDateTime] from the millisecond of the Unix epoch at [zone].
         */
        fun fromMillisecondOfUnixEpoch(millisecond: Long, zone: TimeZone): ZonedDateTime {
            return fromMillisecondsSinceUnixEpoch(millisecond.milliseconds, zone)
        }

        /**
         * Creates a [ZonedDateTime] from the second of the Unix epoch at [zone].
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Int = 0, zone: TimeZone): ZonedDateTime {
            return fromSecondsSinceUnixEpoch(second.seconds, nanosecond.nanoseconds, zone)
        }

        @Deprecated(
            "Use fromMillisecondOfUnixEpoch() instead.",
            ReplaceWith("ZonedDateTime.fromMillisecondOfUnixEpoch(millisecond, zone)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER", "unused")
        fun fromUnixEpochMillisecond(millisecond: Long, zone: TimeZone): ZonedDateTime = deprecatedToError()

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("ZonedDateTime.fromSecondOfUnixEpoch(second, nanoOfSecond, zone)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER", "unused")
        fun fromUnixEpochSecond(second: Long, nanoOfSecond: Int, zone: TimeZone): ZonedDateTime = deprecatedToError()

        /**
         * Creates a [ZonedDateTime] with no additional validation.
         */
        internal fun create(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): ZonedDateTime {
            return ZonedDateTime(dateTime, offset, zone)
        }
    }
}

/**
 * Creates a [ZonedDateTime] from a local date and time.
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
): ZonedDateTime = ZonedDateTime.fromLocal(DateTime(year, month, day, hour, minute, second, nanosecond), zone)

/**
 * Creates a [ZonedDateTime] from a local date and time.
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
): ZonedDateTime = ZonedDateTime.fromLocal(DateTime(year, monthNumber, day, hour, minute, second, nanosecond), zone)

/**
 * Creates a [ZonedDateTime] from a local date and time.
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
): ZonedDateTime = ZonedDateTime.fromLocal(DateTime(year, dayOfYear, hour, minute, second, nanosecond), zone)

/**
 * Creates a [ZonedDateTime] from a local date and time.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
fun ZonedDateTime(date: Date, time: Time, zone: TimeZone): ZonedDateTime =
    ZonedDateTime.fromLocal(DateTime(date, time), zone)

/**
 * Creates a [ZonedDateTime] from a local date and time.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
fun ZonedDateTime(dateTime: DateTime, zone: TimeZone): ZonedDateTime = ZonedDateTime.fromLocal(dateTime, zone)

/**
 * Converts a string to a [ZonedDateTime].
 *
 * The string is assumed to be a complete ISO-8601 date and time representation in extended format, optionally including
 * a non-standard region ID. For example, `2005-05-06T23:30+01` or `2005-05-06T23:30-04:00[America/New_York]`.
 *
 * The output of [ZonedDateTime.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date-time is invalid
 */
fun String.toZonedDateTime(): ZonedDateTime = toZonedDateTime(DateTimeParsers.Iso.Extended.ZONED_DATE_TIME)

/**
 * Converts a string to a [ZonedDateTime] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date-time is invalid
 */
fun String.toZonedDateTime(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): ZonedDateTime {
    val result = parser.parse(this, settings)
    return result.toZonedDateTime() ?: throwParserFieldResolutionException<ZonedDateTime>(this)
}

internal fun DateTimeParseResult.toZonedDateTime(): ZonedDateTime? {
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()

    return if (dateTime != null && offset != null) {
        val zone = timeZoneId?.let { TimeZone(it) } ?: offset.asTimeZone()

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

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_LOCAL_TIME)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_LOCAL_TIME"
    ),
    DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER", "unused")
fun OffsetDateTime.similarLocalTimeAt(zone: TimeZone): ZonedDateTime = deprecatedToError()

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_LOCAL_TIME)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_LOCAL_TIME"
    ),
    DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER", "unused")
fun OffsetDateTime.dateTimeAt(zone: TimeZone): ZonedDateTime = deprecatedToError()

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_INSTANT)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_INSTANT"
    ),
    DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER", "unused")
fun OffsetDateTime.sameInstantAt(zone: TimeZone): ZonedDateTime = deprecatedToError()

@Deprecated(
    "Use toZonedDateTime() instead.",
    ReplaceWith(
        "this.toZonedDateTime(zone, PRESERVE_INSTANT)",
        "io.islandtime.OffsetConversionStrategy.PRESERVE_INSTANT"
    ),
    DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER", "unused")
fun OffsetDateTime.instantAt(zone: TimeZone): ZonedDateTime = deprecatedToError()
