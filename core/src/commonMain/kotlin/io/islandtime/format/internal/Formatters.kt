package io.islandtime.format.internal

import io.islandtime.DateTimeException
import io.islandtime.UtcOffset
import io.islandtime.base.*
import io.islandtime.format.*
import io.islandtime.internal.appendZeroPadded
import io.islandtime.internal.toIntExact
import io.islandtime.internal.toZeroPaddedString
import io.islandtime.measures.seconds
import kotlin.math.absoluteValue

internal object EmptyFormatter : TemporalFormatter() {
    override fun format(context: FormatContext, stringBuilder: StringBuilder) = Unit
}

internal class CompositeFormatter(
    private val childFormatters: List<TemporalFormatter>
) : TemporalFormatter() {

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        childFormatters.forEach { it.format(context, stringBuilder) }
    }
}

internal class OnlyIfFormatter(
    private val condition: (temporal: Temporal) -> Boolean,
    private val childFormatter: TemporalFormatter
) : TemporalFormatter() {

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        if (condition(context.temporal)) {
            childFormatter.format(context, stringBuilder)
        }
    }
}

internal class CharLiteralFormatter(private val literal: Char) : TemporalFormatter() {
    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        stringBuilder.append(literal)
    }
}

internal class StringLiteralFormatter(private val literal: String) : TemporalFormatter() {
    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        stringBuilder.append(literal)
    }
}

internal class SignFormatter(private val property: NumberProperty) : TemporalFormatter() {
    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(property)
        val numberStyle = context.settings.numberStyle

        stringBuilder.append(
            when {
                value >= 0L -> numberStyle.plusSign.first()
                else -> numberStyle.minusSign.first()
            }
        )
    }
}

internal class WholeNumberFormatter(
    private val property: NumberProperty,
    private val minLength: Int,
    private val maxLength: Int,
    private val signStyle: SignStyle,
    private val lengthExceededBehavior: LengthExceededBehavior,
    private val transform: (Long) -> Long
) : TemporalFormatter() {

    init {
        require(minLength <= maxLength) { "minLength must be <= maxLength" }
        require(minLength in 1..19) { "minLength must be in 1..19" }
        require(maxLength <= 19) { "maxLength must be in 1..19" }
    }

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val value = transform(context.temporal.get(property))

        val numberString = when (value) {
            Long.MIN_VALUE -> "9223372036854775808"
            else -> value.absoluteValue.toString()
        }

        val adjustedSignStyle = if (numberString.length > maxLength) {
            when (lengthExceededBehavior) {
                LengthExceededBehavior.SIGN_STYLE_AWAYS -> SignStyle.ALWAYS
                LengthExceededBehavior.THROW -> throw DateTimeException(
                    "The value '$value' of '$property' exceeds the maximum allowed length"
                )
            }
        } else {
            signStyle
        }

        val numberStyle = context.settings.numberStyle
        stringBuilder.appendSign(value, adjustedSignStyle, numberStyle)

        val requiredPadding = minLength - numberString.length
        repeat(requiredPadding) { stringBuilder.append(numberStyle.zeroDigit) }
        stringBuilder.appendLocalizedNumber(numberString, numberStyle)
    }

    private fun StringBuilder.appendSign(
        value: Long,
        adjustedSignStyle: SignStyle,
        numberStyle: NumberStyle
    ) {
        when (adjustedSignStyle) {
            SignStyle.ALWAYS -> if (value >= 0) {
                append(numberStyle.plusSign.first())
            } else {
                append(numberStyle.minusSign.first())
            }
            SignStyle.NEGATIVE_ONLY -> if (value < 0) {
                append(numberStyle.minusSign.first())
            }
            SignStyle.NEVER -> if (value < 0) {
                throw DateTimeException(
                    "The value '$value' of '$property' cannot be negative according to the sign style"
                )
            }
        }
    }
}

internal class DecimalNumberFormatter(
    private val wholeProperty: NumberProperty,
    private val fractionProperty: NumberProperty,
    private val minWholeLength: Int,
    private val maxWholeLength: Int,
    private val minFractionLength: Int,
    private val maxFractionLength: Int,
    private val fractionScale: Int
) : TemporalFormatter() {

    init {
        require(minWholeLength <= maxWholeLength) {
            "minWholeLength must be <= maxWholeLength"
        }
        require(minWholeLength in 0..19) { "minWholeLength must be in 0..19" }
        require(maxWholeLength in 1..19) { "maxWholeLength must be in 1..19" }
        require(minFractionLength <= maxFractionLength) {
            "minFractionLength must be <= maxFractionLength"
        }
        require(minFractionLength in 0..9) { "minFractionLength must in 0..9" }
        require(maxFractionLength <= 9) { "maxFractionLength must in 0..9" }
        require(fractionScale in 1..9) { "fractionScale must in 1..9" }
    }

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val wholeValue = context.temporal.get(wholeProperty)
        val fractionValue = context.temporal.get(fractionProperty)
        val numberStyle = context.settings.numberStyle

        if (wholeValue < 0L || (wholeValue == 0L && fractionValue < 0L)) {
            stringBuilder.append(numberStyle.minusSign.first())
        }

        if (wholeValue != 0L || minWholeLength > 0 || (wholeValue == 0L && fractionValue == 0L)) {
            val wholeNumberString = when (wholeValue) {
                Long.MIN_VALUE -> "9223372036854775808"
                else -> wholeValue.absoluteValue.toString()
            }

            if (wholeNumberString.length > maxWholeLength) {
                throw DateTimeException(
                    "The value of $wholeProperty exceeds the maximum allowed length"
                )
            }

            stringBuilder.appendPaddedLocalizedNumber(
                wholeNumberString,
                minWholeLength,
                numberStyle
            )
        }

        if (minFractionLength > 0 || (fractionValue != 0L && maxFractionLength > 0)) {
            stringBuilder.appendDecimalSeparator(numberStyle)

            val absFractionValue = fractionValue.absoluteValue
            validateFractionValue(fractionProperty, absFractionValue, fractionScale)

            stringBuilder.appendFraction(
                absFractionValue,
                minFractionLength,
                maxFractionLength,
                fractionScale,
                numberStyle
            )
        }
    }
}

