package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.base.DateTimeField
import io.islandtime.locale.Locale
import io.islandtime.DateTimeException

typealias ParsableTextList = List<Pair<String, Long>>

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
     * @throws DateTimeException if the value is out of range for the specified field
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

    /**
     * Get a list of all localized text in a particular style along with the values associated that text. The list will
     * be sorted in descending order by the length of text, making it suitable for parsing.
     *
     * Any text with conflicting values will be excluded. For example, the English narrow month name "M" could be
     * `March` or `May`, so any attempt to parse it would be ambiguous.
     *
     * @param field the field to get text for
     * @param style the style of the text
     * @param locale the locale
     * @return the list of parsable text -- empty if the field is invalid
     */
    fun parsableTextFor(field: DateTimeField, style: TextStyle, locale: Locale): ParsableTextList {
        return parsableTextFor(field, setOf(style), locale)
    }

    /**
     * Get a list of all localized text in a set of styles along with the values associated that text. The list will
     * be sorted in descending order by the length of text, making it suitable for parsing.
     *
     * Any text with conflicting values will be excluded. For example, the English narrow month name "M" could be
     * `March` or `May`, so any attempt to parse it would be ambiguous.
     *
     * @param field the field to get text for
     * @param styles the set of styles to include
     * @param locale the locale
     * @return the list of parsable text -- empty if the field is invalid or no styles are specified
     */
    fun parsableTextFor(field: DateTimeField, styles: Set<TextStyle>, locale: Locale): ParsableTextList

    /**
     * Get the localized day of the week text for a given ISO day of week number.
     *
     * @param value an ISO day of week number, from Monday (1) to Sunday (7)
     * @param style the style of the text
     * @param locale the locale
     * @return the localized day of week text or `null` if unavailable
     * @throws DateTimeException if the value is not a valid day of the week number
     */
    fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? = null

    /**
     * Get the localized month text for a given ISO month number.
     *
     * @param value an ISO month number, from January (1) to December (12)
     * @param style the style of the text
     * @param locale the locale
     * @return the localized month text or `null` if unavailable
     * @throws DateTimeException if the value is not a valid month number
     */
    fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String? = null

    /**
     * Get the localized AM/PM text.
     *
     * @param value `0` for AM or `1` for PM
     * @param locale the locale
     * @return the localized AM/PM text or `null` if unavailable
     * @throws DateTimeException if the value is not `0` or `1`
     */
    fun amPmTextFor(value: Long, locale: Locale): String? = null

    /**
     * Get the localized ISO era text.
     *
     * @param value `0` for BCE or `1` for CE
     * @param locale the locale
     * @param style the style of the text
     * @return the localized era text or `null` if unavailable
     * @throws DateTimeException if the value is not `0` or `1`
     */
    fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String? = null

    companion object : DateTimeTextProvider {
        override fun textFor(field: DateTimeField, value: Long, style: TextStyle, locale: Locale): String? {
            return IslandTime.dateTimeTextProvider.textFor(field, value, style, locale)
        }

        override fun parsableTextFor(field: DateTimeField, styles: Set<TextStyle>, locale: Locale): ParsableTextList {
            return IslandTime.dateTimeTextProvider.parsableTextFor(field, styles, locale)
        }

        override fun amPmTextFor(value: Long, locale: Locale): String? {
            return IslandTime.dateTimeTextProvider.amPmTextFor(value, locale)
        }

        override fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
            return IslandTime.dateTimeTextProvider.dayOfWeekTextFor(value, style, locale)
        }

        override fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String? {
            return IslandTime.dateTimeTextProvider.eraTextFor(value, style, locale)
        }

        override fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String? {
            return IslandTime.dateTimeTextProvider.monthTextFor(value, style, locale)
        }

        override fun parsableTextFor(field: DateTimeField, style: TextStyle, locale: Locale): ParsableTextList {
            return IslandTime.dateTimeTextProvider.parsableTextFor(field, style, locale)
        }
    }
}

/**
 * The default provider of localized date-time text for the current platform.
 */
expect object PlatformDateTimeTextProvider : DateTimeTextProvider
