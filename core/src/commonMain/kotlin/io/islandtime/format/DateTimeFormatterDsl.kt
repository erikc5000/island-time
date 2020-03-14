package io.islandtime.format

import io.islandtime.base.*

@DslMarker
annotation class DateTimeFormatterDsl

@DateTimeFormatterDsl
interface DateTimeFormatterBuilder {
    /**
     * Print a character indicating the sign of a [NumberProperty] -- "minus" if less than 0, otherwise "plus".
     *
     * The characters associated with a number's sign are controlled by the [DateTimeFormatterSettings]. By default,
     * this is '+' or '-'. The characters may be overridden by using a different [NumberStyle].
     *
     * @see NumberStyle
     */
    fun sign(property: NumberProperty)

    /**
     * Print a whole number, padding the start with "zero" as necessary to reach a minimum fixed length.
     *
     * @param property the number property to print the value of
     * @param length the number of characters to print, excluding any sign
     */
    fun wholeNumber(
        property: NumberProperty,
        length: Int,
        builder: NumberFormatterBuilder.() -> Unit = {}
    )

    /**
     * Print a whole number.
     *
     * @param property the number property to print the value of
     * @param length the number of characters to print, excluding any sign
     * @param builder configure parser behavior
     */
    fun wholeNumber(
        property: NumberProperty,
        length: IntRange = 1..19,
        builder: NumberFormatterBuilder.() -> Unit = {}
    )

    /**
     * Print a decimal number.
     *
     * If the minimum [fractionLength] is zero, a decimal separator isn't required.
     *
     * The character associated with the decimal separator is controlled by the [DateTimeFormatterSettings]. By default,
     * this is '.'. This may be overridden by using a different [NumberStyle].
     *
     * @param wholeLength the number of digits to parse from the whole part, excluding sign
     * @param fractionLength the number of digits to parse from the fraction part
     * @param fractionScale the number of digits to normalize the fraction to -- by default 9, indicating nanoseconds
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

    /**
     * Print the value of a [StringProperty].
     */
    fun string(property: StringProperty)

    /**
     * Print a [Char] literal.
     */
    operator fun Char.unaryPlus() {
        literal(this)
    }

    /**
     * Print a [String] literal.
     */
    operator fun String.unaryPlus() {
        literal(this)
    }

    /**
     * Print a [Char] literal.
     */
    fun literal(char: Char)

    /**
     * Print a [String] literal.
     */
    fun literal(string: String)

    /**
     * Print the localized text associated with the value of a particular [NumberProperty] in any of the specified
     * styles. If no text is available, an exception will be thrown.
     *
     * The locale used when printing text is determined by the [DateTimeFormatterSettings] in use. Text is provided by
     * the configured [DateTimeTextProvider]. This text may differ between platforms and devices.
     *
     * @param property the property to print text for
     * @param style the style of text to print
     * @see DateTimeFormatterSettings.locale
     * @see DateTimeTextProvider
     */
    fun localizedText(property: NumberProperty, style: TextStyle)

    /**
     * Make parsing optional within a block.
     *
     * If any of the parsers defined within [builder] fail, the parse result will be reset to its state before the
     * block started and parsing will continue on, assuming there are additional parsers remaining.
     *
     * @param builder define the parsers that should be considered 'optional'
     */
    fun onlyIf(condition: Temporal.() -> Boolean, builder: DateTimeFormatterBuilder.() -> Unit)

//    fun DateTimeFormatterBuilder.utcOffset(
//        format: IsoFormat = IsoFormat.EXTENDED,
//        useUtcDesignatorWhenZero: Boolean = true,
//        minutesOptional: Boolean = false,
//        includeSeconds: Boolean = true,
//        secondsOptional: Boolean = true
//    )

    /**
     * Use a formatter that has been defined outside of this builder.
     */
    fun use(child: DateTimeFormatter)
}

@DateTimeFormatterDsl
interface GroupedDateTimeFormatterBuilder {
    /**
     * Associate formatters with the [Temporal] at the corresponding position.
     *
     * @param builder define the formatters that should be associated with this [Temporal]
     */
    fun group(builder: DateTimeFormatterBuilder.() -> Unit)

    /**
     * Print a [Char] literal.
     */
    operator fun Char.unaryPlus() {
        literal(this)
    }

    /**
     * Print a [String] literal.
     */
    operator fun String.unaryPlus() {
        literal(this)
    }

    /**
     * Print a [Char] literal.
     *
     * @param char the character to print
     */
    fun literal(char: Char)

    /**
     * Print a [String] literal.
     *
     * @param string the string to print
     */
    fun literal(string: String)
}

enum class LengthExceededBehavior {
    THROW,
    SIGN_STYLE_AWAYS
}

@DateTimeFormatterDsl
interface NumberFormatterBuilder {
    /**
     * The sign style to use.
     */
    var signStyle: SignStyle

    var onLengthExceeded: LengthExceededBehavior
}

//@DateTimeFormatterDsl
//interface WholeNumberFormatterBuilder : NumberFormatterBuilder {
//    fun onLengthExceeded(action: NumberFormatterBuilder.() -> Unit)
//}

//@DateTimeFormatterDsl
//interface StringFormatterBuilder {
//    fun transform(action: (value: String) -> String)
//}

//@DateTimeFormatterDsl
//interface DecimalNumberFormatterBuilder {
//    /**
//     * Perform an action when parsing succeeds.
//     */
//    fun onParsed(action: DateTimeFormatesult.(whole: Long, fraction: Long) -> Unit)
//
//    /**
//     * Associate both the whole and fractional part of the result with a particular [NumberProperty], populating their
//     * values when parsing succeeds.
//     */
//    fun associateWith(wholeProperty: NumberProperty, fractionProperty: NumberProperty) {
//        onParsed { whole, fraction ->
//            this[wholeProperty] = whole
//            this[fractionProperty] = fraction
//        }
//    }
//}