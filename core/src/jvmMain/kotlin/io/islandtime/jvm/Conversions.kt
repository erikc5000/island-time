@file:JvmName("Conversions")
@file:JvmMultifileClass
@file:Suppress("NewApi")

package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.internal.MICROSECONDS_PER_SECOND
import io.islandtime.measures.*

/**
 * Convert to an equivalent Island Time [Instant].
 */
fun java.time.Instant.toIslandInstant(): Instant {
    return Instant.fromSecondOfUnixEpoch(epochSecond, nano)
}

/**
 * Convert to an equivalent Java `Instant`.
 */
fun Instant.toJavaInstant(): java.time.Instant {
    return java.time.Instant.ofEpochSecond(secondOfUnixEpoch, nanosecond.toLong())
}

/**
 * Convert to an equivalent Island Time [Date].
 */
fun java.time.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

/**
 * Convert to an equivalent Java `LocalDate`.
 */
fun Date.toJavaLocalDate(): java.time.LocalDate {
    return java.time.LocalDate.of(year, monthNumber, dayOfMonth)
}

/**
 * Convert to an equivalent Island Time [Time].
 */
fun java.time.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

/**
 * Convert to an equivalent Java `LocalTime`.
 */
fun Time.toJavaLocalTime(): java.time.LocalTime {
    return java.time.LocalTime.of(hour, minute, second, nanosecond)
}

/**
 * Convert to an equivalent Island Time [DateTime].
 */
fun java.time.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

/**
 * Convert to an equivalent Java `LocalDateTime`.
 */
fun DateTime.toJavaLocalDateTime(): java.time.LocalDateTime {
    return java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond)
}

/**
 * Convert to an equivalent Island Time [OffsetTime].
 */
fun java.time.OffsetTime.toIslandOffsetTime(): OffsetTime {
    return OffsetTime(
        Time(hour, minute, second, nano),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

/**
 * Convert to an equivalent Java `OffsetTime`.
 */
fun OffsetTime.toJavaOffsetTime(): java.time.OffsetTime {
    return java.time.OffsetTime.of(
        java.time.LocalTime.of(hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

/**
 * Convert to an equivalent Island Time [OffsetDateTime].
 */
fun java.time.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
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
fun OffsetDateTime.toJavaOffsetDateTime(): java.time.OffsetDateTime {
    return java.time.OffsetDateTime.of(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

/**
 * Convert to an equivalent Island Time [ZonedDateTime].
 */
fun java.time.ZonedDateTime.toIslandZonedDateTime(): ZonedDateTime {
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
fun ZonedDateTime.toJavaZonedDateTime(): java.time.ZonedDateTime {
    return java.time.ZonedDateTime.ofLocal(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneId.of(zone.id),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

/**
 * Convert to an equivalent Island Time [UtcOffset].
 */
fun java.time.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset(totalSeconds.seconds)
}

/**
 * Convert to an equivalent Java `ZoneOffset`.
 */
fun UtcOffset.toJavaZoneOffset(): java.time.ZoneOffset {
    return java.time.ZoneOffset.ofTotalSeconds(totalSeconds.value)
}

/**
 * Convert to an equivalent Java `ZoneId`.
 */
fun TimeZone.toJavaZoneId(): java.time.ZoneId {
    return java.time.ZoneId.of(id)
}

/**
 * Convert to an equivalent Island Time [TimeZone].
 */
fun java.time.ZoneId.toIslandTimeZone(): TimeZone {
    return TimeZone(id)
}

/**
 * Convert to an equivalent Island Time [Duration].
 */
fun java.time.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

/**
 * Convert to an equivalent Java `Duration`.
 */
fun Duration.toJavaDuration(): java.time.Duration {
    return java.time.Duration.ofSeconds(seconds.value, nanosecondAdjustment.value.toLong())
}

/**
 * Convert to an equivalent Island Time [Period].
 */
fun java.time.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

/**
 * Convert to an equivalent Java `Period`.
 */
fun Period.toJavaPeriod(): java.time.Period {
    return java.time.Period.of(years.value, months.value, days.value)
}

/**
 * Convert to an equivalent Island Time [YearMonth].
 */
fun java.time.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

/**
 * Convert to an equivalent Java `YearMonth`.
 */
fun YearMonth.toJavaYearMonth(): java.time.YearMonth {
    return java.time.YearMonth.of(year, monthNumber)
}

/**
 * Convert to an equivalent Island Time [Year].
 */
fun java.time.Year.toIslandYear(): Year {
    return Year(value)
}

/**
 * Convert to an equivalent Java `Year`.
 */
fun Year.toJavaYear(): java.time.Year {
    return java.time.Year.of(value)
}

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntCenturies.toJavaPeriod(): java.time.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongCenturies.toJavaPeriod(): java.time.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntDecades.toJavaPeriod(): java.time.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongDecades.toJavaPeriod(): java.time.Period = this.inYears.toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntYears.toJavaPeriod(): java.time.Period = java.time.Period.ofYears(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongYears.toJavaPeriod(): java.time.Period = this.toIntYears().toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntMonths.toJavaPeriod(): java.time.Period = java.time.Period.ofMonths(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongMonths.toJavaPeriod(): java.time.Period = this.toIntMonths().toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntWeeks.toJavaPeriod(): java.time.Period = java.time.Period.ofWeeks(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongWeeks.toJavaPeriod(): java.time.Period = this.toIntWeeks().toJavaPeriod()

/**
 * Convert to an equivalent Java `Period`.
 */
fun IntDays.toJavaPeriod(): java.time.Period = java.time.Period.ofDays(value)

/**
 * Convert to an equivalent Java `Period`.
 */
fun LongDays.toJavaPeriod(): java.time.Period = this.toIntDays().toJavaPeriod()

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntDays.toJavaDuration(): java.time.Duration = java.time.Duration.ofDays(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongDays.toJavaDuration(): java.time.Duration = java.time.Duration.ofDays(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntHours.toJavaDuration(): java.time.Duration = java.time.Duration.ofHours(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongHours.toJavaDuration(): java.time.Duration = java.time.Duration.ofHours(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntMinutes.toJavaDuration(): java.time.Duration = java.time.Duration.ofMinutes(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongMinutes.toJavaDuration(): java.time.Duration = java.time.Duration.ofMinutes(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntSeconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofSeconds(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongSeconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofSeconds(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntMilliseconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofMillis(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongMilliseconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofMillis(value)

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntMicroseconds.toJavaDuration(): java.time.Duration = this.toLongMicroseconds().toJavaDuration()

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongMicroseconds.toJavaDuration(): java.time.Duration {
    val seconds = this.inSeconds
    val nanoOfSeconds = (this % MICROSECONDS_PER_SECOND).inNanoseconds
    return java.time.Duration.ofSeconds(seconds.value, nanoOfSeconds.value)
}

/**
 * Convert to an equivalent Java `Duration`.
 */
fun IntNanoseconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofNanos(value.toLong())

/**
 * Convert to an equivalent Java `Duration`.
 */
fun LongNanoseconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofNanos(value)