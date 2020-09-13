package io.islandtime.format

import io.islandtime.locale.Locale
import java.text.DateFormatSymbols
import java.util.concurrent.ConcurrentHashMap

actual object PlatformTimeZoneNameProvider : TimeZoneNameProvider {
    private val zoneNameCache = ConcurrentHashMap<Locale, Map<String, Array<String>>>()

    override fun getNameFor(regionId: String, style: TimeZoneNameStyle, locale: Locale): String? {
        val namesArray = zoneNameCache
            .getOrPut(locale) { DateFormatSymbols.getInstance(locale).zoneStrings.associateBy { it[0] } }
            .getOrElse(regionId) { return null }

        val index = style.toZoneStringsArrayIndex()

        return if (index < namesArray.size) {
            namesArray[index]
        } else {
            null
        }
    }

    private fun TimeZoneNameStyle.toZoneStringsArrayIndex(): Int = ordinal + 1
}
