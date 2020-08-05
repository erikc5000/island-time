package io.islandtime.clock

import io.islandtime.*
import io.islandtime.clock.internal.nowImpl
import io.islandtime.internal.deprecatedToError

/**
 * Gets the current [Instant] from the system clock.
 */
fun Instant.Companion.now(): Instant = now(SystemClock.UTC)

/**
 * Gets the current [Instant] from the provided [clock].
 */
fun Instant.Companion.now(clock: Clock): Instant = clock.readInstant()

/**
 * Gets the current [Year] from the system clock.
 */
fun Year.Companion.now(): Year = now(SystemClock())

/**
 * Gets the current [Year] from the provided [clock].
 */
fun Year.Companion.now(clock: Clock): Year = Date.now(clock).toYear()

/**
 * Gets the current [YearMonth] from the system clock.
 */
fun YearMonth.Companion.now(): YearMonth = now(SystemClock())

/**
 * Gets the current [YearMonth] from the provided [clock].
 */
fun YearMonth.Companion.now(clock: Clock): YearMonth = Date.now(clock).toYearMonth()

/**
 * Gets the current [Date] from the system clock.
 */
fun Date.Companion.now(): Date = nowImpl(SystemClock())

/**
 * Gets the current [Date] from the provided [clock].
 */
fun Date.Companion.now(clock: Clock): Date = nowImpl(clock)

/**
 * Gets the current [DateTime] from the system clock.
 */
fun DateTime.Companion.now(): DateTime = nowImpl(SystemClock())

/**
 * Gets the current [DateTime] from the provided [clock].
 */
fun DateTime.Companion.now(clock: Clock): DateTime = nowImpl(clock)

/**
 * Gets the current [OffsetDateTime] from the system clock.
 */
fun OffsetDateTime.Companion.now(): OffsetDateTime = nowImpl(SystemClock())

/**
 * Gets the current [OffsetDateTime] from the provided [clock].
 */
fun OffsetDateTime.Companion.now(clock: Clock): OffsetDateTime = nowImpl(clock)

/**
 * Gets the current [ZonedDateTime] from the system clock.
 */
fun ZonedDateTime.Companion.now(): ZonedDateTime = nowImpl(SystemClock())

/**
 * Gets the current [ZonedDateTime] from the provided [clock].
 */
fun ZonedDateTime.Companion.now(clock: Clock): ZonedDateTime = nowImpl(clock)

/**
 * Gets the current [Time] from the system clock.
 */
fun Time.Companion.now(): Time = nowImpl(SystemClock())

/**
 * Gets the current [Time] from the provided [clock].
 */
fun Time.Companion.now(clock: Clock): Time = nowImpl(clock)

/**
 * Gets the current [OffsetTime] from the system clock.
 */
fun OffsetTime.Companion.now(): OffsetTime = nowImpl(SystemClock())

/**
 * Gets the current [OffsetTime] from the provided [clock].
 */
fun OffsetTime.Companion.now(clock: Clock): OffsetTime = nowImpl(clock)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated(
    "Moved to TimeZone companion object.",
    ReplaceWith("systemDefault()"),
    DeprecationLevel.ERROR
)
fun TimeZone.Companion.systemDefault(): TimeZone = deprecatedToError()