package io.islandtime.format.internal

import io.islandtime.base.NumberProperty
import io.islandtime.base.StringProperty
import io.islandtime.base.Temporal
import io.islandtime.format.NumberFormatterBuilder
import io.islandtime.format.TemporalFormatter
import io.islandtime.format.TemporalFormatterBuilder
import io.islandtime.format.TextStyle

@PublishedApi
internal class TemporalFormatterBuilderImpl : TemporalFormatterBuilder {
    private val formatters = mutableListOf<TemporalFormatter>()

    override fun sign(property: NumberProperty) {
        formatters += SignFormatter(property)
    }

    override fun wholeNumber(
        property: NumberProperty,
        minLength: Int,
        maxLength: Int,
        builder: NumberFormatterBuilder.() -> Unit
    ) {
        formatters += NumberFormatterBuilderImpl(property, minLength, maxLength)
            .apply(builder)
            .build()
    }

    override fun decimalNumber(
        wholeProperty: NumberProperty,
        fractionProperty: NumberProperty,
        wholeLength: IntRange,
        fractionLength: IntRange,
        fractionScale: Int
    ) {
        formatters += DecimalNumberFormatter(
            wholeProperty,
            fractionProperty,
            wholeLength.first,
            wholeLength.last,
            fractionLength.first,
            fractionLength.last,
            fractionScale
        )
    }

    override fun fraction(property: NumberProperty, length: IntRange, scale: Int) {
        formatters += FractionFormatter(property, length.first, length.last, scale)
    }

    override fun literal(char: Char) {
        formatters += CharLiteralFormatter(char)
    }

    override fun literal(string: String) {
        formatters += StringLiteralFormatter(string)
    }

    override fun text(property: StringProperty) {
        formatters += TextFormatter(property)
    }

    override fun localizedDateTimeText(property: NumberProperty, style: TextStyle) {
        formatters += LocalizedDateTimeTextFormatter(property, style)
    }

    override fun localizedTimeZoneText(style: TextStyle, generic: Boolean) {
        formatters += LocalizedTimeZoneTextFormatter(style, generic)
    }

    override fun onlyIf(
        predicate: (temporal: Temporal) -> Boolean,
        builder: TemporalFormatterBuilder.() -> Unit
    ) {
        val child = TemporalFormatterBuilderImpl().apply(builder).build()

        if (child != EmptyFormatter) {
            formatters += OnlyIfFormatter(predicate, child)
        }
    }

    override fun use(formatter: TemporalFormatter) {
        if (formatter != EmptyFormatter) {
            formatters += formatter
        }
    }

    fun build(): TemporalFormatter {
        return when (formatters.size) {
            0 -> EmptyFormatter
            1 -> formatters.first()
            else -> CompositeFormatter(formatters)
        }
    }
}