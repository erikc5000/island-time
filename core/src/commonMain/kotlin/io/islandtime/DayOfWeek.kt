package io.islandtime

import io.islandtime.internal.DAYS_PER_WEEK
import io.islandtime.measures.IntDays
import io.islandtime.measures.LongDays

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

    operator fun plus(days: IntDays) = plus(days.value % DAYS_PER_WEEK)
    operator fun plus(days: LongDays) = plus((days.value % DAYS_PER_WEEK).toInt())
    operator fun minus(days: IntDays) = plus(-(days.value % DAYS_PER_WEEK))
    operator fun minus(days: LongDays) = plus(-(days.value % DAYS_PER_WEEK).toInt())

    private fun plus(daysToAdd: Int): DayOfWeek {
        return values()[(ordinal + (daysToAdd + DAYS_PER_WEEK)) % DAYS_PER_WEEK]
    }

    companion object {
        inline val MIN get() = MONDAY
        inline val MAX get() = SUNDAY
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