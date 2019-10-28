@file:JvmName("IslandTimeUtils")
@file:Suppress("NewApi")

package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.measures.*

@JvmName("convertToIslandInstant")
fun java.time.Instant.toIslandInstant(): Instant {
    return Instant.fromUnixEpochSecond(epochSecond, nano)
}

@JvmName("convertToJavaInstant")
fun Instant.toJavaInstant(): java.time.Instant {
    return java.time.Instant.ofEpochSecond(unixEpochSecond, unixEpochNanoOfSecond.toLong())
}

@JvmName("convertToIslandDate")
fun java.time.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

@JvmName("convertToJavaLocalDate")
fun Date.toJavaLocalDate(): java.time.LocalDate {
    return java.time.LocalDate.of(year, monthNumber, dayOfMonth)
}

@JvmName("convertToIslandTime")
fun java.time.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

@JvmName("convertToJavaLocalTime")
fun Time.toJavaLocalTime(): java.time.LocalTime {
    return java.time.LocalTime.of(hour, minute, second, nanosecond)
}

@JvmName("convertToIslandDateTime")
fun java.time.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

@JvmName("convertToJavaLocalDateTime")
fun DateTime.toJavaLocalDateTime(): java.time.LocalDateTime {
    return java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond)
}

@JvmName("convertToIslandOffsetTime")
fun java.time.OffsetTime.toIslandOffsetTime(): OffsetTime {
    return OffsetTime(
        Time(hour, minute, second, nano),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

@JvmName("convertToJavaOffsetTime")
fun OffsetTime.toJavaOffsetTime(): java.time.OffsetTime {
    return java.time.OffsetTime.of(
        java.time.LocalTime.of(hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

@JvmName("convertToIslandOffsetDateTime")
fun java.time.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

@JvmName("convertToJavaOffsetDateTime")
fun OffsetDateTime.toJavaOffsetDateTime(): java.time.OffsetDateTime {
    return java.time.OffsetDateTime.of(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

@JvmName("convertToIslandZonedDateTime")
fun java.time.ZonedDateTime.toIslandZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.fromLocal(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        TimeZone(zone.id),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

@JvmName("convertToJavaZonedDateTime")
fun ZonedDateTime.toJavaZonedDateTime(): java.time.ZonedDateTime {
    return java.time.ZonedDateTime.ofLocal(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneId.of(zone.regionId),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

@JvmName("convertToIslandUtcOffset")
fun java.time.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset(totalSeconds.seconds)
}

@JvmName("convertToJavaZoneOffset")
fun UtcOffset.toJavaZoneOffset(): java.time.ZoneOffset {
    return java.time.ZoneOffset.ofTotalSeconds(totalSeconds.value)
}

@JvmName("convertToIslandDuration")
fun java.time.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

@JvmName("convertToJavaDuration")
fun Duration.toJavaDuration(): java.time.Duration {
    return java.time.Duration.ofSeconds(seconds.value, nanosecondAdjustment.value.toLong())
}

@JvmName("convertToIslandPeriod")
fun java.time.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

@JvmName("convertToJavaPeriod")
fun Period.toJavaPeriod(): java.time.Period {
    return java.time.Period.of(years.value, months.value, days.value)
}

@JvmName("convertToIslandYearMonth")
fun java.time.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

@JvmName("convertToJavaYearMonth")
fun YearMonth.toJavaYearMonth(): java.time.YearMonth {
    return java.time.YearMonth.of(year, monthNumber)
}

@JvmName("convertToIslandYear")
fun java.time.Year.toIslandYear(): Year {
    return Year(value)
}

@JvmName("convertToJavaYear")
fun Year.toJavaYear(): java.time.Year {
    return java.time.Year.of(value)
}