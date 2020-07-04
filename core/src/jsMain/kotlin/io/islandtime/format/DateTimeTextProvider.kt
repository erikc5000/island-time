package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.base.DateTimeField
import io.islandtime.js.internal.intl.DateTimeFormat
import io.islandtime.locale.Locale
import io.islandtime.objectOf
import kotlin.collections.HashMap

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    private val monthText = HashMap<Locale, HashMap<TextStyle, Array<String>>>()
    private val parsableText = HashMap<ParsableTextKey, ParsableTextList>()
    private val narrowEraSymbols = arrayOf("B", "A")
    private val englishLongEraSymbols = arrayOf("Before Christ", "Anno Domini")

    private val descendingTextComparator =
        compareByDescending<Pair<String, Long>> { it.first.length }.thenBy { it.second }

    private data class ParsableTextKey(
        val field: DateTimeField,
        val styles: Set<TextStyle>,
        val locale: Locale
    )

    override fun parsableTextFor(
        field: DateTimeField,
        styles: Set<TextStyle>,
        locale: Locale
    ): ParsableTextList {
        if (styles.isEmpty() || !supports(field)) {
            return emptyList()
        }

        val key = ParsableTextKey(field, styles, locale)

        return parsableText.getOrPut(key) {
            val valueMap = mutableMapOf<String, MutableSet<Long>>()

            styles.forEach { style ->
                allTextFor(field, style, locale).forEachIndexed { index, symbol ->
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

    override fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        if (value !in 1L..7L) {
            throw DateTimeException("'$value' is outside the supported day of week field range")
        }

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
        if (value !in 1L..12L) {
            throw DateTimeException("'$value' is outside the supported month of year field range")
        }

        return allMonthTextFor(style, locale)[value.toInt() - 1]
    }

    override fun amPmTextFor(value: Long, locale: Locale): String? {
        if (value !in 0L..1L) {
            throw DateTimeException("'$value' is outside the supported AM/PM range")
        }

        return allAmPmTextFor(locale)[value.toInt()]
    }

    override fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        if (value !in 0L..1L) {
            throw DateTimeException("'$value' is outside the supported era field range")
        }

        return allEraTextFor(style, locale)[value.toInt()]
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

    private fun allTextFor(field: DateTimeField, style: TextStyle, locale: Locale): Array<String> {
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
            DateTimeField.MONTH_OF_YEAR,
            DateTimeField.DAY_OF_WEEK -> index + 1L
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

        return array
    }

    private fun allMonthTextFor(style: TextStyle, locale: Locale): Array<String> {
        return allMonthTextFor(locale).getValue(style)
    }

    private fun allMonthTextFor(locale: Locale): HashMap<TextStyle, Array<String>> {
        return monthText.getOrPut(locale) {
            val symbols = DateFormatSymbols.getInstance(locale)
            val fullArray = symbols.months
            //TODO support LLLL
            val fullStandaloneArray = symbols.months
            val shortArray = symbols.shortMonthsNames
            //TODO support LLL
            val shortStandaloneArray = symbols.shortMonthsNames
            val narrowArray = Array(fullStandaloneArray.size) { fullStandaloneArray[it].substring(0, 1) }

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
        return DateFormatSymbols.getInstance(locale).apPm
    }

    private fun allEraTextFor(style: TextStyle, locale: Locale): Array<String> {
        val eraStyle = when (style) {
            TextStyle.FULL, TextStyle.FULL_STANDALONE -> "long"
            TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "short"
            TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "narrow"
        }

        val dtf = DateTimeFormat(locale.locale, objectOf {
            era = eraStyle
        })

        //TODO should we resolve a old date too?
        return dtf
            .formatToParts()
            ?.find { it.type == "era" }
            ?.value
            ?.let {
                arrayOf(it)
            } ?: englishLongEraSymbols
    }
}