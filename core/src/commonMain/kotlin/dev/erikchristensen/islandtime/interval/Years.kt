package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR
import dev.erikchristensen.islandtime.internal.timesExact
import dev.erikchristensen.islandtime.internal.toIntExact
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

    val inMonths get() = IntMonths(value * MONTHS_IN_YEAR)
    fun inMonthsExact() = IntMonths(value timesExact MONTHS_IN_YEAR)

    fun toLong() = LongYears(value.toLong())

    operator fun unaryMinus() = IntYears(-value)
    operator fun plus(years: IntYears) = IntYears(value + years.value)
    operator fun plus(months: IntMonths) = this.inMonths + months
    operator fun minus(years: IntYears) = plus(-years)
    operator fun minus(months: IntMonths) = plus(-months)

    operator fun times(scalar: Int) = IntYears(value * scalar)
    operator fun div(scalar: Int) = IntYears(value / scalar)
    operator fun rem(scalar: Int) = IntYears(value % scalar)

    override fun compareTo(other: IntYears) = value.compareTo(other.value)

    operator fun compareTo(other: LongYears) = value.compareTo(other.value)
    operator fun compareTo(other: LongMonths) = toLong().inMonths.value.compareTo(other.value)
    operator fun compareTo(other: IntMonths) = toLong().inMonths.value.compareTo(other.value)

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

    val inMonths get() = LongMonths(value * MONTHS_IN_YEAR)
    fun inMonthsExact() = LongMonths(value timesExact MONTHS_IN_YEAR)

    fun toInt() = IntYears(value.toInt())
    fun toIntExact() = IntYears(value.toIntExact())

    operator fun unaryMinus() = LongYears(-value)
    operator fun plus(years: LongYears) = LongYears(value + years.value)
    operator fun plus(months: LongMonths) = this.inMonths + months
    operator fun minus(years: LongYears) = plus(-years)
    operator fun minus(months: LongMonths) = plus(-months)

    operator fun times(scalar: Long) = LongYears(value * scalar)
    operator fun times(scalar: Int) = LongYears(value * scalar)

    operator fun div(scalar: Long) = LongYears(value / scalar)
    operator fun div(scalar: Int) = LongYears(value / scalar)

    operator fun rem(scalar: Long) = LongYears(value % scalar)
    operator fun rem(scalar: Int) = LongYears(value % scalar)

    override fun compareTo(other: LongYears) = value.compareTo(other.value)

    operator fun compareTo(other: IntYears) = value.compareTo(other.value)

    operator fun compareTo(other: LongMonths): Int {
        return when {
            value > Long.MAX_VALUE / MONTHS_IN_YEAR -> 1
            value < Long.MIN_VALUE / MONTHS_IN_YEAR -> -1
            else -> inMonths.value.compareTo(other.value)
        }
    }

    operator fun compareTo(other: IntMonths): Int {
        return when {
            value > Int.MAX_VALUE -> 1
            value < Int.MIN_VALUE -> -1
            else -> inMonths.value.compareTo(other.value)
        }
    }

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

val Long.years: LongYears get() = LongYears(this)