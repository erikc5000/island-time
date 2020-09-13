package io.islandtime.format

import io.islandtime.base.ProviderProxy
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

    companion object : ProviderProxy<TimeZoneNameProvider>(), TimeZoneNameProvider {
        override fun getNameFor(regionId: String, style: TimeZoneNameStyle, locale: Locale): String? {
            return provider.getNameFor(regionId, style, locale)
        }

        override fun createDefault(): TimeZoneNameProvider = createDefaultTimeZoneNameProvider()
    }
}

internal expect fun createDefaultTimeZoneNameProvider(): TimeZoneNameProvider
