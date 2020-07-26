@file:Suppress("NOTHING_TO_INLINE")

package io.islandtime.internal

import io.islandtime.*
import io.islandtime.calendar.WeekSettings
import io.islandtime.measures.IntWeeks
import io.islandtime.measures.weeks
import io.islandtime.measures.years

internal inline fun Date.weekOfMonthImpl(settings: WeekSettings): Int = weekNumber(dayOfMonth, settings)
internal inline fun Date.weekOfYearImpl(settings: WeekSettings): Int = weekNumber(dayOfYear, settings)

internal fun Date.weekBasedYearImpl(settings: WeekSettings): Int {
    val dayOfYear = dayOfYear
    val offset = startOfWeekOffset(dayOfWeek, dayOfYear, settings)
    val week = weekNumber(dayOfYear, offset)

    return if (week == 0) {
        year - 1
    } else {
        val weekOfNextYear = weekNumber(
            dayOfMonthOrYear = lengthOfYear.value + settings.minimumDaysInFirstWeek,
            startOfWeekOffset = offset
        )

        if (week >= weekOfNextYear) year + 1 else year
    }
}

internal fun Date.weekOfWeekBasedYearImpl(settings: WeekSettings): Int {
    val dayOfYear = dayOfYear
    val offset = startOfWeekOffset(dayOfWeek, dayOfYear, settings)
    val week = weekNumber(dayOfYear, offset)

    return if (week == 0) {
        (toYear() - 1.years).endDate.weekOfWeekBasedYear(settings)
    } else {
        val weekOfNextYear = weekNumber(
            dayOfMonthOrYear = lengthOfYear.value + settings.minimumDaysInFirstWeek,
            startOfWeekOffset = offset
        )

        if (week >= weekOfNextYear) week - weekOfNextYear + 1 else week
    }
}

internal fun lengthOfWeekBasedYear(weekBasedYear: Int): IntWeeks {
    return lastWeekOfWeekBasedYear(weekBasedYear).weeks
}

internal fun lastWeekOfWeekBasedYear(weekBasedYear: Int): Int {
    val year = Year(weekBasedYear)
    val startOfWeekBasedYear = year.startDate
    val dayOfWeek = startOfWeekBasedYear.dayOfWeek
    val isLongYear = dayOfWeek == DayOfWeek.THURSDAY || (dayOfWeek == DayOfWeek.WEDNESDAY && year.isLeap)
    return if (isLongYear) 53 else 52
}

private fun Date.weekNumber(dayOfMonthOrYear: Int, settings: WeekSettings): Int {
    return weekNumber(dayOfWeek, dayOfMonthOrYear, settings)
}

private fun weekNumber(dayOfWeek: DayOfWeek, dayOfMonthOrYear: Int, settings: WeekSettings): Int {
    val offset = startOfWeekOffset(dayOfWeek, dayOfMonthOrYear, settings)
    return weekNumber(dayOfMonthOrYear, offset)
}

private fun startOfWeekOffset(dayOfWeek: DayOfWeek, dayOfMonthOrYear: Int, settings: WeekSettings): Int {
    val adjustedDayOfWeek = dayOfWeek.number(settings)
    val startOfWeek = (dayOfMonthOrYear - adjustedDayOfWeek) floorMod 7

    return if (startOfWeek >= settings.minimumDaysInFirstWeek) {
        7 - startOfWeek
    } else {
        -startOfWeek
    }
}

private fun weekNumber(dayOfMonthOrYear: Int, startOfWeekOffset: Int): Int {
    return (7 + startOfWeekOffset + dayOfMonthOrYear - 1) / 7
}