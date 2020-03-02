package io.islandtime.parser

import io.islandtime.base.BooleanProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.TemporalProperty
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.TextStyle
import io.islandtime.format.NumberStyle

@DslMarker
annotation class DateTimeParserDsl

enum class SignStyle {
    NEGATIVE_ONLY,
    NEVER,
    ALWAYS
}

enum class StringParseAction {
    ACCEPT_AND_CONTINUE,
    REJECT_AND_STOP
}

@DateTimeParserDsl
interface DateTimeParserBuilder {
    /**
     * Parse a character indicating the sign of a number.
     *
     * The characters associated with a number's sign are controlled by the [DateTimeParserSettings]. By default, this
     * is '+', '-', or '−' as specified in ISO-8601. The characters may be overridden by using a different
     * [NumberStyle].
     *
     * @param builder configure parser behavior
     * @see NumberStyle
     */
    fun sign(builder: SignParserBuilder.() -> Unit = {})

    /**
     * Parse a whole number of fixed length.
     *
     * @param length the number of characters to parse, excluding any sign
     * @param builder configure parser behavior
     */
    fun wholeNumber(
        length: Int,
        builder: WholeNumberParserBuilder.() -> Unit = {}
    )

    /**
     * Parse a whole number of variable length.
     *
     * @param length the number of characters to parse, excluding any sign
     * @param builder configure parser behavior
     */
    fun wholeNumber(
        length: IntRange = 1..19,
        builder: WholeNumberParserBuilder.() -> Unit = {}
    )

    /**
     * Parse a decimal number.
     *
     * If the minimum [fractionLength] is zero, a decimal separator isn't required.
     *
     * The characters associated with a decimal separator are controlled by the [DateTimeParserSettings]. By default,
     * this is '.' or ',' as specified in ISO-8601. The characters may be overridden by using a different
     * [NumberStyle].
     *
     * @param wholeLength the number of digits to parse from the whole part, excluding sign
     * @param fractionLength the number of digits to parse from the fraction part
     * @param fractionScale the number of digits to normalize the fraction to -- by default 9, indicating nanoseconds
     * @param builder configure parser behavior
     * @see NumberStyle
     */
    fun decimalNumber(
        wholeLength: IntRange = 1..19,
        fractionLength: IntRange = 0..9,
        fractionScale: Int = 9,
        builder: DecimalNumberParserBuilder.() -> Unit = {}
    )

    /**
     * Parse a variable length string.
     *
     * Each character will be parsed starting from the current position until either the maximum number of characters
     * allowed by [length] is reached or parsing is stopped by a [StringParserBuilder.onEachChar] handler.
     *
     * @param length restricts the length of the string to a certain range -- no restrictions if the range is empty
     * @param builder configure the parser behavior
     */
    fun string(
        length: IntRange = IntRange.EMPTY,
        builder: StringParserBuilder.() -> Unit
    )

    /**
     * Parse a [Char] literal.
     */
    operator fun Char.unaryPlus() {
        literal(this)
    }

    /**
     * Parse a [String] literal.
     */
    operator fun String.unaryPlus() {
        literal(this)
    }

    /**
     * Parse a [Char] literal.
     *
     * @param char the character to match
     * @param builder configure the parser behavior
     */
    fun literal(char: Char, builder: LiteralParserBuilder.() -> Unit = {})

    /**
     * Parse a [String] literal.
     *
     * @param string the string to match
     * @param builder configure the parser behavior
     */
    fun literal(string: String, builder: LiteralParserBuilder.() -> Unit = {})

    /**
     * Parse localized text associated with a particular [NumberProperty] in any of the specified styles. If
     * successful, the property's value will be populated. If no text is known for the property or a match can't be found,
     * the parsing operation will return an error.
     *
     * The locale used when matching text is determined by the [DateTimeParserSettings] in use. Text is provided by the
     * configured [DateTimeTextProvider]. Be mindful that this text may differ between platforms and devices. If at
     * all possible, non-localized representations should be used instead.
     *
     * @param property the property to match text for
     * @param styles the styles of text to match
     * @see DateTimeParserSettings.locale
     * @see DateTimeTextProvider
     */
    fun localizedText(property: NumberProperty, styles: Set<TextStyle>)

    /**
     * Make parsing optional within a block.
     *
     * If any of the parsers defined within [builder] fail, the parse result will be reset to its state before the
     * block started and parsing will continue on, assuming there are additional parsers remaining.
     *
     * @param builder define the parsers that should be considered 'optional'
     */
    fun optional(builder: DateTimeParserBuilder.() -> Unit)

    /**
     * Try each of the parsers defined by [builders] until one succeeds. If none succeed, parsing is considered to have
     * failed.
     */
    fun anyOf(vararg builders: DateTimeParserBuilder.() -> Unit)

    /**
     * Try each of the parsers defined by [childParsers] until one succeeds. If none succeed, parsing is considered to
     * have failed.
     */
    fun anyOf(vararg childParsers: DateTimeParser)

