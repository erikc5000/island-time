package io.islandtime.parser.internal

import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParserSettings

internal class DateTimeParseContext(
    val settings: DateTimeParserSettings
) {
    var result = DateTimeParseResult()
}