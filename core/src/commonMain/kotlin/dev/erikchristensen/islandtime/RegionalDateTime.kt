package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.parser.raiseParserFieldResolutionException

/**
 * A date and time in a particular region
 */
class RegionalDateTime private constructor(
    val dateTime: DateTime,
    val offset: UtcOffset,
    val zone: TimeZone
) : Comparable<RegionalDateTime> {

    /**
     * The [OffsetDateTime] representing the current date, time, and offset
     */
    inline val offsetDateTime: OffsetDateTime get() = OffsetDateTime(dateTime, offset)

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

    inline val secondsSinceUnixEpoch: LongSeconds
        get() = dateTime.secondsSinceUnixEpochAt(offset)

    inline val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = dateTime.millisecondsSinceUnixEpochAt(offset)

    inline val instant: Instant
        get() = Instant.fromSecondsSinceUnixEpoch(secondsSinceUnixEpoch, nanosecond.nanoseconds)

    override fun compareTo(other: RegionalDateTime): Int {
        val secondDiff = secondsSinceUnixEpoch.compareTo(other.secondsSinceUnixEpoch)

        return if (secondDiff != 0) {
            secondDiff
        } else {
            val nanoDiff = nanosecond - other.nanosecond

            if (nanoDiff != 0) {
                nanoDiff
            } else {
                val dateTimeDiff = dateTime.compareTo(other.dateTime)

                if (dateTimeDiff != 0) {
                    dateTimeDiff
                } else {
                    zone.compareTo(zone)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is RegionalDateTime &&
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
        return buildString(MAX_REGIONAL_DATE_TIME_STRING_LENGTH) {
            appendRegionalDateTime(this@RegionalDateTime)
        }
    }

    operator fun plus(years: IntYears) = copy(dateTime = dateTime + years)
    operator fun plus(years: LongYears) = copy(dateTime = dateTime + years)
    operator fun plus(months: IntMonths) = copy(dateTime = dateTime + months)
    operator fun plus(months: LongMonths) = copy(dateTime = dateTime + months)
    operator fun plus(days: IntDays) = copy(dateTime = dateTime + days)
    operator fun plus(days: LongDays) = copy(dateTime = dateTime + days)
    operator fun plus(hours: IntHours) = resolveInstant(dateTime + hours)
    operator fun plus(hours: LongHours) = resolveInstant(dateTime + hours)
    operator fun plus(minutes: IntMinutes) = resolveInstant(dateTime + minutes)
    operator fun plus(minutes: LongMinutes) = resolveInstant(dateTime + minutes)
    operator fun plus(seconds: IntSeconds) = resolveInstant(dateTime + seconds)
    operator fun plus(seconds: LongSeconds) = resolveInstant(dateTime + seconds)
    operator fun plus(milliseconds: IntMilliseconds) = resolveInstant(dateTime + milliseconds)
    operator fun plus(milliseconds: LongMilliseconds) = resolveInstant(dateTime + milliseconds)
    operator fun plus(microseconds: IntMicroseconds) = resolveInstant(dateTime + microseconds)
    operator fun plus(microseconds: LongMicroseconds) = resolveInstant(dateTime + microseconds)
    operator fun plus(nanoseconds: IntNanoseconds) = resolveInstant(dateTime + nanoseconds)
    operator fun plus(nanoseconds: LongNanoseconds) = resolveInstant(dateTime + nanoseconds)

    operator fun minus(years: IntYears) = copy(dateTime = dateTime - years)
    operator fun minus(years: LongYears) = copy(dateTime = dateTime - years)
    operator fun minus(months: IntMonths) = copy(dateTime = dateTime - months)
    operator fun minus(months: LongMonths) = copy(dateTime = dateTime - months)
    operator fun minus(days: IntDays) = copy(dateTime = dateTime - days)
    operator fun minus(days: LongDays) = copy(dateTime = dateTime - days)
    operator fun minus(hours: IntHours) = resolveInstant(dateTime - hours)
    operator fun minus(hours: LongHours) = resolveInstant(dateTime - hours)
    operator fun minus(minutes: IntMinutes) = resolveInstant(dateTime - minutes)
    operator fun minus(minutes: LongMinutes) = resolveInstant(dateTime - minutes)
    operator fun minus(seconds: IntSeconds) = resolveInstant(dateTime - seconds)
    operator fun minus(seconds: LongSeconds) = resolveInstant(dateTime - seconds)
    operator fun minus(milliseconds: IntMilliseconds) = resolveInstant(dateTime - milliseconds)
    operator fun minus(milliseconds: LongMilliseconds) = resolveInstant(dateTime - milliseconds)
    operator fun minus(microseconds: IntMicroseconds) = resolveInstant(dateTime - microseconds)
    operator fun minus(microseconds: LongMicroseconds) = resolveInstant(dateTime - microseconds)
    operator fun minus(nanoseconds: IntNanoseconds) = resolveInstant(dateTime - nanoseconds)
    operator fun minus(nanoseconds: LongNanoseconds) = resolveInstant(dateTime - nanoseconds)

    /**
     * Return a new [RegionalDateTime], replacing any of the components with new values.
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
     * Return a new [RegionalDateTime], replacing any of the components with new values.
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
     * Return a new [RegionalDateTime], replacing any of the components with new values.
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
     * Return a new [RegionalDateTime], replacing any of the components with new values.
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
     * Return a new [RegionalDateTime], replacing any of the components with new values.
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

    fun withEarlierOffsetAtOverlap(): RegionalDateTime {
        val transition = zone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val earlierOffset = transition.offsetBefore

            if (earlierOffset != offset) {
                return RegionalDateTime(dateTime, earlierOffset, zone)
            }
        }
        return this
    }

    fun withLaterOffsetAtOverlap(): RegionalDateTime {
        val transition = zone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val laterOffset = transition.offsetAfter

            if (laterOffset != offset) {
                return RegionalDateTime(dateTime, laterOffset, zone)
            }
        }
        return this
    }

    /**
     * Change the time zone of a [RegionalDateTime], adjusting the date, time, and offset such that the instant
     * represented by it remains the same
     */
    fun adjustedTo(newTimeZone: TimeZone): RegionalDateTime {
        return if (newTimeZone == zone) {
            this
        } else {
            ofInstant(instant, newTimeZone)
        }
    }

    private fun resolveInstant(newDateTime: DateTime) = ofInstant(newDateTime, offset, zone)

    companion object {

        operator fun invoke(
            year: Int,
            month: Month,
            day: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanosecond: Int,
            zone: TimeZone
        ) = ofLocal(DateTime(year, month, day, hour, minute, second, nanosecond), zone)

        operator fun invoke(
            year: Int,
            monthNumber: Int,
            day: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanosecond: Int,
            zone: TimeZone
        ) = ofLocal(DateTime(year, monthNumber, day, hour, minute, second, nanosecond), zone)

        operator fun invoke(
            year: Int,
            dayOfYear: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanosecond: Int,
            zone: TimeZone
        ) = ofLocal(DateTime(year, dayOfYear, hour, minute, second, nanosecond), zone)

        operator fun invoke(date: Date, time: Time, zone: TimeZone) = ofLocal(DateTime(date, time), zone)

        operator fun invoke(dateTime: DateTime, zone: TimeZone) = ofLocal(dateTime, zone)

        fun ofLocal(
            dateTime: DateTime,
            zone: TimeZone,
            preferredOffset: UtcOffset? = null
        ): RegionalDateTime {
            val rules = zone.rules
            val validOffsets = rules.validOffsetsAt(dateTime)

            return when (validOffsets.size) {
                1 -> RegionalDateTime(dateTime, validOffsets[0], zone)
                0 -> {
                    val transition = rules.transitionAt(dateTime)
                    val adjustedDateTime = dateTime + transition!!.duration
                    RegionalDateTime(adjustedDateTime, transition.offsetAfter, zone)
                }
                else -> {
                    val offset = if (preferredOffset != null && validOffsets.contains(preferredOffset)) {
                        preferredOffset
                    } else {
                        validOffsets[0]
                    }
                    RegionalDateTime(dateTime, offset, zone)
                }
            }
        }

        fun ofInstant(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): RegionalDateTime {
            return ofInstant(dateTime.instantAt(offset), zone)
        }

        fun ofInstant(instant: Instant, zone: TimeZone): RegionalDateTime {
            val offset = zone.rules.offsetAt(instant)
            val dateTime = instant.toDateTimeAt(offset)
            return RegionalDateTime(dateTime, offset, zone)
        }

        /**
         * Create a [RegionalDateTime] with no additional validation
         */
        internal fun create(dateTime: DateTime, offset: UtcOffset, zone: TimeZone): RegionalDateTime {
            return RegionalDateTime(dateTime, offset, zone)
        }

        fun now() = now(systemClock())

        fun now(clock: Clock): RegionalDateTime {
            return clock.instant() at clock.timeZone
        }
    }
}

/**
 * Get the [RegionalDateTime] corresponding to the local date and time in a particular time zone.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap, it will adjusted forward by the length of the gap. If it falls within an overlap, the earlier offset will be
 * used.
 */
infix fun DateTime.at(zone: TimeZone) = RegionalDateTime.ofLocal(this, zone, null)

/**
 * Get the [RegionalDateTime] corresponding to a local date, time, and offset in a particular time zone. The offset
 * will be preserved if it is valid based on the rules of the time zone.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap, it will adjusted forward by the length of the gap. If it falls within an overlap, the earlier offset will be
 * used.
 */
fun OffsetDateTime.atSimilarLocalTimeIn(zone: TimeZone): RegionalDateTime {
    return RegionalDateTime.ofLocal(dateTime, zone, offset)
}

/**
 * Get the [RegionalDateTime] corresponding to the same instant represented by an [OffsetDateTime] in a particular
 * time zone
 */
fun OffsetDateTime.atSameInstantIn(zone: TimeZone): RegionalDateTime {
    return RegionalDateTime.ofInstant(dateTime, offset, zone)
}

/**
 * Get the [RegionalDateTime] corresponding to an instant in a particular time zone
 */
infix fun Instant.at(zone: TimeZone) = RegionalDateTime.ofInstant(this, zone)

fun Date.atStartOfDayIn(zone: TimeZone): RegionalDateTime {
    val dateTime = this at Time.MIDNIGHT
    val transition = zone.rules.transitionAt(dateTime)

    return if (transition?.isGap == true) {
        transition.dateTimeAfter at zone
    } else {
        dateTime at zone
    }
}

fun Date.atEndOfDayIn(zone: TimeZone): RegionalDateTime {
    val dateTime = this at Time.MAX
    val rules = zone.rules
    val validOffsets = rules.validOffsetsAt(dateTime)

    return if (validOffsets.size == 1) {
        RegionalDateTime.create(dateTime, validOffsets[0], zone)
    } else {
        val transition = rules.transitionAt(dateTime)

        if (validOffsets.isEmpty()) {
            RegionalDateTime.create(transition!!.dateTimeBefore, transition.offsetBefore, zone)
        } else {
            RegionalDateTime.create(dateTime, transition!!.offsetAfter, zone)
        }
    }
}

fun String.toRegionalDateTime() = toRegionalDateTime(Iso8601.Extended.REGIONAL_DATE_TIME_PARSER)

fun String.toRegionalDateTime(parser: DateTimeParser): RegionalDateTime {
    val result = parser.parse(this)
    return result.toRegionalDateTime() ?: raiseParserFieldResolutionException("RegionalDateTime", this)
}

internal fun DateTimeParseResult.toRegionalDateTime(): RegionalDateTime? {
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()
    val regionId = timeZoneRegion

    return if (dateTime != null && offset != null && regionId != null) {
        val zone = regionId.toTimeZone().validated()

        // Check if the offset is valid for the time zone as we understand it and if not, interpret it as an instant
        if (!zone.rules.isValidOffset(dateTime, offset)) {
            dateTime.instantAt(offset).at(zone)
        } else {
            RegionalDateTime.create(dateTime, offset, zone)
        }
    } else {
        null
    }
}

internal const val MAX_REGIONAL_DATE_TIME_STRING_LENGTH =
    MAX_DATE_TIME_STRING_LENGTH + MAX_UTC_OFFSET_STRING_LENGTH + MAX_TIME_ZONE_STRING_LENGTH + 2

internal fun StringBuilder.appendRegionalDateTime(regionalDateTime: RegionalDateTime): StringBuilder {
    with(regionalDateTime) {
        appendDateTime(dateTime)
        appendUtcOffset(offset)
        append('[')
        append(zone)
        append(']')
    }
    return this
}