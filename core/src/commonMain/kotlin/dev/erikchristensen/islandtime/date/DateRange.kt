package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.interval.Period
import dev.erikchristensen.islandtime.interval.days
import dev.erikchristensen.islandtime.interval.months
import dev.erikchristensen.islandtime.interval.years
import kotlin.random.Random
import kotlin.random.nextLong

/**
 * An inclusive range of dates
 */
class DateRange(
    start: Date,
    endInclusive: Date
) : DateDayProgression(start, endInclusive, 1.days),
    ClosedRange<Date> {

    override val start: Date get() = first
    override val endInclusive: Date get() = last

    override fun toString() = "$first..$last"

    companion object {
        /**
         * A range containing zero days
         */
        val EMPTY = DateRange(Date.ofUnixEpochDays(1L.days), Date.ofUnixEpochDays(0L.days))
    }
}

/**
 * Return a random date within the range using the default random number generator
 */
fun DateRange.random(): Date = random(Random)

/**
 * Return a random date within the range using the supplied random number generator
 */
fun DateRange.random(random: Random): Date {
    try {
        val longRange = first.unixEpochDays.value..last.unixEpochDays.value
        return Date.ofUnixEpochDays(random.nextLong(longRange).days)
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

/**
 * Get a range containing all of the days up to, but not including [to]
 */
infix fun Date.until(to: Date) = DateRange(this, to - 1L.days)

/**
 * Convert a range of dates into a period containing each day in the range. As a range is inclusive, if the start and
 * end date are the same, the resulting period will contain one day.
 */
fun DateRange.asPeriod(): Period {
    return if (start > endInclusive) {
        Period.ZERO
    } else {
        periodBetween(start, endInclusive + 1.days)
    }
}

/**
 * Get the number of days in a range of dates. As a range is inclusive, if the start and end date are the same, the
 * result be one day.
 */
val DateRange.days
    get() = if (start > endInclusive) 0L.days else daysBetween(start, endInclusive + 1.days)

/**
 * Get the number of months in a range of dates. A month is considered to have passed if the day of the end month is
 * greater than or equal to the day of the start month minus one (as a range is inclusive).
 */
val DateRange.months
    get() = if (start > endInclusive) 0.months else monthsBetween(start, endInclusive + 1.days)

/**
 * Get the number of years in a range of dates. A year is considered to have passed if twelve full months have passed
 * between the start date and end date, according to the definition of 'month' in [DateRange.months].
 */
val DateRange.years
    get() = if (start > endInclusive) 0.years else yearsBetween(start, endInclusive + 1.days)