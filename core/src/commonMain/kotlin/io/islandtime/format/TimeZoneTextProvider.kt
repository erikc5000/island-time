package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.TimeZone
import io.islandtime.locale.Locale

/**
 * An abstraction that allows localized time zone names to be supplied from different data sources.
 */
interface TimeZoneTextProvider {
    /**
     * Get the localized time zone text.
     *
     * @param zone the time zone
     * @param style the style of the text
     * @param locale the locale
     * @return the localized time zone text or `null` if unavailable in the specified style
     */
    fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? = null

    companion object : TimeZoneTextProvider {
        override fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
            return IslandTime.timeZoneTextProvider.timeZoneTextFor(zone, style, locale)
        }
    }
}
/**
 * The default provider of localized time zone text for the current platform.
 */
expect object PlatformTimeZoneTextProvider : TimeZoneTextProvider
