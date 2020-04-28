package io.islandtime.format

import io.islandtime.base.NumberProperty
import io.islandtime.base.StringProperty

@IslandTimeFormatDsl
interface TemporalFormatterBuilder :
    LiteralFormatBuilder,
    ConditionalFormatterBuilder<TemporalFormatterBuilder>,
    ComposableFormatterBuilder {
    /**
     * Print a character indicating the sign of a [NumberProperty] -- "minus" if less than 0,
     * otherwise "plus".
     *
     * The characters associated with a number's sign are controlled by the
     * [TemporalFormatter.Settings]. By default, this is '+' or '-'. The characters may be
     * overridden by using a different [NumberStyle].
     *
     * @see NumberStyle
     */
    fun sign(property: NumberProperty)

    /**
     * Append a whole number, padding the start with zero if necessary to satisfy [length].
     *
     * @param property the number property to append the value of
     * @param minLength the minimum number of characters to append, excluding any sign
     * @param maxLength the maximum number of characters to append
     * @param builder configure formatter behavior
     */
    fun wholeNumber(
        property: NumberProperty,
        minLength: Int = 1,
        maxLength: Int = 19,
        builder: NumberFormatterBuilder.() -> Unit = {}
    )

    /**
     * Append a whole number, padding the start with zero if necessary to satisfy [length].
     *
     * @param property the number property to append the value of
     * @param length the number of characters to append, excluding any sign
     * @param builder configure formatter behavior
     */
    fun wholeNumber(
        property: NumberProperty,
        length: IntRange,
        builder: NumberFormatterBuilder.() -> Unit = {}
    ) {
        wholeNumber(property, length.first, length.last, builder)
    }

    /**
     * Append a whole number, padding the start with zero if necessary to reach [length].
     *
     * @param property the number property to append the value of
     * @param length the number of characters to append, excluding any sign
     * @param builder configure formatter behavior
     */
    fun wholeNumber(
        property: NumberProperty,
        length: Int,
        builder: NumberFormatterBuilder.() -> Unit = {}
    ) {
        wholeNumber(property, length..length, builder)
    }

    /**
     * Append a decimal number.
     *
     * The start will be padded with zero if necessary to satisfy [wholeLength]. Similarly, zero
     * will be added to the end to satisfy the minimum [fractionLength].
     *
     * If the value of the fraction property is zero and the minimum [fractionLength] is zero, the
     * decimal separator will be omitted.
     *
     * The character associated with the decimal separator is controlled by the
     * [TemporalFormatter.Settings]. By default, this is '.'. This may be overridden by using a
     * different [NumberStyle].
     *
     * @param wholeLength the number of digits to parse from the whole part, excluding sign
     * @param fractionLength the number of digits to parse from the fraction part
     * @param fractionScale the number of digits to normalize the fraction to -- by default 9,
     *                      indicating nanoseconds
     * @param builder configure parser behavior
     * @see NumberStyle
     */
    fun decimalNumber(
        wholeProperty: NumberProperty,
        fractionProperty: NumberProperty,
        wholeLength: IntRange = 1..19,
        fractionLength: IntRange = 0..9,
        fractionScale: Int = 9,
        builder: NumberFormatterBuilder.() -> Unit = {}
    )

    fun fraction(property: NumberProperty, length: IntRange = 1..9, scale: Int = 9)

    fun fraction(property: NumberProperty, length: Int, scale: Int = 9) {
        fraction(property, length..length, scale)
    }

    /**
     * Append the value of a [StringProperty].
     */
    fun text(property: StringProperty)

    /**
     * Print the localized text associated with the value of a particular [NumberProperty] in any of
     * the specified styles. If no text is available, an exception will be thrown.
     *
     * The locale used when printing text is determined by the [TemporalFormatter.Settings] in use.
     * Text is provided by the configured [DateTimeTextProvider]. This text may differ between
     * platforms and devices.
     *
     * @param property the property to print text for
     * @param style the style of text to print
     * @see TemporalFormatter.Settings.locale
     * @see DateTimeTextProvider
     */
    fun localizedDateTimeText(property: NumberProperty, style: TextStyle)

    fun localizedTimeZoneText(style: TextStyle, generic: Boolean = false)
}

enum class LengthExceededBehavior {
    THROW,
    SIGN_STYLE_AWAYS
}

@IslandTimeFormatDsl
interface NumberFormatterBuilder {
    /**
     * The sign style to use.
     */
    var signStyle: SignStyle

    var lengthExceededBehavior: LengthExceededBehavior

    var valueMapper: (Long) -> Long
}