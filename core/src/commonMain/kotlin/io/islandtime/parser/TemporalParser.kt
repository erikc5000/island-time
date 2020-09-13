package io.islandtime.parser

import io.islandtime.calendar.LocalizationContext
import io.islandtime.calendar.WeekSettings
import io.islandtime.calendar.weekSettings
import io.islandtime.format.NumberStyle
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale
import io.islandtime.parser.dsl.TemporalParserBuilder
import io.islandtime.parser.internal.TemporalParserBuilderImpl

/**
 * A parser that converts text into a collection of properties that are understood throughout Island Time.
 */
abstract class TemporalParser internal constructor() {
    /**
     * Parses [text] into a [ParseResult] containing all parsed properties.
     *
     * @param text text to parse
     * @param settings customize parsing behavior
     * @return a result containing all of the parsed properties
     * @throws TemporalParseException if parsing failed
     */
    fun parse(
        text: CharSequence,
        settings: Settings = Settings.DEFAULT
    ): ParseResult {
        val context = MutableContext(settings)
        val endPosition = parse(context, text, position = 0)

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

    internal abstract fun parse(context: MutableContext, text: CharSequence, position: Int): Int

    /**
     * Settings that control the parsing behavior.
     * @property numberStyle Defines the set of characters that should be used when parsing numbers.
     * @property locale A function that will be invoked to provide a locale if one is needed during
     *                  parsing.
     */
    data class Settings(
        val numberStyle: NumberStyle = NumberStyle.DEFAULT,
        val locale: () -> Locale = { defaultLocale() },
        val weekSettingsOverride: WeekSettings? = null,
        val isCaseSensitive: Boolean = true
    ) {
        constructor(
            numberStyle: NumberStyle = NumberStyle.DEFAULT,
            locale: Locale,
            weekSettingsOverride: WeekSettings? = null,
            isCaseSensitive: Boolean = true
        ) : this(numberStyle, { locale }, weekSettingsOverride, isCaseSensitive)

        companion object {
            /**
             * The default parser settings.
             */
            val DEFAULT = Settings()
        }
    }

    interface Context : LocalizationContext {
        val numberStyle: NumberStyle
        val isCaseSensitive: Boolean
        val result: ParseResult
    }

    internal class MutableContext(private val settings: Settings) : Context {
        override val locale by lazy(LazyThreadSafetyMode.NONE, settings.locale)
        override val weekSettings: WeekSettings get() = settings.weekSettingsOverride ?: locale.weekSettings
        override val numberStyle: NumberStyle get() = settings.numberStyle
        override var isCaseSensitive = settings.isCaseSensitive
        override var result = ParseResult()
    }
}

/**
 * Builds a custom [TemporalParser].
 * @see DateTimeParsers
 */
@Suppress("FunctionName")
inline fun TemporalParser(builder: TemporalParserBuilder.() -> Unit): TemporalParser {
    return TemporalParserBuilderImpl().apply(builder).build()
}
