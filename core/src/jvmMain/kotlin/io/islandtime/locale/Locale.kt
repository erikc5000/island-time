package io.islandtime.locale

import io.islandtime.DayOfWeek
import io.islandtime.measures.days
import java.util.*

actual typealias Locale = java.util.Locale

actual fun defaultLocale(): Locale = Locale.getDefault()

@Suppress("NewApi")
internal actual fun localeFor(identifier: String): Locale {
    return Locale.forLanguageTag(identifier)
}

internal actual val Locale.firstDayOfWeek
    get(): DayOfWeek {
        val localeWithoutVariant = java.util.Locale(language, country)
        val gregorianCalendar = GregorianCalendar(localeWithoutVariant)
        return DayOfWeek.SUNDAY + (gregorianCalendar.firstDayOfWeek - 1).days
    }

internal actual val Locale.lastDayOfWeek: DayOfWeek get() = firstDayOfWeek + 6.days