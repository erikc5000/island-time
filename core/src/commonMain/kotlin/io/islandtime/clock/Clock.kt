@file:Suppress("FunctionName", "UNUSED_PARAMETER")

package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.internal.deprecatedToError
import io.islandtime.measures.*

/**
 * An abstraction providing the current time.
 *
 * For an implementation that uses the system's clock, see [SystemClock]. [FixedClock] is also available for testing
 * purposes.
 *
 * @see SystemClock
 * @see FixedClock
 */
interface Clock {
    /**
     * The time zone of this clock.
     */
    val zone: TimeZone

    /**
     * Reads the current number of milliseconds that have elapsed since the Unix epoch of `1970-01-01T00:00` in UTC.
     */
    fun readMilliseconds(): LongMilliseconds

    /**
     * Reads the current [Instant].
     */
    fun readInstant(): Instant

    /**
     * Reads the current [PlatformInstant].
     */
    fun readPlatformInstant(): PlatformInstant
}

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(days.inSeconds), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(days: LongDays, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(days.toLong().inSeconds), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(days: IntDays, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(hours.inSeconds), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(hours: LongHours, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(hours.toLong().inSeconds), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(hours: IntHours, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(minutes.inSeconds), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(minutes: LongMinutes, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(minutes.toLong().inSeconds), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(minutes: IntMinutes, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(seconds), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(seconds: LongSeconds, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(seconds.toLongSeconds()), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(seconds: IntSeconds, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()

@Deprecated(
    message = "Specify the instant explicitly.",
    replaceWith = ReplaceWith(
        "FixedClock(Instant(milliseconds.toLongMilliseconds()), zone)",
        "io.islandtime.Instant"
    ),
    level = DeprecationLevel.ERROR
)
fun FixedClock(milliseconds: IntMilliseconds, zone: TimeZone = TimeZone.UTC): FixedClock = deprecatedToError()
