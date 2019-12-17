package io.islandtime.darwin

import io.islandtime.*
import io.islandtime.base.TimePoint
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.seconds
import io.islandtime.zone.TimeZoneRulesException
import kotlinx.cinterop.convert
import platform.Foundation.*

fun Year.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.year = value.convert()
    }
}

fun YearMonth.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.year = year.convert()
        it.month = monthNumber.convert()
    }
}

fun Date.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(this)
    }
}

fun Time.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(this)
    }
}

fun DateTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(this)
    }
}

fun OffsetTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(time)
        it.timeZone = offset.toNSTimeZone()
    }
}

fun OffsetDateTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(dateTime)
        it.timeZone = offset.toNSTimeZone()
    }
}

fun ZonedDateTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(dateTime)
        it.timeZone = zone.toNSTimeZone()
    }
}

fun NSDateComponents.toIslandDate(): Date {
    return Date(year.convert(), month.convert<Int>(), day.convert())
}

fun NSDateComponents.toIslandTime(): Time {
    return Time(hour.convert(), minute.convert(), second.convert(), nanosecond.convert())
}

fun NSDateComponents.toIslandDateTime(): DateTime {
    return DateTime(toIslandDate(), toIslandTime())
}

fun NSDateComponents.toIslandOffsetDateTime() = toIslandOffsetDateTimeOrNull()
    ?: throw DateTimeException("The 'timeZone' property must be non-null")

fun NSDateComponents.toIslandOffsetDateTimeOrNull(): OffsetDateTime? {
    return timeZone?.let { OffsetDateTime(toIslandDateTime(), it.toIslandUtcOffset()) }
}

fun NSDateComponents.toIslandZonedDateTime() = toIslandZonedDateTimeOrNull()
    ?: throw TimeZoneRulesException("The 'timeZone' property must be non-null")

fun NSDateComponents.toIslandZonedDateTimeOrNull(): ZonedDateTime? {
    return timeZone?.let { ZonedDateTime(toIslandDateTime(), it.toIslandTimeZone()) }
}

fun <T> TimePoint<T>.toNSDate(): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(
        unixEpochSecond.toDouble() + unixEpochNanoOfSecond.toDouble() / NANOSECONDS_PER_SECOND
    )
}

fun NSDate.toIslandInstant(): Instant {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return Instant.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond)
}

fun NSDate.toIslandDateTimeAt(offset: UtcOffset): DateTime {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return DateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, offset)
}

fun NSDate.toIslandDateTimeAt(nsTimeZone: NSTimeZone) = toIslandDateTimeAt(nsTimeZone.toIslandUtcOffset())

fun NSDate.toIslandOffsetDateTimeAt(offset: UtcOffset): OffsetDateTime {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return OffsetDateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, offset)
}

fun NSDate.toIslandOffsetDateTimeAt(nsTimeZone: NSTimeZone) = toIslandOffsetDateTimeAt(nsTimeZone.toIslandUtcOffset())

fun NSDate.toIslandZonedDateTimeAt(zone: TimeZone): ZonedDateTime {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return ZonedDateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, zone)
}

fun NSDate.toIslandZonedDateTimeAt(nsTimeZone: NSTimeZone) = toIslandZonedDateTimeAt(nsTimeZone.toIslandTimeZone())

fun UtcOffset.toNSTimeZone(): NSTimeZone = NSTimeZone.timeZoneForSecondsFromGMT(totalSeconds.value.convert())

fun NSTimeZone.toIslandUtcOffset(): UtcOffset = UtcOffset(secondsFromGMT.convert<Int>().seconds)

/**
 * Convert an Apple `NSTimeZone` to an Island Time [TimeZone] with the same identifier.
 */
fun NSTimeZone.toIslandTimeZone(): TimeZone = name.toTimeZone()

/**
 * Convert an Island Time [TimeZone] to an Apple `NSTimeZone`.
 *
 * @throws TimeZoneRulesException if the identifier isn't recognized as valid for an [NSTimeZone]
 */
fun TimeZone.toNSTimeZone(): NSTimeZone = toNSTimeZoneOrNull()
    ?: throw TimeZoneRulesException("The identifier '$id' could not be converted to an NSTimeZone")

/**
 * Convert an Island Time [TimeZone] to an Apple [NSTimeZone] or `null` if the identifier isn't recognized as valid for
 * an [NSTimeZone].
 */
fun TimeZone.toNSTimeZoneOrNull(): NSTimeZone? {
    return if (this is TimeZone.FixedOffset) {
        NSTimeZone.timeZoneForSecondsFromGMT(offset.totalSeconds.value.convert())
    } else {
        NSTimeZone.timeZoneWithName(id)
    }
}

private fun NSDateComponents.populateFrom(date: Date) {
    year = date.year.convert()
    month = date.monthNumber.convert()
    day = date.dayOfMonth.convert()
}

private fun NSDateComponents.populateFrom(time: Time) {
    hour = time.hour.convert()
    minute = time.minute.convert()
    second = time.second.convert()
    nanosecond = time.nanosecond.convert()
}

private fun NSDateComponents.populateFrom(dateTime: DateTime) {
    populateFrom(dateTime.date)
    populateFrom(dateTime.time)
}