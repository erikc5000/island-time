package io.islandtime

import dev.erikchristensen.javamath2kmp.floorDiv
import dev.erikchristensen.javamath2kmp.floorMod
import dev.erikchristensen.javamath2kmp.toIntExact
import io.islandtime.base.BooleanProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.Temporal
import io.islandtime.base.TemporalProperty
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.properties.DateProperty
import io.islandtime.ranges.DateRange

/**
 * A month in a particular year.
 *
 * @constructor Creates a [YearMonth].
 * @param year the year
 * @param month the month of the year
 * @throws DateTimeException if the year is outside the supported range
 */
class YearMonth(
    /** The year. */
    val year: Int,
    /** The month of the year. */
    val month: Month
) : Temporal,
    Comparable<YearMonth> {

    init {
        checkValidYear(year)
    }

    /**
     * Creates a [YearMonth].
     * @throws DateTimeException if the year or month is invalid
     */
    constructor(year: Int, monthNumber: Int) : this(year, monthNumber.toMonth())

    /**
     * The ISO month number, from 1-12.
     */
    inline val monthNumber: Int get() = month.number

    /**
     * Checks if this year-month falls within a leap year.
     */
    val isInLeapYear: Boolean get() = isLeapYear(year)

    /**
     * The range of days within this year-month.
     */
    val dayRange: IntRange get() = month.dayRangeIn(year)

    /**
     * The range of dates within this year-month.
     */
    val dateRange: DateRange get() = DateRange(startDate, endDate)

    /**
     * The length of the year-month in days.
     */
    val lengthOfMonth: IntDays get() = month.lengthIn(year)

    /**
     * The length of the year in days.
     */
    val lengthOfYear: IntDays get() = lengthOfYear(year)

    /**
     * The last day of the year-month.
     */
    val lastDay: Int get() = month.lastDayIn(year)

    /**
     * The ordinal date corresponding to the first day of this year-month.
     */
    val firstDayOfYear: Int get() = month.firstDayOfYearIn(year)

    /**
     * The ordinal date corresponding to the last day of this year-month.
     */
    val lastDayOfYear: Int get() = month.lastDayOfYearIn(year)

    /**
     * The [Date] representing the first day in this year-month.
     */
    val startDate: Date get() = Date(year, month, 1)

    /**
     * The [Date] representing the last day in this year-month.
     */
    val endDate: Date get() = Date(year, month, month.lastDayIn(year))

    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            is DateProperty.Year,
            is DateProperty.YearOfEra,
            is DateProperty.Era,
            is DateProperty.MonthOfYear,
            is DateProperty.IsFarPast,
            is DateProperty.IsFarFuture -> true
            else -> super.has(property)
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            is DateProperty.IsFarPast -> this == MIN
            is DateProperty.IsFarFuture -> this == MAX
            else -> super.get(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            is DateProperty.Year -> year
            is DateProperty.YearOfEra -> if (year >= 1) year else 1 - year
            is DateProperty.Era -> if (year >= 1) 1 else 0
            is DateProperty.MonthOfYear -> monthNumber
            else -> super.get(property)
        }.toLong()
    }

    override fun compareTo(other: YearMonth): Int {
        val yearDiff = year - other.year

        return if (yearDiff == 0) {
            month.ordinal - other.month.ordinal
        } else {
            yearDiff
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is YearMonth && year == other.year && month == other.month)
    }

    override fun hashCode(): Int {
        return 31 * year + month.hashCode()
    }

    /**
     * Converts this date-time to a string in ISO-8601 extended format. For example, `2012-04`.
     */
    override fun toString(): String {
        return buildString(7) {
            appendYear(year)
            append('-')
            appendZeroPadded(monthNumber, 2)
        }
    }

    /**
     * Returns a copy of this year-month with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the year is invalid
     */
    fun copy(year: Int = this.year, month: Month = this.month): YearMonth = YearMonth(year, month)

    /**
     * Returns a copy of this year-month with the values of any individual components replaced by the new values
     * specified.
     * @throws DateTimeException if the year or month is invalid
     */
    fun copy(year: Int = this.year, monthNumber: Int): YearMonth = YearMonth(year, monthNumber)

    operator fun plus(years: IntYears): YearMonth = plus(years.toLongYears())

    operator fun plus(years: LongYears): YearMonth {
        return if (years.value == 0L) {
            this
        } else {
            val newYear = checkValidYear(year + years.value)
            copy(year = newYear)
        }
    }

    operator fun plus(months: IntMonths): YearMonth = plus(months.toLongMonths())

    operator fun plus(months: LongMonths): YearMonth {
        return if (months.value == 0L) {
            this
        } else {
            val newMonthsSinceYear0 = year.toLong() * MONTHS_PER_YEAR + month.ordinal + months.value
            val newYear = checkValidYear(newMonthsSinceYear0 floorDiv MONTHS_PER_YEAR)
            val newMonth = Month.values()[(newMonthsSinceYear0 floorMod MONTHS_PER_YEAR).toInt()]
            YearMonth(newYear, newMonth)
        }
    }

    operator fun minus(years: IntYears): YearMonth = plus(years.toLongYears().negateUnchecked())

    operator fun minus(years: LongYears): YearMonth {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1L.years
        } else {
            plus(years.negateUnchecked())
        }
    }

    operator fun minus(months: IntMonths): YearMonth = plus(months.toLongMonths().negateUnchecked())

    operator fun minus(months: LongMonths): YearMonth {
        return if (months.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.months + 1L.months
        } else {
            plus(months.negateUnchecked())
        }
    }

    operator fun contains(date: Date): Boolean = date.year == year && date.month == month

    companion object {
        /**
         * The earliest supported [YearMonth], which may be used to indicate the "far past".
         */
        val MIN = YearMonth(Year.MIN_VALUE, Month.MIN)

        /**
         * The latest supported [YearMonth], which may be used to indicate the "far future".
         */
        val MAX = YearMonth(Year.MAX_VALUE, Month.MAX)
    }
}

/**
 * Converts a string to a [YearMonth].
 *
 * The string is assumed to be an ISO-8601 year-month. For example, `2010-05` or `1960-12`. The output of
 * [YearMonth.toString] can be safely parsed using this method.
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed year-month is invalid
 */
fun String.toYearMonth(): YearMonth = toYearMonth(DateTimeParsers.Iso.YEAR_MONTH)

/**
 * Converts a string to a [YearMonth] using a specific parser.
 *
 * The parser must be capable of supplying values for [DateProperty.Year] and [DateProperty.MonthOfYear].
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed year-month is invalid
 */
fun String.toYearMonth(
    parser: TemporalParser,
    settings: TemporalParser.Settings = TemporalParser.Settings.DEFAULT
): YearMonth {
    val result = parser.parse(this, settings)
    return result.toYearMonth() ?: throwParserPropertyResolutionException<YearMonth>(this)
}

internal fun ParseResult.toYearMonth(): YearMonth? {
    val year = this[DateProperty.Year]
    val month = this[DateProperty.MonthOfYear]

    return if (year != null && month != null) {
        try {
            YearMonth(year.toIntExact(), month.toIntExact())
        } catch (e: ArithmeticException) {
            throw DateTimeException(e.message, e)
        }
    } else {
        null
    }
}
