//
// This file is auto-generated by 'tools:code-generator'
//
@file:JvmMultifileClass
@file:JvmName("HoursKt")
@file:OptIn(ExperimentalContracts::class)

package io.islandtime.measures

import dev.erikchristensen.javamath2kmp.absExact
import dev.erikchristensen.javamath2kmp.minusExact
import dev.erikchristensen.javamath2kmp.negateExact
import dev.erikchristensen.javamath2kmp.plusExact
import dev.erikchristensen.javamath2kmp.timesExact
import dev.erikchristensen.javamath2kmp.toIntExact
import io.islandtime.`internal`.HOURS_PER_DAY
import io.islandtime.`internal`.MICROSECONDS_PER_HOUR
import io.islandtime.`internal`.MILLISECONDS_PER_HOUR
import io.islandtime.`internal`.MINUTES_PER_HOUR
import io.islandtime.`internal`.NANOSECONDS_PER_HOUR
import io.islandtime.`internal`.SECONDS_PER_HOUR
import io.islandtime.`internal`.deprecatedToError
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Deprecated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.OptIn
import kotlin.PublishedApi
import kotlin.String
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.hours as kotlinHours
import kotlin.time.DurationUnit as KotlinDurationUnit
import kotlin.time.Duration as KotlinDuration

@Deprecated(
  message = "Replace with Hours.",
  replaceWith = ReplaceWith("Hours"),
  level = DeprecationLevel.ERROR,
)
public typealias IntHours = Hours

@Deprecated(
  message = "Replace with Hours.",
  replaceWith = ReplaceWith("Hours"),
  level = DeprecationLevel.ERROR,
)
public typealias LongHours = Hours

