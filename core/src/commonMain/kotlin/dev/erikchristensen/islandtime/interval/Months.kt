package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class IntMonths internal constructor(val value: Int) : Comparable<IntMonths> {
    override fun compareTo(other: IntMonths) = value.compareTo(other.value)
}

operator fun IntMonths.unaryPlus() = IntMonths(+value)
operator fun IntMonths.unaryMinus() = IntMonths(-value)

operator fun IntMonths.plus(years: IntYears) = this + years.asMonths()
operator fun IntMonths.plus(months: IntMonths) = IntMonths(value + months.value)
operator fun IntMonths.minus(years: IntYears) = plus(-years)
operator fun IntMonths.minus(months: IntMonths) = plus(-months)

operator fun IntMonths.times(scalar: Int) = IntMonths(value * scalar)
operator fun IntMonths.div(scalar: Int) = IntMonths(value / scalar)
operator fun IntMonths.rem(scalar: Int) = IntMonths(value % scalar)

fun IntMonths.toLong() = LongMonths(value.toLong())

/**
 * Convert a month span into the number of whole years represented by it
 */
fun IntMonths.toWholeYears() = IntYears(value / MONTHS_IN_YEAR)

val Int.months: IntMonths get() = IntMonths(this)

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class LongMonths internal constructor(val value: Long) : Comparable<LongMonths> {
    override fun compareTo(other: LongMonths) = value.compareTo(other.value)
}

operator fun LongMonths.unaryPlus() = LongMonths(+value)
operator fun LongMonths.unaryMinus() = LongMonths(-value)

operator fun LongMonths.plus(years: LongYears) = this + years.asMonths()
operator fun LongMonths.plus(months: LongMonths) = LongMonths(value + months.value)
operator fun LongMonths.minus(years: LongYears) = plus(-years)
operator fun LongMonths.minus(months: LongMonths) = plus(-months)

operator fun LongMonths.times(scalar: Long) = LongMonths(value * scalar)
operator fun LongMonths.times(scalar: Int) = LongMonths(value * scalar)

operator fun LongMonths.div(scalar: Long) = LongMonths(value / scalar)
operator fun LongMonths.div(scalar: Int) = LongMonths(value / scalar)

operator fun LongMonths.rem(scalar: Long) = LongMonths(value % scalar)
operator fun LongMonths.rem(scalar: Int) = LongMonths(value % scalar)

fun LongMonths.toInt() = IntMonths(value.toInt())

/**
 * Convert a month span into the number of whole years that it represents
 */
fun LongMonths.toWholeYears() = LongYears(value / MONTHS_IN_YEAR)

val Long.months: LongMonths get() = LongMonths(this)