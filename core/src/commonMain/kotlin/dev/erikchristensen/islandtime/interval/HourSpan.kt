package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MINUTES_PER_HOUR
import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_HOUR
import dev.erikchristensen.islandtime.internal.SECONDS_PER_HOUR

inline class HourSpan(val value: Int) : Comparable<HourSpan> {
    override fun compareTo(other: HourSpan) = value.compareTo(other.value)
}

operator fun HourSpan.unaryPlus() = HourSpan(+value)
operator fun HourSpan.unaryMinus() = HourSpan(-value)

operator fun HourSpan.plus(hours: HourSpan) = HourSpan(value + hours.value)
operator fun HourSpan.plus(minutes: MinuteSpan) = this.asMinutes() + minutes
operator fun HourSpan.plus(seconds: SecondSpan) = this.asSeconds() + seconds
operator fun HourSpan.plus(nanoseconds: NanosecondSpan) = this.asNanoseconds() + nanoseconds

operator fun HourSpan.minus(hours: HourSpan) = plus(-hours)
operator fun HourSpan.minus(minutes: MinuteSpan) = plus(-minutes)
operator fun HourSpan.minus(seconds: SecondSpan) = plus(-seconds)
operator fun HourSpan.minus(nanoseconds: NanosecondSpan) = plus(-nanoseconds)

//operator fun HourSpan.times(scalar: Long) = HourSpan(value * scalar)
operator fun HourSpan.times(scalar: Int) = HourSpan(value * scalar)

//operator fun HourSpan.div(scalar: Long) = HourSpan(value / scalar)
operator fun HourSpan.div(scalar: Int) = HourSpan(value / scalar)

//operator fun HourSpan.rem(scalar: Long) = HourSpan(value % scalar)
operator fun HourSpan.rem(scalar: Int) = HourSpan(value % scalar)

fun HourSpan.toLong() = LongHourSpan(value.toLong())

fun HourSpan.asMinutes() = MinuteSpan(value * MINUTES_PER_HOUR)
fun HourSpan.asSeconds() = SecondSpan(value * SECONDS_PER_HOUR)
fun HourSpan.asNanoseconds() = NanosecondSpan(value * NANOSECONDS_PER_HOUR.toInt())

//inline val Int.hours: HourSpan get() = HourSpan(this.toLong())
inline val Int.hours: HourSpan get() = HourSpan(this)

inline class LongHourSpan(val value: Long) : Comparable<LongHourSpan> {
    override fun compareTo(other: LongHourSpan) = value.compareTo(other.value)
}

operator fun LongHourSpan.unaryPlus() = LongHourSpan(+value)
operator fun LongHourSpan.unaryMinus() = LongHourSpan(-value)

operator fun LongHourSpan.plus(hours: LongHourSpan) = LongHourSpan(value + hours.value)
operator fun LongHourSpan.plus(minutes: LongMinuteSpan) = this.asMinutes() + minutes
operator fun LongHourSpan.plus(seconds: LongSecondSpan) = this.asSeconds() + seconds
operator fun LongHourSpan.plus(nanoseconds: LongNanosecondSpan) = this.asNanoseconds() + nanoseconds

operator fun LongHourSpan.minus(hours: LongHourSpan) = plus(-hours)
operator fun LongHourSpan.minus(minutes: LongMinuteSpan) = plus(-minutes)
operator fun LongHourSpan.minus(seconds: LongSecondSpan) = plus(-seconds)
operator fun LongHourSpan.minus(nanoseconds: LongNanosecondSpan) = plus(-nanoseconds)

operator fun LongHourSpan.times(scalar: Long) = LongHourSpan(value * scalar)
operator fun LongHourSpan.times(scalar: Int) = LongHourSpan(value * scalar)

operator fun LongHourSpan.div(scalar: Long) = LongHourSpan(value / scalar)
operator fun LongHourSpan.div(scalar: Int) = LongHourSpan(value / scalar)

operator fun LongHourSpan.rem(scalar: Long) = LongHourSpan(value % scalar)
operator fun LongHourSpan.rem(scalar: Int) = LongHourSpan(value % scalar)

fun LongHourSpan.toInt() = HourSpan(value.toInt())

fun LongHourSpan.asMinutes() = LongMinuteSpan(value * MINUTES_PER_HOUR)
fun LongHourSpan.asSeconds() = LongSecondSpan(value * SECONDS_PER_HOUR)
fun LongHourSpan.asNanoseconds() = LongNanosecondSpan(value * NANOSECONDS_PER_HOUR)

inline val Long.hours: LongHourSpan get() = LongHourSpan(this)