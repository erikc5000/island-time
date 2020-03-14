package io.islandtime.format

import io.islandtime.DateTime
import io.islandtime.base.*

/**
 * Parse a year with a variable number of digits.
 *
 * The result will be associated with [DateProperty.Year].
 */
inline fun DateTimeFormatterBuilder.year(
    length: IntRange = 1..19,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.Year, length) {
        builder()
    }
}

/**
 * Parse a year with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.Year].
 */
inline fun DateTimeFormatterBuilder.year(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.Year, length) {
        builder()
    }
}

/**
 * Parse a year of era with a variable number of digits.
 *
 * The result will be associated with [DateProperty.YearOfEra].
 */
inline fun DateTimeFormatterBuilder.yearOfEra(
    length: IntRange = 1..19,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.YearOfEra, length) {
        builder()
    }
}

/**
 * Parse a year of era with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.YearOfEra].
 */
inline fun DateTimeFormatterBuilder.yearOfEra(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.YearOfEra, length) {
        builder()
    }
}

/**
 * Print the textual representation of an era in a specific style.
 *
 * The property [DateProperty.Era] is required.
 */
fun DateTimeFormatterBuilder.era(style: TextStyle) {
    localizedText(DateProperty.Era, style)
}

/**
 * Parse a month of year value with a variable number of digits.
 *
 * The result will be associated with [DateProperty.MonthOfYear].
 */
inline fun DateTimeFormatterBuilder.monthNumber(
    length: IntRange = 1..19,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.MonthOfYear, length) {
        builder()
    }
}

/**
 * Parse a month of year number with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.MonthOfYear].
 */
inline fun DateTimeFormatterBuilder.monthNumber(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.MonthOfYear, length) {
        builder()
    }
}

/**
 * Print the localized name of a month in a specific style.
 *
 * The property [DateProperty.MonthOfYear] is required.
 */
fun DateTimeFormatterBuilder.localizedMonth(style: TextStyle) {
    localizedText(DateProperty.MonthOfYear, style)
}

/**
 * Parse a day of the year value with a variable number of digits.
 *
 * The result will be associated with [DateProperty.DayOfYear].
 */
inline fun DateTimeFormatterBuilder.dayOfYear(
    length: IntRange = 1..19,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.DayOfYear, length) {
        builder()
    }
}

/**
 * Parse a day of the year value with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.DayOfYear].
 */
inline fun DateTimeFormatterBuilder.dayOfYear(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.DayOfYear, length) {
        builder()
    }
}

/**
 * Parse a day of the month value with a variable number of digits.
 *
 * The result will be associated with [DateProperty.DayOfMonth].
 */
inline fun DateTimeFormatterBuilder.dayOfMonth(
    length: IntRange = 1..19,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.DayOfMonth, length) {
        builder()
    }
}

/**
 * Parse a day of the month value with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.DayOfMonth].
 */
inline fun DateTimeFormatterBuilder.dayOfMonth(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.DayOfMonth, length) {
        builder()
    }
}

/**
 * Parse a day of week number with a fixed number of digits.
 *
 * The result will be associated with [DateProperty.DayOfWeek].
 */
inline fun DateTimeFormatterBuilder.dayOfWeekNumber(
    length: Int = 1,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(DateProperty.DayOfWeek, length) {
        builder()
    }
}

/**
 * Print the localized name of a day of the week from in the specified style.
 *
 * The property [DateProperty.DayOfWeek] is required.
 */
fun DateTimeFormatterBuilder.localizedDayOfWeek(style: TextStyle) {
    localizedText(DateProperty.DayOfWeek, style)
}

/**
 * Print the textual representation of the AM or PM of the day.
 *
 * The property [TimeProperty.AmPmOfDay] is required.
 */
fun DateTimeFormatterBuilder.amPm() {
    localizedText(TimeProperty.AmPmOfDay, TextStyle.FULL)
}

/**
 * Parse an hour of the day with a variable number of digits.
 *
 * The result will be associated with [TimeProperty.HourOfDay].
 */
