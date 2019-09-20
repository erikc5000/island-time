package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.*

/**
 * An instant in time with nanosecond precision
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class Instant internal constructor(
    val durationSinceUnixEpoch: Duration
) : Comparable<Instant> {

    /**
     * The number of seconds since the Unix epoch of 1970-01-01T00:00Z
     */
    inline val secondsSinceUnixEpoch: LongSeconds
        get() = durationSinceUnixEpoch.seconds

    /**
     * The number of additional nanoseconds on top of the seconds since the Unix epoch
     */
    inline val nanosecondAdjustment: IntNanoseconds
        get() = durationSinceUnixEpoch.nanosecondAdjustment

    /**
     * The number of milliseconds since the Unix epoch of 1970-01-01T00:00Z
     */
    val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = secondsSinceUnixEpoch.inMillisecondsExact() plusExact nanosecondAdjustment.inWholeMilliseconds.toLong()

    /**
     * The second of the Unix epoch
     */
    inline val unixEpochSecond: Long
        get() = secondsSinceUnixEpoch.value

    /**
     * The nanosecond of the second of the Unix epoch
     */
    inline val unixEpochNanoOfSecond: Int
        get() = nanosecondAdjustment.value

    /**
     * The millisecond of the Unix epoch
     */
    inline val unixEpochMillisecond: Long
        get() = millisecondsSinceUnixEpoch.value

    operator fun plus(days: IntDays) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + days)

    operator fun plus(days: LongDays) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + days)

    operator fun plus(hours: IntHours) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + hours)

    operator fun plus(hours: LongHours) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + hours)

    operator fun plus(minutes: IntMinutes) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + minutes)

    operator fun plus(minutes: LongMinutes) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + minutes)

    operator fun plus(seconds: IntSeconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + seconds)

    operator fun plus(seconds: LongSeconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + seconds)

    operator fun plus(milliseconds: IntMilliseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + milliseconds)

    operator fun plus(milliseconds: LongMilliseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + milliseconds)

    operator fun plus(microseconds: IntMicroseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + microseconds)

    operator fun plus(microseconds: LongMicroseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + microseconds)

    operator fun plus(nanoseconds: IntNanoseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + nanoseconds)

    operator fun plus(nanoseconds: LongNanoseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch + nanoseconds)

    operator fun minus(days: IntDays) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - days)

    operator fun minus(days: LongDays) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - days)

    operator fun minus(hours: IntHours) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - hours)

    operator fun minus(hours: LongHours) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - hours)

    operator fun minus(minutes: IntMinutes) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - minutes)

    operator fun minus(minutes: LongMinutes) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - minutes)

    operator fun minus(seconds: IntSeconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - seconds)

    operator fun minus(seconds: LongSeconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - seconds)

    operator fun minus(milliseconds: IntMilliseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - milliseconds)

    operator fun minus(milliseconds: LongMilliseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - milliseconds)

    operator fun minus(microseconds: IntMicroseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - microseconds)

    operator fun minus(microseconds: LongMicroseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - microseconds)

    operator fun minus(nanoseconds: IntNanoseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - nanoseconds)

    operator fun minus(nanoseconds: LongNanoseconds) =
        fromDurationSinceUnixEpoch(durationSinceUnixEpoch - nanoseconds)

    operator fun rangeTo(other: Instant) = InstantRange(this, other)

    override fun compareTo(other: Instant): Int {
        return durationSinceUnixEpoch.compareTo(other.durationSinceUnixEpoch)
    }

    override fun toString(): String {
        val dateTime = this.toDateTimeAt(UtcOffset.ZERO)

        return buildString(MAX_DATE_TIME_STRING_LENGTH + 1) {
            appendDateTime(dateTime)
            append('Z')
        }
    }

    companion object {
        val MIN = Instant(Duration.MIN)
        val MAX = Instant(Duration.MAX)
        val UNIX_EPOCH = Instant(Duration.ZERO)

        /**
         * Create an [Instant] from a [Duration] relative to the Unix epoch of 1970-01-01T00:00Z
         */
        fun fromDurationSinceUnixEpoch(duration: Duration): Instant {
            return Instant(duration)
        }

        /**
         * Create the [Instant] represented by a number of seconds relative to the Unix epoch of 1970-01-01T00:00Z
         */
        fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds = 0.nanoseconds
        ): Instant {
            return fromDurationSinceUnixEpoch(durationOf(seconds, nanosecondAdjustment))
        }

        /**
         * Create the [Instant] represented by a number of milliseconds relative to the Unix epoch of 1970-01-01T00:00Z
         */
        fun fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds): Instant {
            return fromDurationSinceUnixEpoch(milliseconds.asDuration())
        }

        /**
         * Create an [Instant] from the second of the Unix epoch
         */
        fun fromUnixEpochSecond(second: Long, nanoOfSecond: Int = 0): Instant {
            return fromSecondsSinceUnixEpoch(second.seconds, nanoOfSecond.nanoseconds)
        }

        /**
         * Create an [Instant] from the millisecond of the Unix epoch
         */
        fun fromUnixEpochMillisecond(millisecond: Long): Instant {
            return fromMillisecondsSinceUnixEpoch(millisecond.milliseconds)
        }
    }
}

