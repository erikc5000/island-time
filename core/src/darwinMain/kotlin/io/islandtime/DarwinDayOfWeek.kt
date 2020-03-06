package io.islandtime

import io.islandtime.locale.Locale
import io.islandtime.measures.days
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian

internal actual val Locale.firstDayOfWeek: DayOfWeek
    get() = NSCalendar(NSCalendarIdentifierGregorian).also { it.locale = this }.firstDayOfWeek

internal actual fun systemDefaultFirstDayOfWeek(): DayOfWeek = NSCalendar.currentCalendar.firstDayOfWeek

@OptIn(ExperimentalUnsignedTypes::class)
private val NSCalendar.firstDayOfWeek: DayOfWeek
    get() {
        val sundayIndexedWeekNumber = firstWeekday.toInt()
        return DayOfWeek.SUNDAY + (sundayIndexedWeekNumber - 1).days
    }