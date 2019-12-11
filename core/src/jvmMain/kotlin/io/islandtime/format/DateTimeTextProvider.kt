package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.base.DateTimeField
import io.islandtime.locale.Locale
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    private val monthSymbols = ConcurrentHashMap<Locale, HashMap<TextStyle, Map<Long, String>>>()

    override fun textFor(field: DateTimeField, value: Long, style: TextStyle, locale: Locale): String? {
        return when (field) {
            DateTimeField.DAY_OF_WEEK -> dayOfWeekTextFor(value, style, locale)
            DateTimeField.MONTH_OF_YEAR -> monthTextFor(value, style, locale)
            DateTimeField.AM_PM_OF_DAY -> amPmTextFor(value, locale)
            else -> null
        }
    }

    fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
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

    fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        fun mapMonthSymbols(symbols: Array<String>): Map<Long, String> {
            val valueToSymbolMap = mutableMapOf<Long, String>()
            symbols.sliceArray(Calendar.JANUARY..Calendar.DECEMBER)
                .forEachIndexed { index, s -> valueToSymbolMap[index + 1L] = s }
            return valueToSymbolMap
        }

        fun standaloneSymbolMapFromDateFormat(pattern: String): Map<Long, String> {
            val valueToSymbolMap = mutableMapOf<Long, String>()
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

    fun amPmTextFor(value: Long, locale: Locale): String? {
        if (value !in 0L..1L) {
            throw DateTimeException("'$value' is outside the supported AM/PM range")
        }

        val symbols = DateFormatSymbols.getInstance(locale)
        return symbols.amPmStrings[value.toInt()]
    }

//    private fun dayOfWeekTextListFor(style: TextStyle, locale: Locale): Array<String>? {
//        return with(DateFormatSymbols.getInstance(locale)) {
//            when (style) {
//                TextStyle.FULL -> weekdays
//                TextStyle.FULL_STANDALONE -> weekdays
//                TextStyle.SHORT -> shortWeekdays
//                TextStyle.SHORT_STANDALONE -> shortWeekdays
//                TextStyle.NARROW -> weekdays
//                TextStyle.NARROW_STANDALONE -> weekdays
//            }
//        }
//    }
//
//    private fun monthTextListFor(style: TextStyle, locale: Locale): Array<String>? {
//        return with(DateFormatSymbols.getInstance(locale)) {
//            when (style) {
//                TextStyle.FULL -> months
//                TextStyle.FULL_STANDALONE -> months
//                TextStyle.SHORT -> shortMonths
//                TextStyle.SHORT_STANDALONE -> shortMonths
//                TextStyle.NARROW -> months
//                TextStyle.NARROW_STANDALONE -> months
//            }
//        }
//    }
}