internal class FractionFormatter(
    private val property: NumberProperty,
    private val minLength: Int,
    private val maxLength: Int,
    private val scale: Int
) : TemporalFormatter() {

    init {
        require(minLength <= maxLength) { "minLength must be <= maxLength" }
        require(minLength in 1..9) { "minLength must be in 1..9" }
        require(maxLength <= 9) { "maxLength must be in 1..9" }
        require(scale in 1..9) { "scale must in in 1..9" }
    }

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(property).absoluteValue
        validateFractionValue(property, value, scale)

        stringBuilder.appendFraction(
            value,
            minLength,
            maxLength,
            scale,
            context.settings.numberStyle
        )
    }
}

internal class TextFormatter(private val property: StringProperty) : TemporalFormatter() {
    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(property)
        stringBuilder.append(value)
    }
}

internal class LocalizedDateTimeStyleFormatter(
    private val dateStyle: FormatStyle?,
    private val timeStyle: FormatStyle?
) : TemporalFormatter() {

    init {
        require(dateStyle != null || timeStyle != null) {
            "At least one date or time style must be non-null"
        }
    }

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        DateTimeFormatProvider.formatterFor(dateStyle, timeStyle, context.locale)
            .format(context, stringBuilder)
    }
}

internal class LocalizedDateTimeSkeletonFormatter(
    private val skeleton: String
) : TemporalFormatter() {

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        DateTimeFormatProvider.formatterFor(skeleton, context.locale)
            ?.format(context, stringBuilder)
            ?: throw UnsupportedOperationException(
                "The configured DateTimeFormatProvider does not support localized format skeletons"
            )
    }
}

internal class LocalizedDateTimeTextFormatter(
    private val property: NumberProperty,
    private val style: TextStyle,
    private val overrideProvider: DateTimeTextProvider? = null
) : TemporalFormatter() {

    private val provider get() = overrideProvider ?: DateTimeTextProvider.Companion

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(property)
        val text = provider.textFor(property, value, style, context.locale)
        stringBuilder.append(text)
    }
}

internal class LocalizedTimeZoneTextFormatter(
    style: TextStyle,
    private val generic: Boolean
) : TemporalFormatter() {

    private val style: TextStyle = style.asNormal()

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val temporal = context.temporal
        val zone = temporal.get(TimeZoneProperty.TimeZone)
        var useGeneric = generic
        var useDaylight = false

        if (!generic) {
            if (temporal.has(TimePointProperty.Instant)) {
                val instant = temporal.get(TimePointProperty.Instant)
                useDaylight = zone.rules.isDaylightSavingsAt(instant)
            } else {
                useGeneric = true
            }
        }

        val text = style.toTimeZoneTextStyle(useDaylight, useGeneric)
            ?.let { TimeZoneTextProvider.textFor(zone, it, context.locale) }
            ?: zone.id

        stringBuilder.append(text)
    }
}

private fun TextStyle.toTimeZoneTextStyle(daylight: Boolean, generic: Boolean): TimeZoneTextStyle? {
    return when (this) {
        TextStyle.SHORT -> when {
            generic -> TimeZoneTextStyle.SHORT_GENERIC
            daylight -> TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING
            else -> TimeZoneTextStyle.SHORT_STANDARD
        }
        TextStyle.FULL -> when {
            generic -> TimeZoneTextStyle.GENERIC
            daylight -> TimeZoneTextStyle.DAYLIGHT_SAVING
            else -> TimeZoneTextStyle.STANDARD
        }
        else -> null
    }
}

