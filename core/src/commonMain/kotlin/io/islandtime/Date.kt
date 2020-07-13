@file:Suppress("FunctionName")

package io.islandtime

import io.islandtime.base.*
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateRange

/**
 * A date in an ambiguous region.
 *
 * @constructor Create a [Date] from a year, month, and day of month.
 * @param year the year
 * @param month the month
 * @param day the day of the month
 * @throws DateTimeException if the year or day is invalid
 */
class Date(
    /** The year. */
    val year: Int,
    /** The month of the year. */
    val month: Month,
    private val day: Int
) : Temporal,
    Comparable<Date> {

    init {
        checkValidYear(year)
        checkValidDayOfMonth(year, month, day)
    }

    /**
     * Create a [Date] from a year, ISO month number, and day of month.
     * @param year the year
     * @param monthNumber the ISO month number, from 1-12
     * @param day the day of the month
     * @throws DateTimeException if the year, month, or day is invalid
     */
    constructor(
        year: Int,
        monthNumber: Int,
        day: Int
    ) : this(year, monthNumber.toMonth(), day)

    override fun has(property: TemporalProperty<*>): Boolean {
        return property is DateProperty
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            DateProperty.IsFarPast -> this == MIN
            DateProperty.IsFarFuture -> this == MAX
            else -> super.get(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            DateProperty.DayOfUnixEpoch -> dayOfUnixEpoch
            else -> getInt(property).toLong()
        }
    }

    private fun getInt(property: NumberProperty): Int {
        return when (property) {
            DateProperty.Year -> year
            DateProperty.YearOfEra -> if (year >= 1) year else 1 - year
            DateProperty.MonthOfYear -> monthNumber
            DateProperty.DayOfWeek -> dayOfWeek.number
            DateProperty.DayOfMonth -> dayOfMonth
            DateProperty.DayOfYear -> dayOfYear
            DateProperty.Era -> if (year >= 1) 1 else 0
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    /**
     * The day of the week.
     */
    val dayOfWeek: DayOfWeek
        get() {
            val zeroIndexedDayOfWeek = (dayOfUnixEpoch + 3) floorMod 7
            return DayOfWeek.values()[zeroIndexedDayOfWeek.toInt()]
        }

    /**
     * The day of the month.
     */
    val dayOfMonth: Int get() = day

    /**
     * The day of the year -- also known as the ordinal date in ISO-8601.
     */
    val dayOfYear: Int get() = month.firstDayOfYearIn(year) + dayOfMonth - 1

    /**
     * The day of the Unix epoch.
     */
    val dayOfUnixEpoch: Long get() = getDayOfUnixEpochFrom(year, monthNumber, dayOfMonth)

    /**
     * The ISO month number, from 1-12.
     */
    inline val monthNumber: Int get() = month.number

    /**
     * Check if this date falls within a leap year.
     */
    val isInLeapYear: Boolean get() = isLeapYear(year)

    /**
     * Check if this is a leap day.
     */
    val isLeapDay: Boolean get() = month == Month.FEBRUARY && dayOfMonth == 29

    /**
     * The length of this date's month in days.
     */
    val lengthOfMonth: IntDays get() = month.lengthIn(year)

    /**
     * The length of this date's year in days.
     */
    val lengthOfYear: IntDays get() = lengthOfYear(year)

    @Deprecated(
        "Use toYearMonth() instead.",
        ReplaceWith("this.toYearMonth()"),
        DeprecationLevel.WARNING
    )
    inline val yearMonth: YearMonth
        get() = toYearMonth()

    /**
     * The number of days away from the Unix epoch (`1970-01-01T00:00Z`) that this date falls.
     */
    inline val daysSinceUnixEpoch: LongDays get() = dayOfUnixEpoch.days

    @Deprecated(
        "Use dayOfUnixEpoch instead.",
        ReplaceWith("this.dayOfUnixEpoch"),
        DeprecationLevel.WARNING
    )
    val unixEpochDay: Long
        get() = dayOfUnixEpoch

    /**
     * Return a [Date] with [period] added to it.
     *
     * Years are added first, then months, then days. If the day exceeds the maximum month length at any step, it will
     * be coerced into the valid range. This behavior is consistent with the order of operations for period addition as
     * defined in ISO-8601-2.
     */
    operator fun plus(period: Period): Date {
        return if (period.isZero()) {
            this
        } else {
            return this + period.years + period.months + period.days
        }
    }

    operator fun plus(years: IntYears) = plus(years.toLongYears())

    operator fun plus(years: LongYears): Date {
        return if (years.value == 0L) {
            this
        } else {
            val newYear = checkValidYear(year + years.value)
            copy(year = newYear, dayOfMonth = dayOfMonth.coerceAtMost(month.lastDayIn(newYear)))
        }
    }

    operator fun plus(months: IntMonths) = plus(months.toLongMonths())

    operator fun plus(months: LongMonths): Date {
        return if (months.value == 0L) {
            this
        } else {
            val newMonthsSinceYear0 = monthsSinceYear0 + months.value
            val newYear = checkValidYear(newMonthsSinceYear0 floorDiv MONTHS_PER_YEAR)
            val newMonth = Month.values()[(newMonthsSinceYear0 floorMod MONTHS_PER_YEAR).toInt()]

            Date(newYear, newMonth, dayOfMonth.coerceAtMost(newMonth.lastDayIn(newYear)))
        }
    }

    operator fun plus(weeks: IntWeeks) = plus(weeks.toLongWeeks().inDaysUnchecked)
    operator fun plus(weeks: LongWeeks) = plus(weeks.inDays)

    operator fun plus(days: IntDays) = plus(days.toLongDays())

    operator fun plus(days: LongDays): Date {
        return if (days.value == 0L) {
            this
        } else {
            fromDaysSinceUnixEpoch(daysSinceUnixEpoch + days)
        }
    }

    /**
     * Return a [Date] with [period] subtracted from it.
     *
     * Years are subtracted first, then months, then days. If the day exceeds the maximum month length at any step, it
     * will be coerced into the valid range. This behavior is consistent with the order of operations for period
     * addition as defined in ISO-8601-2.
     */
    operator fun minus(period: Period): Date {
        return if (period.isZero()) {
            this
        } else {
            return this - period.years - period.months - period.days
        }
    }

    operator fun minus(years: IntYears) = plus(years.toLongYears().negateUnchecked())

    operator fun minus(years: LongYears): Date {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1.years
        } else {
            plus(years.negateUnchecked())
        }
    }

    operator fun minus(months: IntMonths) = plus(months.toLongMonths().negateUnchecked())

    operator fun minus(months: LongMonths): Date {
        return if (months.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.months + 1.months
        } else {
            plus(months.negateUnchecked())
        }
    }

    operator fun minus(weeks: IntWeeks) = plus(weeks.toLongWeeks().inDaysUnchecked.negateUnchecked())

    operator fun minus(weeks: LongWeeks): Date {
        return if (weeks.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.weeks
        } else {
            plus(weeks.negateUnchecked())
        }
    }

    operator fun minus(days: IntDays) = plus(days.toLongDays().negateUnchecked())

    operator fun minus(days: LongDays): Date {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(days.negateUnchecked())
        }
    }

    operator fun rangeTo(other: Date) = DateRange(this, other)

    operator fun component1() = year
    operator fun component2() = month
    operator fun component3() = day

    override fun compareTo(other: Date): Int {
        val yearDiff = year - other.year

        return if (yearDiff != 0) {
            yearDiff
        } else {
            val monthDiff = month.ordinal - other.month.ordinal

            if (monthDiff != 0) {
                monthDiff
            } else {
                day - other.day
            }
        }
    }

    override fun toString() = buildString(MAX_DATE_STRING_LENGTH) { appendDate(this@Date) }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is Date && year == other.year && month == other.month && day == other.day)
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month.hashCode()
        result = 31 * result + day
        return result
    }

    /**
     * Return a copy of this [Date], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting date is invalid
     */
    fun copy(
        year: Int = this.year,
        month: Month = this.month,
        dayOfMonth: Int = this.day
    ) = Date(year, month, dayOfMonth)

    /**
     * Return a copy of this [Date], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting date is invalid
     */
    fun copy(
        year: Int = this.year,
        monthNumber: Int,
        dayOfMonth: Int = this.day
    ) = Date(year, monthNumber, dayOfMonth)

    /**
     * Return a copy of this [Date], replacing individual components with new values as desired.
     *
     * @throws DateTimeException if the resulting date is invalid
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear
    ) = Date(year, dayOfYear)

    companion object {
        /**
         * The smallest supported [Date], which can be used as a "far past" sentinel.
         */
        val MIN = Date(Year.MIN_VALUE, Month.JANUARY, 1)

        /**
         * The largest supported [Date], which can be used as a "far future" sentinel.
         */
        val MAX = Date(Year.MAX_VALUE, Month.DECEMBER, 31)

        /**
         * Create a [Date] from a duration of days relative to the Unix epoch of 1970-01-01.
         * @param days the number of days relative to the Unix epoch
         * @throws DateTimeException if outside of the supported date range
         */
        fun fromDaysSinceUnixEpoch(days: LongDays): Date = fromDayOfUnixEpoch(days.value)

        /**
         * Create a [Date] from the day of the Unix epoch.
         * @param day the day of the Unix epoch
         * @throws DateTimeException if outside of the supported date range
         */
        fun fromDayOfUnixEpoch(day: Long): Date {
            if (day !in -365243219162L..365241780471L) {
                throw DateTimeException("The day '$day' of the Unix epoch is outside the supported range")
            }

            return withComponentizedDayOfUnixEpoch(day) { year, month, dayOfMonth ->
                Date(year, month, dayOfMonth)
            }
        }

        @Deprecated(
            "Use fromDayOfUnixEpoch() instead.",
            ReplaceWith("Date.fromDayOfUnixEpoch(day)"),
            DeprecationLevel.WARNING
        )
        fun fromUnixEpochDay(day: Long): Date = fromDayOfUnixEpoch(day)
    }
}

