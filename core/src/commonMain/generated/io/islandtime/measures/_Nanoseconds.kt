//
// This file is auto-generated by 'tools:code-generator'
//
@file:JvmMultifileClass
@file:JvmName("NanosecondsKt")
@file:OptIn(ExperimentalContracts::class)

package io.islandtime.measures

import dev.erikchristensen.javamath2kmp.absExact
import dev.erikchristensen.javamath2kmp.minusExact
import dev.erikchristensen.javamath2kmp.negateExact
import dev.erikchristensen.javamath2kmp.plusExact
import dev.erikchristensen.javamath2kmp.timesExact
import dev.erikchristensen.javamath2kmp.toIntExact
import io.islandtime.`internal`.NANOSECONDS_PER_DAY
import io.islandtime.`internal`.NANOSECONDS_PER_HOUR
import io.islandtime.`internal`.NANOSECONDS_PER_MICROSECOND
import io.islandtime.`internal`.NANOSECONDS_PER_MILLISECOND
import io.islandtime.`internal`.NANOSECONDS_PER_MINUTE
import io.islandtime.`internal`.NANOSECONDS_PER_SECOND
import io.islandtime.`internal`.deprecatedToError
import io.islandtime.`internal`.toZeroPaddedString
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
import kotlin.time.Duration.Companion.nanoseconds as kotlinNanoseconds
import kotlin.time.DurationUnit as KotlinDurationUnit
import kotlin.time.Duration as KotlinDuration

@Deprecated(
  message = "Replace with Nanoseconds.",
  replaceWith = ReplaceWith("Nanoseconds"),
  level = DeprecationLevel.ERROR,
)
public typealias IntNanoseconds = Nanoseconds

@Deprecated(
  message = "Replace with Nanoseconds.",
  replaceWith = ReplaceWith("Nanoseconds"),
  level = DeprecationLevel.ERROR,
)
public typealias LongNanoseconds = Nanoseconds

