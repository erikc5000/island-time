package io.islandtime.format

import io.islandtime.base.DateProperty
import io.islandtime.base.TimeProperty
import io.islandtime.base.UtcOffsetProperty

@IslandTimeFormatDsl
interface DateTimeFormatBuilder : LiteralFormatBuilder {
    /**
     * Append a date-time format pattern.
     *
     * For more information on acceptable patterns, see
     * [Unicode Technical Standard #35](https://unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table).
     */
    fun pattern(pattern: String)

    /**
     * Append the textual representation of an era in a specific style.
     *
     * The property [DateProperty.Era] is required during formatting.
     */
    fun era(style: TextStyle)

    /**
     * Append a year of era, padding the start with zero as necessary to satisfy [length].
     *
     * The property [DateProperty.YearOfEra] is required during formatting.
     */
    fun yearOfEra(length: IntRange) {
        yearOfEra(length.first, length.last)
    }

    /**
     * Append a year of era, padding the start with zero as necessary to satisfy [minLength].
     *
     * The property [DateProperty.YearOfEra] is required during formatting.
     */
    fun yearOfEra(minLength: Int = 1, maxLength: Int = 19)

    /**
     * Append a two-digit year of era.
     *
     * The property [DateProperty.YearOfEra] is required during formatting.
     */
    fun twoDigitYearOfEra()

    /**
     * Append a year with a variable number of digits.
     *
     * The property [DateProperty.Year] is required during formatting.
     */
    fun year(length: IntRange) {
        year(length.first, length.last)
    }

    /**
     * Append a year with a fixed number of digits.
     *
     * The property [DateProperty.Year] is required during formatting.
     */
    fun year(minLength: Int = 1, maxLength: Int = 19)

    /**
     * Append a month of year value with a variable number of digits.
     *
     * The property [DateProperty.MonthOfYear] is required during formatting.
     */
    fun monthNumber(length: IntRange) {
        monthNumber(length.first, length.last)
    }

