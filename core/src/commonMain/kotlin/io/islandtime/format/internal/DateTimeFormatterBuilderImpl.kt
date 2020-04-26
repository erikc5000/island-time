package io.islandtime.format.internal

import io.islandtime.base.DateProperty
import io.islandtime.base.Temporal
import io.islandtime.base.TimeProperty
import io.islandtime.base.TimeZoneProperty
import io.islandtime.format.*

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
        predicate: (temporal: Temporal) -> Boolean,
        builder: DateTimeFormatterBuilder.() -> Unit
    ) {
        val child = DateTimeFormatterBuilderImpl().apply(builder).build()

        if (child != EmptyFormatter) {
            use(OnlyIfFormatter(predicate, child))
        }
    }

    override fun pattern(pattern: String) {
        if (pattern.isNotEmpty()) {
            parseDateTimePatternTo(this, pattern)
        }
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
            valueMapper = { it % 100 }
        }
    }

    override fun year(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.Year, minLength, maxLength)
    }

    override fun monthNumber(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(DateProperty.MonthOfYear, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun monthName(style: TextStyle) {
        temporalFormatterBuilder.localizedDateTimeText(DateProperty.MonthOfYear, style)
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
        // FIXME: Use localized day of week number?
        temporalFormatterBuilder.wholeNumber(DateProperty.DayOfWeek, length) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun dayOfWeekName(style: TextStyle) {
        temporalFormatterBuilder.localizedDateTimeText(DateProperty.DayOfWeek, style)
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

    override fun fractionalSecondOfMinute(wholeLength: IntRange, fractionLength: IntRange) {
        temporalFormatterBuilder.decimalNumber(
            TimeProperty.SecondOfMinute,
            TimeProperty.NanosecondOfSecond,
            wholeLength,
            fractionLength
        )
    }

    override fun millisecondOfDay(minLength: Int, maxLength: Int) {
        temporalFormatterBuilder.wholeNumber(TimeProperty.MillisecondOfDay, minLength, maxLength) {
            signStyle = SignStyle.NEVER
        }
    }

    override fun nanosecondOfSecond(fractionLength: IntRange) {
        temporalFormatterBuilder.fraction(TimeProperty.NanosecondOfSecond, fractionLength)
    }

    override fun offset(
        format: IsoFormat,
        useUtcDesignatorWhenZero: Boolean,
        minutes: FormatOption,
        seconds: FormatOption
    ) {
        use(
            UtcOffsetFormatter(
                format,
                useUtcDesignatorWhenZero,
                minutes,
                seconds
            )
        )
    }

    override fun localizedOffset(style: TextStyle) {
        use(LocalizedUtcOffsetFormatter(style))
    }

    override fun timeZoneId() {
        temporalFormatterBuilder.text(TimeZoneProperty.Id)
    }

    override fun timeZoneName(style: TextStyle, generic: Boolean) {
        temporalFormatterBuilder.localizedTimeZoneText(style, generic)
    }

    fun build(): TemporalFormatter {
        return temporalFormatterBuilder.build()
    }
}