package io.islandtime

import io.islandtime.base.*
import io.islandtime.internal.toZeroPaddedString
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateRange
import kotlin.math.absoluteValue

inline class Year(val value: Int) : Temporal, Comparable<Year> {

    /**
     * Is this year within the supported range?
     */
    val isValid: Boolean get() = value in MIN_VALUE..MAX_VALUE

    /**
     * Is this a leap year?
     */
    val isLeap: Boolean
        get() = value % 4 == 0 && (value % 100 != 0 || value % 400 == 0)

    /**
     * The length of the year in days.
     */
    val length: IntDays
        get() = if (isLeap) 366.days else 365.days

    /**
     * The last day of the year. This will be either `365` or `366` depending on whether this is a common or leap year.
     */
    val lastDay: Int get() = length.value

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

    operator fun plus(years: LongYears): Year {
        val newValue = checkValidYear(value + years.value)
        return Year(newValue)
    }

    operator fun plus(years: IntYears) = plus(years.toLongYears())

    operator fun minus(years: LongYears): Year {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1L.years
        } else {
            plus(years.negateUnchecked())
        }
    }

    operator fun minus(years: IntYears) = plus(years.toLongYears().negateUnchecked())

    operator fun contains(yearMonth: YearMonth) = yearMonth.year == value
    operator fun contains(date: Date) = date.year == value

    fun validated(): Year {
        if (!isValid) {
            throw DateTimeException(getInvalidYearMessage(value.toLong()))
        }
        return this
    }

    override fun has(property: TemporalProperty<*>): Boolean {
        return if (property is DateProperty) {
            when (property) {
                is DateProperty.Year,
                is DateProperty.YearOfEra,
                is DateProperty.Era,
                is DateProperty.IsFarPast,
                is DateProperty.IsFarFuture -> true
                else -> false
            }
        } else {
            false
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            is DateProperty.IsFarPast -> this == MIN
            is DateProperty.IsFarFuture -> this == MAX
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            is DateProperty.Year -> value
            is DateProperty.YearOfEra -> if (value >= 1) value else 1 - value
            is DateProperty.Era -> if (value >= 1) 1 else 0
            else -> throwUnsupportedTemporalPropertyException(property)
        }.toLong()
    }

    override fun compareTo(other: Year) = value - other.value

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
        const val MIN_VALUE = -999_999_999
        const val MAX_VALUE = 999_999_999

        val MIN = Year(MIN_VALUE)
        val MAX = Year(MAX_VALUE)
    }
}

/**
 * Convert a string to a [Year].
 *
 * The string is assumed to be an ISO-8601 year. For example, `2010`, `+002010`, or 'Y12345'. The output of
 * [Year.toString] can be safely parsed using this method.
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed year is invalid
 */
fun String.toYear() = toYear(DateTimeParsers.Iso.YEAR)

/**
 * Convert a string to a [Year] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * The parser must be capable of supplying [DateTimeField.YEAR].
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed year is invalid
 */
fun String.toYear(
    parser: TemporalParser,
    settings: TemporalParser.Settings = TemporalParser.Settings.DEFAULT
): Year {
    val result = parser.parse(this, settings)
    return result.toYear() ?: throwParserPropertyResolutionException<Year>(this)
}

internal fun TemporalParseResult.toYear(): Year? {
    val value = this[DateProperty.Year]

    return if (value != null) {
        Year(checkValidYear(value))
    } else {
        null
    }
}

internal fun isLeapYear(year: Int) = Year(year).isLeap
internal fun lengthOfYear(year: Int) = Year(year).length
internal fun lastDayOfYear(year: Int): Int = Year(year).lastDay
internal fun checkValidYear(year: Int) = Year(year).validated().value

internal fun checkValidYear(year: Long): Int {
    if (!isValidYear(year)) {
        throw DateTimeException(getInvalidYearMessage(year))
    }
    return year.toInt()
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