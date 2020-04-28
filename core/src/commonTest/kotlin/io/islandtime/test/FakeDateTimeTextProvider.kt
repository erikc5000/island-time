package io.islandtime.test

import io.islandtime.DateTimeException
import io.islandtime.base.NumberProperty
import io.islandtime.format.AbstractDateTimeTextProvider
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.ParsableTextList
import io.islandtime.format.TextStyle
import io.islandtime.locale.Locale

object FakeDateTimeTextProvider : AbstractDateTimeTextProvider() {
    override fun parsableTextFor(
        property: NumberProperty,
        styles: Set<TextStyle>,
        locale: Locale
    ): ParsableTextList {
        TODO("Not yet implemented")
    }

    override fun eraTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        return when (value) {
            0L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "B"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "BC"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Before Christ"
            }
            1L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "A"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "AD"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Anno Domini"
            }
            else -> throw DateTimeException("Invalid value")
        }
    }

    override fun monthTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        return when (value) {
            1L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "J ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Jan ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "January ($style)"
            }
            2L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "F ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Feb ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "February ($style)"
            }
            3L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "M ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Mar ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "March ($style)"
            }
            4L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "A ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Apr ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "April ($style)"
            }
            5L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "M ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "May ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "May ($style)"
            }
            6L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "J ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Jun ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "June ($style)"
            }
            7L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "J ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Jul ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "July ($style)"
            }
            8L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "A ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Aug ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "August ($style)"
            }
            9L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "S ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Sep ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "September ($style)"
            }
            10L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "O ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Oct ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "October ($style)"
            }
            11L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "N ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Nov ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "November ($style)"
            }
            12L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "D ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Dec ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "December ($style)"
            }
            else -> throw DateTimeException("Invalid value")
        }
    }

    override fun dayOfWeekTextFor(value: Long, style: TextStyle, locale: Locale): String? {
        return when (value) {
            1L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "M ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Mon ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Monday ($style)"
            }
            2L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "T ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Tue ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Tuesday ($style)"
            }
            3L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "W ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Wed ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Wednesday ($style)"
            }
            4L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "T ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Thu ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Thursday ($style)"
            }
            5L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "F ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Fri ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Friday ($style)"
            }
            6L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "S ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Sat ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Saturday ($style)"
            }
            7L -> when (style) {
                TextStyle.NARROW, TextStyle.NARROW_STANDALONE -> "S ($style)"
                TextStyle.SHORT, TextStyle.SHORT_STANDALONE -> "Sun ($style)"
                TextStyle.FULL, TextStyle.FULL_STANDALONE -> "Sunday ($style)"
            }
            else -> throw DateTimeException("Invalid value")
        }
    }

    override fun amPmTextFor(value: Long, locale: Locale): String? {
        return when (value) {
            0L -> "AM"
            1L -> "PM"
            else -> throw DateTimeException("Invalid value")
        }
    }
}