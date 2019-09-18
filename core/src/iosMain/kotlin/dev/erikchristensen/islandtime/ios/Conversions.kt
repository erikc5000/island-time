package dev.erikchristensen.islandtime.ios

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_SECOND
import dev.erikchristensen.islandtime.interval.seconds
import dev.erikchristensen.islandtime.interval.nanoseconds
import platform.Foundation.*

fun DateTime.toNSDateComponents(): NSDateComponents {
    return NSDateComponents().also {
        it.year = year.toLong()
        it.month = monthNumber.toLong()
        it.day = dayOfMonth.toLong()
        it.hour = hour.toLong()
        it.minute = minute.toLong()
        it.second = second.toLong()
        it.nanosecond = nanosecond.toLong()
    }
}

fun NSDate.toIslandInstant(): Instant {
    val seconds = timeIntervalSince1970.toLong().seconds
    val nanoseconds = ((timeIntervalSince1970 - seconds.value) * NANOSECONDS_PER_SECOND).toInt().nanoseconds

    return Instant.fromSecondsSinceUnixEpoch(seconds, nanoseconds)
}

fun Instant.toNSDate(): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(
        secondsSinceUnixEpoch.value.toDouble() + nanosecondAdjustment.value.toDouble() / NANOSECONDS_PER_SECOND
    )
}

fun NSTimeZone.toIslandTimeZone(): TimeZone = TimeZone(name)

fun TimeZone.toNSTimeZoneOrNull(): NSTimeZone? = NSTimeZone.timeZoneWithName(regionId)