package io.islandtime.format.internal

import io.islandtime.base.Temporal
import io.islandtime.calendar.WeekSettings
import io.islandtime.calendar.weekSettings
import io.islandtime.format.TemporalFormatter
import io.islandtime.locale.Locale

internal class FormatContext(
    override var temporal: Temporal,
    val settings: TemporalFormatter.Settings
) : TemporalFormatter.Context{
    override val locale: Locale by lazy(LazyThreadSafetyMode.NONE, settings.locale)
    override val weekSettings: WeekSettings get() = settings.weekSettingsOverride ?: locale.weekSettings
}