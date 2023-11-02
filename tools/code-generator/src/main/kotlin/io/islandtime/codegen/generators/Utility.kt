package io.islandtime.codegen.generators

import io.islandtime.codegen.descriptions.TemporalUnitDescription
import io.islandtime.codegen.measures

internal val TemporalUnitDescription.className get() = measures(pluralName)

internal val TemporalUnitDescription.nextBiggest get() = TemporalUnitDescription.entries[this.ordinal + 1]

internal val TemporalUnitDescription.nextSmallest get() = TemporalUnitDescription.entries[this.ordinal - 1]

internal val TemporalUnitDescription.nextSmallestOrNull
    get() = if (this == TemporalUnitDescription.NANOSECONDS) null else nextSmallest
