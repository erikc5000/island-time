package io.islandtime.formatter.internal

import io.islandtime.base.NumberProperty
import io.islandtime.base.StringProperty
import io.islandtime.base.get
import io.islandtime.calendar.LocalizedNumberProperty
import io.islandtime.format.ContextualTimeZoneNameStyle
import io.islandtime.format.TextStyle
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.formatter.dsl.NumberFormatterBuilder
import io.islandtime.formatter.dsl.TemporalFormatterBuilder

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
        formatters += NumberFormatterBuilderImpl(
            property.toString(),
            { temporal.get(property) },
            minLength,
            maxLength
        )
            .apply(builder)
            .build()
    }

    override fun wholeNumber(
        property: LocalizedNumberProperty,
        minLength: Int,
        maxLength: Int,
        builder: NumberFormatterBuilder.() -> Unit
    ) {
        formatters += NumberFormatterBuilderImpl(
            property.toString(),
            { temporal.get(property, context = this) },
            minLength,
            maxLength
        )
            .apply(builder)
            .build()
    }

    override fun decimalNumber(
        wholeProperty: NumberProperty,
        fractionProperty: NumberProperty,
        minWholeLength: Int,
        maxWholeLength: Int,
        minFractionLength: Int,
        maxFractionLength: Int,
        fractionScale: Int
    ) {
        formatters += DecimalNumberFormatter(
            wholeProperty,
            fractionProperty,
            minWholeLength,
            maxWholeLength,
            minFractionLength,
            maxFractionLength,
            fractionScale
        )
    }

    override fun fraction(property: NumberProperty, minLength: Int, maxLength: Int, scale: Int) {
        formatters += FractionFormatter(property, minLength, maxLength, scale)
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

    override fun localizedOffset(style: TextStyle) {
        formatters += style.toLocalizedUtcOffsetFormatter()
    }

    override fun timeZoneName(style: ContextualTimeZoneNameStyle) {
        formatters += TimeZoneNameFormatter(style)
    }

    override fun onlyIf(
        predicate: TemporalFormatter.Context.() -> Boolean,
        builder: TemporalFormatterBuilder.() -> Unit
    ) {
        val child = TemporalFormatterBuilderImpl().apply(builder).build()
        onlyIf(predicate, child)
    }

    fun onlyIf(predicate: TemporalFormatter.Context.() -> Boolean, formatter: TemporalFormatter) {
        if (formatter != EmptyFormatter) {
            formatters += OnlyIfFormatter(predicate, formatter)
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