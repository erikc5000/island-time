package io.islandtime.ios

import io.islandtime.*
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.interval.LongMilliseconds
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

val NSDate.millisecondsSinceUnixEpoch: LongMilliseconds
    get() = (timeIntervalSince1970 * MILLISECONDS_PER_SECOND).toLong().milliseconds

fun NSDate.Companion.fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(milliseconds.value.toDouble() / MILLISECONDS_PER_SECOND)
}

fun Instant.toNSDate(): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(
        secondsSinceUnixEpoch.value.toDouble() + nanoOfSecondsSinceUnixEpoch.value.toDouble() / NANOSECONDS_PER_SECOND
    )
}

fun NSTimeZone.toIslandTimeZone(): TimeZone = TimeZone(name)

fun TimeZone.toNSTimeZoneOrNull(): NSTimeZone? = NSTimeZone.timeZoneWithName(regionId)