package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.base.DateTimeField
import io.islandtime.locale.Locale
import io.islandtime.DateTimeException
import io.islandtime.TimeZone

/**
 * An abstraction that allows localized date-time text to be supplied from different data sources.
 */
interface DateTimeTextProvider {
    /**
     * Get localized text for the specified field, value, style, and locale.
     *
     * @param field the field to get text for
     * @param value the value of the field
     * @param style the style of the text
     * @param locale the locale
     * @return the localized text or `null` if unavailable
     * @throws DateTimeException if the value if out of range for the specified field
     */
    fun textFor(field: DateTimeField, value: Long, style: TextStyle, locale: Locale): String? {
        return when (field) {
            DateTimeField.DAY_OF_WEEK -> dayOfWeekTextFor(value, style, locale)
            DateTimeField.MONTH_OF_YEAR -> monthTextFor(value, style, locale)
            DateTimeField.AM_PM_OF_DAY -> amPmTextFor(value, locale)
            DateTimeField.ERA -> eraTextFor(value, style, locale)
            else -> null
        }
    }

//    fun textIteratorFor(field: DateTimeField, style: TextStyle, locale: Locale): Iterator<Map.Entry<String, Long>>?

    /**
     * Get the localized day of the week text for a given ISO day of week number.
     *
     * @param value an ISO day of week number, from Monday (1) to Sunday (7)
     * @param style the style of the text
     * @param locale the locale
     * @return the localized day of week text or `null` if unavailable
     * @throws DateTimeException if the value is not a valid day of the week number
     */
    fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Get the localized month text for a given ISO month number.
     *
     * @param value an ISO month number, from January (1) to December (12)
     * @param style the style of the text
     * @param locale the locale
     * @return the localized month text or `null` if unavailable
     * @throws DateTimeException if the value is not a valid month number
     */
    fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Get the localized AM/PM text.
     *
     * @param value `0` for AM or `1` for PM
     * @param locale the locale
     * @return the localized AM/PM text or `null` if unavailable
     * @throws DateTimeException if the value is not `0` or `1`
     */
    fun amPmTextFor(value: Long, locale: Locale): String?

    /**
     * Get the localized ISO era text.
     *
     * @param value `0` for BCE or `1` for CE
     * @param locale the locale
     * @param style the style of the text
     * @return the localized era text or `null` if unavailable
     * @throws DateTimeException if the value is not `0` or `1`
     */
    fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Get the localized time zone text.
     *
     * @param zone the time zone
     * @param style the style of the text
     * @param locale the locale
     * @return the localized time zone text or `null` if unavailable in the specified style
     */
    fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String?

    companion object : DateTimeTextProvider by IslandTime.dateTimeTextProvider
}

/**
 * The default provider of localized date-time text for the current platform.
 */
expect object PlatformDateTimeTextProvider : DateTimeTextProvider