package io.islandtime.clock.internal

import io.islandtime.*
import io.islandtime.internal.*

internal fun java.time.Instant.toTimeAt(offset: UtcOffset): Time = toTimeAt(offset, epochSecond, nano)
internal fun java.time.Instant.toTimeAt(zone: TimeZone): Time = toTimeAt(zone.rules.offsetAt(this))

internal fun java.time.Instant.toOffsetTimeAt(offset: UtcOffset): OffsetTime {
    return toOffsetTimeAt(offset, epochSecond, nano)
}

internal fun java.time.Instant.toOffsetTimeAt(zone: TimeZone): OffsetTime {
    return toOffsetTimeAt(zone.rules.offsetAt(this))
}

internal fun java.time.Instant.toDateAt(offset: UtcOffset): Date = toDateAt(offset, epochSecond)
internal fun java.time.Instant.toDateAt(zone: TimeZone): Date = toDateAt(zone.rules.offsetAt(this))

internal fun java.time.Instant.toDateTimeAt(offset: UtcOffset): DateTime = toDateTimeAt(offset, epochSecond, nano)
internal fun java.time.Instant.toDateTimeAt(zone: TimeZone): DateTime = toDateTimeAt(zone.rules.offsetAt(this))

internal fun java.time.Instant.toOffsetDateTimeAt(offset: UtcOffset): OffsetDateTime {
    return toOffsetDateTimeAt(offset, epochSecond, nano)
}

internal fun java.time.Instant.toOffsetDateTimeAt(zone: TimeZone): OffsetDateTime {
    return toOffsetDateTimeAt(zone.rules.offsetAt(this))
}

internal fun java.time.Instant.toZonedDateTimeAt(zone: TimeZone): ZonedDateTime {
    val offset = zone.rules.offsetAt(this)
    val dateTime = DateTime.fromSecondOfUnixEpoch(epochSecond, nano, offset)
    return ZonedDateTime.create(dateTime, offset, zone)
}
