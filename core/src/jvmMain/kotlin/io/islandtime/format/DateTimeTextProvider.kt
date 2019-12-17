package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.TimeZone
import io.islandtime.locale.Locale
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    private val monthSymbols = ConcurrentHashMap<Locale, HashMap<TextStyle, Map<Long, String>>>()
    private val narrowEraTextSymbols = arrayOf("B", "A")
    private val englishLongEraTextSymbols = arrayOf("Before Christ", "Anno Domini")

    override fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        if (value !in 1L..7L) {
            throw DateTimeException("'$value' is outside the supported day of week field range")
        }

        val symbols = DateFormatSymbols.getInstance(locale)
        val index = (value.toInt() + 1) % 7

        return when (style) {
            TextStyle.FULL,
            TextStyle.FULL_STANDALONE -> symbols.weekdays[index]
            TextStyle.SHORT,
            TextStyle.SHORT_STANDALONE -> symbols.shortWeekdays[index]
            TextStyle.NARROW,
            TextStyle.NARROW_STANDALONE -> symbols.weekdays[index].substring(0, 1)
        }
    }

    override fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        fun mapMonthSymbols(symbols: Array<String>): Map<Long, String> {
            val valueToSymbolMap = hashMapOf<Long, String>()
            symbols.sliceArray(Calendar.JANUARY..Calendar.DECEMBER)
                .forEachIndexed { index, s -> valueToSymbolMap[index + 1L] = s }
            return valueToSymbolMap
        }

        fun standaloneSymbolMapFromDateFormat(pattern: String): Map<Long, String> {
            val valueToSymbolMap = hashMapOf<Long, String>()
            val dateFormat = SimpleDateFormat(pattern, locale)

            for (index in Calendar.JANUARY..Calendar.DECEMBER) {
                valueToSymbolMap[index + 1L] = dateFormat.run {
                    calendar.set(Calendar.MONTH, index)
                    format(calendar.time)
                }
            }

            return valueToSymbolMap
        }

        if (value !in 1L..12L) {
            throw DateTimeException("'$value' is outside the supported month of year field range")
        }

        val localeSymbols = monthSymbols.getOrElse(locale) {
            val symbols = DateFormatSymbols.getInstance(locale)

            val fullMap = mapMonthSymbols(symbols.months)
            val shortMap = mapMonthSymbols(symbols.shortMonths)
            val narrowMap = fullMap.mapValues { it.value.substring(0, 1) }

            val localeMap = hashMapOf(
                TextStyle.FULL to fullMap,
                TextStyle.FULL_STANDALONE to standaloneSymbolMapFromDateFormat("LLLL"),
                TextStyle.SHORT to shortMap,
                TextStyle.SHORT_STANDALONE to standaloneSymbolMapFromDateFormat("LLL"),
                TextStyle.NARROW to narrowMap,
                TextStyle.NARROW_STANDALONE to narrowMap
            )

            monthSymbols.putIfAbsent(locale, localeMap)
            monthSymbols.getValue(locale)
        }

        return localeSymbols.getValue(style)[value]
    }

    override fun amPmTextFor(value: Long, locale: Locale): String? {
        if (value !in 0L..1L) {
            throw DateTimeException("'$value' is outside the supported AM/PM range")
        }

        val symbols = DateFormatSymbols.getInstance(locale)
        return symbols.amPmStrings[value.toInt()]
    }

    override fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        if (value !in 0L..1L) {
            throw DateTimeException("'$value' is outside the supported era field range")
        }

        val eraSymbols = when (style) {
            TextStyle.FULL, TextStyle.FULL_STANDALONE -> if (locale.language == Locale.ENGLISH.language) {
                englishLongEraTextSymbols
            } else {
                DateFormatSymbols.getInstance(locale).eras
            }
            TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> DateFormatSymbols.getInstance(locale).eras
            TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> narrowEraTextSymbols
        }

        return eraSymbols[value.toInt()]
    }

    override fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        return if (zone is TimeZone.FixedOffset || !zone.isValid || style.isGeneric()) {
            null
        } else {
            val javaTzStyle = if (style.isShort()) java.util.TimeZone.SHORT else java.util.TimeZone.LONG
            val isDaylightSaving = style.isDaylightSaving()

            return java.util.TimeZone.getTimeZone(zone.id).getDisplayName(isDaylightSaving, javaTzStyle, locale)
        }
    }
}