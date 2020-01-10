package io.islandtime

import io.islandtime.locale.Locale
import io.islandtime.measures.days
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

@UseExperimental(ExperimentalUnsignedTypes::class)
internal actual val Locale.firstDayOfWeek
    get(): DayOfWeek {
        val calendar = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierGregorian)?.also {
            it.locale = this
        } ?: throw IllegalStateException("Unable to create calendar")

        val sundayIndexedWeekNumber = calendar.firstWeekday.toInt()
        return DayOfWeek.SUNDAY + (sundayIndexedWeekNumber - 1).days
    }

internal actual val Locale.lastDayOfWeek: DayOfWeek get() = firstDayOfWeek + 6.days