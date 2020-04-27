package io.islandtime.format

import co.touchlab.stately.isolate.IsolateState
import io.islandtime.locale.Locale
import platform.Foundation.*

/**
 * The default provider of localized date-time format styles for the current platform.
 */
actual object PlatformDateTimeFormatProvider : DateTimeFormatProvider {
    private val styleCache = IsolateState { hashMapOf<StyleCacheKey, TemporalFormatter>() }
    private val skeletonCache = IsolateState { hashMapOf<SkeletonCacheKey, TemporalFormatter?>() }

    private data class StyleCacheKey(
        val dateStyle: FormatStyle?,
        val timeStyle: FormatStyle?,
        val locale: String
    )

    private data class SkeletonCacheKey(val skeleton: String, val locale: String)

    override fun formatterFor(
        dateStyle: FormatStyle?,
        timeStyle: FormatStyle?,
        locale: Locale
    ): TemporalFormatter {
        require(dateStyle != null || timeStyle != null) {
            "At least one of the provided styles must be non-null"
        }

        val adjustedLocale = locale.withDefaultCalendar()

        return styleCache.access { cache ->
            val key = StyleCacheKey(dateStyle, timeStyle, adjustedLocale.localeIdentifier)

            cache.getOrPut(key) {
                val darwinFormatter = NSDateFormatter().also {
                    it.locale = adjustedLocale
                    it.calendar = NSCalendar.ISO
                    it.dateStyle = dateStyle.toNSDateFormatterStyle()
                    it.timeStyle = timeStyle.toNSDateFormatterStyle()
                }

                DateTimeFormatter(darwinFormatter.dateFormat)
            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun formatterFor(skeleton: String, locale: Locale): TemporalFormatter? {
        val adjustedLocale = locale.withDefaultCalendar()

        return skeletonCache.access { cache ->
            val key = SkeletonCacheKey(skeleton, adjustedLocale.localeIdentifier)

            cache.getOrPut(key) {
                NSDateFormatter.dateFormatFromTemplate(skeleton, 0u, adjustedLocale)
                    ?.let { pattern -> DateTimeFormatter(pattern) }
            }
        }
    }
}

private val NSCalendar.Companion.ISO
    get() = checkNotNull(NSCalendar.calendarWithIdentifier(NSCalendarIdentifierISO8601)) {
        "Missing ISO-8601 calendar"
    }

private fun NSLocale.withDefaultCalendar(): NSLocale {
    return NSLocale.componentsFromLocaleIdentifier(localeIdentifier)
        .filterKeys { it != "calendar" }
        .let { NSLocale.localeIdentifierFromComponents(it) }
        .let { NSLocale(it) }
}

private fun FormatStyle?.toNSDateFormatterStyle(): NSDateFormatterStyle {
    return when (this) {
        FormatStyle.FULL -> NSDateFormatterFullStyle
        FormatStyle.LONG -> NSDateFormatterLongStyle
        FormatStyle.MEDIUM -> NSDateFormatterMediumStyle
        FormatStyle.SHORT -> NSDateFormatterShortStyle
        null -> NSDateFormatterNoStyle
    }
}