internal class UtcOffsetFormatter(
    private val format: IsoFormat,
    private val useUtcDesignatorWhenZero: Boolean,
    private val minutesOption: FormatOption,
    private val secondsOption: FormatOption
) : TemporalFormatter() {

    init {
        require(minutesOption != FormatOption.NEVER) {
            "The minutes component of a UTC offset cannot be set to ${FormatOption.NEVER}"
        }
    }

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val totalSeconds = context.temporal.get(UtcOffsetProperty.TotalSeconds)
        val offset = UtcOffset(totalSeconds.seconds.toIntSeconds()).validated()

        stringBuilder.apply {
            if (offset.isZero()) {
                appendZeroOffset()
            } else {
                appendNonZeroOffset(offset)
            }
        }
    }

    private fun StringBuilder.appendZeroOffset() {
        if (useUtcDesignatorWhenZero) {
            append('Z')
        } else {
            append('+')

            when {
                secondsOption == FormatOption.ALWAYS -> when (format) {
                    IsoFormat.EXTENDED -> append("00:00:00")
                    IsoFormat.BASIC -> append("000000")
                }
                minutesOption == FormatOption.ALWAYS -> when (format) {
                    IsoFormat.EXTENDED -> append("00:00")
                    IsoFormat.BASIC -> append("0000")
                }
                else -> append("00")
            }
        }
    }

    private fun StringBuilder.appendNonZeroOffset(offset: UtcOffset) {
        offset.toComponents { sign, hours, minutes, seconds ->
            append(if (sign >= 0) '+' else '-')
            appendZeroPadded(hours.value, 2)

            if (minutesOption == FormatOption.ALWAYS ||
                !minutes.isZero() ||
                (secondsOption != FormatOption.NEVER && !seconds.isZero())
            ) {
                if (format == IsoFormat.EXTENDED) append(':')
                appendZeroPadded(minutes.value, 2)

                if (secondsOption != FormatOption.NEVER &&
                    (secondsOption != FormatOption.OPTIONAL || !seconds.isZero())
                ) {
                    if (format == IsoFormat.EXTENDED) append(':')
                    appendZeroPadded(seconds.value, 2)
                }
            }
        }
    }
}

internal class LocalizedUtcOffsetFormatter(private val style: TextStyle) : TemporalFormatter() {

    init {
        require(style == TextStyle.SHORT || style == TextStyle.FULL) {
            "The localized offset style must be ${TextStyle.SHORT} or ${TextStyle.FULL}"
        }
    }

    override fun format(context: FormatContext, stringBuilder: StringBuilder) {
        val value = context.temporal.get(UtcOffsetProperty.TotalSeconds).toIntExact()
        val offset = UtcOffset(value.seconds).validated()

        // TODO: This could be localized on platforms offering more complete CLDR data (or if using
        //  the ICU), but only JDK14+ seems to have any support for it and it doesn't seem
        //  consistent with Darwin.
        stringBuilder.apply {
            append("GMT")
            if (!offset.isZero()) appendShortOrLongOffset(offset)
        }
    }

    private fun StringBuilder.appendShortOrLongOffset(offset: UtcOffset) {
        offset.toComponents { sign, hours, minutes, seconds ->
            append(if (sign >= 0) '+' else '-')

            if (style == TextStyle.SHORT) {
                if (hours.value / 10 == 0) {
                    append('0' + hours.value)
                } else {
                    append('1')
                    append('0' + hours.value % 10)
                }
            } else {
                appendZeroPadded(hours.value, 2)
            }

            if (style == TextStyle.FULL || !minutes.isZero() || !seconds.isZero()) {
                append(':')
                appendZeroPadded(minutes.value, 2)

                if (!seconds.isZero()) {
                    append(':')
                    appendZeroPadded(seconds.value, 2)
                }
            }
        }
    }
}

private fun validateFractionValue(property: NumberProperty, value: Long, scale: Int) {
    if (value !in 0..maxFractionValue(scale)) {
        throw DateTimeException("The value of '$property' is outside the valid range")
    }
}

private fun StringBuilder.appendFraction(
    value: Long,
    minLength: Int,
    maxLength: Int,
    scale: Int,
    numberStyle: NumberStyle
) {
    var valueString = value.toZeroPaddedString(scale).take(maxLength)

    for (i in valueString.length - 1 downTo minLength) {
        if (valueString[i] == '0') {
            valueString = valueString.removeRange(i, i + 1)
        } else {
            break
        }
    }

    appendLocalizedNumber(valueString, numberStyle)
}

private fun StringBuilder.appendDecimalSeparator(numberStyle: NumberStyle) {
    append(numberStyle.decimalSeparator.first())
}

private fun maxFractionValue(scale: Int): Int {
    return FRACTION_SCALE_TO_MAX_VALUE[scale]
}

private val FRACTION_SCALE_TO_MAX_VALUE = arrayOf(
    0,
    9,
    99,
    999,
    9_999,
    99_999,
    999_999,
    9_999_999,
    99_999_999,
    999_999_999
)

private fun StringBuilder.appendPaddedLocalizedNumber(
    number: String,
    minLength: Int,
    numberStyle: NumberStyle
) {
    val requiredPadding = minLength - number.length
    repeat(requiredPadding) { append(numberStyle.zeroDigit) }
    appendLocalizedNumber(number, numberStyle)
}

private fun StringBuilder.appendLocalizedNumber(number: String, numberStyle: NumberStyle) {
    if (numberStyle.zeroDigit == '0') {
        append(number)
    } else {
        val diff = numberStyle.zeroDigit - '0'
        number.forEach { append(it + diff) }
    }
}