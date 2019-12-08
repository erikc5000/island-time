package io.islandtime.format

import io.islandtime.IslandTime
import io.islandtime.base.DateTimeField
import io.islandtime.locale.Locale

/**
 * An abstraction that allows localized date-time text to be supplied from different data sources.
 */
interface DateTimeTextProvider {
    fun textFor(field: DateTimeField, value: Long, style: TextStyle, locale: Locale): String?

//    fun textIteratorFor(field: DateTimeField, style: TextStyle, locale: Locale): Iterator<Map.Entry<String, Long>>?

    companion object : DateTimeTextProvider by IslandTime.dateTimeTextProvider
}

/**
 * The default provider of localized date-time text for the current platform.
 */
expect object PlatformDateTimeTextProvider : DateTimeTextProvider