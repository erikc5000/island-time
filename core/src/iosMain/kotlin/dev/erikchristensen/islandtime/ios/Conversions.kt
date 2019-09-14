package dev.erikchristensen.islandtime.ios

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.internal.MILLISECONDS_PER_SECOND
import dev.erikchristensen.islandtime.interval.milliseconds
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

fun NSDate.toIslandInstant(): Instant = Instant((timeIntervalSince1970 * MILLISECONDS_PER_SECOND).toLong().milliseconds)

fun Instant.toNSDate(): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(
        millisecondsSinceUnixEpoch.value.toDouble() / MILLISECONDS_PER_SECOND
    )
}

fun NSTimeZone.toIslandTimeZone(): TimeZone = TimeZone(name)

fun TimeZone.toNSTimeZoneOrNull(): NSTimeZone? = NSTimeZone.timeZoneWithName(regionId)