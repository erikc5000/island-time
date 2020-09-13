package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.locale.Locale

/**
 * An abstraction that allows formatters for localized date and time styles to be supplied from different data sources.
 */
interface DateTimeFormatProvider {
    /**
     * Gets a localized formatter with the specified date and time styles.
     * @throws IllegalArgumentException if both the date and time style are `null`
     */
    fun getFormatterFor(dateStyle: FormatStyle?, timeStyle: FormatStyle?, locale: Locale): TemporalFormatter

    /**
     * Checks if localized skeletons are supported by this provider.
     */
    val supportsSkeletons: Boolean get() = false

    /**
     * Gets the best localized formatter from an input [skeleton], as defined in
     * [Unicode Technical Standard #35](https://unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table). If
     * skeleton patterns are unsupported or a suitable formatter cannot be provided, `null` will be returned.
     */
    fun getFormatterFor(skeleton: String, locale: Locale): TemporalFormatter? = null

    companion object : DateTimeFormatProvider {
        override fun getFormatterFor(
            dateStyle: FormatStyle?,
            timeStyle: FormatStyle?,
            locale: Locale
        ): TemporalFormatter {
            return IslandTime.dateTimeFormatProvider.getFormatterFor(dateStyle, timeStyle, locale)
        }

        override fun getFormatterFor(skeleton: String, locale: Locale): TemporalFormatter? {
            return IslandTime.dateTimeFormatProvider.getFormatterFor(skeleton, locale)
        }
    }
}

/**
 * The default provider of localized date-time format styles for the current platform.
 */
expect object PlatformDateTimeFormatProvider : DateTimeFormatProvider
