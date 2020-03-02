package io.islandtime.parser

import io.islandtime.parser.internal.DateTimeParseContext
import io.islandtime.parser.internal.DateTimeParserBuilderImpl

/**
 * A parser that converts text into a collection of properties that are understood throughout Island Time.
 */
abstract class DateTimeParser internal constructor() {
    /**
     * Parse [text] into a [DateTimeParseResult] containing all parsed properties.
     *
     * @param text text to parse
     * @param settings customize parsing behavior
     * @return a result containing all of the parsed properties
     * @throws DateTimeParseException if parsing failed
     */
    fun parse(
        text: CharSequence,
        settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
    ): DateTimeParseResult {
        val context = DateTimeParseContext(settings)
        val endPosition = parse(context, text, 0)

        if (endPosition < 0) {
            val errorPosition = endPosition.inv()
            throw DateTimeParseException("Parsing failed at index $errorPosition", text.toString(), errorPosition)
        } else if (endPosition < text.length) {
            throw DateTimeParseException("Unexpected character at index $endPosition", text.toString(), endPosition)
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

    internal abstract fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int
}

/**
 * Define a custom [DateTimeParser].
 * @see DateTimeParsers
 */
inline fun dateTimeParser(builder: DateTimeParserBuilder.() -> Unit): DateTimeParser {
    return DateTimeParserBuilderImpl().apply(builder).build()
}