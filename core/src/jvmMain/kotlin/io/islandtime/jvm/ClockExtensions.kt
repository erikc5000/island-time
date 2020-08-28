package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.clock.internal.*
import io.islandtime.internal.toIslandInstant
import java.time.Clock as JavaClock

/**
 * Gets the current [Instant] from the provided [clock].
 */
fun Instant.Companion.now(clock: JavaClock): Instant = clock.instant().toIslandInstant()

/**
 * Gets the current [Year] from the provided [clock].
 */
fun Year.Companion.now(clock: JavaClock): Year = Date.now(clock).toYear()

/**
 * Gets the current [YearMonth] from the provided [clock].
 */
fun YearMonth.Companion.now(clock: JavaClock): YearMonth = Date.now(clock).toYearMonth()

/**
 * Gets the current [Date] from the provided [clock].
 */
fun Date.Companion.now(clock: JavaClock): Date {
    return with(clock) { instant().toDateAt(zone.toIslandTimeZone()) }
}

/**
 * Gets the current [DateTime] from the provided [clock].
 */
fun DateTime.Companion.now(clock: JavaClock): DateTime {
    return with(clock) { instant().toDateTimeAt(zone.toIslandTimeZone()) }
}

/**
 * Gets the current [OffsetDateTime] from the provided [clock].
 */
fun OffsetDateTime.Companion.now(clock: JavaClock): OffsetDateTime {
    return with(clock) { instant().toOffsetDateTimeAt(zone.toIslandTimeZone()) }
}

/**
 * Gets the current [ZonedDateTime] from the provided [clock].
 */
fun ZonedDateTime.Companion.now(clock: JavaClock): ZonedDateTime {
    return with(clock) { instant().toZonedDateTimeAt(zone.toIslandTimeZone()) }
}

/**
 * Gets the current [Time] from the provided [clock].
 */
fun Time.Companion.now(clock: JavaClock): Time {
    return with(clock) { instant().toTimeAt(zone.toIslandTimeZone()) }
}

/**
 * Gets the current [OffsetTime] from the provided [clock].
 */
fun OffsetTime.Companion.now(clock: JavaClock): OffsetTime {
    return with(clock) { instant().toOffsetTimeAt(zone.toIslandTimeZone()) }
}
