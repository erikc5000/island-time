@file:OptIn(UnsafeNumber::class, ExperimentalForeignApi::class)

package io.islandtime.calendar

import io.islandtime.DayOfWeek
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian

actual val Locale.weekSettings: WeekSettings
    get() = NSCalendar(NSCalendarIdentifierGregorian)
        .also { it.locale = this }
        .run { WeekSettings(firstDayOfWeek, minimumDaysInFirstWeek.convert()) }

internal actual fun systemDefaultWeekSettings(): WeekSettings {
    return with(NSCalendar.currentCalendar) { WeekSettings(firstDayOfWeek, minimumDaysInFirstWeek.convert()) }
}

internal actual val Locale.firstDayOfWeek: DayOfWeek
    get() = NSCalendar(NSCalendarIdentifierGregorian).also { it.locale = this }.firstDayOfWeek

internal val NSCalendar.firstDayOfWeek: DayOfWeek
    get() {
        val sundayIndexedWeekNumber = firstWeekday.toInt()
        return DayOfWeek.SUNDAY + (sundayIndexedWeekNumber - 1).days
    }
