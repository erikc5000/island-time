package io.islandtime.parser

import io.islandtime.MAX_TIME_ZONE_STRING_LENGTH
import io.islandtime.base.DateTimeField
import io.islandtime.format.TextStyle

/**
 * Parse a year with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.YEAR].
 */
inline fun DateTimeParserBuilder.year(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.YEAR)
        builder()
    }
}

/**
 * Parse a year with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.YEAR].
 */
inline fun DateTimeParserBuilder.year(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.YEAR)
        builder()
    }
}

/**
 * Parse a year of era with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.YEAR_OF_ERA].
 */
inline fun DateTimeParserBuilder.yearOfEra(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.YEAR_OF_ERA)
        builder()
    }
}

/**
 * Parse a year of era with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.YEAR_OF_ERA].
 */
inline fun DateTimeParserBuilder.yearOfEra(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.YEAR_OF_ERA)
        builder()
    }
}

/**
 * Parse an era from its textual representation in any of the specified styles.
 *
 * The result will be associated with [DateTimeField.ERA].
 */
fun DateTimeParserBuilder.era(styles: Set<TextStyle>) {
    localizedText(DateTimeField.ERA, styles)
}

/**
 * Parse an era from its textual representation in a specific style.
 *
 * The result will be associated with [DateTimeField.ERA].
 */
fun DateTimeParserBuilder.era(style: TextStyle) {
    localizedText(DateTimeField.ERA, setOf(style))
}

/**
 * Parse a month of year value with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.MONTH_OF_YEAR].
 */
inline fun DateTimeParserBuilder.monthNumber(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.MONTH_OF_YEAR)
        builder()
    }
}

/**
 * Parse a month of year number with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.MONTH_OF_YEAR].
 */
inline fun DateTimeParserBuilder.monthNumber(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.MONTH_OF_YEAR)
        builder()
    }
}

/**
 * Parse a month from its textual representation in any of the specified styles.
 *
 * The result will be associated with [DateTimeField.MONTH_OF_YEAR].
 */
fun DateTimeParserBuilder.localizedMonth(styles: Set<TextStyle>) {
    localizedText(DateTimeField.MONTH_OF_YEAR, styles)
}

/**
 * Parse a month from its textual representation in a specific style.
 *
 * The result will be associated with [DateTimeField.MONTH_OF_YEAR].
 */
fun DateTimeParserBuilder.localizedMonth(style: TextStyle) {
    localizedText(DateTimeField.MONTH_OF_YEAR, setOf(style))
}

/**
 * Parse a day of the year value with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.DAY_OF_YEAR].
 */
inline fun DateTimeParserBuilder.dayOfYear(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DAY_OF_YEAR)
        builder()
    }
}

/**
 * Parse a day of the year value with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.DAY_OF_YEAR].
 */
inline fun DateTimeParserBuilder.dayOfYear(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DAY_OF_YEAR)
        builder()
    }
}

/**
 * Parse a day of the month value with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.DAY_OF_MONTH].
 */
inline fun DateTimeParserBuilder.dayOfMonth(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DAY_OF_MONTH)
        builder()
    }
}

/**
 * Parse a day of the month value with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.DAY_OF_MONTH].
 */
inline fun DateTimeParserBuilder.dayOfMonth(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DAY_OF_MONTH)
        builder()
    }
}

/**
 * Parse a day of week number with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.DAY_OF_WEEK].
 */
inline fun DateTimeParserBuilder.dayOfWeekNumber(
    length: Int = 1,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DAY_OF_WEEK)
        builder()
    }
}

/**
 * Parse a day of the week from its textual representation in any of the specified styles.
 *
 * The result will be associated with [DateTimeField.DAY_OF_WEEK].
 */
fun DateTimeParserBuilder.localizedDayOfWeek(styles: Set<TextStyle>) {
    localizedText(DateTimeField.DAY_OF_WEEK, styles)
}

/**
 * Parse a day of the week from its textual representation in a specific style.
 *
 * The result will be associated with [DateTimeField.DAY_OF_WEEK].
 */
