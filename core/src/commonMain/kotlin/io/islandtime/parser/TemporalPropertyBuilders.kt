package io.islandtime.parser

import io.islandtime.MAX_TIME_ZONE_STRING_LENGTH
import io.islandtime.base.*
import io.islandtime.format.TextStyle

/**
 * Parse a year with a variable number of digits.
 *
 * The result will be associated with [DateProperty.Year].
 */
inline fun TemporalParserBuilder.year(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.Year)
        builder()
    }
}

/**
 * Parse a year with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.Year].
 */
inline fun TemporalParserBuilder.year(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.Year)
        builder()
    }
}

/**
 * Parse a year of era with a variable number of digits.
 *
 * The result will be associated with [DateProperty.YearOfEra].
 */
inline fun TemporalParserBuilder.yearOfEra(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.YearOfEra)
        builder()
    }
}

/**
 * Parse a year of era with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.YearOfEra].
 */
inline fun TemporalParserBuilder.yearOfEra(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.YearOfEra)
        builder()
    }
}

/**
 * Parse an era from its textual representation in any of the specified styles.
 *
 * The result will be associated with [DateProperty.Era].
 */
fun TemporalParserBuilder.era(styles: Set<TextStyle>) {
    localizedText(DateProperty.Era, styles)
}

/**
 * Parse an era from its textual representation in a specific style.
 *
 * The result will be associated with [DateProperty.Era].
 */
fun TemporalParserBuilder.era(style: TextStyle) {
    localizedText(DateProperty.Era, setOf(style))
}

/**
 * Parse a month of year value with a variable number of digits.
 *
 * The result will be associated with [DateProperty.MonthOfYear].
 */
inline fun TemporalParserBuilder.monthNumber(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.MonthOfYear)
        builder()
    }
}

/**
 * Parse a month of year number with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.MonthOfYear].
 */
inline fun TemporalParserBuilder.monthNumber(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.MonthOfYear)
        builder()
    }
}

/**
 * Parse a month from its textual representation in any of the specified styles.
 *
 * The result will be associated with [DateProperty.MonthOfYear].
 */
fun TemporalParserBuilder.localizedMonth(styles: Set<TextStyle>) {
    localizedText(DateProperty.MonthOfYear, styles)
}

/**
 * Parse a month from its textual representation in a specific style.
 *
 * The result will be associated with [DateProperty.MonthOfYear].
 */
fun TemporalParserBuilder.localizedMonth(style: TextStyle) {
    localizedText(DateProperty.MonthOfYear, setOf(style))
}

/**
 * Parse a day of the year value with a variable number of digits.
 *
 * The result will be associated with [DateProperty.DayOfYear].
 */
inline fun TemporalParserBuilder.dayOfYear(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.DayOfYear)
        builder()
    }
}

/**
 * Parse a day of the year value with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.DayOfYear].
 */
inline fun TemporalParserBuilder.dayOfYear(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.DayOfYear)
        builder()
    }
}

/**
 * Parse a day of the month value with a variable number of digits.
 *
 * The result will be associated with [DateProperty.DayOfMonth].
 */
inline fun TemporalParserBuilder.dayOfMonth(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.DayOfMonth)
        builder()
    }
}

/**
 * Parse a day of the month value with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.DayOfMonth].
 */
inline fun TemporalParserBuilder.dayOfMonth(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.DayOfMonth)
        builder()
    }
}

/**
 * Parse a day of week number with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.DayOfWeek].
 */
inline fun TemporalParserBuilder.dayOfWeekNumber(
    length: Int = 1,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateProperty.DayOfWeek)
        builder()
    }
}

/**
 * Parse a day of the week from its textual representation in any of the specified styles.
 *
 * The result will be associated with [DateProperty.DayOfWeek].
 */
fun TemporalParserBuilder.localizedDayOfWeek(styles: Set<TextStyle>) {
    localizedText(DateProperty.DayOfWeek, styles)
}