fun String.toInstant() = toInstant(Iso8601.Extended.INSTANT_PARSER)

fun String.toInstant(parser: DateTimeParser): Instant {
    val result = parser.parse(this)
    return result.toInstant() ?: raiseParserFieldResolutionException("Instant", this)
}

internal fun DateTimeParseResult.toInstant(): Instant? {
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()

    return if (dateTime != null && offset != null) {
        dateTime.instantAt(offset)
    } else {
        null
    }
}

/**
 * Get the [Duration] between two instants
 */
fun durationBetween(start: Instant, endExclusive: Instant): Duration {
    return endExclusive.durationSinceUnixEpoch - start.durationSinceUnixEpoch
}

/**
 * Get the number of 24-hour days between two instants
 */
fun daysBetween(start: Instant, endExclusive: Instant): LongDays {
    return secondsBetween(start, endExclusive).inWholeDays
}

/**
 * Get the number of whole hours between two instants
 */
fun hoursBetween(start: Instant, endExclusive: Instant): LongHours {
    return secondsBetween(start, endExclusive).inWholeHours
}

/**
 * Get the number of whole minutes between two instants
 */
fun minutesBetween(start: Instant, endExclusive: Instant): LongMinutes {
    return secondsBetween(start, endExclusive).inWholeMinutes
}

/**
 * Get the number of whole seconds between two instants
 * @throws ArithmeticException if the result overflows
 */
fun secondsBetween(start: Instant, endExclusive: Instant): LongSeconds {
    val secondDiff = endExclusive.secondsSinceUnixEpoch minusExact start.secondsSinceUnixEpoch
    val nanoDiff = endExclusive.nanosecondAdjustment minusWithOverflow start.nanosecondAdjustment

    return when {
        secondDiff.value > 0 && nanoDiff.value < 0 -> secondDiff - 1.seconds
        secondDiff.value < 0 && nanoDiff.value > 0 -> secondDiff + 1.seconds
        else -> secondDiff
    }
}

/**
 * Get the number of whole milliseconds between two instants
 * @throws ArithmeticException if the result overflows
 */
fun millisecondsBetween(start: Instant, endExclusive: Instant): LongMilliseconds {
    return (endExclusive.secondsSinceUnixEpoch minusExact start.secondsSinceUnixEpoch).inMillisecondsExact() plusExact
        (endExclusive.nanosecondAdjustment - start.nanosecondAdjustment).inWholeMilliseconds
}

/**
 * Get the number of whole microseconds between two instants
 *  @throws ArithmeticException if the result overflows
 */
fun microsecondsBetween(start: Instant, endExclusive: Instant): LongMicroseconds {
    return (endExclusive.secondsSinceUnixEpoch minusExact start.secondsSinceUnixEpoch).inMicrosecondsExact() plusExact
        (endExclusive.nanosecondAdjustment - start.nanosecondAdjustment).inWholeMicroseconds
}

/**
 * Get the number of nanoseconds between two instants
 * @throws ArithmeticException if the result overflows
 */
fun nanosecondsBetween(start: Instant, endExclusive: Instant): LongNanoseconds {
    return (endExclusive.secondsSinceUnixEpoch minusExact start.secondsSinceUnixEpoch).inNanosecondsExact() plusExact
        (endExclusive.nanosecondAdjustment - start.nanosecondAdjustment)
}