package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.IslandTime
import io.islandtime.base.NumberProperty
import io.islandtime.locale.Locale
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeProperty

typealias ParsableTextList = List<Pair<String, Long>>

/**
 * An abstraction that allows localized date-time text to be supplied from different data sources.
 */
interface DateTimeTextProvider {
    /**
     * Gets localized text associated with the specified [property], [value], [style], and [locale] &mdash or `null` if
     * unavailable.
     * @throws DateTimeException if the value is out of range for the specified property
     */
    fun getTextFor(property: NumberProperty, value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Gets a list of all localized text available for the specified [property] in a set of [styles] along with the
     * values associated with that text. The list will be sorted in descending order by the length of text, making it
     * suitable for parsing.
     *
     * Any text with conflicting values will be excluded. For example, the English narrow month name "M" could be
     * `March` or `May`, so any attempt to parse it would be ambiguous.
     *
     * @return the list of parsable text &mdash empty if the property is invalid or no styles are specified
     */
    fun getParsableTextFor(
        property: NumberProperty,
        styles: Set<TextStyle>,
        locale: Locale
    ): ParsableTextList

    companion object : DateTimeTextProvider {
        override fun getTextFor(
            property: NumberProperty,
            value: Long,
            style: TextStyle,
            locale: Locale
        ): String? {
            return IslandTime.dateTimeTextProvider.getTextFor(property, value, style, locale)
        }

        override fun getParsableTextFor(
            property: NumberProperty,
            styles: Set<TextStyle>,
            locale: Locale
        ): ParsableTextList {
            return IslandTime.dateTimeTextProvider.getParsableTextFor(property, styles, locale)
        }
    }
}

/**
 * Gets a list of all localized text available for the specified [property] and [style] along with the values associated
 * with that text. The list will be sorted in descending order by the length of text, making it suitable for parsing.
 *
 * Any text with conflicting values will be excluded. For example, the English narrow month name "M" could be `March` or
 * `May`, so any attempt to parse it would be ambiguous.
 *
 * @return the list of parsable text &mdash empty if the property is invalid or no styles are specified
 */
fun DateTimeTextProvider.getParsableTextFor(
    property: NumberProperty,
    style: TextStyle,
    locale: Locale
): ParsableTextList = getParsableTextFor(property, setOf(style), locale)

/**
 * An abstract [DateTimeTextProvider] that supports the standard set of available properties.
 */
abstract class AbstractDateTimeTextProvider : DateTimeTextProvider {
    override fun getTextFor(
        property: NumberProperty,
        value: Long,
        style: TextStyle,
        locale: Locale
    ): String? {
        if (value !in property.valueRange) {
            throw DateTimeException("'$value' is outside the supported range for '$property'")
        }

        return when (property) {
            DateProperty.DayOfWeek -> getDayOfWeekTextFor(value, style, locale)
            DateProperty.MonthOfYear -> getMonthTextFor(value, style, locale)
            TimeProperty.AmPmOfDay -> getAmPmTextFor(value, locale)
            DateProperty.Era -> getEraTextFor(value, style, locale)
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
     * Gets the localized day of week text for a given ISO day of week number, from Monday (1) to Sunday (7). If no text\
     * is available, `null` will be returned.
     * @throws DateTimeException if the value is not a valid day of week number
     */
    protected abstract fun getDayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Gets the localized month text for a given ISO month number, from January (1) to December (12). If no text is
     * available, `null` will be returned.
     * @throws DateTimeException if the value is not a valid month number
     */
    protected abstract fun getMonthTextFor(value: Long, style: TextStyle, locale: Locale): String?

    /**
     * Gets the localized AM/PM text for a given [value], `0` for AM or `1` for PM. If no text is available, `null` will
     * be returned.
     * @throws DateTimeException if the value is not `0` or `1`
     */
    protected abstract fun getAmPmTextFor(value: Long, locale: Locale): String?

    /**
     * Gets the localized ISO era text for a given [value], `0` for BCE or `1` for CE. If no text is available, `null`
     * will be returned.
     * @throws DateTimeException if the value is not `0` or `1`
     */
    protected abstract fun getEraTextFor(value: Long, style: TextStyle, locale: Locale): String?
}

/**
 * The default provider of localized date-time text for the current platform.
 */
expect object PlatformDateTimeTextProvider : AbstractDateTimeTextProvider
