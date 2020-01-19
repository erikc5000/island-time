package io.islandtime.parser.internal

import io.islandtime.parser.*
import io.islandtime.parser.GroupedDateTimeParserBuilder

@PublishedApi
internal class GroupedDateTimeParserBuilderImpl : GroupedDateTimeParserBuilder {
    private val parsers = mutableListOf<Any>()

    override fun group(builder: DateTimeParserBuilder.() -> Unit) {
        parsers += DateTimeParserBuilderImpl().apply(builder).build()
    }

    override fun literal(char: Char) {
        parsers += CharLiteralParserBuilderImpl(char).build()
    }

    override fun literal(string: String) {
        parsers += StringLiteralParserBuilderImpl(string).build()
    }

    override fun anyOf(vararg builders: GroupedDateTimeParserBuilder.() -> Unit) {
        val childParsers = builders.map { GroupedDateTimeParserBuilderImpl().apply(it).build() }

        if (childParsers.isNotEmpty()) {
            parsers += GroupedDateTimeParser(childParsers, isAnyOf = true)
        }
    }

    override fun anyOf(vararg childParsers: GroupedDateTimeParser) {
        if (childParsers.isNotEmpty()) {
            parsers += GroupedDateTimeParser(childParsers.asList(), isAnyOf = true)
        }
    }

    fun build(): GroupedDateTimeParser {
        return GroupedDateTimeParser(parsers)
    }
}