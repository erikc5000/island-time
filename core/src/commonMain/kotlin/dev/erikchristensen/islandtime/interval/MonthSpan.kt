package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR

inline class MonthSpan(val value: Int) : Comparable<MonthSpan> {
    override fun compareTo(other: MonthSpan) = value.compareTo(other.value)
}

operator fun MonthSpan.unaryPlus() = MonthSpan(+value)
operator fun MonthSpan.unaryMinus() = MonthSpan(-value)
operator fun MonthSpan.plus(monthsToAdd: MonthSpan) = MonthSpan(value + monthsToAdd.value)
operator fun MonthSpan.minus(monthsToSubtract: MonthSpan) = plus(-monthsToSubtract)

//operator fun MonthSpan.times(scalar: Long) = MonthSpan(value * scalar)
operator fun MonthSpan.times(scalar: Int) = MonthSpan(value * scalar)

//operator fun MonthSpan.div(scalar: Long) = MonthSpan(value / scalar)
operator fun MonthSpan.div(scalar: Int) = MonthSpan(value / scalar)

//operator fun MonthSpan.rem(scalar: Long) = MonthSpan(value % scalar)
operator fun MonthSpan.rem(scalar: Int) = MonthSpan(value % scalar)

fun MonthSpan.toLong() = LongMonthSpan(value.toLong())

/**
 * Convert a month span into the number of whole years that it represents
 */
fun MonthSpan.asWholeYears() = YearSpan(value / MONTHS_IN_YEAR)

inline val Int.months: MonthSpan get() = MonthSpan(this)

inline class LongMonthSpan(val value: Long) : Comparable<LongMonthSpan> {
    override fun compareTo(other: LongMonthSpan) = value.compareTo(other.value)
}

operator fun LongMonthSpan.unaryPlus() = LongMonthSpan(+value)
operator fun LongMonthSpan.unaryMinus() = LongMonthSpan(-value)

operator fun LongMonthSpan.plus(monthsToAdd: LongMonthSpan) = LongMonthSpan(value + monthsToAdd.value)
operator fun LongMonthSpan.minus(monthsToSubtract: LongMonthSpan) = plus(-monthsToSubtract)

operator fun LongMonthSpan.times(scalar: Long) = LongMonthSpan(value * scalar)
operator fun LongMonthSpan.times(scalar: Int) = LongMonthSpan(value * scalar)

operator fun LongMonthSpan.div(scalar: Long) = LongMonthSpan(value / scalar)
operator fun LongMonthSpan.div(scalar: Int) = LongMonthSpan(value / scalar)

operator fun LongMonthSpan.rem(scalar: Long) = LongMonthSpan(value % scalar)
operator fun LongMonthSpan.rem(scalar: Int) = LongMonthSpan(value % scalar)

fun LongMonthSpan.toInt() = MonthSpan(value.toInt())

/**
 * Convert a month span into the number of whole years that it represents
 */
fun LongMonthSpan.asWholeYears() = LongYearSpan(value / MONTHS_IN_YEAR)

inline val Long.months: LongMonthSpan get() = LongMonthSpan(this)