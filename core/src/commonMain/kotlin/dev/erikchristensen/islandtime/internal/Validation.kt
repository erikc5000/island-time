package dev.erikchristensen.islandtime.internal

internal fun isValidHour(hour: Int): Boolean {
    return hour in 0 until HOURS_PER_DAY
}

internal fun isValidMinute(minute: Int): Boolean {
    return minute in 0 until MINUTES_PER_HOUR
}

internal fun isValidSecond(second: Int): Boolean {
    return second in 0 until SECONDS_PER_MINUTE
}

internal fun isValidNanoOfSecond(nanoOfSecond: Int): Boolean {
    return nanoOfSecond in 0 until NANOSECONDS_PER_SECOND
}