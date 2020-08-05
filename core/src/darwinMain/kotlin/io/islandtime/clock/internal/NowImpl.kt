package io.islandtime.clock.internal

import io.islandtime.*
import io.islandtime.clock.Clock
import io.islandtime.internal.*

actual fun Date.Companion.nowImpl(clock: Clock): Date {
    return with(clock) { readInstant().toDateAt(zone) }
}

actual fun DateTime.Companion.nowImpl(clock: Clock): DateTime {
    return with(clock) { readInstant().toDateTimeAt(zone) }
}

actual fun OffsetDateTime.Companion.nowImpl(clock: Clock): OffsetDateTime {
    return with(clock) { readInstant().toOffsetDateTimeAt(zone) }
}

actual fun ZonedDateTime.Companion.nowImpl(clock: Clock): ZonedDateTime {
    return with(clock) { readInstant() at zone }
}

actual fun Time.Companion.nowImpl(clock: Clock): Time {
    return with(clock) { readInstant().toTimeAt(zone) }
}

actual fun OffsetTime.Companion.nowImpl(clock: Clock): OffsetTime {
    return with(clock) { readInstant().toOffsetTimeAt(zone) }
}

private fun Instant.toOffsetDateTimeAt(zone: TimeZone): OffsetDateTime {
    return this at zone.rules.offsetAt(this)
}

private fun Instant.toTimeAt(offset: UtcOffset): Time = toTimeAt(offset, secondOfUnixEpoch, nanosecond)
private fun Instant.toTimeAt(zone: TimeZone): Time = toTimeAt(zone.rules.offsetAt(this))

private fun Instant.toOffsetTimeAt(offset: UtcOffset): OffsetTime =
    toOffsetTimeAt(offset, secondOfUnixEpoch, nanosecond)

private fun Instant.toOffsetTimeAt(zone: TimeZone): OffsetTime = toOffsetTimeAt(zone.rules.offsetAt(this))