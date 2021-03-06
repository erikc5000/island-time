package io.islandtime.format

import io.islandtime.formatter.DateTimeFormatter
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.locale.Locale
import java.time.chrono.IsoChronology
import java.util.concurrent.ConcurrentHashMap
import java.time.format.DateTimeFormatterBuilder as JavaDateTimeFormatterBuilder

internal actual fun createDefaultDateTimeFormatProvider(): DateTimeFormatProvider = JvmDateTimeFormatProvider()

open class JvmDateTimeFormatProvider : DateTimeFormatProvider {
    private val cache = ConcurrentHashMap<CacheKey, TemporalFormatter>()

    private data class CacheKey(
        val locale: Locale,
        val dateStyle: FormatStyle?,
        val timeStyle: FormatStyle?
    )

    override fun getFormatterFor(
        dateStyle: FormatStyle?,
        timeStyle: FormatStyle?,
        locale: Locale
    ): TemporalFormatter {
        require(dateStyle != null || timeStyle != null) {
            "At least one date or time style must be non-null"
        }

        val key = CacheKey(locale, dateStyle, timeStyle)

        return cache.getOrPut(key) {
            val pattern = JavaDateTimeFormatterBuilder.getLocalizedDateTimePattern(
                dateStyle,
                timeStyle,
                IsoChronology.INSTANCE,
                locale
            )

            DateTimeFormatter(pattern)
        }
    }
}