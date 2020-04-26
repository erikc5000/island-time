package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.locale.Locale
import io.islandtime.DateTimeException
import io.islandtime.base.DateProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.TimeProperty

typealias ParsableTextList = List<Pair<String, Long>>

/**
 * An abstraction that allows localized date-time text to be supplied from different data sources.
 */
interface DateTimeTextProvider {
    /**
     * Get localized text for the specified property, value, style, and locale.
     *
     * @param property the property to get text for
     * @param value the value of the property
     * @param style the style of the text
     * @param locale the locale
     * @return the localized text or `null` if unavailable
     * @throws DateTimeException if the value if out of range for the specified property
     */
    fun textFor(property: NumberProperty, value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Get a list of all localized text in a particular style along with the values associated that
     * text. The list will be sorted in descending order by the length of text, making it suitable
     * for parsing.
     *
     * Any text with conflicting values will be excluded. For example, the English narrow month name
     * "M" could be `March` or `May`, so any attempt to parse it would be ambiguous.
     *
     * @param property the property to get text for
     * @param style the style of the text
     * @param locale the locale
     * @return the list of parsable text -- empty if the property is invalid
     */
    fun parsableTextFor(
        property: NumberProperty,
        style: TextStyle,
        locale: Locale
    ): ParsableTextList {
        return parsableTextFor(property, setOf(style), locale)
    }

    /**
     * Get a list of all localized text in a set of styles along with the values associated that
     * text. The list will be sorted in descending order by the length of text, making it suitable
     * for parsing.
     *
     * Any text with conflicting values will be excluded. For example, the English narrow month name
     * "M" could be `March` or `May`, so any attempt to parse it would be ambiguous.
     *
     * @param property the property to get text for
     * @param styles the set of styles to include
     * @param locale the locale
     * @return the list of parsable text -- empty if the property is invalid or no styles are
     * specified
     */
    fun parsableTextFor(
        property: NumberProperty,
        styles: Set<TextStyle>,
        locale: Locale
    ): ParsableTextList

    companion object : DateTimeTextProvider {
        override fun textFor(
            property: NumberProperty,
            value: Long,
            style: TextStyle,
            locale: Locale
        ): String? {
            return IslandTime.dateTimeTextProvider.textFor(property, value, style, locale)
        }

        override fun parsableTextFor(
            property: NumberProperty,
            styles: Set<TextStyle>,
            locale: Locale
        ): ParsableTextList {
            return IslandTime.dateTimeTextProvider.parsableTextFor(property, styles, locale)
        }
    }
}

/**
 * An abstract date time text provider that supports the standard set of available properties.
 */
abstract class AbstractDateTimeTextProvider : DateTimeTextProvider {
    override fun textFor(
        property: NumberProperty,
        value: Long,
        style: TextStyle,
        locale: Locale
    ): String? {
        if (value !in property.valueRange) {
            throw DateTimeException("'$value' is outside the supported range for '$property'")
        }

        return when (property) {
            DateProperty.DayOfWeek -> dayOfWeekTextFor(value, style, locale)
            DateProperty.MonthOfYear -> monthTextFor(value, style, locale)
            TimeProperty.AmPmOfDay -> amPmTextFor(value, locale)
            DateProperty.Era -> eraTextFor(value, style, locale)
            else -> null
        }
    }

    protected open fun supports(property: NumberProperty): Boolean {
        return when (property) {
            DateProperty.MonthOfYear,
            DateProperty.DayOfWeek,
            TimeProperty.AmPmOfDay,
            DateProperty.Era -> true
            else -> false
        }
    }

    /**
     * Get the localized day of the week text for a given ISO day of week number.
     *
     * @param value an ISO day of week number, from Monday (1) to Sunday (7)
     * @param style the style of the text
     * @param locale the locale
     * @return the localized day of week text or `null` if unavailable
     * @throws DateTimeException if the value is not a valid day of the week number
     */
    protected abstract fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Get the localized month text for a given ISO month number.
     *
     * @param value an ISO month number, from January (1) to December (12)
     * @param style the style of the text
     * @param locale the locale
     * @return the localized month text or `null` if unavailable
     * @throws DateTimeException if the value is not a valid month number
     */
    protected abstract fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Get the localized AM/PM text.
     *
     * @param value `0` for AM or `1` for PM
     * @param locale the locale
     * @return the localized AM/PM text or `null` if unavailable
     * @throws DateTimeException if the value is not `0` or `1`
     */
    protected abstract fun amPmTextFor(value: Long, locale: Locale): String?

    /**
     * Get the localized ISO era text.
     *
     * @param value `0` for BCE or `1` for CE
     * @param locale the locale
     * @param style the style of the text
     * @return the localized era text or `null` if unavailable
     * @throws DateTimeException if the value is not `0` or `1`
     */
    protected abstract fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String?
}

/**
 * The default provider of localized date-time text for the current platform.
 */
expect object PlatformDateTimeTextProvider : AbstractDateTimeTextProvider