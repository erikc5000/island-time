package io.islandtime.format.internal

import io.islandtime.base.Temporal
import io.islandtime.format.TemporalFormatter

internal class FormatContext(
    var temporal: Temporal,
    val settings: TemporalFormatter.Settings
) {
    val locale by lazy(LazyThreadSafetyMode.NONE, settings.locale)
}