package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.interval.*
import kotlin.math.abs

open class DateDayProgression protected constructor(
    first: Date,
    endInclusive: Date,
    val step: DaySpan
) : Iterable<Date> {

    init {
        require(step.value != 0) { "Step must be non-zero" }
        require(step.value != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation"
        }
    }

    private val firstUnixEpochDay: LongDaySpan = first.asUnixEpochDays()
    private val lastUnixEpochDay: LongDaySpan = getLastDayInProgression(firstUnixEpochDay, endInclusive, step)
    val first: Date get() = Date.ofUnixEpochDays(firstUnixEpochDay)
    val last: Date get() = Date.ofUnixEpochDays(lastUnixEpochDay)

    override fun iterator(): DateIterator = DateDayProgressionIterator(firstUnixEpochDay, lastUnixEpochDay, step)

    override fun toString() = if (step.value > 0L) "$first..$last step $step" else "$first downTo $ last step ${-step}"

    companion object {
        fun fromClosedRange(rangeStart: Date, rangeEnd: Date, step: DaySpan): DateDayProgression {
            return DateDayProgression(rangeStart, rangeEnd, step)
        }
    }
}

class DateMonthProgression private constructor(
    val first: Date,
    endInclusive: Date,
    val step: MonthSpan
) : Iterable<Date> {

    init {
        require(step.value != 0) { "Step must be non-zero" }
        require(step.value != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation"
        }
    }

    val last = getLastDateInProgression(first, endInclusive, step)

    override fun iterator(): DateIterator = DateMonthProgressionIterator(first, last, step)

    override fun toString() = if (step > 0.months) "$first..$last step $step" else "$first downTo $ last step ${-step}"

    companion object {
        fun fromClosedRange(rangeStart: Date, rangeEnd: Date, step: MonthSpan): DateMonthProgression {
            return DateMonthProgression(rangeStart, rangeEnd, step)
        }
    }
}

fun DateDayProgression.reversed() = DateDayProgression.fromClosedRange(last, first, -step)

/**
 * Step over dates in increments of days
 */
infix fun DateDayProgression.step(step: DaySpan): DateDayProgression {
    if (step.value <= 0) {
        throw IllegalArgumentException("step must be positive")
    }

    return DateDayProgression.fromClosedRange(first, last, if (this.step.value > 0) step else -step)
}

/**
 * Step over dates in increments of months
 */
infix fun DateDayProgression.step(step: MonthSpan): DateMonthProgression {
    if (step <= 0.months) {
        throw IllegalArgumentException("step must be positive")
    }

    return DateMonthProgression.fromClosedRange(first, last, if (this.step.value > 0) step else -step)
}

infix fun DateDayProgression.step(step: YearSpan) = this.step(step.asMonths())

fun DateMonthProgression.reversed() = DateMonthProgression.fromClosedRange(last, first, -step)

/**
 * Assumes step is non-zero
 */
private fun getLastDayInProgression(startInDays: LongDaySpan, endDate: Date, step: DaySpan): LongDaySpan {
    val endInDays = endDate.asUnixEpochDays()

    return when {
        step.value > 0L -> if (startInDays >= endInDays) {
            endInDays
        } else {
            endInDays - (abs(endInDays.value - startInDays.value) % step.value).days
        }
        else -> if (startInDays <= endInDays) {
            endInDays
        } else {
            endInDays - (abs(startInDays.value - endInDays.value) % step.value).days
        }
    }
}

private fun getLastDateInProgression(start: Date, end: Date, step: MonthSpan): Date {
    return when {
        step.value > 0 -> if (start >= end) {
            end
        } else {
            val monthsBetween = monthsBetween(start, end)
            val steppedMonths = monthsBetween - (monthsBetween % step.value)
            start + steppedMonths
        }
        else -> if (start <= end) {
            end
        } else {
            val monthsBetween = monthsBetween(end, start)
            val steppedMonths = monthsBetween - (monthsBetween % step.value)
            start - steppedMonths
        }
    }
}