/**
 * Create a [Date] from a year and day of year
 * @param year the year
 * @param dayOfYear the day of the calendar year
 * @throws DateTimeException if the year or day of year are invalid
 */
fun Date(year: Int, dayOfYear: Int): Date {
    checkValidYear(year)
    checkValidDayOfYear(year, dayOfYear)

    val testMonth = ((dayOfYear - 1) / 31 + 1).toMonth()
    val month = if (dayOfYear > testMonth.lastDayOfYearIn(year)) testMonth + 1.months else testMonth
    val dayOfMonth = dayOfYear - month.firstDayOfYearIn(year) + 1

    return Date(year, month, dayOfMonth)
}

/**
 * Convert an [Instant] into the [Date] represented by it at a particular UTC offset.
 */
fun Instant.toDateAt(offset: UtcOffset): Date {
    val adjustedSeconds = secondOfUnixEpoch + offset.totalSeconds.value
    val dayOfUnixEpoch = adjustedSeconds floorDiv SECONDS_PER_DAY
    return Date.fromDayOfUnixEpoch(dayOfUnixEpoch)
}

/**
 * Convert an [Instant] into the [Date] represented by it in a particular time zone.
 */
fun Instant.toDateAt(zone: TimeZone): Date {
    return this.toDateAt(zone.rules.offsetAt(this))
}

