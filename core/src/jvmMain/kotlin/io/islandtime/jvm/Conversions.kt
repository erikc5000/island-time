@file:JvmName("IslandTimeUtils")

package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.clock.Clock
import io.islandtime.internal.MICROSECONDS_PER_SECOND
import io.islandtime.internal.toIslandInstant
import io.islandtime.measures.*

/**
 * Converts this instant to an equivalent Island Time [Instant].
 */
fun java.time.Instant.toIslandInstant(): Instant {
    return Instant.fromSecondOfUnixEpoch(epochSecond, nano)
}

/**
 * Converts this instant to an equivalent Java `Instant`.
 */
fun Instant.toJavaInstant(): java.time.Instant {
    return java.time.Instant.ofEpochSecond(secondOfUnixEpoch, nanosecond.toLong())
}

/**
 * Converts this date to an equivalent Island Time [Date].
 */
fun java.time.LocalDate.toIslandDate(): Date {
    return Date(year, monthValue, dayOfMonth)
}

/**
 * Converts this date to an equivalent Java `LocalDate`.
 */
fun Date.toJavaLocalDate(): java.time.LocalDate {
    return java.time.LocalDate.of(year, monthNumber, dayOfMonth)
}

/**
 * Converts this time to an equivalent Island Time [Time].
 */
fun java.time.LocalTime.toIslandTime(): Time {
    return Time(hour, minute, second, nano)
}

/**
 * Converts this time to an equivalent Java `LocalTime`.
 */
fun Time.toJavaLocalTime(): java.time.LocalTime {
    return java.time.LocalTime.of(hour, minute, second, nanosecond)
}

/**
 * Converts this date-time to an equivalent Island Time [DateTime].
 */
fun java.time.LocalDateTime.toIslandDateTime(): DateTime {
    return DateTime(
        Date(year, monthValue, dayOfMonth),
        Time(hour, minute, second, nano)
    )
}

/**
 * Converts this date-time to an equivalent Java `LocalDateTime`.
 */
fun DateTime.toJavaLocalDateTime(): java.time.LocalDateTime {
    return java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond)
}

/**
 * Converts this time to an equivalent Island Time [OffsetTime].
 */
fun java.time.OffsetTime.toIslandOffsetTime(): OffsetTime {
    return OffsetTime(
        Time(hour, minute, second, nano),
        UtcOffset.fromTotalSeconds(offset.totalSeconds)
    )
}

/**
 * Converts this time to an equivalent Java `OffsetTime`.
 */
fun OffsetTime.toJavaOffsetTime(): java.time.OffsetTime {
    return java.time.OffsetTime.of(
        java.time.LocalTime.of(hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSecondsValue)
    )
}

/**
 * Converts this date-time to an equivalent Island Time [OffsetDateTime].
 */
fun java.time.OffsetDateTime.toIslandOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        UtcOffset.fromTotalSeconds(offset.totalSeconds)
    )
}

/**
 * Converts this date-time to an equivalent Java `OffsetDateTime`.
 */
fun OffsetDateTime.toJavaOffsetDateTime(): java.time.OffsetDateTime {
    return java.time.OffsetDateTime.of(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSecondsValue)
    )
}

/**
 * Converts this date-time to an equivalent Island Time [ZonedDateTime].
 */
fun java.time.ZonedDateTime.toIslandZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.fromLocal(
        DateTime(
            Date(year, monthValue, dayOfMonth),
            Time(hour, minute, second, nano)
        ),
        zone.toIslandTimeZone(),
        UtcOffset.fromTotalSeconds(offset.totalSeconds)
    )
}

/**
 * Converts this date-time to an equivalent Java `ZonedDateTime`.
 */
fun ZonedDateTime.toJavaZonedDateTime(): java.time.ZonedDateTime {
    return java.time.ZonedDateTime.ofLocal(
        java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond),
        java.time.ZoneId.of(zone.id),
        java.time.ZoneOffset.ofTotalSeconds(offset.totalSecondsValue)
    )
}

/**
 * Converts this UTC offset to an equivalent Island Time [UtcOffset].
 */
fun java.time.ZoneOffset.toIslandUtcOffset(): UtcOffset {
    return UtcOffset.fromTotalSeconds(totalSeconds)
}

/**
 * Converts this UTC offset to an equivalent Java `ZoneOffset`.
 */
fun UtcOffset.toJavaZoneOffset(): java.time.ZoneOffset {
    return java.time.ZoneOffset.ofTotalSeconds(totalSecondsValue)
}

/**
 * Converts this time zone to an equivalent Java `ZoneId`.
 */
