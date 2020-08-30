package io.islandtime

import io.islandtime.base.*
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.TextStyle
import io.islandtime.locale.Locale
import io.islandtime.properties.*
import io.islandtime.base.throwUnsupportedTemporalPropertyException

/**
 * The AM or PM of the day.
 */
enum class AmPm : Temporal {
    AM,
    PM;

    /**
     * The localized "AM" or "PM" text, if available for the [locale]. The result depends on the configured
     * [DateTimeTextProvider] and may differ between platforms.
     *
     * @param locale the locale
     * @return the localized name or `null` if unavailable for the specified locale
     * @see displayName
     */
    fun localizedName(locale: Locale): String? {
        return DateTimeTextProvider.textFor(TimeProperty.AmPmOfDay, ordinal.toLong(), TextStyle.FULL, locale)
    }

    /**
     * A textual representation of "AM" or "PM" that's suitable for display purposes. The localized name will be
     * returned, if available. If not, the number 0 or 1 will be returned instead.
     *
     * The result depends on the configured [DateTimeTextProvider] and may differ between platforms.
     *
     * @param locale the locale
     * @return the localized text, or number if unavailable for the specified locale
     * @see localizedName
     */
    fun displayName(locale: Locale): String = localizedName(locale) ?: ordinal.toString()

    override fun has(property: TemporalProperty<*>): Boolean {
        return property == TimeProperty.AmPmOfDay || super.has(property)
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            TimeProperty.AmPmOfDay -> ordinal.toLong()
            else -> super.get(property)
        }
    }
}