    /**
     * Append a month of year number with a fixed number of digits.
     *
     * The property [DateProperty.MonthOfYear] is required during formatting.
     */
    fun monthNumber(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append the localized name of a month in a specific style.
     *
     * The property [DateProperty.MonthOfYear] is required during formatting.
     */
    fun monthName(style: TextStyle)

    /**
     * Append a day of the year value with a variable number of digits.
     *
     * The property [DateProperty.DayOfYear] is required during formatting.
     */
    fun dayOfYear(length: IntRange) {
        dayOfYear(length.first, length.last)
    }

    /**
     * Append a day of the year value with a fixed number of digits.
     *
     * The property [DateProperty.DayOfYear] is required during formatting.
     */
    fun dayOfYear(minLength: Int = 1, maxLength: Int = 3)

    /**
     * Append a day of the month value with a variable number of digits.
     *
     * The property [DateProperty.DayOfMonth] is required during formatting.
     */
    fun dayOfMonth(length: IntRange) {
        dayOfMonth(length.first, length.last)
    }

    /**
     * Append a day of the month value with a fixed number of digits.
     *
     * The property [DateProperty.DayOfMonth] is required during formatting.
     */
    fun dayOfMonth(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append a day of week number with a fixed number of digits.
     *
     * The property [DateProperty.DayOfWeek] is required during formatting.
     */
    fun dayOfWeekNumber(length: Int = 1)

    /**
     * Append the localized name of a day of the week from in the specified style.
     *
     * The property [DateProperty.DayOfWeek] is required during formatting.
     */
    fun dayOfWeekName(style: TextStyle)

    /**
     * Append the textual representation of the AM or PM of the day.
     *
     * The property [TimeProperty.AmPmOfDay] is required during formatting.
     */
    fun amPm()

    /**
     * Append an hour of the day with a variable number of digits.
     *
     * The property [TimeProperty.HourOfDay] is required during formatting.
     */
    fun hourOfDay(length: IntRange) {
        hourOfDay(length.first, length.last)
    }

    /**
     * Append an hour of the day with a fixed number of digits.
     *
     * The property [TimeProperty.HourOfDay] is required during formatting.
     */
    fun hourOfDay(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append an hour of AM-PM with a variable number of digits.
     *
     * The property [TimeProperty.HourOfAmPm] is required during formatting.
     */
    fun hourOfAmPm(length: IntRange) {
        hourOfAmPm(length.first, length.last)
    }

    /**
     * Append an hour of AM-PM with a fixed number of digits.
     *
     * The property [TimeProperty.HourOfAmPm] is required during formatting.
     */
    fun hourOfAmPm(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append a clock hour of the day with a variable number of digits.
     *
     * The property [TimeProperty.ClockHourOfDay] is required during formatting.
     */
    fun clockHourOfDay(length: IntRange) {
        clockHourOfDay(length.first, length.last)
    }

    /**
     * Append a clock hour of the day with a fixed number of digits.
     *
     * The property [TimeProperty.ClockHourOfDay] is required during formatting.
     */
    fun clockHourOfDay(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append a clock hour of AM-PM with a variable number of digits.
     *
     * The property [TimeProperty.ClockHourOfAmPm] is required during formatting.
     */
    fun clockHourOfAmPm(length: IntRange) {
        clockHourOfAmPm(length.first, length.last)
    }

    /**
     * Append a clock hour of AM-PM with a fixed number of digits.
     *
     * The property [TimeProperty.ClockHourOfAmPm] is required during formatting.
     */
    fun clockHourOfAmPm(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append a minute of the hour with a variable number of digits.
     *
     * The property [TimeProperty.MinuteOfHour] is required during formatting.
     */
    fun minuteOfHour(length: IntRange) {
        minuteOfHour(length.first, length.last)
    }

    /**
     * Append a minute of the hour with a fixed number of digits.
     *
     * The property [TimeProperty.MinuteOfHour] is required during formatting.
     */
    fun minuteOfHour(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append a second of the minute with a variable number of digits.
     *
     * The property [TimeProperty.SecondOfMinute] is required during formatting.
     */
    fun secondOfMinute(length: IntRange) {
        secondOfMinute(length.first, length.last)
    }

    /**
     * Append a second of the minute with a fixed number of digits.
     *
     * The property [TimeProperty.SecondOfMinute] is required during formatting.
     */
    fun secondOfMinute(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Append a fractional second of the minute.
     *
     * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any
     * fractional part will be associated with [TimeProperty.NanosecondOfSecond]. The decimal
     * separator character will be determined by the [TemporalFormatter.Settings].
     */
    fun fractionalSecondOfMinute(wholeLength: IntRange = 1..2, fractionLength: IntRange = 0..9)

    /**
     * Append a fractional second of the minute with a fixed number of characters representing the
     * whole second.
     *
     * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any
     * fractional part will be associated with [TimeProperty.NanosecondOfSecond]. The decimal
     * separator character will be determined by the [TemporalFormatter.Settings].
     */
    fun fractionalSecondOfMinute(wholeLength: Int, fractionLength: IntRange = 0..9) {
        return fractionalSecondOfMinute(wholeLength..wholeLength, fractionLength)
    }

    fun fractionalSecondOfMinute(wholeLength: Int, fractionLength: Int) {
        return fractionalSecondOfMinute(
            wholeLength..wholeLength,
            fractionLength..fractionLength
        )
    }

    /**
     * Append a second of the minute with a variable number of digits.
     *
     * The property [TimeProperty.MillisecondOfDay] is required during formatting.
     */
    fun millisecondOfDay(length: IntRange) {
        millisecondOfDay(length.first, length.last)
    }

    /**
     * Append a second of the minute with a fixed number of digits.
     *
     * The property [TimeProperty.MillisecondOfDay] is required during formatting.
     */
    fun millisecondOfDay(minLength: Int = 1, maxLength: Int = 19)

    fun nanosecondOfSecond(fractionLength: IntRange = 1..9)

    fun nanosecondOfSecond(fractionLength: Int) {
        nanosecondOfSecond(fractionLength..fractionLength)
    }

    /**
     * Append a UTC offset in an ISO-8601-compatible format.
     *
     * The property [UtcOffsetProperty.TotalSeconds] is required during formatting.
     */
    fun offset(
        format: IsoFormat = IsoFormat.EXTENDED,
        useUtcDesignatorWhenZero: Boolean = true,
        minutes: FormatOption = FormatOption.ALWAYS,
        seconds: FormatOption = FormatOption.OPTIONAL
    )

    /**
     * Append a localized UTC offset in either short or long format.
     *
     * The property [UtcOffsetProperty.TotalSeconds] is required during formatting.
     *
     * @param style [TextStyle.SHORT] for short format or [TextStyle.FULL] for long format
     * @throws IllegalArgumentException if style is not [TextStyle.SHORT] or [TextStyle.FULL]
     */
    fun localizedOffset(style: TextStyle)

    fun timeZoneId()

    fun timeZoneName(style: TextStyle, generic: Boolean)
}