fun DateTimeParserBuilder.localizedDayOfWeek(style: TextStyle) {
    localizedText(DateTimeField.DAY_OF_WEEK, setOf(style))
}

/**
 * Parse the AM or PM of the day from its textual representation.
 *
 * The result will be associated with [DateTimeField.AM_PM_OF_DAY].
 */
fun DateTimeParserBuilder.amPm() {
    localizedText(DateTimeField.AM_PM_OF_DAY, setOf(TextStyle.FULL))
}

/**
 * Parse an hour of the day with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.HOUR_OF_DAY].
 */
inline fun DateTimeParserBuilder.hourOfDay(
    length: IntRange = 1..2,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.HOUR_OF_DAY)
        builder()
    }
}

/**
 * Parse an hour of the day with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.HOUR_OF_DAY].
 */
inline fun DateTimeParserBuilder.hourOfDay(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.HOUR_OF_DAY)
        builder()
    }
}

/**
 * Parse a minute of the hour with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.MINUTE_OF_HOUR].
 */
inline fun DateTimeParserBuilder.minuteOfHour(
    length: IntRange = 1..2,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.MINUTE_OF_HOUR)
        builder()
    }
}

/**
 * Parse a minute of the hour with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.MINUTE_OF_HOUR].
 */
inline fun DateTimeParserBuilder.minuteOfHour(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.MINUTE_OF_HOUR)
        builder()
    }
}

/**
 * Parse a second of the minute with a variable number of digits.
 *
 * The result will be associated with [DateTimeField.SECOND_OF_MINUTE].
 */
inline fun DateTimeParserBuilder.secondOfMinute(
    length: IntRange = 1..2,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.SECOND_OF_MINUTE)
        builder()
    }
}

/**
 * Parse a second of the minute with a fixed number of digits.
 *
 * The result will be associated with [DateTimeField.SECOND_OF_MINUTE].
 */
inline fun DateTimeParserBuilder.secondOfMinute(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.SECOND_OF_MINUTE)
        builder()
    }
}

/**
 * Parse a fractional second of the minute.
 *
 * The number of whole seconds will be associated with [DateTimeField.SECOND_OF_MINUTE] while any fractional part
 * will be associated with [DateTimeField.NANOSECOND_OF_SECOND]. The decimal separator character will be determined by
 * the [DateTimeParserSettings].
 */
inline fun DateTimeParserBuilder.fractionalSecondOfMinute(
    wholeLength: IntRange = 1..2,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    decimalNumber(wholeLength, fractionLength, fractionScale) {
        associateWith(DateTimeField.SECOND_OF_MINUTE, DateTimeField.NANOSECOND_OF_SECOND)
        builder()
    }
}

/**
 * Parse a fractional second of the minute with a fixed number of characters representing the whole second.
 *
 * The number of whole seconds will be associated with [DateTimeField.SECOND_OF_MINUTE] while any fractional part
 * will be associated with [DateTimeField.NANOSECOND_OF_SECOND]. The decimal separator character will be determined by
 * the [DateTimeParserSettings].
 */
inline fun DateTimeParserBuilder.fractionalSecondOfMinute(
    wholeLength: Int,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    fractionalSecondOfMinute(wholeLength..wholeLength, fractionLength, fractionScale, builder)
}

inline fun DateTimeParserBuilder.utcOffsetHours(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.UTC_OFFSET_HOURS)
        builder()
    }
}

inline fun DateTimeParserBuilder.utcOffsetMinutes(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.UTC_OFFSET_MINUTES)
        builder()
    }
}

inline fun DateTimeParserBuilder.utcOffsetSeconds(
    length: Int,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.UTC_OFFSET_SECONDS)
        builder()
    }
}

/**
 * Parses a number's sign and populates [DateTimeField.UTC_OFFSET_SIGN] with `-1L`, if negative or `1L`, if positive.
 */
fun DateTimeParserBuilder.utcOffsetSign() {
    sign {
        associateWith(DateTimeField.UTC_OFFSET_SIGN)
    }
}

