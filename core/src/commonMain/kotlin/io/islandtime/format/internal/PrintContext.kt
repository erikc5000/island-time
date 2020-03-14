package io.islandtime.format.internal

import io.islandtime.base.Temporal
import io.islandtime.format.DateTimeFormatterSettings

internal class PrintContext(
    var temporal: Temporal,
    val settings: DateTimeFormatterSettings
) {
    val locale by lazy(LazyThreadSafetyMode.NONE, settings.locale)
}