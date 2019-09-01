package dev.erikchristensen.islandtime

open class DateTimeException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)