package io.islandtime.operators

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.Time.Companion.NOON
import io.islandtime.internal.*
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.internal.SECONDS_PER_DAY
import io.islandtime.internal.SECONDS_PER_HOUR
import io.islandtime.internal.SECONDS_PER_MINUTE
import io.islandtime.internal.floorMod
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import io.islandtime.measures.internal.plusWithOverflow
import io.islandtime.operators.internal.*

/**
 * Return this time, rounded to the nearest hour that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: IntHours): Time {
    return roundedToNearest(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: IntMinutes): Time {
    return roundedToNearest(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: IntSeconds): Time {
    return roundedToNearest(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: IntMilliseconds): Time {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: IntMicroseconds): Time {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: LongMicroseconds): Time {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: IntNanoseconds): Time {
    return roundedToNearest(increment)
}

/**
 * Return this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedToNearest(increment: LongNanoseconds): Time {
    checkRoundingIncrement(increment)
    val remainder = nanosecondsSinceStartOfDay % increment.value

    return if (remainder.value > 0) {
        if (remainder < increment / 2) {
            this - remainder
        } else {
            this + (increment - remainder)
        }
    } else {
        this
    }
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
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: IntMinutes): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: IntSeconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: IntMilliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: IntMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: LongMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: IntNanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedToNearest(increment))
}

/**
 * Return this time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedToNearest(increment: LongNanoseconds): OffsetTime {
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
    return roundedToNearest(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded to the nearest minute that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: IntMinutes): DateTime {
    return roundedToNearest(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: IntSeconds): DateTime {
    return roundedToNearest(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: IntMilliseconds): DateTime {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: IntMicroseconds): DateTime {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: LongMicroseconds): DateTime {
    return roundedToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: IntNanoseconds): DateTime {
    return roundedToNearest(increment.toLongNanoseconds())
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedToNearest(increment: LongNanoseconds): DateTime {
    val newTime = time.roundedToNearest(increment)

    return when {
        newTime == time -> this
        newTime == MIDNIGHT && time >= NOON -> DateTime(date + 1.days, newTime)
        else -> copy(time = newTime)
    }
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
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: IntMinutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest second that satisfies the [increment]. If the time is halfway between
 * increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: IntSeconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest millisecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: IntMilliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: IntMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: LongMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: IntNanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedToNearest(increment: LongNanoseconds): OffsetDateTime {
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * Return this date-time, rounded to the nearest microsecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: LongMicroseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedToNearest(increment))
}

/**
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
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
 * Return this date-time, rounded to the nearest nanosecond that satisfies the [increment]. If the time is halfway
 * between increments, it will be rounded up.
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedToNearest(increment: LongNanoseconds): ZonedDateTime {
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

fun Instant.roundedToNearest(increment: IntHours): Instant {
    return roundedToNearest(increment.inSecondsUnchecked)
}

fun Instant.roundedToNearest(increment: IntMinutes): Instant {
    return roundedToNearest(increment.inSecondsUnchecked)
}

fun Instant.roundedToNearest(increment: IntSeconds): Instant {
    checkRoundingIncrement(increment)
    val remainder = (secondOfUnixEpoch floorMod increment.value).seconds + additionalNanosecondsSinceUnixEpoch

    return if (remainder.value > 0L) {
        if (remainder < increment.inNanoseconds / 2) {
            this - remainder
        } else {
            this + (increment - remainder)
        }
    } else {
        this
    }
}

fun Instant.roundedToNearest(increment: IntMilliseconds): Instant {
    return roundedToNearest(increment.inNanoseconds)
}

fun Instant.roundedToNearest(increment: IntMicroseconds): Instant {
    return roundedToNearest(increment.inNanoseconds)
}

fun Instant.roundedToNearest(increment: LongMicroseconds): Instant {
    return roundedToNearest(increment.inNanoseconds)
}

fun Instant.roundedToNearest(increment: IntNanoseconds): Instant {
    return roundedToNearest(increment.toLongNanoseconds())
}

fun Instant.roundedToNearest(increment: LongNanoseconds): Instant {
    checkRoundingIncrement(increment)
    val incrementSeconds = (increment.value / NANOSECONDS_PER_SECOND).seconds
    val adjustedNanoseconds = (increment % NANOSECONDS_PER_SECOND).toIntNanosecondsUnchecked()
    val remainder = (secondOfUnixEpoch floorMod incrementSeconds.value).seconds +
        (additionalNanosecondsSinceUnixEpoch plusWithOverflow adjustedNanoseconds)

    return if (remainder > 0L.nanoseconds) {
        if (remainder < (incrementSeconds + adjustedNanoseconds) / 2) {
            this - remainder
        } else {
            this + (increment - remainder)
        }
    } else {
        this
    }
}

fun Instant.roundedTo(unit: TimeUnit): Instant {
    return when (unit) {
        DAYS -> roundedToNearest(SECONDS_PER_DAY.seconds)
        HOURS -> roundedToNearest(SECONDS_PER_HOUR.seconds)
        MINUTES -> roundedToNearest(SECONDS_PER_MINUTE.seconds)
        SECONDS -> roundedToNearest(1.seconds)
        MILLISECONDS -> roundedToNearest(NANOSECONDS_PER_MILLISECOND.nanoseconds)
        MICROSECONDS -> roundedToNearest(NANOSECONDS_PER_MICROSECOND.nanoseconds)
        NANOSECONDS -> this
    }
}