fun TimeZone.toJavaZoneId(): java.time.ZoneId {
    return java.time.ZoneId.of(id)
}

/**
 * Converts this time zone to an equivalent Island Time [TimeZone].
 */
fun java.time.ZoneId.toIslandTimeZone(): TimeZone {
    return TimeZone(id)
}

/**
 * Converts this duration to an equivalent Island Time [Duration].
 */
fun java.time.Duration.toIslandDuration(): Duration {
    return durationOf(seconds.seconds, nano.nanoseconds)
}

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Duration.toJavaDuration(): java.time.Duration {
    return java.time.Duration.ofSeconds(seconds.value, nanosecondAdjustment.value)
}

/**
 * Converts this period to an equivalent Island Time [Period].
 */
fun java.time.Period.toIslandPeriod(): Period {
    return periodOf(years.years, months.months, days.days)
}

/**
 * Converts this period to an equivalent Java `Period`.
 */
fun Period.toJavaPeriod(): java.time.Period {
    return java.time.Period.of(years.toInt(), months.toInt(), days.toInt())
}

/**
 * Converts this year-month to an equivalent Island Time [YearMonth].
 */
fun java.time.YearMonth.toIslandYearMonth(): YearMonth {
    return YearMonth(year, monthValue)
}

/**
 * Converts this year-month to an equivalent Java `YearMonth`.
 */
fun YearMonth.toJavaYearMonth(): java.time.YearMonth {
    return java.time.YearMonth.of(year, monthNumber)
}

/**
 * Converts this year to an equivalent Island Time [Year].
 */
fun java.time.Year.toIslandYear(): Year {
    return Year(value)
}

/**
 * Converts this year to an equivalent Java `Year`.
 */
fun Year.toJavaYear(): java.time.Year {
    return java.time.Year.of(value)
}

/**
 * Converts this duration to an equivalent Java `Period`.
 */
fun Centuries.toJavaPeriod(): java.time.Period = this.inYears.toJavaPeriod()

/**
 * Converts this duration to an equivalent Java `Period`.
 */
fun Decades.toJavaPeriod(): java.time.Period = this.inYears.toJavaPeriod()

/**
 * Converts this duration to an equivalent Java `Period`.
 */
fun Years.toJavaPeriod(): java.time.Period = java.time.Period.ofYears(this.toInt())

/**
 * Converts this duration to an equivalent Java `Period`.
 */
fun Months.toJavaPeriod(): java.time.Period = java.time.Period.ofMonths(this.toInt())

/**
 * Converts this duration to an equivalent Java `Period`.
 */
fun Weeks.toJavaPeriod(): java.time.Period = java.time.Period.ofWeeks(this.toInt())

/**
 * Converts this duration to an equivalent Java `Period`.
 */
fun Days.toJavaPeriod(): java.time.Period = java.time.Period.ofDays(this.toInt())

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Days.toJavaDuration(): java.time.Duration = java.time.Duration.ofDays(value)

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Hours.toJavaDuration(): java.time.Duration = java.time.Duration.ofHours(value)

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Minutes.toJavaDuration(): java.time.Duration = java.time.Duration.ofMinutes(value)

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Seconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofSeconds(value)

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Milliseconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofMillis(value)

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Microseconds.toJavaDuration(): java.time.Duration {
    val seconds = this.inWholeSeconds
    val nanoOfSeconds = (this % MICROSECONDS_PER_SECOND).inNanoseconds
    return java.time.Duration.ofSeconds(seconds.value, nanoOfSeconds.value)
}

/**
 * Converts this duration to an equivalent Java `Duration`.
 */
fun Nanoseconds.toJavaDuration(): java.time.Duration = java.time.Duration.ofNanos(value)

/**
 * Makes this clock compatible with Island Time's [Clock] interface.
 */
@JvmName("toIslandClock")
fun java.time.Clock.asIslandClock(): Clock = WrappedJavaClock(clock = this)

private class WrappedJavaClock(private val clock: java.time.Clock) : Clock {
    override fun equals(other: Any?): Boolean {
        return other is WrappedJavaClock && other.clock == clock
    }

    override fun hashCode(): Int = clock.hashCode()
    override fun toString(): String = "$clock (Java)"
    override val zone: TimeZone get() = clock.zone.toIslandTimeZone()
    override fun readPlatformInstant(): PlatformInstant = clock.instant()
    override fun readInstant(): Instant = readPlatformInstant().toIslandInstant()
    override fun readMilliseconds(): Milliseconds = clock.millis().milliseconds
}
