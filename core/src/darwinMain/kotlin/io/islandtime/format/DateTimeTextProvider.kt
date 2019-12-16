package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.TimeZone
import io.islandtime.base.DateTimeField
import io.islandtime.locale.Locale
import platform.Foundation.*

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    override fun textFor(field: DateTimeField, value: Long, style: TextStyle, locale: Locale): String? {
        return when (field) {
            DateTimeField.DAY_OF_WEEK -> dayOfWeekTextFor(value, style, locale)
            DateTimeField.MONTH_OF_YEAR -> monthTextFor(value, style, locale)
            DateTimeField.AM_PM_OF_DAY -> amPmTextFor(value, locale)
            else -> null
        }
    }

//    override fun textIteratorFor(
//        field: DateTimeField,
//        style: TextStyle,
//        locale: Locale
//    ): Iterator<Map.Entry<String, Long>>? {
//        return when (field) {
//            DateTimeField.DAY_OF_WEEK -> dayOfWeekTextListFor(style, locale)?.values
//        }
//    }

    override fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        return dayOfWeekTextListFor(style, locale)?.run {
            if (value !in 1L..7L) {
                throw DateTimeException("'$value' is outside the supported day of week field range")
            }

            val index = if (value == 7L) 0 else value.toInt()
            get(index)
        }
    }

    override fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        return monthTextListFor(style, locale)?.run {
            if (value !in 1L..12L) {
                throw DateTimeException("'$value' is outside the supported month of year field range")
            }

            get(value.toInt() - 1)
        }
    }

    override fun amPmTextFor(value: Long, locale: Locale): String? {
        return withCalendarIn(locale) {
            when (value) {
                0L -> AMSymbol
                1L -> PMSymbol
                else -> throw DateTimeException("'$value' is outside the supported AM/PM range")
            }
        }
    }

    override fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        return if (zone is TimeZone.Region) {
            NSTimeZone.timeZoneWithName(zone.id)?.run {
                val darwinStyle = when (style) {
                    TimeZoneTextStyle.STANDARD -> NSTimeZoneNameStyle.NSTimeZoneNameStyleStandard
                    TimeZoneTextStyle.SHORT_STANDARD -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortStandard
                    TimeZoneTextStyle.DAYLIGHT_SAVING -> NSTimeZoneNameStyle.NSTimeZoneNameStyleDaylightSaving
                    TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortDaylightSaving
                    TimeZoneTextStyle.GENERIC -> NSTimeZoneNameStyle.NSTimeZoneNameStyleGeneric
                    TimeZoneTextStyle.SHORT_GENERIC -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortGeneric
                }

                localizedName(darwinStyle, locale)
            }
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun dayOfWeekTextListFor(style: TextStyle, locale: Locale): List<String>? {
        return withCalendarIn(locale) {
            when (style) {
                TextStyle.FULL -> weekdaySymbols
                TextStyle.FULL_STANDALONE -> standaloneWeekdaySymbols
                TextStyle.SHORT -> shortWeekdaySymbols
                TextStyle.SHORT_STANDALONE -> shortStandaloneWeekdaySymbols
                TextStyle.NARROW -> veryShortWeekdaySymbols
                TextStyle.NARROW_STANDALONE -> veryShortStandaloneWeekdaySymbols
            } as List<String>
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun monthTextListFor(style: TextStyle, locale: Locale): List<String>? {
        return withCalendarIn(locale) {
            when (style) {
                TextStyle.FULL -> monthSymbols
                TextStyle.FULL_STANDALONE -> standaloneMonthSymbols
                TextStyle.SHORT -> shortMonthSymbols
                TextStyle.SHORT_STANDALONE -> shortStandaloneMonthSymbols
                TextStyle.NARROW -> veryShortMonthSymbols
                TextStyle.NARROW_STANDALONE -> veryShortStandaloneMonthSymbols
            } as List<String>
        }
    }
}

private inline fun <T> withCalendarIn(locale: Locale, block: NSCalendar.() -> T): T? {
    return NSCalendar.calendarWithIdentifier(NSCalendarIdentifierISO8601)?.also {
        it.locale = locale
    }?.block()
}