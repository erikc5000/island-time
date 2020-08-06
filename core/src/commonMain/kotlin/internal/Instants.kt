package io.islandtime.internal

import io.islandtime.*

internal fun toTimeAt(offset: UtcOffset, secondOfUnixEpoch: Long, nanosecond: Int): Time {
    val nanosecondOfDay = ((secondOfUnixEpoch % SECONDS_PER_DAY) * NANOSECONDS_PER_SECOND
        + nanosecond + offset.totalSeconds.inNanoseconds.value + NANOSECONDS_PER_DAY) % NANOSECONDS_PER_DAY
    return Time.fromNanosecondOfDay(nanosecondOfDay)
}

internal fun toOffsetTimeAt(offset: UtcOffset, secondOfUnixEpoch: Long, nanosecond: Int): OffsetTime {
    return toTimeAt(offset, secondOfUnixEpoch, nanosecond) at offset
}

internal fun toDateAt(offset: UtcOffset, secondOfUnixEpoch: Long): Date {
    val dayOfUnixEpoch = (secondOfUnixEpoch + offset.totalSeconds.value) floorDiv SECONDS_PER_DAY
    return Date.fromDayOfUnixEpoch(dayOfUnixEpoch)
}

internal fun toDateTimeAt(offset: UtcOffset, secondOfUnixEpoch: Long, nanosecond: Int): DateTime {
    return DateTime.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, offset)
}

internal fun toOffsetDateTimeAt(offset: UtcOffset, secondOfUnixEpoch: Long, nanosecond: Int): OffsetDateTime {
    return OffsetDateTime.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, offset)
}