/**
 * Combine a [YearMonth] with a day of the month to create a [Date].
 * @param day the day of the month
 */
fun YearMonth.atDay(day: Int) = Date(year, month, day)

/**
 * Convert a string to a [Date].
 *
 * The string is assumed to be an ISO-8601 calendar date in extended format. For example, `2010-10-05`. The output of
 * [Date.toString] can be safely parsed using this method.
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed date is invalid
 */
fun String.toDate() = toDate(DateTimeParsers.Iso.Extended.CALENDAR_DATE)

/**
 * Convert a string to a [Date] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * Any custom parser must be capable of supplying one of the following field combinations:
 * - [DateProperty.Year], [DateProperty.MonthOfYear], [DateProperty.DayOfMonth]
 * - [DateProperty.Year], [DateProperty.DayOfYear]
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed date is invalid
 */
fun String.toDate(
    parser: TemporalParser,
    settings: TemporalParser.Settings = TemporalParser.Settings.DEFAULT
): Date {
    val result = parser.parse(this, settings)
    return result.toDate() ?: throwParserPropertyResolutionException<Date>(this)
}

internal fun TemporalParseResult.toDate(): Date? {
    val year = this[DateProperty.Year]

    if (year != null) {
        val month = this[DateProperty.MonthOfYear]
        val dayOfMonth = this[DateProperty.DayOfMonth]

        try {
            if (month != null && dayOfMonth != null) {
                return Date(year.toIntExact(), month.toIntExact().toMonth(), dayOfMonth.toIntExact())
            }

            val dayOfYear = this[DateProperty.DayOfYear]

            if (dayOfYear != null) {
                return Date(year.toIntExact(), dayOfYear.toIntExact())
            }
        } catch (e: ArithmeticException) {
            throw DateTimeException(e.message, e)
        }
    }

    return null
}

