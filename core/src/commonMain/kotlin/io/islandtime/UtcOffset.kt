package io.islandtime

import io.islandtime.base.DateTimeField
import io.islandtime.internal.SECONDS_PER_HOUR
import io.islandtime.internal.SECONDS_PER_MINUTE
import io.islandtime.internal.appendZeroPadded
import io.islandtime.internal.toIntExact
import io.islandtime.measures.*
import io.islandtime.parser.*

/**
 * The time shift between a local time and UTC.
 *
 * To ensure that the offset is within the valid supported range, you must explicitly call [validate] or [validated].
 *
 * @param totalSeconds the total number of seconds to offset by
 * @see validate
 * @see validated
 */
inline class UtcOffset(val totalSeconds: IntSeconds) : Comparable<UtcOffset> {

    /**
     * Check if this offset is within the supported range.
     */
    val isValid: Boolean get() = totalSeconds in MIN_TOTAL_SECONDS..MAX_TOTAL_SECONDS

    /**
     * Is this the UTC offset of +00:00?
     */
    fun isZero(): Boolean = this == ZERO

    /**
     * Break a UTC offset down into components. The sign will indicate whether the offset is positive or negative while
     * each component will be positive.
     */
    inline fun <T> toComponents(
        action: (sign: Int, hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) -> T
    ): T {
        val sign = if (totalSeconds.isNegative()) -1 else 1
        val absTotalSeconds = totalSeconds.absoluteValue
        val hours = (absTotalSeconds.value / SECONDS_PER_HOUR).hours
        val minutes = ((absTotalSeconds.value % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE).minutes
        val seconds = absTotalSeconds % SECONDS_PER_MINUTE

        return action(sign, hours, minutes, seconds)
    }

    /**
     * Break a UTC offset down into components. If the offset is negative, each component will be negative.
     */
    inline fun <T> toComponents(
        action: (hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) -> T
    ): T {
        val hours = (totalSeconds.value / SECONDS_PER_HOUR).hours
        val minutes = ((totalSeconds.value % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE).minutes
        val seconds = totalSeconds % SECONDS_PER_MINUTE

        return action(hours, minutes, seconds)
    }


    override fun compareTo(other: UtcOffset) = totalSeconds.compareTo(other.totalSeconds)

    override fun toString(): String {
        return if (isZero()) {
            "Z"
        } else {
            buildString(MAX_UTC_OFFSET_STRING_LENGTH) { appendUtcOffset(this@UtcOffset) }
        }
    }

    /**
     * Check if the offset is valid and throw an exception if it isn't.
     * @throws DateTimeException if the offset is outside the supported range
     * @see isValid
     */
    fun validate() {
        if (!isValid) {
            throw DateTimeException("'$totalSeconds' is outside the valid offset range of +/-18:00")
        }
    }

    /**
     * Ensure that the offset is valid, throwing an exception if it isn't.
     * @throws DateTimeException if the offset is outside the supported range
     * @see isValid
     */
    fun validated(): UtcOffset = apply { validate() }

    companion object {
        val MAX_TOTAL_SECONDS = (18 * SECONDS_PER_HOUR).seconds
        val MIN_TOTAL_SECONDS = (-18 * SECONDS_PER_HOUR).seconds

        val MIN = UtcOffset(MIN_TOTAL_SECONDS)
        val MAX = UtcOffset(MAX_TOTAL_SECONDS)
        val ZERO = UtcOffset(0.seconds)
    }
}

/**
 * Create a UTC offset of hours, minutes, and seconds. Each component must be within its valid range and without any
 * mixed positive and negative values.
 * @param hours hours to offset by, within +/-18
 * @param minutes minutes to offset by, within +/-59
 * @param seconds seconds to offset by, within +/-59
 * @throws DateTimeException if any of the individual components is outside the valid range
 * @return a [UtcOffset]
 */
@Suppress("FunctionName")
fun UtcOffset(
    hours: IntHours,
    minutes: IntMinutes = 0.minutes,
    seconds: IntSeconds = 0.seconds
): UtcOffset {
    validateUtcOffsetComponents(hours, minutes, seconds)
    return UtcOffset(hours + minutes + seconds)
}

/**
 * Convert a duration of hours into a UTC time offset of the same length.
 * @throws ArithmeticException if overflow occurs
 */
fun IntHours.asUtcOffset() = UtcOffset(this.inSeconds)

/**
 * Convert a duration of minutes into a UTC time offset of the same length.
 * @throws ArithmeticException if overflow occurs
 */
fun IntMinutes.asUtcOffset() = UtcOffset(this.inSeconds)

/**
 * Convert a duration of seconds into a UTC time offset of the same length.
 */
fun IntSeconds.asUtcOffset() = UtcOffset(this)

/**
 * Convert a string to a [UtcOffset].
 *
 * The string is assumed to be an ISO-8601 UTC offset representation in extended format. For example, `Z`, `+05`, or
 * `-04:30`. The output of [UtcOffset.toString] can be safely parsed using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed UTC offset is invalid
 */
fun String.toUtcOffset() = toUtcOffset(DateTimeParsers.Iso.Extended.UTC_OFFSET)

/**
 * Convert a string to a [UtcOffset] using a specific parser.
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
 * Resolve a parser result into a [UtcOffset].
 *
 * Required fields are [DateTimeField.UTC_OFFSET_TOTAL_SECONDS] or [DateTimeField.UTC_OFFSET_SIGN] in conjunction with
 * any combination of [DateTimeField.UTC_OFFSET_HOURS], [DateTimeField.UTC_OFFSET_MINUTES], and
 * [DateTimeField.UTC_OFFSET_SECONDS].
 */
internal fun DateTimeParseResult.toUtcOffset(): UtcOffset? {
    val totalSeconds = fields[DateTimeField.UTC_OFFSET_TOTAL_SECONDS]

    if (totalSeconds != null) {
        return UtcOffset(totalSeconds.toIntExact().seconds).validated()
    }

    val sign = fields[DateTimeField.UTC_OFFSET_SIGN]

    if (sign != null) {
        val hours = (fields[DateTimeField.UTC_OFFSET_HOURS]?.toIntExact() ?: 0).hours
        val minutes = (fields[DateTimeField.UTC_OFFSET_MINUTES]?.toIntExact() ?: 0).minutes
        val seconds = (fields[DateTimeField.UTC_OFFSET_SECONDS]?.toIntExact() ?: 0).seconds

        return if (sign < 0L) {
            UtcOffset(-hours, -minutes, -seconds).validated()
        } else {
            UtcOffset(hours, minutes, seconds).validated()
        }
    }

    return null
}

internal const val MAX_UTC_OFFSET_STRING_LENGTH = 9

internal fun StringBuilder.appendUtcOffset(offset: UtcOffset): StringBuilder {
    if (offset.isZero()) {
        append('Z')
    } else {
        offset.toComponents { sign, hours, minutes, seconds ->
            append(if (sign < 0) '-' else '+')
            appendZeroPadded(hours.value, 2)
            append(':')
            appendZeroPadded(minutes.value, 2)

            if (seconds.value != 0) {
                append(':')
                appendZeroPadded(seconds.value, 2)
            }
        }
    }
    return this
}

private fun validateUtcOffsetComponents(hours: IntHours, minutes: IntMinutes, seconds: IntSeconds) {
    when {
        hours.isPositive() -> if (minutes.isNegative() || seconds.isNegative()) {
            throw DateTimeException("Time offset minutes and seconds must be positive when hours are positive")
        }
        hours.isNegative() -> if (minutes.isPositive() || seconds.isPositive()) {
            throw DateTimeException("Time offset minutes and seconds must be negative when hours are negative")
        }
        else -> if ((minutes.isNegative() && seconds.isPositive()) || (minutes.isPositive() && seconds.isNegative())) {
            throw DateTimeException("Time offset minutes and seconds must have the same sign")
        }
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