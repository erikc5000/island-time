package io.islandtime

import dev.erikchristensen.javamath2kmp.floorDiv
import dev.erikchristensen.javamath2kmp.floorMod
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateTimeInterval

/**
 * A date and time of day in an ambiguous region.
 *
 * @constructor Creates a [DateTime] by combining a [Date] and [Time].
 * @param date the date
 * @param time the time
 */
class DateTime(
    /** The date. */
    val date: Date,
    /** The time of day. */
    val time: Time
) : Comparable<DateTime> {

    /**
     * Creates a [DateTime].
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
     * Creates a [DateTime].
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
     * Creates a [DateTime].
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
     * The day of the year.
     */
    inline val dayOfYear: Int get() = date.dayOfYear

    /**
     * The year.
     */
    inline val year: Int get() = date.year

    @Deprecated(
        "Use toYearMonth() instead.",
        ReplaceWith("this.toYearMonth()"),
        DeprecationLevel.ERROR
    )
    inline val yearMonth: YearMonth
        get() = toYearMonth()

    /**
     * Returns this date-time with [period] added to it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range.
     */
    operator fun plus(period: Period): DateTime {
        return if (period.isZero()) {
            this
        } else {
            copy(date = date + period)
        }
    }

    operator fun plus(duration: Duration): DateTime {
        return duration.toComponents { seconds, nanoseconds -> this + seconds + nanoseconds }
    }

    /**
     * Returns this date-time with [duration] added to it.
     */
    @kotlin.time.ExperimentalTime
    operator fun plus(duration: kotlin.time.Duration): DateTime {
        require(duration.isFinite()) { "The duration must be finite" }
        return duration.toComponents { seconds, nanoseconds -> this + Seconds(seconds) + Nanoseconds(nanoseconds) }
    }

    /**
     * Returns this date-tme with [centuries] added to it.
     */
    operator fun plus(centuries: Centuries): DateTime {
        return if (centuries.value == 0L) {
            this
        } else {
            copy(date = date + centuries)
        }
    }

    /**
     * Returns this date-time with [decades] added to it.
     */
    operator fun plus(decades: Decades): DateTime {
        return if (decades.value == 0L) {
            this
        } else {
            copy(date = date + decades)
        }
    }

    /**
     * Returns this date-time with [years] added to it.
     */
    operator fun plus(years: Years): DateTime {
        return if (years.value == 0L) {
            this
        } else {
            copy(date = date + years)
        }
    }

    /**
     * Returns this date-time with [months] added to it.
     */
    operator fun plus(months: Months): DateTime {
        return if (months.value == 0L) {
            this
        } else {
            copy(date = date + months)
        }
    }

    /**
     * Returns this date-time with [weeks] added to it.
     */
    operator fun plus(weeks: Weeks): DateTime {
        return if (weeks.value == 0L) {
            this
        } else {
            copy(date = date + weeks)
        }
    }

    /**
     * Returns this date-time with [days] added to it.
     */
    operator fun plus(days: Days): DateTime {
        return if (days.value == 0L) {
            this
        } else {
            copy(date = date + days)
        }
    }

    /**
     * Returns this date-time with [hours] added to it.
     */
    operator fun plus(hours: Hours): DateTime {
        return if (hours.value == 0L) {
            this
        } else {
            var daysToAdd = hours.inWholeDays
            val wrappedHours = (hours % HOURS_PER_DAY).toInt()
            var newHour = time.hour + wrappedHours
            daysToAdd += (newHour floorDiv HOURS_PER_DAY).days
            newHour = newHour floorMod HOURS_PER_DAY

            val newDate = date + daysToAdd
            val newTime = time.copy(hour = newHour)
            DateTime(newDate, newTime)
        }
    }

    /**
     * Returns this date-time with [minutes] added to it.
     */
    operator fun plus(minutes: Minutes): DateTime {
        return if (minutes.value == 0L) {
            this
        } else {
            var daysToAdd = minutes.inWholeDays
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

    /**
     * Returns this date-time with [seconds] added to it.
     */
    operator fun plus(seconds: Seconds): DateTime {
        return if (seconds.value == 0L) {
            this
        } else {
            var daysToAdd = seconds.inWholeDays
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

    /**
     * Returns this date-time with [milliseconds] added to it.
     */
    operator fun plus(milliseconds: Milliseconds): DateTime {
        return if (milliseconds.value == 0L) {
            this
        } else {
            plus(milliseconds.inWholeDays, (milliseconds % MILLISECONDS_PER_DAY).inNanosecondsUnchecked)
        }
    }

    /**
     * Returns this date-time with [microseconds] added to it.
     */
    operator fun plus(microseconds: Microseconds): DateTime {
        return if (microseconds.value == 0L) {
            this
        } else {
            plus(microseconds.inWholeDays, (microseconds % MICROSECONDS_PER_DAY).inNanosecondsUnchecked)
        }
    }

    /**
     * Returns this date-time with [nanoseconds] added to it.
     */
    operator fun plus(nanoseconds: Nanoseconds): DateTime {
        return if (nanoseconds.value == 0L) {
            this
        } else {
            plus(nanoseconds.inWholeDays, nanoseconds % NANOSECONDS_PER_DAY)
        }
    }

    private fun plus(days: Days, wrappedNanoseconds: Nanoseconds): DateTime {
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
     * Returns this date-time with [period] subtracted from it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range.
     */
    operator fun minus(period: Period) = plus(-period)

    operator fun minus(duration: Duration): DateTime {
        return this - duration.seconds - duration.nanosecondAdjustment
    }

    /**
     * Returns this date-time with [duration] subtracted from it.
     */
    @kotlin.time.ExperimentalTime
    operator fun minus(duration: kotlin.time.Duration): DateTime {
        require(duration.isFinite()) { "The duration must be finite" }
        return duration.toComponents { seconds, nanoseconds -> this - Seconds(seconds) - Nanoseconds(nanoseconds) }
    }

    /**
     * Returns this date-time with [centuries] subtracted from it.
     */
    operator fun minus(centuries: Centuries): DateTime {
        return if (centuries.value == 0L) {
            this
        } else {
            copy(date = date - centuries)
        }
    }

    /**
     * Returns this date-time with [decades] subtracted from it.
     */
    operator fun minus(decades: Decades): DateTime {
        return if (decades.value == 0L) {
            this
        } else {
            copy(date = date - decades)
        }
    }

    /**
     * Returns this date-time with [years] subtracted from it.
     */
    operator fun minus(years: Years): DateTime {
        return if (years.value == 0L) {
            this
        } else {
            copy(date = date - years)
        }
    }

    /**
     * Returns this date-time with [months] subtracted from it.
     */
    operator fun minus(months: Months): DateTime {
        return if (months.value == 0L) {
            this
        } else {
            copy(date = date - months)
        }
    }

    /**
     * Returns this date-time with [weeks] subtracted from it.
     */
    operator fun minus(weeks: Weeks): DateTime {
        return if (weeks.value == 0L) {
            this
        } else {
            copy(date = date - weeks)
        }
    }

    /**
     * Returns this date-time with [days] subtracted from it.
     */
    operator fun minus(days: Days): DateTime {
        return if (days.value == 0L) {
            this
        } else {
            copy(date = date - days)
        }
    }

    /**
     * Returns this date-time with [hours] subtracted from it.
     */
    operator fun minus(hours: Hours): DateTime {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(hours.negateUnchecked())
        }
    }

    /**
     * Returns this date-time with [minutes] subtracted from it.
     */
    operator fun minus(minutes: Minutes): DateTime {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(minutes.negateUnchecked())
        }
    }

    /**
     * Returns this date-time with [seconds] subtracted from it.
     */
    operator fun minus(seconds: Seconds): DateTime {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(seconds.negateUnchecked())
        }
    }

    /**
     * Returns this date-time with [milliseconds] subtracted from it.
     */
    operator fun minus(milliseconds: Milliseconds): DateTime {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(milliseconds.negateUnchecked())
        }
    }

    /**
     * Returns this date-time with [microseconds] subtracted from it.
     */
    operator fun minus(microseconds: Microseconds): DateTime {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(microseconds.negateUnchecked())
        }
    }

    /**
     * Returns this date-time with [nanoseconds] subtracted from it.
     */
    operator fun minus(nanoseconds: Nanoseconds): DateTime {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(nanoseconds.negateUnchecked())
        }
    }

    operator fun component1(): Date = date
    operator fun component2(): Time = time

    operator fun rangeTo(other: DateTime) = DateTimeInterval.withInclusiveEnd(this, other)

    override fun compareTo(other: DateTime): Int {
        val dateDiff = date.compareTo(other.date)

        return if (dateDiff != 0) {
            dateDiff
        } else {
            time.compareTo(other.time)
        }
    }

    /**
     * Converts this date-time to a string in ISO-8601 extended format. For example, `2012-04-15T17:31:45.923452091` or
     * `2020-02-13T02:30`.
     */
    override fun toString(): String = buildString(MAX_DATE_TIME_STRING_LENGTH) {
        appendDateTime(this@DateTime)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is DateTime && date == other.date && time == other.time)
    }

    override fun hashCode(): Int {
        return 31 * date.hashCode() + time.hashCode()
    }

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        date: Date = this.date,
        time: Time = this.time
    ): DateTime = DateTime(date, time)

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the resulting date-time is invalid
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanosecond: Int = this.nanosecond
    ): DateTime = DateTime(date.copy(year, dayOfYear), time.copy(hour, minute, second, nanosecond))

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
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
    ): DateTime = DateTime(date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanosecond))

    /**
     * Returns a copy of this date-time with the values of any individual components replaced by the new values
     * specified.
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
    ): DateTime = DateTime(date.copy(year, monthNumber, dayOfMonth), time.copy(hour, minute, second, nanosecond))

    /**
     * The number of seconds relative to the Unix epoch of `1970-01-01T00:00Z` at a particular offset. This is a "floor"
     * value, so 1 nanosecond before the Unix epoch will be at a distance of 1 second.
     *
     * @param offset the offset from UTC
     * @see additionalNanosecondsSinceUnixEpoch
     */
    fun secondsSinceUnixEpochAt(offset: UtcOffset): Seconds = secondOfUnixEpochAt(offset).seconds

    /**
     * The number of additional nanoseconds that should be applied on top of the number of seconds since the Unix epoch
     * returned by [secondsSinceUnixEpochAt].
     * @see secondsSinceUnixEpochAt
     */
    val additionalNanosecondsSinceUnixEpoch: Nanoseconds
        get() = nanosecond.nanoseconds

    /**
     * The number of milliseconds relative to the Unix epoch of `1970-01-01T00:00Z` at a particular offset. This is a
     * "floor" value, so 1 nanosecond before the Unix epoch will be at a distance of 1 millisecond.
     * @param offset the offset from UTC
     */
    fun millisecondsSinceUnixEpochAt(offset: UtcOffset): Milliseconds = millisecondOfUnixEpochAt(offset).milliseconds

    /**
     * The second of the Unix epoch.
     *
     * @param offset the offset from UTC
     * @see nanosecond
     */
    fun secondOfUnixEpochAt(offset: UtcOffset): Long {
        return date.daysSinceUnixEpoch.inSecondsUnchecked.value + time.secondOfDay - offset.totalSecondsValue
    }

    /**
     * The millisecond of the Unix epoch.
     * @param offset the offset from UTC
     */
    fun millisecondOfUnixEpochAt(offset: UtcOffset): Long {
        return date.daysSinceUnixEpoch.inMillisecondsUnchecked.value +
            time.nanosecondsSinceStartOfDay.inWholeMilliseconds.value -
            offset.totalSeconds.inMilliseconds.value
    }

    @Deprecated(
        "Use secondOfUnixEpochAt() instead.",
        ReplaceWith("this.secondOfUnixEpochAt(offset)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER", "unused")
    fun unixEpochSecondAt(offset: UtcOffset): Long = deprecatedToError()

    @Deprecated(
        "Use nanosecond instead.",
        ReplaceWith("this.nanosecond"),
        DeprecationLevel.ERROR
    )
    @Suppress("unused")
    val unixEpochNanoOfSecond: Int
        get() = deprecatedToError()

    @Deprecated(
        "Use millisecondOfUnixEpoch() instead.",
        ReplaceWith("this.millisecondOfUnixEpochAt(offset)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER", "unused")
    fun unixEpochMillisecondAt(offset: UtcOffset): Long = deprecatedToError()

    @Deprecated(
        "Use toInstantAt() instead.",
        ReplaceWith("this.toInstantAt(offset)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER", "unused")
    fun instantAt(offset: UtcOffset): Instant = deprecatedToError()

    companion object {
        /**
         * The earliest supported [DateTime], which can be used as a "far past" sentinel.
         */
        val MIN: DateTime = DateTime(Date.MIN, Time.MIN)

        /**
         * The latest supported [DateTime], which can be used as a "far future" sentinel.
         */
        val MAX: DateTime = DateTime(Date.MAX, Time.MAX)

        /**
         * Creates a [DateTime] from a duration of milliseconds relative to the Unix epoch at [offset].
         */
        fun fromMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch: Milliseconds, offset: UtcOffset): DateTime {
            val localMilliseconds = millisecondsSinceUnixEpoch + offset.totalSeconds
            val localEpochDay = localMilliseconds.value floorDiv MILLISECONDS_PER_DAY
            val nanosecondOfDay =
                (localMilliseconds.value floorMod MILLISECONDS_PER_DAY).milliseconds.inNanoseconds.value
            val date = Date.fromDayOfUnixEpoch(localEpochDay)
            val time = Time.fromNanosecondOfDay(nanosecondOfDay)
            return DateTime(date, time)
        }

        /**
         * Creates a [DateTime] from a duration of seconds relative to the Unix epoch at [offset], optionally, with some
         * number of additional nanoseconds added to it.
         */
        fun fromSecondsSinceUnixEpoch(
            secondsSinceUnixEpoch: Seconds,
            nanosecondAdjustment: Nanoseconds = 0.nanoseconds,
            offset: UtcOffset
        ): DateTime {
            val adjustedSeconds =
                secondsSinceUnixEpoch + (nanosecondAdjustment.value floorDiv NANOSECONDS_PER_SECOND).seconds
            val nanosecond = nanosecondAdjustment.value floorMod NANOSECONDS_PER_SECOND
            val localSeconds = adjustedSeconds + offset.totalSeconds
            val localEpochDay = (localSeconds.value floorDiv SECONDS_PER_DAY)
            val secondOfDay = localSeconds.value floorMod SECONDS_PER_DAY
            val date = Date.fromDayOfUnixEpoch(localEpochDay)
            val time = Time.fromSecondOfDay(secondOfDay, nanosecond)
            return DateTime(date, time)
        }

        /**
         * Creates a [DateTime] from the millisecond of the Unix epoch at [offset].
         */
        fun fromMillisecondOfUnixEpoch(millisecond: Long, offset: UtcOffset): DateTime {
            return fromMillisecondsSinceUnixEpoch(millisecond.milliseconds, offset)
        }

        /**
         * Creates a [DateTime] from the second of the Unix epoch at [offset] and optionally, the nanosecond of the
         * second.
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Int = 0, offset: UtcOffset): DateTime {
            return fromSecondsSinceUnixEpoch(second.seconds, nanosecond.nanoseconds, offset)
        }

        @Deprecated(
            "Use fromMillisecondOfUnixEpoch() instead.",
            ReplaceWith("DateTime.fromMillisecondOfUnixEpoch(millisecond, offset)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER", "unused")
        fun fromUnixEpochMillisecond(millisecond: Long, offset: UtcOffset): DateTime = deprecatedToError()

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("DateTime.fromSecondOfUnixEpoch(second, nanosecondAdjustment, offset)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER", "unused")
        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Int = 0, offset: UtcOffset): DateTime =
            deprecatedToError()
    }
}

/**
 * Convert a string to a [DateTime].
 *
 * The string is assumed to be an ISO-8601 date-time representation in extended format. For example, `2019-08-22T18:00`
 * or `2019-08-22 18:00:30.123456789`. The output of [DateTime.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date-time is invalid
 */
fun String.toDateTime(): DateTime = toDateTime(DateTimeParsers.Iso.Extended.DATE_TIME)

/**
 * Converts a string to a [DateTime] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * Any custom parser must be capable of supplying the fields necessary to resolve both a [Date] and [Time].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date-time is invalid
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
