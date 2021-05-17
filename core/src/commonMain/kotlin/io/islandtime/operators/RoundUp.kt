@file:JvmName("DateTimesKt")
@file:JvmMultifileClass
@file:Suppress("PackageDirectoryMismatch")

package io.islandtime

import dev.erikchristensen.javamath2kmp.floorMod
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Returns this time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: Hours): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Time.roundedUpToNearest(increment: Minutes): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Time.roundedUpToNearest(increment: Seconds): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedUpToNearest(increment: Milliseconds): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedUpToNearest(increment: Microseconds): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedUpToNearest(increment: Nanoseconds): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

/**
 * Returns this time, rounded up to match the precision of a given [unit].
 */
fun Time.roundedUpTo(unit: TimeUnit): Time {
    return when (unit) {
        DAYS -> MIDNIGHT
        HOURS -> if (minute > 0 || second > 0 || nanosecond > 0) nextWholeHour() else this
        MINUTES -> if (second > 0 || nanosecond > 0) nextWholeMinute() else this
        SECONDS -> if (nanosecond > 0) nextWholeSecond() else this
        MILLISECONDS -> roundedUpToNearest(1.milliseconds)
        MICROSECONDS -> roundedUpToNearest(1.microseconds)
        NANOSECONDS -> this
    }
}

/**
 * Returns this time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: Hours): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Returns this time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetTime.roundedUpToNearest(increment: Minutes): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Returns this time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetTime.roundedUpToNearest(increment: Seconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Returns this time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedUpToNearest(increment: Milliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Returns this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedUpToNearest(increment: Microseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Returns this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedUpToNearest(increment: Nanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Returns this time, rounded up to match the precision of a given [unit].
 */
fun OffsetTime.roundedUpTo(unit: TimeUnit): OffsetTime {
    return copyIfChanged(time = time.roundedUpTo(unit))
}

/**
 * Returns this date-time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: Hours): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun DateTime.roundedUpToNearest(increment: Minutes): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun DateTime.roundedUpToNearest(increment: Seconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedUpToNearest(increment: Milliseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedUpToNearest(increment: Microseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedUpToNearest(increment: Nanoseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

/**
 * Returns this date-time, rounded up to match the precision of a given [unit].
 *
 * The start of the next day will be returned when the resulting time is after midnight.
 */
fun DateTime.roundedUpTo(unit: TimeUnit): DateTime {
    return when (unit) {
        DAYS -> if (time > MIDNIGHT) (date + 1.days).startOfDay else this
        HOURS -> if (minute > 0 || second > 0 || nanosecond > 0) nextWholeHour() else this
        MINUTES -> if (second > 0 || nanosecond > 0) nextWholeMinute() else this
        SECONDS -> if (nanosecond > 0) nextWholeSecond() else this
        MILLISECONDS -> roundedUpToNearest(1.milliseconds)
        MICROSECONDS -> roundedUpToNearest(1.microseconds)
        NANOSECONDS -> this
    }
}

/**
 * Returns this date-time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: Hours): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetDateTime.roundedUpToNearest(increment: Minutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetDateTime.roundedUpToNearest(increment: Seconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedUpToNearest(increment: Milliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedUpToNearest(increment: Microseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedUpToNearest(increment: Nanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to match the precision of a given [unit].
 *
 * The start of the next day will be returned when the resulting time is after midnight.
 */
fun OffsetDateTime.roundedUpTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpTo(unit))
}

/**
 * Returns this date-time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: Hours): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: Minutes): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: Seconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: Milliseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: Microseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: Nanoseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Returns this date-time, rounded up to match the precision of a given [unit].
 *
 * The start of the next day will be returned when the resulting time is after midnight.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpTo(unit: TimeUnit): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpTo(unit))
}

/**
 * Returns this instant, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Instant.roundedUpToNearest(increment: Hours): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Returns this instant, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Instant.roundedUpToNearest(increment: Minutes): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Returns this instant, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Instant.roundedUpToNearest(increment: Seconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

/**
 * Returns this instant, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedUpToNearest(increment: Milliseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this instant, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedUpToNearest(increment: Microseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this instant, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedUpToNearest(increment: Nanoseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

/**
 * Returns this instant, rounded up to match the precision of a given [unit].
 */
fun Instant.roundedUpTo(unit: TimeUnit): Instant {
    return when (unit) {
        DAYS -> roundedUpToNearestUnchecked(24.hours.inSecondsUnchecked)
        HOURS -> roundedUpToNearestUnchecked(1.hours.inSecondsUnchecked)
        MINUTES -> roundedUpToNearestUnchecked(1.minutes.inSecondsUnchecked)
        SECONDS -> roundedUpToNearestUnchecked(1.seconds)
        MILLISECONDS -> roundedUpToNearestUnchecked(1_000_000.nanoseconds)
        MICROSECONDS -> roundedUpToNearestUnchecked(1_000.nanoseconds)
        NANOSECONDS -> this
    }
}

private fun Time.roundedUpToNearestUnchecked(increment: Nanoseconds): Time {
    val remainder = nanosecondsSinceStartOfDay % increment.value
    return if (remainder.value > 0) this + (increment - remainder) else this
}

private fun DateTime.roundedUpToNearestUnchecked(increment: Nanoseconds): DateTime {
    val newTime = time.roundedUpToNearestUnchecked(increment)

    return when {
        newTime == time -> this
        newTime == MIDNIGHT && time > MIDNIGHT -> DateTime(date + 1.days, newTime)
        else -> copy(time = newTime)
    }
}

private fun Instant.roundedUpToNearestUnchecked(increment: Seconds): Instant {
    val remainder = (secondOfUnixEpoch floorMod increment.value).seconds + additionalNanosecondsSinceUnixEpoch
    return if (remainder.value > 0) this + (increment - remainder) else this
}

private fun Instant.roundedUpToNearestUnchecked(increment: Nanoseconds): Instant {
    val remainder = additionalNanosecondsSinceUnixEpoch % increment.value
    return if (remainder.value > 0) this + (increment - remainder) else this
}
