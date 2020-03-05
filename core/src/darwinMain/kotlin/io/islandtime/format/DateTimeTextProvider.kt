package io.islandtime.format

import co.touchlab.stately.isolate.IsolateState
import io.islandtime.DateTimeException
import io.islandtime.base.DateTimeField
import io.islandtime.locale.Locale
import platform.Foundation.*

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    private val narrowEraTextSymbols = listOf("B", "A")
    private val parsableText = IsolateState { hashMapOf<ParsableTextKey, ParsableTextList>() }

    private val descendingTextComparator =
        compareByDescending<Pair<String, Long>> { it.first.length }.thenBy { it.second }

    private data class ParsableTextKey(
        val field: DateTimeField,
        val styles: Set<TextStyle>,
        val locale: Locale
    )

    override fun parsableTextFor(field: DateTimeField, styles: Set<TextStyle>, locale: Locale): ParsableTextList {
        if (styles.isEmpty() || !supports(field)) {
            return emptyList()
        }

        val key = ParsableTextKey(field, styles, locale)

        return parsableText.access {
            it.getOrPut(key) {
                val valueMap = hashMapOf<String, MutableSet<Long>>()

                styles.forEach { style ->
                    allTextFor(field, style, locale)?.forEachIndexed { index, symbol ->
                        valueMap.getOrPut(symbol) { mutableSetOf() } += valueForArrayIndex(field, index)
                    }
                }

                valueMap.mapNotNull {
                    if (it.value.size == 1) {
                        it.key to it.value.first()
                    } else {
                        null
                    }
                }.sortedWith(descendingTextComparator)
            }
        }
    }

    override fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        if (value !in 1L..7L) {
            throw DateTimeException("'$value' is outside the supported day of week field range")
        }

        return allDayOfWeekTextFor(style, locale)?.run {
            val index = if (value == 7L) 0 else value.toInt()
            get(index)
        }
    }

    override fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        if (value !in 1L..12L) {
            throw DateTimeException("'$value' is outside the supported month of year field range")
        }

        return allMonthTextFor(style, locale)?.get(value.toInt() - 1)
    }

    override fun amPmTextFor(value: Long, locale: Locale): String? {
        return withCalendarIn(locale) {
            when (value) {
                0L -> AMSymbol
                1L -> PMSymbol
                else -> throw DateTimeException("'$value' is outside the supported AM/PM range")
            }
        }
    }

    override fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        if (value !in 0L..1L) {
            throw DateTimeException("'$value' is outside the supported era field range")
        }

        return allEraTextFor(style, locale)?.get(value.toInt())
    }

    private fun supports(field: DateTimeField): Boolean {
        return when (field) {
            DateTimeField.MONTH_OF_YEAR,
            DateTimeField.DAY_OF_WEEK,
            DateTimeField.AM_PM_OF_DAY,
            DateTimeField.ERA -> true
            else -> false
        }
    }

    private fun allTextFor(field: DateTimeField, style: TextStyle, locale: Locale): List<String>? {
        return when (field) {
            DateTimeField.MONTH_OF_YEAR -> allMonthTextFor(style, locale)
            DateTimeField.DAY_OF_WEEK -> allDayOfWeekTextFor(style, locale)
            DateTimeField.AM_PM_OF_DAY -> allAmPmTextFor(locale)
            DateTimeField.ERA -> allEraTextFor(style, locale)
            else -> throw IllegalStateException("Unexpected field")
        }
    }

    private fun valueForArrayIndex(field: DateTimeField, index: Int): Long {
        return when (field) {
            DateTimeField.MONTH_OF_YEAR -> index + 1L
            DateTimeField.DAY_OF_WEEK -> if (index == 0) 7L else index.toLong()
            else -> index.toLong()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun allDayOfWeekTextFor(style: TextStyle, locale: Locale): List<String>? {
        return withCalendarIn(locale) {
            when (style) {
                TextStyle.FULL -> weekdaySymbols
                TextStyle.FULL_STANDALONE -> standaloneWeekdaySymbols
                TextStyle.SHORT -> shortWeekdaySymbols
                TextStyle.SHORT_STANDALONE -> shortStandaloneWeekdaySymbols
                TextStyle.NARROW -> veryShortWeekdaySymbols
                TextStyle.NARROW_STANDALONE -> veryShortStandaloneWeekdaySymbols
            } as List<String>
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun allMonthTextFor(style: TextStyle, locale: Locale): List<String>? {
        return withCalendarIn(locale) {
            when (style) {
                TextStyle.FULL -> monthSymbols
                TextStyle.FULL_STANDALONE -> standaloneMonthSymbols
                TextStyle.SHORT -> shortMonthSymbols
                TextStyle.SHORT_STANDALONE -> shortStandaloneMonthSymbols
                TextStyle.NARROW -> veryShortMonthSymbols
                TextStyle.NARROW_STANDALONE -> veryShortStandaloneMonthSymbols
            } as List<String>
        }
    }

    private fun allAmPmTextFor(locale: Locale): List<String>? {
        return withCalendarIn(locale) {
            listOf(AMSymbol, PMSymbol)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun allEraTextFor(style: TextStyle, locale: Locale): List<String>? {
        return when (style) {
            TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> narrowEraTextSymbols
            else -> withCalendarIn(locale) {
                when (style) {
                    TextStyle.FULL, TextStyle.FULL_STANDALONE -> longEraSymbols
                    else -> eraSymbols
                } as List<String>
            }
        }
    }

    private inline fun <T> withCalendarIn(locale: Locale, block: NSCalendar.() -> T): T? {
        return NSCalendar.calendarWithIdentifier(NSCalendarIdentifierISO8601)?.also {
            it.locale = locale
        }?.block()
    }
}