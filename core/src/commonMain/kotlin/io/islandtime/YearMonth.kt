package io.islandtime

import io.islandtime.internal.MONTHS_IN_YEAR
import io.islandtime.internal.appendZeroPadded
import io.islandtime.internal.toIntExact
import io.islandtime.interval.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateRange

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class YearMonth internal constructor(
    private val monthsSinceYear0: IntMonths
) : Comparable<YearMonth> {

    val year: Int
        get() = monthsSinceYear0.inWholeYears.value

    val month: Month
        get() = Month.values()[monthsSinceYear0.value % MONTHS_IN_YEAR]

    /**
     * The ISO month number
     */
    inline val monthNumber: Int get() = month.number

    /**
     * Is this year month within the supported range?
     *
     * Due to the nature of inline classes, it's not possible to guarantee that the value is valid when manipulated
     * via Java code, but the validity can be checked via this property.
     */
    val isValid: Boolean
        get() = this.monthsSinceYear0.value in MIN.monthsSinceYear0.value..MAX.monthsSinceYear0.value

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
        return this.monthsSinceYear0.value - other.monthsSinceYear0.value
    }

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
    operator fun plus(years: LongYears) = plus(years.inMonthsExact())

    operator fun plus(months: IntMonths) = plus(months.toLong())

    operator fun plus(months: LongMonths): YearMonth {
        return YearMonth(checkValid(monthsSinceYear0 + months))
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

    companion object {
        val MIN = YearMonth(Year.MIN_VALUE, Month.MIN)
        val MAX = YearMonth(Year.MAX_VALUE, Month.MAX)

        private fun isValid(monthsRelativeToYear0: LongMonths): Boolean {
            return monthsRelativeToYear0.value in MIN.monthsSinceYear0.value..MAX.monthsSinceYear0.value
        }

        private fun checkValid(monthsRelativeToYear0: LongMonths): IntMonths {
            if (!isValid(monthsRelativeToYear0)) {
                throw DateTimeException(
                    "Year month '$monthsRelativeToYear0' is outside the supported range of " +
                        "${MIN.monthsSinceYear0}..${MAX.monthsSinceYear0}"
                )
            }
            return monthsRelativeToYear0.toInt()
        }
    }
}

/**
 * Create a fully validated [YearMonth]
 */
@Suppress("FunctionName")
fun YearMonth(year: Int, monthNumber: Int): YearMonth {
    return YearMonth(year, Month(monthNumber))
}

/**
 * Create a fully validated [YearMonth]
 */
@Suppress("FunctionName")
fun YearMonth(year: Int, month: Month): YearMonth {
    return YearMonth((checkValidYear(year) * MONTHS_IN_YEAR + month.ordinal).months)
}

/**
 * Convert an ISO-8601 year-month in extended format into a [YearMonth]
 */
fun String.toYearMonth() = toYearMonth(Iso8601.Extended.YEAR_MONTH_PARSER)

/**
 * Convert a string into a [YearMonth] using a specific parser
 */
fun String.toYearMonth(parser: DateTimeParser): YearMonth {
    val result = parser.parse(this)
    return result.toYearMonth() ?: raiseParserFieldResolutionException("YearMonth", this)
}

internal fun DateTimeParseResult.toYearMonth(): YearMonth? {
    val year = this[DateTimeField.YEAR]
    val month = this[DateTimeField.MONTH_OF_YEAR]

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
fun Year.atMonth(number: Int) = YearMonth(value, Month(number))