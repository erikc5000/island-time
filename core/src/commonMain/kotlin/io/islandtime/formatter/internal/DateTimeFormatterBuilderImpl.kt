package io.islandtime.formatter.internal

import io.islandtime.calendar.WeekProperty
import io.islandtime.format.ContextualTimeZoneNameStyle
import io.islandtime.format.FormatStyle
import io.islandtime.format.SignStyle
import io.islandtime.format.TextStyle
import io.islandtime.format.dsl.FormatOption
import io.islandtime.format.dsl.IsoFormat
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.formatter.dsl.DateTimeFormatterBuilder
import io.islandtime.formatter.dsl.LengthExceededBehavior
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeProperty
import io.islandtime.properties.TimeZoneProperty

@PublishedApi
internal class DateTimeFormatterBuilderImpl : DateTimeFormatterBuilder {
    private val temporalFormatterBuilder = TemporalFormatterBuilderImpl()

    override fun literal(char: Char) {
        temporalFormatterBuilder.literal(char)
    }

    override fun literal(string: String) {
        temporalFormatterBuilder.literal(string)
    }

    override fun use(formatter: TemporalFormatter) {
        temporalFormatterBuilder.use(formatter)
    }

    override fun onlyIf(
        predicate: TemporalFormatter.Context.() -> Boolean,
        builder: DateTimeFormatterBuilder.() -> Unit
    ) {
        val child = DateTimeFormatterBuilderImpl().apply(builder).build()
        temporalFormatterBuilder.onlyIf(predicate, child)
    }

    override fun localizedDate(style: FormatStyle) {
        use(LocalizedDateTimeStyleFormatter(style, null))
    }

    override fun localizedTime(style: FormatStyle) {
        use(LocalizedDateTimeStyleFormatter(null, style))
    }

    override fun localizedDateTime(dateStyle: FormatStyle, timeStyle: FormatStyle) {
        use(LocalizedDateTimeStyleFormatter(dateStyle, timeStyle))
    }

    override fun localizedPattern(skeleton: String) {
        if (skeleton.isNotEmpty()) {
            use(LocalizedDateTimeSkeletonFormatter(skeleton))
        }
    }

    override fun era(style: TextStyle) {
        temporalFormatterBuilder.localizedDateTimeText(DateProperty.Era, style)
    }

    override fun yearOfEra(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.YearOfEra, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun twoDigitYearOfEra() {
        temporalFormatterBuilder.wholeNumber(DateProperty.YearOfEra, 2) {
            signStyle = SignStyle.NEVER
            valueTransform = { it % 100 }
        }
    }

    override fun weekBasedYear(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(WeekProperty.LocalizedWeekBasedYear, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun twoDigitWeekBasedYear() {
        temporalFormatterBuilder.wholeNumber(WeekProperty.LocalizedWeekBasedYear, 2) {
            signStyle = SignStyle.NEVER
            valueTransform = { it % 100 }
        }
    }

    override fun year(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.Year, minLength, maxLength) {
            lengthExceededBehavior = LengthExceededBehavior.SIGN_STYLE_ALWAYS
        }
    }

    override fun monthNumber(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.MonthOfYear, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun monthName(style: TextStyle) {
        temporalFormatterBuilder.localizedDateTimeText(DateProperty.MonthOfYear, style)
    }

    override fun weekOfWeekBasedYear(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(WeekProperty.LocalizedWeekOfWeekBasedYear, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun weekOfMonth(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(WeekProperty.LocalizedWeekOfMonth, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun dayOfYear(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.DayOfYear, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun dayOfMonth(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.DayOfMonth, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun dayOfWeekNumber(length: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.DayOfWeek, length) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun localizedDayOfWeekNumber(length: Int) {
        temporalFormatterBuilder.wholeNumber(WeekProperty.LocalizedDayOfWeek, length) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun dayOfWeekName(style: TextStyle) {
        temporalFormatterBuilder.localizedDateTimeText(DateProperty.DayOfWeek, style)
    }

    override fun dayOfWeekInMonth() {
        temporalFormatterBuilder.wholeNumber(DateProperty.DayOfWeekInMonth, minLength = 1, maxLength = 1) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun amPm() {
        temporalFormatterBuilder.localizedDateTimeText(TimeProperty.AmPmOfDay, TextStyle.FULL)
    }

    override fun hourOfDay(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.HourOfDay, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun hourOfAmPm(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.HourOfAmPm, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun clockHourOfAmPm(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.ClockHourOfAmPm, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun clockHourOfDay(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.ClockHourOfDay, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun minuteOfHour(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.MinuteOfHour, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun secondOfMinute(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.SecondOfMinute, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun fractionalSecondOfMinute(
        minWholeLength: Int,
        maxWholeLength: Int,
        minFractionLength: Int,
        maxFractionLength: Int
    ) {
        temporalFormatterBuilder.decimalNumber(
            TimeProperty.SecondOfMinute,
            TimeProperty.NanosecondOfSecond,
            minWholeLength,
            maxWholeLength,
            minFractionLength,
            maxFractionLength
        )
    }

    override fun millisecondOfDay(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.MillisecondOfDay, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun nanosecondOfSecond(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.fraction(TimeProperty.NanosecondOfSecond, minLength, maxLength)
    }

    override fun offset(
        format: IsoFormat,
        useUtcDesignatorWhenZero: Boolean,
        minutes: FormatOption,
        seconds: FormatOption
    ) {
        use(UtcOffsetFormatter(format, useUtcDesignatorWhenZero, minutes, seconds))
    }

    override fun localizedOffset(style: TextStyle) {
        temporalFormatterBuilder.localizedOffset(style)
    }

    override fun timeZoneId() {
        temporalFormatterBuilder.text(TimeZoneProperty.Id)
    }

    override fun timeZoneName(style: ContextualTimeZoneNameStyle) {
        temporalFormatterBuilder.timeZoneName(style)
    }

    fun build(): TemporalFormatter {
        return temporalFormatterBuilder.build()
    }
}