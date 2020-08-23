@file:JvmMultifileClass
@file:JvmName("DateTimesKt")

package io.islandtime

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Combines a year and month to create a [YearMonth].
 */
infix fun Year.at(month: Month): YearMonth = YearMonth(value, month)

/**
 * Combines a year and month number to create a [YearMonth].
 */
fun Year.atMonth(number: Int): YearMonth = YearMonth(value, number.toMonth())

/**
 * Combines a [YearMonth] with a day of the month to create a [Date].
 * @param day the day of the month
 */
fun YearMonth.atDay(day: Int): Date = Date(year, month, day)

/**
 * Combines a [Date] with a [Time] to create a [DateTime].
 */
infix fun Date.at(time: Time): DateTime = DateTime(this, time)

/**
 * Combines a [Date] with a time to create a [DateTime].
 */
fun Date.atTime(hour: Int, minute: Int, second: Int = 0, nanosecond: Int = 0): DateTime {
    return DateTime(this, Time(hour, minute, second, nanosecond))
}

/**
 * Combines a local date and time with a UTC offset to create an [OffsetDateTime].
 */
infix fun DateTime.at(offset: UtcOffset): OffsetDateTime = OffsetDateTime(this, offset)

/**
 * Combines a local date with a time and UTC offset to create an [OffsetDateTime].
 */
infix fun Date.at(offsetTime: OffsetTime): OffsetDateTime =
    OffsetDateTime(this, offsetTime.time, offsetTime.offset)

/**
 * Combines an instant with a UTC offset to create an [OffsetDateTime].
 */
infix fun Instant.at(offset: UtcOffset): OffsetDateTime = OffsetDateTime(this.toDateTimeAt(offset), offset)

/**
 * Combines an instant with a time zone to create a [ZonedDateTime].
 */
infix fun Instant.at(zone: TimeZone): ZonedDateTime =
    ZonedDateTime.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond, zone)

/**
 * Combines a local date and time with a time zone to create a [ZonedDateTime].
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it falls within an overlap
 * (meaning the local time exists twice), the earlier offset will be used.
 */
infix fun DateTime.at(zone: TimeZone): ZonedDateTime = ZonedDateTime.fromLocal(this, zone)

/**
 * Combines a local time with a UTC offset to create an [OffsetTime].
 */
infix fun Time.at(offset: UtcOffset): OffsetTime = OffsetTime(this, offset)

/**
 * The [DateTime] at the start of the day. Daylight savings transitions are not taken into account, so the returned
 * date-time may not necessarily exist in all time zones.
 * @see Date.startOfDayAt
 */
val Date.startOfDay: DateTime get() = DateTime(this, Time.MIDNIGHT)

/**
 * The [DateTime] at the last representable instant of the day. Daylight savings transitions are not taken into account,
 * so the returned date-time may not necessarily exist in all time zones.
 * @see Date.endOfDayAt
 */
val Date.endOfDay: DateTime get() = DateTime(this, Time.MAX)

/**
 * The [ZonedDateTime] at the start of the day in [zone], taking into account any daylight savings transitions.
 */
fun Date.startOfDayAt(zone: TimeZone): ZonedDateTime {
    val dateTime = this at Time.MIDNIGHT
    val transition = zone.rules.transitionAt(dateTime)

    return if (transition?.isGap == true) {
        transition.dateTimeAfter at zone
    } else {
        dateTime at zone
    }
}

/**
 * The [ZonedDateTime] at the last representable instant of the day in [zone], taking into account any daylight savings
 * transitions.
 */
fun Date.endOfDayAt(zone: TimeZone): ZonedDateTime {
    val dateTime = this at Time.MAX
    val rules = zone.rules
    val validOffsets = rules.validOffsetsAt(dateTime)

    return if (validOffsets.size == 1) {
        ZonedDateTime.create(dateTime, validOffsets[0], zone)
    } else {
        val transition = rules.transitionAt(dateTime)

        if (validOffsets.isEmpty()) {
            ZonedDateTime.create(transition!!.dateTimeBefore, transition.offsetBefore, zone)
        } else {
            ZonedDateTime.create(dateTime, transition!!.offsetAfter, zone)
        }
    }
}
