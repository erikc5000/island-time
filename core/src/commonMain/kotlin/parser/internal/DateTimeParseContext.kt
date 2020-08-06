package io.islandtime.parser.internal

import io.islandtime.parser.DateTimeParseResult
import io.islandtime.parser.DateTimeParserSettings

internal class DateTimeParseContext(
    val settings: DateTimeParserSettings
) {
    val locale by lazy(LazyThreadSafetyMode.NONE, settings.locale)
    var isCaseSensitive = settings.isCaseSensitive
    var result = DateTimeParseResult()
}
