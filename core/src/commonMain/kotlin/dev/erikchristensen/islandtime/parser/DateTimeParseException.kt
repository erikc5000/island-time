package dev.erikchristensen.islandtime.parser

class DateTimeParseException(
    message: String,
    val parsedString: String,
    val errorIndex: Int,
    cause: Throwable? = null
) : Exception(message, cause)