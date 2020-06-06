package io.islandtime.operators

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import io.islandtime.operators.internal.*

/**
 * Return this time, rounded up to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: IntHours): Time {
    return roundedUpToNearest(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: IntMinutes): Time {
    return roundedUpToNearest(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: IntSeconds): Time {
    return roundedUpToNearest(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: IntMilliseconds): Time {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: IntMicroseconds): Time {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: LongMicroseconds): Time {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: IntNanoseconds): Time {
    return roundedUpToNearest(increment)
}

/**
 * Return this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedUpToNearest(increment: LongNanoseconds): Time {
    checkRoundingIncrement(increment)
    val remainder = nanosecondsSinceStartOfDay % increment.value
    return if (remainder.value > 0) this + (increment - remainder) else this
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
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: IntMinutes): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: IntSeconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: IntMilliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: IntMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: LongMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: IntNanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedUpToNearest(increment))
}

/**
 * Return this time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedUpToNearest(increment: LongNanoseconds): OffsetTime {
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
    return roundedUpToNearest(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded up to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: IntMinutes): DateTime {
    return roundedUpToNearest(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: IntSeconds): DateTime {
    return roundedUpToNearest(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: IntMilliseconds): DateTime {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: IntMicroseconds): DateTime {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: LongMicroseconds): DateTime {
    return roundedUpToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: IntNanoseconds): DateTime {
    return roundedUpToNearest(increment.toLongNanoseconds())
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedUpToNearest(increment: LongNanoseconds): DateTime {
    val newTime = time.roundedUpToNearest(increment)

    return when {
        newTime == time -> this
        newTime == MIDNIGHT && time > MIDNIGHT -> DateTime(date + 1.days, newTime)
        else -> copy(time = newTime)
    }
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
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntMinutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntSeconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntMilliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: LongMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: IntNanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedUpToNearest(increment: LongNanoseconds): OffsetDateTime {
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * The [increment] must multiply evenly into a 24-hour day.
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
 * Return this date-time, rounded up to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: LongMicroseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedUpToNearest(increment))
}

/**
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
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
 * Return this date-time, rounded up to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedUpToNearest(increment: LongNanoseconds): ZonedDateTime {
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