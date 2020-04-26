@file:Suppress("NewApi")

package io.islandtime.format

import io.islandtime.locale.Locale
import java.time.chrono.IsoChronology
import java.util.concurrent.ConcurrentHashMap
import java.time.format.DateTimeFormatterBuilder as JavaDateTimeFormatterBuilder

actual object PlatformDateTimeFormatProvider : DateTimeFormatProvider {
    private val cache = ConcurrentHashMap<CacheKey, TemporalFormatter>(16)

    private data class CacheKey(
        val locale: Locale,
        val dateStyle: FormatStyle?,
        val timeStyle: FormatStyle?
    )

    override fun formatterFor(
        dateStyle: FormatStyle?,
        timeStyle: FormatStyle?,
        locale: Locale
    ): TemporalFormatter {
        require(dateStyle != null || timeStyle != null) {
            "At least one date or time style must be non-null"
        }

        val key = CacheKey(locale, dateStyle, timeStyle)

        return cache.getOrElse(key) {
            val pattern = JavaDateTimeFormatterBuilder.getLocalizedDateTimePattern(
                dateStyle,
                timeStyle,
                IsoChronology.INSTANCE,
                locale
            )

            val formatter = DateTimeFormatter(pattern)
            cache.putIfAbsent(key, formatter)
            formatter
        }
    }
}