@file:JvmName("IslandTimeUtils")

package io.islandtime.extensions.threetenabp

import io.islandtime.*
import io.islandtime.measures.*

/**
 * Convert to an equivalent Island Time [Instant].
 */
fun org.threeten.bp.Instant.toIslandInstant(): Instant {
    return Instant.fromSecondOfUnixEpoch(epochSecond, nano)
}

/**
 * Convert to an equivalent Java `Instant`.
 */
fun Instant.toJavaInstant(): org.threeten.bp.Instant {
    return org.threeten.bp.Instant.ofEpochSecond(secondOfUnixEpoch, nanosecond.toLong())
}

/**
 * Convert to an equivalent Island Time [Date].
 */
fun org.threeten.bp.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

/**
 * Convert to an equivalent Java `LocalDate`.
 */
fun Date.toJavaLocalDate(): org.threeten.bp.LocalDate {
    return org.threeten.bp.LocalDate.of(year, monthNumber, dayOfMonth)
}

/**
 * Convert to an equivalent Island Time [Time].
 */
fun org.threeten.bp.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

/**
 * Convert to an equivalent Java `LocalTime`.
 */
fun Time.toJavaLocalTime(): org.threeten.bp.LocalTime {
    return org.threeten.bp.LocalTime.of(hour, minute, second, nanosecond)
}

/**
 * Convert to an equivalent Island Time [DateTime].
 */
fun org.threeten.bp.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

/**
 * Convert to an equivalent Java `LocalDateTime`.
 */
fun DateTime.toJavaLocalDateTime(): org.threeten.bp.LocalDateTime {
    return org.threeten.bp.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond)
}

/**
 * Convert to an equivalent Island Time [OffsetTime].
 */
fun org.threeten.bp.OffsetTime.toIslandOffsetTime(): OffsetTime {
    return OffsetTime(
        Time(hour, minute, second, nano),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

/**
 * Convert to an equivalent Java `OffsetTime`.
 */
fun OffsetTime.toJavaOffsetTime(): org.threeten.bp.OffsetTime {
    return org.threeten.bp.OffsetTime.of(
        org.threeten.bp.LocalTime.of(hour, minute, second, nanosecond),
        org.threeten.bp.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

/**
 * Convert to an equivalent Island Time [OffsetDateTime].
 */
fun org.threeten.bp.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

/**
 * Convert to an equivalent Java `OffsetDateTime`.
 */
fun OffsetDateTime.toJavaOffsetDateTime(): org.threeten.bp.OffsetDateTime {
    return org.threeten.bp.OffsetDateTime.of(
        org.threeten.bp.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        org.threeten.bp.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

/**
 * Convert to an equivalent Island Time [ZonedDateTime].
 */
fun org.threeten.bp.ZonedDateTime.toIslandZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.fromLocal(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        zone.toIslandTimeZone(),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

/**
 * Convert to an equivalent Java `ZonedDateTime`.
 */
fun ZonedDateTime.toJavaZonedDateTime(): org.threeten.bp.ZonedDateTime {
    return org.threeten.bp.ZonedDateTime.ofLocal(
        org.threeten.bp.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        org.threeten.bp.ZoneId.of(zone.id),
        org.threeten.bp.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

/**
 * Convert to an equivalent Island Time [UtcOffset].
 */
fun org.threeten.bp.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset(totalSeconds.seconds)
}

/**
 * Convert to an equivalent Java `ZoneOffset`.
 */
fun UtcOffset.toJavaZoneOffset(): org.threeten.bp.ZoneOffset {
    return org.threeten.bp.ZoneOffset.ofTotalSeconds(totalSeconds.value)
}

/**
 * Convert to an equivalent Java `ZoneId`.
 */
fun TimeZone.toJavaZoneId(): org.threeten.bp.ZoneId {
    return org.threeten.bp.ZoneId.of(id)
}

/**
 * Convert to an equivalent Island Time [TimeZone].
 */
fun org.threeten.bp.ZoneId.toIslandTimeZone(): TimeZone {
    return TimeZone(id)
}

/**
 * Convert to an equivalent Island Time [Duration].
 */
fun org.threeten.bp.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

/**
 * Convert to an equivalent Java `Duration`.
 */
fun Duration.toJavaDuration(): org.threeten.bp.Duration {
    return org.threeten.bp.Duration.ofSeconds(seconds.value, nanosecondAdjustment.value.toLong())
}

/**
 * Convert to an equivalent Island Time [Period].
 */
fun org.threeten.bp.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

/**
 * Convert to an equivalent Java `Period`.
 */
fun Period.toJavaPeriod(): org.threeten.bp.Period {
    return org.threeten.bp.Period.of(years.value, months.value, days.value)
}

/**
 * Convert to an equivalent Island Time [YearMonth].
 */
fun org.threeten.bp.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

/**
 * Convert to an equivalent Java `YearMonth`.
 */
fun YearMonth.toJavaYearMonth(): org.threeten.bp.YearMonth {
    return org.threeten.bp.YearMonth.of(year, monthNumber)
}

/**
 * Convert to an equivalent Island Time [Year].
 */
fun org.threeten.bp.Year.toIslandYear(): Year {
    return Year(value)
}

/**
 * Convert to an equivalent Java `Year`.
 */
fun Year.toJavaYear(): org.threeten.bp.Year {
    return org.threeten.bp.Year.of(value)
}

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntCenturies.toJavaPeriod(): org.threeten.bp.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongCenturies.toJavaPeriod(): org.threeten.bp.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntDecades.toJavaPeriod(): org.threeten.bp.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongDecades.toJavaPeriod(): org.threeten.bp.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntYears.toJavaPeriod(): org.threeten.bp.Period = org.threeten.bp.Period.ofYears(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongYears.toJavaPeriod(): org.threeten.bp.Period = this.toIntYears().toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntMonths.toJavaPeriod(): org.threeten.bp.Period = org.threeten.bp.Period.ofMonths(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongMonths.toJavaPeriod(): org.threeten.bp.Period = this.toIntMonths().toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntWeeks.toJavaPeriod(): org.threeten.bp.Period = org.threeten.bp.Period.ofWeeks(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongWeeks.toJavaPeriod(): org.threeten.bp.Period = this.toIntWeeks().toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntDays.toJavaPeriod(): org.threeten.bp.Period = org.threeten.bp.Period.ofDays(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongDays.toJavaPeriod(): org.threeten.bp.Period = this.toIntDays().toJavaPeriod()

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntDays.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofDays(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongDays.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofDays(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntHours.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofHours(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongHours.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofHours(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntMinutes.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofMinutes(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongMinutes.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofMinutes(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntSeconds.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofSeconds(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongSeconds.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofSeconds(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntMilliseconds.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofMillis(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongMilliseconds.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofMillis(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntMicroseconds.toJavaDuration(): org.threeten.bp.Duration = this.toLongMicroseconds().toJavaDuration()

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongMicroseconds.toJavaDuration(): org.threeten.bp.Duration {
    val seconds = this.inSeconds
    val nanoOfSeconds = (this % 1_000_000).inNanoseconds
    return org.threeten.bp.Duration.ofSeconds(seconds.value, nanoOfSeconds.value)
}

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntNanoseconds.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofNanos(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongNanoseconds.toJavaDuration(): org.threeten.bp.Duration = org.threeten.bp.Duration.ofNanos(value)