/**
 * Parse a day of the week from its textual representation in a specific style.
 *
 * The result will be associated with [DateProperty.DayOfWeek].
 */
fun TemporalParserBuilder.localizedDayOfWeek(style: TextStyle) {
    localizedText(DateProperty.DayOfWeek, setOf(style))
}

/**
 * Parse the AM or PM of the day from its textual representation.
 *
 * The result will be associated with [TimeProperty.AmPmOfDay].
 */
fun TemporalParserBuilder.amPm() {
    localizedText(TimeProperty.AmPmOfDay, setOf(TextStyle.FULL))
}

/**
 * Parse an hour of the day with a variable number of digits.
 *
 * The result will be associated with [TimeProperty.HourOfDay].
 */
inline fun TemporalParserBuilder.hourOfDay(
    length: IntRange = 1..2,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(TimeProperty.HourOfDay)
        builder()
    }
}

/**
 * Parse an hour of the day with a fixed number of digits.
 *
 * The result will be associated with [TimeProperty.HourOfDay].
 */
inline fun TemporalParserBuilder.hourOfDay(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(TimeProperty.HourOfDay)
        builder()
    }
}

/**
 * Parse a minute of the hour with a variable number of digits.
 *
 * The result will be associated with [TimeProperty.MinuteOfHour].
 */
inline fun TemporalParserBuilder.minuteOfHour(
    length: IntRange = 1..2,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(TimeProperty.MinuteOfHour)
        builder()
    }
}

/**
 * Parse a minute of the hour with a fixed number of digits.
 *
 * The result will be associated with [TimeProperty.MinuteOfHour].
 */
inline fun TemporalParserBuilder.minuteOfHour(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(TimeProperty.MinuteOfHour)
        builder()
    }
}

/**
 * Parse a second of the minute with a variable number of digits.
 *
 * The result will be associated with [TimeProperty.SecondOfMinute].
 */
inline fun TemporalParserBuilder.secondOfMinute(
    length: IntRange = 1..2,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(TimeProperty.SecondOfMinute)
        builder()
    }
}

/**
 * Parse a second of the minute with a fixed number of digits.
 *
 * The result will be associated with [TimeProperty.SecondOfMinute].
 */
inline fun TemporalParserBuilder.secondOfMinute(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(TimeProperty.SecondOfMinute)
        builder()
    }
}

/**
 * Parse a fractional second of the minute.
 *
 * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any fractional part will be
 * associated with [TimeProperty.NanosecondOfSecond]. The decimal separator character will be determined by the
 * [TemporalParser.Settings].
 */
inline fun TemporalParserBuilder.fractionalSecondOfMinute(
    wholeLength: IntRange = 1..2,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    decimalNumber(wholeLength, fractionLength, fractionScale) {
        associateWith(TimeProperty.SecondOfMinute, TimeProperty.NanosecondOfSecond)
        builder()
    }
}

/**
 * Parse a fractional second of the minute with a fixed number of characters representing the whole second.
 *
 * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any fractional part will be
 * associated with [TimeProperty.NanosecondOfSecond]. The decimal separator character will be determined by the
 * [TemporalParser.Settings].
 */
inline fun TemporalParserBuilder.fractionalSecondOfMinute(
    wholeLength: Int,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    fractionalSecondOfMinute(wholeLength..wholeLength, fractionLength, fractionScale, builder)
}

inline fun TemporalParserBuilder.utcOffsetHours(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(UtcOffsetProperty.Hours)
        builder()
    }
}

inline fun TemporalParserBuilder.utcOffsetMinutes(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(UtcOffsetProperty.Minutes)
        builder()
    }
}

inline fun TemporalParserBuilder.utcOffsetSeconds(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(UtcOffsetProperty.Seconds)
        builder()
    }
}

/**
 * Parses a number's sign and populates [UtcOffsetProperty.Sign] with `-1L`, if negative or `1L`, if positive.
 */
fun TemporalParserBuilder.utcOffsetSign() {
    sign {
        associateWith(UtcOffsetProperty.Sign)
    }
}

