package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.*
import dev.erikchristensen.islandtime.date.MAX_DATE_STRING_LENGTH
import dev.erikchristensen.islandtime.internal.HOURS_PER_DAY
import dev.erikchristensen.islandtime.internal.MINUTES_PER_DAY
import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_DAY
import dev.erikchristensen.islandtime.internal.SECONDS_PER_DAY
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
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
inline val DateTime.lengthOfMonth: DaySpan get() = date.lengthOfMonth
inline val DateTime.lengthOfYear: DaySpan get() = date.lengthOfYear

operator fun DateTime.plus(daysToAdd: LongDaySpan) = date.plus(daysToAdd)
operator fun DateTime.plus(daysToAdd: DaySpan) = date.plus(daysToAdd)
operator fun DateTime.plus(monthsToAdd: LongMonthSpan) = date.plus(monthsToAdd)
operator fun DateTime.plus(monthsToAdd: MonthSpan) = date.plus(monthsToAdd)
operator fun DateTime.plus(yearsToAdd: LongYearSpan) = date.plus(yearsToAdd)
operator fun DateTime.plus(yearsToAdd: YearSpan) = date.plus(yearsToAdd)

operator fun DateTime.plus(hoursToAdd: LongHourSpan): DateTime {
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

operator fun DateTime.plus(hoursToAdd: HourSpan) = plus(hoursToAdd.toLong())

operator fun DateTime.plus(minutesToAdd: LongMinuteSpan): DateTime {
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

operator fun DateTime.plus(minutesToAdd: MinuteSpan) = plus(minutesToAdd.toLong())

operator fun DateTime.plus(secondsToAdd: LongSecondSpan): DateTime {
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

operator fun DateTime.plus(secondsToAdd: SecondSpan) = plus(secondsToAdd.toLong())

operator fun DateTime.plus(nanosecondsToAdd: LongNanosecondSpan): DateTime {
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

operator fun DateTime.plus(nanosecondsToAdd: NanosecondSpan) = plus(nanosecondsToAdd.toLong())

operator fun DateTime.minus(daysToSubtract: LongDaySpan) = plus(-daysToSubtract)
operator fun DateTime.minus(monthsToSubtract: MonthSpan) = plus(-monthsToSubtract)
operator fun DateTime.minus(yearsToSubtract: YearSpan) = plus(-yearsToSubtract)
operator fun DateTime.minus(hoursToSubtract: HourSpan) = plus(-hoursToSubtract)
operator fun DateTime.minus(minutesToSubtract: LongMinuteSpan) = plus(-minutesToSubtract)
operator fun DateTime.minus(minutesToSubtract: MinuteSpan) = plus(-minutesToSubtract)
operator fun DateTime.minus(secondsToSubtract: LongSecondSpan) = plus(-secondsToSubtract)
operator fun DateTime.minus(secondsToSubtract: SecondSpan) = plus(-secondsToSubtract)
operator fun DateTime.minus(nanosecondsToSubtract: LongNanosecondSpan) = plus(-nanosecondsToSubtract)
operator fun DateTime.minus(nanosecondsToSubtract: NanosecondSpan) = plus(-nanosecondsToSubtract)

fun String.toDateTime() = toDateTime(Iso8601.Extended.DATE_TIME_PARSER)

fun String.toDateTime(parser: DateTimeParser): DateTime {
    val result = parser.parse(this)
    return result.toDateTime()
}

internal fun DateTimeParseResult.toDateTime(): DateTime {
    val date = this.toDate()
    val time = this.toTime()
    return DateTime(date, time)
}

internal const val MAX_DATE_TIME_STRING_LENGTH = MAX_DATE_STRING_LENGTH + 1 + MAX_TIME_STRING_LENGTH

internal fun StringBuilder.appendDateTime(dateTime: DateTime): StringBuilder {
    return dateTime.run { appendDate(date).append('T').appendTime(time) }
}