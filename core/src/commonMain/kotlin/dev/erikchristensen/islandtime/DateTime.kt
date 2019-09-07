package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.*
import dev.erikchristensen.islandtime.internal.*
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.parser.raiseParserFieldResolutionException

class DateTime(
    val date: Date,
    val time: Time
) : Comparable<DateTime> {

    constructor(
        year: Int,
        month: Month,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanoOfSecond: Int = 0
    ) : this(Date(year, month, day), Time(hour, minute, second, nanoOfSecond))

    constructor(
        year: Int,
        monthNumber: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanoOfSecond: Int = 0
    ) : this(year, Month(monthNumber), day, hour, minute, second, nanoOfSecond)

    operator fun component1() = date
    operator fun component2() = time

    override fun compareTo(other: DateTime): Int {
        val dateDiff = date.compareTo(other.date)

        return if (dateDiff != 0) {
            dateDiff
        } else {
            time.compareTo(other.time)
        }
    }

    override fun toString() = buildString(MAX_DATE_TIME_STRING_LENGTH) { appendDateTime(this@DateTime) }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is DateTime && date == other.date && time == other.time)
    }

    override fun hashCode(): Int {
        return 31 * date.hashCode() + time.hashCode()
    }

    fun copy(
        date: Date = this.date,
        time: Time = this.time
    ) = DateTime(date, time)

    /**
     * Return a new [DateTime], replacing any of the components with new values
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanoOfSecond: Int = this.nanoOfSecond
    ) = DateTime(date.copy(year, dayOfYear), time.copy(hour, minute, second, nanoOfSecond))

    /**
     * Return a new [DateTime], replacing any of the components with new values
     */
    fun copy(
        year: Int = this.year,
        month: Month = this.month,
        dayOfMonth: Int = this.dayOfMonth,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanoOfSecond: Int = this.nanoOfSecond
    ) = DateTime(date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanoOfSecond))

    /**
     * Return a new [DateTime], replacing any of the components with new values
     */
    fun copy(
        year: Int = this.year,
        monthNumber: Int,
        dayOfMonth: Int = this.dayOfMonth,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanoOfSecond: Int = this.nanoOfSecond
    ) = DateTime(year, Month(monthNumber), dayOfMonth, hour, minute, second, nanoOfSecond)

    companion object {
        val MIN = DateTime(Date.MIN, Time.MIN)
        val MAX = DateTime(Date.MAX, Time.MAX)

        fun ofUnixEpochMilliseconds(unixEpochMilliseconds: LongMilliseconds, offset: UtcOffset): DateTime {
            val localMilliseconds = unixEpochMilliseconds + offset.totalSeconds
            val localEpochDays = localMilliseconds.toWholeDays()
            val remainingNanoseconds = (localMilliseconds - localEpochDays).asNanoseconds()
            val date = Date.ofUnixEpochDays(localEpochDays)
            val time = Time.ofNanosecondOfDay(remainingNanoseconds.value)
            return DateTime(date, time)
        }
    }
}

/**
 * Combine a Time with a Date to create a DateTime
 */
infix fun Time.on(date: Date) = DateTime(date, this)

/**
 * Combine a Date with a Time to create a DateTime
 */
infix fun Date.at(time: Time) = DateTime(this, time)

fun Instant.toDateTime(offset: UtcOffset): DateTime {
    return DateTime.ofUnixEpochMilliseconds(unixEpochMilliseconds, offset)
}

