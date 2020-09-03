package io.islandtime.parser.internal

import io.islandtime.MAX_TIME_ZONE_STRING_LENGTH
import io.islandtime.calendar.WeekProperty
import io.islandtime.format.SignStyle
import io.islandtime.format.TextStyle
import io.islandtime.format.dsl.FormatOption
import io.islandtime.format.dsl.IsoFormat
import io.islandtime.parser.TemporalParser
import io.islandtime.parser.dsl.*
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeProperty
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty

class DateTimeParserBuilderImpl : DateTimeParserBuilder {
    private val temporalParserBuilder = TemporalParserBuilderImpl()

    override fun literal(char: Char) {
        temporalParserBuilder.literal(char)
    }

    override fun literal(string: String) {
        temporalParserBuilder.literal(string)
    }

    override fun era(style: TextStyle) {
        temporalParserBuilder.localizedDateTimeText(DateProperty.Era, setOf(style))
    }

    override fun yearOfEra(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(DateProperty.YearOfEra)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun twoDigitYearOfEra() {
        twoDigitYearOfEra {}
    }

    override fun twoDigitYearOfEra(builder: TwoDigitYearParserBuilder.() -> Unit) {
        temporalParserBuilder.wholeNumber(length = 2) {
            onParsed { value ->
                result[DateProperty.YearOfEra] = getTwoDigitYear(
                    value,
                    TwoDigitYearParserBuilder().apply(builder).earliestYear()
                )
            }
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun weekBasedYear(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(WeekProperty.LocalizedWeekBasedYear)
        }
    }

    override fun twoDigitWeekBasedYear() {
        twoDigitWeekBasedYear { }
    }

    override fun twoDigitWeekBasedYear(builder: TwoDigitYearParserBuilder.() -> Unit) {
        temporalParserBuilder.wholeNumber(length = 2) {
            onParsed { value ->
                result[WeekProperty.LocalizedWeekBasedYear] = getTwoDigitYear(
                    value,
                    TwoDigitYearParserBuilder().apply(builder).earliestYear()
                )
            }
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun year(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(DateProperty.Year)
        }
    }

    override fun monthNumber(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(DateProperty.MonthOfYear)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun monthName(style: TextStyle) {
        temporalParserBuilder.localizedDateTimeText(DateProperty.MonthOfYear, setOf(style))
    }

    override fun weekOfWeekBasedYear(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(WeekProperty.LocalizedWeekOfWeekBasedYear)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun weekOfMonth(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(WeekProperty.LocalizedWeekOfMonth)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun dayOfYear(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(DateProperty.DayOfYear)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun dayOfMonth(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(DateProperty.DayOfMonth)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun dayOfWeekNumber(length: Int) {
        temporalParserBuilder.wholeNumber(length = length) {
            associateWith(DateProperty.DayOfWeek)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun localizedDayOfWeekNumber(length: Int) {
        temporalParserBuilder.wholeNumber(length = length) {
            associateWith(WeekProperty.LocalizedDayOfWeek)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun dayOfWeekName(style: TextStyle) {
        temporalParserBuilder.localizedDateTimeText(DateProperty.DayOfWeek, setOf(style))
    }

    override fun dayOfWeekInMonth() {
        temporalParserBuilder.wholeNumber(length = 1) {
            associateWith(DateProperty.DayOfWeekInMonth)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun amPm() {
        temporalParserBuilder.localizedDateTimeText(TimeProperty.AmPmOfDay, setOf(TextStyle.FULL))
    }

    override fun hourOfDay(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(TimeProperty.HourOfDay)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun hourOfAmPm(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(TimeProperty.HourOfAmPm)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun clockHourOfDay(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(TimeProperty.ClockHourOfDay)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun clockHourOfAmPm(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(TimeProperty.ClockHourOfAmPm)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun minuteOfHour(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(TimeProperty.MinuteOfHour)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun secondOfMinute(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(TimeProperty.SecondOfMinute)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun fractionalSecondOfMinute(
        minWholeLength: Int,
        maxWholeLength: Int,
        minFractionLength: Int,
        maxFractionLength: Int
    ) {
        temporalParserBuilder.decimalNumber(
            minWholeLength,
            maxWholeLength,
            minFractionLength,
            maxFractionLength
        ) {
            associateWith(TimeProperty.SecondOfMinute, TimeProperty.NanosecondOfSecond)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun millisecondOfDay(minLength: Int, maxLength: Int) {
        temporalParserBuilder.wholeNumber(minLength, maxLength) {
            associateWith(TimeProperty.MillisecondOfDay)
            enforceSignStyle(SignStyle.NEVER)
        }
    }

    override fun nanosecondOfSecond(minLength: Int, maxLength: Int) {
        temporalParserBuilder.fraction(minLength, maxLength) {
            associatedWith(TimeProperty.NanosecondOfSecond)
        }
    }

    override fun offset(
        format: IsoFormat,
        useUtcDesignatorWhenZero: Boolean,
        minutes: FormatOption,
        seconds: FormatOption
    ) {
        temporalParserBuilder.createOffsetParser(format, useUtcDesignatorWhenZero, minutes)
    }

    override fun localizedOffset(style: TextStyle) {
        TODO("Not yet implemented")
    }

    override fun timeZoneId() {
        temporalParserBuilder.text(minLength = 1, maxLength = MAX_TIME_ZONE_STRING_LENGTH) {
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

            onParsed { result[TimeZoneProperty.Id] = it }
        }
    }

    override fun timeZoneName(style: TextStyle, generic: Boolean) {
        TODO("Support parsing localized time zone names")
    }

    override fun optional(builder: DateTimeParserBuilder.() -> Unit) {
        val child = DateTimeParserBuilderImpl().apply(builder).build()
        temporalParserBuilder.optional(child)
    }

    override fun anyOf(vararg builders: DateTimeParserBuilder.() -> Unit) {
        val childParsers = Array(builders.size) { DateTimeParserBuilderImpl().apply(builders[it]).build() }
        temporalParserBuilder.anyOf(*childParsers)
    }

    override fun anyOf(vararg parsers: TemporalParser) {
        temporalParserBuilder.anyOf(*parsers)
    }

    override fun caseSensitive(builder: DateTimeParserBuilder.() -> Unit) {
        val child = DateTimeParserBuilderImpl().apply(builder).build()
        temporalParserBuilder.caseInsensitive(child)
    }

    override fun caseInsensitive(builder: DateTimeParserBuilder.() -> Unit) {
        val child = DateTimeParserBuilderImpl().apply(builder).build()
        temporalParserBuilder.caseSensitive(child)
    }

    override fun use(parser: TemporalParser) {
        temporalParserBuilder.use(parser)
    }

    fun build(): TemporalParser = temporalParserBuilder.build()
}

private fun TemporalParserBuilder.optionalUnless(condition: Boolean, block: TemporalParserBuilder.() -> Unit) {
    if (!condition) {
        optional(block)
    } else {
        block()
    }
}

private fun TemporalParserBuilder.createOffsetParser(
    format: IsoFormat,
    useUtcDesignatorWhenZero: Boolean,
    minutes: FormatOption
) {
    val hoursMinutesSecondsParser = createOffsetHoursMinutesSecondsParser(format, minutes)

    if (useUtcDesignatorWhenZero) {
        anyOf(UTC_OFFSET_ZERO_DESIGNATOR_PARSER, hoursMinutesSecondsParser)
    } else {
        use(hoursMinutesSecondsParser)
    }
}

private val UTC_OFFSET_ZERO_DESIGNATOR_PARSER = TemporalParser {
    literal('Z') {
        onParsed { result[UtcOffsetProperty.TotalSeconds] = 0 }
    }
}

private fun createOffsetHoursMinutesSecondsParser(
    format: IsoFormat,
    minutes: FormatOption
): TemporalParser = TemporalParser {
    sign {
        associateWith(UtcOffsetProperty.Sign)
    }

    wholeNumber(length = 2) {
        enforceSignStyle(SignStyle.NEVER)
        associateWith(UtcOffsetProperty.Hours)
    }

    optionalUnless(minutes == FormatOption.ALWAYS) {
        if (format == IsoFormat.EXTENDED) +':'

        wholeNumber(length = 2) {
            enforceSignStyle(SignStyle.NEVER)
            associateWith(UtcOffsetProperty.Minutes)
        }

        optional {
            if (format == IsoFormat.EXTENDED) +':'

            wholeNumber(length = 2) {
                enforceSignStyle(SignStyle.NEVER)
                associateWith(UtcOffsetProperty.Seconds)
            }
        }
    }
}

private fun getTwoDigitYear(value: Long, earliestYear: Int): Long {
    val centuryPart = earliestYear - (earliestYear % 100)

    var adjustedValue = if (earliestYear > 0) {
        centuryPart + value;
    } else {
        centuryPart - value;
    }

    if (adjustedValue < earliestYear) {
        adjustedValue += 100;
    }

    return adjustedValue
}
