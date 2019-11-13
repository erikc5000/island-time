package io.islandtime

import io.islandtime.internal.*
import io.islandtime.internal.MONTHS_IN_YEAR
import io.islandtime.internal.appendZeroPadded
import io.islandtime.internal.toIntExact
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateRange

/**
 * A month in a particular year.
 *
 * @constructor Create a [YearMonth].
 * @param year The year
 * @param month The month of the year
 */
class YearMonth(
    val year: Int,
    val month: Month
) : Comparable<YearMonth> {

    init {
        checkValidYear(year)
    }

    /**
     * Create a [YearMonth].
     */
    constructor(year: Int, monthNumber: Int) : this(year, monthNumber.toMonth())

    /**
     * The ISO month number
     */
    inline val monthNumber: Int get() = month.number

    val isInLeapYear: Boolean get() = isLeapYear(year)

    /**
     * Get the range of days within this year and month
     */
    val dayRange: IntRange get() = month.dayRangeIn(year)

    /**
     * Get the range of dates within this year and month
     */
    val dateRange: DateRange get() = DateRange(startDate, endDate)

    /**
     * Get the length of this year month in days
     */
    val lengthOfMonth: IntDays get() = month.lengthIn(year)

    /**
     * Get the length of the year in days
     */
    val lengthOfYear: IntDays get() = lengthOfYear(year)

    /**
     * Get the last day of the year month
     */
    val lastDay: Int get() = month.lastDayIn(year)

    /**
     * Get the ordinal date corresponding to the first day of this year month
     */
    val firstDayOfYear: Int get() = month.firstDayOfYearIn(year)

    /**
     * Get the ordinal date corresponding to the last day of this year month
     */
    val lastDayOfYear: Int get() = month.lastDayOfYearIn(year)

    /**
     * Get the [Date] representing the first day in this year and month
     */
    val startDate: Date get() = Date(year, month, 1)

    /**
     * Get the [Date] representing the last day in this year and month
     */
    val endDate: Date get() = Date(year, month, month.lastDayIn(year))

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
     * Convert this year-month to a string in ISO-8601 extended format.
     */
    override fun toString(): String {
        return buildString(7) {
            appendZeroPadded(year, 4)
            append('-')
            appendZeroPadded(monthNumber, 2)
        }
    }

    /**
     * Create a copy of this [YearMonth], replacing the value of any component, as desired
     */
    fun copy(year: Int = this.year, month: Month = this.month) = YearMonth(year, month)

    /**
     * Create a copy of this [YearMonth], replacing the value of any component, as desired
     */
    fun copy(year: Int = this.year, monthNumber: Int) = YearMonth(year, monthNumber)

    operator fun plus(years: IntYears) = plus(years.toLong().inMonths)

    operator fun plus(years: LongYears): YearMonth {
        return if (years.value == 0L) {
            this
        } else {
            val newYear = checkValidYear(year + years.value)
            copy(year = newYear)
        }
    }

    operator fun plus(months: IntMonths) = plus(months.toLong())

    operator fun plus(months: LongMonths): YearMonth {
        return if (months.value == 0L) {
            this
        } else {
            val newMonthsSinceYear0 = year.toLong() * MONTHS_IN_YEAR + month.ordinal + months.value
            val newYear = checkValidYear(newMonthsSinceYear0 floorDiv MONTHS_IN_YEAR)
            val newMonth = Month.values()[(newMonthsSinceYear0 floorMod MONTHS_IN_YEAR).toInt()]
            YearMonth(newYear, newMonth)
        }
    }

    operator fun minus(months: IntMonths) = plus(-months.toLong())

    operator fun minus(months: LongMonths): YearMonth {
        return if (months.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.months + 1L.months
        } else {
            plus(-months)
        }
    }

    operator fun minus(years: IntYears) = plus(-years.toLong())

    operator fun minus(years: LongYears): YearMonth {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1L.years
        } else {
            plus(-years)
        }
    }

    operator fun contains(date: Date) = date.year == year && date.month == month

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
 * Convert a string to a [YearMonth].
 *
 * The string is assumed to be an ISO-8601 year-month in extended format. For example, `2010-05` or `1960-12`. The
 * output of [YearMonth.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toYearMonth() = toYearMonth(DateTimeParsers.Iso.Extended.YEAR_MONTH)

/**
 * Convert a string to a [YearMonth] using a specific parser.
 *
 * The parser must be capable of supplying [DateTimeField.YEAR] and [DateTimeField.MONTH_OF_YEAR].
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toYearMonth(parser: DateTimeParser): YearMonth {
    val result = parser.parse(this)
    return result.toYearMonth() ?: throwParserFieldResolutionException<YearMonth>(this)
}

internal fun DateTimeParseResult.toYearMonth(): YearMonth? {
    val year = fields[DateTimeField.YEAR]
    val month = fields[DateTimeField.MONTH_OF_YEAR]

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

/**
 * Combine a year and month to get a [YearMonth]
 */
infix fun Year.at(month: Month) = YearMonth(value, month)

/**
 * Combine a year and month number to get a [YearMonth]
 */
fun Year.atMonth(number: Int) = YearMonth(value, number.toMonth())