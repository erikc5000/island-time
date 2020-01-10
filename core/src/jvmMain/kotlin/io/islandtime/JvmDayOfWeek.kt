package io.islandtime

import io.islandtime.locale.Locale
import io.islandtime.measures.days

internal actual val Locale.firstDayOfWeek
    get(): DayOfWeek {
        val localeWithoutVariant = java.util.Locale(language, country)
        val gregorianCalendar = java.util.GregorianCalendar(localeWithoutVariant)
        return DayOfWeek.SUNDAY + (gregorianCalendar.firstDayOfWeek - 1).days
    }

internal actual val Locale.lastDayOfWeek: DayOfWeek get() = firstDayOfWeek + 6.days