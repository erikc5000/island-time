package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.InstantInterval

/**
 * An instant in time with nanosecond precision
 */
// TODO: Copy Duration code into here and avoid using inline class
inline class Instant(
    val durationSinceUnixEpoch: Duration
) : TimePoint<Instant>,
    Comparable<Instant> {

    override val secondsSinceUnixEpoch: LongSeconds
        get() = durationSinceUnixEpoch.seconds

    override val nanoOfSecondsSinceUnixEpoch: IntNanoseconds
        get() = durationSinceUnixEpoch.nanosecondAdjustment

    override val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = secondsSinceUnixEpoch.inMillisecondsExact() plusExact
            nanoOfSecondsSinceUnixEpoch.inMilliseconds.toLong()

    override val unixEpochSecond: Long
        get() = secondsSinceUnixEpoch.value

    override val unixEpochNanoOfSecond: Int
        get() = nanoOfSecondsSinceUnixEpoch.value

    override val unixEpochMillisecond: Long
        get() = millisecondsSinceUnixEpoch.value

    operator fun plus(duration: Duration) = Instant(durationSinceUnixEpoch + duration)
    operator fun plus(days: IntDays) = Instant(durationSinceUnixEpoch + days)
    operator fun plus(days: LongDays) = Instant(durationSinceUnixEpoch + days)
    override operator fun plus(hours: IntHours) = Instant(durationSinceUnixEpoch + hours)
    override operator fun plus(hours: LongHours) = Instant(durationSinceUnixEpoch + hours)
    override operator fun plus(minutes: IntMinutes) = Instant(durationSinceUnixEpoch + minutes)
    override operator fun plus(minutes: LongMinutes) = Instant(durationSinceUnixEpoch + minutes)
    override operator fun plus(seconds: IntSeconds) = Instant(durationSinceUnixEpoch + seconds)
    override operator fun plus(seconds: LongSeconds) = Instant(durationSinceUnixEpoch + seconds)

    override operator fun plus(milliseconds: IntMilliseconds) =
        Instant(durationSinceUnixEpoch + milliseconds)

    override operator fun plus(milliseconds: LongMilliseconds) =
        Instant(durationSinceUnixEpoch + milliseconds)

    override operator fun plus(microseconds: IntMicroseconds) =
        Instant(durationSinceUnixEpoch + microseconds)

    override operator fun plus(microseconds: LongMicroseconds) =
        Instant(durationSinceUnixEpoch + microseconds)

    override operator fun plus(nanoseconds: IntNanoseconds) =
        Instant(durationSinceUnixEpoch + nanoseconds)

    override operator fun plus(nanoseconds: LongNanoseconds) =
        Instant(durationSinceUnixEpoch + nanoseconds)

    operator fun minus(duration: Duration) = Instant(durationSinceUnixEpoch - duration)
    operator fun minus(days: IntDays) = Instant(durationSinceUnixEpoch - days)
    operator fun minus(days: LongDays) = Instant(durationSinceUnixEpoch - days)
    override operator fun minus(hours: IntHours) = Instant(durationSinceUnixEpoch - hours)
    override operator fun minus(hours: LongHours) = Instant(durationSinceUnixEpoch - hours)
    override operator fun minus(minutes: IntMinutes) = Instant(durationSinceUnixEpoch - minutes)
    override operator fun minus(minutes: LongMinutes) = Instant(durationSinceUnixEpoch - minutes)
    override operator fun minus(seconds: IntSeconds) = Instant(durationSinceUnixEpoch - seconds)
    override operator fun minus(seconds: LongSeconds) = Instant(durationSinceUnixEpoch - seconds)

    override operator fun minus(milliseconds: IntMilliseconds) =
        Instant(durationSinceUnixEpoch - milliseconds)

    override operator fun minus(milliseconds: LongMilliseconds) =
        Instant(durationSinceUnixEpoch - milliseconds)

    override operator fun minus(microseconds: IntMicroseconds) =
        Instant(durationSinceUnixEpoch - microseconds)

    override operator fun minus(microseconds: LongMicroseconds) =
        Instant(durationSinceUnixEpoch - microseconds)

    override operator fun minus(nanoseconds: IntNanoseconds) =
        Instant(durationSinceUnixEpoch - nanoseconds)

    override operator fun minus(nanoseconds: LongNanoseconds) =
        Instant(durationSinceUnixEpoch - nanoseconds)

    operator fun rangeTo(other: Instant) = InstantInterval.withInclusiveEnd(this, other)

    override fun compareTo(other: Instant): Int {
        return durationSinceUnixEpoch.compareTo(other.durationSinceUnixEpoch)
    }

    override fun toString() = buildString(MAX_INSTANT_STRING_LENGTH) { appendInstant(this@Instant) }

    companion object {
        val MIN = Instant(Duration.MIN)
        val MAX = Instant(Duration.MAX)
        val UNIX_EPOCH = Instant(Duration.ZERO)

        /**
         * Create the [Instant] represented by a number of seconds relative to the Unix epoch of 1970-01-01T00:00Z
         */
        fun fromSecondsSinceUnixEpoch(
            seconds: LongSeconds,
            nanosecondAdjustment: IntNanoseconds = 0.nanoseconds
        ): Instant {
            return Instant(durationOf(seconds, nanosecondAdjustment))
        }

        /**
         * Create the [Instant] represented by a number of milliseconds relative to the Unix epoch of 1970-01-01T00:00Z
         */
        fun fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds): Instant {
            return Instant(milliseconds)
        }

        /**
         * Create an [Instant] from the second of the Unix epoch
         */
        fun fromUnixEpochSecond(second: Long, nanoOfSecond: Int = 0): Instant {
            return Instant(second.seconds, nanoOfSecond.nanoseconds)
        }

        /**
         * Create an [Instant] from the millisecond of the Unix epoch
         */
        fun fromUnixEpochMillisecond(millisecond: Long): Instant {
            return Instant(millisecond.milliseconds)
        }
    }
}

