package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.IntDays
import dev.erikchristensen.islandtime.interval.days

inline class Year(val value: Int) : Comparable<Year> {
    override fun compareTo(other: Year) = value - other.value

    companion object {
        const val MIN_VALUE = 1
        const val MAX_VALUE = 9999
    }
}

val Year.isValid: Boolean
    get() = value in Year.MIN_VALUE..Year.MAX_VALUE

val Year.isLeap: Boolean
    get() = value % 4 == 0 && (value % 100 != 0 || value % 400 == 0)

val Year.length: IntDays
    get() = if (isLeap) 366.days else 365.days

fun checkYear(year: Int) {
    if (!Year(year).isValid) {
        throw IllegalArgumentException(
            "The year '$year' is outside the supported range of ${Year.MIN_VALUE}..${Year.MAX_VALUE}"
        )
    }
}