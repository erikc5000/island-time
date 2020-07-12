@file:JvmMultifileClass
@file:JvmName("OperatorsKt")

package io.islandtime.operators

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.internal.NANOSECONDS_PER_MICROSECOND
import io.islandtime.internal.NANOSECONDS_PER_MILLISECOND
import io.islandtime.internal.floorMod
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import io.islandtime.operators.internal.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Return this time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: IntHours): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Time.roundedUpToNearest(increment: IntMinutes): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Time.roundedUpToNearest(increment: IntSeconds): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedUpToNearest(increment: IntMilliseconds): Time {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedUpToNearest(increment: IntMicroseconds): Time {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedUpToNearest(increment: IntNanoseconds): Time {
    return roundedUpToNearest(increment.toLongNanoseconds())
}

/**
 * Return this time, rounded up to match the precision of a given [unit].
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
 * Return this time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: IntHours): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetTime.roundedUpToNearest(increment: IntMinutes): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetTime.roundedUpToNearest(increment: IntSeconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedUpToNearest(increment: IntMilliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedUpToNearest(increment: IntMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedUpToNearest(increment: IntNanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to match the precision of a given [unit].
 */
fun OffsetTime.roundedUpTo(unit: TimeUnit): OffsetTime {
    return copyIfChanged(time = time.roundedUpTo(unit))
}

/**
 * Return this date-time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: IntHours): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun DateTime.roundedUpToNearest(increment: IntMinutes): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun DateTime.roundedUpToNearest(increment: IntSeconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedUpToNearest(increment: IntMilliseconds): DateTime {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedUpToNearest(increment: IntMicroseconds): DateTime {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedUpToNearest(increment: IntNanoseconds): DateTime {
    return roundedUpToNearest(increment.toLongNanoseconds())
}

/**
 * Return this date-time, rounded up to match the precision of a given [unit].
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
 * Return this date-time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntHours): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntMinutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntSeconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntMilliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntNanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to match the precision of a given [unit].
 *
 * The start of the next day will be returned when the resulting time is after midnight.
 */
fun OffsetDateTime.roundedUpTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpTo(unit))
}

/**
 * Return this date-time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: IntHours): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: IntMinutes): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: IntSeconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: IntMilliseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: IntMicroseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: IntNanoseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to match the precision of a given [unit].
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
 * Return this instant, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Instant.roundedUpToNearest(increment: IntHours): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Return this instant, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Instant.roundedUpToNearest(increment: IntMinutes): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Return this instant, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Instant.roundedUpToNearest(increment: IntSeconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

/**
 * Return this instant, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedUpToNearest(increment: IntMilliseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked((increment.value * NANOSECONDS_PER_MILLISECOND).nanoseconds)
}

/**
 * Return this instant, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedUpToNearest(increment: IntMicroseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked((increment.value * NANOSECONDS_PER_MICROSECOND).nanoseconds)
}

/**
 * Return this instant, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedUpToNearest(increment: IntNanoseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

/**
 * Return this instant, rounded up to match the precision of a given [unit].
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

private fun Time.roundedUpToNearest(increment: LongNanoseconds): Time {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

private fun Time.roundedUpToNearestUnchecked(increment: LongNanoseconds): Time {
    val remainder = nanosecondsSinceStartOfDay % increment.value
    return if (remainder.value > 0) this + (increment - remainder) else this
}

private fun DateTime.roundedUpToNearest(increment: LongNanoseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedUpToNearestUnchecked(increment)
}

private fun DateTime.roundedUpToNearestUnchecked(increment: LongNanoseconds): DateTime {
    val newTime = time.roundedUpToNearestUnchecked(increment)

    return when {
        newTime == time -> this
        newTime == MIDNIGHT && time > MIDNIGHT -> DateTime(date + 1.days, newTime)
        else -> copy(time = newTime)
    }
}

private fun Instant.roundedUpToNearestUnchecked(increment: IntSeconds): Instant {
    val remainder = (secondOfUnixEpoch floorMod increment.value).seconds + additionalNanosecondsSinceUnixEpoch
    return if (remainder.value > 0) this + (increment - remainder) else this
}

private fun Instant.roundedUpToNearestUnchecked(increment: IntNanoseconds): Instant {
    val remainder = additionalNanosecondsSinceUnixEpoch % increment.value
    return if (remainder.value > 0) this + (increment - remainder) else this
}