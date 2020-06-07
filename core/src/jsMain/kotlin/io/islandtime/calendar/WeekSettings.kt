package io.islandtime.calendar

import io.islandtime.DayOfWeek
import io.islandtime.locale.Locale
import io.islandtime.locale.MLocale
import io.islandtime.measures.days
import moment.Moment
import moment.moment
import moment.now
import kotlin.js.Date

internal actual fun systemDefaultWeekSettings(): WeekSettings {
    //TODO we have to figure out better ways to find the `minimumDaysInFirstWeek
    return WeekSettings(
        firstIslandDayOfWeek,
        7
        )
}

internal actual val Locale.firstDayOfWeek: DayOfWeek
    get() = firstIslandDayOfWeek(this)

internal fun firstIslandDayOfWeek(locale: Locale = MLocale()): DayOfWeek =
    DayOfWeek.SUNDAY + locale.firstDayOfWeek().toInt().days

internal val firstIslandDayOfWeek: DayOfWeek
    get() = firstIslandDayOfWeek()
