package io.islandtime.format

import io.islandtime.calendar.LocalizedNumberProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.StringProperty

@IslandTimeFormatDsl
interface TemporalFormatterBuilder :
    LiteralFormatBuilder,
    ConditionalFormatterBuilder<TemporalFormatterBuilder>,
    ComposableFormatterBuilder {

    /**
     * Appends the character indicating the sign of a [NumberProperty] -- "minus" if less than zero, otherwise "plus".
     *
     * The characters associated with a number's sign are controlled by the [TemporalFormatter.Settings]. By default,
     * this is '+' or '-'. The characters may be overridden by using a different [NumberStyle].
     *
     * @see NumberStyle
     */
    fun sign(property: NumberProperty)

    /**
     * Appends a whole number, padding the start with zero if necessary to satisfy the [minLength].
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
     * Appends a whole number, padding the start with zero if necessary to satisfy the [minLength].
     *
     * @param value a function that obtains the value to output
     * @param minLength the minimum number of characters to append, excluding any sign
     * @param maxLength the maximum number of characters to append
     * @param builder configure formatter behavior
     */
    fun wholeNumber(
        property: LocalizedNumberProperty,
        minLength: Int = 1,
        maxLength: Int = 19,
        builder: NumberFormatterBuilder.() -> Unit = {}
    )

    /**
     * Appends a decimal number.
     *
     * The start will be padded with zero if necessary to satisfy [minWholeLength]. Similarly, zero will be added to the
     * end to satisfy the [minFractionLength]. If the value of the fraction property is zero and the [minFractionLength]
     * is zero, the decimal separator will be omitted.
     *
     * The character associated with the decimal separator is controlled by the [TemporalFormatter.Settings]. By
     * default, this is '.'. This may be overridden by using a different [NumberStyle].
     *
     * @param minWholeLength the minimum number of digits to parse from the whole part, excluding sign
     * @param maxWholeLength the maximum number of digits to parse from the whole part, excluding sign
     * @param minFractionLength the minimum number of digits to parse from the fraction part
     * @param maxFractionLength the maximum number of digits to parse from the fraction part
     * @param fractionScale the number of digits to normalize the fraction to -- by default 9, indicating nanoseconds
     * @see NumberStyle
     */
    fun decimalNumber(
        wholeProperty: NumberProperty,
        fractionProperty: NumberProperty,
        minWholeLength: Int = 1,
        maxWholeLength: Int = 19,
        minFractionLength: Int = 0,
        maxFractionLength: Int = 9,
        fractionScale: Int = 9
    )

    /**
     * Appends a fraction.
     */
    fun fraction(property: NumberProperty, minLength: Int = 1, maxLength: Int = 9, scale: Int = 9)

    /**
     * Appends the value of a [StringProperty].
     */
    fun text(property: StringProperty)

    /**
     * Appends the localized text associated with the value of a particular [NumberProperty] in the specified [style].
     * If no text is available, an exception will be thrown.
     *
     * The locale used when printing text is determined by the [TemporalFormatter.Settings] in use. Text is provided by
     * the configured [DateTimeTextProvider]. This text may differ between platforms and devices.
     *
     * @param property the property to print text for
     * @param style the style of text to print
     * @see TemporalFormatter.Settings.locale
     * @see DateTimeTextProvider
     */
    fun localizedDateTimeText(property: NumberProperty, style: TextStyle)

    /**
     * Appends the localized time zone text in the specified [style].
     */
    fun localizedTimeZoneText(style: TextStyle, generic: Boolean = false)
}

/**
 * Behavior when the maximum length of a numeric property is exceeded.
 */
enum class LengthExceededBehavior {
    THROW,
    SIGN_STYLE_ALWAYS
}

@IslandTimeFormatDsl
interface NumberFormatterBuilder {
    /**
     * The sign style to use.
     */
    var signStyle: SignStyle

    /**
     * The behavior when the maximum length of this property is exceeded.
     */
    var lengthExceededBehavior: LengthExceededBehavior

    /**
     * A transform to apply to the original value.
     */
    var valueTransform: (Long) -> Long
}

/**
 * Appends a whole number, padding the start with zero if necessary to satisfy [length].
 *
 * @param property the number property to append the value of
 * @param length the number of characters to append, excluding any sign
 * @param builder configure formatter behavior
 */
fun TemporalFormatterBuilder.wholeNumber(
    property: NumberProperty,
    length: IntRange,
    builder: NumberFormatterBuilder.() -> Unit = {}
) {
    wholeNumber(property, length.first, length.last, builder)
}

/**
 * Appends a decimal number.
 *
 * The start will be padded with zero if necessary to satisfy [wholeLength]. Similarly, zero will be added to the end to
 * satisfy the minimum [fractionLength]. If the value of the fraction property is zero and the minimum [fractionLength]
 * is zero, the decimal separator will be omitted.
 *
 * The character associated with the decimal separator is controlled by the [TemporalFormatter.Settings]. By default,
 * this is '.'. This may be overridden by using a different [NumberStyle].
 *
 * @param wholeLength the number of digits to parse from the whole part, excluding sign
 * @param fractionLength the number of digits to parse from the fraction part
 * @param fractionScale the number of digits to normalize the fraction to -- by default 9, indicating nanoseconds
 * @see NumberStyle
 */
fun TemporalFormatterBuilder.decimalNumber(
    wholeProperty: NumberProperty,
    fractionProperty: NumberProperty,
    wholeLength: IntRange = 1..19,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9
) {
    decimalNumber(
        wholeProperty,
        fractionProperty,
        wholeLength.first,
        wholeLength.last,
        fractionLength.first,
        fractionLength.last,
        fractionScale
    )
}

/**
 * Appends a fraction.
 */
fun TemporalFormatterBuilder.fraction(property: NumberProperty, length: IntRange, scale: Int = 9) {
    fraction(property, length.first, length.last, scale)
}