internal const val MAX_INSTANT_STRING_LENGTH = MAX_DATE_TIME_STRING_LENGTH + 1

internal fun StringBuilder.appendInstant(instant: Instant): StringBuilder {
    val dateTime = instant.toDateTimeAt(UtcOffset.ZERO)
    appendDateTime(dateTime)
    append('Z')
    return this
}

@Suppress("FunctionName")
fun Instant(daysSinceUnixEpoch: IntDays, nanosecondAdjustment: IntNanoseconds = 0.nanoseconds) =
    Instant(durationOf(daysSinceUnixEpoch.inSecondsExact(), nanosecondAdjustment))

@Suppress("FunctionName")
fun Instant(daysSinceUnixEpoch: LongDays, nanosecondAdjustment: IntNanoseconds = 0.nanoseconds) =
    Instant(durationOf(daysSinceUnixEpoch.inSecondsExact(), nanosecondAdjustment))

@Suppress("FunctionName")
fun Instant(secondsSinceUnixEpoch: IntSeconds, nanosecondAdjustment: IntNanoseconds = 0.nanoseconds) =
    Instant(durationOf(secondsSinceUnixEpoch, nanosecondAdjustment))

@Suppress("FunctionName")
fun Instant(secondsSinceUnixEpoch: LongSeconds, nanosecondAdjustment: IntNanoseconds = 0.nanoseconds) =
    Instant(durationOf(secondsSinceUnixEpoch, nanosecondAdjustment))

/**
 * Create the [Instant] represented by a number of milliseconds relative to the Unix epoch of 1970-01-01T00:00Z
 */
@Suppress("FunctionName")
fun Instant(milliseconds: IntMilliseconds): Instant {
    return Instant(milliseconds.asDuration())
}

/**
 * Create the [Instant] represented by a number of milliseconds relative to the Unix epoch of 1970-01-01T00:00Z
 */
@Suppress("FunctionName")
fun Instant(milliseconds: LongMilliseconds): Instant {
    return Instant(milliseconds.asDuration())
}

fun String.toInstant() = toInstant(DateTimeParsers.Iso.Extended.INSTANT)

fun String.toInstant(parser: DateTimeParser): Instant {
    val result = parser.parse(this)
    return result.toInstant() ?: throwParserFieldResolutionException<Instant>(this)
}

internal fun DateTimeParseResult.toInstant(): Instant? {
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()

    return if (dateTime != null && offset != null) {
        dateTime.toInstantAt(offset)
    } else {
        null
    }
}