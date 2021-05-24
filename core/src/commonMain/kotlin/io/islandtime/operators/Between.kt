@file:JvmName("DateTimesKt")
@file:JvmMultifileClass
@file:Suppress("PackageDirectoryMismatch")

package io.islandtime

import io.islandtime.base.TimePoint
import io.islandtime.measures.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Returns the number of whole centuries between two years.
 */
fun Centuries.Companion.between(start: Year, endExclusive: Year): Centuries {
    return Years.between(start, endExclusive).inWholeCenturies
}

/**
 * Returns the number of whole centuries between two year-months.
 */
fun Centuries.Companion.between(start: YearMonth, endExclusive: YearMonth): Centuries {
    return Years.between(start, endExclusive).inWholeCenturies
}

/**
 * Returns the number of whole centuries between two dates.
 */
fun Centuries.Companion.between(start: Date, endExclusive: Date): Centuries {
    return Months.between(start, endExclusive).inWholeCenturies
}

/**
 * Returns the number of whole centuries between two date-times, which are assumed to be in the same time zone.
 */
fun Centuries.Companion.between(start: DateTime, endExclusive: DateTime): Centuries {
    return Months.between(start, endExclusive).inWholeCenturies
}

/**
 * Returns the number of whole centuries between two date-times, adjusting the offset of [endExclusive] if necessary to
 * match the starting date-time.
 */
fun Centuries.Companion.between(start: OffsetDateTime, endExclusive: OffsetDateTime): Centuries {
    return Months.between(start, endExclusive).inWholeCenturies
}

/**
 * Returns the number of whole centuries between two date-times, adjusting the time zone of [endExclusive] if necessary
 * to match the starting date-time.
 */
fun Centuries.Companion.between(start: ZonedDateTime, endExclusive: ZonedDateTime): Centuries {
    return Months.between(start, endExclusive).inWholeCenturies
}

/**
 * Returns the number of whole decades between two years.
 */
fun Decades.Companion.between(start: Year, endExclusive: Year): Decades {
    return Years.between(start, endExclusive).inWholeDecades
}

/**
 * Returns the number of whole decades between two year-months.
 */
fun Decades.Companion.between(start: YearMonth, endExclusive: YearMonth): Decades {
    return Years.between(start, endExclusive).inWholeDecades
}

/**
 * Returns the number of whole decades between two dates.
 */
fun Decades.Companion.between(start: Date, endExclusive: Date): Decades {
    return Months.between(start, endExclusive).inWholeDecades
}

/**
 * Returns the number of whole decades between two date-times, which are assumed to be in the same time zone.
 */
fun Decades.Companion.between(start: DateTime, endExclusive: DateTime): Decades {
    return Months.between(start, endExclusive).inWholeDecades
}

/**
 * Returns the number of whole decades between two date-times, adjusting the offset of [endExclusive] if necessary to
 * match the starting date-time.
 */
fun Decades.Companion.between(start: OffsetDateTime, endExclusive: OffsetDateTime): Decades {
    return Months.between(start, endExclusive).inWholeDecades
}

/**
 * Returns the number of whole decades between two date-times, adjusting the time zone of [endExclusive] if necessary to
 * match the starting date-time.
 */
fun Decades.Companion.between(start: ZonedDateTime, endExclusive: ZonedDateTime): Decades {
    return Months.between(start, endExclusive).inWholeDecades
}

/**
 * Returns the number of years between two years.
 */
fun Years.Companion.between(start: Year, endExclusive: Year): Years {
    return Years(endExclusive.value - start.value)
}

/**
 * Returns the number of whole years between two year-months.
 */
fun Years.Companion.between(start: YearMonth, endExclusive: YearMonth): Years {
    return Months.between(start, endExclusive).inWholeYears
}

/**
 * Returns the number of whole years between two dates.
 */
fun Years.Companion.between(start: Date, endExclusive: Date): Years {
    return Months.between(start, endExclusive).inWholeYears
}

/**
 * Returns the number of whole years between two date-times, which are assumed to be in the same time zone.
 */
