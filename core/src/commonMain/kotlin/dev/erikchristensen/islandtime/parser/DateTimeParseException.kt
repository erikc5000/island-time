package dev.erikchristensen.islandtime.parser

class DateTimeParseException(
    message: String,
    val parsedString: String,
    val errorIndex: Int,
    cause: Throwable? = null
) : Exception(message, cause)

fun raiseParserFieldResolutionException(objectType: String, parsedText: String): Nothing {
    throw DateTimeParseException(
        "The supplied parser was unable to supply the fields needed to resolve '$objectType'",
        parsedText,
        0
    )
}