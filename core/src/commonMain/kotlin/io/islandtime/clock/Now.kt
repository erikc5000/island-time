package io.islandtime.clock

import io.islandtime.*
import io.islandtime.internal.*

/**
 * Gets the current [Instant] from the system clock.
 */
fun Instant.Companion.now() = now(SystemClock.UTC)

/**
 * Gets the current [Instant] from the provided [clock].
 */
fun Instant.Companion.now(clock: Clock) = clock.readInstant()

/**
 * Gets the current [Year] from the system clock.
 */
fun Year.Companion.now() = now(SystemClock())

/**
 * Gets the current [Year] from the provided [clock].
 */
fun Year.Companion.now(clock: Clock) = Date.now(clock).toYear()

/**
 * Gets the current [YearMonth] from the system clock.
 */
fun YearMonth.Companion.now() = now(SystemClock())

/**
 * Gets the current [YearMonth] from the provided [clock].
 */
fun YearMonth.Companion.now(clock: Clock) = Date.now(clock).toYearMonth()

/**
 * Gets the current [Date] from the system clock.
 */
fun Date.Companion.now() = now(SystemClock())

/**
 * Gets the current [Date] from the provided [clock].
 */
fun Date.Companion.now(clock: Clock): Date {
    return with(clock) { readPlatformInstant().toDateAt(zone) }
}

/**
 * Gets the current [DateTime] from the system clock.
 */
fun DateTime.Companion.now() = now(SystemClock())

/**
 * Gets the current [DateTime] from the provided [clock].
 */
fun DateTime.Companion.now(clock: Clock): DateTime {
    return with(clock) { readPlatformInstant().toDateTimeAt(zone) }
}

/**
 * Gets the current [OffsetDateTime] from the system clock.
 */
fun OffsetDateTime.Companion.now() = now(SystemClock())

/**
 * Gets the current [OffsetDateTime] from the provided [clock].
 */
fun OffsetDateTime.Companion.now(clock: Clock): OffsetDateTime {
    return with(clock) { readPlatformInstant().toOffsetDateTimeAt(zone) }
}

/**
 * Gets the current [ZonedDateTime] from the system clock.
 */
fun ZonedDateTime.Companion.now() = now(SystemClock())

/**
 * Gets the current [ZonedDateTime] from the provided [clock].
 */
fun ZonedDateTime.Companion.now(clock: Clock): ZonedDateTime {
    return with(clock) { readPlatformInstant().toZonedDateTimeAt(zone) }
}

/**
 * Gets the current [Time] from the system clock.
 */
fun Time.Companion.now() = now(SystemClock())

/**
 * Gets the current [Time] from the provided [clock].
 */
fun Time.Companion.now(clock: Clock): Time {
    return with(clock) { readPlatformInstant().toTimeAt(zone) }
}

/**
 * Gets the current [OffsetTime] from the system clock.
 */
fun OffsetTime.Companion.now() = now(SystemClock())

/**
 * Gets the current [OffsetTime] from the provided [clock].
 */
fun OffsetTime.Companion.now(clock: Clock): OffsetTime {
    return with(clock) { readPlatformInstant().toOffsetTimeAt(zone) }
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated(
    "Moved to TimeZone companion object.",
    ReplaceWith("systemDefault()"),
    DeprecationLevel.ERROR
)
fun TimeZone.Companion.systemDefault(): TimeZone = deprecatedToError()