inline fun DateTimeFormatterBuilder.hourOfDay(
    length: IntRange = 1..2,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(TimeProperty.HourOfDay, length) {
        builder()
    }
}

/**
 * Parse an hour of the day with a fixed number of digits.
 *
 * The result will be associated with [TimeProperty.HourOfDay].
 */
inline fun DateTimeFormatterBuilder.hourOfDay(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(TimeProperty.HourOfDay, length) {
        builder()
    }
}

/**
 * Parse a minute of the hour with a variable number of digits.
 *
 * The result will be associated with [TimeProperty.MinuteOfHour].
 */
inline fun DateTimeFormatterBuilder.minuteOfHour(
    length: IntRange = 1..2,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(TimeProperty.MinuteOfHour, length) {
        builder()
    }
}

/**
 * Parse a minute of the hour with a fixed number of digits.
 *
 * The result will be associated with [TimeProperty.MinuteOfHour].
 */
inline fun DateTimeFormatterBuilder.minuteOfHour(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(TimeProperty.MinuteOfHour, length) {
        builder()
    }
}

/**
 * Parse a second of the minute with a variable number of digits.
 *
 * The result will be associated with [TimeProperty.SecondOfMinute].
 */
inline fun DateTimeFormatterBuilder.secondOfMinute(
    length: IntRange = 1..2,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(TimeProperty.SecondOfMinute, length) {
        builder()
    }
}

/**
 * Parse a second of the minute with a fixed number of digits.
 *
 * The result will be associated with [TimeProperty.SecondOfMinute].
 */
inline fun DateTimeFormatterBuilder.secondOfMinute(
    length: Int,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(TimeProperty.SecondOfMinute, length) {
        builder()
    }
}

/**
 * Parse a fractional second of the minute.
 *
 * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any fractional part will be
 * associated with [TimeProperty.NanosecondOfSecond]. The decimal separator character will be determined by the
 * [DateTimeParserSettings].
 */
inline fun DateTimeFormatterBuilder.fractionalSecondOfMinute(
    wholeLength: IntRange = 1..2,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    decimalNumber(
        TimeProperty.SecondOfMinute,
        TimeProperty.NanosecondOfSecond,
        wholeLength,
        fractionLength,
        fractionScale
    ) {
        builder()
    }
}

/**
 * Parse a fractional second of the minute with a fixed number of characters representing the whole second.
 *
 * The number of whole seconds will be associated with [TimeProperty.SecondOfMinute] while any fractional part will be
 * associated with [TimeProperty.NanosecondOfSecond]. The decimal separator character will be determined by the
 * [DateTimeParserSettings].
 */
inline fun DateTimeFormatterBuilder.fractionalSecondOfMinute(
    wholeLength: Int,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    crossinline builder: NumberFormatterBuilder.() -> Unit = {}
) {
    fractionalSecondOfMinute(wholeLength..wholeLength, fractionLength, fractionScale, builder)
}

