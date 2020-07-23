@file:JvmMultifileClass
@file:JvmName("DateTimesKt")

package io.islandtime

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Converts this date to an ISO week date representation.
 */
inline fun <T> Date.toWeekDate(action: (year: Int, week: Int, day: Int) -> T): T {
    return action(weekBasedYear, weekOfWeekBasedYear, dayOfWeek.number)
}