fun Years.Companion.between(start: DateTime, endExclusive: DateTime): Years {
    return Years.between(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Returns the number of whole years between two date-times, adjusting the offset of [endExclusive] if necessary to
 * match the starting date-time.
 */
fun Years.Companion.between(start: OffsetDateTime, endExclusive: OffsetDateTime): Years {
    return Years.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of whole years between date-times, adjusting the time zone of [endExclusive] if necessary to match
 * the starting date-time.
 */
fun Years.Companion.between(start: ZonedDateTime, endExclusive: ZonedDateTime): Years {
    return Years.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of months between two year-months.
 */
fun Months.Companion.between(start: YearMonth, endExclusive: YearMonth): Months {
    return (endExclusive.monthsSinceYear0 - start.monthsSinceYear0).months
}

/**
 * Returns the number of whole months between two dates.
 */
fun Months.Companion.between(start: Date, endExclusive: Date): Months {
    val startDays = start.monthsSinceYear0 * 32L + start.dayOfMonth
    val endDays = endExclusive.monthsSinceYear0 * 32L + endExclusive.dayOfMonth
    return ((endDays - startDays) / 32).months
}

/**
 * Returns the number of whole months between two date-times, which are assumed to be in the same time zone.
 */
fun Months.Companion.between(start: DateTime, endExclusive: DateTime): Months {
    return Months.between(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Returns the number of whole months between two date-times, adjusting the offset of [endExclusive] if necessary to match
 * the starting date-time.
 */
fun Months.Companion.between(start: OffsetDateTime, endExclusive: OffsetDateTime): Months {
    return Months.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of whole months between two zoned date-times, adjusting the time zone of [endExclusive] if necessary
 * to match the starting date-time.
 */
fun Months.Companion.between(start: ZonedDateTime, endExclusive: ZonedDateTime): Months {
    return Months.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of whole weeks between two dates.
 */
fun Weeks.Companion.between(start: Date, endExclusive: Date): Weeks {
    return Days.between(start, endExclusive).inWholeWeeks
}

/**
 * Returns the number whole weeks between two date-times, which are assumed to be in the same time zone.
 */
fun Weeks.Companion.between(start: DateTime, endExclusive: DateTime): Weeks {
    return Days.between(start, endExclusive).inWholeWeeks
}

/**
 * Returns the number whole weeks between two date-times, adjusting the offset of [endExclusive] if necessary to match
 * the starting date-time.
 */
fun Weeks.Companion.between(start: OffsetDateTime, endExclusive: OffsetDateTime): Weeks {
    return Weeks.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of whole weeks between two zoned date-times, adjusting the time zone of [endExclusive] if
 * necessary to match the starting date-time.
 */
fun Weeks.Companion.between(start: ZonedDateTime, endExclusive: ZonedDateTime): Weeks {
    return Weeks.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of days between two dates.
 */
fun Days.Companion.between(start: Date, endExclusive: Date): Days {
    return endExclusive.daysSinceUnixEpoch - start.daysSinceUnixEpoch
}

/**
 * Returns the number whole days between two date-times, which are assumed to be in the same time zone.
 */
fun Days.Companion.between(start: DateTime, endExclusive: DateTime): Days {
    return Seconds.between(start, endExclusive).inWholeDays
}

/**
 * Returns the number of 24-hour days between two time points.
 */
//fun Days.Companion.between(start: TimePoint<*>, endExclusive: TimePoint<*>): Days {
//    return Seconds.between(start, endExclusive).inWholeDays
//}

/**
 * Returns the number whole days between two date-times, adjusting the offset of [endExclusive] if necessary to match
 * the starting date-time.
 */
fun Days.Companion.between(start: OffsetDateTime, endExclusive: OffsetDateTime): Days {
    return Days.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of whole days between two zoned date-times, adjusting the time zone of [endExclusive] if necessary
 * to match the starting date-time.
 */
fun Days.Companion.between(start: ZonedDateTime, endExclusive: ZonedDateTime): Days {
    return Days.between(start.dateTime, adjustedEndDateTime(start, endExclusive))
}

/**
 * Returns the number of whole hours between two date-times, which are assumed to be at the same UTC offset. In general,
 * it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 */
fun Hours.Companion.between(start: DateTime, endExclusive: DateTime): Hours {
    return Seconds.between(start, endExclusive).inWholeHours
}

/**
 * Returns the number of whole hours between two time points.
 */
fun Hours.Companion.between(start: TimePoint<*>, endExclusive: TimePoint<*>): Hours {
    return Seconds.between(start, endExclusive).inWholeHours
}

/**
 * Returns the number of whole minutes between two date-times, which are assumed to be at the same UTC offset. In
 * general, it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules
 * won't be taken into account when working with [DateTime] directly.
 */
fun Minutes.Companion.between(start: DateTime, endExclusive: DateTime): Minutes {
    return Seconds.between(start, endExclusive).inWholeMinutes
}

/**
 * Returns the number of whole minutes between two time points.
 */
fun Minutes.Companion.between(start: TimePoint<*>, endExclusive: TimePoint<*>): Minutes {
    return Seconds.between(start, endExclusive).inWholeMinutes
}

/**
 * Returns the number of whole seconds between two date-times, which are assumed to be at the same UTC offset. In
 * general, it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules
 * won't be taken into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun Seconds.Companion.between(start: DateTime, endExclusive: DateTime): Seconds {
    return secondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

/**
 * Returns the number of whole seconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun Seconds.Companion.between(start: TimePoint<*>, endExclusive: TimePoint<*>): Seconds {
    return secondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}

/**
 * Returns the number of whole milliseconds between two date-times, which are assumed to be at the same UTC offset. In
 * general, it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules
 * won't be taken into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun Milliseconds.Companion.between(start: DateTime, endExclusive: DateTime): Milliseconds {
    return millisecondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

/**
 * Returns the number of whole milliseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun Milliseconds.Companion.between(start: TimePoint<*>, endExclusive: TimePoint<*>): Milliseconds {
    return millisecondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}

/**
 * Returns the number of whole microseconds between two date-times, which are assumed to be at the same UTC offset. In
 * general, it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules
 * won't be taken into account when working with [DateTime] directly.
 *
 *  @throws ArithmeticException if the result overflows
 */
fun Microseconds.Companion.between(start: DateTime, endExclusive: DateTime): Microseconds {
    return microsecondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

/**
 * Returns the number of whole microseconds between two time points.
 *  @throws ArithmeticException if the result overflows
 */
fun Microseconds.Companion.between(start: TimePoint<*>, endExclusive: TimePoint<*>): Microseconds {
    return microsecondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}

/**
 * Returns the number of nanoseconds between two date-times, which are assumed to be at the same UTC offset. In general,
 * it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun Nanoseconds.Companion.between(start: DateTime, endExclusive: DateTime): Nanoseconds {
    return nanosecondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

/**
 * Returns the number of nanoseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun Nanoseconds.Companion.between(start: TimePoint<*>, endExclusive: TimePoint<*>): Nanoseconds {
    return nanosecondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}

private inline val YearMonth.monthsSinceYear0: Long get() = year * 12L + month.ordinal

private fun secondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Seconds {
    val secondDiff = endExclusiveSecond - startSecond
    val nanoDiff = endExclusiveNanosecond - startNanosecond

    return when {
        secondDiff > 0 && nanoDiff < 0 -> secondDiff - 1
        secondDiff < 0 && nanoDiff > 0 -> secondDiff + 1
        else -> secondDiff
    }.seconds
}

private fun millisecondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Milliseconds {
    return (endExclusiveSecond - startSecond).seconds +
        (endExclusiveNanosecond - startNanosecond).nanoseconds.inWholeMilliseconds
}

private fun microsecondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Microseconds {
    return (endExclusiveSecond - startSecond).seconds +
        (endExclusiveNanosecond - startNanosecond).nanoseconds.inWholeMicroseconds
}

private fun nanosecondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Nanoseconds {
    return (endExclusiveSecond - startSecond).seconds + (endExclusiveNanosecond - startNanosecond).nanoseconds
}

private fun adjustedEndDate(start: DateTime, endExclusive: DateTime): Date {
    return when {
        endExclusive.date > start.date && endExclusive.time < start.time -> endExclusive.date - 1.days
        endExclusive.date < start.date && endExclusive.time > start.time -> endExclusive.date + 1.days
        else -> endExclusive.date
    }
}

private fun adjustedEndDateTime(start: OffsetDateTime, endExclusive: OffsetDateTime): DateTime {
    val offsetDelta = start.offset.totalSeconds - endExclusive.offset.totalSeconds
    return endExclusive.dateTime + offsetDelta
}

private fun adjustedEndDateTime(start: ZonedDateTime, endExclusive: ZonedDateTime): DateTime {
    return endExclusive.adjustedTo(start.zone).dateTime
}
