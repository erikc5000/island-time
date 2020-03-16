package io.islandtime.darwin

import io.islandtime.*
import io.islandtime.base.TimePoint
import io.islandtime.internal.MICROSECONDS_PER_SECOND
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.*
import io.islandtime.ranges.InstantInterval
import io.islandtime.ranges.TimePointInterval
import io.islandtime.ranges.until
import io.islandtime.zone.TimeZoneRulesException
import kotlinx.cinterop.convert
import platform.Foundation.*

/**
 * Convert an Island Time [Year] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun Year.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.year = value.convert()
    }
}

/**
 * Convert an Island Time [YearMonth] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun YearMonth.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.year = year.convert()
        it.month = monthNumber.convert()
    }
}

/**
 * Convert an Island Time [Date] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun Date.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(this)
    }
}

/**
 * Convert an Island Time [Time] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun Time.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(this)
    }
}

/**
 * Convert an Island Time [DateTime] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun DateTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(this)
    }
}

/**
 * Convert an Island Time [OffsetTime] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun OffsetTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(time)
        it.timeZone = offset.toNSTimeZone()
    }
}

/**
 * Convert an Island Time [OffsetDateTime] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun OffsetDateTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(dateTime)
        it.timeZone = offset.toNSTimeZone()
    }
}

/**
 * Convert an Island Time [ZonedDateTime] to an equivalent `NSDateComponents` object.
 * @param includeCalendar `true` if the resulting `NSDateComponents` should include the ISO-8601 calendar
 * @return an equivalent `NSDateComponents` object
 */
fun ZonedDateTime.toNSDateComponents(includeCalendar: Boolean = false): NSDateComponents {
    return NSDateComponents().also {
        if (includeCalendar) it.calendar = NSCalendar(NSCalendarIdentifierISO8601)
        it.populateFrom(dateTime)
        it.timeZone = zone.toNSTimeZone()
    }
}

/**
 * Convert an `NSDateComponents` object to an Island Time [Date].
 */
fun NSDateComponents.toIslandDate(): Date {
    return Date(year.convert(), month.convert<Int>(), day.convert())
}

/**
 * Convert an `NSDateComponents` object to an Island Time [Time].
 */
fun NSDateComponents.toIslandTime(): Time {
    return Time(hour.convert(), minute.convert(), second.convert(), nanosecond.convert())
}

/**
 * Convert an `NSDateComponents` object to an Island Time [DateTime].
 */
fun NSDateComponents.toIslandDateTime(): DateTime {
    return DateTime(toIslandDate(), toIslandTime())
}

/**
 * Convert an `NSDateComponents` object to an Island Time [OffsetDateTime].
 * @throws DateTimeException if the `timeZone` property is absent.
 */
fun NSDateComponents.toIslandOffsetDateTime() = toIslandOffsetDateTimeOrNull()
    ?: throw DateTimeException("The 'timeZone' property must be non-null")

/**
 * Convert an `NSDateComponents` object to an Island Time [OffsetDateTime] or `null` if the `timeZone` property is
 * absent.
 */
fun NSDateComponents.toIslandOffsetDateTimeOrNull(): OffsetDateTime? {
    return timeZone?.let {
        val dateTime = toIslandDateTime()
        OffsetDateTime(dateTime, it.toIslandTimeZone().rules.offsetAt(dateTime))
    }
}

/**
 * Convert an `NSDateComponents` object to an Island Time [ZonedDateTime].
 * @throws DateTimeException if the `timeZone` property is absent.
 */
fun NSDateComponents.toIslandZonedDateTime() = toIslandZonedDateTimeOrNull()
    ?: throw DateTimeException("The 'timeZone' property must be non-null")

/**
 * Convert an `NSDateComponents` object to an Island Time [ZonedDateTime] or `null` if the `timeZone` property is
 * absent.
 */
fun NSDateComponents.toIslandZonedDateTimeOrNull(): ZonedDateTime? {
    return timeZone?.let { ZonedDateTime(toIslandDateTime(), it.toIslandTimeZone()) }
}

