package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.*
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

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

    abstract val lengthInCommonYear: DaySpan

    open val lengthInLeapYear: DaySpan
        get() = lengthInCommonYear

    abstract val firstDayOfCommonYear: Int

    companion object {
        @JvmField
        val MIN = JANUARY

        @JvmField
        val MAX = DECEMBER

        @JvmStatic
        fun of(number: Int): Month {
            require(number in MIN.number..MAX.number) { "'$number' is not a valid month of the year" }
            return values()[number - 1]
        }
    }
}

val Month.firstDayOfLeapYear: Int
    get() = when (this) {
        Month.JANUARY, Month.FEBRUARY -> firstDayOfCommonYear
        else -> firstDayOfCommonYear + 1
    }

/**
 * The number of days in the month for a particular year
 * @param year Retrieve the length of the month within this year
 */
fun Month.lengthIn(year: Int): DaySpan {
    return when (this) {
        Month.FEBRUARY -> if (Year(year).isLeap) lengthInLeapYear else lengthInCommonYear
        else -> lengthInCommonYear
    }
}

/**
 * The last day of the month
 */
fun Month.lastDayIn(year: Int) = lengthIn(year).value

/**
 * The number of days into the year that this month's first date falls.  This may vary depending on whether or not
 * the year is a leap year.
 * @param year Retrieve the day of year number within this year
 */
fun Month.firstDayOfYearIn(year: Int): Int {
    return if (Year(year).isLeap) firstDayOfLeapYear else firstDayOfCommonYear
}

fun Month.lastDayOfYearIn(year: Int): Int {
    val isLeap = Year(year).isLeap

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
fun Month.dayRangeIn(year: Int): IntRange {
    return 1..lengthIn(year).value
}

/**
 * Add a given number of months to this month.
 */
operator fun Month.plus(months: MonthSpan): Month {
    val monthsToAdd = months.value % 12
    return Month.values()[(ordinal + (monthsToAdd + 12)) % 12]
}

operator fun Month.minus(months: MonthSpan) = plus(-months)

fun Int.toMonth(): Month {
    return try {
        Month.of(this)
    } catch (e: IllegalArgumentException) {
        throw DateTimeException(e.message)
    }
}

fun isLeapDay(month: Month, day: Int) = month == Month.FEBRUARY && day == 29