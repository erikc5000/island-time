@file:Suppress("FunctionName")

package io.islandtime.parser

import io.islandtime.format.dsl.pattern
import io.islandtime.parser.dsl.DateTimeParserBuilder
import io.islandtime.parser.internal.DateTimeParserBuilderImpl

inline fun DateTimeParser(builder: DateTimeParserBuilder.() -> Unit): TemporalParser {
    return DateTimeParserBuilderImpl().apply(builder).build()
}

fun DateTimeParser(pattern: String): TemporalParser {
    return DateTimeParser { pattern(pattern) }
}
