package io.islandtime.format.internal

import io.islandtime.base.NumberProperty
import io.islandtime.base.StringProperty
import io.islandtime.base.Temporal
import io.islandtime.base.UtcOffsetProperty
import io.islandtime.format.*

@PublishedApi
internal class DateTimeFormatterBuilderImpl : DateTimeFormatterBuilder {
    private val formatters = mutableListOf<DateTimeFormatter>()

    override fun sign(property: NumberProperty) {

    }

    override fun wholeNumber(property: NumberProperty, length: Int, builder: NumberFormatterBuilder.() -> Unit) {
//        formatters += WholeNumberFormatter(property, length, length, signStyle)
    }

    override fun wholeNumber(property: NumberProperty, length: IntRange, builder: NumberFormatterBuilder.() -> Unit) {
//        formatters += WholeNumberFormatter(property, length.first, length.last, signStyle)
    }

    override fun decimalNumber(
        wholeProperty: NumberProperty,
        fractionProperty: NumberProperty,
        wholeLength: IntRange,
        fractionLength: IntRange,
        fractionScale: Int,
        builder: NumberFormatterBuilder.() -> Unit
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun literal(char: Char) {
        formatters += CharLiteralFormatter(char)
    }

    override fun literal(string: String) {
        formatters += StringLiteralFormatter(string)
    }

    override fun string(property: StringProperty) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun localizedText(property: NumberProperty, style: TextStyle) {
        formatters += LocalizedTextFormatter(property, style)
    }

    override fun onlyIf(condition: Temporal.() -> Boolean, builder: DateTimeFormatterBuilder.() -> Unit) {
        val child = DateTimeFormatterBuilderImpl().apply(builder).buildElement()

        if (child != null) {
            formatters += OnlyIfDateTimeFormatter(condition, child)
        }
    }

//    override fun DateTimeFormatterBuilder.utcOffset(
//        format: IsoFormat,
//        useUtcDesignatorWhenZero: Boolean,
//        minutesOptional: Boolean,
//        includeSeconds: Boolean,
//        secondsOptional: Boolean
//    ) {
//        if (useUtcDesignatorWhenZero) {
//            onlyIf({  })
//        }
//        sign(UtcOffsetProperty.Sign)
//        wholeNumber(UtcOffsetProperty.Hours, 2)
//        onlyIf({  }) {
//            if (format == IsoFormat.EXTENDED) {
//                +':'
//            }
//            wholeNumber(UtcOffsetProperty.Minutes, 2)
//        }
//        onlyIf()
//    }

    override fun use(child: DateTimeFormatter) {
        formatters += child
    }

//    override fun sign(builder: SignParserBuilder.() -> Unit) {
//        formatters += SignParserBuilderImpl().apply(builder).build()
//    }
//
//    override fun wholeNumber(
//        length: Int,
//        builder: WholeNumberParserBuilder.() -> Unit
//    ) {
//        formatters += FixedLengthNumberParserBuilderImpl(length).apply(builder).build()
//    }
//
//    override fun wholeNumber(length: IntRange, builder: WholeNumberParserBuilder.() -> Unit) {
//        formatters += VariableLengthNumberParserBuilderImpl(length.first, length.last).apply(builder).build()
//    }
//
//    override fun decimalNumber(
//        wholeLength: IntRange,
//        fractionLength: IntRange,
//        fractionScale: Int,
//        builder: DecimalNumberParserBuilder.() -> Unit
//    ) {
//        formatters += DecimalNumberParserBuilderImpl(
//            wholeLength.first,
//            wholeLength.last,
//            fractionLength.first,
//            fractionLength.last,
//            fractionScale
//        ).apply(builder).build()
//    }
//
//    override fun string(length: IntRange, builder: StringParserBuilder.() -> Unit) {
//        formatters += StringParserBuilderImpl(length).apply(builder).build()
//    }
//
//    override fun literal(char: Char, builder: LiteralParserBuilder.() -> Unit) {
//        formatters += CharLiteralParserBuilderImpl(char).apply(builder).build()
//    }
//
//    override fun literal(string: String, builder: LiteralParserBuilder.() -> Unit) {
//        formatters += StringLiteralParserBuilderImpl(string).apply(builder).build()
//    }
//
//    override fun localizedText(property: NumberProperty, styles: Set<TextStyle>) {
//        formatters += LocalizedTextParser(property, styles)
//    }
//
//    override fun optional(builder: DateTimeParserBuilder.() -> Unit) {
//        val childParser = DateTimeParserBuilderImpl().apply(builder).buildElement()
//
//        if (childParser != null) {
//            formatters += OptionalDateTimeParser(childParser)
//        }
//    }
//
//    override fun anyOf(vararg builders: DateTimeParserBuilder.() -> Unit) {
//        val childParsers = builders.map { DateTimeParserBuilderImpl().apply(it).build() }
//        anyOf(*childParsers.toTypedArray())
//    }
//
//    override fun anyOf(vararg childParsers: DateTimeParser) {
//        require(childParsers.size >= 2) { "anyOf() requires at least 2 child parsers" }
//        formatters += AnyOfDateTimeParser(childParsers)
//    }
//
//    override fun childParser(childParser: DateTimeParser) {
//        formatters += childParser
//    }
//
//    override fun caseSensitive(builder: DateTimeParserBuilder.() -> Unit) {
//        buildCaseSensitiveParser(true, builder)
//    }
//
//    override fun caseInsensitive(builder: DateTimeParserBuilder.() -> Unit) {
//        buildCaseSensitiveParser(false, builder)
//    }

    fun build(): DateTimeFormatter {
        return buildElement() ?: EmptyDateTimeFormatter
    }

    private fun buildElement(): DateTimeFormatter? {
        return when (formatters.size) {
            0 -> null
            1 -> formatters.first()
            else -> CompositeDateTimeFormatter(formatters)
        }
    }
}