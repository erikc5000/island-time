@file:JvmName("IslandTimeUtils")

package dev.erikchristensen.islandtime.jvm

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.*

@JvmName("convertFromJava")
fun java.time.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

@JvmName("convertToJava")
fun Date.toJavaLocalDate(): java.time.LocalDate {
    return java.time.LocalDate.of(year, month.number, dayOfMonth)
}

@JvmName("convertFromJava")
fun java.time.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

@JvmName("convertToJava")
fun Time.toJavaLocalTime(): java.time.LocalTime {
    return java.time.LocalTime.of(hour, minute, second, nanosecond)
}

@JvmName("convertFromJava")
fun java.time.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

@JvmName("convertToJava")
fun DateTime.toJavaLocalDateTime(): java.time.LocalDateTime {
    return java.time.LocalDateTime.of(year, month.number, dayOfMonth, hour, minute, second, nanosecond)
}

@JvmName("convertFromJava")
fun java.time.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

@JvmName("convertToJava")
fun OffsetDateTime.toJavaOffsetDateTime(): java.time.OffsetDateTime {
    return java.time.OffsetDateTime.of(
        java.time.LocalDateTime.of(year, month.number, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

@JvmName("convertFromJava")
fun java.time.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset(totalSeconds.seconds)
}

@JvmName("convertToJava")
fun UtcOffset.toJavaZoneOffset(): java.time.ZoneOffset {
    return java.time.ZoneOffset.ofTotalSeconds(totalSeconds.value)
}

@JvmName("convertFromJava")
fun java.time.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

@JvmName("convertToJava")
fun Duration.toJavaDuration(): java.time.Duration {
    return java.time.Duration.ofSeconds(seconds.value, nanoOfSeconds.value.toLong())
}

@JvmName("convertFromJava")
fun java.time.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

@JvmName("convertToJava")
fun Period.toJavaPeriod(): java.time.Period {
    return java.time.Period.of(years.value, months.value, days.value)
}

@JvmName("convertFromJava")
fun java.time.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

@JvmName("convertToJava")
fun YearMonth.toJavaYearMonth(): java.time.YearMonth {
    return java.time.YearMonth.of(year, month.number)
}