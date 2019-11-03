@file:JvmName("IslandTimeUtils")

package io.islandtime.extensions.threetenabp

import io.islandtime.*
import io.islandtime.measures.*

fun org.threeten.bp.Instant.toIslandInstant(): Instant {
    return Instant.fromUnixEpochSecond(epochSecond, nano)
}

fun Instant.toJavaInstant(): org.threeten.bp.Instant {
    return org.threeten.bp.Instant.ofEpochSecond(unixEpochSecond, unixEpochNanoOfSecond.toLong())
}

fun org.threeten.bp.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

fun Date.toJavaLocalDate(): org.threeten.bp.LocalDate {
    return org.threeten.bp.LocalDate.of(year, monthNumber, dayOfMonth)
}

fun org.threeten.bp.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

fun Time.toJavaLocalTime(): org.threeten.bp.LocalTime {
    return org.threeten.bp.LocalTime.of(hour, minute, second, nanosecond)
}

fun org.threeten.bp.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

fun DateTime.toJavaLocalDateTime(): org.threeten.bp.LocalDateTime {
    return org.threeten.bp.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond)
}

fun org.threeten.bp.OffsetTime.toIslandOffsetTime(): OffsetTime {
    return OffsetTime(
        Time(hour, minute, second, nano),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

fun OffsetTime.toJavaOffsetTime(): org.threeten.bp.OffsetTime {
    return org.threeten.bp.OffsetTime.of(
        org.threeten.bp.LocalTime.of(hour, minute, second, nanosecond),
        org.threeten.bp.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

fun org.threeten.bp.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

fun OffsetDateTime.toJavaOffsetDateTime(): org.threeten.bp.OffsetDateTime {
    return org.threeten.bp.OffsetDateTime.of(
        org.threeten.bp.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        org.threeten.bp.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

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

fun ZonedDateTime.toJavaZonedDateTime(): org.threeten.bp.ZonedDateTime {
    return org.threeten.bp.ZonedDateTime.ofLocal(
        org.threeten.bp.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        org.threeten.bp.ZoneId.of(zone.id),
        org.threeten.bp.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

fun org.threeten.bp.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset(totalSeconds.seconds)
}

fun UtcOffset.toJavaZoneOffset(): org.threeten.bp.ZoneOffset {
    return org.threeten.bp.ZoneOffset.ofTotalSeconds(totalSeconds.value)
}

fun TimeZone.toJavaZoneId(): org.threeten.bp.ZoneId {
    return org.threeten.bp.ZoneId.of(id)
}

fun org.threeten.bp.ZoneId.toIslandTimeZone(): TimeZone {
    return TimeZone(id)
}

fun org.threeten.bp.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

fun Duration.toJavaDuration(): org.threeten.bp.Duration {
    return org.threeten.bp.Duration.ofSeconds(seconds.value, nanosecondAdjustment.value.toLong())
}

fun org.threeten.bp.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

fun Period.toJavaPeriod(): org.threeten.bp.Period {
    return org.threeten.bp.Period.of(years.value, months.value, days.value)
}

fun org.threeten.bp.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

fun YearMonth.toJavaYearMonth(): org.threeten.bp.YearMonth {
    return org.threeten.bp.YearMonth.of(year, monthNumber)
}

fun org.threeten.bp.Year.toIslandYear(): Year {
    return Year(value)
}

fun Year.toJavaYear(): org.threeten.bp.Year {
    return org.threeten.bp.Year.of(value)
}