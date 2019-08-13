package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.interval.*
import kotlin.jvm.JvmField
import kotlin.random.Random
import kotlin.random.nextLong

class DateRange(
    start: Date,
    endInclusive: Date
) : DateDayProgression(start, endInclusive, 1.days),
    ClosedRange<Date> {

    override val start: Date get() = first
    override val endInclusive: Date get() = last

    override fun toString() = "$first..$last"

    companion object {
        @JvmField
        val EMPTY = DateRange(Date.ofUnixEpochDays(1L.days), Date.ofUnixEpochDays(0L.days))
    }
}

fun DateRange.random(): Date = random(Random)

fun DateRange.random(random: Random): Date {
    try {
        val longRange = first.asUnixEpochDays().value..last.asUnixEpochDays().value
        return Date.ofUnixEpochDays(random.nextLong(longRange).days)
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

infix fun Date.until(to: Date) = this..to - 1L.days
infix fun Date.downTo(to: Date) = DateDayProgression.fromClosedRange(this, to, (-1).days)

fun DateRange.asPeriod() = if (start > endInclusive) Period.ZERO else periodBetween(start, endInclusive)
val DateRange.days get() = if (start > endInclusive) 0.days else daysBetween(start, endInclusive)
val DateRange.months get() = if (start > endInclusive) 0.months else monthsBetween(start, endInclusive)
val DateRange.years get() = if (start > endInclusive) 0.years else yearsBetween(start, endInclusive)