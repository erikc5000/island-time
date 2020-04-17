@file:Suppress("NewApi")

package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.base.*
import java.time.temporal.ChronoField

fun Temporal.asJavaTemporalAccessor(): java.time.temporal.TemporalAccessor {
    return object : java.time.temporal.TemporalAccessor {
        override fun isSupported(field: java.time.temporal.TemporalField?): Boolean {
            return when (field) {
                is ChronoField -> field.toIslandTimeProperty()?.let { has(it) } ?: false
                null -> false
                else -> field.isSupportedBy(this)
            }
        }

        override fun getLong(field: java.time.temporal.TemporalField): Long {
            return when (field) {
                is ChronoField -> field.toIslandTimeProperty()?.let {
                    try {
                        get(it)
                    } catch (e: TemporalPropertyException) {
                        null
                    }
                } ?: throw java.time.temporal.UnsupportedTemporalTypeException("'$field' is not supported")
                else -> field.getFrom(this)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <R : Any?> query(query: java.time.temporal.TemporalQuery<R>?): R {
            return when (query) {
                java.time.temporal.TemporalQueries.chronology() -> when (this@asJavaTemporalAccessor) {
                    is Year,
                    is YearMonth,
                    is Date,
                    is DateTime,
                    is OffsetDateTime,
                    is ZonedDateTime,
                    is Month -> java.time.chrono.IsoChronology.INSTANCE
                    else -> null
                } as R
                java.time.temporal.TemporalQueries.zoneId(),
                java.time.temporal.TemporalQueries.zone() -> when {
                    has(TimeZoneProperty.Id) -> java.time.ZoneId.of(get(TimeZoneProperty.Id))
                    query == java.time.temporal.TemporalQueries.zone() && has(UtcOffsetProperty.TotalSeconds) ->
                        java.time.ZoneId.from(
                            java.time.ZoneOffset.ofTotalSeconds(get(UtcOffsetProperty.TotalSeconds).toInt())
                        )
                    else -> null
                } as R
                java.time.temporal.TemporalQueries.offset() -> when (this@asJavaTemporalAccessor) {
                    is UtcOffset -> toJavaZoneOffset()
                    is OffsetDateTime -> offset.toJavaZoneOffset()
                    is ZonedDateTime -> offset.toJavaZoneOffset()
                    else -> null
                } as R
                java.time.temporal.TemporalQueries.localDate() -> when (this@asJavaTemporalAccessor) {
                    is Date -> toJavaLocalDate()
                    is DateTime -> date.toJavaLocalDate()
                    is OffsetDateTime -> date.toJavaLocalDate()
                    is ZonedDateTime -> date.toJavaLocalDate()
                    else -> null
                } as R
                java.time.temporal.TemporalQueries.localTime() -> when (this@asJavaTemporalAccessor) {
                    is Time -> toJavaLocalTime()
                    is DateTime -> time.toJavaLocalTime()
                    is OffsetTime -> time.toJavaLocalTime()
                    is OffsetDateTime -> time.toJavaLocalTime()
                    is ZonedDateTime -> time.toJavaLocalTime()
                    else -> null
                } as R
                else -> super.query(query)
            }
        }
    }
}

private fun ChronoField.toIslandTimeProperty(): NumberProperty? {
    return when (this) {
        ChronoField.INSTANT_SECONDS -> TimePointProperty.SecondOfUnixEpoch
        ChronoField.YEAR -> DateProperty.Year
        ChronoField.YEAR_OF_ERA -> DateProperty.YearOfEra
        ChronoField.ERA -> DateProperty.Era
        ChronoField.MONTH_OF_YEAR -> DateProperty.MonthOfYear
        ChronoField.EPOCH_DAY -> DateProperty.DayOfUnixEpoch
        ChronoField.DAY_OF_YEAR -> DateProperty.DayOfYear
        ChronoField.DAY_OF_MONTH -> DateProperty.DayOfMonth
        ChronoField.DAY_OF_WEEK -> DateProperty.DayOfWeek
        ChronoField.AMPM_OF_DAY -> TimeProperty.AmPmOfDay
        ChronoField.HOUR_OF_AMPM -> TimeProperty.HourOfAmPm
        ChronoField.CLOCK_HOUR_OF_AMPM -> TimeProperty.ClockHourOfAmPm
        ChronoField.HOUR_OF_DAY -> TimeProperty.HourOfDay
        ChronoField.CLOCK_HOUR_OF_DAY -> TimeProperty.ClockHourOfDay
        ChronoField.MINUTE_OF_HOUR -> TimeProperty.MinuteOfHour
        ChronoField.SECOND_OF_DAY -> TimeProperty.SecondOfDay
        ChronoField.SECOND_OF_MINUTE -> TimeProperty.SecondOfMinute
        ChronoField.NANO_OF_SECOND -> TimeProperty.NanosecondOfSecond
        ChronoField.NANO_OF_DAY -> TimeProperty.NanosecondOfDay
        ChronoField.MICRO_OF_SECOND -> TimeProperty.MicrosecondOfSecond
        ChronoField.MICRO_OF_DAY -> TimeProperty.MicrosecondOfDay
        ChronoField.MILLI_OF_SECOND -> TimeProperty.MillisecondOfSecond
        ChronoField.MILLI_OF_DAY -> TimeProperty.MillisecondOfDay
        ChronoField.OFFSET_SECONDS -> UtcOffsetProperty.TotalSeconds
        else -> null
    }
}