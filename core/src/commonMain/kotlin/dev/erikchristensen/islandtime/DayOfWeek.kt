package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.internal.DAYS_IN_WEEK
import dev.erikchristensen.islandtime.interval.IntDays
import dev.erikchristensen.islandtime.interval.unaryMinus

enum class DayOfWeek(val number: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    companion object {
        val MIN = MONDAY
        val MAX = SUNDAY
    }
}

operator fun DayOfWeek.plus(days: IntDays): DayOfWeek {
    val daysToAdd = days.value % DAYS_IN_WEEK
    return DayOfWeek.values()[(ordinal + (daysToAdd + DAYS_IN_WEEK)) % DAYS_IN_WEEK]
}

operator fun DayOfWeek.minus(days: IntDays) = plus(-days)

fun Int.toDayOfWeek(): DayOfWeek {
    if (this !in DayOfWeek.MIN.number..DayOfWeek.MAX.number) {
        throw DateTimeException("'$this' is not a valid day of week number")
    }

    return DayOfWeek.values()[this - 1]
}