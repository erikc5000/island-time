@file:Suppress("FunctionName")

package io.islandtime

import dev.erikchristensen.javamath2kmp.toIntExact
import io.islandtime.base.DateTimeField
import io.islandtime.internal.SECONDS_PER_HOUR
import io.islandtime.internal.SECONDS_PER_MINUTE
import io.islandtime.internal.appendZeroPadded
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.serialization.UtcOffsetSerializer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlinx.serialization.Serializable

/**
 * The time shift between a local time and UTC.
 *
 * @constructor Creates an offset from the number of seconds relative to UTC.
 * @param totalSecondsValue the total number of seconds to offset by
 * @throws DateTimeException if the offset is outside the supported range
 * @property totalSecondsValue The number of seconds relative to UTC.
 */
@Serializable(with = UtcOffsetSerializer::class)
@JvmInline
value class UtcOffset @PublishedApi internal constructor(val totalSecondsValue: Int) : Comparable<UtcOffset> {
    /**
     * The number of seconds relative to UTC.
     */
    val totalSeconds: Seconds get() = Seconds(totalSecondsValue)

    @PublishedApi
    internal val hoursComponent: Int
        get() = totalSecondsValue / SECONDS_PER_HOUR

    @PublishedApi
    internal val minutesComponent: Int
        get() = (totalSecondsValue % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE

    @PublishedApi
    internal val secondsComponent: Int
        get() = totalSecondsValue % SECONDS_PER_MINUTE

    /**
     * Creates an offset from the number of seconds relative to UTC.
     * @throws DateTimeException if the offset is outside the supported range
     */
    constructor(totalSeconds: Seconds) : this(checkValidOffset(totalSeconds.value))

    /**
     * Checks if this is the UTC offset of +00:00.
     */
    fun isZero(): Boolean = this == ZERO

    /**
     * Breaks a UTC offset down into components. The sign will indicate whether the offset is positive or negative while
     * each component will be positive.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun <T> toComponents(
        action: (sign: Int, hours: Hours, minutes: Minutes, seconds: Seconds) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }

        return Seconds(totalSecondsValue.absoluteValue).toComponents { hours, minutes, seconds ->
            action(totalSeconds.value.sign, hours, minutes, seconds)
        }
    }

    /**
     * Breaks a UTC offset down into components. If the offset is negative, each component will be negative.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun <T> toComponents(
        action: (hours: Hours, minutes: Minutes, seconds: Seconds) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return totalSeconds.toComponents(action)
    }

    /**
     * Breaks a UTC offset down into components. The sign will indicate whether the offset is positive or negative while
     * each component will be positive.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun <T> toComponentValues(
        action: (sign: Int, hours: Int, minutes: Int, seconds: Int) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }

        return UtcOffset(totalSecondsValue.absoluteValue).toComponentValues { hours, minutes, seconds ->
            action(totalSecondsValue.sign, hours, minutes, seconds)
        }
    }

    /**
     * Breaks a UTC offset down into components. If the offset is negative, each component will be negative.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun <T> toComponentValues(
        action: (hours: Int, minutes: Int, seconds: Int) -> T
    ): T {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return action(hoursComponent, minutesComponent, secondsComponent)
    }

    override fun compareTo(other: UtcOffset): Int = totalSecondsValue - other.totalSecondsValue

    /**
     * Converts this offset to a string in ISO-8601 extended format. For example, `-04:00` or `Z`.
     */
    override fun toString(): String {
        return if (isZero()) {
            "Z"
        } else {
            buildString(MAX_UTC_OFFSET_STRING_LENGTH) { appendUtcOffset(this@UtcOffset) }
        }
    }

    companion object {
        val MAX_TOTAL_SECONDS_VALUE: Int = 18.hours.inSecondsUnchecked.toInt()
        val MIN_TOTAL_SECONDS_VALUE: Int = (-18).hours.inSecondsUnchecked.toInt()

        val MAX_TOTAL_SECONDS: Seconds get() = MAX_TOTAL_SECONDS_VALUE.seconds
        val MIN_TOTAL_SECONDS: Seconds get() = MIN_TOTAL_SECONDS_VALUE.seconds

        val MIN: UtcOffset = UtcOffset(MIN_TOTAL_SECONDS)
        val MAX: UtcOffset = UtcOffset(MAX_TOTAL_SECONDS)
        val ZERO: UtcOffset = UtcOffset(totalSecondsValue = 0)

        fun fromTotalSeconds(value: Int): UtcOffset = UtcOffset(checkValidOffset(value))
    }
}

/**
 * Creates a UTC offset of hours, minutes, and seconds. Each component must be within its valid range and without any
 * mixed positive and negative values.
 * @param hours hours to offset by, within +/-18
 * @param minutes minutes to offset by, within +/-59
 * @param seconds seconds to offset by, within +/-59
 * @throws DateTimeException if the offset or any of its components are invalid
 * @return a [UtcOffset]
 */
fun UtcOffset(
    hours: Hours,
    minutes: Minutes = 0.minutes,
    seconds: Seconds = 0.seconds
): UtcOffset {
    validateUtcOffsetComponents(hours.value, minutes.value, seconds.value)
    return UtcOffset(totalSeconds = hours + minutes + seconds)
}

/**
 * Converts a duration of hours into a [UtcOffset] of the same length.
 * @throws ArithmeticException if overflow occurs
 */
fun Hours.asUtcOffset(): UtcOffset = UtcOffset(this.inSeconds)

/**
 * Converts a duration of minutes into a [UtcOffset] of the same length.
 * @throws ArithmeticException if overflow occurs
 */
fun Minutes.asUtcOffset(): UtcOffset = UtcOffset(this.inSeconds)

/**
 * Converts a duration of seconds into a [UtcOffset] of the same length.
 * @throws ArithmeticException if overflow occurs
 */
fun Seconds.asUtcOffset(): UtcOffset = UtcOffset(totalSeconds = this)

/**
 * Converts a string to a [UtcOffset].
 *
 * The string is assumed to be an ISO-8601 UTC offset representation in extended format. For example, `Z`, `+05`, or
 * `-04:30`. The output of [UtcOffset.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed UTC offset is invalid
 */
fun String.toUtcOffset(): UtcOffset = toUtcOffset(DateTimeParsers.Iso.Extended.UTC_OFFSET)

/**
 * Converts a string to a [UtcOffset] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed UTC offset is invalid
 */
fun String.toUtcOffset(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): UtcOffset {
    val result = parser.parse(this, settings)
    return result.toUtcOffset() ?: throwParserFieldResolutionException<UtcOffset>(this)
}

/**
 * Resolves a parser result into a [UtcOffset].
 *
 * Required fields are [DateTimeField.UTC_OFFSET_TOTAL_SECONDS] or [DateTimeField.UTC_OFFSET_SIGN] in conjunction with
 * any combination of [DateTimeField.UTC_OFFSET_HOURS], [DateTimeField.UTC_OFFSET_MINUTES], and
 * [DateTimeField.UTC_OFFSET_SECONDS].
 */
internal fun DateTimeParseResult.toUtcOffset(): UtcOffset? {
    val totalSeconds = fields[DateTimeField.UTC_OFFSET_TOTAL_SECONDS]

    if (totalSeconds != null) {
        return UtcOffset(totalSeconds.toIntExact().seconds)
    }

    val sign = fields[DateTimeField.UTC_OFFSET_SIGN]

    if (sign != null) {
        val hours = (fields[DateTimeField.UTC_OFFSET_HOURS] ?: 0L).hours
        val minutes = (fields[DateTimeField.UTC_OFFSET_MINUTES] ?: 0L).minutes
        val seconds = (fields[DateTimeField.UTC_OFFSET_SECONDS] ?: 0L).seconds

        return if (sign < 0L) {
            UtcOffset(-hours, -minutes, -seconds)
        } else {
            UtcOffset(hours, minutes, seconds)
        }
    }

    return null
}

internal const val MAX_UTC_OFFSET_STRING_LENGTH = 9

internal fun StringBuilder.appendUtcOffset(offset: UtcOffset): StringBuilder {
    if (offset.isZero()) {
        append('Z')
    } else {
        offset.toComponentValues { sign, hours, minutes, seconds ->
            append(if (sign < 0) '-' else '+')
            appendZeroPadded(hours, 2)
            append(':')
            appendZeroPadded(minutes, 2)

            if (seconds != 0) {
                append(':')
                appendZeroPadded(seconds, 2)
            }
        }
    }
    return this
}

private fun validateUtcOffsetComponents(hours: Long, minutes: Long, seconds: Long) {
    when {
        hours > 0 -> if (minutes < 0 || seconds < 0) {
            throw DateTimeException("Time offset minutes and seconds must be positive when hours are positive")
        }
        hours < 0 -> if (minutes > 0 || seconds > 0) {
            throw DateTimeException("Time offset minutes and seconds must be negative when hours are negative")
        }
        else -> if ((minutes < 0 && seconds > 0) || (minutes > 0 && seconds < 0)) {
            throw DateTimeException("Time offset minutes and seconds must have the same sign")
        }
    }

    if (hours !in -18..18) {
        throw DateTimeException("Time offset hours must be within +/-18, got '$hours'")
    }
    if (minutes !in -59..59) {
        throw DateTimeException("Time offset minutes must be within +/-59, got '$minutes'")
    }
    if (seconds !in -59..59) {
        throw DateTimeException("Time offset seconds must be within +/-59, got '$seconds'")
    }
}

private fun checkValidOffset(totalSeconds: Int): Int {
    if (totalSeconds !in UtcOffset.MIN_TOTAL_SECONDS_VALUE..UtcOffset.MAX_TOTAL_SECONDS_VALUE) {
        throwInvalidTotalSecondsException(totalSeconds.toLong())
    }
    return totalSeconds
}

private fun checkValidOffset(totalSeconds: Long): Int {
    if (totalSeconds !in UtcOffset.MIN_TOTAL_SECONDS_VALUE..UtcOffset.MAX_TOTAL_SECONDS_VALUE) {
        throwInvalidTotalSecondsException(totalSeconds)
    }
    return totalSeconds.toInt()
}

private fun throwInvalidTotalSecondsException(totalSeconds: Long): Nothing {
    throw DateTimeException("'$totalSeconds seconds' is outside the valid offset range of +/-18:00")
}
