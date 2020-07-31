package io.islandtime.internal

import io.islandtime.*

internal fun PlatformInstant.toTimeAt(zone: TimeZone): Time {
    val offset = zone.rules.offsetAt(this)
    val nanosecondOfDay = ((secondOfUnixEpoch % SECONDS_PER_DAY) * NANOSECONDS_PER_SECOND
        + nanosecond + offset.totalSeconds.inNanoseconds.value + NANOSECONDS_PER_DAY) % NANOSECONDS_PER_DAY
    return Time.fromNanosecondOfDay(nanosecondOfDay)
}

internal fun PlatformInstant.toOffsetTimeAt(zone: TimeZone): OffsetTime {
    val offset = zone.rules.offsetAt(this)
    val nanosecondOfDay = ((secondOfUnixEpoch % SECONDS_PER_DAY) * NANOSECONDS_PER_SECOND
        + nanosecond + offset.totalSeconds.inNanoseconds.value + NANOSECONDS_PER_DAY) % NANOSECONDS_PER_DAY
    return Time.fromNanosecondOfDay(nanosecondOfDay) at offset
}

internal fun PlatformInstant.toDateAt(zone: TimeZone): Date {
    val offset = zone.rules.offsetAt(this)
    val dayOfUnixEpoch = (secondOfUnixEpoch + offset.totalSeconds.value) floorDiv SECONDS_PER_DAY
    return Date.fromDayOfUnixEpoch(dayOfUnixEpoch)
}

internal fun PlatformInstant.toDateTimeAt(zone: TimeZone): DateTime {
    val offset = zone.rules.offsetAt(this)
    return DateTime.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, offset)
}

internal fun PlatformInstant.toOffsetDateTimeAt(zone: TimeZone): OffsetDateTime {
    val offset = zone.rules.offsetAt(this)
    return OffsetDateTime.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, offset)
}

internal fun PlatformInstant.toZonedDateTimeAt(zone: TimeZone): ZonedDateTime {
    val offset = zone.rules.offsetAt(this)
    val dateTime = DateTime.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, offset)
    return ZonedDateTime.create(dateTime, offset, zone)
}
