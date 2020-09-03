package io.islandtime.parser.dsl

import io.islandtime.format.dsl.IslandTimeFormatDsl
import io.islandtime.format.dsl.LiteralFormatBuilder
import io.islandtime.parser.GroupedTemporalParser

@IslandTimeFormatDsl
interface GroupedTemporalParserBuilder : LiteralFormatBuilder {
    /**
     * Creates a distinct parse result, which will be associated with all values parsed within it.
     */
    fun group(builder: TemporalParserBuilder.() -> Unit)

    /**
     * Tries each of the parsers defined by [builders] until the first one succeeds. If none succeed, parsing is
     * considered to have failed.
     */
    fun anyOf(vararg builders: GroupedTemporalParserBuilder.() -> Unit)

    /**
     * Tries each of the provided [parsers] until the first one succeeds and includes all of its groups in the parsing
     * results. If none of the parsers succeed, parsing is considered to have failed at the starting index.
     */
    fun anyOf(vararg parsers: GroupedTemporalParser)
}
