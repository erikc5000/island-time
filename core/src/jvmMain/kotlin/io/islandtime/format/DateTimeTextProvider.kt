package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.base.DateTimeField
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

actual object PlatformDateTimeTextProvider : DateTimeTextProvider {
    private val monthText = ConcurrentHashMap<Locale, HashMap<TextStyle, Array<String>>>()
    private val parsableText = ConcurrentHashMap<ParsableTextKey, ParsableTextList>()
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
        return DateFormatSymbols.getInstance(locale).amPmStrings
    }

    private fun allEraTextFor(style: TextStyle, locale: Locale): Array<String> {
        return when (style) {
            TextStyle.FULL, TextStyle.FULL_STANDALONE -> if (locale.language == Locale.ENGLISH.language) {
                englishLongEraSymbols
            } else {
                DateFormatSymbols.getInstance(locale).eras
            }
            TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> DateFormatSymbols.getInstance(locale).eras
            TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> narrowEraSymbols
        }
    }
}