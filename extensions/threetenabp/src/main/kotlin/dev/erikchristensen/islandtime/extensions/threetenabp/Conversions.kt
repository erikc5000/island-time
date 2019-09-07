@file:JvmName("IslandTimeUtils")

package dev.erikchristensen.islandtime.extensions.threetenabp

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.*

@JvmName("convertFromJava")
fun org.threeten.bp.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

@JvmName("convertToJava")
fun Date.toJavaLocalDate(): org.threeten.bp.LocalDate {
    return org.threeten.bp.LocalDate.of(year, month.number, dayOfMonth)
}

@JvmName("convertFromJava")
fun org.threeten.bp.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

@JvmName("convertToJava")
fun Time.toJavaLocalTime(): org.threeten.bp.LocalTime {
    return org.threeten.bp.LocalTime.of(hour, minute, second, nanoOfSecond)
}

@JvmName("convertFromJava")
fun org.threeten.bp.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

@JvmName("convertToJava")
fun DateTime.toJavaLocalDateTime(): org.threeten.bp.LocalDateTime {
    return org.threeten.bp.LocalDateTime.of(year, month.number, dayOfMonth, hour, minute, second, nanoOfSecond)
}

@JvmName("convertFromJava")
fun org.threeten.bp.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        UtcOffset(offset.totalSeconds.seconds)
    )
}

@JvmName("convertToJava")
fun OffsetDateTime.toJavaOffsetDateTime(): org.threeten.bp.OffsetDateTime {
    return org.threeten.bp.OffsetDateTime.of(
        org.threeten.bp.LocalDateTime.of(year, month.number, dayOfMonth, hour, minute, second, nanoOfSecond),
        org.threeten.bp.ZoneOffset.ofTotalSeconds(offset.totalSeconds.value)
    )
}

@JvmName("convertFromJava")
fun org.threeten.bp.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset(totalSeconds.seconds)
}

@JvmName("convertToJava")
fun UtcOffset.toJavaZoneOffset(): org.threeten.bp.ZoneOffset {
    return org.threeten.bp.ZoneOffset.ofTotalSeconds(totalSeconds.value)
}

@JvmName("convertFromJava")
fun org.threeten.bp.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

@JvmName("convertToJava")
fun Duration.toJavaDuration(): org.threeten.bp.Duration {
    return org.threeten.bp.Duration.ofSeconds(seconds.value, nanoOfSeconds.value.toLong())
}

@JvmName("convertFromJava")
fun org.threeten.bp.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

@JvmName("convertToJava")
fun Period.toJavaPeriod(): org.threeten.bp.Period {
    return org.threeten.bp.Period.of(years.value, months.value, days.value)
}

@JvmName("convertFromJava")
fun org.threeten.bp.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

@JvmName("convertToJava")
fun YearMonth.toJavaYearMonth(): org.threeten.bp.YearMonth {
    return org.threeten.bp.YearMonth.of(year, month.number)
}