package io.islandtime

import io.islandtime.internal.toZeroPaddedString
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.DateRange

inline class Year(val value: Int) : Comparable<Year> {

    /**
     * Is this year within the supported range?
     */
    val isValid: Boolean get() = value in MIN_VALUE..MAX_VALUE

    val isLeap: Boolean
        get() = value % 4 == 0 && (value % 100 != 0 || value % 400 == 0)

    val length: IntDays
        get() = if (isLeap) 366.days else 365.days

    val lastDay: Int get() = length.value
    inline val dayRange: IntRange get() = 1..lastDay
    inline val dateRange: DateRange get() = DateRange(startDate, endDate)
    inline val startDate: Date get() = Date(value, Month.JANUARY, 1)
    inline val endDate: Date get() = Date(value, Month.DECEMBER, 31)

    operator fun plus(years: LongYears): Year {
        val newValue = checkValidYear(value + years.value)
        return Year(newValue)
    }

    operator fun plus(years: IntYears) = plus(years.toLong())

    operator fun minus(years: LongYears): Year {
        return if (years.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.years + 1L.years
        } else {
            plus(-years)
        }
    }

    operator fun minus(years: IntYears) = plus(-years.toLong())

    fun validated(): Year {
        if (!isValid) {
            throw DateTimeException(getInvalidYearMessage(value.toLong()))
        }
        return this
    }

    override fun compareTo(other: Year) = value - other.value

    override fun toString(): String {
        return value.toZeroPaddedString(4)
    }

    companion object {
        const val MIN_VALUE = 1
        const val MAX_VALUE = 9999

        val MIN = Year(MIN_VALUE)
        val MAX = Year(MAX_VALUE)
    }
}

fun String.toYear() = toYear(DateTimeParsers.Iso.YEAR)

fun String.toYear(parser: DateTimeParser): Year {
    val result = parser.parse(this)
    return result.toYear() ?: throwParserFieldResolutionException<Year>(this)
}

internal fun DateTimeParseResult.toYear(): Year? {
    val value = fields[DateTimeField.YEAR]

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

private fun getInvalidYearMessage(year: Long): String {
    return "The year '${year}' is outside the supported range of ${Year.MIN_VALUE}..${Year.MAX_VALUE}"
}