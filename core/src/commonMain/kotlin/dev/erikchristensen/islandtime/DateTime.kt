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

    operator fun plus(years: IntYears) = plus(years.toLong())

    operator fun plus(years: LongYears): DateTime {
        return if (years.value == 0L) {
            this
        } else {
            copy(date = date + years)
        }
    }

    operator fun plus(months: IntMonths) = plus(months.toLong())

    operator fun plus(months: LongMonths): DateTime {
        return if (months.value == 0L) {
            this
        } else {
            copy(date = date + months)
        }
    }

    operator fun plus(days: IntDays) = plus(days.toLong())

    operator fun plus(days: LongDays): DateTime {
        return if (days.value == 0L) {
            this
        } else {
            copy(date = date + days)
        }
    }

    operator fun plus(hours: IntHours) = plus(hours.toLong())

    operator fun plus(hours: LongHours): DateTime {
        return if (hours.value == 0L) {
            this
        } else {
            var daysToAdd = hours.inWholeDays
            val wrappedHours = (hours % HOURS_PER_DAY).toInt()
            var newHour = time.hour + wrappedHours.value
            daysToAdd += (newHour floorDiv HOURS_PER_DAY).days
            newHour = newHour floorMod HOURS_PER_DAY

            val newDate = date + daysToAdd
            val newTime = time.copy(hour = newHour)
            DateTime(newDate, newTime)
        }
    }

    operator fun plus(minutes: IntMinutes) = plus(minutes.toLong())

    operator fun plus(minutes: LongMinutes): DateTime {
        return if (minutes.value == 0L) {
            this
        } else {
            var daysToAdd = minutes.inWholeDays
            val currentMinuteOfDay = time.hour * MINUTES_PER_HOUR + minute
            val wrappedMinutes = (minutes % MINUTES_PER_DAY).toInt()
            var newMinuteOfDay = currentMinuteOfDay + wrappedMinutes.value
            daysToAdd += (newMinuteOfDay floorDiv MINUTES_PER_DAY).days
            newMinuteOfDay = newMinuteOfDay floorMod MINUTES_PER_DAY

            val newDate = date + daysToAdd

            val newTime = if (currentMinuteOfDay == newMinuteOfDay) {
                time
            } else {
                val newHour = newMinuteOfDay / MINUTES_PER_HOUR
                val newMinute = newMinuteOfDay % MINUTES_PER_HOUR
                Time(newHour, newMinute, time.second, time.nanosecond)
            }

            DateTime(newDate, newTime)
        }
    }

    operator fun plus(seconds: IntSeconds) = plus(seconds.toLong())

    operator fun plus(seconds: LongSeconds): DateTime {
        return if (seconds.value == 0L) {
            this
        } else {
            var daysToAdd = seconds.inWholeDays
            val currentSecondOfDay = time.secondOfDay
            val wrappedSeconds = (seconds % SECONDS_PER_DAY).toInt()
            var newSecondOfDay = currentSecondOfDay + wrappedSeconds.value
            daysToAdd += (newSecondOfDay floorDiv SECONDS_PER_DAY).days
            newSecondOfDay = newSecondOfDay floorMod SECONDS_PER_DAY

            val newDate = date + daysToAdd

            val newTime = if (currentSecondOfDay == newSecondOfDay) {
                time
            } else {
                Time.fromSecondOfDay(newSecondOfDay, time.nanosecond)
            }

            DateTime(newDate, newTime)
        }
    }

    operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.toLong())

    operator fun plus(milliseconds: LongMilliseconds): DateTime {
        return if (milliseconds.value == 0L) {
            this
        } else {
            plus(milliseconds.inWholeDays, (milliseconds % MILLISECONDS_PER_DAY).inNanoseconds)
        }
    }

    operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.toLong())

    operator fun plus(microseconds: LongMicroseconds): DateTime {
        return if (microseconds.value == 0L) {
            this
        } else {
            plus(microseconds.inWholeDays, (microseconds % MICROSECONDS_PER_DAY).inNanoseconds)
        }
    }

    operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLong())

    operator fun plus(nanoseconds: LongNanoseconds): DateTime {
        return if (nanoseconds.value == 0L) {
            this
        } else {
            plus(nanoseconds.inWholeDays, nanoseconds % NANOSECONDS_PER_DAY)
        }
    }

    private fun plus(days: LongDays, wrappedNanoseconds: LongNanoseconds): DateTime {
        val currentNanosecondOfDay = time.nanosecondOfDay
        var newNanosecondOfDay = currentNanosecondOfDay + wrappedNanoseconds.value
        val daysToAdd = days + (newNanosecondOfDay floorDiv NANOSECONDS_PER_DAY).days
        newNanosecondOfDay = newNanosecondOfDay floorMod NANOSECONDS_PER_DAY

        val newDate = date + daysToAdd

        val newTime = if (currentNanosecondOfDay == newNanosecondOfDay) {
            time
        } else {
            Time.fromNanosecondOfDay(newNanosecondOfDay)
        }

        return DateTime(newDate, newTime)
    }

    operator fun minus(years: IntYears) = plus(-years.toLong())

    operator fun minus(years: LongYears): DateTime {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1.years
        } else {
            plus(-years)
        }
    }

    operator fun minus(months: IntMonths) = plus(-months.toLong())

    operator fun minus(months: LongMonths): DateTime {
        return if (months.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.months + 1.months
        } else {
            plus(-months)
        }
    }

    operator fun minus(days: IntDays) = plus(-days.toLong())

    operator fun minus(days: LongDays): DateTime {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(-days)
        }
    }

    operator fun minus(hours: IntHours) = plus(-hours.toLong())

    operator fun minus(hours: LongHours): DateTime {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(-hours)
        }
    }

    operator fun minus(minutes: IntMinutes) = plus(-minutes.toLong())

    operator fun minus(minutes: LongMinutes): DateTime {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(-minutes)
        }
    }

    operator fun minus(seconds: IntSeconds) = plus(-seconds.toLong())

    operator fun minus(seconds: LongSeconds): DateTime {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(-seconds)
        }
    }

    operator fun minus(milliseconds: IntMilliseconds) = plus(-milliseconds.toLong())

    operator fun minus(milliseconds: LongMilliseconds): DateTime {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(-milliseconds)
        }
    }

    operator fun minus(microseconds: IntMicroseconds) = plus(-microseconds.toLong())

    operator fun minus(microseconds: LongMicroseconds): DateTime {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(-microseconds)
        }
    }

    operator fun minus(nanoseconds: IntNanoseconds) = plus(-nanoseconds.toLong())

    operator fun minus(nanoseconds: LongNanoseconds): DateTime {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(-nanoseconds)
        }
    }

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
    ) = DateTime(date.copy(year, monthNumber, dayOfMonth), time.copy(hour, minute, second, nanosecond))

    /**
     * Truncate to the [hour] value, replacing all smaller components with zero
     */
    fun truncatedToHours() = copy(time = time.truncatedToHours())

    /**
     * Truncate to the [minute] value, replacing all smaller components with zero
     */
    fun truncatedToMinutes() = copy(time = time.truncatedToMinutes())

    /**
     * Truncate to the [second] value, replacing all smaller components with zero
     */
    fun truncatedToSeconds() = copy(time = time.truncatedToSeconds())

    /**
     * Truncate the [nanosecond] value to milliseconds, replacing the rest with zero
     */
    fun truncatedToMilliseconds() = copy(time = time.truncatedToMilliseconds())

    /**
     * Truncate the [nanosecond] value to microseconds, replacing the rest with zero
     */
    fun truncatedToMicroseconds() = copy(time = time.truncatedToMicroseconds())

    fun secondsSinceUnixEpochAt(offset: UtcOffset): LongSeconds {
        return date.daysSinceUnixEpoch + time.secondsSinceStartOfDay - offset.totalSeconds
    }

    fun millisecondsSinceUnixEpochAt(offset: UtcOffset): LongMilliseconds {
        return date.daysSinceUnixEpoch +
            time.secondsSinceStartOfDay +
            time.nanosecondsSinceStartOfDay.inWholeMilliseconds -
            offset.totalSeconds
    }

    fun unixEpochSecondAt(offset: UtcOffset): Long = secondsSinceUnixEpochAt(offset).value
    fun unixEpochMillisecondAt(offset: UtcOffset): Long = millisecondsSinceUnixEpochAt(offset).value

    fun instantAt(offset: UtcOffset): Instant {
        return Instant.fromUnixEpochSecond(unixEpochSecondAt(offset), nanosecond)
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
                (localMilliseconds.value floorMod MILLISECONDS_PER_DAY).milliseconds.inNanoseconds.value
            val date = Date.fromDaysSinceUnixEpoch(localEpochDays)
            val time = Time.fromNanosecondOfDay(nanosecondOfDay)
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
            val adjustedSeconds = seconds + (nanosecondAdjustment.value floorDiv NANOSECONDS_PER_SECOND).seconds
            val nanosecondOfDay = nanosecondAdjustment.value floorMod NANOSECONDS_PER_SECOND
            return fromSecondsSinceUnixEpoch(adjustedSeconds, nanosecondOfDay, offset)
        }

        /**
         * Create the [DateTime] that falls a given number of seconds relative to the Unix epoch, plus the nanosecond
         * of day value.
         */
        private fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondOfDay: Int,
            offset: UtcOffset
        ): DateTime {
            val localSeconds = seconds + offset.totalSeconds
            val localEpochDays = (localSeconds.value floorDiv SECONDS_PER_DAY).days
            val secondOfDay = (localSeconds.value floorMod SECONDS_PER_DAY).toInt()
            val date = Date.fromDaysSinceUnixEpoch(localEpochDays)
            val time = Time.fromSecondOfDay(secondOfDay, nanosecondOfDay)
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
val Date.startOfDay get() = DateTime(this, Time.MIDNIGHT)

/**
 * Get the [DateTime] at the end of the day
 */
val Date.endOfDay get() = DateTime(this, Time.MAX)

fun Instant.toDateTimeAt(offset: UtcOffset): DateTime {
    return DateTime.fromDurationSinceUnixEpoch(durationSinceUnixEpoch, offset)
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