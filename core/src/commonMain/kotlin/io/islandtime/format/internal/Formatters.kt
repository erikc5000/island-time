package io.islandtime.format.internal

import io.islandtime.DateTimeException
import io.islandtime.base.NumberProperty
import io.islandtime.base.StringProperty
import io.islandtime.base.Temporal
import io.islandtime.format.*
import kotlin.math.absoluteValue

object EmptyDateTimeFormatter : DateTimeFormatter() {
    override fun format(context: PrintContext, stringBuilder: StringBuilder) {}
}

internal class CompositeDateTimeFormatter(
    private val childFormatters: List<DateTimeFormatter>
) : DateTimeFormatter() {

    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        childFormatters.forEach { it.format(context, stringBuilder) }
    }
}

internal class OnlyIfDateTimeFormatter(
    private val condition: (temporal: Temporal) -> Boolean,
    private val childFormatter: DateTimeFormatter
) : DateTimeFormatter() {

    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        if (condition(context.temporal)) {
            childFormatter.format(context, stringBuilder)
        }
    }
}

internal class CharLiteralFormatter(
    private val literal: Char
) : DateTimeFormatter() {
    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        stringBuilder.append(literal)
    }
}

internal class StringLiteralFormatter(
    private val literal: String
) : DateTimeFormatter() {
    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        stringBuilder.append(literal)
    }
}

internal class WholeNumberFormatter(
    private val property: NumberProperty,
    private val minLength: Int,
    private val maxLength: Int,
    private val signStyle: SignStyle
) : DateTimeFormatter() {

    init {
        require(minLength <= maxLength) { "minLength must be <= maxLength" }
        require(minLength in 1..19) { "minLength must be in 1..19" }
        require(maxLength in 1..19) { "maxLength must be in 1..19" }
    }

    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(property)
        printSign(value, context.settings.numberStyle, stringBuilder)

        val numberString = when {
            value == Long.MIN_VALUE -> "9223372036854775808"
            value == 0L && minLength == 0 -> ""
            else -> value.absoluteValue.toString()
        }

        if (numberString.length > maxLength) {
            throw DateTimeException("The value '$value' of '$property' exceeds the maximum allowed length")
        }

        val requiredPadding = minLength - numberString.length
        repeat(requiredPadding) { stringBuilder.append(context.settings.numberStyle.zeroDigit) }
        printLocalizedNumber(numberString, context.settings.numberStyle, stringBuilder)
    }

    private fun printSign(value: Long, numberStyle: NumberStyle, stringBuilder: StringBuilder) {
        when (signStyle) {
            SignStyle.ALWAYS -> if (value >= 0) {
                stringBuilder.append(numberStyle.plusSign.first())
            } else {
                stringBuilder.append(numberStyle.minusSign.first())
            }
            SignStyle.NEGATIVE_ONLY -> if (value < 0) {
                stringBuilder.append(numberStyle.minusSign.first())
            }
            SignStyle.NEVER -> if (value < 0) {
                throw DateTimeException(
                    "The value '$value' of '$property' cannot be negative according to the sign style"
                )
            }
        }
    }

    private fun printLocalizedNumber(number: String, numberStyle: NumberStyle, stringBuilder: StringBuilder) {
        if (numberStyle.zeroDigit == '0') {
            stringBuilder.append(number)
        } else {
            val diff = numberStyle.zeroDigit - '0'
            number.forEach { stringBuilder.append(it + diff) }
        }
    }
}

internal class DecimalNumberFormatter(
    private val wholeProperty: NumberProperty,
    private val fractionProperty: NumberProperty,
    private val minWholeLength: Int,
    private val fractionLength: IntRange,
    private val fractionScale: Int,
    private val signStyle: SignStyle
) : DateTimeFormatter() {
    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        val wholeValue = context.temporal.get(wholeProperty)
        val fractionValue = context.temporal.get(fractionProperty)
    }
}

internal class StringFormatter(
    private val property: StringProperty
) : DateTimeFormatter() {
    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(property)
        stringBuilder.append(value)
    }
}

internal class LocalizedTextFormatter(
    private val property: NumberProperty,
    private val style: TextStyle,
    private val provider: DateTimeTextProvider? = null
) : DateTimeFormatter() {
    override fun format(context: PrintContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(property)
        val text = getProvider().textFor(property, value, style, context.locale)
        stringBuilder.append(text)
    }

    private fun getProvider() = provider ?: DateTimeTextProvider.Companion
}