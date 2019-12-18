package io.islandtime

import io.islandtime.measures.IntDays
import io.islandtime.measures.IntMonths
import io.islandtime.measures.LongMonths
import io.islandtime.measures.days

/**
 * A month of the year.
 */
enum class Month {
    JANUARY {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 1
    },
    FEBRUARY {
        override val lengthInCommonYear = 28.days
        override val lengthInLeapYear = 29.days
        override val firstDayOfCommonYear = 32
    },
    MARCH {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 60
    },
    APRIL {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 91
    },
    MAY {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 121
    },
    JUNE {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 152
    },
    JULY {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 182
    },
    AUGUST {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 213
    },
    SEPTEMBER {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 244
    },
    OCTOBER {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 272
    },
    NOVEMBER {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 305
    },
    DECEMBER {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 335
    };


    /**
     * Get the ISO month number, from 1-12.
     */
    val number: Int get() = ordinal + 1

    /**
     * The number of days in the month in a common year.
     */
    abstract val lengthInCommonYear: IntDays

    /**
     * The number of days in the month in a leap month.
     */
    open val lengthInLeapYear: IntDays
        get() = lengthInCommonYear

    /**
     * The day of the year corresponding to the month's first day in a common year.
     *
     * For example, the first day of [MARCH] is the 60th day of a common year.
     */
    abstract val firstDayOfCommonYear: Int

    /**
     * The day of the year corresponding to the month's first day in a leap year.
     *
     * For example, the first day of [MARCH] is the 61st day of a leap year.
     */
    val firstDayOfLeapYear: Int
        get() = when (this) {
            JANUARY, FEBRUARY -> firstDayOfCommonYear
            else -> firstDayOfCommonYear + 1
        }

    /**
     * Get the number of days in the month for a particular year.
     * @param year retrieve the length of the month within this year
     * @return the number of days in the month
     */
    fun lengthIn(year: Int): IntDays {
        return when (this) {
            FEBRUARY -> if (isLeapYear(year)) lengthInLeapYear else lengthInCommonYear
            else -> lengthInCommonYear
        }
    }

    /**
     * The last day of the month in a particular year.
     */
    fun lastDayIn(year: Int) = lengthIn(year).value

    /**
     * The day of the year that this month's first days falls on.  This may vary depending on whether or not the year is
     * a leap year.
     *
     * For example, the first day of [MARCH] will be either 60th or 61st day of the year.
     *
     * @param year retrieve the day of year number within this year
     * @return the first day of year number
     */
    fun firstDayOfYearIn(year: Int): Int {
        return if (isLeapYear(year)) firstDayOfLeapYear else firstDayOfCommonYear
    }

    /**
     * The day of the year that this month's last day falls on. This may vary depending on whether or not the year is a
     * leap year.
     *
     * For example, the last of [FEBRUARY] will be either 59th or 60th day of the year.
     *
     * @param year retrieve the day of year number within this year
     * @return the last day of year number
     */
    fun lastDayOfYearIn(year: Int): Int {
        val isLeap = isLeapYear(year)

        return if (isLeap) {
            firstDayOfLeapYear + lengthInLeapYear.value - 1
        } else {
            firstDayOfCommonYear + lengthInCommonYear.value - 1
        }
    }

    /**
     * The range of valid days for this month within a given year
     * @param year retrieve the day range within this year
     * @return the range of valid days
     */
    fun dayRangeIn(year: Int): IntRange {
        return 1..lengthIn(year).value
    }

    /**
     * Add months to this month, wrapping when the beginning or end of the year is reached.
     */
    operator fun plus(months: IntMonths) = plus(months.value % 12)

    /**
     * Add months to this month, wrapping when the beginning or end of the year is reached.
     */
    operator fun plus(months: LongMonths) = plus((months.value % 12).toInt())

    /**
     * Subtract months from this month, wrapping when the beginning or end of the year is reached.
     */
    operator fun minus(months: IntMonths) = plus(-(months.value % 12))

    /**
     * Subtract months from this month, wrapping when the beginning or end of the year is reached.
     */
    operator fun minus(months: LongMonths) = plus(-(months.value % 12).toInt())

    private fun plus(monthsToAdd: Int): Month {
        return values()[(ordinal + (monthsToAdd + 12)) % 12]
    }

    companion object {
        inline val MIN get() = JANUARY
        inline val MAX get() = DECEMBER
    }
}

/**
 * Convert an ISO month number (from 1-12) to a [Month].
 */
fun Int.toMonth(): Month {
    if (this !in Month.MIN.number..Month.MAX.number) {
        throw DateTimeException("'this' is not a valid month of the year")
    }

    return Month.values()[this - 1]
}