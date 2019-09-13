package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.IntDays
import dev.erikchristensen.islandtime.interval.IntMonths
import dev.erikchristensen.islandtime.interval.days
import dev.erikchristensen.islandtime.interval.unaryMinus

enum class Month(val number: Int) {
    JANUARY(1) {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 1
    },
    FEBRUARY(2) {
        override val lengthInCommonYear = 28.days
        override val lengthInLeapYear = 29.days
        override val firstDayOfCommonYear = 32
    },
    MARCH(3) {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 60
    },
    APRIL(4) {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 91
    },
    MAY(5) {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 121
    },
    JUNE(6) {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 152
    },
    JULY(7) {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 182
    },
    AUGUST(8) {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 213
    },
    SEPTEMBER(9) {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 244
    },
    OCTOBER(10) {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 272
    },
    NOVEMBER(11) {
        override val lengthInCommonYear = 30.days
        override val firstDayOfCommonYear = 305
    },
    DECEMBER(12) {
        override val lengthInCommonYear = 31.days
        override val firstDayOfCommonYear = 335
    };

    abstract val lengthInCommonYear: IntDays

    open val lengthInLeapYear: IntDays
        get() = lengthInCommonYear

    abstract val firstDayOfCommonYear: Int

    val firstDayOfLeapYear: Int
        get() = when (this) {
            JANUARY, FEBRUARY -> firstDayOfCommonYear
            else -> firstDayOfCommonYear + 1
        }

    /**
     * The number of days in the month for a particular year
     * @param year Retrieve the length of the month within this year
     */
    fun lengthIn(year: Int): IntDays {
        return when (this) {
            FEBRUARY -> if (isLeapYear(year)) lengthInLeapYear else lengthInCommonYear
            else -> lengthInCommonYear
        }
    }

    /**
     * The last day of the month
     */
    fun lastDayIn(year: Int) = lengthIn(year).value

    /**
     * The number of days into the year that this month's first date falls.  This may vary depending on whether or not
     * the year is a leap year.
     * @param year Retrieve the day of year number within this year
     */
    fun firstDayOfYearIn(year: Int): Int {
        return if (isLeapYear(year)) firstDayOfLeapYear else firstDayOfCommonYear
    }

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
     * @param year Retrieve the day range within this year
     * @return Range of valid days
     */
    fun dayRangeIn(year: Int): IntRange {
        return 1..lengthIn(year).value
    }

    /**
     * Add a given number of months to this month.
     */
    operator fun plus(months: IntMonths): Month {
        val monthsToAdd = months.value % 12
        return values()[(ordinal + (monthsToAdd + 12)) % 12]
    }

    operator fun minus(months: IntMonths) = plus(-months)

    companion object {
        val MIN = JANUARY
        val MAX = DECEMBER

        operator fun invoke(number: Int): Month {
            if (number !in MIN.number..MAX.number) {
                throw DateTimeException("'$number' is not a valid month of the year")
            }

            return values()[number - 1]
        }
    }
}

fun Int.toMonth() = Month(this)