package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.parser.raiseParserFieldResolutionException

/**
 * A calendar date and time combined with a fixed UTC offset
 */
class OffsetDateTime(
    val dateTime: DateTime,
    val offset: UtcOffset
) : Comparable<OffsetDateTime> {

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

    operator fun plus(daysToAdd: LongDays): OffsetDateTime {
        return if (daysToAdd == 0L.days) {
            this
        } else {
            copy(dateTime = dateTime + daysToAdd)
        }
    }

    operator fun plus(daysToAdd: IntDays) = plus(daysToAdd.toLong())

    operator fun plus(monthsToAdd: LongMonths): OffsetDateTime {
        return if (monthsToAdd == 0L.months) {
            this
        } else {
            copy(dateTime = dateTime + monthsToAdd)
        }
    }

    operator fun plus(monthsToAdd: IntMonths) = plus(monthsToAdd.toLong())

    operator fun plus(yearsToAdd: LongYears): OffsetDateTime {
        return if (yearsToAdd == 0L.years) {
            this
        } else {
            copy(dateTime = dateTime + yearsToAdd)
        }
    }

    operator fun plus(yearsToAdd: IntYears) = plus(yearsToAdd.toLong())

    operator fun plus(hoursToAdd: LongHours): OffsetDateTime {
        return if (hoursToAdd == 0L.hours) {
            this
        } else {
            copy(dateTime = dateTime + hoursToAdd)
        }
    }

    operator fun plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

    operator fun plus(minutesToAdd: LongMinutes): OffsetDateTime {
        return if (minutesToAdd == 0L.minutes) {
            this
        } else {
            copy(dateTime = dateTime + minutesToAdd)
        }
    }

    operator fun plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

    operator fun plus(secondsToAdd: LongSeconds): OffsetDateTime {
        return if (secondsToAdd == 0L.seconds) {
            this
        } else {
            copy(dateTime = dateTime + secondsToAdd)
        }
    }

    operator fun plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

    operator fun plus(nanosecondsToAdd: LongNanoseconds): OffsetDateTime {
        return if (nanosecondsToAdd == 0L.nanoseconds) {
            this
        } else {
            copy(dateTime = dateTime + nanosecondsToAdd)
        }
    }

    operator fun plus(nanosecondsToAdd: IntNanoseconds) = plus(nanosecondsToAdd.toLong())

    operator fun minus(daysToSubtract: LongDays) = plus(-daysToSubtract)
    operator fun minus(monthsToSubtract: IntMonths) = plus(-monthsToSubtract)
    operator fun minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)
    operator fun minus(hoursToSubtract: IntHours) = plus(-hoursToSubtract)
    operator fun minus(minutesToSubtract: LongMinutes) = plus(-minutesToSubtract)
    operator fun minus(minutesToSubtract: IntMinutes) = plus(-minutesToSubtract)
    operator fun minus(secondsToSubtract: LongSeconds) = plus(-secondsToSubtract)
    operator fun minus(secondsToSubtract: IntSeconds) = plus(-secondsToSubtract)
    operator fun minus(nanosecondsToSubtract: LongNanoseconds) = plus(-nanosecondsToSubtract)
    operator fun minus(nanosecondsToSubtract: IntNanoseconds) = plus(-nanosecondsToSubtract)

    operator fun component1() = dateTime
    operator fun component2() = offset

    override fun toString() = buildString(MAX_OFFSET_DATE_TIME_STRING_LENGTH) {
        appendOffsetDateTime(this@OffsetDateTime)
    }

    override fun compareTo(other: OffsetDateTime): Int {
        return if (offset == other.offset) {
            dateTime.compareTo(other.dateTime)
        } else {
            val secondDiff = secondsSinceUnixEpoch.compareTo(other.secondsSinceUnixEpoch)

            if (secondDiff != 0) {
                secondDiff
            } else {
                val nanoDiff = nanosecond - other.nanosecond

                if (nanoDiff != 0) {
                    nanoDiff
                } else {
                    dateTime.compareTo(other.dateTime)
                }
            }
        }
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

    companion object {
        val MIN = DateTime.MIN at UtcOffset.MAX
        val MAX = DateTime.MAX at UtcOffset.MIN
    }
}

infix fun DateTime.at(offset: UtcOffset) = OffsetDateTime(this, offset)
infix fun Date.at(offsetTime: OffsetTime) = OffsetDateTime(this, offsetTime.time, offsetTime.offset)
infix fun Instant.at(offset: UtcOffset) = OffsetDateTime(this.toDateTime(offset), offset)

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