@JvmInline
public value class Hours(
  /**
   * The underlying value.
   */
  public val `value`: Long,
) : Comparable<Hours> {
  /**
   * The absolute value of this duration.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public val absoluteValue: Hours
    get() = Hours(absExact(value))

  /**
   * Converts this duration to nanoseconds.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public val inNanoseconds: Nanoseconds
    get() = Nanoseconds(value timesExact NANOSECONDS_PER_HOUR)

  /**
   * Converts this duration to nanoseconds without checking for overflow.
   */
  internal val inNanosecondsUnchecked: Nanoseconds
    get() = Nanoseconds(value * NANOSECONDS_PER_HOUR)

  /**
   * Converts this duration to microseconds.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public val inMicroseconds: Microseconds
    get() = Microseconds(value timesExact MICROSECONDS_PER_HOUR)

  /**
   * Converts this duration to microseconds without checking for overflow.
   */
  internal val inMicrosecondsUnchecked: Microseconds
    get() = Microseconds(value * MICROSECONDS_PER_HOUR)

  /**
   * Converts this duration to milliseconds.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public val inMilliseconds: Milliseconds
    get() = Milliseconds(value timesExact MILLISECONDS_PER_HOUR)

  /**
   * Converts this duration to milliseconds without checking for overflow.
   */
  internal val inMillisecondsUnchecked: Milliseconds
    get() = Milliseconds(value * MILLISECONDS_PER_HOUR)

  /**
   * Converts this duration to seconds.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public val inSeconds: Seconds
    get() = Seconds(value timesExact SECONDS_PER_HOUR)

  /**
   * Converts this duration to seconds without checking for overflow.
   */
  internal val inSecondsUnchecked: Seconds
    get() = Seconds(value * SECONDS_PER_HOUR)

  /**
   * Converts this duration to minutes.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public val inMinutes: Minutes
    get() = Minutes(value timesExact MINUTES_PER_HOUR)

  /**
   * Converts this duration to minutes without checking for overflow.
   */
  internal val inMinutesUnchecked: Minutes
    get() = Minutes(value * MINUTES_PER_HOUR)

  /**
   * Converts this duration to the number of whole days.
   */
  public val inWholeDays: Days
    get() = Days(value / HOURS_PER_DAY)

  @Deprecated(
    message = "Use inWholeDays instead.",
    replaceWith = ReplaceWith("this.inWholeDays"),
    level = DeprecationLevel.ERROR,
  )
  public val inDays: Days
    get() = deprecatedToError()

  public constructor(`value`: Int) : this(value.toLong())

  /**
   * Checks if this duration is zero.
   */
  @Deprecated(
    message = "Replace with direct comparison.",
    replaceWith = ReplaceWith("this == 0L.hours"),
    level = DeprecationLevel.ERROR,
  )
  public fun isZero(): Boolean = value == 0L

  /**
   * Checks if this duration is negative.
   */
  @Deprecated(
    message = "Replace with direct comparison.",
    replaceWith = ReplaceWith("this < 0L.hours"),
    level = DeprecationLevel.ERROR,
  )
  public fun isNegative(): Boolean = value < 0L

  /**
   * Checks if this duration is positive.
   */
  @Deprecated(
    message = "Replace with direct comparison.",
    replaceWith = ReplaceWith("this > 0L.hours"),
    level = DeprecationLevel.ERROR,
  )
  public fun isPositive(): Boolean = value > 0L

  override fun compareTo(other: Hours): Int = value.compareTo(other.value)

  /**
   * Converts this duration to a [kotlin.time.Duration].
   */
  public fun toKotlinDuration(): KotlinDuration = value.kotlinHours

  /**
   * Converts this duration to an ISO-8601 time interval representation.
   */
  override fun toString(): String {
     return when (value) {
       0L -> "PT0H"
       Long.MIN_VALUE -> "-PT9223372036854775808H"
       else -> buildString {
         if (value < 0) { append('-') }
         append("PT")
         append(value.absoluteValue)
         append('H')
       }
     }
  }

  /**
   * Negates this duration.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public operator fun unaryMinus(): Hours = Hours(value.negateExact())

  /**
   * Negates this duration without checking for overflow.
   */
  internal fun negateUnchecked(): Hours = Hours(-value)

  public operator fun plus(nanoseconds: Nanoseconds): Nanoseconds = this.inNanoseconds + nanoseconds

  public operator fun minus(nanoseconds: Nanoseconds): Nanoseconds = this.inNanoseconds -
      nanoseconds

  public operator fun plus(microseconds: Microseconds): Microseconds = this.inMicroseconds +
      microseconds

  public operator fun minus(microseconds: Microseconds): Microseconds = this.inMicroseconds -
      microseconds

  public operator fun plus(milliseconds: Milliseconds): Milliseconds = this.inMilliseconds +
      milliseconds

  public operator fun minus(milliseconds: Milliseconds): Milliseconds = this.inMilliseconds -
      milliseconds

  public operator fun plus(seconds: Seconds): Seconds = this.inSeconds + seconds

  public operator fun minus(seconds: Seconds): Seconds = this.inSeconds - seconds

  public operator fun plus(minutes: Minutes): Minutes = this.inMinutes + minutes

  public operator fun minus(minutes: Minutes): Minutes = this.inMinutes - minutes

  public operator fun plus(hours: Hours): Hours = Hours(value plusExact hours.value)

  public operator fun minus(hours: Hours): Hours = Hours(value minusExact hours.value)

  public operator fun plus(days: Days): Hours = this + days.inHours

  public operator fun minus(days: Days): Hours = this - days.inHours

  /**
   * Multiplies this duration by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public operator fun times(scalar: Int): Hours = Hours(value timesExact scalar)

  /**
   * Returns this duration divided by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs or the scalar is zero
   */
  public operator fun div(scalar: Int): Hours {
     return if (scalar == -1) {
       -this
     } else {
       Hours(value / scalar)
     }
  }

  /**
   * Returns the remainder of this duration divided by a scalar value.
   */
  public operator fun rem(scalar: Int): Hours = Hours(value % scalar)

  /**
   * Multiplies this duration by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public operator fun times(scalar: Long): Hours = Hours(value timesExact scalar)

  /**
   * Returns this duration divided by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs or the scalar is zero
   */
  public operator fun div(scalar: Long): Hours {
     return if (scalar == -1L) {
       -this
     } else {
       Hours(value / scalar)
     }
  }

  /**
   * Returns the remainder of this duration divided by a scalar value.
   */
  public operator fun rem(scalar: Long): Hours = Hours(value % scalar)

  public inline fun <T> toComponentValues(action: (days: Long, hours: Int) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val days = (value / HOURS_PER_DAY)
    val hours = (value % HOURS_PER_DAY).toInt()
    return action(days, hours)
  }

  public inline fun <T> toComponents(action: (days: Days, hours: Hours) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
     return toComponentValues { days, hours ->
         action(Days(days), Hours(hours))
     }
  }

  /**
   * Converts this duration to an `Int` value.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public fun toInt(): Int = value.toIntExact()

  /**
   * Converts this duration to an `Int` value without checking for overflow.
   */
  internal fun toIntUnchecked(): Int = value.toInt()

  /**
   * Converts this duration to [IntHours].
   *
   * @throws ArithmeticException if overflow occurs
   */
  @Deprecated(
    message = "The 'Int' class no longer exists.",
    replaceWith = ReplaceWith("this"),
    level = DeprecationLevel.ERROR,
  )
  public fun toIntHours(): Hours = this

  /**
   * Converts this duration to [IntHours] without checking for overflow.
   */
  @Deprecated(
    message = "The 'Int' class no longer exists.",
    replaceWith = ReplaceWith("this"),
    level = DeprecationLevel.ERROR,
  )
  @PublishedApi
  internal fun toIntHoursUnchecked(): Hours = this

  /**
   * Converts this duration to a `Long` value.
   */
  public fun toLong(): Long = value

  /**
   * Converts this duration to a `Double` value.
   */
  public fun toDouble(): Double = value.toDouble()

  public companion object {
    /**
     * The smallest supported value.
     */
    public val MIN: Hours = Hours(Long.MIN_VALUE)

    /**
     * The largest supported value.
     */
    public val MAX: Hours = Hours(Long.MAX_VALUE)
  }
}

/**
 * Converts this value to a duration of hours.
 */
public val Int.hours: Hours
  get() = Hours(this)

/**
 * Multiplies this value by a duration of hours.
 *
 * @throws ArithmeticException if overflow occurs
 */
public operator fun Int.times(hours: Hours): Hours = hours * this

/**
 * Converts this value to a duration of hours.
 */
public val Long.hours: Hours
  get() = Hours(this)

/**
 * Multiplies this value by a duration of hours.
 *
 * @throws ArithmeticException if overflow occurs
 */
public operator fun Long.times(hours: Hours): Hours = hours * this

/**
 * Converts this duration to Island Time [Hours].
 */
public fun KotlinDuration.toIslandHours(): Hours = Hours(this.toLong(KotlinDurationUnit.HOURS))
