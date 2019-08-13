package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_MINUTE
import dev.erikchristensen.islandtime.internal.SECONDS_PER_MINUTE

inline class MinuteSpan(val value: Int) : Comparable<MinuteSpan> {
    override fun compareTo(other: MinuteSpan) = value.compareTo(other.value)
}

operator fun MinuteSpan.unaryPlus() = MinuteSpan(+value)
operator fun MinuteSpan.unaryMinus() = MinuteSpan(-value)

operator fun MinuteSpan.plus(minutes: MinuteSpan) = MinuteSpan(value + minutes.value)
operator fun MinuteSpan.plus(hours: HourSpan) = this + hours.asMinutes()
operator fun MinuteSpan.plus(seconds: SecondSpan) = this.asSeconds() + seconds
operator fun MinuteSpan.plus(nanoseconds: NanosecondSpan) = this.asNanoseconds() + nanoseconds

operator fun MinuteSpan.minus(minutes: MinuteSpan) = plus(-minutes)
operator fun MinuteSpan.minus(hours: HourSpan) = plus(-hours)
operator fun MinuteSpan.minus(seconds: SecondSpan) = plus(-seconds)
operator fun MinuteSpan.minus(nanoseconds: NanosecondSpan) = plus(-nanoseconds)

//operator fun MinuteSpan.times(scalar: Long) = MinuteSpan(value * scalar)
operator fun MinuteSpan.times(scalar: Int) = MinuteSpan(value * scalar)

//operator fun MinuteSpan.div(scalar: Long) = MinuteSpan(value / scalar)
operator fun MinuteSpan.div(scalar: Int) = MinuteSpan(value / scalar)

//operator fun MinuteSpan.rem(scalar: Long) = MinuteSpan(value % scalar)
operator fun MinuteSpan.rem(scalar: Int) = MinuteSpan(value % scalar)

fun MinuteSpan.toLong() = LongMinuteSpan(value.toLong())

fun MinuteSpan.asSeconds() = SecondSpan(value * SECONDS_PER_MINUTE)
fun MinuteSpan.asNanoseconds() = NanosecondSpan(value * NANOSECONDS_PER_MINUTE.toInt())

inline val Int.minutes: MinuteSpan get() = MinuteSpan(this)

inline class LongMinuteSpan(val value: Long) : Comparable<LongMinuteSpan> {
    override fun compareTo(other: LongMinuteSpan) = value.compareTo(other.value)
}

operator fun LongMinuteSpan.unaryPlus() = LongMinuteSpan(+value)
operator fun LongMinuteSpan.unaryMinus() = LongMinuteSpan(-value)

operator fun LongMinuteSpan.plus(minutes: LongMinuteSpan) = LongMinuteSpan(value + minutes.value)
operator fun LongMinuteSpan.plus(hours: LongHourSpan) = this + hours.asMinutes()
operator fun LongMinuteSpan.plus(seconds: LongSecondSpan) = this.asSeconds() + seconds
operator fun LongMinuteSpan.plus(nanoseconds: LongNanosecondSpan) = this.asNanoseconds() + nanoseconds

operator fun LongMinuteSpan.minus(minutes: LongMinuteSpan) = plus(-minutes)
operator fun LongMinuteSpan.minus(hours: LongHourSpan) = plus(-hours)
operator fun LongMinuteSpan.minus(seconds: LongSecondSpan) = plus(-seconds)
operator fun LongMinuteSpan.minus(nanoseconds: LongNanosecondSpan) = plus(-nanoseconds)

operator fun LongMinuteSpan.times(scalar: Long) = LongMinuteSpan(value * scalar)
operator fun LongMinuteSpan.times(scalar: Int) = LongMinuteSpan(value * scalar)

operator fun LongMinuteSpan.div(scalar: Long) = LongMinuteSpan(value / scalar)
operator fun LongMinuteSpan.div(scalar: Int) = LongMinuteSpan(value / scalar)

operator fun LongMinuteSpan.rem(scalar: Long) = LongMinuteSpan(value % scalar)
operator fun LongMinuteSpan.rem(scalar: Int) = LongMinuteSpan(value % scalar)

fun LongMinuteSpan.toInt() = MinuteSpan(value.toInt())

fun LongMinuteSpan.asSeconds() = LongSecondSpan(value * SECONDS_PER_MINUTE)
fun LongMinuteSpan.asNanoseconds() = LongNanosecondSpan(value * NANOSECONDS_PER_MINUTE)

inline val Long.minutes: LongMinuteSpan get() = LongMinuteSpan(this)