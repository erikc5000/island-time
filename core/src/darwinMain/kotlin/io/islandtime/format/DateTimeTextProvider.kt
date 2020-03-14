package io.islandtime.format

import co.touchlab.stately.isolate.IsolateState
import io.islandtime.DateTimeException
import io.islandtime.base.DateProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.TimeProperty
import io.islandtime.locale.Locale
import platform.Foundation.*

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    private val narrowEraTextSymbols = listOf("B", "A")
    private val parsableText = IsolateState { hashMapOf<ParsableTextKey, ParsableTextList>() }

    private val descendingTextComparator =
        compareByDescending<Pair<String, Long>> { it.first.length }.thenBy { it.second }

    private data class ParsableTextKey(
        val property: NumberProperty,
        val styles: Set<TextStyle>,
        val locale: Locale
    )

    override fun parsableTextFor(property: NumberProperty, styles: Set<TextStyle>, locale: Locale): ParsableTextList {
        if (styles.isEmpty() || !supports(property)) {
            return emptyList()
        }

        val key = ParsableTextKey(property, styles, locale)

        return parsableText.access {
            it.getOrPut(key) {
                val valueMap = hashMapOf<String, MutableSet<Long>>()

            styles.forEach { style ->
                allTextFor(property, style, locale)?.forEachIndexed { index, symbol ->
                    valueMap.getOrPut(symbol) { mutableSetOf() } += valueForArrayIndex(property, index)
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

    private fun supports(property: NumberProperty): Boolean {
        return when (property) {
            DateProperty.MonthOfYear,
            DateProperty.DayOfWeek,
            TimeProperty.AmPmOfDay,
            DateProperty.Era -> true
            else -> false
        }
    }

    private fun allTextFor(property: NumberProperty, style: TextStyle, locale: Locale): List<String>? {
        return when (property) {
            DateProperty.MonthOfYear -> allMonthTextFor(style, locale)
            DateProperty.DayOfWeek -> allDayOfWeekTextFor(style, locale)
            TimeProperty.AmPmOfDay -> allAmPmTextFor(locale)
            DateProperty.Era -> allEraTextFor(style, locale)
            else -> throw IllegalStateException("Unexpected field")
        }
    }

    private fun valueForArrayIndex(property: NumberProperty, index: Int): Long {
        return when (property) {
            DateProperty.MonthOfYear -> index + 1L
            DateProperty.DayOfWeek -> if (index == 0) 7L else index.toLong()
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