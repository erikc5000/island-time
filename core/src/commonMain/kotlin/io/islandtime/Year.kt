package io.islandtime

import io.islandtime.base.DateTimeField
import io.islandtime.internal.toZeroPaddedString
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateRange
import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue

/**
 * A year as defined by ISO-8601.
 * @constructor Creates a [Year].
 * @param value the year
 * @throws DateTimeException if the year is invalid
 * @property value The year value.
 */
@JvmInline
value class Year(val value: Int) : Comparable<Year> {

    /**
     * Creates a [Year].
     * @param value the year
     * @throws DateTimeException if the year is invalid
     */
    constructor(value: Long) : this(checkValidYear(value))

    init {
        checkValidYear(value)
    }

    /**
     * Checks if this is a leap year.
     */
    val isLeap: Boolean get() = isLeapYear(value)

    /**
     * The length of the year in days.
     */
    val length: Days get() = lengthOfYear(value)

    /**
     * The last day of the year. This will be either `365` or `366` depending on whether this is a common or leap year.
     */
    val lastDay: Int get() = lastDayOfYear(value)

    /**
     * The day range of the year. This will be either `1..365` or `1.366` depending on whether this is a common or leap
     * year.
     */
    val dayRange: IntRange get() = 1..lastDay

    /**
     * The date range of the year.
     */
    val dateRange: DateRange get() = DateRange(startDate, endDate)

    /**
     * The first date of the year.
     */
    val startDate: Date get() = Date(value, Month.JANUARY, 1)

    /**
     * The last date of the year.
     */
    val endDate: Date get() = Date(value, Month.DECEMBER, 31)

    /**
     * Returns this year with [centuries] added to it.
     */
    operator fun plus(centuries: Centuries): Year = plus(centuries.inYears)

    /**
     * Returns this year with [decades] added to it.
     */
    operator fun plus(decades: Decades): Year = plus(decades.inYears)

    /**
     * Returns this year with [years] added to it.
     */
    operator fun plus(years: Years): Year = Year(value + years.value)

    /**
     * Returns this year with [centuries] subtracted from it.
     */
    operator fun minus(centuries: Centuries): Year = minus(centuries.inYears)

    /**
     * Returns this year with [decades] subtracted from it.
     */
    operator fun minus(decades: Decades): Year = minus(decades.inYears)

    /**
     * Returns this year with [years] subtracted from it.
     */
    operator fun minus(years: Years): Year {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1L.years
        } else {
            plus(years.negateUnchecked())
        }
    }

    /**
     * Checks if this year contains the specified year-month.
     */
    operator fun contains(yearMonth: YearMonth): Boolean = yearMonth.year == value

    /**
     * Checks if this year contains the specified date.
     */
    operator fun contains(date: Date): Boolean = date.year == value

    override fun compareTo(other: Year): Int = value - other.value

    /**
     * Converts this year to a string in ISO-8601 extended format. For example, `2012`, `-0001`, or `+10000`.
     */
    override fun toString(): String {
        val absValue = value.absoluteValue

        return when {
            absValue < 1000 -> if (value < 0) {
                "-${absValue.toZeroPaddedString(4)}"
            } else {
                absValue.toZeroPaddedString(4)
            }
            value > 9999 -> "+$value"
            else -> value.toString()
        }
    }

    companion object {
        /**
         * The earliest supported year value.
         */
        const val MIN_VALUE: Int = -999_999_999

        /**
         * The latest supported year value.
         */
        const val MAX_VALUE: Int = 999_999_999

        /**
         * The earliest supported [Year], which can be used as a "far past" sentinel.
         */
        val MIN: Year = Year(MIN_VALUE)

        /**
         * The latest supported [Year], which can be used as a "far future" sentinel.
         */
        val MAX: Year = Year(MAX_VALUE)
    }
}

/**
 * Converts a string to a [Year].
 *
 * The string is assumed to be an ISO-8601 year. For example, `2010`, `+002010`, or `Y12345`. The output of
 * [Year.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed year is invalid
 */
fun String.toYear(): Year = toYear(DateTimeParsers.Iso.YEAR)

/**
 * Converts a string to a [Year] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * The parser must be capable of supplying [DateTimeField.YEAR].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed year is invalid
 */
fun String.toYear(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): Year {
    val result = parser.parse(this, settings)
    return result.toYear() ?: throwParserFieldResolutionException<Year>(this)
}

internal fun DateTimeParseResult.toYear(): Year? {
    val value = fields[DateTimeField.YEAR]

    return if (value != null) {
        Year(value)
    } else {
        null
    }
}

internal fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

internal fun lengthOfYear(year: Int): Days = lastDayOfYear(year).days

internal fun lastDayOfYear(year: Int): Int = if (isLeapYear(year)) 366 else 365

internal fun checkValidYear(year: Int): Int {
    if (!isValidYear(year)) {
        throw DateTimeException(getInvalidYearMessage(year.toLong()))
    }
    return year
}

internal fun checkValidYear(year: Long): Int {
    if (!isValidYear(year)) {
        throw DateTimeException(getInvalidYearMessage(year))
    }
    return year.toInt()
}

internal fun isValidYear(year: Int): Boolean {
    return year in Year.MIN_VALUE..Year.MAX_VALUE
}

internal fun isValidYear(year: Long): Boolean {
    return year in Year.MIN_VALUE..Year.MAX_VALUE
}

internal fun StringBuilder.appendYear(year: Int): StringBuilder {
    val absValue = year.absoluteValue

    return when {
        absValue < 1000 -> {
            if (year < 0) append('-')
            append(absValue.toZeroPaddedString(4))
        }
        year > 9999 -> append('+').append(year)
        else -> append(year)
    }
}

private fun getInvalidYearMessage(year: Long): String {
    return "The year '${year}' is outside the supported range of ${Year.MIN_VALUE}..${Year.MAX_VALUE}"
}
