package io.islandtime.parser.internal

import io.islandtime.base.DateTimeField
import io.islandtime.format.TextStyle
import io.islandtime.parser.*

@PublishedApi
internal class DateTimeParserBuilderImpl : DateTimeParserBuilder {
    private val parsers = mutableListOf<DateTimeParser>()

    override fun sign(builder: SignParserBuilder.() -> Unit) {
        parsers += SignParserBuilderImpl().apply(builder).build()
    }

    override fun decimalSeparator(builder: LiteralParserBuilder.() -> Unit) {
        parsers += DecimalSeparatorParserBuilderImpl().apply(builder).build()
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

    override fun fraction(length: IntRange, scale: Int, builder: FractionParserBuilder.() -> Unit) {
        parsers += FractionParserBuilderImpl(length.first, length.last, scale).apply(builder).build()
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

    override fun localizedText(field: DateTimeField, styles: Set<TextStyle>) {
        parsers += LocalizedTextParser(field, styles)
    }

    override fun optional(builder: DateTimeParserBuilder.() -> Unit) {
        val childParser = DateTimeParserBuilderImpl().apply(builder).buildElement()

        if (childParser != null) {
            parsers += OptionalDateTimeParser(childParser)
        }
    }

    override fun anyOf(vararg builders: DateTimeParserBuilder.() -> Unit) {
        val childParsers = builders.mapNotNull {
            DateTimeParserBuilderImpl().apply(it).buildElement()
        }
        anyOf(*childParsers.toTypedArray())
    }

    override fun anyOf(vararg childParsers: DateTimeParser) {
        if (childParsers.isNotEmpty()) {
            parsers += AnyOfDateTimeParser(childParsers)
        }
    }

    override fun childParser(childParser: DateTimeParser) {
        parsers += childParser
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
}