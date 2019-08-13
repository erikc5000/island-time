package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_SECOND
import dev.erikchristensen.islandtime.internal.SECONDS_PER_HOUR
import dev.erikchristensen.islandtime.internal.SECONDS_PER_MINUTE

inline class SecondSpan(val value: Int) : Comparable<SecondSpan> {
    override fun compareTo(other: SecondSpan) = value.compareTo(other.value)
}

operator fun SecondSpan.unaryPlus() = SecondSpan(+value)
operator fun SecondSpan.unaryMinus() = SecondSpan(-value)

operator fun SecondSpan.plus(seconds: SecondSpan) = SecondSpan(value + seconds.value)
operator fun SecondSpan.plus(hours: HourSpan) = this + hours.asSeconds()
operator fun SecondSpan.plus(minutes: MinuteSpan) = this + minutes.asSeconds()
operator fun SecondSpan.plus(nanoseconds: NanosecondSpan) = this.asNanoseconds() + nanoseconds

operator fun SecondSpan.minus(seconds: SecondSpan) = plus(-seconds)
operator fun SecondSpan.minus(hours: HourSpan) = plus(-hours)
operator fun SecondSpan.minus(minutes: MinuteSpan) = plus(-minutes)
operator fun SecondSpan.minus(nanoseconds: NanosecondSpan) = plus(-nanoseconds)

operator fun SecondSpan.times(scalar: Long) = SecondSpan((value * scalar).toInt())
operator fun SecondSpan.times(scalar: Int) = SecondSpan(value * scalar)

operator fun SecondSpan.div(scalar: Long) = SecondSpan((value / scalar).toInt())
operator fun SecondSpan.div(scalar: Int) = SecondSpan(value / scalar)

operator fun SecondSpan.rem(scalar: Long) = SecondSpan((value % scalar).toInt())
operator fun SecondSpan.rem(scalar: Int) = SecondSpan(value % scalar)

fun SecondSpan.toLong() = LongSecondSpan(value.toLong())

fun SecondSpan.asWholeHours() = HourSpan(value / SECONDS_PER_HOUR)
fun SecondSpan.asWholeMinutes() = HourSpan(value / SECONDS_PER_MINUTE)
fun SecondSpan.asNanoseconds() = NanosecondSpan(value * NANOSECONDS_PER_SECOND.toInt())

inline val Int.seconds: SecondSpan get() = SecondSpan(this)

inline class LongSecondSpan(val value: Long) : Comparable<LongSecondSpan> {
    override fun compareTo(other: LongSecondSpan) = value.compareTo(other.value)
}

operator fun LongSecondSpan.unaryPlus() = LongSecondSpan(+value)

operator fun LongSecondSpan.unaryMinus() = LongSecondSpan(-value)

operator fun LongSecondSpan.plus(seconds: LongSecondSpan) = LongSecondSpan(value + seconds.value)

operator fun LongSecondSpan.plus(hours: LongHourSpan) = this + hours.asSeconds()
operator fun LongSecondSpan.plus(minutes: LongMinuteSpan) = this + minutes.asSeconds()
operator fun LongSecondSpan.plus(nanoseconds: LongNanosecondSpan) = this.asNanoseconds() + nanoseconds
operator fun LongSecondSpan.minus(seconds: LongSecondSpan) = plus(-seconds)
operator fun LongSecondSpan.minus(hours: LongHourSpan) = plus(-hours)
operator fun LongSecondSpan.minus(minutes: LongMinuteSpan) = plus(-minutes)
operator fun LongSecondSpan.minus(nanoseconds: LongNanosecondSpan) = plus(-nanoseconds)

operator fun LongSecondSpan.times(scalar: Long) = LongSecondSpan(value * scalar)
operator fun LongSecondSpan.times(scalar: Int) = LongSecondSpan(value * scalar)

operator fun LongSecondSpan.div(scalar: Long) = LongSecondSpan(value / scalar)
operator fun LongSecondSpan.div(scalar: Int) = LongSecondSpan(value / scalar)

operator fun LongSecondSpan.rem(scalar: Long) = LongSecondSpan(value % scalar)
operator fun LongSecondSpan.rem(scalar: Int) = LongSecondSpan(value % scalar)

fun LongSecondSpan.toInt() = SecondSpan(value.toInt())

fun LongSecondSpan.asWholeHours() = LongHourSpan(value / SECONDS_PER_HOUR)
fun LongSecondSpan.asWholeMinutes() = LongMinuteSpan(value / SECONDS_PER_MINUTE)
fun LongSecondSpan.asNanoseconds() = LongNanosecondSpan(value * NANOSECONDS_PER_SECOND)

inline val Long.seconds: LongSecondSpan get() = LongSecondSpan(this)