/**
 * Convert an Island Time [TimePoint] to an `NSDate`.
 */
fun <T> TimePoint<T>.toNSDate(): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(
        unixEpochSecond.toDouble() + unixEpochNanoOfSecond.toDouble() / NANOSECONDS_PER_SECOND
    )
}

/**
 * Convert an `NSDate` to an Island Time [Instant].
 */
fun NSDate.toIslandInstant(): Instant {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return Instant.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond)
}

/**
 * Convert an `NSDate` to an Island Time [DateTime] at the specified UTC offset.
 */
fun NSDate.toIslandDateTimeAt(offset: UtcOffset): DateTime {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return DateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, offset)
}

/**
 * Convert an `NSDate` to an Island Time [DateTime] at the specified time zone.
 */
fun NSDate.toIslandDateTimeAt(nsTimeZone: NSTimeZone) = toIslandDateTimeAt(nsTimeZone.toIslandUtcOffsetAt(this))

/**
 * Convert an `NSDate` to an Island Time [OffsetDateTime] at the specified UTC offset.
 */
fun NSDate.toIslandOffsetDateTimeAt(offset: UtcOffset): OffsetDateTime {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return OffsetDateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, offset)
}

/**
 * Convert an `NSDate` to an Island Time [OffsetDateTime] at the specified time zone.
 */
fun NSDate.toIslandOffsetDateTimeAt(nsTimeZone: NSTimeZone): OffsetDateTime {
    return toIslandOffsetDateTimeAt(nsTimeZone.toIslandUtcOffsetAt(this))
}

/**
 * Convert an `NSDate` to an Island Time [ZonedDateTime] at the specified time zone.
 */
fun NSDate.toIslandZonedDateTimeAt(zone: TimeZone): ZonedDateTime {
    val unixEpochSecond = timeIntervalSince1970.toLong()
    val unixEpochNanoOfSecond = ((timeIntervalSince1970 - unixEpochSecond) * NANOSECONDS_PER_SECOND).toInt()
    return ZonedDateTime.fromUnixEpochSecond(unixEpochSecond, unixEpochNanoOfSecond, zone)
}

/**
 * Convert an `NSDate` to an Island Time [ZonedDateTime] at the specified time zone.
 */
fun NSDate.toIslandZonedDateTimeAt(nsTimeZone: NSTimeZone) = toIslandZonedDateTimeAt(nsTimeZone.toIslandTimeZone())

/**
 * Convert an Island Time [UtcOffset] into an equivalent `NSTimeZone` with a fixed UTC offset.
 *
 * Note that `NSTimeZone`will round the `totalSeconds` value to the nearest minute.
 */
fun UtcOffset.toNSTimeZone(): NSTimeZone = NSTimeZone.timeZoneForSecondsFromGMT(totalSeconds.value.convert())

@Deprecated(
    message = "This operation is ambiguous. An NSDate must be provided.",
    replaceWith = ReplaceWith("this.toIslandUtcOffsetAt()"),
    level = DeprecationLevel.ERROR
)
fun NSTimeZone.toIslandUtcOffset(): UtcOffset = throw UnsupportedOperationException("Should not be called")

/**
 * Convert an `NSTimeZone` to an Island Time [UtcOffset] with the same shift from UTC at the provided date.
 */
fun NSTimeZone.toIslandUtcOffsetAt(date: NSDate): UtcOffset {
    return secondsFromGMTForDate(date).convert<Int>().seconds.asUtcOffset()
}

/**
 * Convert an NSTimeZone` to an Island Time [TimeZone] with the same identifier.
 */
fun NSTimeZone.toIslandTimeZone(): TimeZone = name.toTimeZone()

/**
 * Convert an Island Time [TimeZone] to an `NSTimeZone`.
 *
 * @throws TimeZoneRulesException if the identifier isn't recognized as valid for an `NSTimeZone`
 */
fun TimeZone.toNSTimeZone(): NSTimeZone = toNSTimeZoneOrNull()
    ?: throw TimeZoneRulesException("The identifier '$id' could not be converted to an NSTimeZone")

