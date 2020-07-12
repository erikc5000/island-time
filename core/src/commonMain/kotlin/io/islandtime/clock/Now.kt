package io.islandtime.clock

import io.islandtime.*
import io.islandtime.internal.*
import io.islandtime.internal.MILLISECONDS_PER_DAY
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.SECONDS_PER_DAY
import io.islandtime.internal.floorDiv
import io.islandtime.measures.nanoseconds

/**
 * Get the current [Instant] from the system clock.
 */
fun Instant.Companion.now() = now(SystemClock.UTC)

/**
 * Get the current [Instant] from the specified clock.
 */
fun Instant.Companion.now(clock: Clock) = clock.instant()

/**
 * Get the current [Year] from the system clock.
 */
fun Year.Companion.now() = now(SystemClock())

/**
 * Get the current [Year] from the specified clock.
 */
fun Year.Companion.now(clock: Clock) = Date.now(clock).toYear()

/**
 * Get the current [YearMonth] from the system clock.
 */
fun YearMonth.Companion.now() = now(SystemClock())

/**
 * Get the current [YearMonth] from the specified clock.
 */
fun YearMonth.Companion.now(clock: Clock) = Date.now(clock).toYearMonth()

/**
 * Get the current [Date] from the system clock.
 */
fun Date.Companion.now() = now(SystemClock())

/**
 * Get the current [Date] from the specified clock.
 */
fun Date.Companion.now(clock: Clock): Date {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)
    val unixEpochSecond = (milliseconds.value floorDiv MILLISECONDS_PER_SECOND) + offset.totalSeconds.value
    val unixEpochDay = unixEpochSecond floorDiv SECONDS_PER_DAY
    return fromDayOfUnixEpoch(unixEpochDay)
}

/**
 * Get the current [DateTime] from the system clock.
 */
fun DateTime.Companion.now() = now(SystemClock())

/**
 * Get the current [DateTime] from the specified clock.
 */
fun DateTime.Companion.now(clock: Clock): DateTime {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)
    return fromMillisecondsSinceUnixEpoch(milliseconds, offset)
}

/**
 * Get the current [OffsetDateTime] from the system clock.
 */
fun OffsetDateTime.Companion.now() = now(SystemClock())

/**
 * Get the current [OffsetDateTime] from the specified clock.
 */
fun OffsetDateTime.Companion.now(clock: Clock): OffsetDateTime {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)
    return DateTime.fromMillisecondsSinceUnixEpoch(milliseconds, offset) at offset
}

/**
 * Get the current [OffsetTime] from the system clock.
 */
fun OffsetTime.Companion.now() = now(SystemClock())

/**
 * Get the current [OffsetTime] from the specified clock.
 */
fun OffsetTime.Companion.now(clock: Clock): OffsetTime {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)

    val nanosecondsSinceStartOfDay = ((milliseconds % MILLISECONDS_PER_DAY).inNanoseconds +
        offset.totalSeconds + NANOSECONDS_PER_DAY.nanoseconds) % NANOSECONDS_PER_DAY

    return Time.fromNanosecondOfDay(nanosecondsSinceStartOfDay.value) at offset
}

/**
 * Get the current [Time] from the system clock.
 */
fun Time.Companion.now() = now(SystemClock())

/**
 * Get the current [Time] from the specified clock.
 */
fun Time.Companion.now(clock: Clock): Time {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)

    val nanosecondsSinceStartOfDay = ((milliseconds % MILLISECONDS_PER_DAY).inNanoseconds +
        offset.totalSeconds + NANOSECONDS_PER_DAY.nanoseconds) % NANOSECONDS_PER_DAY

    return fromNanosecondOfDay(nanosecondsSinceStartOfDay.value)
}

/**
 * Get the current [ZonedDateTime] from the system clock.
 */
fun ZonedDateTime.Companion.now() = now(SystemClock())

/**
 * Get the current [ZonedDateTime] from the specified clock.
 */
fun ZonedDateTime.Companion.now(clock: Clock): ZonedDateTime {
    return fromMillisecondsSinceUnixEpoch(clock.read(), clock.zone)
}

/**
 * Get the system clock's current [TimeZone].
 */
fun TimeZone.Companion.systemDefault() = SystemClock.currentZone()