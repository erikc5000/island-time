package io.islandtime.parser

import io.islandtime.parser.internal.DateTimeParseContext
import io.islandtime.parser.internal.GroupedDateTimeParserBuilderImpl

class GroupedDateTimeParser internal constructor(
    private val childParsers: List<Any>,
    private val isAnyOf: Boolean = false
) {
    /**
     * Parse [text] into a list of results, each containing the parsed properties associated with a particular group.
     *
     * @param text text to parse
     * @param settings customize parsing behavior
     * @return a list of results, matching the number of groups defined in the parser
     * @throws DateTimeParseException if parsing failed
     */
    fun parse(
        text: CharSequence,
        settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
    ): List<DateTimeParseResult> {
        val context = DateTimeParseContext(settings)
        val (endPosition, results) = parse(context, text, 0)

        if (endPosition < 0) {
            val errorPosition = endPosition.inv()

            throw DateTimeParseException(
                "Parsing failed at index $errorPosition",
                text.toString(),
                errorPosition
            )
        } else if (endPosition < text.length) {
            throw DateTimeParseException(
                "Unexpected character at index $endPosition",
                text.toString(),
                endPosition
            )
        }

        return results
    }

    internal fun parse(
        context: DateTimeParseContext,
        text: CharSequence,
        position: Int
    ): Pair<Int, List<DateTimeParseResult>> {
        var currentPosition = position
        val results = mutableListOf<DateTimeParseResult>()
        var first = true

        for (parser in childParsers) {
            if (first) {
                first = false
            } else {
                context.result = DateTimeParseResult()
            }

            val doneParsing = when (parser) {
                is GroupedDateTimeParser -> {
                    val (childEndPosition, subResults) = parser.parse(context, text, currentPosition)

                    if (childEndPosition >= 0) {
                        results += subResults
                    }

                    if (!isAnyOf || childEndPosition >= 0) {
                        currentPosition = childEndPosition
                    }

                    (isAnyOf && childEndPosition >= 0) || (!isAnyOf && childEndPosition < 0)
                }
                is DateTimeParser -> {
                    val childEndPosition = parser.parse(context, text, currentPosition)

                    if (childEndPosition >= 0 && !parser.isLiteral) {
                        results += context.result
                    }

                    if (!isAnyOf || childEndPosition >= 0) {
                        currentPosition = childEndPosition
                    }

                    (isAnyOf && childEndPosition >= 0) || (!isAnyOf && childEndPosition < 0)
                }
                else -> throw IllegalStateException("Unexpected parser type")
            }

            if (doneParsing) {
                break
            }
        }

        return Pair(currentPosition, results)
    }
}

/**
 * Create a [GroupedDateTimeParser].
 *
 * A grouped parser is capable of grouping the parsed properties into separate results, allowing the same property to be
 * reused multiple times within a character sequence.
 */
inline fun groupedDateTimeParser(
    builder: GroupedDateTimeParserBuilder.() -> Unit
): GroupedDateTimeParser {
    return GroupedDateTimeParserBuilderImpl().apply(builder).build()
}