package io.islandtime.format

import io.islandtime.IslandTime

/**
 * An abstraction that allows formatters for localized date and time styles to be supplied from different data sources.
 */
interface DateTimeFormatStyleProvider {
    /**
     * Get a formatter for the specified date and time styles.
     */
    fun formatterFor(dateStyle: FormatStyle?, timeStyle: FormatStyle?): DateTimeFormatter

    companion object : DateTimeFormatStyleProvider by IslandTime.dateTimeFormatStyleProvider
}

/**
 * The default provider of localized date-time format styles for the current platform.
 */
expect object PlatformDateTimeFormatStyleProvider : DateTimeFormatStyleProvider