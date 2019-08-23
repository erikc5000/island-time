package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.*
import dev.erikchristensen.islandtime.internal.HOURS_PER_DAY
import dev.erikchristensen.islandtime.internal.MINUTES_PER_DAY
import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_DAY
import dev.erikchristensen.islandtime.internal.SECONDS_PER_DAY
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.parser.raiseParserFieldResolutionException
import kotlin.jvm.JvmField

data class DateTime(
    val date: Date,
    val time: Time
) : Comparable<DateTime> {

    override fun compareTo(other: DateTime): Int {
        val dateDiff = date.compareTo(other.date)

        return if (dateDiff != 0) {
            dateDiff
        } else {
            time.compareTo(other.time)
        }
    }

    override fun toString() = buildString(MAX_DATE_TIME_STRING_LENGTH) { appendDateTime(this@DateTime) }

    companion object {
        @JvmField
        val MIN = DateTime(Date.MIN, Time.MIN)

        @JvmField
        val MAX = DateTime(Date.MAX, Time.MAX)
    }
}

inline val DateTime.hour: Int get() = time.hour
inline val DateTime.minute: Int get() = time.minute
inline val DateTime.second: Int get() = time.second
inline val DateTime.nanoOfSecond: Int get() = time.nanoOfSecond
inline val DateTime.month: Month get() = date.month
inline val DateTime.dayOfWeek: DayOfWeek get() = date.dayOfWeek
inline val DateTime.dayOfMonth: Int get() = date.dayOfMonth
inline val DateTime.dayOfYear: Int get() = date.dayOfYear
inline val DateTime.year: Int get() = date.year
inline val DateTime.isInLeapYear: Boolean get() = date.isInLeapYear
inline val DateTime.isLeapDay: Boolean get() = date.isLeapDay
inline val DateTime.lengthOfMonth: IntDays get() = date.lengthOfMonth
inline val DateTime.lengthOfYear: IntDays get() = date.lengthOfYear

operator fun DateTime.plus(daysToAdd: LongDays) = date.plus(daysToAdd)
operator fun DateTime.plus(daysToAdd: IntDays) = date.plus(daysToAdd)
operator fun DateTime.plus(monthsToAdd: LongMonths) = date.plus(monthsToAdd)
operator fun DateTime.plus(monthsToAdd: IntMonths) = date.plus(monthsToAdd)
operator fun DateTime.plus(yearsToAdd: LongYears) = date.plus(yearsToAdd)
operator fun DateTime.plus(yearsToAdd: IntYears) = date.plus(yearsToAdd)

operator fun DateTime.plus(hoursToAdd: LongHours): DateTime {
    return if (hoursToAdd.value == 0L) {
        this
    } else {
        val daysToAdd = (hoursToAdd.value / HOURS_PER_DAY).days
        val wrappedHours = hoursToAdd % HOURS_PER_DAY
        val newDate = date + daysToAdd
        val newTime = time + wrappedHours
        DateTime(newDate, newTime)
    }
}

operator fun DateTime.plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

operator fun DateTime.plus(minutesToAdd: LongMinutes): DateTime {
    return if (minutesToAdd.value == 0L) {
        this
    } else {
        val daysToAdd = (minutesToAdd.value / MINUTES_PER_DAY).days
        val wrappedMinutes = (minutesToAdd % MINUTES_PER_DAY).toInt()
        val newDate = date + daysToAdd
        val newTime = time + wrappedMinutes
        DateTime(newDate, newTime)
    }
}

operator fun DateTime.plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

operator fun DateTime.plus(secondsToAdd: LongSeconds): DateTime {
    return if (secondsToAdd.value == 0L) {
        this
    } else {
        val daysToAdd = (secondsToAdd.value / SECONDS_PER_DAY).days
        val wrappedSeconds = (secondsToAdd % SECONDS_PER_DAY).toInt()
        val newDate = date + daysToAdd
        val newTime = time + wrappedSeconds
        DateTime(newDate, newTime)
    }
}

operator fun DateTime.plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

operator fun DateTime.plus(nanosecondsToAdd: LongNanoseconds): DateTime {
    return if (nanosecondsToAdd.value == 0L) {
        this
    } else {
        val daysToAdd = (nanosecondsToAdd.value / NANOSECONDS_PER_DAY).days
        val wrappedNanoseconds = nanosecondsToAdd % NANOSECONDS_PER_DAY
        val newDate = date + daysToAdd
        val newTime = time + wrappedNanoseconds
        DateTime(newDate, newTime)
    }
}

operator fun DateTime.plus(nanosecondsToAdd: IntNanoseconds) = plus(nanosecondsToAdd.toLong())

operator fun DateTime.minus(daysToSubtract: LongDays) = plus(-daysToSubtract)
operator fun DateTime.minus(monthsToSubtract: IntMonths) = plus(-monthsToSubtract)
operator fun DateTime.minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)
operator fun DateTime.minus(hoursToSubtract: IntHours) = plus(-hoursToSubtract)
operator fun DateTime.minus(minutesToSubtract: LongMinutes) = plus(-minutesToSubtract)
operator fun DateTime.minus(minutesToSubtract: IntMinutes) = plus(-minutesToSubtract)
operator fun DateTime.minus(secondsToSubtract: LongSeconds) = plus(-secondsToSubtract)
operator fun DateTime.minus(secondsToSubtract: IntSeconds) = plus(-secondsToSubtract)
operator fun DateTime.minus(nanosecondsToSubtract: LongNanoseconds) = plus(-nanosecondsToSubtract)
operator fun DateTime.minus(nanosecondsToSubtract: IntNanoseconds) = plus(-nanosecondsToSubtract)

/**
 * Parse a string in ISO-8601 extended calendar date format into a [DateTime] -- for example, "2019-08-22T18:00" or
 * "2019-08-22 18:00:30.123456789"
 */
fun String.toDateTime() = toDateTime(Iso8601.Extended.CALENDAR_DATE_TIME_PARSER)

/**
 * Parse a string into a [DateTime] using a [DateTimeParser] capable of supplying the necessary fields
 */
fun String.toDateTime(parser: DateTimeParser): DateTime {
    val result = parser.parse(this)
    return result.toDateTime() ?: raiseParserFieldResolutionException("DateTime", this)
}

internal fun DateTimeParseResult.toDateTime(): DateTime? {
    val date = this.toDate()
    val time = this.toTime()

    return if (date != null && time != null) {
        DateTime(date, time)
    } else {
        null
    }
}

internal const val MAX_DATE_TIME_STRING_LENGTH = MAX_DATE_STRING_LENGTH + 1 + MAX_TIME_STRING_LENGTH

internal fun StringBuilder.appendDateTime(dateTime: DateTime): StringBuilder {
    return dateTime.run { appendDate(date).append('T').appendTime(time) }
}