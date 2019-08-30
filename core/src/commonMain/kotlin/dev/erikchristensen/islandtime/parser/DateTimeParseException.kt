package dev.erikchristensen.islandtime.parser

class DateTimeParseException(
    message: String? = null,
    val parsedString: String? = null,
    val errorIndex: Int = 0,
    cause: Throwable? = null
) : Exception(message, cause)

fun raiseParserFieldResolutionException(objectType: String, parsedText: String): Nothing {
    throw DateTimeParseException(
        "The provided parser was unable to supply the fields needed to resolve '$objectType'",
        parsedText
    )
}