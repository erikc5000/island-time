package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.*

/**
 * An instant in time with nanosecond precision
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class Instant internal constructor(
    val durationSinceUnixEpoch: Duration
): Comparable<Instant> {

    inline val secondsSinceUnixEpoch: LongSeconds
        get() = durationSinceUnixEpoch.seconds

    inline val nanosecondAdjustment: IntNanoseconds
        get() = durationSinceUnixEpoch.nanosecondAdjustment

    // Think about whether a truncated representation like this should be part of the API
//    val millisecondsSinceUnixEpoch: LongMilliseconds
//        get() = durationSinceUnixEpoch.seconds + durationSinceUnixEpoch.nanosecondAdjustment.inWholeMilliseconds

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

    override fun compareTo(other: Instant): Int {
        return durationSinceUnixEpoch.compareTo(other.durationSinceUnixEpoch)
    }

    override fun toString(): String {
        val dateTime = DateTime.fromDurationSinceUnixEpoch(durationSinceUnixEpoch, UtcOffset.ZERO)

        return buildString(MAX_DATE_TIME_STRING_LENGTH + 1) {
            appendDateTime(dateTime)
            append('Z')
        }
    }

    companion object {
        val MAX = Instant(DateTime.MAX.secondsSinceUnixEpochAt(UtcOffset.ZERO).asDuration())
        val MIN = Instant(DateTime.MIN.secondsSinceUnixEpochAt(UtcOffset.ZERO).asDuration())
        val UNIX_EPOCH = Instant(Duration.ZERO)

        fun fromDurationSinceUnixEpoch(duration: Duration): Instant {
            if (duration !in MIN.durationSinceUnixEpoch..MAX.durationSinceUnixEpoch) {
                throw DateTimeException("'$duration' exceeds the supported Instant range")
            }

            return Instant(duration)
        }

        fun fromSecondsSinceUnixEpoch(seconds: LongSeconds, nanosecondAdjustment: IntNanoseconds): Instant {
            return fromDurationSinceUnixEpoch(durationOf(seconds, nanosecondAdjustment))
        }

        fun fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds): Instant {
            return fromDurationSinceUnixEpoch(milliseconds.asDuration())
        }

        fun now() = now(systemClock())

        fun now(clock: Clock) = clock.instant()
    }
}