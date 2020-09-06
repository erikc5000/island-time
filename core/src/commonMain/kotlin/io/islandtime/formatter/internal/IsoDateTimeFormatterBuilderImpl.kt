package io.islandtime.formatter.internal

import io.islandtime.TimeZone
import io.islandtime.base.getOrNull
import io.islandtime.format.dsl.IsoFormat
import io.islandtime.format.dsl.IsoTimeDesignator
import io.islandtime.formatter.DateTimeFormatter
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.formatter.dsl.IsoDateTimeFormatterBuilder
import io.islandtime.formatter.dsl.onlyIfAbsent
import io.islandtime.formatter.dsl.onlyIfPresent
import io.islandtime.formatter.dsl.onlyIfPresentAndNonZero
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeProperty
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty

@PublishedApi
internal class IsoDateTimeFormatterBuilderImpl : IsoDateTimeFormatterBuilder {
    override var format: IsoFormat = IsoFormat.EXTENDED
    override var timeDesignator: IsoTimeDesignator = IsoTimeDesignator.T

    fun build(): TemporalFormatter {
        val format = format
        val timeDesignator = timeDesignator

        return DateTimeFormatter {
            onlyIfPresent(DateProperty.Year) {
                year(4)

                onlyIfPresent(DateProperty.MonthOfYear) {
                    if (format == IsoFormat.EXTENDED) {
                        +'-'
                    } else {
                        onlyIfAbsent(DateProperty.DayOfMonth) { +'-' }
                    }

                    monthNumber(2)

                    onlyIfPresent(DateProperty.DayOfMonth) {
                        if (format == IsoFormat.EXTENDED) +'-'
                        dayOfMonth(2)
                    }
                }

                onlyIfPresent(TimeProperty.HourOfDay) {
                    timeDesignator.char?.let { +it }
                }
            }

            onlyIfPresent(TimeProperty.HourOfDay) {
                hourOfDay(2)

                onlyIfPresent(TimeProperty.MinuteOfHour) {
                    if (format == IsoFormat.EXTENDED) +':'
                    minuteOfHour(2)

                    onlyIfPresentAndNonZero(TimeProperty.SecondOfMinute) {
                        if (format == IsoFormat.EXTENDED) +':'
                        fractionalSecondOfMinute(2)
                    }
                }
            }

            onlyIfPresent(UtcOffsetProperty.TotalSeconds) {
                offset(format = format)
            }

            onlyIf({ temporal.getOrNull(TimeZoneProperty.TimeZoneObject) is TimeZone.Region }) {
                +'['
                timeZoneId()
                +']'
            }
        }
    }
}
