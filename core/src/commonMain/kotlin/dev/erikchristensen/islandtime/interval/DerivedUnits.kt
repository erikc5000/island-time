package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.DAYS_IN_WEEK

val Int.weeks: IntDays get() = IntDays(DAYS_IN_WEEK * this)
val Long.weeks: LongDays get() = LongDays(DAYS_IN_WEEK * this)

val Int.decades: IntYears get() = IntYears(this * 10)
val Long.decades: LongYears get() = LongYears(this * 10)

val Int.centuries: IntYears get() = IntYears(this * 100)
val Long.centuries: LongYears get() = LongYears(this * 100)