package io.islandtime.parser

import io.islandtime.DateTimeException

class TemporalParseException(
    message: String? = null,
    val parsedString: String? = null,
    val errorIndex: Int = 0,
    cause: Throwable? = null
) : DateTimeException(message, cause)

internal inline fun <reified T> throwParserPropertyResolutionException(parsedText: String): Nothing {
    val objectType = T::class.simpleName ?: "Unknown"

    throw TemporalParseException(
        "The provided parser was unable to supply the properties needed to resolve an object of type '$objectType'",
        parsedText
    )
}

internal inline fun <reified T> throwParserGroupResolutionException(
    expectedCount: Int,
    actualCount: Int,
    parsedText: String
): Nothing {
    val objectType = T::class.simpleName ?: "Unknown"

    throw TemporalParseException(
        "The provided parser was unable resolve an object of type '$objectType'. Expected $expectedCount groups, got " +
            "$actualCount.",
        parsedText
    )
}

internal inline fun <reified T> List<TemporalParseResult>.expectingGroupCount(
    expected: Int,
    parsedText: String
): List<TemporalParseResult> {
    if (size != expected) {
        throwParserGroupResolutionException<T>(2, size, parsedText)
    }
    return this
}