package io.islandtime.calendar

import io.islandtime.base.ContextualNumberProperty
import io.islandtime.locale.Locale

/**
 * A localization context.
 */
interface LocalizationContext {
    val locale: Locale
    val weekSettings: WeekSettings
}

typealias LocalizedNumberProperty = ContextualNumberProperty<LocalizationContext>
