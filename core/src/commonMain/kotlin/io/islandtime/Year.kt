package io.islandtime

import io.islandtime.date.Date
import io.islandtime.date.DateRange
import io.islandtime.internal.toZeroPaddedString
import io.islandtime.interval.*
import io.islandtime.parser.*

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class Year internal constructor(val value: Int) : Comparable<Year> {

    /**
     * Is this year within the supported range?
     *
     * Due to the nature of inline classes, it's not possible to guarantee that the value is valid when manipulated
     * via Java code, but the validity can be checked via this property.
     */
    val isValid: Boolean get() = isValidYear(value)

    val isLeap: Boolean get() = isLeapYear(value)
    val length: IntDays get() = lengthOfYear(value)
    val lastDay: Int get() = lastDayOfYear(value)
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

    override fun compareTo(other: Year) = value - other.value

    override fun toString(): String {
        return value.toZeroPaddedString(4)
    }

    companion object {
        const val MIN_VALUE = 1
        const val MAX_VALUE = 9999

        val MIN = Year(MIN_VALUE)
        val MAX = Year(MAX_VALUE)

        /**
         * Create a [Year]
         */
        operator fun invoke(value: Int): Year {
            checkValidYear(value)
            return Year(value)
        }
    }
}

fun String.toYear() = toYear(Iso8601.YEAR_PARSER)

fun String.toYear(parser: DateTimeParser): Year {
    val result = parser.parse(this)
    return result.toYear() ?: raiseParserFieldResolutionException("Year", this)
}

internal fun DateTimeParseResult.toYear(): Year? {
    val value = this[DateTimeField.YEAR]

    return if (value != null) {
        checkValidYear(value)
        Year(value.toInt())
    } else {
        null
    }
}

internal fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

internal fun lengthOfYear(year: Int): IntDays {
    return if (isLeapYear(year)) 366.days else 365.days
}

internal fun lastDayOfYear(year: Int): Int = lengthOfYear(year).value

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

private fun getInvalidYearMessage(year: Long): String {
    return "The year '${year}' is outside the supported range of ${Year.MIN_VALUE}..${Year.MAX_VALUE}"
}