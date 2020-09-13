package io.islandtime.parser.dsl

import io.islandtime.base.BooleanProperty
import io.islandtime.base.NumberProperty
import io.islandtime.format.*
import io.islandtime.format.dsl.IslandTimeFormatDsl
import io.islandtime.format.dsl.LiteralFormatBuilder
import io.islandtime.parser.TemporalParser
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty

@IslandTimeFormatDsl
interface TemporalParserBuilder : LiteralFormatBuilder, BaseParserBuilder<TemporalParserBuilder> {
    /**
     * Appends a character literal.
     */
    fun literal(char: Char, builder: LiteralParserBuilder.() -> Unit)

    /**
     * Appends a string literal.
     */
    fun literal(string: String, builder: LiteralParserBuilder.() -> Unit)

    /**
     * Appends the character indicating the sign of a number.
     *
     * The characters associated with a number's sign are controlled by the [TemporalParser.Settings]. By default, this
     * is '+', '-', or '−' as specified in ISO-8601. The characters may be overridden by using a different
     * [NumberStyle].
     *
     * @param builder configure parser behavior
     * @see NumberStyle
     */
    fun sign(builder: SignParserBuilder.() -> Unit = {})

    /**
     * Appends a whole number.
     */
    fun wholeNumber(minLength: Int = 1, maxLength: Int = 19, builder: WholeNumberParserBuilder.() -> Unit = {})

    /**
     * Appends a decimal number.
     *
     * The characters associated with a decimal separator are controlled by the [TemporalParser.Settings]. By default,
     * this is '.' or ',' as specified in ISO-8601. The characters may be overridden by using a different [NumberStyle].
     *
     * @param minWholeLength the minimum number of digits in the whole part
     * @param maxWholeLength the maximum number of digits in the whole part
     * @param minFractionLength the minimum number of digits in the fractional part
     * @param maxFractionLength the maximum number of digits in the fractional part
     * @param fractionScale the number of digits to normalize to &mdash; for example, `3` would indicate "milli"
     * @see NumberStyle
     */
    fun decimalNumber(
        minWholeLength: Int = 1,
        maxWholeLength: Int = 19,
        minFractionLength: Int = 0,
        maxFractionLength: Int = 9,
        fractionScale: Int = 9,
        builder: DecimalNumberParserBuilder.() -> Unit = {}
    )

    /**
     * Appends a fraction.
     *
     * @param minLength the minimum number of digits
     * @param maxLength the maximum number of digits
     * @param scale the number of digits to normalize to &mdash; for example, `3` would indicate "milli"
     */
    fun fraction(minLength: Int = 1, maxLength: Int = 9, scale: Int = 9, builder: FractionParserBuilder.() -> Unit = {})

    /**
     * Appends a text value.
     *
     * Each character will be parsed starting from the current position until either the maximum number of characters
     * allowed is reached or parsing is stopped by a [TextParserBuilder.onEachChar] handler.
     */
    fun text(minLength: Int = 0, maxLength: Int = Int.MAX_VALUE, builder: TextParserBuilder.() -> Unit = {})

    /**
     * Appends the localized date-time text associated with a [NumberProperty] in any of the specified [styles]. If
     * successful, the property's value will be populated. If no text is known for the property or a match can't be
     * found, the parsing operation will return an error.
     *
     * The locale used when matching text is determined by the [TemporalParser.Settings] in use. Text is provided by the
     * configured [DateTimeTextProvider]. Be mindful that this text may differ between platforms and devices. If at
     * all possible, non-localized representations should be used instead.
     *
     * @param property the property to match text for
     * @param styles the styles of text to match
     * @see TemporalParser.Settings.locale
     * @see DateTimeTextProvider
     */
    fun localizedDateTimeText(property: NumberProperty, styles: Set<TextStyle>)

    /**
     * Appends a localized UTC offset, optionally accepting the long format only.
     *
     * The result will be associated with either [UtcOffsetProperty.TotalSeconds] or the combination of
     * [UtcOffsetProperty.Sign], [UtcOffsetProperty.Hours], [UtcOffsetProperty.Minutes], and
     * [UtcOffsetProperty.Seconds] as appropriate.
     */
    fun localizedOffset(longFormatOnly: Boolean = false)

    /**
     * Appends a localized time zone name, associating the result with [TimeZoneProperty.Id] or in the case of a fixed
     * offset, some combination of [UtcOffsetProperty].
     */
    fun timeZoneName(builder: TimeZoneNameParserBuilder.() -> Unit = {})
}

fun TemporalParserBuilder.wholeNumber(length: IntRange = 1..19, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(minLength = length.first, maxLength = length.last, builder)
}

fun TemporalParserBuilder.wholeNumber(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(minLength = length, maxLength = length, builder)
}

fun TemporalParserBuilder.decimalNumber(
    wholeLength: IntRange = 1..19,
    fractionLength: IntRange = 0..9,
    fractionScale: Int = 9,
    builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    decimalNumber(
        minWholeLength = wholeLength.first,
        maxWholeLength = wholeLength.last,
        minFractionLength = fractionLength.first,
        maxFractionLength = fractionLength.last,
        fractionScale,
        builder
    )
}

