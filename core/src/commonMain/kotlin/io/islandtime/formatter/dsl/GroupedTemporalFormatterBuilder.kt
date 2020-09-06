package io.islandtime.formatter.dsl

import io.islandtime.base.Temporal
import io.islandtime.format.dsl.IslandTimeFormatDsl
import io.islandtime.format.dsl.LiteralFormatBuilder

@IslandTimeFormatDsl
interface GroupedTemporalFormatterBuilder : LiteralFormatBuilder {
    /**
     * Group the elements described in this block with the [Temporal] at the corresponding position
     * during formatting.
     */
    fun group(builder: TemporalFormatterBuilder.() -> Unit)
}
