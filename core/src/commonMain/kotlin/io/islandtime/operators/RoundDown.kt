package io.islandtime.operators

import io.islandtime.*
import io.islandtime.Time.Companion.MIDNIGHT
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import io.islandtime.operators.internal.*

/**
 * Return this time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: IntHours): Time {
    return roundedDownToNearest(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: IntMinutes): Time {
    return roundedDownToNearest(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: IntSeconds): Time {
    return roundedDownToNearest(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: IntMilliseconds): Time {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: IntMicroseconds): Time {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: LongMicroseconds): Time {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: IntNanoseconds): Time {
    return roundedDownToNearest(increment)
}

/**
 * Return this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: LongNanoseconds): Time {
    checkRoundingIncrement(increment)
    val remainder = nanosecondsSinceStartOfDay % increment.value
    return if (remainder.value > 0) this - remainder else this
}

/**
 * Return this time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [truncatedTo].
 */
fun Time.roundedDownTo(unit: TimeUnit): Time {
    return when (unit) {
        DAYS -> MIDNIGHT
        HOURS -> previousWholeHour()
        MINUTES -> previousWholeMinute()
        SECONDS -> previousWholeSecond()
        MILLISECONDS -> previousWholeMillisecond()
        MICROSECONDS -> previousWholeMicrosecond()
        NANOSECONDS -> this
    }
}

/**
 * Return this time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun Time.truncatedTo(unit: TimeUnit): Time = roundedDownTo(unit)

/**
 * Return this time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: IntHours): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: IntMinutes): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: IntSeconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: IntMilliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: IntMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: LongMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: IntNanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: LongNanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [truncatedTo].
 */
fun OffsetTime.roundedDownTo(unit: TimeUnit): OffsetTime {
    return copyIfChanged(time = time.roundedDownTo(unit))
}

/**
 * Return this time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun OffsetTime.truncatedTo(unit: TimeUnit): OffsetTime = roundedDownTo(unit)

/**
 * Return this date-time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: IntHours): DateTime {
    return roundedDownToNearest(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: IntMinutes): DateTime {
    return roundedDownToNearest(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: IntSeconds): DateTime {
    return roundedDownToNearest(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: IntMilliseconds): DateTime {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: IntMicroseconds): DateTime {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: LongMicroseconds): DateTime {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: IntNanoseconds): DateTime {
    return roundedDownToNearest(increment.toLongNanoseconds())
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: LongNanoseconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [truncatedTo].
 */
fun DateTime.roundedDownTo(unit: TimeUnit): DateTime {
    return when (unit) {
        DAYS -> date.startOfDay
        HOURS -> previousWholeHour()
        MINUTES -> previousWholeMinute()
        SECONDS -> previousWholeSecond()
        MILLISECONDS -> previousWholeMillisecond()
        MICROSECONDS -> previousWholeMicrosecond()
        NANOSECONDS -> this
    }
}

/**
 * Return this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun DateTime.truncatedTo(unit: TimeUnit): DateTime = roundedDownTo(unit)

/**
 * Return this date-time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntHours): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntMinutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntSeconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntMilliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: LongMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntNanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: LongNanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [truncatedTo].
 */
fun OffsetDateTime.roundedDownTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownTo(unit))
}

/**
 * Return this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun OffsetDateTime.truncatedTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownTo(unit))
}

/**
 * Return this date-time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: IntHours): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: IntMinutes): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: IntSeconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: IntMilliseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: IntMicroseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: LongMicroseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: IntNanoseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: LongNanoseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to match the precision of a given [unit].
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 *
 * This is equivalent to [truncatedTo].
 */
fun ZonedDateTime.roundedDownTo(unit: TimeUnit): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownTo(unit))
}

/**
 * Return this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun ZonedDateTime.truncatedTo(unit: TimeUnit): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownTo(unit))
}