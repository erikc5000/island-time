package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR

inline class YearSpan(val value: Int) : Comparable<YearSpan> {
    override fun compareTo(other: YearSpan) = value.compareTo(other.value)
}

operator fun YearSpan.unaryPlus() = YearSpan(+value)
operator fun YearSpan.unaryMinus() = YearSpan(-value)
operator fun YearSpan.plus(years: YearSpan) = YearSpan(value + years.value)
operator fun YearSpan.minus(years: YearSpan) = plus(-years)

//operator fun YearSpan.times(scalar: Long) = YearSpan(value * scalar)
operator fun YearSpan.times(scalar: Int) = YearSpan(value * scalar)

//operator fun YearSpan.div(scalar: Long) = YearSpan(value / scalar)
operator fun YearSpan.div(scalar: Int) = YearSpan(value / scalar)

//operator fun YearSpan.rem(scalar: Long) = YearSpan(value % scalar)
operator fun YearSpan.rem(scalar: Int) = YearSpan(value % scalar)

fun YearSpan.asMonths() = MonthSpan(value * MONTHS_IN_YEAR)
fun YearSpan.toLong() = LongYearSpan(value.toLong())

inline val Int.years: YearSpan get() = YearSpan(this)
inline val Int.decades: YearSpan get() = YearSpan(this * 10)
inline val Int.centuries: YearSpan get() = YearSpan(this * 100)

inline class LongYearSpan(val value: Long) : Comparable<LongYearSpan> {
    override fun compareTo(other: LongYearSpan) = value.compareTo(other.value)
}

operator fun LongYearSpan.unaryPlus() = LongYearSpan(+value)
operator fun LongYearSpan.unaryMinus() = LongYearSpan(-value)
operator fun LongYearSpan.plus(years: LongYearSpan) = LongYearSpan(value + years.value)
operator fun LongYearSpan.minus(years: LongYearSpan) = plus(-years)

operator fun LongYearSpan.times(scalar: Long) = LongYearSpan(value * scalar)
operator fun LongYearSpan.times(scalar: Int) = LongYearSpan(value * scalar)

operator fun LongYearSpan.div(scalar: Long) = LongYearSpan(value / scalar)
operator fun LongYearSpan.div(scalar: Int) = LongYearSpan(value / scalar)

operator fun LongYearSpan.rem(scalar: Long) = LongYearSpan(value % scalar)
operator fun LongYearSpan.rem(scalar: Int) = LongYearSpan(value % scalar)

fun LongYearSpan.asMonths() = LongMonthSpan(value * MONTHS_IN_YEAR)
fun LongYearSpan.toInt() = YearSpan(value.toInt())

inline val Long.years: LongYearSpan get() = LongYearSpan(this)
inline val Long.decades: LongYearSpan get() = LongYearSpan(this * 10)
inline val Long.centuries: LongYearSpan get() = LongYearSpan(this * 100)