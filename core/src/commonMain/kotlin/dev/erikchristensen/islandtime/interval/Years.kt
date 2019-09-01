package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR
import kotlin.math.absoluteValue

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class IntYears internal constructor(val value: Int) : Comparable<IntYears> {
    val isZero: Boolean
        get() = this.value == 0

    val isNegative: Boolean
        get() = this.value < 0

    val isPositive: Boolean
        get() = this.value > 0

    val absoluteValue: IntYears
        get() = IntYears(this.value.absoluteValue)

    override fun compareTo(other: IntYears) = value.compareTo(other.value)

    override fun toString(): String = if (this.isZero) {
        "P0D"
    } else {
        buildString {
            append('P')
            append(value)
            append('Y')
        }
    }
}

operator fun IntYears.compareTo(other: LongYears) = value.compareTo(other.value)
operator fun IntYears.compareTo(other: LongMonths) = toLong().asMonths().value.compareTo(other.value)
operator fun IntYears.compareTo(other: IntMonths) = toLong().asMonths().value.compareTo(other.value)

operator fun IntYears.unaryPlus() = this
operator fun IntYears.unaryMinus() = IntYears(-value)
operator fun IntYears.plus(years: IntYears) = IntYears(value + years.value)
operator fun IntYears.plus(months: IntMonths) = this.asMonths() + months
operator fun IntYears.minus(years: IntYears) = plus(-years)
operator fun IntYears.minus(months: IntMonths) = plus(-months)

operator fun IntYears.times(scalar: Int) = IntYears(value * scalar)
operator fun IntYears.div(scalar: Int) = IntYears(value / scalar)
operator fun IntYears.rem(scalar: Int) = IntYears(value % scalar)

fun IntYears.asMonths() = IntMonths(value * MONTHS_IN_YEAR)
fun IntYears.toLong() = LongYears(value.toLong())

val Int.years: IntYears get() = IntYears(this)

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class LongYears internal constructor(val value: Long) : Comparable<LongYears> {
    val isZero: Boolean
        get() = this.value == 0L

    val isNegative: Boolean
        get() = this.value < 0L

    val isPositive: Boolean
        get() = this.value > 0L

    val absoluteValue: LongYears
        get() = LongYears(this.value.absoluteValue)

    override fun compareTo(other: LongYears) = value.compareTo(other.value)

    override fun toString(): String = if (this.isZero) {
        "P0D"
    } else {
        buildString {
            append('P')
            append(value)
            append('Y')
        }
    }
}

operator fun LongYears.compareTo(other: IntYears) = value.compareTo(other.value)

operator fun LongYears.compareTo(other: LongMonths): Int {
    return if (value > Long.MAX_VALUE / MONTHS_IN_YEAR) {
        1
    } else {
        (value * MONTHS_IN_YEAR).compareTo(other.value)
    }
}

operator fun LongYears.compareTo(other: IntMonths): Int {
    return if (value > Int.MAX_VALUE) {
        1
    } else {
        (value * MONTHS_IN_YEAR).compareTo(other.value)
    }
}

operator fun LongYears.unaryPlus() = this
operator fun LongYears.unaryMinus() = LongYears(-value)
operator fun LongYears.plus(years: LongYears) = LongYears(value + years.value)
operator fun LongYears.plus(months: LongMonths) = this.asMonths() + months
operator fun LongYears.minus(years: LongYears) = plus(-years)
operator fun LongYears.minus(months: LongMonths) = plus(-months)

operator fun LongYears.times(scalar: Long) = LongYears(value * scalar)
operator fun LongYears.times(scalar: Int) = LongYears(value * scalar)

operator fun LongYears.div(scalar: Long) = LongYears(value / scalar)
operator fun LongYears.div(scalar: Int) = LongYears(value / scalar)

operator fun LongYears.rem(scalar: Long) = LongYears(value % scalar)
operator fun LongYears.rem(scalar: Int) = LongYears(value % scalar)

fun LongYears.asMonths() = LongMonths(value * MONTHS_IN_YEAR)
fun LongYears.toInt() = IntYears(value.toInt())

val Long.years: LongYears get() = LongYears(this)