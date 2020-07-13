package io.islandtime.parser.internal

import io.islandtime.parser.*
import io.islandtime.parser.GroupedTemporalParserBuilder

@PublishedApi
internal class GroupedTemporalParserBuilderImpl : GroupedTemporalParserBuilder {
    private val parsers = mutableListOf<Any>()

    override fun group(builder: TemporalParserBuilder.() -> Unit) {
        parsers += TemporalParserBuilderImpl().apply(builder).build()
    }

    override fun literal(char: Char) {
        parsers += CharLiteralParserBuilderImpl(char).build()
    }

    override fun literal(string: String) {
        parsers += StringLiteralParserBuilderImpl(string).build()
    }

    override fun anyOf(vararg builders: GroupedTemporalParserBuilder.() -> Unit) {
        val childParsers = builders.map { GroupedTemporalParserBuilderImpl().apply(it).build() }

        if (childParsers.isNotEmpty()) {
            parsers += GroupedTemporalParser(childParsers, isAnyOf = true)
        }
    }

    override fun anyOf(vararg childParsers: GroupedTemporalParser) {
        if (childParsers.isNotEmpty()) {
            parsers += GroupedTemporalParser(childParsers.asList(), isAnyOf = true)
        }
    }

    fun build(): GroupedTemporalParser {
        return GroupedTemporalParser(parsers)
    }
}