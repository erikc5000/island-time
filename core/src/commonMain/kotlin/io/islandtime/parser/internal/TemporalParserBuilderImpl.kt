package io.islandtime.parser.internal

import io.islandtime.base.NumberProperty
import io.islandtime.format.TextStyle
import io.islandtime.parser.TemporalParser
import io.islandtime.parser.dsl.*

@PublishedApi
internal class TemporalParserBuilderImpl : TemporalParserBuilder {
    private val parsers = mutableListOf<TemporalParser>()

    override fun literal(char: Char) = literal(char, {})

    override fun literal(string: String) = literal(string, {})

    override fun literal(char: Char, builder: LiteralParserBuilder.() -> Unit) {
        parsers += CharLiteralParserBuilderImpl(char).apply(builder).build()
    }

    override fun literal(string: String, builder: LiteralParserBuilder.() -> Unit) {
        parsers += StringLiteralParserBuilderImpl(string).apply(builder).build()
    }

    override fun sign(builder: SignParserBuilder.() -> Unit) {
        parsers += SignParserBuilderImpl().apply(builder).build()
    }

    override fun wholeNumber(minLength: Int, maxLength: Int, builder: WholeNumberParserBuilder.() -> Unit) {
        parsers += WholeNumberParserBuilderImpl(minLength, maxLength).apply(builder).build()
    }

    override fun decimalNumber(
        minWholeLength: Int,
        maxWholeLength: Int,
        minFractionLength: Int,
        maxFractionLength: Int,
        fractionScale: Int,
        builder: DecimalNumberParserBuilder.() -> Unit
    ) {
        parsers += DecimalNumberParserBuilderImpl(
            minWholeLength,
            maxWholeLength,
            minFractionLength,
            maxFractionLength,
            fractionScale,
        )
            .apply(builder)
            .build()
    }

    override fun fraction(minLength: Int, maxLength: Int, scale: Int, builder: FractionParserBuilder.() -> Unit) {
        parsers += FractionParserBuilderImpl(minLength, maxLength, scale).apply(builder).build()
    }

    override fun text(minLength: Int, maxLength: Int, builder: TextParserBuilder.() -> Unit) {
        parsers += TextParserBuilderImpl(minLength, maxLength).apply(builder).build()
    }

    override fun localizedDateTimeText(property: NumberProperty, styles: Set<TextStyle>) {
        parsers += LocalizedTextParser(property, styles)
    }

    override fun optional(builder: TemporalParserBuilder.() -> Unit) {
        val child = TemporalParserBuilderImpl().apply(builder).build()
        optional(child)
    }

    fun optional(parser: TemporalParser) {
        if (parser != EmptyParser) {
            parsers += OptionalParser(parser)
        }
    }

    override fun anyOf(vararg builders: TemporalParserBuilder.() -> Unit) {
        val childParsers = Array(builders.size) { TemporalParserBuilderImpl().apply(builders[it]).build() }
        anyOf(*childParsers)
    }

    override fun anyOf(vararg parsers: TemporalParser) {
        require(parsers.size >= 2) { "anyOf() requires at least 2 child parsers" }
        this.parsers += AnyOfParser(parsers)
    }

    override fun caseSensitive(builder: TemporalParserBuilder.() -> Unit) {
        val child = TemporalParserBuilderImpl().apply(builder).build()
        caseSensitive(child)
    }

    fun caseSensitive(child: TemporalParser) {
        if (child != EmptyParser) {
            parsers += CaseSensitiveParser(isCaseSensitive = true, child)
        }
    }

    override fun caseInsensitive(builder: TemporalParserBuilder.() -> Unit) {
        val child = TemporalParserBuilderImpl().apply(builder).build()
        caseInsensitive(child)
    }

    fun caseInsensitive(child: TemporalParser) {
        if (child != EmptyParser) {
            parsers += CaseSensitiveParser(isCaseSensitive = false, child)
        }
    }

    override fun use(parser: TemporalParser) {
        parsers += parser
    }

    fun build(): TemporalParser {
        return when (parsers.size) {
            0 -> EmptyParser
            1 -> parsers.first()
            else -> CompositeParser(parsers)
        }
    }
}
