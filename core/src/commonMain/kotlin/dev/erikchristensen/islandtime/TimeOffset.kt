package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.internal.SECONDS_PER_HOUR
import dev.erikchristensen.islandtime.internal.SECONDS_PER_MINUTE
import dev.erikchristensen.islandtime.internal.appendZeroPadded
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.*

inline class TimeOffset(val totalSeconds: IntSeconds) : Comparable<TimeOffset> {
    val isValid: Boolean get() = totalSeconds.value in MIN_VALUE..MAX_VALUE

    override fun compareTo(other: TimeOffset) = totalSeconds.compareTo(other.totalSeconds)

    override fun toString(): String {
        return if (isUtc) {
            "Z"
        } else {
            buildString(MAX_TIME_OFFSET_STRING_LENGTH) { appendTimeOffset(this@TimeOffset) }
        }
    }

    companion object {
        const val MAX_VALUE = 18 * SECONDS_PER_HOUR.toInt()
        const val MIN_VALUE = -18 * SECONDS_PER_HOUR.toInt()

        val MIN = TimeOffset(MAX_VALUE.seconds)
        val MAX = TimeOffset(MIN_VALUE.seconds)
        val UTC = TimeOffset(0.seconds)
    }
}

inline val TimeOffset.isUtc get() = this == TimeOffset.UTC

fun validateTimeOffset(hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) {
    if ((hours.isPositive && (minutes.isNegative || seconds.isNegative)) ||
        (minutes.isPositive && (hours.isNegative || seconds.isNegative)) ||
        (seconds.isPositive && (hours.isNegative || minutes.isNegative)) ||
        (hours.isNegative && (minutes.isPositive || seconds.isPositive)) ||
        (minutes.isNegative && (hours.isPositive || seconds.isPositive)) ||
        (seconds.isNegative && (hours.isPositive || minutes.isPositive))
    ) {
        throw DateTimeException("All time offset components must have the same sign")
    }

    if (hours.value !in -18..18) {
        throw DateTimeException("Hours must be within +/18, got '${hours.value}'")
    }

    if (minutes.value !in -59..59) {
        throw DateTimeException("Minutes must be within +/59, got '${minutes.value}'")
    }

    if (seconds.value !in -59..59) {
        throw DateTimeException("Seconds must be within +/59, got '${seconds.value}'")
    }
}

fun timeOffsetOf(
    hours: IntHours,
    minutes: IntMinutes = 0.minutes,
    seconds: IntSeconds = 0.seconds
): TimeOffset {
    validateTimeOffset(hours, minutes, seconds)
    val totalSeconds = hours.asSeconds() + minutes.asSeconds() + seconds

    if (totalSeconds.value !in TimeOffset.MIN_VALUE..TimeOffset.MAX_VALUE) {
        throw DateTimeException("Time offset must be within +/-18:00")
    }

    return TimeOffset(totalSeconds)
}

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

fun <T> TimeOffset.toComponents(
    action: (hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) -> T
): T {
    val hours = (totalSeconds.value / SECONDS_PER_HOUR.toInt()).hours
    val minutes = ((totalSeconds.value % SECONDS_PER_HOUR.toInt()) / SECONDS_PER_MINUTE.toInt()).minutes
    val seconds = totalSeconds % SECONDS_PER_MINUTE.toInt()

    return action(hours, minutes, seconds)
}

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