fun TemporalParserBuilder.fraction(
    length: IntRange = 1..19,
    scale: Int = 9,
    builder: FractionParserBuilder.() -> Unit = {}
) {
    fraction(minLength = length.first, maxLength = length.last, scale = scale, builder)
}

fun TemporalParserBuilder.fraction(length: Int, scale: Int = 9, builder: FractionParserBuilder.() -> Unit = {}) {
    fraction(minLength = length, maxLength = length, scale, builder)
}

fun TemporalParserBuilder.text(length: IntRange = 1..19, builder: TextParserBuilder.() -> Unit = {}) {
    text(minLength = length.first, maxLength = length.last, builder)
}

enum class StringParseAction {
    ACCEPT_AND_CONTINUE,
    REJECT_AND_STOP
}

@IslandTimeFormatDsl
interface LiteralParserBuilder {
    /**
     * Performs an action when parsing succeeds.
     */
    fun onParsed(action: TemporalParser.Context.() -> Unit)
}

/**
 * Associates the result with a [BooleanProperty], setting its value to `true` when parsing succeeds.
 */
fun LiteralParserBuilder.associateWith(property: BooleanProperty) {
    onParsed { result[property] = true }
}

@IslandTimeFormatDsl
interface SignParserBuilder {
    /**
     * Performs an action when a number's sign has been successfully parsed.
     */
    fun onParsed(action: TemporalParser.Context.(parsed: Int) -> Unit)
}

/**
 * Associates the result with a [NumberProperty], setting it to `-1L` when negative or `1L` when positive.
 */
fun SignParserBuilder.associateWith(property: NumberProperty) {
    onParsed { result[property] = it.toLong() }
}

@IslandTimeFormatDsl
interface SignedNumberParserBuilder {
    /**
     * Enforces a [SignStyle], causing parsing to fail when it isn't satisfied by the input.
     */
    fun enforceSignStyle(signStyle: SignStyle)
}

@IslandTimeFormatDsl
interface WholeNumberParserBuilder : SignedNumberParserBuilder {
    /**
     * Performs an action when parsing succeeds.
     */
    fun onParsed(action: TemporalParser.Context.(parsed: Long) -> Unit)
}

/**
 * Associates the result with a [NumberProperty], populating its value when parsing succeeds.
 */
fun WholeNumberParserBuilder.associateWith(property: NumberProperty) {
    onParsed { result[property] = it }
}

@IslandTimeFormatDsl
interface DecimalNumberParserBuilder : SignedNumberParserBuilder {
    /**
     * Performs an action when parsing succeeds.
     */
    fun onParsed(action: TemporalParser.Context.(whole: Long, fraction: Long) -> Unit)
}

/**
 * Associates both the whole and fractional part of the result with a [NumberProperty], populating their values when
 * parsing succeeds.
 */
fun DecimalNumberParserBuilder.associateWith(wholeProperty: NumberProperty, fractionProperty: NumberProperty) {
    onParsed { whole, fraction ->
        result[wholeProperty] = whole
        result[fractionProperty] = fraction
    }
}

@IslandTimeFormatDsl
interface FractionParserBuilder {
    /**
     * Performs an action when parsing succeeds.
     */
    fun onParsed(action: TemporalParser.Context.(parsed: Long) -> Unit)
}

/**
 * Associates the result with a [property], populating its value when parsing succeeds.
 */
fun FractionParserBuilder.associatedWith(property: NumberProperty) {
    onParsed { result[property] = it }
}

@IslandTimeFormatDsl
interface TextParserBuilder {
    /**
     * Executes a block as each character in the string is encountered during parsing.
     *
     * Return [StringParseAction.ACCEPT_AND_CONTINUE] to continue parsing or [StringParseAction.REJECT_AND_STOP] to
     * reject the current character and trigger the end of parsing.
     */
    fun onEachChar(action: TemporalParser.Context.(char: Char, index: Int) -> StringParseAction)

    /**
     * Performs an action when parsing succeeds.
     */
    fun onParsed(action: TemporalParser.Context.(parsed: String) -> Unit)
}


typealias DisambiguationAction = TemporalParser.Context.(name: String, possibleValues: Iterable<String>) -> String?

@IslandTimeFormatDsl
interface LocalizedTextParserBuilder {
    /**
     * Determines the value to use when more than one possibility exists.
     */
    fun disambiguate(action: DisambiguationAction)
}

/**
 * A behavior that can be used to disambiguate parsing results.
 */
enum class DisambiguationStrategy(internal val action: DisambiguationAction) {
    PICK_FIRST({ _, possibleValues -> possibleValues.first() }),
    RAISE_ERROR({ _, _ -> null })
}

/**
 * Uses the provided [strategy] to determine the value to use when more than one possibility exists.
 */
fun LocalizedTextParserBuilder.disambiguate(strategy: DisambiguationStrategy): Unit = disambiguate(strategy.action)

interface TimeZoneNameParserBuilder : LocalizedTextParserBuilder {
    /**
     * The set of styles to parse. If empty, all styles will be used.
     */
    var styles: Set<TimeZoneNameStyle>
}
