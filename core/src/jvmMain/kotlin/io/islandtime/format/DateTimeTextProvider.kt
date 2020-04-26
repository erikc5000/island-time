package io.islandtime.format

import io.islandtime.base.DateProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.TimeProperty
import io.islandtime.locale.Locale
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

actual object PlatformDateTimeTextProvider : AbstractDateTimeTextProvider() {
    private val monthText = ConcurrentHashMap<Locale, HashMap<TextStyle, Array<String>>>()
    private val parsableText = ConcurrentHashMap<ParsableTextKey, ParsableTextList>()
    private val narrowEraSymbols = arrayOf("B", "A")
    private val englishLongEraSymbols = arrayOf("Before Christ", "Anno Domini")

    private val descendingTextComparator =
        compareByDescending<Pair<String, Long>> { it.first.length }.thenBy { it.second }

    private data class ParsableTextKey(
        val property: NumberProperty,
        val styles: Set<TextStyle>,
        val locale: Locale
    )

    override fun parsableTextFor(
        property: NumberProperty,
        styles: Set<TextStyle>,
        locale: Locale
    ): ParsableTextList {
        if (styles.isEmpty() || !supports(property)) {
            return emptyList()
        }

        val key = ParsableTextKey(property, styles, locale)

        return parsableText.getOrPut(key) {
            val valueMap = mutableMapOf<String, MutableSet<Long>>()

            styles.forEach { style ->
                allTextFor(property, style, locale).forEachIndexed { index, symbol ->
                    valueMap.getOrPut(symbol) { mutableSetOf() } +=
                        valueForArrayIndex(property, index)
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

    override fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        val symbols = DateFormatSymbols.getInstance(locale)
        val index = if (value == 7L) 1 else value.toInt() + 1

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
        return allMonthTextFor(style, locale)[value.toInt() - 1]
    }

    override fun amPmTextFor(value: Long, locale: Locale): String? {
        return allAmPmTextFor(locale)[value.toInt()]
    }

    override fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        return allEraTextFor(style, locale)[value.toInt()]
    }

    private fun allTextFor(
        property: NumberProperty,
        style: TextStyle,
        locale: Locale
    ): Array<String> {
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
            DateProperty.MonthOfYear,
            DateProperty.DayOfWeek -> index + 1L
            else -> index.toLong()
        }
    }

    private fun allDayOfWeekTextFor(style: TextStyle, locale: Locale): Array<String> {
        val symbols = DateFormatSymbols.getInstance(locale)

        val array = when (style) {
            TextStyle.FULL,
            TextStyle.FULL_STANDALONE -> symbols.weekdays
            TextStyle.SHORT,
            TextStyle.SHORT_STANDALONE -> symbols.shortWeekdays
            TextStyle.NARROW,
            TextStyle.NARROW_STANDALONE -> symbols.weekdays
                .map { if (it.isNotEmpty()) it.substring(0, 1) else it }
                .toTypedArray()
        }

        return arrayOf(
            array[Calendar.MONDAY],
            array[Calendar.TUESDAY],
            array[Calendar.WEDNESDAY],
            array[Calendar.THURSDAY],
            array[Calendar.FRIDAY],
            array[Calendar.SATURDAY],
            array[Calendar.SUNDAY]
        )
    }

    private fun allMonthTextFor(style: TextStyle, locale: Locale): Array<String> {
        return allMonthTextFor(locale).getValue(style)
    }

    private fun allMonthTextFor(locale: Locale): HashMap<TextStyle, Array<String>> {
        fun getNormalText(symbols: Array<String>): Array<String> {
            return symbols.sliceArray(Calendar.JANUARY..Calendar.DECEMBER)
        }

        fun getTextFromDateFormat(pattern: String): Array<String> {
            val dateFormat = SimpleDateFormat(pattern, locale)

            return Array(12) {
                dateFormat.run {
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.MONTH, it)
                    format(calendar.time)
                }
            }
        }

        return monthText.getOrPut(locale) {
            val symbols = DateFormatSymbols.getInstance(locale)
            val fullArray = getNormalText(symbols.months)
            val fullStandaloneArray = getTextFromDateFormat("LLLL")
            val shortArray = getNormalText(symbols.shortMonths)
            val shortStandaloneArray = getTextFromDateFormat("LLL")
            val narrowArray = Array(fullStandaloneArray.size) {
                fullStandaloneArray[it].substring(0, 1)
            }

            hashMapOf(
                TextStyle.FULL to fullArray,
                TextStyle.FULL_STANDALONE to fullStandaloneArray,
                TextStyle.SHORT to shortArray,
                TextStyle.SHORT_STANDALONE to shortStandaloneArray,
                TextStyle.NARROW to narrowArray,
                TextStyle.NARROW_STANDALONE to narrowArray
            )
        }
    }

    private fun allAmPmTextFor(locale: Locale): Array<String> {
        return DateFormatSymbols.getInstance(locale).amPmStrings
    }

    private fun allEraTextFor(style: TextStyle, locale: Locale): Array<String> {
        return when (style) {
            TextStyle.FULL, TextStyle.FULL_STANDALONE -> when (locale.language) {
                Locale.ENGLISH.language -> englishLongEraSymbols
                else -> DateFormatSymbols.getInstance(locale).eras
            }
            TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> {
                DateFormatSymbols.getInstance(locale).eras
            }
            TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> narrowEraSymbols
        }
    }
}