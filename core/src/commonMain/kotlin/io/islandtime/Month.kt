package io.islandtime

import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.TextStyle
import io.islandtime.locale.Locale
import io.islandtime.measures.Days
import io.islandtime.measures.Months
import io.islandtime.measures.days

/**
 * A month of the year.
 */
enum class Month {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    /**
     * The ISO month number, from 1-12.
     */
    val number: Int get() = ordinal + 1

    /**
     * The last day of the month in a common year.
     */
    val lastDayInCommonYear: Int
        get() = when (this) {
            JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, DECEMBER -> 31
            APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30
            FEBRUARY -> 28
        }

    /**
     * The last day of the month in a leap year.
     */
    val lastDayInLeapYear: Int
        get() = when (this) {
            FEBRUARY -> 29
            else -> lastDayInCommonYear
        }

    /**
     * The day of the year corresponding to the month's first day in a common year.
     *
     * For example, the first day of [MARCH] is the 60th day of a common year.
     */
    val firstDayOfYearInCommonYear: Int
        get() = when (this) {
            JANUARY -> 1
            FEBRUARY -> 32
            MARCH -> 60
            APRIL -> 91
            MAY -> 121
            JUNE -> 152
            JULY -> 182
            AUGUST -> 213
            SEPTEMBER -> 244
            OCTOBER -> 272
            NOVEMBER -> 305
            DECEMBER -> 335
        }

    /**
     * The day of the year corresponding to the month's first day in a leap year.
     *
     * For example, the first day of [MARCH] is the 61st day of a leap year.
     */
    val firstDayOfYearInLeapYear: Int
        get() = when (this) {
            JANUARY, FEBRUARY -> firstDayOfYearInCommonYear
            else -> firstDayOfYearInCommonYear + 1
        }

    /**
     * The number of days in the month in a common year.
     */
    val lengthInCommonYear: Days get() = lastDayInCommonYear.days

    /**
     * The number of days in the month in a leap year.
     */
    val lengthInLeapYear: Days get() = lastDayInLeapYear.days

    /**
     * The localized name of the month, if available for the [locale] in the specified style. The result depends on the
     * configured [DateTimeTextProvider] and may differ between platforms.
     *
     * @param style the style of text
     * @param locale the locale
     * @return the localized name or `null` if unavailable for the specified locale
     * @see displayName
     */
    fun localizedName(style: TextStyle, locale: Locale): String? {
        return DateTimeTextProvider.monthTextFor(number.toLong(), style, locale)
    }

    /**
     * A textual representation of the month, suitable for display purposes. The localized name will be returned, if
     * available. If not, the ISO month number (1-12) will be returned instead.
     *
     * The result depends on the configured [DateTimeTextProvider] and may differ between platforms.
     *
     * @param style the style of text
     * @param locale the locale
     * @return the localized name or [number] if unavailable for the specified locale
     * @see localizedName
     */
    fun displayName(style: TextStyle, locale: Locale): String {
        return localizedName(style, locale) ?: number.toString()
    }

    /**
     * Returns the last day of the month in [year].
     */
    fun lastDayIn(year: Int): Int = when (this) {
        FEBRUARY -> if (isLeapYear(year)) 29 else 28
        else -> lastDayInCommonYear
    }

    /**
     * Returns the number of days in the month in [year].
     * @param year retrieve the length of the month within this year
     * @return the number of days in the month
     */
    fun lengthIn(year: Int): Days = lastDayIn(year).days

    /**
     * Returns the day of the year that this month's first days falls on. This may vary depending on whether the year is
     * a leap year. For example, the first day of [MARCH] will be either 60th or 61st day of the year.
     * @param year retrieve the day of year number within this year
     * @return the first day of year number
     */
    fun firstDayOfYearIn(year: Int): Int {
        return if (isLeapYear(year)) firstDayOfYearInLeapYear else firstDayOfYearInCommonYear
    }

    /**
     * Returns the day of the year that this month's last day falls on. This may vary depending on whether the year is
     * a leap year. For example, the last of [FEBRUARY] will be either 59th or 60th day of the year.
     * @param year retrieve the day of year number within this year
     * @return the last day of year number
     */
    fun lastDayOfYearIn(year: Int): Int {
        val isLeap = isLeapYear(year)

        return if (isLeap) {
            firstDayOfYearInLeapYear + lastDayInLeapYear - 1
        } else {
            firstDayOfYearInCommonYear + lastDayInCommonYear - 1
        }
    }

    /**
     * Returns the range of valid days for this month within a given year
     * @param year retrieve the day range within this year
     * @return the range of valid days
     */
    fun dayRangeIn(year: Int): IntRange = 1..lastDayIn(year)

    /**
     * Returns this month with [months] added to it, wrapping when the beginning or end of the year is reached.
     */
    operator fun plus(months: Months): Month = plus((months.value % 12).toInt())

    /**
     * Returns this month with [months] subtracted from it, wrapping when the beginning or end of the year is reached.
     */
    operator fun minus(months: Months): Month = plus(-(months.value % 12).toInt())

    private fun plus(monthsToAdd: Int): Month = values()[(ordinal + (monthsToAdd + 12)) % 12]

    companion object {
        inline val MIN: Month get() = JANUARY
        inline val MAX: Month get() = DECEMBER
    }
}

/**
 * Converts an ISO month number, from 1-12, to a [Month].
 */
fun Int.toMonth(): Month {
    if (this !in Month.MIN.number..Month.MAX.number) {
        throw DateTimeException("'$this' is not a valid month of the year")
    }

    return Month.values()[this - 1]
}
