package io.islandtime.calendar

import io.islandtime.DayOfWeek
import io.islandtime.locale.Locale

/**
 * Week-related calendar settings.
 * @property firstDayOfWeek The first day of the week.
 * @property minimumDaysInFirstWeek The minimum number of days required in the first week of the year.
 */
data class WeekSettings(
    val firstDayOfWeek: DayOfWeek,
    val minimumDaysInFirstWeek: Int
) {
    companion object {
        /**
         * The definition of a week according to the current system settings. This may differ from the definition
         * associated with the default locale on platforms that allow this to be customized, such as iOS and macOS.
         */
        fun systemDefault(): WeekSettings = systemDefaultWeekSettings()

        /**
         * The ISO-8601 calendar system's definition of a week, where the first day of the week is Monday and the first
         * week of the year has a minimum of 4 days.
         */
        val ISO = WeekSettings(DayOfWeek.MONDAY, 4)

        /**
         * A definition of a week that starts on Sunday with a minimum of 1 day in the first week of the year.
         */
        val SUNDAY_START = WeekSettings(DayOfWeek.SUNDAY, 1)
    }
}

internal expect fun systemDefaultWeekSettings(): WeekSettings
internal expect val Locale.firstDayOfWeek: DayOfWeek