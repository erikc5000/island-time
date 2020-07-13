@file:JvmMultifileClass
@file:JvmName("OperatorsKt")

package io.islandtime.operators

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.Time.Companion.NOON
import io.islandtime.internal.NANOSECONDS_PER_MICROSECOND
import io.islandtime.internal.NANOSECONDS_PER_MILLISECOND
import io.islandtime.internal.floorMod
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import io.islandtime.operators.internal.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Return this time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: IntHours): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Time.roundedToNearest(increment: IntMinutes): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Time.roundedToNearest(increment: IntSeconds): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedToNearest(increment: IntMilliseconds): Time {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedToNearest(increment: IntMicroseconds): Time {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedToNearest(increment: IntNanoseconds): Time {
    return roundedToNearest(increment.toLongNanoseconds())
}

/**
 * Return this time, rounded to match the precision of a given [unit]. If the time is halfway between whole values of
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
 * Return this time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: IntHours): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetTime.roundedToNearest(increment: IntMinutes): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetTime.roundedToNearest(increment: IntSeconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedToNearest(increment: IntMilliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedToNearest(increment: IntMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedToNearest(increment: IntNanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to match the precision of a given [unit]. If the time is halfway between whole values of
 * the unit, it will be rounded up.
 */
fun OffsetTime.roundedTo(unit: TimeUnit): OffsetTime {
    return copyIfChanged(time = time.roundedTo(unit))
}

/**
 * Return this date-time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: IntHours): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun DateTime.roundedToNearest(increment: IntMinutes): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun DateTime.roundedToNearest(increment: IntSeconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedToNearest(increment: IntMilliseconds): DateTime {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedToNearest(increment: IntMicroseconds): DateTime {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedToNearest(increment: IntNanoseconds): DateTime {
    return roundedToNearest(increment.toLongNanoseconds())
}

/**
 * Return this date-time, rounded to match the precision of a given [unit]. If the time is halfway between whole values
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
 * Return this date-time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: IntHours): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetDateTime.roundedToNearest(increment: IntMinutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetDateTime.roundedToNearest(increment: IntSeconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedToNearest(increment: IntMilliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedToNearest(increment: IntMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedToNearest(increment: IntNanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to match the precision of a given [unit]. If the time is halfway between whole values
 * of the unit, it will be rounded up.
 */
fun OffsetDateTime.roundedTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedTo(unit))
}

/**
 * Return this date-time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: IntHours): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: IntMinutes): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: IntSeconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: IntMilliseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: IntMicroseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: IntNanoseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to match the precision of a given [unit]. If the time is halfway between whole values
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
 * Return this instant, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Instant.roundedToNearest(increment: IntHours): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Return this instant, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Instant.roundedToNearest(increment: IntMinutes): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Return this instant, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Instant.roundedToNearest(increment: IntSeconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

/**
 * Return this instant, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedToNearest(increment: IntMilliseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked((increment.value * NANOSECONDS_PER_MILLISECOND).nanoseconds)
}

/**
 * Return this instant, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedToNearest(increment: IntMicroseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked((increment.value * NANOSECONDS_PER_MICROSECOND).nanoseconds)
}

/**
 * Return this instant, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedToNearest(increment: IntNanoseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

/**
 * Return this instant, rounded to match the precision of a given [unit]. If the time is halfway between whole values
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

private fun Time.roundedToNearest(increment: LongNanoseconds): Time {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

private fun Time.roundedToNearestUnchecked(increment: LongNanoseconds): Time {
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

private fun DateTime.roundedToNearest(increment: LongNanoseconds): DateTime {
    checkRoundingIncrement(increment)
    return roundedToNearestUnchecked(increment)
}

private fun DateTime.roundedToNearestUnchecked(increment: LongNanoseconds): DateTime {
    val newTime = time.roundedToNearestUnchecked(increment)

    return when {
        newTime == time -> this
        newTime == MIDNIGHT && time >= NOON -> DateTime(date + 1.days, newTime)
        else -> copy(time = newTime)
    }
}

private fun Instant.roundedToNearestUnchecked(increment: IntSeconds): Instant {
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

private fun Instant.roundedToNearestUnchecked(increment: IntNanoseconds): Instant {
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