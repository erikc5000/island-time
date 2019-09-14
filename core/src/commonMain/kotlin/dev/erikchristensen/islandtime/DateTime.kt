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
        nanosecond: Int = 0
    ) : this(Date(year, month, day), Time(hour, minute, second, nanosecond))

    constructor(
        year: Int,
        monthNumber: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanosecond: Int = 0
    ) : this(year, Month(monthNumber), day, hour, minute, second, nanosecond)

    /**
     * The hour of the day
     */
    inline val hour: Int get() = time.hour

    /**
     * The minute within the hour
     */
    inline val minute: Int get() = time.minute

    /**
     * The second within the minute
     */
    inline val second: Int get() = time.second

    /**
     * The nanosecond within the second
     */
    inline val nanosecond: Int get() = time.nanosecond

    /**
     * The month of the year
     */
    inline val month: Month get() = date.month

    /**
     * The ISO month number
     */
    inline val monthNumber: Int get() = month.number

    /**
     * The day of the week
     */
    inline val dayOfWeek: DayOfWeek get() = date.dayOfWeek

    /**
     * The day of the month
     */
    inline val dayOfMonth: Int get() = date.dayOfMonth

    /**
     * The day of the year
     */
    inline val dayOfYear: Int get() = date.dayOfYear

    /**
     * The year
     */
    inline val year: Int get() = date.year

    /**
     * true if this date falls within a leap year
     */
    inline val isInLeapYear: Boolean get() = date.isInLeapYear

    /**
     * true if this is a leap day
     */
    inline val isLeapDay: Boolean get() = date.isLeapDay

    /**
     * The length of the date's month in days
     */
    inline val lengthOfMonth: IntDays get() = date.lengthOfMonth

    /**
     * The length of the date's year in days
     */
    inline val lengthOfYear: IntDays get() = date.lengthOfYear

    /**
     * Get the year and month of this date
     */
    inline val yearMonth: YearMonth get() = date.yearMonth

    operator fun plus(daysToAdd: LongDays): DateTime {
        return if (daysToAdd == 0L.days) {
            this
        } else {
            copy(date = date + daysToAdd)
        }
    }

    operator fun plus(daysToAdd: IntDays) = plus(daysToAdd.toLong())
    operator fun plus(monthsToAdd: LongMonths) = plus(monthsToAdd.toInt())

    operator fun plus(monthsToAdd: IntMonths): DateTime {
        return if (monthsToAdd == 0.months) {
            this
        } else {
            copy(date = date + monthsToAdd)
        }
    }

    operator fun plus(yearsToAdd: LongYears) = plus(yearsToAdd.toInt())

    operator fun plus(yearsToAdd: IntYears): DateTime {
        return if (yearsToAdd == 0.years) {
            this
        } else {
            copy(date = date + yearsToAdd)
        }
    }

    operator fun plus(hoursToAdd: LongHours): DateTime {
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

    operator fun plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

    operator fun plus(minutesToAdd: LongMinutes): DateTime {
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
                Time(newHour, newMinute, time.second, time.nanosecond)
            }

            DateTime(newDate, newTime)
        }
    }

    operator fun plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

    operator fun plus(secondsToAdd: LongSeconds): DateTime {
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
                Time.ofSecondOfDay(newSecondOfDay, time.nanosecond)
            }

            DateTime(newDate, newTime)
        }
    }

    operator fun plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

    operator fun plus(nanosecondsToAdd: LongNanoseconds): DateTime {
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
        nanosecond: Int = this.nanosecond
    ) = DateTime(date.copy(year, dayOfYear), time.copy(hour, minute, second, nanosecond))

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
        nanosecond: Int = this.nanosecond
    ) = DateTime(date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanosecond))

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
        nanosecond: Int = this.nanosecond
    ) = DateTime(year, Month(monthNumber), dayOfMonth, hour, minute, second, nanosecond)

    fun secondsSinceUnixEpochAt(offset: UtcOffset): LongSeconds {
        return date.daysSinceUnixEpoch + time.secondsSinceStartOfDay - offset.totalSeconds
    }

    fun millisecondsSinceUnixEpochAt(offset: UtcOffset): LongMilliseconds {
        return date.daysSinceUnixEpoch +
            time.secondsSinceStartOfDay +
            time.nanosecondsSinceStartOfDay.toWholeMilliseconds() -
            offset.totalSeconds
    }

    companion object {
        val MIN = DateTime(Date.MIN, Time.MIN)
        val MAX = DateTime(Date.MAX, Time.MAX)

        fun fromMillisecondsSinceUnixEpoch(
            millisecondsSinceUnixEpoch: LongMilliseconds,
            offset: UtcOffset
        ): DateTime {
            val localMilliseconds = millisecondsSinceUnixEpoch + offset.totalSeconds
            val localEpochDays = (localMilliseconds.value floorDiv MILLISECONDS_PER_DAY).days
            val nanosecondOfDay =
                (localMilliseconds.value floorMod MILLISECONDS_PER_DAY).milliseconds.asNanoseconds().value
            val date = Date.fromDaysSinceUnixEpoch(localEpochDays)
            val time = Time.ofNanosecondOfDay(nanosecondOfDay)
            return DateTime(date, time)
        }

        /**
         * Create the [DateTime] that falls a given duration away from the Unix Epoch
         */
        fun fromDurationSinceUnixEpoch(
            duration: Duration,
            offset: UtcOffset
        ) = fromSecondsSinceUnixEpoch(duration.seconds, duration.nanosecondAdjustment, offset)

        /**
         * Create the [DateTime] that falls a given number of seconds relative to the Unix epoch, plus some number of
         * additional nanoseconds
         */
        fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds,
            offset: UtcOffset
        ): DateTime {
            val adjustedSeconds =
                seconds + (nanosecondAdjustment.value floorDiv NANOSECONDS_PER_SECOND.toInt()).seconds

            val nanosecondOfDay = nanosecondAdjustment.value floorMod NANOSECONDS_PER_SECOND.toInt()
            return fromSecondsSinceUnixEpoch(adjustedSeconds, nanosecondOfDay, offset)
        }

        /**
         * Create the [DateTime] that falls a given number of seconds relative to the Unix epoch, plus the nanosecond
         * of day value.
         */
        internal fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondOfDay: Int,
            offset: UtcOffset
        ): DateTime {
            val localSeconds = seconds + offset.totalSeconds
            val localEpochDays = (localSeconds.value floorDiv SECONDS_PER_DAY).days
            val secondOfDay = (localSeconds.value floorMod SECONDS_PER_DAY).toInt()
            val date = Date.fromDaysSinceUnixEpoch(localEpochDays)
            val time = Time.ofSecondOfDay(secondOfDay, nanosecondOfDay)
            return DateTime(date, time)
        }
    }
}

/**
 * Combine a Date with a Time to create a [DateTime]
 */
infix fun Date.at(time: Time) = DateTime(this, time)

/**
 * Get the [DateTime] at the start of the day
 */
fun Date.atStartOfDay() = DateTime(this, Time.MIDNIGHT)

/**
 * Get the [DateTime] at the end of the day
 */
fun Date.atEndOfDay() = DateTime(this, Time.MAX)

fun Instant.toDateTime(offset: UtcOffset): DateTime {
    return DateTime.fromMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch, offset)
}

fun Instant.toDateTime(timeZone: TimeZone): DateTime {
    val offset = timeZone.rules.offsetAt(this)
    return DateTime.fromMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch, offset)
}

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