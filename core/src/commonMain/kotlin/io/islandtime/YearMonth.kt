package io.islandtime

import dev.erikchristensen.javamath2kmp.floorDiv
import dev.erikchristensen.javamath2kmp.floorMod
import dev.erikchristensen.javamath2kmp.toIntExact
import io.islandtime.base.DateTimeField
import io.islandtime.internal.MONTHS_PER_YEAR
import io.islandtime.internal.appendZeroPadded
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateRange
import io.islandtime.serialization.YearMonthSerializer
import kotlinx.serialization.Serializable

/**
 * A month in a particular year.
 *
 * @constructor Creates a [YearMonth].
 * @param year the year
 * @param month the month of the year
 * @throws DateTimeException if the year is outside the supported range
 */
@Serializable(with = YearMonthSerializer::class)
class YearMonth(
    /** The year. */
    val year: Int,
    /** The month of the year. */
    val month: Month
) : Comparable<YearMonth> {

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
    val lengthOfMonth: Days get() = month.lengthIn(year)

    /**
     * The length of the year in days.
     */
    val lengthOfYear: Days get() = lengthOfYear(year)

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

    /**
     * Returns this year-month with [centuries] added to it.
     */
    operator fun plus(centuries: Centuries): YearMonth = plus(centuries.inYears)

    /**
     * Returns this year-month with [decades] added to it.
     */
    operator fun plus(decades: Decades): YearMonth = plus(decades.inYears)

    /**
     * Returns this year-month with [years] added to it.
     */
    operator fun plus(years: Years): YearMonth {
        return if (years.value == 0L) {
            this
        } else {
            val newYear = checkValidYear(year + years.value)
            copy(year = newYear)
        }
    }

    /**
     * Returns this year-month with [months] added to it.
     */
    operator fun plus(months: Months): YearMonth {
        return if (months.value == 0L) {
            this
        } else {
            val newMonthsSinceYear0 = year.toLong() * MONTHS_PER_YEAR + month.ordinal + months.value
            val newYear = checkValidYear(newMonthsSinceYear0 floorDiv MONTHS_PER_YEAR)
            val newMonth = Month.values()[newMonthsSinceYear0 floorMod MONTHS_PER_YEAR]
            YearMonth(newYear, newMonth)
        }
    }

    /**
     * Returns this year-month with [centuries] subtracted from it.
     */
    operator fun minus(centuries: Centuries): YearMonth = minus(centuries.inYears)

    /**
     * Returns this year-month with [decades] subtracted from it.
     */
    operator fun minus(decades: Decades): YearMonth = minus(decades.inYears)

    /**
     * Returns this year-month with [years] subtracted from it.
     */
    operator fun minus(years: Years): YearMonth {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1L.years
        } else {
            plus(years.negateUnchecked())
        }
    }

    /**
     * Returns this year-month with [months] subtracted from it.
     */
    operator fun minus(months: Months): YearMonth {
        return if (months.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.months + 1L.months
        } else {
            plus(months.negateUnchecked())
        }
    }

    /**
     * Checks if this year contains the specified date.
     */
    operator fun contains(date: Date): Boolean = date.year == year && date.month == month

    companion object {
        /**
         * The earliest supported [YearMonth], which may be used to indicate the "far past".
         */
        val MIN: YearMonth = YearMonth(Year.MIN_VALUE, Month.MIN)

        /**
         * The latest supported [YearMonth], which may be used to indicate the "far future".
         */
        val MAX: YearMonth = YearMonth(Year.MAX_VALUE, Month.MAX)
    }
}

/**
 * Converts a string to a [YearMonth].
 *
 * The string is assumed to be an ISO-8601 year-month. For example, `2010-05` or `1960-12`. The output of
 * [YearMonth.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed year-month is invalid
 */
fun String.toYearMonth(): YearMonth = toYearMonth(DateTimeParsers.Iso.YEAR_MONTH)

/**
 * Converts a string to a [YearMonth] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * The parser must be capable of supplying [DateTimeField.YEAR] and [DateTimeField.MONTH_OF_YEAR].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed year-month is invalid
 */
fun String.toYearMonth(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): YearMonth {
    val result = parser.parse(this, settings)
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
