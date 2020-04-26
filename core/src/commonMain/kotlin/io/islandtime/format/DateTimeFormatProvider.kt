package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.locale.Locale

/**
 * An abstraction that allows formatters for localized date and time styles to be supplied from
 * different data sources.
 */
interface DateTimeFormatProvider {
    /**
     * Get a localized formatter with the specified date and time styles.
     */
    fun formatterFor(
        dateStyle: FormatStyle?,
        timeStyle: FormatStyle?,
        locale: Locale
    ): TemporalFormatter

    /**
     * Get the best localized formatter from an input skeleton, as defined in
     * [Unicode Technical Standard #35](https://unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table).
     */
    fun formatterFor(skeleton: String, locale: Locale): TemporalFormatter? = null

    companion object : DateTimeFormatProvider {
        override fun formatterFor(
            dateStyle: FormatStyle?,
            timeStyle: FormatStyle?,
            locale: Locale
        ): TemporalFormatter {
            return IslandTime.dateTimeFormatProvider.formatterFor(dateStyle, timeStyle, locale)
        }

        override fun formatterFor(skeleton: String, locale: Locale): TemporalFormatter? {
            return IslandTime.dateTimeFormatProvider.formatterFor(skeleton, locale)
        }
    }
}

/**
 * The default provider of localized date-time format styles for the current platform.
 */
expect object PlatformDateTimeFormatProvider : DateTimeFormatProvider