    /**
     * Use a parser that has been defined outside of this builder.
     */
    fun childParser(childParser: DateTimeParser)

    /**
     * Force parsing to be case-sensitive within this block.
     */
    fun caseSensitive(builder: DateTimeParserBuilder.() -> Unit)

    /**
     * Force parsing to be case-insensitive within this block.
     */
    fun caseInsensitive(builder: DateTimeParserBuilder.() -> Unit)
}

@DateTimeParserDsl
interface GroupedDateTimeParserBuilder {
    /**
     * Create a distinct parse result and associate any parsed data with it.
     *
     * @param builder define the parsers that should be associated with this result
     */
    fun group(builder: DateTimeParserBuilder.() -> Unit)

    /**
     * Parse a [Char] literal.
     */
    operator fun Char.unaryPlus() {
        literal(this)
    }

    /**
     * Parse a [String] literal.
     */
    operator fun String.unaryPlus() {
        literal(this)
    }

    /**
     * Parse a [Char] literal.
     *
     * @param char the character to match
     */
    fun literal(char: Char)

    /**
     * Parse a [String] literal.
     *
     * @param string the string to match
     */
    fun literal(string: String)

    /**
     * Try each of the parsers defined by [builders] until one succeeds. If none succeed, parsing is considered to have
     * failed.
     */
    fun anyOf(vararg builders: GroupedDateTimeParserBuilder.() -> Unit)

    /**
     * Try each of the parsers defined by [childParsers] until one succeeds and include all of its groups in the
     * parsing results. If none of the parsers succeed, parsing is considered to have failed at the starting index.
     */
    fun anyOf(vararg childParsers: GroupedDateTimeParser)
}

@DateTimeParserDsl
interface SignParserBuilder {
    /**
     * Perform an action when a number's sign has been successfully parsed
     */
    fun onParsed(action: DateTimeParseResult.(parsed: Int) -> Unit)

    /**
     * Associate the result with a particular [TemporalProperty], setting it to `-1` when negative or `1` when positive.
     */
    fun associateWith(property: TemporalProperty<Long>) {
        onParsed { this[property] = it.toLong() }
    }

    /**
     * Associate the result with a particular [NumberProperty], setting it to `-1L` when negative or `1L` when positive.
     */
    fun associateWith(property: NumberProperty) {
        onParsed { this[property] = it.toLong() }
    }
}

@DateTimeParserDsl
interface NumberParserBuilder {
    /**
     * Enforce a particular sign style.
     */
    fun enforceSignStyle(signStyle: SignStyle)
}

@DateTimeParserDsl
interface WholeNumberParserBuilder : NumberParserBuilder {
    /**
     * Perform an action when parsing succeeds.
     */
    fun onParsed(action: DateTimeParseResult.(parsed: Long) -> Unit)

    /**
     * Associate the result with a particular [TemporalProperty], populating its value when parsing succeeds.
     */
    fun associateWith(property: TemporalProperty<Long>) {
        onParsed { this[property] = it }
    }

    /**
     * Associate the result with a particular [NumberProperty], populating its value when parsing succeeds.
     */
    fun associateWith(property: NumberProperty) {
        onParsed { this[property] = it }
    }
}

@DateTimeParserDsl
interface DecimalNumberParserBuilder : NumberParserBuilder {
    /**
     * Perform an action when parsing succeeds.
     */
    fun onParsed(action: DateTimeParseResult.(whole: Long, fraction: Long) -> Unit)

    /**
     * Associate both the whole and fractional part of the result with a particular [NumberProperty], populating their
     * values when parsing succeeds.
     */
    fun associateWith(wholeProperty: NumberProperty, fractionProperty: NumberProperty) {
        onParsed { whole, fraction ->
            this[wholeProperty] = whole
            this[fractionProperty] = fraction
        }
    }
}

@DateTimeParserDsl
interface StringParserBuilder {
    /**
     * Execute a block as each character in the string is encountered during parsing.
     *
     * Return [StringParseAction.ACCEPT_AND_CONTINUE] to continue parsing or [StringParseAction.REJECT_AND_STOP] to
     * reject the current character and trigger the end of parsing.
     */
    fun onEachChar(action: DateTimeParseResult.(char: Char, index: Int) -> StringParseAction)

    /**
     * Perform an action when parsing succeeds.
     */
    fun onParsed(action: DateTimeParseResult.(parsed: String) -> Unit)
}

@DateTimeParserDsl
interface LiteralParserBuilder {
    /**
     * Perform an action when parsing succeeds.
     */
    fun onParsed(action: DateTimeParseResult.() -> Unit)

    /**
     * Associate the result with a particular [BooleanProperty], setting its value to `true` when parsing succeeds.
     */
    fun associateWith(property: BooleanProperty) {
        onParsed { this[property] = true }
    }
}