package io.islandtime

import io.islandtime.base.*
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.parser.throwParserFieldResolutionException
import io.islandtime.ranges.DateTimeInterval

/**
 * A date and time of day in an arbitrary region.
 *
 * @constructor Create a [DateTime] by combining a [Date] and [Time].
 * @param date the date
 * @param time the time
 */
class DateTime(
    /** The date. */
    val date: Date,
    /** The time of day. */
    val time: Time
) : Temporal,
    Comparable<DateTime> {

    /**
     * Create a [DateTime].
     * @throws DateTimeException if the date-time is invalid
     */
    constructor(
        year: Int,
        month: Month,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanosecond: Int = 0
    ) : this(Date(year, month, day), Time(hour, minute, second, nanosecond))

    /**
     * Create a [DateTime].
     * @throws DateTimeException if the date-time is invalid
     */
    constructor(
        year: Int,
        monthNumber: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanosecond: Int = 0
    ) : this(year, monthNumber.toMonth(), day, hour, minute, second, nanosecond)

    /**
     * Create a [DateTime].
     * @throws DateTimeException if the date-time is invalid
     */
    constructor(
        year: Int,
        dayOfYear: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nanosecond: Int
    ) : this(Date(year, dayOfYear), Time(hour, minute, second, nanosecond))

    /**
     * The hour of the day.
     */
    inline val hour: Int get() = time.hour

    /**
     * The minute of the hour.
     */
    inline val minute: Int get() = time.minute

    /**
     * The second of the minute.
     */
    inline val second: Int get() = time.second

    /**
     * The nanosecond of the second.
     */
    inline val nanosecond: Int get() = time.nanosecond

    /**
     * The month of the year.
     */
    inline val month: Month get() = date.month

    /**
     * The ISO month number, from 1-12.
     */
    inline val monthNumber: Int get() = month.number

    /**
     * The day of the week.
     */
    inline val dayOfWeek: DayOfWeek get() = date.dayOfWeek

    /**
     * The day of the month.
     */
    inline val dayOfMonth: Int get() = date.dayOfMonth

    /**
     * The day of the year -- also known as the ordinal date in ISO-8601.
     */
    inline val dayOfYear: Int get() = date.dayOfYear

    /**
     * The year.
     */
    inline val year: Int get() = date.year

    /**
     * Check if this date falls within a leap year.
     */
    inline val isInLeapYear: Boolean get() = date.isInLeapYear

    /**
     * Check if this is a leap day.
     */
    inline val isLeapDay: Boolean get() = date.isLeapDay

    /**
     * The length of the date's month in days.
     */
    inline val lengthOfMonth: IntDays get() = date.lengthOfMonth

    /**
     * The length of the date's year in days.
     */
    inline val lengthOfYear: IntDays get() = date.lengthOfYear

    /**
     * The combined year and month.
     */
    inline val yearMonth: YearMonth get() = date.yearMonth

    /**
     * Return a [DateTime] with [period] added to it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range. This behavior is consistent with the order of operations for period addition as
     * defined in ISO-8601-2.
     */
    operator fun plus(period: Period): DateTime {
        return if (period.isZero()) {
            this
        } else {
            copy(date = date + period)
        }
    }

    operator fun plus(duration: Duration): DateTime {
        return this + duration.seconds + duration.nanosecondAdjustment
    }

    operator fun plus(years: IntYears) = plus(years.toLongYears())

    operator fun plus(years: LongYears): DateTime {
        return if (years.value == 0L) {
            this
        } else {
            copy(date = date + years)
        }
    }

    operator fun plus(months: IntMonths) = plus(months.toLongMonths())

    operator fun plus(months: LongMonths): DateTime {
        return if (months.value == 0L) {
            this
        } else {
            copy(date = date + months)
        }
    }
    
    operator fun plus(weeks: IntWeeks) = plus(weeks.toLongWeeks().inDaysUnchecked)
    
    operator fun plus(weeks: LongWeeks): DateTime {
        return if (weeks.value == 0L) {
            this
        } else {
            copy(date = date + weeks)
        }
    }

    operator fun plus(days: IntDays) = plus(days.toLongDays())

    operator fun plus(days: LongDays): DateTime {
        return if (days.value == 0L) {
            this
        } else {
            copy(date = date + days)
        }
    }

    operator fun plus(hours: IntHours) = plus(hours.toLongHours())

    operator fun plus(hours: LongHours): DateTime {
        return if (hours.value == 0L) {
            this
        } else {
            var daysToAdd = hours.inDays
            val wrappedHours = (hours % HOURS_PER_DAY).toInt()
            var newHour = time.hour + wrappedHours
            daysToAdd += (newHour floorDiv HOURS_PER_DAY).days
            newHour = newHour floorMod HOURS_PER_DAY

            val newDate = date + daysToAdd
            val newTime = time.copy(hour = newHour)
            DateTime(newDate, newTime)
        }
    }

    operator fun plus(minutes: IntMinutes) = plus(minutes.toLongMinutes())

    operator fun plus(minutes: LongMinutes): DateTime {
        return if (minutes.value == 0L) {
            this
        } else {
            var daysToAdd = minutes.inDays
            val currentMinuteOfDay = time.hour * MINUTES_PER_HOUR + minute
            val wrappedMinutes = (minutes % MINUTES_PER_DAY).toInt()
            var newMinuteOfDay = currentMinuteOfDay + wrappedMinutes
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

    operator fun plus(seconds: IntSeconds) = plus(seconds.toLongSeconds())

    operator fun plus(seconds: LongSeconds): DateTime {
        return if (seconds.value == 0L) {
            this
        } else {
            var daysToAdd = seconds.inDays
            val currentSecondOfDay = time.secondOfDay
            val wrappedSeconds = (seconds % SECONDS_PER_DAY).toInt()
            var newSecondOfDay = currentSecondOfDay + wrappedSeconds
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

    operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.toLongMilliseconds())

    operator fun plus(milliseconds: LongMilliseconds): DateTime {
        return if (milliseconds.value == 0L) {
            this
        } else {
            plus(milliseconds.inDays, (milliseconds % MILLISECONDS_PER_DAY).inNanosecondsUnchecked)
        }
    }

    operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.toLongMicroseconds())

    operator fun plus(microseconds: LongMicroseconds): DateTime {
        return if (microseconds.value == 0L) {
            this
        } else {
            plus(microseconds.inDays, (microseconds % MICROSECONDS_PER_DAY).inNanosecondsUnchecked)
        }
    }

    operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds())

    operator fun plus(nanoseconds: LongNanoseconds): DateTime {
        return if (nanoseconds.value == 0L) {
            this
        } else {
            plus(nanoseconds.inDays, nanoseconds % NANOSECONDS_PER_DAY)
        }
    }

    private fun plus(days: LongDays, wrappedNanoseconds: LongNanoseconds): DateTime {
        val currentNanosecondOfDay = time.nanosecondOfDay
        var newNanosecondOfDay = currentNanosecondOfDay + wrappedNanoseconds.value
        val daysToAdd = (days.value + (newNanosecondOfDay floorDiv NANOSECONDS_PER_DAY)).days
        newNanosecondOfDay = newNanosecondOfDay floorMod NANOSECONDS_PER_DAY

        val newDate = date + daysToAdd

        val newTime = if (currentNanosecondOfDay == newNanosecondOfDay) {
            time
        } else {
            Time.fromNanosecondOfDay(newNanosecondOfDay)
        }

        return DateTime(newDate, newTime)
    }

    /**
     * Return a [DateTime] with [period] subtracted from it.
     *
     * Years are subtracted first, then months, then days. If the day exceeds the maximum month length at any step, it
     * will be coerced into the valid range. This behavior is consistent with the order of operations for period
     * addition as defined in ISO-8601-2.
     */
    operator fun minus(period: Period) = plus(-period)

    operator fun minus(duration: Duration): DateTime {
        return this - duration.seconds - duration.nanosecondAdjustment
    }

    operator fun minus(years: IntYears) = plus(years.toLongYears().negateUnchecked())

    operator fun minus(years: LongYears): DateTime {
        return if (years.value == 0L) {
            this
        } else {
            copy(date = date - years)
        }
    }

    operator fun minus(months: IntMonths) = plus(months.toLongMonths().negateUnchecked())

    operator fun minus(months: LongMonths): DateTime {
        return if (months.value == 0L) {
            this
        } else {
            copy(date = date - months)
        }
    }
    
    operator fun minus(weeks: IntWeeks) = plus(weeks.toLongWeeks().inDaysUnchecked.negateUnchecked())

    operator fun minus(weeks: LongWeeks): DateTime {
        return if (weeks.value == 0L) {
            this
        } else {
            copy(date = date - weeks)
        }
    }

    operator fun minus(days: IntDays) = plus(days.toLongDays().negateUnchecked())

    operator fun minus(days: LongDays): DateTime {
        return if (days.value == 0L) {
            this
        } else {
            copy(date = date - days)
        }
    }

    operator fun minus(hours: IntHours) = plus(hours.toLongHours().negateUnchecked())

    operator fun minus(hours: LongHours): DateTime {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(hours.negateUnchecked())
        }
    }

    operator fun minus(minutes: IntMinutes) = plus(minutes.toLongMinutes().negateUnchecked())

    operator fun minus(minutes: LongMinutes): DateTime {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(minutes.negateUnchecked())
        }
    }

    operator fun minus(seconds: IntSeconds) = plus(seconds.toLongSeconds().negateUnchecked())

    operator fun minus(seconds: LongSeconds): DateTime {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(seconds.negateUnchecked())
        }
    }

    operator fun minus(milliseconds: IntMilliseconds) = plus(milliseconds.toLongMilliseconds().negateUnchecked())

    operator fun minus(milliseconds: LongMilliseconds): DateTime {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(milliseconds.negateUnchecked())
        }
    }

    operator fun minus(microseconds: IntMicroseconds) = plus(microseconds.toLongMicroseconds().negateUnchecked())

    operator fun minus(microseconds: LongMicroseconds): DateTime {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(microseconds.negateUnchecked())
        }
    }

    operator fun minus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds().negateUnchecked())

    operator fun minus(nanoseconds: LongNanoseconds): DateTime {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(nanoseconds.negateUnchecked())
        }
    }

    operator fun component1() = date
    operator fun component2() = time

    operator fun rangeTo(other: DateTime) = DateTimeInterval.withInclusiveEnd(this, other)

    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            is DateProperty, is TimeProperty -> true
            else -> false
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            is DateProperty.IsFarPast -> this == MIN
            is DateProperty.IsFarFuture -> this == MAX
            is DateProperty -> date.get(property)
            is TimeProperty -> time.get(property)
            else -> super.get(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            is DateProperty -> date.get(property)
            is TimeProperty -> time.get(property)
            else -> super.get(property)
        }
    }

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

    /**
     * Return a copy of this [DateTime], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        date: Date = this.date,
        time: Time = this.time
    ) = DateTime(date, time)

    /**
     * Return a copy of this [DateTime], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting date-time is invalid
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
     * Return a copy of this [DateTime], replacing individual components with new values as desired.
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
        nanosecond: Int = this.nanosecond
    ) = DateTime(date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanosecond))

    /**
     * Return a copy of this [DateTime], replacing individual components with new values as desired.
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
        nanosecond: Int = this.nanosecond
    ) = DateTime(date.copy(year, monthNumber, dayOfMonth), time.copy(hour, minute, second, nanosecond))

    /**
     * The number of seconds relative to the Unix epoch of `1970-01-01T00:00Z` at a particular offset. This is a "floor"
     * value, so 1 nanosecond before the Unix epoch will be at a distance of 1 second.
     *
     * @param offset the offset from UTC
     * @see nanoOfSecondsSinceUnixEpoch
     * @see unixEpochSecondAt
     */
    fun secondsSinceUnixEpochAt(offset: UtcOffset): LongSeconds {
        return (date.daysSinceUnixEpoch.inSecondsUnchecked.value +
            time.secondsSinceStartOfDay.value -
            offset.totalSeconds.value).seconds
    }

    /**
     * The number of additional nanoseconds that should be applied on top of the number of seconds since the Unix epoch
     * returned by [secondsSinceUnixEpochAt].
     * @see secondsSinceUnixEpochAt
     * @see unixEpochNanoOfSecond
     */
    val nanoOfSecondsSinceUnixEpoch: IntNanoseconds
        get() = nanosecond.nanoseconds

    /**
     * The number of milliseconds relative to the Unix epoch of `1970-01-01T00:00Z` at a particular offset. This is a
     * "floor" value, so 1 nanosecond before the Unix epoch will be at a distance of 1 millisecond.
     * @param offset the offset from UTC
     */
    fun millisecondsSinceUnixEpochAt(offset: UtcOffset): LongMilliseconds {
        return (date.daysSinceUnixEpoch.inMillisecondsUnchecked.value +
            time.nanosecondsSinceStartOfDay.inMilliseconds.value -
            offset.totalSeconds.inMilliseconds.value).milliseconds
    }

    /**
     * The second of the Unix epoch.
     *
     * @param offset the offset from UTC
     * @see nanoOfSecondsSinceUnixEpoch
     * @see unixEpochSecondAt
     */
    fun unixEpochSecondAt(offset: UtcOffset): Long = secondsSinceUnixEpochAt(offset).value

    /**
     * The nanosecond of the second of the Unix Epoch.
     * @see nanoOfSecondsSinceUnixEpoch
     * @see unixEpochSecondAt
     */
    val unixEpochNanoOfSecond: Int get() = nanosecond

    /**
     * The millisecond of the Unix epoch.
     * @param offset the offset from UTC
     */
    fun unixEpochMillisecondAt(offset: UtcOffset): Long = millisecondsSinceUnixEpochAt(offset).value

    /**
     * The [Instant] represented by this date-time at a particular offset from UTC.
     * @param offset the offset from UTC
     */
    fun instantAt(offset: UtcOffset): Instant {
        return Instant.fromUnixEpochSecond(unixEpochSecondAt(offset), nanosecond)
    }

    companion object {
        /**
         * The smallest supported [DateTime], which can be used as a "far past" sentinel.
         */
        val MIN = DateTime(Date.MIN, Time.MIN)

        /**
         * The largest supported [DateTime], which can be used as a "far future" sentinel.
         */
        val MAX = DateTime(Date.MAX, Time.MAX)

        fun fromUnixEpochMillisecond(millisecond: Long, offset: UtcOffset): DateTime {
            return fromMillisecondsSinceUnixEpoch(millisecond.milliseconds, offset)
        }

        fun fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds, offset: UtcOffset): DateTime {
            val localMilliseconds = milliseconds + offset.totalSeconds
            val localEpochDays = (localMilliseconds.value floorDiv MILLISECONDS_PER_DAY).days
            val nanosecondOfDay =
                (localMilliseconds.value floorMod MILLISECONDS_PER_DAY).milliseconds.inNanosecondsUnchecked.value
            val date = Date.fromDaysSinceUnixEpoch(localEpochDays)
            val time = Time.fromNanosecondOfDay(nanosecondOfDay)
            return DateTime(date, time)
        }

        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Int = 0, offset: UtcOffset): DateTime {
            return fromSecondsSinceUnixEpoch(second.seconds, nanosecondAdjustment.nanoseconds, offset)
        }

        /**
         * Create the [DateTime] that falls a given number of seconds relative to the Unix epoch, plus some number of
         * additional nanoseconds
         */
        fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds = 0.nanoseconds,
            offset: UtcOffset
        ): DateTime {
            val adjustedSeconds = seconds + (nanosecondAdjustment.value floorDiv NANOSECONDS_PER_SECOND).seconds
            val nanosecondOfDay = nanosecondAdjustment.value floorMod NANOSECONDS_PER_SECOND
            val localSeconds = adjustedSeconds + offset.totalSeconds
            val localEpochDays = (localSeconds.value floorDiv SECONDS_PER_DAY).days
            val secondOfDay = (localSeconds.value floorMod SECONDS_PER_DAY).toInt()
            val date = Date.fromDaysSinceUnixEpoch(localEpochDays)
            val time = Time.fromSecondOfDay(secondOfDay, nanosecondOfDay)
            return DateTime(date, time)
        }
    }
}

