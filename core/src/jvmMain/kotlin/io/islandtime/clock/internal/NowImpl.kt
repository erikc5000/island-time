package io.islandtime.clock.internal

import io.islandtime.*
import io.islandtime.clock.Clock

internal actual fun Date.Companion.nowImpl(clock: Clock): Date {
    return with(clock) { readPlatformInstant().toDateAt(zone) }
}

internal actual fun DateTime.Companion.nowImpl(clock: Clock): DateTime {
    return with(clock) { readPlatformInstant().toDateTimeAt(zone) }
}

internal actual fun OffsetDateTime.Companion.nowImpl(clock: Clock): OffsetDateTime {
    return with(clock) { readPlatformInstant().toOffsetDateTimeAt(zone) }
}

internal actual fun ZonedDateTime.Companion.nowImpl(clock: Clock): ZonedDateTime {
    return with(clock) { readPlatformInstant().toZonedDateTimeAt(zone) }
}

internal actual fun Time.Companion.nowImpl(clock: Clock): Time {
    return with(clock) { readPlatformInstant().toTimeAt(zone) }
}

internal actual fun OffsetTime.Companion.nowImpl(clock: Clock): OffsetTime {
    return with(clock) { readPlatformInstant().toOffsetTimeAt(zone) }
}
