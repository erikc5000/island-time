package io.islandtime.format.dsl

import io.islandtime.calendar.WeekProperty
import io.islandtime.format.TemporalFormatter
import io.islandtime.format.TextStyle
import io.islandtime.format.internal.parseDateTimePatternTo
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeProperty
import io.islandtime.properties.UtcOffsetProperty

@IslandTimeFormatDsl
interface DateTimeFormatBuilder : LiteralFormatBuilder {
    /**
     * Appends the textual representation of an era in a specific style.
     *
     * The property [DateProperty.Era] is required during formatting.
     */
    fun era(style: TextStyle)

    /**
     * Appends the year of the era, padding the start with zero as necessary to satisfy [minLength].
     *
     * The property [DateProperty.YearOfEra] is required during formatting.
     */
    fun yearOfEra(minLength: Int = 1, maxLength: Int = 19)

    /**
     * Appends the two-digit year of the era.
     *
     * The property [DateProperty.YearOfEra] is required during formatting.
     */
    fun twoDigitYearOfEra()

    /**
     * Appends the week-based year, padding the start with zero as necessary to satisfy [minLength].
     *
     * The property [WeekProperty.LocalizedWeekBasedYear] is required during formatting.
     */
    fun weekBasedYear(minLength: Int = 1, maxLength: Int = 19)

    /**
     * Appends the two-digit week-based year.
     *
     * The property [WeekProperty.LocalizedWeekBasedYear] is required during formatting.
     */
    fun twoDigitWeekBasedYear()

    /**
     * Appends a year, padding the start with zero as necessary to satisfy [minLength].
     *
     * The property [DateProperty.Year] is required during formatting.
     */
    fun year(minLength: Int = 1, maxLength: Int = 19)

