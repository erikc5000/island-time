@file:JvmName("Conversions")
@file:JvmMultifileClass
@file:Suppress("NewApi")

package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.base.*
import io.islandtime.base.Temporal
import io.islandtime.measures.seconds
import java.time.chrono.IsoChronology
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor as JavaTemporalAccessor
import java.time.temporal.TemporalField as JavaTemporalField
import java.time.temporal.TemporalQueries as JavaTemporalQueries
import java.time.temporal.TemporalQuery as JavaTemporalQuery
import java.time.temporal.UnsupportedTemporalTypeException as JavaUnsupportedTemporalTypeException

@JvmName("toJavaTemporalAccessor")
fun Temporal.asJavaTemporalAccessor(): JavaTemporalAccessor {
    return object : JavaTemporalAccessor {
        override fun isSupported(field: JavaTemporalField?): Boolean {
            return when (field) {
                is ChronoField -> field.toIslandNumberProperty()?.let { has(it) } ?: false
                null -> false
                else -> field.isSupportedBy(this)
            }
        }

        override fun getLong(field: JavaTemporalField): Long {
            return when (field) {
                is ChronoField -> field.toIslandNumberProperty()?.let {
                    try {
                        get(it)
                    } catch (e: TemporalPropertyException) {
                        null
                    }
                } ?: throw JavaUnsupportedTemporalTypeException("'$field' is not supported")
                else -> field.getFrom(this)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <R : Any?> query(query: JavaTemporalQuery<R>?): R {
            return when (query) {
                JavaTemporalQueries.chronology() -> when (this@asJavaTemporalAccessor) {
                    is Year,
                    is YearMonth,
                    is Date,
                    is DateTime,
                    is OffsetDateTime,
                    is ZonedDateTime,
                    is Month -> IsoChronology.INSTANCE
                    else -> null
                } as R
                JavaTemporalQueries.zoneId(),
                JavaTemporalQueries.zone() -> when {
                    has(TimeZoneProperty.TimeZone) -> get(TimeZoneProperty.TimeZone).toJavaZoneId()
                    query == JavaTemporalQueries.zone() && has(UtcOffsetProperty.TotalSeconds) ->
                        UtcOffset(get(UtcOffsetProperty.TotalSeconds).toInt().seconds).toJavaZoneOffset()
                    else -> null
                } as R
                JavaTemporalQueries.offset() -> when (this@asJavaTemporalAccessor) {
                    is UtcOffset -> toJavaZoneOffset()
                    is OffsetDateTime -> offset.toJavaZoneOffset()
                    is ZonedDateTime -> offset.toJavaZoneOffset()
                    else -> null
                } as R
                JavaTemporalQueries.localDate() -> when (this@asJavaTemporalAccessor) {
                    is Date -> toJavaLocalDate()
                    is DateTime -> date.toJavaLocalDate()
                    is OffsetDateTime -> date.toJavaLocalDate()
                    is ZonedDateTime -> date.toJavaLocalDate()
                    else -> null
                } as R
                JavaTemporalQueries.localTime() -> when (this@asJavaTemporalAccessor) {
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

private fun ChronoField.toIslandNumberProperty(): NumberProperty? {
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