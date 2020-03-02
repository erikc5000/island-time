package io.islandtime.parser.internal

import io.islandtime.base.NumberProperty
import io.islandtime.format.TextStyle
import io.islandtime.parser.*

@PublishedApi
internal class DateTimeParserBuilderImpl : DateTimeParserBuilder {
    private val parsers = mutableListOf<DateTimeParser>()

    override fun sign(builder: SignParserBuilder.() -> Unit) {
        parsers += SignParserBuilderImpl().apply(builder).build()
    }

    override fun wholeNumber(
        length: Int,
        builder: WholeNumberParserBuilder.() -> Unit
    ) {
        parsers += FixedLengthNumberParserBuilderImpl(length).apply(builder).build()
    }

    override fun wholeNumber(length: IntRange, builder: WholeNumberParserBuilder.() -> Unit) {
        parsers += VariableLengthNumberParserBuilderImpl(length.first, length.last).apply(builder).build()
    }

    override fun decimalNumber(
        wholeLength: IntRange,
        fractionLength: IntRange,
        fractionScale: Int,
        builder: DecimalNumberParserBuilder.() -> Unit
    ) {
        parsers += DecimalNumberParserBuilderImpl(
            wholeLength.first,
            wholeLength.last,
            fractionLength.first,
            fractionLength.last,
            fractionScale
        ).apply(builder).build()
    }

    override fun string(length: IntRange, builder: StringParserBuilder.() -> Unit) {
        parsers += StringParserBuilderImpl(length).apply(builder).build()
    }

    override fun literal(char: Char, builder: LiteralParserBuilder.() -> Unit) {
        parsers += CharLiteralParserBuilderImpl(char).apply(builder).build()
    }

    override fun literal(string: String, builder: LiteralParserBuilder.() -> Unit) {
        parsers += StringLiteralParserBuilderImpl(string).apply(builder).build()
    }

    override fun localizedText(property: NumberProperty, styles: Set<TextStyle>) {
        parsers += LocalizedTextParser(property, styles)
    }

    override fun optional(builder: DateTimeParserBuilder.() -> Unit) {
        val childParser = DateTimeParserBuilderImpl().apply(builder).buildElement()

        if (childParser != null) {
            parsers += OptionalDateTimeParser(childParser)
        }
    }

    override fun anyOf(vararg builders: DateTimeParserBuilder.() -> Unit) {
        val childParsers = builders.map { DateTimeParserBuilderImpl().apply(it).build() }
        anyOf(*childParsers.toTypedArray())
    }

    override fun anyOf(vararg childParsers: DateTimeParser) {
        require(childParsers.size >= 2) { "anyOf() requires at least 2 child parsers" }
        parsers += AnyOfDateTimeParser(childParsers)
    }

    override fun childParser(childParser: DateTimeParser) {
        parsers += childParser
    }

    override fun caseSensitive(builder: DateTimeParserBuilder.() -> Unit) {
        buildCaseSensitiveParser(true, builder)
    }

    override fun caseInsensitive(builder: DateTimeParserBuilder.() -> Unit) {
        buildCaseSensitiveParser(false, builder)
    }

    fun build(): DateTimeParser {
        return buildElement() ?: EmptyDateTimeParser
    }

    private fun buildElement(): DateTimeParser? {
        return when (parsers.count()) {
            0 -> null
            1 -> parsers.first()
            else -> CompositeDateTimeParser(parsers)
        }
    }

    private fun buildCaseSensitiveParser(isCaseSensitive: Boolean, builder: DateTimeParserBuilder.() -> Unit) {
        val childParser = DateTimeParserBuilderImpl().apply(builder).buildElement()

        if (childParser != null) {
            parsers += CaseSensitiveDateTimeParser(isCaseSensitive, childParser)
        }
    }
}