/**
 * Convert an Island Time [TimeZone] to an `NSTimeZone` or `null` if the identifier isn't recognized as valid for an
 * `NSTimeZone`.
 */
fun TimeZone.toNSTimeZoneOrNull(): NSTimeZone? {
    return if (this is TimeZone.FixedOffset) {
        NSTimeZone.timeZoneForSecondsFromGMT(offset.totalSeconds.value.convert())
    } else {
        NSTimeZone.timeZoneWithName(id)
    }
}

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun Duration.toNSTimeInterval(): NSTimeInterval = toComponents { seconds, nanoseconds ->
    seconds.value.toDouble() + nanoseconds.value.toDouble() / NANOSECONDS_PER_SECOND
}

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun IntDays.toNSTimeInterval(): NSTimeInterval = this.toLongDays().inSecondsUnchecked.value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun LongDays.toNSTimeInterval(): NSTimeInterval = this.inSeconds.value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun IntHours.toNSTimeInterval(): NSTimeInterval = this.toLongHours().inSecondsUnchecked.value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun LongHours.toNSTimeInterval(): NSTimeInterval = this.inSeconds.value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun IntMinutes.toNSTimeInterval(): NSTimeInterval = this.toLongMinutes().inSecondsUnchecked.value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun LongMinutes.toNSTimeInterval(): NSTimeInterval = this.inSeconds.value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun IntSeconds.toNSTimeInterval(): NSTimeInterval = value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun LongSeconds.toNSTimeInterval(): NSTimeInterval = value.toDouble()

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun IntMilliseconds.toNSTimeInterval(): NSTimeInterval = toComponents { seconds, milliseconds ->
    seconds.value.toDouble() + milliseconds.value.toDouble() / MILLISECONDS_PER_SECOND
}

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun LongMilliseconds.toNSTimeInterval(): NSTimeInterval = toComponents { seconds, milliseconds ->
    seconds.value.toDouble() + milliseconds.value.toDouble() / MILLISECONDS_PER_SECOND
}

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun IntMicroseconds.toNSTimeInterval(): NSTimeInterval = toComponents { seconds, microseconds ->
    seconds.value.toDouble() + microseconds.value.toDouble() / MICROSECONDS_PER_SECOND
}

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun LongMicroseconds.toNSTimeInterval(): NSTimeInterval = toComponents { seconds, microseconds ->
    seconds.value.toDouble() + microseconds.value.toDouble() / MICROSECONDS_PER_SECOND
}

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun IntNanoseconds.toNSTimeInterval(): NSTimeInterval = toComponents { seconds, nanoseconds ->
    seconds.value.toDouble() + nanoseconds.value.toDouble() / NANOSECONDS_PER_SECOND
}

/**
 * Convert to an equivalent `NSTimeInterval`.
 */
fun LongNanoseconds.toNSTimeInterval(): NSTimeInterval = toComponents { seconds, nanoseconds ->
    seconds.value.toDouble() + nanoseconds.value.toDouble() / NANOSECONDS_PER_SECOND
}

/**
 * Convert to an equivalent `NSDateInterval`.
 * @throws UnsupportedOperationException if the interval is unbounded
 */
fun <T : TimePoint<T>> TimePointInterval<T>.toNSDateInterval(): NSDateInterval {
    return toNSDateIntervalOrNull()
        ?: throw UnsupportedOperationException("An unbounded interval cannot be converted to an NSDateInterval")
}

/**
 * Convert to an equivalent `NSDateInterval` or `null` if the interval is unbounded.
 */
fun <T : TimePoint<T>> TimePointInterval<T>.toNSDateIntervalOrNull(): NSDateInterval? {
    return if (isBounded()) {
        NSDateInterval(start.toNSDate(), endExclusive.toNSDate())
    } else {
        null
    }
}

/**
 * Convert to an equivalent Island Time [InstantInterval].
 */
fun NSDateInterval.toIslandInstantInterval(): InstantInterval {
    return startDate.toIslandInstant() until endDate.toIslandInstant()
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