/**
 * Parses the character 'Z' and populates [DateTimeField.UTC_OFFSET_TOTAL_SECONDS] with `0L`.
 */
fun DateTimeParserBuilder.utcDesignator() {
    literal('Z') {
        onParsed { fields[DateTimeField.UTC_OFFSET_TOTAL_SECONDS] = 0L }
    }
}

/**
 * Parses a number's sign and populates [DateTimeField.PERIOD_SIGN] with `-1L`, if negative or `1L`, if positive.
 */
fun DateTimeParserBuilder.periodSign() {
    sign {
        associateWith(DateTimeField.PERIOD_SIGN)
    }
}

/**
 * Parse a period of whole years.
 *
 * The number of years will be associated with [DateTimeField.PERIOD_OF_YEARS].
 */
inline fun DateTimeParserBuilder.periodOfYears(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.PERIOD_OF_YEARS)
        builder()
    }
}

/**
 * Parse a period of whole months.
 *
 * The number of months will be associated with [DateTimeField.PERIOD_OF_MONTHS].
 */
inline fun DateTimeParserBuilder.periodOfMonths(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.PERIOD_OF_MONTHS)
        builder()
    }
}

/**
 * Parse a period of whole weeks.
 *
 * The number of weeks will be associated with [DateTimeField.PERIOD_OF_WEEKS].
 */
inline fun DateTimeParserBuilder.periodOfWeeks(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.PERIOD_OF_WEEKS)
        builder()
    }
}

/**
 * Parse a period of whole days.
 *
 * The number of days will be associated with [DateTimeField.PERIOD_OF_DAYS].
 */
inline fun DateTimeParserBuilder.periodOfDays(
    length: IntRange = 1..10,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.PERIOD_OF_DAYS)
        builder()
    }
}

/**
 * Parse a duration of whole hours.
 *
 * The number of hours will be associated with [DateTimeField.DURATION_OF_HOURS].
 */
inline fun DateTimeParserBuilder.durationOfHours(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DURATION_OF_HOURS)
        builder()
    }
}

/**
 * Parse a duration of whole minutes.
 *
 * The number of minutes will be associated with [DateTimeField.DURATION_OF_MINUTES].
 */
inline fun DateTimeParserBuilder.durationOfMinutes(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DURATION_OF_MINUTES)
        builder()
    }
}

/**
 * Parse a duration of whole seconds.
 *
 * The number of seconds will be associated with [DateTimeField.DURATION_OF_SECONDS].
 */
inline fun DateTimeParserBuilder.durationOfSeconds(
    length: IntRange = 1..19,
    crossinline builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length) {
        associateWith(DateTimeField.DURATION_OF_SECONDS)
        builder()
    }
}

/**
 * Parse a duration of fractional seconds.
 *
 * The number of whole seconds will be associated with [DateTimeField.DURATION_OF_SECONDS] while any fractional part
 * will be associated with [DateTimeField.NANOSECOND_OF_SECOND]. The decimal separator character will be determined by
 * the [DateTimeParserSettings].
 */
inline fun DateTimeParserBuilder.durationOfFractionalSeconds(
    wholeLength: IntRange = 1..19,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    decimalNumber(wholeLength, fractionLength, fractionScale) {
        associateWith(DateTimeField.DURATION_OF_SECONDS, DateTimeField.NANOSECOND_OF_SECOND)
        builder()
    }
}

/**
 * Parse a time zone region ID.
 *
 * The format should match that defined in the IANA time zone database. The parsed string will be associated with
 * [DateTimeParseResult.timeZoneId].
 */
fun DateTimeParserBuilder.timeZoneId() {
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

        onParsed { timeZoneId = it }
    }
}

/**
 * Parses the string ".." and populates [DateTimeField.IS_UNBOUNDED] with `1L`.
 */
fun DateTimeParserBuilder.unboundedDesignator() {
    literal("..") {
        associateWith(DateTimeField.IS_UNBOUNDED)
    }
}