/**
 * Parses the character 'Z' and populates [UtcOffsetProperty.TotalSeconds] with `0L`.
 */
fun TemporalParserBuilder.utcDesignator() {
    literal('Z') {
        onParsed { this[UtcOffsetProperty.TotalSeconds] = 0L }
    }
}

/**
 * Parses a number's sign and populates [DurationProperty.Sign] with `-1L`, if negative or `1L`, if positive.
 */
fun TemporalParserBuilder.periodSign() {
    sign {
        associateWith(DurationProperty.Sign)
    }
}

/**
 * Parse a period of whole years.
 *
 * The number of years will be associated with [DurationProperty.Years].
 */
inline fun TemporalParserBuilder.periodOfYears(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DurationProperty.Years)
        builder()
    }
}

/**
 * Parse a period of whole months.
 *
 * The number of months will be associated with [DurationProperty.Months].
 */
inline fun TemporalParserBuilder.periodOfMonths(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DurationProperty.Months)
        builder()
    }
}

/**
 * Parse a period of whole weeks.
 *
 * The number of weeks will be associated with [DurationProperty.Weeks].
 */
inline fun TemporalParserBuilder.periodOfWeeks(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DurationProperty.Weeks)
        builder()
    }
}

/**
 * Parse a period of whole days.
 *
 * The number of days will be associated with [DurationProperty.Days].
 */
inline fun TemporalParserBuilder.periodOfDays(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DurationProperty.Days)
        builder()
    }
}

/**
 * Parse a duration of whole hours.
 *
 * The number of hours will be associated with [DurationProperty.Hours].
 */
inline fun TemporalParserBuilder.durationOfHours(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DurationProperty.Hours)
        builder()
    }
}

/**
 * Parse a duration of whole minutes.
 *
 * The number of minutes will be associated with [DurationProperty.Minutes].
 */
inline fun TemporalParserBuilder.durationOfMinutes(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DurationProperty.Minutes)
        builder()
    }
}

/**
 * Parse a duration of whole seconds.
 *
 * The number of seconds will be associated with [DurationProperty.Seconds].
 */
inline fun TemporalParserBuilder.durationOfSeconds(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DurationProperty.Seconds)
        builder()
    }
}

/**
 * Parse a duration of fractional seconds.
 *
 * The number of whole seconds will be associated with [DurationProperty.Seconds] while any fractional part will be
 * associated with [DurationProperty.Nanoseconds]. The decimal separator character will be determined by the
 * [TemporalParser.Settings].
 */
inline fun TemporalParserBuilder.durationOfFractionalSeconds(
    wholeLength: IntRange = 1..19,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    decimalNumber(wholeLength, fractionLength, fractionScale) {
        associateWith(DurationProperty.Seconds, DurationProperty.Nanoseconds)
        builder()
    }
}

/**
 * Parse a time zone region ID.
 *
 * The format should match that defined in the IANA time zone database. The parsed string will be associated with
 * [TimeZoneProperty.Id].
 */
fun TemporalParserBuilder.timeZoneId() {
    string(length = 1..MAX_TIME_ZONE_STRING_LENGTH) {
        onEachChar { char, index ->
            if (index == 0) {
                when (char) {
                    in 'A'..'Z',
                    in 'a'..'z' -> StringParseAction.ACCEPT_AND_CONTINUE
                    else -> StringParseAction.REJECT_AND_STOP
                }
            } else {
                when (char) {
                    in 'A'..'Z',
                    in 'a'..'z',
                    in '-'..'9',
                    '~',
                    '_',
                    '+' -> StringParseAction.ACCEPT_AND_CONTINUE
                    else -> StringParseAction.REJECT_AND_STOP
                }
            }
        }

        onParsed { this[TimeZoneProperty.Id] = it }
    }
}

/**
 * Parses the string ".." and populates the provided [BooleanProperty] with `true`.
 */
fun TemporalParserBuilder.unboundedDesignator(property: BooleanProperty) {
    literal("..") {
        associateWith(property)
    }
}