internal const val MAX_DATE_STRING_LENGTH = 10

internal fun StringBuilder.appendDate(date: Date): StringBuilder {
    return with(date) {
        appendDate(year, monthNumber, dayOfMonth)
    }
}

internal fun StringBuilder.appendDate(year: Int, monthNumber: Int, dayOfMonth: Int): StringBuilder {
    appendYear(year)
    append('-')
    appendZeroPadded(monthNumber, 2)
    append('-')
    appendZeroPadded(dayOfMonth, 2)
    return this
}

//
// Adapted from https://github.com/ThreeTen/threetenbp/blob/master/src/main/java/org/threeten/bp/LocalDate.java
//
internal inline fun <T> withComponentizedDayOfUnixEpoch(
    day: Long,
    block: (year: Int, month: Int, day: Int) -> T
): T {
    var zeroDay = day + DAYS_FROM_0000_TO_1970
    // find the march-based year
    zeroDay -= 60  // adjust to 0000-03-01 so leap day is at end of four year cycle
    var adjust: Long = 0
    if (zeroDay < 0) {
        // adjust negative years to positive for calculation
        val adjustCycles = (zeroDay + 1) / DAYS_PER_400_YEAR_CYCLE - 1
        adjust = adjustCycles * 400
        zeroDay += -adjustCycles * DAYS_PER_400_YEAR_CYCLE
    }
    var yearEst = (400 * zeroDay + 591) / DAYS_PER_400_YEAR_CYCLE
    var doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400)
    if (doyEst < 0) {
        // fix estimate
        yearEst--
        doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400)
    }
    yearEst += adjust  // reset any negative year
    val marchDoy0 = doyEst.toInt()

    // convert march-based values back to january-based
    val marchMonth0 = (marchDoy0 * 5 + 2) / 153
    val month = (marchMonth0 + 2) % 12 + 1
    val dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1
    yearEst += (marchMonth0 / 10).toLong()

    return block(yearEst.toIntExact(), month, dom)
}

internal inline val Date.monthsSinceYear0: Long get() = year * 12L + month.ordinal

//
// Adapted from https://github.com/ThreeTen/threetenbp/blob/master/src/main/java/org/threeten/bp/LocalDate.java
//
private fun getDayOfUnixEpochFrom(year: Int, monthNumber: Int, dayOfMonth: Int): Long {
    var total = DAYS_IN_COMMON_YEAR * year

    if (year >= 0) {
        total += (year + 3) / 4 - (year + 99) / 100 + (year + 399) / 400
    } else {
        total -= year / -4 - year / -100 + year / -400
    }

    total += ((367 * monthNumber - 362) / MONTHS_PER_YEAR)
    total += dayOfMonth - 1

    if (monthNumber > 2) {
        total -= if (isLeapYear(year)) 1 else 2
    }

    return total - DAYS_FROM_0000_TO_1970
}

private fun checkValidDayOfMonth(year: Int, month: Month, dayOfMonth: Int): Int {
    if (dayOfMonth !in month.dayRangeIn(year)) {
        throw DateTimeException("The day '$dayOfMonth' doesn't exist in $month of $year")
    }
    return dayOfMonth
}

private fun checkValidDayOfYear(year: Int, dayOfYear: Int): Int {
    if (dayOfYear !in 1..lastDayOfYear(year)) {
        if (dayOfYear == 366) {
            throw DateTimeException("Day of year '$dayOfYear' is invalid since '$year' isn't a leap year")
        } else {
            throw DateTimeException("'$dayOfYear' is not a valid day of the year")
        }
    }
    return dayOfYear
}