package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.internal.SECONDS_PER_HOUR
import dev.erikchristensen.islandtime.internal.SECONDS_PER_MINUTE
import dev.erikchristensen.islandtime.internal.appendZeroPadded
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeField
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import kotlin.math.abs

inline class TimeOffset(val totalSeconds: SecondSpan) : Comparable<TimeOffset> {
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
        const val MAX_VALUE = 18 * SECONDS_PER_HOUR
        const val MIN_VALUE = -18 * SECONDS_PER_HOUR

        val MIN = TimeOffset(MAX_VALUE.seconds)
        val MAX = TimeOffset(MIN_VALUE.seconds)
        val UTC = TimeOffset(0.seconds)
    }
}

inline val TimeOffset.isUtc get() = this == TimeOffset.UTC

fun validateTimeOffset(hours: HourSpan, minutes: MinuteSpan, seconds: SecondSpan) {
    // TODO: Throw exceptions if values are invalid
//    val absHours = abs(hours.value)
//
//    if (hours.value !in TimeOffset.MIN_VALUE..TimeOffset.MAX_VALUE) {
//        throw DateTimeException("Hours")
//    }
}

fun timeOffsetOf(
    hours: HourSpan,
    minutes: MinuteSpan = 0.minutes,
    seconds: SecondSpan = 0.seconds
): TimeOffset {
    validateTimeOffset(hours, minutes, seconds)
    val totalSeconds = hours.asSeconds() + minutes.asSeconds() + seconds

    if (totalSeconds.value !in TimeOffset.MIN_VALUE..TimeOffset.MAX_VALUE) {
        throw DateTimeException("Time offset must be within +/-18:00")
    }

    return TimeOffset(totalSeconds)
}

fun String.toTimeOffset() = toTimeOffset(Iso8601.Extended.TIME_OFFSET_PARSER)

fun String.toTimeOffset(parser: DateTimeParser): TimeOffset {
    val result = parser.parse(this)
    return result.toTimeOffset()
}

internal fun DateTimeParseResult.toTimeOffset(): TimeOffset {
    val isUtc = this[DateTimeField.TIME_OFFSET_UTC] != null

    return if (isUtc) {
        TimeOffset.UTC
    } else {
        val sign = this[DateTimeField.TIME_OFFSET_SIGN] ?: throw DateTimeException("Missing sign of time offset")
        val hours = (this[DateTimeField.TIME_OFFSET_HOURS]?.toInt() ?: 0).hours
        val minutes = (this[DateTimeField.TIME_OFFSET_MINUTES]?.toInt() ?: 0).minutes
        val seconds = (this[DateTimeField.TIME_OFFSET_SECONDS]?.toInt() ?: 0).seconds

        if (sign < 0L) {
            timeOffsetOf(-hours, -minutes, -seconds)
        } else {
            timeOffsetOf(hours, minutes, seconds)
        }
    }
}

internal const val MAX_TIME_OFFSET_STRING_LENGTH = 9

internal fun StringBuilder.appendTimeOffset(timeOffset: TimeOffset): StringBuilder {
    with(timeOffset) {
        val absTotalSeconds = abs(totalSeconds.value)
        val hours = absTotalSeconds / SECONDS_PER_HOUR
        val minutes = (absTotalSeconds / SECONDS_PER_MINUTE) % SECONDS_PER_HOUR
        val seconds = absTotalSeconds % SECONDS_PER_MINUTE

        append(if (totalSeconds.value < 0L) '-' else '+')
        appendZeroPadded(hours, 2)
        append(':')
        appendZeroPadded(minutes, 2)

        if (seconds != 0) {
            append(':')
            appendZeroPadded(seconds, 2)
        }
    }
    return this
}