package io.islandtime.calendar

import io.islandtime.DayOfWeek
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import java.util.*

actual val Locale.weekSettings: WeekSettings
    get() {
        val gregorianCalendar = GregorianCalendar(this.withoutVariant())
        return with(gregorianCalendar) { WeekSettings(firstIslandDayOfWeek, minimalDaysInFirstWeek) }
    }

internal actual fun systemDefaultWeekSettings(): WeekSettings = Locale.getDefault().weekSettings

internal actual val Locale.firstDayOfWeek: DayOfWeek
    get() = GregorianCalendar(this.withoutVariant()).firstIslandDayOfWeek

private fun Locale.withoutVariant() = Locale(language, country)

private val Calendar.firstIslandDayOfWeek
    get() = DayOfWeek.SUNDAY + (firstDayOfWeek - 1).days