/**
 * Combine a [Date] with a [Time] to create a [DateTime].
 */
infix fun Date.at(time: Time) = DateTime(this, time)

/**
 * Combine a [Date] with a time to create a [DateTime].
 */
fun Date.atTime(hour: Int, minute: Int, second: Int = 0, nanosecond: Int = 0): DateTime {
    return DateTime(this, Time(hour, minute, second, nanosecond))
}

/**
 * Convert an instant into a [DateTime] at a particular offset from UTC.
 */
fun Instant.toDateTimeAt(offset: UtcOffset): DateTime {
    return DateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, offset)
}

/**
 * The [DateTime] at the start of the day.
 */
val Date.startOfDay: DateTime get() = DateTime(this, Time.MIDNIGHT)

/**
 * The [DateTime] at the end of the day.
 */
val Date.endOfDay: DateTime get() = DateTime(this, Time.MAX)

/**
 * Parse a string in ISO-8601 extended calendar date format into a [DateTime] -- for example, "2019-08-22T18:00" or
 * "2019-08-22 18:00:30.123456789"
 */
fun String.toDateTime() = toDateTime(DateTimeParsers.Iso.Extended.DATE_TIME)

/**
 * Parse a string into a [DateTime] using a [DateTimeParser] capable of supplying the necessary fields
 */
fun String.toDateTime(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): DateTime {
    val result = parser.parse(this, settings)
    return result.toDateTime() ?: throwParserFieldResolutionException<DateTime>(this)
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