//enum class IsoFormat {
//    BASIC,
//    EXTENDED
//}
//
//enum class  {
//    REQUIRED,
//    OPTIONAL,
//}
//
//inline fun DateTimeFormatterBuilder.utcOffsetHours(
//    length: Int,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(UtcOffsetProperty.Hours)
//        builder()
//    }
//}
//
//inline fun DateTimeFormatterBuilder.utcOffsetMinutes(
//    length: Int,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(UtcOffsetProperty.Minutes)
//        builder()
//    }
//}
//
//inline fun DateTimeFormatterBuilder.utcOffsetSeconds(
//    length: Int,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(UtcOffsetProperty.Seconds)
//        builder()
//    }
//}
//
///**
// * Parses a number's sign and populates [UtcOffsetProperty.Sign] with `-1L`, if negative or `1L`, if positive.
// */
//fun DateTimeFormatterBuilder.utcOffsetSign() {
//    sign {
//        associateWith(UtcOffsetProperty.Sign)
//    }
//}
//
///**
// * Parses the character 'Z' and populates [UtcOffsetProperty.TotalSeconds] with `0L`.
// */
//fun DateTimeFormatterBuilder.utcDesignator() {
//    literal('Z') {
//        onParsed { this[UtcOffsetProperty.TotalSeconds] = 0L }
//    }
//}
//
///**
// * Parses a number's sign and populates [DurationProperty.Sign] with `-1L`, if negative or `1L`, if positive.
// */
//fun DateTimeFormatterBuilder.periodSign() {
//    sign {
//        associateWith(DurationProperty.Sign)
//    }
//}
//
///**
// * Parse a period of whole years.
// *
// * The number of years will be associated with [DurationProperty.Years].
// */
//inline fun DateTimeFormatterBuilder.periodOfYears(
//    length: IntRange = 1..10,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(DurationProperty.Years)
//        builder()
//    }
//}
//
///**
// * Parse a period of whole months.
// *
// * The number of months will be associated with [DurationProperty.Months].
// */
//inline fun DateTimeFormatterBuilder.periodOfMonths(
//    length: IntRange = 1..10,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(DurationProperty.Months)
//        builder()
//    }
//}
//
///**
// * Parse a period of whole weeks.
// *
// * The number of weeks will be associated with [DurationProperty.Weeks].
// */
//inline fun DateTimeFormatterBuilder.periodOfWeeks(
//    length: IntRange = 1..10,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(DurationProperty.Weeks)
//        builder()
//    }
//}
//
///**
// * Parse a period of whole days.
// *
// * The number of days will be associated with [DurationProperty.Days].
// */
//inline fun DateTimeFormatterBuilder.periodOfDays(
//    length: IntRange = 1..10,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(DurationProperty.Days)
//        builder()
//    }
//}
//
///**
// * Parse a duration of whole hours.
// *
// * The number of hours will be associated with [DurationProperty.Hours].
// */
//inline fun DateTimeFormatterBuilder.durationOfHours(
//    length: IntRange = 1..19,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(DurationProperty.Hours)
//        builder()
//    }
//}
//
///**
// * Parse a duration of whole minutes.
// *
// * The number of minutes will be associated with [DurationProperty.Minutes].
// */
//inline fun DateTimeFormatterBuilder.durationOfMinutes(
//    length: IntRange = 1..19,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(DurationProperty.Minutes)
//        builder()
//    }
//}
//
///**
// * Parse a duration of whole seconds.
// *
// * The number of seconds will be associated with [DurationProperty.Seconds].
// */
//inline fun DateTimeFormatterBuilder.durationOfSeconds(
//    length: IntRange = 1..19,
//    crossinline builder: WholeNumberFormatterBuilder.() -> Unit = {}
//) {
//    wholeNumber(length) {
//        associateWith(DurationProperty.Seconds)
//        builder()
//    }
//}
//
///**
// * Parse a duration of fractional seconds.
// *
// * The number of whole seconds will be associated with [DurationProperty.Seconds] while any fractional part will be
// * associated with [DurationProperty.Nanoseconds]. The decimal separator character will be determined by the
// * [DateTimeParserSettings].
// */
//inline fun DateTimeFormatterBuilder.durationOfFractionalSeconds(
//    wholeLength: IntRange = 1..19,
//    fractionLength: IntRange = 0..9,
//    fractionScale: Int = 9,
//    crossinline builder: DecimalNumberFormatterBuilder.() -> Unit = {}
//) {
//    decimalNumber(wholeLength, fractionLength, fractionScale) {
//        associateWith(DurationProperty.Seconds, DurationProperty.Nanoseconds)
//        builder()
//    }
//}

/**
 * Parse a time zone region ID.
 *
 * The format should match that defined in the IANA time zone database. The parsed string will be associated with
 * [TimeZoneProperty.Id].
 */
fun DateTimeFormatterBuilder.timeZoneId() {
    string(TimeZoneProperty.Id)
}

/**
 * Parses the string ".." and populates the provided [BooleanProperty] with `true`.
 */
fun DateTimeFormatterBuilder.unboundedDesignator(property: BooleanProperty) {
    literal("..")
}