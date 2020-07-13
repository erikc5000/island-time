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

        return dateTimeFormatter {
            onlyIf({ it.has(DateProperty.Year) }) {
                year(4)

                onlyIf({ it.has(DateProperty.MonthOfYear) }) {
                    onlyIf({ format == IsoFormat.EXTENDED || !it.has(DateProperty.DayOfMonth) }) {
                        +'-'
                    }
                    monthNumber(2)

                    onlyIf({ it.has(DateProperty.DayOfMonth) }) {
                        if (format == IsoFormat.EXTENDED) +'-'
                        dayOfMonth(2)
                    }
                }

                onlyIf({ it.has(TimeProperty.HourOfDay) }) {
                    timeDesignator.char?.let { +it }
                }
            }

            onlyIf({ it.has(TimeProperty.HourOfDay) }) {
                hourOfDay(2)

                onlyIf({ it.has(TimeProperty.MinuteOfHour) }) {
                    if (format == IsoFormat.EXTENDED) +':'
                    minuteOfHour(2)

                    onlyIf({ it.getOrElse(TimeProperty.SecondOfMinute) { 0L } != 0L }) {
                        if (format == IsoFormat.EXTENDED) +':'
                        fractionalSecondOfMinute(2)
                    }
                }
            }

            onlyIf({ it.has(UtcOffsetProperty.TotalSeconds) }) {
                offset(format = format)
            }

            onlyIf({ it.getOrNull(TimeZoneProperty.TimeZone) is TimeZone.Region }) {
                +'['
                timeZoneId()
                +']'
            }
        }
    }
}

internal class IsoDurationFormatterBuilderImpl {
    fun build(): TemporalFormatter {
        return temporalFormatter {
            onlyIf({ it.get(DurationProperty.IsZero) }) {
                +"PT0S"
            }

            onlyIf({ !it.get(DurationProperty.IsZero) }) {
                +'P'
                onlyIf({ it.getOrElse(DurationProperty.Years) { 0L } != 0L }) {
                    wholeNumber(DurationProperty.Years)
                    +'Y'
                }
                onlyIf({ it.getOrElse(DurationProperty.Months) { 0L } != 0L }) {
                    wholeNumber(DurationProperty.Months)
                    +'M'
                }
                onlyIf({ it.getOrElse(DurationProperty.Weeks) { 0L } != 0L }) {
                    wholeNumber(DurationProperty.Weeks)
                    +'W'
                }
                onlyIf({ it.getOrElse(DurationProperty.Days) { 0L } != 0L }) {
                    wholeNumber(DurationProperty.Days)
                    +'D'
                }
                onlyIf({
                    it.getOrElse(DurationProperty.Hours) { 0L } != 0L ||
                        it.getOrElse(DurationProperty.Minutes) { 0L } != 0L ||
                        it.getOrElse(DurationProperty.Seconds) { 0L } != 0L ||
                        it.getOrElse(DurationProperty.Nanoseconds) { 0L } != 0L
                }) {
                    +'T'
                }
                onlyIf({ it.getOrElse(DurationProperty.Hours) { 0L } != 0L }) {
                    wholeNumber(DurationProperty.Hours)
                    +'H'
                }
                onlyIf({ it.getOrElse(DurationProperty.Minutes) { 0L } != 0L }) {
                    wholeNumber(DurationProperty.Minutes)
                    +'M'
                }
                onlyIf({
                    it.getOrElse(DurationProperty.Seconds) { 0L } != 0L ||
                        it.getOrElse(DurationProperty.Nanoseconds) { 0L } != 0L
                }) {
                    decimalNumber(DurationProperty.Seconds, DurationProperty.Nanoseconds)
                    +'S'
                }
            }
        }
    }
}