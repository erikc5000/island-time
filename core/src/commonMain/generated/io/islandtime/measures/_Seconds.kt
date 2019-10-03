//
// This file is auto-generated by 'tools:code-generator'
//
@file:JvmMultifileClass
@file:JvmName("SecondsKt")

package io.islandtime.measures

import io.islandtime.internal.SECONDS_PER_DAY
import io.islandtime.internal.SECONDS_PER_HOUR
import io.islandtime.internal.SECONDS_PER_MICROSECOND
import io.islandtime.internal.SECONDS_PER_MILLISECOND
import io.islandtime.internal.SECONDS_PER_MINUTE
import io.islandtime.internal.SECONDS_PER_NANOSECOND
import io.islandtime.internal.timesExact
import io.islandtime.internal.toIntExact
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.math.absoluteValue

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class IntSeconds(
  val value: Int
) : Comparable<IntSeconds> {
  val inWholeDays: IntDays
    get() = (this.value / SECONDS_PER_DAY).days

  val inWholeHours: IntHours
    get() = (this.value / SECONDS_PER_HOUR).hours

  val inWholeMinutes: IntMinutes
    get() = (this.value / SECONDS_PER_MINUTE).minutes

  val inMilliseconds: LongMilliseconds
    get() = (this.value.toLong() * SECONDS_PER_MILLISECOND).milliseconds

  val inMicroseconds: LongMicroseconds
    get() = (this.value.toLong() * SECONDS_PER_MICROSECOND).microseconds

  val inNanoseconds: LongNanoseconds
    get() = (this.value.toLong() * SECONDS_PER_NANOSECOND).nanoseconds

  val isZero: Boolean
    inline get() = this.value == 0

  val isNegative: Boolean
    inline get() = this.value < 0

  val isPositive: Boolean
    inline get() = this.value > 0

  val absoluteValue: IntSeconds
    get() = IntSeconds(this.value.absoluteValue)

  override fun compareTo(other: IntSeconds): Int = this.value.compareTo(other.value)

  override fun toString(): String = if (this.isZero) {
      "PT0S"
  } else {
      buildString {
          append("PT")
          append(value)
          append('S')
      }
  }

  operator fun unaryMinus() = IntSeconds(-value)

  operator fun plus(days: IntDays) = this + days.inSeconds

  operator fun plus(days: LongDays) = this.toLong() + days.inSeconds

  operator fun plus(hours: IntHours) = this + hours.inSeconds

  operator fun plus(hours: LongHours) = this.toLong() + hours.inSeconds

  operator fun plus(minutes: IntMinutes) = this + minutes.inSeconds

  operator fun plus(minutes: LongMinutes) = this.toLong() + minutes.inSeconds

  operator fun plus(seconds: IntSeconds) = IntSeconds(this.value + seconds.value)

  operator fun plus(seconds: LongSeconds) = LongSeconds(this.value.toLong() + seconds.value)

  operator fun plus(milliseconds: IntMilliseconds) = this.inMilliseconds + milliseconds

  operator fun plus(milliseconds: LongMilliseconds) = this.toLong().inMilliseconds + milliseconds

  operator fun plus(microseconds: IntMicroseconds) = this.inMicroseconds + microseconds

  operator fun plus(microseconds: LongMicroseconds) = this.toLong().inMicroseconds + microseconds

  operator fun plus(nanoseconds: IntNanoseconds) = this.inNanoseconds + nanoseconds

  operator fun plus(nanoseconds: LongNanoseconds) = this.toLong().inNanoseconds + nanoseconds

  operator fun minus(days: IntDays) = plus(-days)

  operator fun minus(days: LongDays) = plus(-days)

  operator fun minus(hours: IntHours) = plus(-hours)

  operator fun minus(hours: LongHours) = plus(-hours)

  operator fun minus(minutes: IntMinutes) = plus(-minutes)

  operator fun minus(minutes: LongMinutes) = plus(-minutes)

  operator fun minus(seconds: IntSeconds) = plus(-seconds)

  operator fun minus(seconds: LongSeconds) = plus(-seconds)

  operator fun minus(milliseconds: IntMilliseconds) = plus(-milliseconds)

  operator fun minus(milliseconds: LongMilliseconds) = plus(-milliseconds)

  operator fun minus(microseconds: IntMicroseconds) = plus(-microseconds)

  operator fun minus(microseconds: LongMicroseconds) = plus(-microseconds)

  operator fun minus(nanoseconds: IntNanoseconds) = plus(-nanoseconds)

  operator fun minus(nanoseconds: LongNanoseconds) = plus(-nanoseconds)

  operator fun times(scalar: Int) = IntSeconds(this.value * scalar)

  operator fun times(scalar: Long) = this.toLong() * scalar

  operator fun div(scalar: Int) = IntSeconds(this.value / scalar)

  operator fun div(scalar: Long) = this.toLong() / scalar

  operator fun rem(scalar: Int) = IntSeconds(this.value % scalar)

  operator fun rem(scalar: Long) = this.toLong() % scalar

  inline fun <T> toComponents(action: (
    days: IntDays,
    hours: IntHours,
    minutes: IntMinutes,
    seconds: IntSeconds
  ) -> T): T {
    val days = this.inWholeDays
    val hours = (this - days).inWholeHours
    val minutes = (this - days - hours).inWholeMinutes
    val seconds = (this - days - hours - minutes)
    return action(days, hours, minutes, seconds)
  }

  inline fun <T> toComponents(action: (
    hours: IntHours,
    minutes: IntMinutes,
    seconds: IntSeconds
  ) -> T): T {
    val hours = this.inWholeHours
    val minutes = (this - hours).inWholeMinutes
    val seconds = (this - hours - minutes)
    return action(hours, minutes, seconds)
  }

  inline fun <T> toComponents(action: (minutes: IntMinutes, seconds: IntSeconds) -> T): T {
    val minutes = this.inWholeMinutes
    val seconds = (this - minutes)
    return action(minutes, seconds)
  }

  fun toLong() = LongSeconds(this.value.toLong())

  companion object {
    val MIN: IntSeconds = IntSeconds(Int.MIN_VALUE)

    val MAX: IntSeconds = IntSeconds(Int.MAX_VALUE)
  }
}

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class LongSeconds(
  val value: Long
) : Comparable<LongSeconds> {
  val inWholeDays: LongDays
    get() = (this.value / SECONDS_PER_DAY).days

  val inWholeHours: LongHours
    get() = (this.value / SECONDS_PER_HOUR).hours

  val inWholeMinutes: LongMinutes
    get() = (this.value / SECONDS_PER_MINUTE).minutes

  val inMilliseconds: LongMilliseconds
    get() = (this.value * SECONDS_PER_MILLISECOND).milliseconds

  val inMicroseconds: LongMicroseconds
    get() = (this.value * SECONDS_PER_MICROSECOND).microseconds

  val inNanoseconds: LongNanoseconds
    get() = (this.value * SECONDS_PER_NANOSECOND).nanoseconds

  val isZero: Boolean
    inline get() = this.value == 0L

  val isNegative: Boolean
    inline get() = this.value < 0L

  val isPositive: Boolean
    inline get() = this.value > 0L

  val absoluteValue: LongSeconds
    get() = LongSeconds(this.value.absoluteValue)

  override fun compareTo(other: LongSeconds): Int = this.value.compareTo(other.value)

  override fun toString(): String = if (this.isZero) {
      "PT0S"
  } else {
      buildString {
          append("PT")
          append(value)
          append('S')
      }
  }

  fun inMillisecondsExact() = (this.value timesExact SECONDS_PER_MILLISECOND).milliseconds

  fun inMicrosecondsExact() = (this.value timesExact SECONDS_PER_MICROSECOND).microseconds

  fun inNanosecondsExact() = (this.value timesExact SECONDS_PER_NANOSECOND).nanoseconds

  operator fun unaryMinus() = LongSeconds(-value)

  operator fun plus(days: IntDays) = this + days.inSeconds

  operator fun plus(days: LongDays) = this + days.inSeconds

  operator fun plus(hours: IntHours) = this + hours.inSeconds

  operator fun plus(hours: LongHours) = this + hours.inSeconds

  operator fun plus(minutes: IntMinutes) = this + minutes.inSeconds

  operator fun plus(minutes: LongMinutes) = this + minutes.inSeconds

  operator fun plus(seconds: IntSeconds) = LongSeconds(this.value + seconds.value)

  operator fun plus(seconds: LongSeconds) = LongSeconds(this.value + seconds.value)

  operator fun plus(milliseconds: IntMilliseconds) = this.inMilliseconds + milliseconds

  operator fun plus(milliseconds: LongMilliseconds) = this.inMilliseconds + milliseconds

  operator fun plus(microseconds: IntMicroseconds) = this.inMicroseconds + microseconds

  operator fun plus(microseconds: LongMicroseconds) = this.inMicroseconds + microseconds

  operator fun plus(nanoseconds: IntNanoseconds) = this.inNanoseconds + nanoseconds

  operator fun plus(nanoseconds: LongNanoseconds) = this.inNanoseconds + nanoseconds

  operator fun minus(days: IntDays) = plus(-days)

  operator fun minus(days: LongDays) = plus(-days)

  operator fun minus(hours: IntHours) = plus(-hours)

  operator fun minus(hours: LongHours) = plus(-hours)

  operator fun minus(minutes: IntMinutes) = plus(-minutes)

  operator fun minus(minutes: LongMinutes) = plus(-minutes)

  operator fun minus(seconds: IntSeconds) = plus(-seconds)

  operator fun minus(seconds: LongSeconds) = plus(-seconds)

  operator fun minus(milliseconds: IntMilliseconds) = plus(-milliseconds)

  operator fun minus(milliseconds: LongMilliseconds) = plus(-milliseconds)

  operator fun minus(microseconds: IntMicroseconds) = plus(-microseconds)

  operator fun minus(microseconds: LongMicroseconds) = plus(-microseconds)

  operator fun minus(nanoseconds: IntNanoseconds) = plus(-nanoseconds)

  operator fun minus(nanoseconds: LongNanoseconds) = plus(-nanoseconds)

  operator fun times(scalar: Int) = LongSeconds(this.value * scalar)

  operator fun times(scalar: Long) = LongSeconds(this.value * scalar)

  operator fun div(scalar: Int) = LongSeconds(this.value / scalar)

  operator fun div(scalar: Long) = LongSeconds(this.value / scalar)

  operator fun rem(scalar: Int) = LongSeconds(this.value % scalar)

  operator fun rem(scalar: Long) = LongSeconds(this.value % scalar)

  inline fun <T> toComponents(action: (
    days: LongDays,
    hours: IntHours,
    minutes: IntMinutes,
    seconds: IntSeconds
  ) -> T): T {
    val days = this.inWholeDays
    val hours = (this - days).toInt().inWholeHours
    val minutes = (this - days - hours).toInt().inWholeMinutes
    val seconds = (this - days - hours - minutes).toInt()
    return action(days, hours, minutes, seconds)
  }

  inline fun <T> toComponents(action: (
    hours: LongHours,
    minutes: IntMinutes,
    seconds: IntSeconds
  ) -> T): T {
    val hours = this.inWholeHours
    val minutes = (this - hours).toInt().inWholeMinutes
    val seconds = (this - hours - minutes).toInt()
    return action(hours, minutes, seconds)
  }

  inline fun <T> toComponents(action: (minutes: LongMinutes, seconds: IntSeconds) -> T): T {
    val minutes = this.inWholeMinutes
    val seconds = (this - minutes).toInt()
    return action(minutes, seconds)
  }

  fun toInt() = IntSeconds(this.value.toInt())

  fun toIntExact() = IntSeconds(this.value.toIntExact())

  companion object {
    val MIN: LongSeconds = LongSeconds(Long.MIN_VALUE)

    val MAX: LongSeconds = LongSeconds(Long.MAX_VALUE)
  }
}

val Int.seconds: IntSeconds
  get() = IntSeconds(this)

val Long.seconds: LongSeconds
  get() = LongSeconds(this)