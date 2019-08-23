package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.lastDayIn
import kotlin.math.abs

open class DateDayProgression protected constructor(
    first: Date,
    endInclusive: Date,
    val step: IntDays
) : Iterable<Date> {

    init {
        require(step.value != 0) { "Step must be non-zero" }
        require(step.value != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation"
        }
    }

    private val firstUnixEpochDay: LongDays = first.asUnixEpochDays()
    private val lastUnixEpochDay: LongDays = getLastDayInProgression(firstUnixEpochDay, endInclusive, step)
    val first: Date get() = Date.ofUnixEpochDays(firstUnixEpochDay)
    val last: Date get() = Date.ofUnixEpochDays(lastUnixEpochDay)

    override fun iterator(): DateIterator = DateDayProgressionIterator(firstUnixEpochDay, lastUnixEpochDay, step)

    override fun toString() = if (step.value > 0L) "$first..$last step $step" else "$first downTo $ last step ${-step}"

    companion object {
        fun fromClosedRange(rangeStart: Date, rangeEnd: Date, step: IntDays): DateDayProgression {
            return DateDayProgression(rangeStart, rangeEnd, step)
        }
    }
}

class DateMonthProgression private constructor(
    val first: Date,
    endInclusive: Date,
    val step: IntMonths
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
        fun fromClosedRange(rangeStart: Date, rangeEnd: Date, step: IntMonths): DateMonthProgression {
            return DateMonthProgression(rangeStart, rangeEnd, step)
        }
    }
}

fun DateDayProgression.reversed() = DateDayProgression.fromClosedRange(last, first, -step)

/**
 * Step over dates in increments of days
 */
infix fun DateDayProgression.step(step: IntDays): DateDayProgression {
    require(step.value > 0) { "step must be positive" }

    return DateDayProgression.fromClosedRange(first, last, if (this.step.value > 0) step else -step)
}

/**
 * Step over dates in increments of months
 */
infix fun DateDayProgression.step(step: IntMonths): DateMonthProgression {
    require(step > 0.months) { "step must be positive" }

    return DateMonthProgression.fromClosedRange(first, last, if (this.step.value > 0) step else -step)
}

infix fun DateDayProgression.step(step: IntYears) = this.step(step.asMonths())

fun DateMonthProgression.reversed() = DateMonthProgression.fromClosedRange(last, first, -step)

/**
 * Assumes step is non-zero
 */
private fun getLastDayInProgression(startInDays: LongDays, endDate: Date, step: IntDays): LongDays {
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

private fun getLastDateInProgression(start: Date, end: Date, step: IntMonths): Date {
    return when {
        step.value > 0 -> if (start >= end) {
            end
        } else {
            val monthsBetween = progressionMonthsBetween(start, end)
            val steppedMonths = monthsBetween - (monthsBetween % step.value)
            start + steppedMonths
        }
        else -> if (start <= end) {
            end
        } else {
            val monthsBetween = progressionMonthsBetween(end, start)
            val steppedMonths = monthsBetween - (monthsBetween % step.value)
            start - steppedMonths
        }
    }
}

/**
 * Get the number of months between two dates for the purposes of a progression.  This works a little differently than
 * the usual [monthsBetween] since it tries to use the same day as the start date while stepping months, coercing that
 * day as needed to fit the number of days in the current month.
 */
private fun progressionMonthsBetween(start: Date, endInclusive: Date): IntMonths {
    val yearsBetween = endInclusive.year - start.year
    val monthsBetween = yearsBetween * MONTHS_IN_YEAR + (endInclusive.month.ordinal - start.month.ordinal)

    // Deal with variable month lengths
    val coercedStartDay = start.dayOfMonth.coerceAtMost(endInclusive.month.lastDayIn(endInclusive.year))
    val monthAdjustment = if (endInclusive.dayOfMonth < coercedStartDay) -1 else 0
    return (monthsBetween + monthAdjustment).months
}