fun Instant.toDateTime(timeZone: TimeZone): DateTime {
    val offset = timeZone.rules.offsetAt(this)
    return DateTime.ofUnixEpochMilliseconds(unixEpochMilliseconds, offset)
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

/**
 * Get the year and month of this date
 */
inline val DateTime.yearMonth: YearMonth get() = date.yearMonth

operator fun DateTime.plus(daysToAdd: LongDays): DateTime {
    return if (daysToAdd == 0L.days) {
        this
    } else {
        copy(date = date + daysToAdd)
    }
}

operator fun DateTime.plus(daysToAdd: IntDays) = plus(daysToAdd.toLong())
operator fun DateTime.plus(monthsToAdd: LongMonths) = plus(monthsToAdd.toInt())

operator fun DateTime.plus(monthsToAdd: IntMonths): DateTime {
    return if (monthsToAdd == 0.months) {
        this
    } else {
        copy(date = date + monthsToAdd)
    }
}

operator fun DateTime.plus(yearsToAdd: LongYears) = plus(yearsToAdd.toInt())

operator fun DateTime.plus(yearsToAdd: IntYears): DateTime {
    return if (yearsToAdd == 0.years) {
        this
    } else {
        copy(date = date + yearsToAdd)
    }
}

operator fun DateTime.plus(hoursToAdd: LongHours): DateTime {
    return if (hoursToAdd.value == 0L) {
        this
    } else {
        var daysToAdd = hoursToAdd.toWholeDays()
        val wrappedHours = (hoursToAdd % HOURS_PER_DAY).toInt()
        var newHour = time.hour + wrappedHours.value

        if (newHour >= HOURS_PER_DAY.toInt()) {
            daysToAdd += 1.days
            newHour -= HOURS_PER_DAY.toInt()
        } else if (newHour < 0) {
            daysToAdd -= 1.days
            newHour += HOURS_PER_DAY.toInt()
        }

        val newDate = date + daysToAdd
        val newTime = time.copy(hour = newHour)
        DateTime(newDate, newTime)
    }
}

operator fun DateTime.plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

operator fun DateTime.plus(minutesToAdd: LongMinutes): DateTime {
    return if (minutesToAdd.value == 0L) {
        this
    } else {
        var daysToAdd = minutesToAdd.toWholeDays()
        val currentMinuteOfDay = time.hour * MINUTES_PER_HOUR.toInt() + minute
        val wrappedMinutes = (minutesToAdd % MINUTES_PER_DAY).toInt()
        var newMinuteOfDay = currentMinuteOfDay + wrappedMinutes.value

        if (newMinuteOfDay >= MINUTES_PER_DAY.toInt()) {
            daysToAdd += 1.days
            newMinuteOfDay -= MINUTES_PER_DAY.toInt()
        } else if (newMinuteOfDay < 0) {
            daysToAdd -= 1.days
            newMinuteOfDay += MINUTES_PER_DAY.toInt()
        }

        val newDate = date + daysToAdd

        val newTime = if (currentMinuteOfDay == newMinuteOfDay) {
            time
        } else {
            val newHour = newMinuteOfDay / MINUTES_PER_HOUR.toInt()
            val newMinute = newMinuteOfDay % MINUTES_PER_HOUR.toInt()
            Time(newHour, newMinute, time.second, time.nanoOfSecond)
        }

        DateTime(newDate, newTime)
    }
}

operator fun DateTime.plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

operator fun DateTime.plus(secondsToAdd: LongSeconds): DateTime {
    return if (secondsToAdd.value == 0L) {
        this
    } else {
        var daysToAdd = secondsToAdd.toWholeDays()
        val currentSecondOfDay = time.secondOfDay
        val wrappedSeconds = (secondsToAdd % SECONDS_PER_DAY).toInt()
        var newSecondOfDay = currentSecondOfDay + wrappedSeconds.value

        if (newSecondOfDay >= SECONDS_PER_DAY.toInt()) {
            daysToAdd += 1.days
            newSecondOfDay -= SECONDS_PER_DAY.toInt()
        } else if (newSecondOfDay < 0) {
            daysToAdd -= 1.days
            newSecondOfDay += SECONDS_PER_DAY.toInt()
        }

        val newDate = date + daysToAdd

        val newTime = if (currentSecondOfDay == newSecondOfDay) {
            time
        } else {
            Time.ofSecondOfDay(newSecondOfDay, time.nanoOfSecond)
        }

        DateTime(newDate, newTime)
    }
}

operator fun DateTime.plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

operator fun DateTime.plus(nanosecondsToAdd: LongNanoseconds): DateTime {
    return if (nanosecondsToAdd.value == 0L) {
        this
    } else {
        var daysToAdd = nanosecondsToAdd.toWholeDays()
        val currentNanosecondOfDay = time.nanosecondOfDay
        val wrappedNanoseconds = nanosecondsToAdd % NANOSECONDS_PER_DAY
        var newNanosecondOfDay = currentNanosecondOfDay + wrappedNanoseconds.value

        if (newNanosecondOfDay >= NANOSECONDS_PER_DAY) {
            daysToAdd += 1.days
            newNanosecondOfDay -= NANOSECONDS_PER_DAY
        } else if (newNanosecondOfDay < 0) {
            daysToAdd -= 1.days
            newNanosecondOfDay += NANOSECONDS_PER_DAY
        }

        val newDate = date + daysToAdd

        val newTime = if (currentNanosecondOfDay == newNanosecondOfDay) {
            time
        } else {
            Time.ofNanosecondOfDay(newNanosecondOfDay)
        }

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