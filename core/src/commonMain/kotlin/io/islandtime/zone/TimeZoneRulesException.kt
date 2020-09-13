package io.islandtime.zone

import io.islandtime.DateTimeException

class TimeZoneRulesException(
    message: String? = null,
    cause: Throwable? = null
) : DateTimeException(message, cause)
