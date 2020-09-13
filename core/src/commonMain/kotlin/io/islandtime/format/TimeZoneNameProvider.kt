package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.locale.Locale

/**
 * An abstraction that allows localized time zone names to be supplied from different data sources.
 */
interface TimeZoneNameProvider {
    /**
     * Gets the localized name associated with a time zone region ID or `null` if unavailable for the specified [style]
     * and [locale].
     */
    fun getNameFor(regionId: String, style: TimeZoneNameStyle, locale: Locale): String?

    companion object : TimeZoneNameProvider {
        override fun getNameFor(regionId: String, style: TimeZoneNameStyle, locale: Locale): String? {
            return IslandTime.timeZoneNameProvider.getNameFor(regionId, style, locale)
        }
    }
}

/**
 * The default provider of localized time zone names for the current platform.
 */
expect object PlatformTimeZoneNameProvider : TimeZoneNameProvider
