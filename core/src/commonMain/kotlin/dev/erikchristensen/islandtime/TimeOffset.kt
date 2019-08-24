package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.internal.SECONDS_PER_HOUR
import dev.erikchristensen.islandtime.internal.SECONDS_PER_MINUTE
import dev.erikchristensen.islandtime.internal.appendZeroPadded
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.*

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class TimeOffset internal constructor(
    val totalSeconds: IntSeconds
) : Comparable<TimeOffset> {

    val isValid: Boolean get() = totalSeconds in MIN_TOTAL_SECONDS..MAX_TOTAL_SECONDS

    override fun compareTo(other: TimeOffset) = totalSeconds.compareTo(other.totalSeconds)

    override fun toString(): String {
        return if (isUtc) {
            "Z"
        } else {
            buildString(MAX_TIME_OFFSET_STRING_LENGTH) { appendTimeOffset(this@TimeOffset) }
        }
    }

    companion object {
        val MAX_TOTAL_SECONDS = (18 * SECONDS_PER_HOUR.toInt()).seconds
        val MIN_TOTAL_SECONDS = (-18 * SECONDS_PER_HOUR.toInt()).seconds

        val MIN = TimeOffset(MAX_TOTAL_SECONDS)
        val MAX = TimeOffset(MIN_TOTAL_SECONDS)
        val UTC = TimeOffset(0.seconds)
    }
}

inline val TimeOffset.isUtc get() = this == TimeOffset.UTC

fun timeOffsetOf(
    hours: IntHours,
    minutes: IntMinutes = 0.minutes,
    seconds: IntSeconds = 0.seconds
): TimeOffset {
    validateTimeOffsetComponents(hours, minutes, seconds)
    return TimeOffset(hours.asSeconds() + minutes.asSeconds() + seconds).validated()
}

/**
 * Break a time offset down into components.  The sign will indicate whether the offset is positive or negative while
 * each component will be positive.
 */
fun <T> TimeOffset.toComponents(
    action: (sign: Int, hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) -> T
): T {
    val sign = if (totalSeconds.isNegative) -1 else 1
    val absTotalSeconds = totalSeconds.absoluteValue
    val hours = (absTotalSeconds.value / SECONDS_PER_HOUR.toInt()).hours
    val minutes = ((absTotalSeconds.value % SECONDS_PER_HOUR.toInt()) / SECONDS_PER_MINUTE.toInt()).minutes
    val seconds = absTotalSeconds % SECONDS_PER_MINUTE.toInt()

    return action(sign, hours, minutes, seconds)
}

/**
 * Break a time offset down into components.  If the offset is negative, each component will be negative.
 */
fun <T> TimeOffset.toComponents(
    action: (hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) -> T
): T {
    val hours = (totalSeconds.value / SECONDS_PER_HOUR.toInt()).hours
    val minutes = ((totalSeconds.value % SECONDS_PER_HOUR.toInt()) / SECONDS_PER_MINUTE.toInt()).minutes
    val seconds = totalSeconds % SECONDS_PER_MINUTE.toInt()

    return action(hours, minutes, seconds)
}

fun IntHours.asTimeOffset() = TimeOffset(this.asSeconds()).validated()
fun IntMinutes.asTimeOffset() = TimeOffset(this.asSeconds()).validated()
fun IntSeconds.asTimeOffset() = TimeOffset(this).validated()

fun String.toTimeOffset() = toTimeOffset(Iso8601.Extended.TIME_OFFSET_PARSER)

fun String.toTimeOffset(parser: DateTimeParser): TimeOffset {
    val result = parser.parse(this)
    return result.toTimeOffset() ?: raiseParserFieldResolutionException("TimeOffset", this)
}

/**
 * Resolve a parser result into a [TimeOffset]
 *
 * Required fields are TIME_OFFSET_UTC or TIME_OFFSET_SIGN in conjunction with any combination of TIME_OFFSET_HOURS,
 * TIME_OFFSET_MINUTES, and TIME_OFFSET_SECONDS.
 */
internal fun DateTimeParseResult.toTimeOffset(): TimeOffset? {
    val isUtc = this[DateTimeField.TIME_OFFSET_UTC] != null

    if (isUtc) {
        return TimeOffset.UTC
    }

    val sign = this[DateTimeField.TIME_OFFSET_SIGN]

    if (sign != null) {
        val hours = (this[DateTimeField.TIME_OFFSET_HOURS]?.toInt() ?: 0).hours
        val minutes = (this[DateTimeField.TIME_OFFSET_MINUTES]?.toInt() ?: 0).minutes
        val seconds = (this[DateTimeField.TIME_OFFSET_SECONDS]?.toInt() ?: 0).seconds

        return if (sign < 0L) {
            timeOffsetOf(-hours, -minutes, -seconds)
        } else {
            timeOffsetOf(hours, minutes, seconds)
        }
    }

    return null
}

internal const val MAX_TIME_OFFSET_STRING_LENGTH = 9

internal fun StringBuilder.appendTimeOffset(timeOffset: TimeOffset): StringBuilder {
    timeOffset.toComponents { sign, hours, minutes, seconds ->
        append(if (sign < 0) '-' else '+')
        appendZeroPadded(hours.value, 2)
        append(':')
        appendZeroPadded(minutes.value, 2)

        if (seconds.value != 0) {
            append(':')
            appendZeroPadded(seconds.value, 2)
        }
    }
    return this
}

internal fun TimeOffset.validated(): TimeOffset {
    if (!this.isValid) {
        throw DateTimeException("'$this' is outside the valid offset range of +/-18:00")
    }
    return this
}

private fun validateTimeOffsetComponents(hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) {
    if (hours.isPositive) {
        if (minutes.isNegative || seconds.isNegative) {
            throw DateTimeException("Time offset minutes and seconds must be positive when hours are positive")
        }
    } else if (hours.isNegative) {
        if (minutes.isPositive || seconds.isPositive) {
            throw DateTimeException("Time offset minutes and seconds must be negative when hours are negative")
        }
    } else if (minutes.isPositive && seconds.isNegative || minutes.isNegative && seconds.isPositive) {
        throw DateTimeException("Time offset minutes and seconds must have the same sign")
    }

    if (hours.value !in -18..18) {
        throw DateTimeException("Time offset hours must be within +/-18, got '${hours.value}'")
    }

    if (minutes.value !in -59..59) {
        throw DateTimeException("Time offset minutes must be within +/-59, got '${minutes.value}'")
    }

    if (seconds.value !in -59..59) {
        throw DateTimeException("Time offset seconds must be within +/-59, got '${seconds.value}'")
    }
}