package io.islandtime.parser

import io.islandtime.format.NumberStyle
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale
import io.islandtime.parser.internal.ParseContext
import io.islandtime.parser.internal.TemporalParserBuilderImpl

/**
 * A parser that converts text into a collection of properties that are understood throughout Island
 * Time.
 */
abstract class TemporalParser internal constructor() {
    /**
     * Parse [text] into a [TemporalParseResult] containing all parsed properties.
     *
     * @param text text to parse
     * @param settings customize parsing behavior
     * @return a result containing all of the parsed properties
     * @throws TemporalParseException if parsing failed
     */
    fun parse(
        text: CharSequence,
        settings: Settings = Settings.DEFAULT
    ): TemporalParseResult {
        val context = ParseContext(settings)
        val endPosition = parse(context, text, 0)

        if (endPosition < 0) {
            val errorPosition = endPosition.inv()
            throw TemporalParseException(
                "Parsing failed at index $errorPosition",
                text.toString(),
                errorPosition
            )
        } else if (endPosition < text.length) {
            throw TemporalParseException(
                "Unexpected character at index $endPosition",
                text.toString(),
                endPosition
            )
        }

        return context.result
    }

    /**
     * Is this a literal parser?
     */
    internal open val isLiteral: Boolean get() = false

    /**
     * Returns `true` if the parser never populates values in the result.
     */
    internal open val isConst: Boolean get() = false

    internal abstract fun parse(context: ParseContext, text: CharSequence, position: Int): Int

    /**
     * Settings that control the parsing behavior.
     * @property numberStyle Defines the set of characters that should be used when parsing numbers.
     * @property locale A function that will be invoked to provide a locale if one is needed during
     *                  parsing.
     */
    data class Settings(
        val numberStyle: NumberStyle = NumberStyle.DEFAULT,
        val locale: () -> Locale = { defaultLocale() },
        val isCaseSensitive: Boolean = true
    ) {
        constructor(
            numberStyle: NumberStyle = NumberStyle.DEFAULT,
            locale: Locale,
            isCaseSensitive: Boolean = true
        ) : this(numberStyle, { locale }, isCaseSensitive)

        companion object {
            /**
             * The default parser settings.
             */
            val DEFAULT = Settings()
        }
    }
}

/**
 * Define a custom [TemporalParser].
 * @see DateTimeParsers
 */
inline fun temporalParser(builder: TemporalParserBuilder.() -> Unit): TemporalParser {
    return TemporalParserBuilderImpl().apply(builder).build()
}