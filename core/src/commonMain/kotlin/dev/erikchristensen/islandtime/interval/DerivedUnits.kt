package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.DAYS_IN_WEEK
import dev.erikchristensen.islandtime.internal.timesExact

val Int.weeks: IntDays get() = IntDays(this timesExact DAYS_IN_WEEK)
val Long.weeks: LongDays get() = LongDays(this timesExact DAYS_IN_WEEK)

val Int.decades: IntYears get() = IntYears(this timesExact 10)
val Long.decades: LongYears get() = LongYears(this timesExact 10)

val Int.centuries: IntYears get() = IntYears(this timesExact 100)
val Long.centuries: LongYears get() = LongYears(this timesExact 100)