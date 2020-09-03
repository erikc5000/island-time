package io.islandtime.parser.dsl

import io.islandtime.format.dsl.IslandTimeFormatDsl
import io.islandtime.parser.TemporalParser

@IslandTimeFormatDsl
interface BaseParserBuilder<T> {
    /**
     * Makes parsing optional within this block.
     *
     * If any of the parsers fail, the parse result will be reset to its state before the block started and parsing will
     * continue on, assuming there are additional parsers remaining.
     */
    fun optional(builder: T.() -> Unit)

    /**
     * Tries each of the parsers defined by [builders] until the first one succeeds. If none succeed, parsing is
     * considered to have failed.
     */
    fun anyOf(vararg builders: T.() -> Unit)

    /**
     * Tries each of the provided [parsers] until the first one succeeds. If none succeed, parsing is considered to have
     * failed.
     */
    fun anyOf(vararg parsers: TemporalParser)

    /**
     * Uses a parser that has been defined outside of this builder.
     */
    fun use(parser: TemporalParser)

    /**
     * Forces parsing to be case-sensitive within this block.
     */
    fun caseSensitive(builder: T.() -> Unit)

    /**
     * Forces parsing to be case-insensitive within this block.
     */
    fun caseInsensitive(builder: T.() -> Unit)
}
