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
 * Return this time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: IntHours): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.toLongHours().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Time.roundedDownToNearest(increment: IntMinutes): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.toLongMinutes().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Time.roundedDownToNearest(increment: IntSeconds): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.toLongSeconds().inNanosecondsUnchecked)
}

/**
 * Return this time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedDownToNearest(increment: IntMilliseconds): Time {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedDownToNearest(increment: IntMicroseconds): Time {
    return roundedDownToNearest(increment.inNanoseconds)
}

/**
 * Return this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedDownToNearest(increment: IntNanoseconds): Time {
    return roundedDownToNearest(increment.toLongNanoseconds())
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
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetTime.roundedDownToNearest(increment: IntMinutes): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetTime.roundedDownToNearest(increment: IntSeconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedDownToNearest(increment: IntMilliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedDownToNearest(increment: IntMicroseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedDownToNearest(increment: IntNanoseconds): OffsetTime {
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
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun DateTime.roundedDownToNearest(increment: IntMinutes): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun DateTime.roundedDownToNearest(increment: IntSeconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedDownToNearest(increment: IntMilliseconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedDownToNearest(increment: IntMicroseconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedDownToNearest(increment: IntNanoseconds): DateTime {
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
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntMinutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntSeconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntMilliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntMicroseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedDownToNearest(increment: IntNanoseconds): OffsetDateTime {
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
 * The [increment] must multiply evenly into an hour.
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
 * The [increment] must multiply evenly into a minute.
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
 * The [increment] must multiply evenly into a second.
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
 * The [increment] must multiply evenly into a second.
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
 * Return this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
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

/**
 * Return this instant, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Instant.roundedDownToNearest(increment: IntHours): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Return this instant, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Instant.roundedDownToNearest(increment: IntMinutes): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Return this instant, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Instant.roundedDownToNearest(increment: IntSeconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment)
}

/**
 * Return this instant, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedDownToNearest(increment: IntMilliseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked((increment.value * NANOSECONDS_PER_MILLISECOND).nanoseconds)
}

/**
 * Return this instant, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedDownToNearest(increment: IntMicroseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked((increment.value * NANOSECONDS_PER_MICROSECOND).nanoseconds)
}

/**
 * Return this instant, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedDownToNearest(increment: IntNanoseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment)
}

/**
 * Return this instant, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [truncatedTo].
 */
fun Instant.roundedDownTo(unit: TimeUnit): Instant {
    return when (unit) {
        DAYS -> roundedDownToNearestUnchecked(24.hours.inSecondsUnchecked)
        HOURS -> roundedDownToNearestUnchecked(1.hours.inSecondsUnchecked)
        MINUTES -> roundedDownToNearestUnchecked(1.minutes.inSecondsUnchecked)
        SECONDS -> roundedDownToNearestUnchecked(1.seconds)
        MILLISECONDS -> roundedDownToNearestUnchecked(1_000_000.nanoseconds)
        MICROSECONDS -> roundedDownToNearestUnchecked(1_000.nanoseconds)
        NANOSECONDS -> this
    }
}

/**
 * Return this instant, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun Instant.truncatedTo(unit: TimeUnit): Instant = roundedDownTo(unit)

private fun Time.roundedDownToNearest(increment: LongNanoseconds): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment)
}

private fun Time.roundedDownToNearestUnchecked(increment: LongNanoseconds): Time {
    val remainder = nanosecondsSinceStartOfDay % increment.value
    return if (remainder.value > 0) this - remainder else this
}

private fun Instant.roundedDownToNearestUnchecked(increment: IntSeconds): Instant {
    val currentSecond = secondOfUnixEpoch
    val remainder = currentSecond floorMod increment.value

    return if (remainder > 0 || nanosecond > 0) {
        Instant.fromSecondOfUnixEpoch(currentSecond - remainder, 0)
    } else {
        this
    }
}

private fun Instant.roundedDownToNearestUnchecked(increment: IntNanoseconds): Instant {
    val remainder = additionalNanosecondsSinceUnixEpoch % increment.value
    return if (remainder.value > 0) this - remainder else this
}