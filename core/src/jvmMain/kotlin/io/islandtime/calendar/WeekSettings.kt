package io.islandtime.calendar

import io.islandtime.DayOfWeek
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import java.util.*

internal actual fun systemDefaultWeekSettings(): WeekSettings {
    val gregorianCalendar = GregorianCalendar(Locale.getDefault().withoutVariant())

    return with(gregorianCalendar) {
        WeekSettings(firstIslandDayOfWeek, minimalDaysInFirstWeek)
    }
}

internal actual val Locale.firstDayOfWeek: DayOfWeek
    get() = GregorianCalendar(this.withoutVariant()).firstIslandDayOfWeek

private fun Locale.withoutVariant() = Locale(language, country)

private val Calendar.firstIslandDayOfWeek
    get() = DayOfWeek.SUNDAY + (firstDayOfWeek - 1).days