package io.islandtime

open class DateTimeException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)