@JvmInline
public value class Nanoseconds(
  /**
   * The underlying value.
   */
  public val `value`: Long,
) : Comparable<Nanoseconds> {
  /**
   * The absolute value of this duration.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public val absoluteValue: Nanoseconds
    get() = Nanoseconds(absExact(value))

  /**
   * Converts this duration to the number of whole microseconds.
   */
  public val inWholeMicroseconds: Microseconds
    get() = Microseconds(value / NANOSECONDS_PER_MICROSECOND)

  @Deprecated(
    message = "Use inWholeMicroseconds instead.",
    replaceWith = ReplaceWith("this.inWholeMicroseconds"),
    level = DeprecationLevel.ERROR,
  )
  public val inMicroseconds: Microseconds
    get() = deprecatedToError()

  /**
   * Converts this duration to the number of whole milliseconds.
   */
  public val inWholeMilliseconds: Milliseconds
    get() = Milliseconds(value / NANOSECONDS_PER_MILLISECOND)

  @Deprecated(
    message = "Use inWholeMilliseconds instead.",
    replaceWith = ReplaceWith("this.inWholeMilliseconds"),
    level = DeprecationLevel.ERROR,
  )
  public val inMilliseconds: Milliseconds
    get() = deprecatedToError()

  /**
   * Converts this duration to the number of whole seconds.
   */
  public val inWholeSeconds: Seconds
    get() = Seconds(value / NANOSECONDS_PER_SECOND)

  @Deprecated(
    message = "Use inWholeSeconds instead.",
    replaceWith = ReplaceWith("this.inWholeSeconds"),
    level = DeprecationLevel.ERROR,
  )
  public val inSeconds: Seconds
    get() = deprecatedToError()

  /**
   * Converts this duration to the number of whole minutes.
   */
  public val inWholeMinutes: Minutes
    get() = Minutes(value / NANOSECONDS_PER_MINUTE)

  @Deprecated(
    message = "Use inWholeMinutes instead.",
    replaceWith = ReplaceWith("this.inWholeMinutes"),
    level = DeprecationLevel.ERROR,
  )
  public val inMinutes: Minutes
    get() = deprecatedToError()

  /**
   * Converts this duration to the number of whole hours.
   */
  public val inWholeHours: Hours
    get() = Hours(value / NANOSECONDS_PER_HOUR)

  @Deprecated(
    message = "Use inWholeHours instead.",
    replaceWith = ReplaceWith("this.inWholeHours"),
    level = DeprecationLevel.ERROR,
  )
  public val inHours: Hours
    get() = deprecatedToError()

  /**
   * Converts this duration to the number of whole days.
   */
  public val inWholeDays: Days
    get() = Days(value / NANOSECONDS_PER_DAY)

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
    replaceWith = ReplaceWith("this == 0L.nanoseconds"),
    level = DeprecationLevel.ERROR,
  )
  public fun isZero(): Boolean = value == 0L

  /**
   * Checks if this duration is negative.
   */
  @Deprecated(
    message = "Replace with direct comparison.",
    replaceWith = ReplaceWith("this < 0L.nanoseconds"),
    level = DeprecationLevel.ERROR,
  )
  public fun isNegative(): Boolean = value < 0L

  /**
   * Checks if this duration is positive.
   */
  @Deprecated(
    message = "Replace with direct comparison.",
    replaceWith = ReplaceWith("this > 0L.nanoseconds"),
    level = DeprecationLevel.ERROR,
  )
  public fun isPositive(): Boolean = value > 0L

  override fun compareTo(other: Nanoseconds): Int = value.compareTo(other.value)

  /**
   * Converts this duration to a [kotlin.time.Duration].
   */
  public fun toKotlinDuration(): KotlinDuration = value.kotlinNanoseconds

  /**
   * Converts this duration to an ISO-8601 time interval representation.
   */
  override fun toString(): String {
     return if (value == 0L) {
       "PT0S"
     } else {
       buildString {
         val wholePart = (value / 1000000000).absoluteValue
         val fractionalPart = (value % 1000000000).toInt().absoluteValue
         if (value < 0) { append('-') }
         append("PT")
         append(wholePart)
         if (fractionalPart > 0) {
           append('.')
           append(fractionalPart.toZeroPaddedString(9).dropLastWhile { it == '0' })
         }
         append('S')
       }
     }
  }

  /**
   * Negates this duration.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public operator fun unaryMinus(): Nanoseconds = Nanoseconds(value.negateExact())

  /**
   * Negates this duration without checking for overflow.
   */
  internal fun negateUnchecked(): Nanoseconds = Nanoseconds(-value)

  public operator fun plus(nanoseconds: Nanoseconds): Nanoseconds = Nanoseconds(value plusExact
      nanoseconds.value)

  public operator fun minus(nanoseconds: Nanoseconds): Nanoseconds = Nanoseconds(value minusExact
      nanoseconds.value)

  public operator fun plus(microseconds: Microseconds): Nanoseconds = this +
      microseconds.inNanoseconds

  public operator fun minus(microseconds: Microseconds): Nanoseconds = this -
      microseconds.inNanoseconds

  public operator fun plus(milliseconds: Milliseconds): Nanoseconds = this +
      milliseconds.inNanoseconds

  public operator fun minus(milliseconds: Milliseconds): Nanoseconds = this -
      milliseconds.inNanoseconds

  public operator fun plus(seconds: Seconds): Nanoseconds = this + seconds.inNanoseconds

  public operator fun minus(seconds: Seconds): Nanoseconds = this - seconds.inNanoseconds

  public operator fun plus(minutes: Minutes): Nanoseconds = this + minutes.inNanoseconds

  public operator fun minus(minutes: Minutes): Nanoseconds = this - minutes.inNanoseconds

  public operator fun plus(hours: Hours): Nanoseconds = this + hours.inNanoseconds

  public operator fun minus(hours: Hours): Nanoseconds = this - hours.inNanoseconds

  public operator fun plus(days: Days): Nanoseconds = this + days.inNanoseconds

  public operator fun minus(days: Days): Nanoseconds = this - days.inNanoseconds

  /**
   * Multiplies this duration by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public operator fun times(scalar: Int): Nanoseconds = Nanoseconds(value timesExact scalar)

  /**
   * Returns this duration divided by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs or the scalar is zero
   */
  public operator fun div(scalar: Int): Nanoseconds {
     return if (scalar == -1) {
       -this
     } else {
       Nanoseconds(value / scalar)
     }
  }

  /**
   * Returns the remainder of this duration divided by a scalar value.
   */
  public operator fun rem(scalar: Int): Nanoseconds = Nanoseconds(value % scalar)

  /**
   * Multiplies this duration by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs
   */
  public operator fun times(scalar: Long): Nanoseconds = Nanoseconds(value timesExact scalar)

  /**
   * Returns this duration divided by a scalar value.
   *
   * @throws ArithmeticException if overflow occurs or the scalar is zero
   */
  public operator fun div(scalar: Long): Nanoseconds {
     return if (scalar == -1L) {
       -this
     } else {
       Nanoseconds(value / scalar)
     }
  }

  /**
   * Returns the remainder of this duration divided by a scalar value.
   */
  public operator fun rem(scalar: Long): Nanoseconds = Nanoseconds(value % scalar)

  public inline fun <T> toComponentValues(action: (microseconds: Long, nanoseconds: Int) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val microseconds = (value / NANOSECONDS_PER_MICROSECOND)
    val nanoseconds = (value % NANOSECONDS_PER_MICROSECOND).toInt()
    return action(microseconds, nanoseconds)
  }

  public inline fun <T> toComponents(action: (microseconds: Microseconds,
      nanoseconds: Nanoseconds) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
     return toComponentValues { microseconds, nanoseconds ->
         action(Microseconds(microseconds), Nanoseconds(nanoseconds))
     }
  }

  public inline fun <T> toComponentValues(action: (
    milliseconds: Long,
    microseconds: Int,
    nanoseconds: Int,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val milliseconds = (value / NANOSECONDS_PER_MILLISECOND)
    val microseconds = ((value % NANOSECONDS_PER_MILLISECOND) / NANOSECONDS_PER_MICROSECOND).toInt()
    val nanoseconds = (value % NANOSECONDS_PER_MICROSECOND).toInt()
    return action(milliseconds, microseconds, nanoseconds)
  }

  public inline fun <T> toComponents(action: (
    milliseconds: Milliseconds,
    microseconds: Microseconds,
    nanoseconds: Nanoseconds,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
     return toComponentValues { milliseconds, microseconds, nanoseconds ->
         action(Milliseconds(milliseconds), Microseconds(microseconds), Nanoseconds(nanoseconds))
     }
  }

  public inline fun <T> toComponentValues(action: (
    seconds: Long,
    milliseconds: Int,
    microseconds: Int,
    nanoseconds: Int,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val seconds = (value / NANOSECONDS_PER_SECOND)
    val milliseconds = ((value % NANOSECONDS_PER_SECOND) / NANOSECONDS_PER_MILLISECOND).toInt()
    val microseconds = ((value % NANOSECONDS_PER_MILLISECOND) / NANOSECONDS_PER_MICROSECOND).toInt()
    val nanoseconds = (value % NANOSECONDS_PER_MICROSECOND).toInt()
    return action(seconds, milliseconds, microseconds, nanoseconds)
  }

  public inline fun <T> toComponents(action: (
    seconds: Seconds,
    milliseconds: Milliseconds,
    microseconds: Microseconds,
    nanoseconds: Nanoseconds,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
     return toComponentValues { seconds, milliseconds, microseconds, nanoseconds ->
         action(Seconds(seconds), Milliseconds(milliseconds), Microseconds(microseconds),
        Nanoseconds(nanoseconds))
     }
  }

  public inline fun <T> toComponentValues(action: (
    minutes: Long,
    seconds: Int,
    milliseconds: Int,
    microseconds: Int,
    nanoseconds: Int,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val minutes = (value / NANOSECONDS_PER_MINUTE)
    val seconds = ((value % NANOSECONDS_PER_MINUTE) / NANOSECONDS_PER_SECOND).toInt()
    val milliseconds = ((value % NANOSECONDS_PER_SECOND) / NANOSECONDS_PER_MILLISECOND).toInt()
    val microseconds = ((value % NANOSECONDS_PER_MILLISECOND) / NANOSECONDS_PER_MICROSECOND).toInt()
    val nanoseconds = (value % NANOSECONDS_PER_MICROSECOND).toInt()
    return action(minutes, seconds, milliseconds, microseconds, nanoseconds)
  }

  public inline fun <T> toComponents(action: (
    minutes: Minutes,
    seconds: Seconds,
    milliseconds: Milliseconds,
    microseconds: Microseconds,
    nanoseconds: Nanoseconds,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
     return toComponentValues { minutes, seconds, milliseconds, microseconds, nanoseconds ->
         action(Minutes(minutes), Seconds(seconds), Milliseconds(milliseconds),
        Microseconds(microseconds), Nanoseconds(nanoseconds))
     }
  }

  public inline fun <T> toComponentValues(action: (
    hours: Long,
    minutes: Int,
    seconds: Int,
    milliseconds: Int,
    microseconds: Int,
    nanoseconds: Int,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val hours = (value / NANOSECONDS_PER_HOUR)
    val minutes = ((value % NANOSECONDS_PER_HOUR) / NANOSECONDS_PER_MINUTE).toInt()
    val seconds = ((value % NANOSECONDS_PER_MINUTE) / NANOSECONDS_PER_SECOND).toInt()
    val milliseconds = ((value % NANOSECONDS_PER_SECOND) / NANOSECONDS_PER_MILLISECOND).toInt()
    val microseconds = ((value % NANOSECONDS_PER_MILLISECOND) / NANOSECONDS_PER_MICROSECOND).toInt()
    val nanoseconds = (value % NANOSECONDS_PER_MICROSECOND).toInt()
    return action(hours, minutes, seconds, milliseconds, microseconds, nanoseconds)
  }

  public inline fun <T> toComponents(action: (
    hours: Hours,
    minutes: Minutes,
    seconds: Seconds,
    milliseconds: Milliseconds,
    microseconds: Microseconds,
    nanoseconds: Nanoseconds,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
     return toComponentValues { hours, minutes, seconds, milliseconds, microseconds, nanoseconds ->
         action(Hours(hours), Minutes(minutes), Seconds(seconds), Milliseconds(milliseconds),
        Microseconds(microseconds), Nanoseconds(nanoseconds))
     }
  }

  public inline fun <T> toComponentValues(action: (
    days: Long,
    hours: Int,
    minutes: Int,
    seconds: Int,
    milliseconds: Int,
    microseconds: Int,
    nanoseconds: Int,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val days = (value / NANOSECONDS_PER_DAY)
    val hours = ((value % NANOSECONDS_PER_DAY) / NANOSECONDS_PER_HOUR).toInt()
    val minutes = ((value % NANOSECONDS_PER_HOUR) / NANOSECONDS_PER_MINUTE).toInt()
    val seconds = ((value % NANOSECONDS_PER_MINUTE) / NANOSECONDS_PER_SECOND).toInt()
    val milliseconds = ((value % NANOSECONDS_PER_SECOND) / NANOSECONDS_PER_MILLISECOND).toInt()
    val microseconds = ((value % NANOSECONDS_PER_MILLISECOND) / NANOSECONDS_PER_MICROSECOND).toInt()
    val nanoseconds = (value % NANOSECONDS_PER_MICROSECOND).toInt()
    return action(days, hours, minutes, seconds, milliseconds, microseconds, nanoseconds)
  }

  public inline fun <T> toComponents(action: (
    days: Days,
    hours: Hours,
    minutes: Minutes,
    seconds: Seconds,
    milliseconds: Milliseconds,
    microseconds: Microseconds,
    nanoseconds: Nanoseconds,
  ) -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
     return toComponentValues { days, hours, minutes, seconds, milliseconds, microseconds,
        nanoseconds ->
         action(Days(days), Hours(hours), Minutes(minutes), Seconds(seconds),
        Milliseconds(milliseconds), Microseconds(microseconds), Nanoseconds(nanoseconds))
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
   * Converts this duration to [IntNanoseconds].
   *
   * @throws ArithmeticException if overflow occurs
   */
  @Deprecated(
    message = "The 'Int' class no longer exists.",
    replaceWith = ReplaceWith("this"),
    level = DeprecationLevel.ERROR,
  )
  public fun toIntNanoseconds(): Nanoseconds = this

  /**
   * Converts this duration to [IntNanoseconds] without checking for overflow.
   */
  @Deprecated(
    message = "The 'Int' class no longer exists.",
    replaceWith = ReplaceWith("this"),
    level = DeprecationLevel.ERROR,
  )
  @PublishedApi
  internal fun toIntNanosecondsUnchecked(): Nanoseconds = this

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
    public val MIN: Nanoseconds = Nanoseconds(Long.MIN_VALUE)

    /**
     * The largest supported value.
     */
    public val MAX: Nanoseconds = Nanoseconds(Long.MAX_VALUE)
  }
}

/**
 * Converts this value to a duration of nanoseconds.
 */
public val Int.nanoseconds: Nanoseconds
  get() = Nanoseconds(this)

/**
 * Multiplies this value by a duration of nanoseconds.
 *
 * @throws ArithmeticException if overflow occurs
 */
public operator fun Int.times(nanoseconds: Nanoseconds): Nanoseconds = nanoseconds * this

/**
 * Converts this value to a duration of nanoseconds.
 */
public val Long.nanoseconds: Nanoseconds
  get() = Nanoseconds(this)

/**
 * Multiplies this value by a duration of nanoseconds.
 *
 * @throws ArithmeticException if overflow occurs
 */
public operator fun Long.times(nanoseconds: Nanoseconds): Nanoseconds = nanoseconds * this

/**
 * Converts this duration to Island Time [Nanoseconds].
 */
public fun KotlinDuration.toIslandNanoseconds(): Nanoseconds =
    Nanoseconds(this.toLong(KotlinDurationUnit.NANOSECONDS))
