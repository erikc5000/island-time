@file:JvmMultifileClass
@file:JvmName("DateTimesKt")

package io.islandtime

import io.islandtime.calendar.WeekSettings
import io.islandtime.calendar.weekSettings
import io.islandtime.internal.lastWeekOfWeekBasedYear
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import io.islandtime.measures.weeks
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Converts this date to an ISO week date representation.
 */
inline fun <T> Date.toWeekDate(action: (year: Int, week: Int, day: Int) -> T): T {
    return action(weekBasedYear, weekOfWeekBasedYear, dayOfWeek.number)
}

/**
 * Converts this date to a week date representation using the week definition in [settings].
 */
inline fun <T> Date.toWeekDate(settings: WeekSettings, action: (year: Int, week: Int, day: Int) -> T): T {
    return action(weekBasedYear(settings), weekOfWeekBasedYear(settings), dayOfWeek.number(settings))
}

/**
 * Converts this date to a week date representation using the week definition associated with the provided [locale].
 *
 * Keep in mind that that the system's calendar settings may differ from that of the default locale on some platforms.
 * To respect the system calendar settings, use [WeekSettings.systemDefault] instead.
 */
inline fun <T> Date.toWeekDate(locale: Locale, action: (year: Int, week: Int, day: Int) -> T): T {
    return toWeekDate(locale.weekSettings, action)
}

/**
 * Create a [Date] from an ISO week date.
 * @param year the week-based year
 * @param week the week number of the week-based year
 * @param day the ISO day of week number, 1 (Monday) to 7 (Sunday)
 * @throws DateTimeException if the year, week, or day is invalid
 */
fun Date.Companion.fromWeekDate(year: Int, week: Int, day: Int): Date {
    checkValidDayOfWeek(day)
    checkValidYear(year)
    checkValidWeekOfWeekBasedYear(week, year)
    // TODO: The day number may exceed the max near the end of the year, but is it even worth checking?

    val jan4 = Date(year, Month.JANUARY, 4)
    val dayOfYear = (week * 7 + day) - (jan4.dayOfWeek.number + 3)

    return if (dayOfYear < 1) {
        Date(year = year - 1, dayOfYear = dayOfYear + lastDayOfYear(year - 1))
    } else {
        val lastDay = lastDayOfYear(year)

        if (dayOfYear > lastDay) {
            Date(year = year + 1, dayOfYear = dayOfYear - lastDay)
        } else {
            Date(year, dayOfYear)
        }
    }
}

/**
 * Create a [Date] from a week date representation using the week definition in [settings].
 * @param year the week-based year
 * @param week the week number of the week-based year
 * @param day the day of week number, 1-7
 * @param settings the week definition to use when interpreting the [year], [week], and [day]
 */
fun Date.Companion.fromWeekDate(year: Int, week: Int, day: Int, settings: WeekSettings): Date {
    checkValidDayOfWeek(day)

    // Week dates around Date.MIN and Date.MAX can fail here if the week year exceeds the supported range, but no easy
    // way to work around that.
    checkValidYear(year)

    // TODO: This allows the week number to be invalid for the year, but is it worth checking? The day number may also
    //  exceed the max near the end of the year.
    checkValidWeekOfWeekBasedYear(week)

    val date = Date(year, Month.JANUARY, day = settings.minimumDaysInFirstWeek)
    val weeksToAdd = (week - date.weekOfYear(settings)).weeks
    val daysToAdd = weeksToAdd + (day - date.dayOfWeek.number(settings)).days
    return date + daysToAdd
}

/**
 * Create a [Date] from a week date representation using the week definition associated with the provided [locale].
 * @param year the week-based year
 * @param week the week number of the week-based year
 * @param day the day of week number, 1-7
 * @param locale the locale providing the week definition to use when interpreting the [year], [week], and [day]
 */
fun Date.Companion.fromWeekDate(year: Int, week: Int, day: Int, locale: Locale): Date {
    return fromWeekDate(year, week, day, locale.weekSettings)
}

private fun checkValidWeekOfWeekBasedYear(week: Int): Int {
    if (week !in 1..53) {
        throw DateTimeException("The week '$week' is outside the supported range of 1-53")
    }
    return week
}

private fun checkValidWeekOfWeekBasedYear(week: Int, year: Int): Int {
    checkValidWeekOfWeekBasedYear(week)

    if (week > lastWeekOfWeekBasedYear(year)) {
        throw DateTimeException("Week 53 doesn't exist in $year")
    }

    return week
}
