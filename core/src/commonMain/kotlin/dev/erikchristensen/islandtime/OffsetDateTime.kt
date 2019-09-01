package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.date.unixEpochDays
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
        nanoOfSecond: Int,
        offset: UtcOffset
    ) : this(DateTime(year, month, dayOfMonth, hour, minute, second, nanoOfSecond), offset)

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
        nanoOfSecond: Int,
        offset: UtcOffset
    ) : this(DateTime(year, Month(monthNumber), dayOfMonth, hour, minute, second, nanoOfSecond), offset)

    val date: Date get() = dateTime.date
    val time: Time get() = dateTime.time

    operator fun component1() = dateTime
    operator fun component2() = offset

    override fun toString() = buildString(MAX_OFFSET_DATE_TIME_STRING_LENGTH) {
        appendOffsetDateTime(this@OffsetDateTime)
    }

    override fun compareTo(other: OffsetDateTime): Int {
        return if (offset == other.offset) {
            dateTime.compareTo(other.dateTime)
        } else {
            val secondDiff = unixEpochSeconds.value.compareTo(other.unixEpochSeconds.value)

            if (secondDiff != 0) {
                secondDiff
            } else {
                val nanoDiff = nanoOfSecond - other.nanoOfSecond

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
        nanoOfSecond: Int = this.nanoOfSecond,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(date.copy(year, dayOfYear), time.copy(hour, minute, second, nanoOfSecond), offset)

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
        nanoOfSecond: Int = this.nanoOfSecond,
        offset: UtcOffset = this.offset
    ) = OffsetDateTime(date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanoOfSecond), offset)

    companion object {
        val MIN = DateTime.MIN at UtcOffset.MAX
        val MAX = DateTime.MAX at UtcOffset.MIN
    }
}

infix fun DateTime.at(offset: UtcOffset) = OffsetDateTime(this, offset)
infix fun Date.at(offsetTime: OffsetTime) = OffsetDateTime(this, offsetTime.time, offsetTime.offset)
infix fun OffsetTime.on(date: Date) = OffsetDateTime(date, time, offset)

inline val OffsetDateTime.hour: Int get() = dateTime.hour
inline val OffsetDateTime.minute: Int get() = dateTime.minute
inline val OffsetDateTime.second: Int get() = dateTime.second
inline val OffsetDateTime.nanoOfSecond: Int get() = dateTime.nanoOfSecond
inline val OffsetDateTime.month: Month get() = dateTime.month
inline val OffsetDateTime.dayOfWeek: DayOfWeek get() = dateTime.dayOfWeek
inline val OffsetDateTime.dayOfMonth: Int get() = dateTime.dayOfMonth
inline val OffsetDateTime.dayOfYear: Int get() = dateTime.dayOfYear
inline val OffsetDateTime.year: Int get() = dateTime.year
inline val OffsetDateTime.isInLeapYear: Boolean get() = dateTime.isInLeapYear
inline val OffsetDateTime.isLeapDay: Boolean get() = dateTime.isLeapDay
inline val OffsetDateTime.lengthOfMonth: IntDays get() = dateTime.lengthOfMonth
inline val OffsetDateTime.lengthOfYear: IntDays get() = dateTime.lengthOfYear

val OffsetDateTime.unixEpochSeconds: LongSeconds
    get() = date.unixEpochDays + time.secondOfDay.seconds - offset.totalSeconds

/**
 * Change the offset of an OffsetDateTime, adjusting the date and time components such that the instant represented by
 * it remains the same
 */
fun OffsetDateTime.adjustedTo(newOffset: UtcOffset): OffsetDateTime {
    return if (newOffset == offset) {
        this
    } else {
        val newDateTime = dateTime + (newOffset.totalSeconds - offset.totalSeconds)
        OffsetDateTime(newDateTime, newOffset)
    }
}

operator fun OffsetDateTime.plus(daysToAdd: LongDays): OffsetDateTime {
    return if (daysToAdd == 0L.days) {
        this
    } else {
        copy(dateTime = dateTime + daysToAdd)
    }
}

operator fun OffsetDateTime.plus(daysToAdd: IntDays) = plus(daysToAdd.toLong())

operator fun OffsetDateTime.plus(monthsToAdd: LongMonths): OffsetDateTime {
    return if (monthsToAdd == 0L.months) {
        this
    } else {
        copy(dateTime = dateTime + monthsToAdd)
    }
}

operator fun OffsetDateTime.plus(monthsToAdd: IntMonths) = plus(monthsToAdd.toLong())

operator fun OffsetDateTime.plus(yearsToAdd: LongYears): OffsetDateTime {
    return if (yearsToAdd == 0L.years) {
        this
    } else {
        copy(dateTime = dateTime + yearsToAdd)
    }
}

operator fun OffsetDateTime.plus(yearsToAdd: IntYears) = plus(yearsToAdd.toLong())

operator fun OffsetDateTime.plus(hoursToAdd: LongHours): OffsetDateTime {
    return if (hoursToAdd == 0L.hours) {
        this
    } else {
        copy(dateTime = dateTime + hoursToAdd)
    }
}

operator fun OffsetDateTime.plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

operator fun OffsetDateTime.plus(minutesToAdd: LongMinutes): OffsetDateTime {
    return if (minutesToAdd == 0L.minutes) {
        this
    } else {
        copy(dateTime = dateTime + minutesToAdd)
    }
}

operator fun OffsetDateTime.plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

operator fun OffsetDateTime.plus(secondsToAdd: LongSeconds): OffsetDateTime {
    return if (secondsToAdd == 0L.seconds) {
        this
    } else {
        copy(dateTime = dateTime + secondsToAdd)
    }
}

operator fun OffsetDateTime.plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

operator fun OffsetDateTime.plus(nanosecondsToAdd: LongNanoseconds): OffsetDateTime {
    return if (nanosecondsToAdd == 0L.nanoseconds) {
        this
    } else {
        copy(dateTime = dateTime + nanosecondsToAdd)
    }
}

operator fun OffsetDateTime.plus(nanosecondsToAdd: IntNanoseconds) = plus(nanosecondsToAdd.toLong())

operator fun OffsetDateTime.minus(daysToSubtract: LongDays) = plus(-daysToSubtract)
operator fun OffsetDateTime.minus(monthsToSubtract: IntMonths) = plus(-monthsToSubtract)
operator fun OffsetDateTime.minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)
operator fun OffsetDateTime.minus(hoursToSubtract: IntHours) = plus(-hoursToSubtract)
operator fun OffsetDateTime.minus(minutesToSubtract: LongMinutes) = plus(-minutesToSubtract)
operator fun OffsetDateTime.minus(minutesToSubtract: IntMinutes) = plus(-minutesToSubtract)
operator fun OffsetDateTime.minus(secondsToSubtract: LongSeconds) = plus(-secondsToSubtract)
operator fun OffsetDateTime.minus(secondsToSubtract: IntSeconds) = plus(-secondsToSubtract)
operator fun OffsetDateTime.minus(nanosecondsToSubtract: LongNanoseconds) = plus(-nanosecondsToSubtract)
operator fun OffsetDateTime.minus(nanosecondsToSubtract: IntNanoseconds) = plus(-nanosecondsToSubtract)

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

        if (offset.isZero) {
            append('Z')
        } else {
            appendUtcOffset(offset)
        }
    }
    return this
}