    /**
     * Appends the month of year number.
     *
     * The property [DateProperty.MonthOfYear] is required during formatting.
     */
    fun monthNumber(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the localized name of the month in a specific style.
     *
     * The property [DateProperty.MonthOfYear] is required during formatting.
     */
    fun monthName(style: TextStyle)

    /**
     * Appends the week of the week-based year.
     *
     * The property [WeekProperty.LocalizedWeekOfYear] is required during formatting.
     */
    fun weekOfWeekBasedYear(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the week of the month.
     *
     * The property [WeekProperty.LocalizedWeekOfMonth] is required during formatting.
     */
    fun weekOfMonth(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the day of the year value with a fixed number of digits.
     *
     * The property [DateProperty.DayOfYear] is required during formatting.
     */
    fun dayOfYear(minLength: Int = 1, maxLength: Int = 3)

    /**
     * Appends the day of the month value with a fixed number of digits.
     *
     * The property [DateProperty.DayOfMonth] is required during formatting.
     */
    fun dayOfMonth(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the ISO day of week number with a fixed number of digits.
     *
     * The property [DateProperty.DayOfWeek] is required during formatting.
     */
    fun dayOfWeekNumber(length: Int = 1)

    /**
     * Appends the localized day of week number with a fixed number of digits.
     *
     * The property [WeekProperty.LocalizedDayOfWeek] is required during formatting.
     */
    fun localizedDayOfWeekNumber(length: Int = 1)

    /**
     * Appends the localized name of the day of the week in the specified style.
     *
     * The property [DateProperty.DayOfWeek] is required during formatting.
     */
    fun dayOfWeekName(style: TextStyle)

    /**
     * Appends the day of the week in month number.
     */
    fun dayOfWeekInMonth()

    /**
     * Appends the textual representation of the AM or PM of the day.
     *
     * The property [TimeProperty.AmPmOfDay] is required during formatting.
     */
    fun amPm()

    /**
     * Appends the hour of the day with a fixed number of digits.
     *
     * The property [TimeProperty.HourOfDay] is required during formatting.
     */
    fun hourOfDay(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the hour of AM-PM with a fixed number of digits.
     *
     * The property [TimeProperty.HourOfAmPm] is required during formatting.
     */
    fun hourOfAmPm(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the clock hour of the day with a fixed number of digits.
     *
     * The property [TimeProperty.ClockHourOfDay] is required during formatting.
     */
    fun clockHourOfDay(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the clock hour of AM-PM with a fixed number of digits.
     *
     * The property [TimeProperty.ClockHourOfAmPm] is required during formatting.
     */
    fun clockHourOfAmPm(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the minute of the hour with a fixed number of digits.
     *
     * The property [TimeProperty.MinuteOfHour] is required during formatting.
     */
    fun minuteOfHour(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the second of the minute with a fixed number of digits.
     *
     * The property [TimeProperty.SecondOfMinute] is required during formatting.
     */
    fun secondOfMinute(minLength: Int = 1, maxLength: Int = 2)

    /**
     * Appends the fractional second of the minute.
     *
     * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any fractional part will
     * be associated with [TimeProperty.NanosecondOfSecond]. The decimal separator character will be determined by the
     * [TemporalFormatter.Settings].
     */
    fun fractionalSecondOfMinute(
        minWholeLength: Int = 1,
        maxWholeLength: Int = 2,
        minFractionLength: Int = 0,
        maxFractionLength: Int = 9
    )

    /**
     * Appends the millisecond of the day with a fixed number of digits.
     *
     * The property [TimeProperty.MillisecondOfDay] is required during formatting.
     */
    fun millisecondOfDay(minLength: Int = 1, maxLength: Int = 19)

    /**
     * Appends the nanosecond of the second as a fractional value.
     *
     * The property [TimeProperty.NanosecondOfSecond] is required during formatting.
     */
    fun nanosecondOfSecond(minLength: Int = 1, maxLength: Int = 9)

    /**
     * Appends the UTC offset in an ISO-8601-compatible format.
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
     * Appends the localized UTC offset in either short or long format.
     *
     * The property [UtcOffsetProperty.TotalSeconds] is required during formatting.
     *
     * @param style [TextStyle.SHORT] for short format or [TextStyle.FULL] for long format
     * @throws IllegalArgumentException if style is not [TextStyle.SHORT] or [TextStyle.FULL]
     */
    fun localizedOffset(style: TextStyle)

    /**
     * Appends the time zone ID.
     */
    fun timeZoneId()

    /**
     * Appends the localized time zone name in the specified [style].
     */
    fun timeZoneName(style: TextStyle, generic: Boolean)
}

/**
 * Appends a date-time format pattern.
 *
 * For more information on acceptable patterns, see
 * [Unicode Technical Standard #35](https://unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table).
 */
fun DateTimeFormatBuilder.pattern(pattern: String) {
    if (pattern.isNotEmpty()) {
        parseDateTimePatternTo(this, pattern)
    }
}

/**
 * Appends the year of era, padding the start with zero as necessary to satisfy the minimum length.
 *
 * The property [DateProperty.YearOfEra] is required during formatting.
 */
fun DateTimeFormatBuilder.yearOfEra(length: IntRange) {
    yearOfEra(length.first, length.last)
}

/**
 * Appends the year, padding the start with zero as necessary to satisfy the minimum length.
 *
 * The property [DateProperty.Year] is required during formatting.
 */
fun DateTimeFormatBuilder.year(length: IntRange) {
    year(length.first, length.last)
}

/**
 * Appends the month of year value with a variable number of digits.
 *
 * The property [DateProperty.MonthOfYear] is required during formatting.
 */
fun DateTimeFormatBuilder.monthNumber(length: IntRange) {
    monthNumber(length.first, length.last)
}

/**
 * Appends the day of the year value with a variable number of digits.
 *
 * The property [DateProperty.DayOfYear] is required during formatting.
 */
fun DateTimeFormatBuilder.dayOfYear(length: IntRange) {
    dayOfYear(length.first, length.last)
}

/**
 * Appends the day of the month value with a variable number of digits.
 *
 * The property [DateProperty.DayOfMonth] is required during formatting.
 */
fun DateTimeFormatBuilder.dayOfMonth(length: IntRange) {
    dayOfMonth(length.first, length.last)
}

/**
 * Appends the hour of the day with a variable number of digits.
 *
 * The property [TimeProperty.HourOfDay] is required during formatting.
 */
fun DateTimeFormatBuilder.hourOfDay(length: IntRange) {
    hourOfDay(length.first, length.last)
}

/**
 * Appends the hour of AM-PM with a variable number of digits.
 *
 * The property [TimeProperty.HourOfAmPm] is required during formatting.
 */
fun DateTimeFormatBuilder.hourOfAmPm(length: IntRange) {
    hourOfAmPm(length.first, length.last)
}

/**
 * Appends the clock hour of the day with a variable number of digits.
 *
 * The property [TimeProperty.ClockHourOfDay] is required during formatting.
 */
fun DateTimeFormatBuilder.clockHourOfDay(length: IntRange) {
    clockHourOfDay(length.first, length.last)
}

/**
 * Appends the clock hour of AM-PM with a variable number of digits.
 *
 * The property [TimeProperty.ClockHourOfAmPm] is required during formatting.
 */
fun DateTimeFormatBuilder.clockHourOfAmPm(length: IntRange) {
    clockHourOfAmPm(length.first, length.last)
}

/**
 * Appends the minute of the hour with a variable number of digits.
 *
 * The property [TimeProperty.MinuteOfHour] is required during formatting.
 */
fun DateTimeFormatBuilder.minuteOfHour(length: IntRange) {
    minuteOfHour(length.first, length.last)
}

/**
 * Appends the second of the minute with a variable number of digits.
 *
 * The property [TimeProperty.SecondOfMinute] is required during formatting.
 */
fun DateTimeFormatBuilder.secondOfMinute(length: IntRange) {
    secondOfMinute(length.first, length.last)
}

/**
 * Appends the fractional second of the minute.
 *
 * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any fractional part will
 * be associated with [TimeProperty.NanosecondOfSecond]. The decimal separator character will be determined by the
 * [TemporalFormatter.Settings].
 */
fun DateTimeFormatBuilder.fractionalSecondOfMinute(wholeLength: IntRange = 1..2, fractionLength: IntRange = 0..9) {
    return fractionalSecondOfMinute(wholeLength.first, wholeLength.last, fractionLength.first, fractionLength.last)
}

/**
 * Appends the millisecond of the day with a variable number of digits.
 *
 * The property [TimeProperty.MillisecondOfDay] is required during formatting.
 */
fun DateTimeFormatBuilder.millisecondOfDay(length: IntRange) {
    millisecondOfDay(length.first, length.last)
}

/**
 * Appends the nanosecond of the second as a fractional value.
 *
 * The property [TimeProperty.NanosecondOfSecond] is required during formatting.
 */
fun DateTimeFormatBuilder.nanosecondOfSecond(length: IntRange) {
    nanosecondOfSecond(length.first, length.last)
}
