package io.islandtime

import io.islandtime.calendar.WeekSettings
import io.islandtime.calendar.firstDayOfWeek
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.TextStyle
import io.islandtime.internal.DAYS_PER_WEEK
import io.islandtime.locale.Locale
import io.islandtime.measures.Days
import io.islandtime.measures.days

/**
 * A day of the week.
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    /**
     * The ISO day of week number.
     *
     * The ISO week starts on Monday (1) and ends on Sunday (7).
     */
    val number: Int get() = ordinal + 1

    /**
     * The day of week number (1-7) according to the provided [settings]. Typically, the week will start on either
     * Monday, Sunday, or Saturday.
     */
    fun number(settings: WeekSettings): Int = (this - (settings.firstDayOfWeek.number - 1).days).number

    /**
     * The day of week number (1-7) according to the specified locale. Typically, the week will start on either Monday,
     * Sunday, or Saturday. The number returned may differ between platforms.
     */
    fun number(locale: Locale): Int = (this - (locale.firstDayOfWeek.number - 1).days).number

    /**
     * The localized name of the day, if available for the [locale] in the specified style. The result depends on the
     * configured [DateTimeTextProvider] and may differ between platforms.
     *
     * @param style the style of text
     * @param locale the locale
     * @return the localized name or `null` if unavailable for the specified locale
     * @see displayName
     */
    fun localizedName(style: TextStyle, locale: Locale): String? {
        return DateTimeTextProvider.dayOfWeekTextFor(number.toLong(), style, locale)
    }

    /**
     * A textual representation of the day, suitable for display purposes. The localized name will be returned, if
     * available. If not, the ISO day of week number will be returned instead.
     *
     * The result depends on the configured [DateTimeTextProvider] and may differ between platforms.
     *
     * @param style the style of text
     * @param locale the locale
     * @return the localized name or [number] if unavailable for the specified locale
     * @see localizedName
     */
    fun displayName(style: TextStyle, locale: Locale): String {
        return localizedName(style, locale) ?: number.toString()
    }

    /**
     * Returns this day of the week with [days] added to it, wrapping when the beginning or end of the week is reached.
     */
    operator fun plus(days: Days): DayOfWeek = plus((days.value % DAYS_PER_WEEK).toInt())

    /**
     * Returns this day of the week with [days] subtracted from it, wrapping when the beginning or end of the week is
     * reached.
     */
    operator fun minus(days: Days): DayOfWeek = plus(-(days.value % DAYS_PER_WEEK).toInt())

    private fun plus(daysToAdd: Int): DayOfWeek {
        return entries[(ordinal + (daysToAdd + DAYS_PER_WEEK)) % DAYS_PER_WEEK]
    }

    companion object {
        inline val MIN get() = MONDAY
        inline val MAX get() = SUNDAY
    }
}

/**
 * Converts an ISO day of week number to a [DayOfWeek].
 *
 * The ISO week starts on Monday (1) and ends on Sunday (7).
 */
fun Int.toDayOfWeek(): DayOfWeek {
    return DayOfWeek.entries[checkValidDayOfWeek(this) - 1]
}

/**
 * Converts a day of week number (1-7) to a [DayOfWeek] using the week definition provided by [settings].
 */
fun Int.toDayOfWeek(settings: WeekSettings): DayOfWeek {
    return settings.firstDayOfWeek + (checkValidDayOfWeek(this) - 1).days
}

internal fun checkValidDayOfWeek(number: Int): Int {
    if (number !in 1..7) {
        throw DateTimeException("'$number' is not a valid day of week number")
    }
    return number
}
