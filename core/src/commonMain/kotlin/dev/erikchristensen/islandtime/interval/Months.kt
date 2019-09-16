package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR
import dev.erikchristensen.islandtime.internal.toIntExact
import kotlin.math.absoluteValue

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class IntMonths internal constructor(val value: Int) : Comparable<IntMonths> {
    inline val isZero: Boolean
        get() = this.value == 0

    inline val isNegative: Boolean
        get() = this.value < 0

    inline val isPositive: Boolean
        get() = this.value > 0

    val absoluteValue: IntMonths
        get() = IntMonths(this.value.absoluteValue)

    /**
     * The number of whole years that fit within this span of months
     */
    val inWholeYears get() = IntYears(value / MONTHS_IN_YEAR)

    fun toLong() = LongMonths(value.toLong())

    operator fun unaryMinus() = IntMonths(-value)

    operator fun plus(years: IntYears) = this + years.inMonths
    operator fun plus(years: LongYears) = this + years.inMonths
    operator fun plus(months: IntMonths) = IntMonths(value + months.value)
    operator fun plus(months: LongMonths) = LongMonths(value + months.value)
    operator fun minus(years: IntYears) = plus(-years)
    operator fun minus(years: LongYears) = plus(-years)
    operator fun minus(months: IntMonths) = plus(-months)
    operator fun minus(months: LongMonths) = plus(-months)

    operator fun times(scalar: Int) = IntMonths(value * scalar)
    operator fun div(scalar: Int) = IntMonths(value / scalar)
    operator fun rem(scalar: Int) = IntMonths(value % scalar)

    override fun compareTo(other: IntMonths) = value.compareTo(other.value)
    operator fun compareTo(other: LongMonths) = value.compareTo(other.value)

    operator fun compareTo(other: IntYears) = value.compareTo(other.toLong().inMonths.value)

    operator fun compareTo(other: LongYears): Int {
        return when {
            other.value > Int.MAX_VALUE -> -1
            other.value < Int.MIN_VALUE -> 1
            else -> value.compareTo(other.inMonths.value)
        }
    }

    override fun toString(): String = if (this.isZero) {
        "P0D"
    } else {
        buildString {
            append('P')
            append(value)
            append('M')
        }
    }
}

val Int.months: IntMonths get() = IntMonths(this)

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class LongMonths internal constructor(val value: Long) : Comparable<LongMonths> {
    inline val isZero: Boolean
        get() = this.value == 0L

    inline val isNegative: Boolean
        get() = this.value < 0L

    inline val isPositive: Boolean
        get() = this.value > 0L

    val absoluteValue: LongMonths
        get() = LongMonths(this.value.absoluteValue)

    /**
     * The number of whole years that fit within this span of months
     */
    val inWholeYears get() = LongYears(value / MONTHS_IN_YEAR)

    operator fun unaryMinus() = LongMonths(-value)

    operator fun plus(years: LongYears) = this + years.inMonths
    operator fun plus(years: IntYears) = this + years.inMonths
    operator fun plus(months: LongMonths) = LongMonths(value + months.value)
    operator fun plus(months: IntMonths) = LongMonths(value + months.value)
    operator fun minus(years: LongYears) = plus(-years)
    operator fun minus(years: IntYears) = plus(-years)
    operator fun minus(months: LongMonths) = plus(-months)
    operator fun minus(months: IntMonths) = plus(-months)

    operator fun times(scalar: Long) = LongMonths(value * scalar)
    operator fun times(scalar: Int) = LongMonths(value * scalar)

    operator fun div(scalar: Long) = LongMonths(value / scalar)
    operator fun div(scalar: Int) = LongMonths(value / scalar)

    operator fun rem(scalar: Long) = LongMonths(value % scalar)
    operator fun rem(scalar: Int) = LongMonths(value % scalar)

    fun toInt() = IntMonths(value.toInt())
    fun toIntExact() = IntMonths(value.toIntExact())

    override fun compareTo(other: LongMonths) = value.compareTo(other.value)

    operator fun compareTo(other: IntMonths) = value.compareTo(other.value)
    operator fun compareTo(other: IntYears) = value.compareTo(other.toLong().inMonths.value)

    operator fun compareTo(other: LongYears): Int {
        return when {
            other.value > Long.MAX_VALUE / MONTHS_IN_YEAR -> -1
            other.value < Long.MIN_VALUE / MONTHS_IN_YEAR -> 1
            else -> value.compareTo(other.inMonths.value)
        }
    }

    override fun toString(): String = if (this.isZero) {
        "P0D"
    } else {
        buildString {
            append('P')
            append(value)
            append('M')
        }
    }
}

val Long.months: LongMonths get() = LongMonths(this)