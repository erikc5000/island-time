package io.islandtime.parser.internal

import io.islandtime.parser.TemporalParseResult
import io.islandtime.parser.TemporalParser

internal class ParseContext(
    val settings: TemporalParser.Settings
) {
    val locale by lazy(LazyThreadSafetyMode.NONE, settings.locale)
    var isCaseSensitive = settings.isCaseSensitive
    var result = TemporalParseResult()
}