package io.islandtime.calendar

import io.islandtime.DayOfWeek
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import kotlinx.cinterop.convert
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian

internal actual fun systemDefaultWeekSettings(): WeekSettings {
    return with(NSCalendar.currentCalendar) {
        WeekSettings(firstDayOfWeek, minimumDaysInFirstWeek().convert())
    }
}

internal actual val Locale.firstDayOfWeek: DayOfWeek
    get() = NSCalendar(NSCalendarIdentifierGregorian).also { it.locale = this }.firstDayOfWeek

@OptIn(ExperimentalUnsignedTypes::class)
internal val NSCalendar.firstDayOfWeek: DayOfWeek
    get() {
        val sundayIndexedWeekNumber = firstWeekday.toInt()
        return DayOfWeek.SUNDAY + (sundayIndexedWeekNumber - 1).days
    }