package io.islandtime.clock.internal

import io.islandtime.*
import io.islandtime.clock.Clock

actual fun Date.Companion.nowImpl(clock: Clock): Date {
    return with(clock) { readPlatformInstant().toDateAt(zone) }
}

actual fun DateTime.Companion.nowImpl(clock: Clock): DateTime {
    return with(clock) { readPlatformInstant().toDateTimeAt(zone) }
}

actual fun OffsetDateTime.Companion.nowImpl(clock: Clock): OffsetDateTime {
    return with(clock) { readPlatformInstant().toOffsetDateTimeAt(zone) }
}

actual fun ZonedDateTime.Companion.nowImpl(clock: Clock): ZonedDateTime {
    return with(clock) { readPlatformInstant().toZonedDateTimeAt(zone) }
}

actual fun Time.Companion.nowImpl(clock: Clock): Time {
    return with(clock) { readPlatformInstant().toTimeAt(zone) }
}

actual fun OffsetTime.Companion.nowImpl(clock: Clock): OffsetTime {
    return with(clock) { readPlatformInstant().toOffsetTimeAt(zone) }
}
