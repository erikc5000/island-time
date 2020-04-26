package io.islandtime.format

import io.islandtime.base.Temporal

@IslandTimeFormatDsl
interface GroupedTemporalFormatterBuilder : LiteralFormatBuilder {
    /**
     * Group the elements described in this block with the [Temporal] at the corresponding position
     * during formatting.
     */
    fun group(builder: TemporalFormatterBuilder.() -> Unit)
}