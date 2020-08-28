package io.islandtime.format.internal

import io.islandtime.TimeZone
import io.islandtime.base.*
import io.islandtime.format.*

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

            onlyIf({ temporal.getOrNull(TimeZoneProperty.TimeZone) is TimeZone.Region }) {
                +'['
                timeZoneId()
                +']'
            }
        }
    }
}

internal class IsoDurationFormatterBuilderImpl {
    fun build(): TemporalFormatter {
        return TemporalFormatter {
            onlyIfTrue(DurationProperty.IsZero) {
                +"PT0S"
            }

            onlyIfFalse(DurationProperty.IsZero) {
                +'P'
                onlyIfPresentAndNonZero(DurationProperty.Years) {
                    wholeNumber(DurationProperty.Years)
                    +'Y'
                }
                onlyIfPresentAndNonZero(DurationProperty.Months) {
                    wholeNumber(DurationProperty.Months)
                    +'M'
                }
                onlyIfPresentAndNonZero(DurationProperty.Weeks) {
                    wholeNumber(DurationProperty.Weeks)
                    +'W'
                }
                onlyIfPresentAndNonZero(DurationProperty.Days) {
                    wholeNumber(DurationProperty.Days)
                    +'D'
                }
                onlyIf({
                    temporal.getOrElse(DurationProperty.Hours) { 0L } != 0L ||
                        temporal.getOrElse(DurationProperty.Minutes) { 0L } != 0L ||
                        temporal.getOrElse(DurationProperty.Seconds) { 0L } != 0L ||
                        temporal.getOrElse(DurationProperty.Nanoseconds) { 0L } != 0L
                }) {
                    +'T'
                }
                onlyIfPresentAndNonZero(DurationProperty.Hours) {
                    wholeNumber(DurationProperty.Hours)
                    +'H'
                }
                onlyIfPresentAndNonZero(DurationProperty.Minutes) {
                    wholeNumber(DurationProperty.Minutes)
                    +'M'
                }
                onlyIf({
                    temporal.getOrElse(DurationProperty.Seconds) { 0L } != 0L ||
                        temporal.getOrElse(DurationProperty.Nanoseconds) { 0L } != 0L
                }) {
                    decimalNumber(DurationProperty.Seconds, DurationProperty.Nanoseconds)
                    +'S'
                }
            }
        }
    }
}