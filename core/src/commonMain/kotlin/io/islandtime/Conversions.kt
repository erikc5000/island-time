@file:JvmMultifileClass
@file:JvmName("DateTimesKt")

package io.islandtime

import io.islandtime.base.TimePoint
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Returns this date with the precision reduced to the year.
 */
fun YearMonth.toYear(): Year = Year(year)

/**
 * Returns this date with the precision reduced to the year.
 */
fun Date.toYear(): Year = Year(year)

/**
 * Returns this date-time with the precision reduced to the year.
 */
fun DateTime.toYear(): Year = date.toYear()

/**
 * Returns this date-time with the precision reduced to the year.
 */
fun OffsetDateTime.toYear(): Year = date.toYear()

/**
 * Returns this date-time with the precision reduced to the year.
 */
fun ZonedDateTime.toYear(): Year = date.toYear()

/**
 * Returns this date with the precision reduced to the year-month.
 */
fun Date.toYearMonth(): YearMonth = YearMonth(year, month)

/**
 * Returns this date-time with the precision reduced to the year-month.
 */
fun DateTime.toYearMonth(): YearMonth = date.toYearMonth()

/**
 * Returns this date-time with the precision reduced to the year-month.
 */
fun OffsetDateTime.toYearMonth(): YearMonth = dateTime.toYearMonth()

/**
 * Returns this date-time with the precision reduced to the year-month.
 */
fun ZonedDateTime.toYearMonth(): YearMonth = dateTime.toYearMonth()

/**
 * Returns the combined time and UTC offset.
 */
fun OffsetDateTime.toOffsetTime(): OffsetTime = OffsetTime(time, offset)

/**
 * Returns the combined time and UTC offset.
 */
fun ZonedDateTime.toOffsetTime(): OffsetTime = OffsetTime(time, offset)

/**
 * Returns the combined date, time, and UTC offset.
 *
 * While similar to `ZonedDateTime`, an `OffsetDateTime` representation is unaffected by time zone rule changes or
 * database differences between systems, making it better suited for use cases involving persistence or network
 * transfer.
 */
fun ZonedDateTime.toOffsetDateTime(): OffsetDateTime = OffsetDateTime(dateTime, offset)

/**
 * Converts this instant to the corresponding [DateTime] in [zone].
 */
fun Instant.toDateTimeAt(zone: TimeZone): DateTime = toDateTimeAt(zone.rules.offsetAt(this))

/**
 * Strategy to use when converting a local date-time accompanied by a [UtcOffset] to a date and time that are valid
 * according to the rules of a [TimeZone].
 */
enum class OffsetConversionStrategy {
    /**
     * Preserve the instant on the timeline, ignoring the local time.
     */
    PRESERVE_INSTANT,

    /**
     * Preserve the local date and time in the new time zone (if possible), adjusting the offset if needed.
     */
    PRESERVE_LOCAL_TIME
}

/**
 * Converts this [OffsetDateTime] to a [ZonedDateTime] using the specified [strategy] to adjust it to a valid date,
 * time, and offset in [zone].
 *
 * - [OffsetConversionStrategy.PRESERVE_INSTANT] - Preserve the instant captured by the date, time, and offset,
 * ignoring the local time.
 *
 * - [OffsetConversionStrategy.PRESERVE_LOCAL_TIME] - Preserve the local date and time in the new time zone, adjusting
 * the offset if needed.
 *
 * Alternatively, you can use [asZonedDateTime] to convert to a [ZonedDateTime] with an equivalent fixed-offset zone.
 * However, this comes with the caveat that a fixed-offset zone lacks knowledge of any region and will not respond to
 * daylight savings time changes.
 *
 * @see asZonedDateTime
 */
fun OffsetDateTime.toZonedDateTime(zone: TimeZone, strategy: OffsetConversionStrategy): ZonedDateTime {
    return when (strategy) {
        OffsetConversionStrategy.PRESERVE_INSTANT -> ZonedDateTime.fromInstant(dateTime, offset, zone)
        OffsetConversionStrategy.PRESERVE_LOCAL_TIME -> ZonedDateTime.fromLocal(dateTime, zone, offset)
    }
}

/**
 * Converts this date-time to the corresponding [Instant] at [offset].
 * @param offset the offset from UTC
 */
fun DateTime.toInstantAt(offset: UtcOffset): Instant {
    return Instant.fromSecondOfUnixEpoch(secondOfUnixEpochAt(offset), nanosecond)
}

/**
 * Converts this date-time to the [Instant] representing the same time point.
 */
fun OffsetDateTime.toInstant(): Instant = (this as TimePoint<*>).toInstant()

/**
 * Converts this date-time to the [Instant] representing the same time point.
 */
fun ZonedDateTime.toInstant(): Instant = (this as TimePoint<*>).toInstant()

internal fun TimePoint<*>.toInstant(): Instant = Instant.fromSecondOfUnixEpoch(secondOfUnixEpoch, nanosecond)