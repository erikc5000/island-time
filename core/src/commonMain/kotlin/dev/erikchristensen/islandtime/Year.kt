package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.date.DateRange
import dev.erikchristensen.islandtime.internal.toZeroPaddedString
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.*

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class Year internal constructor(val value: Int) : Comparable<Year> {

    /**
     * Is this year within the supported range?
     *
     * Due to the nature of inline classes, it's not possible to guarantee that the value is valid when manipulated
     * via Java code, but the validity can be checked via this property.
     */
    val isValid: Boolean get() = isValidYear(value)

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

val Year.isLeap: Boolean
    get() = isLeapYear(value)

val Year.length: IntDays
    get() = lengthOfYear(value)

val Year.lastDay: Int
    get() = lastDayOfYear(value)

val Year.dayRange: IntRange
    get() = 1..lastDay

val Year.dateRange: DateRange
    get() = DateRange(firstDate, lastDate)

val Year.firstDate: Date
    get() = Date(value, Month.JANUARY, 1)

val Year.lastDate: Date
    get() = Date(value, Month.DECEMBER, 31)

operator fun Year.plus(yearsToAdd: LongYears): Year {
    val newValue = value + yearsToAdd.value
    checkValidYear(newValue)
    return Year(newValue.toInt())
}

operator fun Year.plus(yearsToAdd: IntYears) = plus(yearsToAdd.toLong())

operator fun Year.minus(yearsToSubtract: LongYears): Year {
    return if (yearsToSubtract.value == Long.MIN_VALUE) {
        this + Long.MAX_VALUE.years + 1L.years
    } else {
        plus(-yearsToSubtract)
    }
}

operator fun Year.minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)

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

internal fun checkValidYear(year: Int) {
    if (!isValidYear(year)) {
        throw DateTimeException(getInvalidYearMessage(year.toLong()))
    }
}

internal fun checkValidYear(year: Long) {
    if (!isValidYear(year)) {
        throw DateTimeException(getInvalidYearMessage(year))
    }
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