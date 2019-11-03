@file:JvmName("IslandTimeUtils")
@file:Suppress("NewApi")

package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.measures.*

fun java.time.Instant.toIslandInstant(): Instant {
    return Instant.fromUnixEpochSecond(epochSecond, nano)
}

fun Instant.toJavaInstant(): java.time.Instant {
    return java.time.Instant.ofEpochSecond(unixEpochSecond, unixEpochNanoOfSecond.toLong())
}

fun java.time.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

fun Date.toJavaLocalDate(): java.time.LocalDate {
    return java.time.LocalDate.of(year, monthNumber, dayOfMonth)
}

fun java.time.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

fun Time.toJavaLocalTime(): java.time.LocalTime {
    return java.time.LocalTime.of(hour, minute, second, nanosecond)
}

fun java.time.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

fun DateTime.toJavaLocalDateTime(): java.time.LocalDateTime {
    return java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond)
}

fun java.time.OffsetTime.toIslandOffsetTime(): OffsetTime {
    return OffsetTime(
        Time(hour, minute, second, nano),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

fun OffsetTime.toJavaOffsetTime(): java.time.OffsetTime {
    return java.time.OffsetTime.of(
        java.time.LocalTime.of(hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

fun java.time.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

fun OffsetDateTime.toJavaOffsetDateTime(): java.time.OffsetDateTime {
    return java.time.OffsetDateTime.of(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

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

fun ZonedDateTime.toJavaZonedDateTime(): java.time.ZonedDateTime {
    return java.time.ZonedDateTime.ofLocal(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneId.of(zone.id),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

fun java.time.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset(totalSeconds.seconds)
}

fun UtcOffset.toJavaZoneOffset(): java.time.ZoneOffset {
    return java.time.ZoneOffset.ofTotalSeconds(totalSeconds.value)
}

fun TimeZone.toJavaZoneId(): java.time.ZoneId {
    return java.time.ZoneId.of(id)
}

fun java.time.ZoneId.toIslandTimeZone(): TimeZone {
    return TimeZone(id)
}

fun java.time.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

fun Duration.toJavaDuration(): java.time.Duration {
    return java.time.Duration.ofSeconds(seconds.value, nanosecondAdjustment.value.toLong())
}

fun java.time.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

fun Period.toJavaPeriod(): java.time.Period {
    return java.time.Period.of(years.value, months.value, days.value)
}

fun java.time.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

fun YearMonth.toJavaYearMonth(): java.time.YearMonth {
    return java.time.YearMonth.of(year, monthNumber)
}

fun java.time.Year.toIslandYear(): Year {
    return Year(value)
}

fun Year.toJavaYear(): java.time.Year {
    return java.time.Year.of(value)
}