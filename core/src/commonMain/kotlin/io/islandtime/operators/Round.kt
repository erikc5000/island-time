@file:JvmName("DateTimesKt")
@file:JvmMultifileClass
@file:Suppress("PackageDirectoryMismatch")

package io.islandtime

import dev.erikchristensen.javamath2kmp.floorMod
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.Time.Companion.NOON
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Returns this time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: Hours): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Time.roundedToNearest(increment: Minutes): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Time.roundedToNearest(increment: Seconds): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedToNearest(increment: Milliseconds): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedToNearest(increment: Microseconds): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedToNearest(increment: Nanoseconds): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

/**
 * Returns this time, rounded to match the precision of a given [unit]. If the time is halfway between whole values of
 * the unit, it will be rounded up.
 */
fun Time.roundedTo(unit: TimeUnit): Time {
    return when (unit) {
        DAYS -> MIDNIGHT
        HOURS -> if (minute >= 30) nextWholeHour() else previousWholeHour()
        MINUTES -> if (second >= 30) nextWholeMinute() else previousWholeMinute()
        SECONDS -> if (nanosecond >= 500_000_000) nextWholeSecond() else previousWholeSecond()
        MILLISECONDS -> roundedToNearest(1.milliseconds)
        MICROSECONDS -> roundedToNearest(1.microseconds)
        NANOSECONDS -> this
    }
}

/**
 * Returns this time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: Hours): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Returns this time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetTime.roundedToNearest(increment: Minutes): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Returns this time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetTime.roundedToNearest(increment: Seconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Returns this time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedToNearest(increment: Milliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Returns this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedToNearest(increment: Microseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Returns this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedToNearest(increment: Nanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Returns this time, rounded to match the precision of a given [unit]. If the time is halfway between whole values of
 * the unit, it will be rounded up.
 */
fun OffsetTime.roundedTo(unit: TimeUnit): OffsetTime {
    return copyIfChanged(time = time.roundedTo(unit))
}

/**
 * Returns this date-time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: Hours): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun DateTime.roundedToNearest(increment: Minutes): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun DateTime.roundedToNearest(increment: Seconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedToNearest(increment: Milliseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedToNearest(increment: Microseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedToNearest(increment: Nanoseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

/**
 * Returns this date-time, rounded to match the precision of a given [unit]. If the time is halfway between whole values
 * of the unit, it will be rounded up.
 */
fun DateTime.roundedTo(unit: TimeUnit): DateTime {
    return when (unit) {
        DAYS -> if (hour >= 12) (date + 1.days).startOfDay else date.startOfDay
        HOURS -> if (minute >= 30) nextWholeHour() else previousWholeHour()
        MINUTES -> if (second >= 30) nextWholeMinute() else previousWholeMinute()
        SECONDS -> if (nanosecond >= 500_000_000) nextWholeSecond() else previousWholeSecond()
        MILLISECONDS -> roundedToNearest(1.milliseconds)
        MICROSECONDS -> roundedToNearest(1.microseconds)
        NANOSECONDS -> this
    }
}

/**
 * Returns this date-time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: Hours): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetDateTime.roundedToNearest(increment: Minutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetDateTime.roundedToNearest(increment: Seconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedToNearest(increment: Milliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedToNearest(increment: Microseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedToNearest(increment: Nanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to match the precision of a given [unit]. If the time is halfway between whole values
 * of the unit, it will be rounded up.
 */
fun OffsetDateTime.roundedTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedTo(unit))
}

/**
 * Returns this date-time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: Hours): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: Minutes): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: Seconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: Milliseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: Microseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: Nanoseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Returns this date-time, rounded to match the precision of a given [unit]. If the time is halfway between whole values
 * of the unit, it will be rounded up.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedTo(unit: TimeUnit): ZonedDateTime {
    return copyIfChanged(dateTime.roundedTo(unit))
}

/**
 * Returns this instant, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Instant.roundedToNearest(increment: Hours): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Returns this instant, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Instant.roundedToNearest(increment: Minutes): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Returns this instant, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Instant.roundedToNearest(increment: Seconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

/**
 * Returns this instant, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedToNearest(increment: Milliseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this instant, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedToNearest(increment: Microseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked((increment.value * NANOSECONDS_PER_MICROSECOND).nanoseconds)
}

/**
 * Returns this instant, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedToNearest(increment: Nanoseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

/**
 * Returns this instant, rounded to match the precision of a given [unit]. If the time is halfway between whole values
 * of the unit, it will be rounded up.
 */
fun Instant.roundedTo(unit: TimeUnit): Instant {
    return when (unit) {
        DAYS -> roundedToNearestUnchecked(24.hours.inSecondsUnchecked)
        HOURS -> roundedToNearestUnchecked(1.hours.inSecondsUnchecked)
        MINUTES -> roundedToNearestUnchecked(1.minutes.inSecondsUnchecked)
        SECONDS -> roundedToNearestUnchecked(1.seconds)
        MILLISECONDS -> roundedToNearestUnchecked(1_000_000.nanoseconds)
        MICROSECONDS -> roundedToNearestUnchecked(1_000.nanoseconds)
        NANOSECONDS -> this
    }
}

private fun Time.roundedToNearestUnchecked(increment: Nanoseconds): Time {
    val remainder = nanosecondsSinceStartOfDay % increment.value

    return if (remainder.value > 0) {
        if (remainder.value < increment.value / 2) {
            this - remainder
        } else {
            this + (increment - remainder)
        }
    } else {
        this
    }
}

private fun DateTime.roundedToNearestUnchecked(increment: Nanoseconds): DateTime {
    val newTime = time.roundedToNearestUnchecked(increment)

    return when {
        newTime == time -> this
        newTime == MIDNIGHT && time >= NOON -> DateTime(date + 1.days, newTime)
        else -> copy(time = newTime)
    }
}

private fun Instant.roundedToNearestUnchecked(increment: Seconds): Instant {
    val remainder = (secondOfUnixEpoch floorMod increment.value).seconds + additionalNanosecondsSinceUnixEpoch

    return if (remainder.value > 0) {
        if (remainder < increment.inNanoseconds / 2) {
            this - remainder
        } else {
            this + (increment - remainder)
        }
    } else {
        this
    }
}

private fun Instant.roundedToNearestUnchecked(increment: Nanoseconds): Instant {
    val remainder = additionalNanosecondsSinceUnixEpoch % increment.value

    return if (remainder.value > 0) {
        if (remainder.value < increment.value / 2) {
            this - remainder
        } else {
            this + (increment - remainder)
        }
    } else {
        this
    }
}
