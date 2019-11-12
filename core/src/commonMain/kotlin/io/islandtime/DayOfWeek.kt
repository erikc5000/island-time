package io.islandtime

import io.islandtime.internal.DAYS_IN_WEEK
import io.islandtime.measures.IntDays

/**
 * A day of the week.
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    /**
     * The ISO day of week number.
     *
     * The ISO week starts on Monday (1) and ends on Sunday (7).
     */
    val number: Int get() = ordinal + 1

    operator fun plus(days: IntDays): DayOfWeek {
        val daysToAdd = days.value % DAYS_IN_WEEK
        return values()[(ordinal + (daysToAdd + DAYS_IN_WEEK)) % DAYS_IN_WEEK]
    }

    operator fun minus(days: IntDays) = plus(-days)

    companion object {
        val MIN = MONDAY
        val MAX = SUNDAY
    }
}

/**
 * Convert an ISO day of week number to a [DayOfWeek].
 *
 * The ISO week starts on Monday (1) and ends on Sunday (7).
 */
fun Int.toDayOfWeek(): DayOfWeek {
    if (this !in DayOfWeek.MIN.number..DayOfWeek.MAX.number) {
        throw DateTimeException("'$this' is not a valid day of week number")
    }

    return DayOfWeek.values()[this - 1]
}