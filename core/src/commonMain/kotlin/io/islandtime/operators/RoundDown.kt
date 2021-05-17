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
 * Returns this time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Time.roundedDownToNearest(increment: Hours): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Time.roundedDownToNearest(increment: Minutes): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Time.roundedDownToNearest(increment: Seconds): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedDownToNearest(increment: Milliseconds): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedDownToNearest(increment: Microseconds): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inNanosecondsUnchecked)
}

/**
 * Returns this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Time.roundedDownToNearest(increment: Nanoseconds): Time {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment)
}

/**
 * Returns this time, rounded down to match the precision of a given [unit].
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
 * Returns this time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun Time.truncatedTo(unit: TimeUnit): Time = roundedDownTo(unit)

/**
 * Returns this time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetTime.roundedDownToNearest(increment: Hours): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetTime.roundedDownToNearest(increment: Minutes): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetTime.roundedDownToNearest(increment: Seconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedDownToNearest(increment: Milliseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedDownToNearest(increment: Microseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetTime.roundedDownToNearest(increment: Nanoseconds): OffsetTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [truncatedTo].
 */
fun OffsetTime.roundedDownTo(unit: TimeUnit): OffsetTime {
    return copyIfChanged(time = time.roundedDownTo(unit))
}

/**
 * Returns this time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun OffsetTime.truncatedTo(unit: TimeUnit): OffsetTime = roundedDownTo(unit)

/**
 * Returns this date-time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun DateTime.roundedDownToNearest(increment: Hours): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun DateTime.roundedDownToNearest(increment: Minutes): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun DateTime.roundedDownToNearest(increment: Seconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedDownToNearest(increment: Milliseconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedDownToNearest(increment: Microseconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun DateTime.roundedDownToNearest(increment: Nanoseconds): DateTime {
    return copyIfChanged(time = time.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to match the precision of a given [unit].
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
 * Returns this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun DateTime.truncatedTo(unit: TimeUnit): DateTime = roundedDownTo(unit)

/**
 * Returns this date-time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun OffsetDateTime.roundedDownToNearest(increment: Hours): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun OffsetDateTime.roundedDownToNearest(increment: Minutes): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun OffsetDateTime.roundedDownToNearest(increment: Seconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedDownToNearest(increment: Milliseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedDownToNearest(increment: Microseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun OffsetDateTime.roundedDownToNearest(increment: Nanoseconds): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [truncatedTo].
 */
fun OffsetDateTime.roundedDownTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownTo(unit))
}

/**
 * Returns this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun OffsetDateTime.truncatedTo(unit: TimeUnit): OffsetDateTime {
    return copyIfChanged(dateTime.roundedDownTo(unit))
}

/**
 * Returns this date-time, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: Hours): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: Minutes): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: Seconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: Milliseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: Microseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the new local time falls within
 * a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the offset will be retained if possible. Otherwise, the earlier offset will be
 * used.
 */
fun ZonedDateTime.roundedDownToNearest(increment: Nanoseconds): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownToNearest(increment))
}

/**
 * Returns this date-time, rounded down to match the precision of a given [unit].
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
 * Returns this date-time, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun ZonedDateTime.truncatedTo(unit: TimeUnit): ZonedDateTime {
    return copyIfChanged(dateTime.roundedDownTo(unit))
}

/**
 * Returns this instant, rounded down to the nearest hour that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a 24-hour day.
 */
fun Instant.roundedDownToNearest(increment: Hours): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Returns this instant, rounded down to the nearest minute that satisfies the [increment].
 *
 * The [increment] must multiply evenly into an hour.
 */
fun Instant.roundedDownToNearest(increment: Minutes): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment.inSecondsUnchecked)
}

/**
 * Returns this instant, rounded down to the nearest second that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a minute.
 */
fun Instant.roundedDownToNearest(increment: Seconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment)
}

/**
 * Returns this instant, rounded down to the nearest millisecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedDownToNearest(increment: Milliseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked((increment.value * NANOSECONDS_PER_MILLISECOND).nanoseconds)
}

/**
 * Returns this instant, rounded down to the nearest microsecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedDownToNearest(increment: Microseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked((increment.value * NANOSECONDS_PER_MICROSECOND).nanoseconds)
}

/**
 * Returns this instant, rounded down to the nearest nanosecond that satisfies the [increment].
 *
 * The [increment] must multiply evenly into a second.
 */
fun Instant.roundedDownToNearest(increment: Nanoseconds): Instant {
    checkRoundingIncrement(increment)
    return roundedDownToNearestUnchecked(increment)
}

/**
 * Returns this instant, rounded down to match the precision of a given [unit].
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
 * Returns this instant, rounded down to match the precision of a given [unit].
 *
 * This is equivalent to [roundedDownTo].
 */
fun Instant.truncatedTo(unit: TimeUnit): Instant = roundedDownTo(unit)

private fun Time.roundedDownToNearestUnchecked(increment: Nanoseconds): Time {
    val remainder = nanosecondsSinceStartOfDay % increment.value
    return if (remainder.value > 0) this - remainder else this
}

private fun Instant.roundedDownToNearestUnchecked(increment: Seconds): Instant {
    val currentSecond = secondOfUnixEpoch
    val remainder = currentSecond floorMod increment.value

    return if (remainder > 0 || nanosecond > 0) {
        Instant.fromSecondOfUnixEpoch(currentSecond - remainder, 0)
    } else {
        this
    }
}

private fun Instant.roundedDownToNearestUnchecked(increment: Nanoseconds): Instant {
    val remainder = additionalNanosecondsSinceUnixEpoch % increment.value
    return if (remainder.value > 0) this - remainder else this
}
