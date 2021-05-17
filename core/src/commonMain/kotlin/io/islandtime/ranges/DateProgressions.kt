package io.islandtime.ranges

import io.islandtime.Date
import io.islandtime.internal.MONTHS_PER_YEAR
import io.islandtime.measures.*
import kotlin.math.abs

abstract class DateDayProgression internal constructor() : Iterable<Date> {
    abstract val first: Date
    abstract val last: Date
    abstract val step: Days

    /**
     * Checks if this progression is empty.
     */
    abstract fun isEmpty(): Boolean

    override fun iterator(): Iterator<Date> = DateDayProgressionIterator(first, last, step)

    companion object {
        fun fromClosedRange(rangeStart: Date, rangeEnd: Date, step: Days): DateDayProgression {
            return DefaultDateDayProgression(rangeStart, rangeEnd, step)
        }
    }
}

private class DefaultDateDayProgression(
    start: Date,
    endInclusive: Date,
    override val step: Days
) : DateDayProgression() {

    init {
        require(step.value != 0L) { "Step must be non-zero" }
        require(step.value != Long.MIN_VALUE) {
            "Step must be greater than Long.MIN_VALUE to avoid overflow on negation"
        }
    }

    override val first: Date = start
    override val last: Date = getLastDateInProgression(start, endInclusive, step)

    override fun isEmpty(): Boolean = if (step.value > 0) first > last else first < last

    override fun toString() = if (step.value > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"

    override fun equals(other: Any?): Boolean {
        return other is DateDayProgression &&
            ((isEmpty() && other.isEmpty()) || (first == other.first && last == other.last && step == other.step))
    }

    override fun hashCode(): Int {
        return if (isEmpty()) {
            -1
        } else {
            31 * (31 * first.hashCode() + last.hashCode()) + step.hashCode()
        }
    }
}

class DateMonthProgression private constructor(
    start: Date,
    endInclusive: Date,
    val step: Months
) : Iterable<Date> {

    init {
        require(step.value != 0L) { "Step must be non-zero" }
    }

    val first = start
    val last = getLastDateInProgression(start, endInclusive, step)

    /**
     * Checks if this progression is empty.
     */
    fun isEmpty(): Boolean = if (step.value > 0) first > last else first < last

    override fun iterator(): Iterator<Date> = DateMonthProgressionIterator(first, last, step)

    override fun toString() = if (step.value > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"

    override fun equals(other: Any?): Boolean {
        return other is DateMonthProgression &&
            ((isEmpty() && other.isEmpty()) || (first == other.first && last == other.last && step == other.step))
    }

    override fun hashCode(): Int {
        return if (isEmpty()) {
            -1
        } else {
            31 * (31 * first.hashCode() + last.hashCode()) + step.hashCode()
        }
    }

    companion object {
        fun fromClosedRange(rangeStart: Date, rangeEnd: Date, step: Months): DateMonthProgression {
            return DateMonthProgression(rangeStart, rangeEnd, step)
        }
    }
}

/**
 * Creates a progression of dates in descending order.
 */
infix fun Date.downTo(to: Date) = DateDayProgression.fromClosedRange(this, to, (-1).days)

/**
 * Reverses this progression such that it counts down instead of up, or vice versa.
 */
fun DateDayProgression.reversed() = DateDayProgression.fromClosedRange(last, first, -step)

/**
 * Creates a progression that steps over the dates in this progression in increments of days.
 */
infix fun DateDayProgression.step(step: Days): DateDayProgression {
    require(step.value > 0) { "step must be positive" }
    return DateDayProgression.fromClosedRange(first, last, if (this.step.value > 0) step else -step)
}

/**
 * Creates a progression that steps over the dates in this progression in increments of weeks.
 */
infix fun DateDayProgression.step(step: Weeks) = this.step(step.inDays)

/**
 * Creates a progression that steps over the dates in this progression in increments of months.
 */
infix fun DateDayProgression.step(step: Months): DateMonthProgression {
    require(step.value > 0) { "step must be positive" }
    return DateMonthProgression.fromClosedRange(first, last, if (this.step.value > 0) step else -step)
}

/**
 * Creates a progression that steps over the dates in this progression in increments of years.
 */
infix fun DateDayProgression.step(step: Years) = this.step(step.inMonths)

/**
 * Creates a progression that steps over the dates in this progression in increments of decades.
 */
infix fun DateDayProgression.step(step: Decades) = this.step(step.inMonths)

/**
 * Creates a progression that steps over the dates in this progression in increments of centuries.
 */
infix fun DateDayProgression.step(step: Centuries) = this.step(step.inMonths)

/**
 * Reverses this progression such that it counts down instead of up, or vice versa.
 */
fun DateMonthProgression.reversed() = DateMonthProgression.fromClosedRange(last, first, -step)

/**
 * Assumes step is non-zero
 */
private fun getLastDateInProgression(start: Date, end: Date, step: Days): Date {
    return when {
        step.value > 0L -> if (start >= end) {
            end
        } else {
            val endEpochDay = end.dayOfUnixEpoch
            Date.fromDayOfUnixEpoch(endEpochDay - (abs(endEpochDay - start.dayOfUnixEpoch) % step.value))
        }
        else -> if (start <= end) {
            end
        } else {
            val endEpochDay = end.dayOfUnixEpoch
            Date.fromDayOfUnixEpoch(endEpochDay - (abs(start.dayOfUnixEpoch - end.dayOfUnixEpoch) % step.value))
        }
    }
}

private fun getLastDateInProgression(start: Date, end: Date, step: Months): Date {
    return if ((step.value > 0 && start >= end) || (step.value < 0 && start <= end)) {
        end
    } else {
        val monthsBetween = progressionMonthsBetween(start, end)
        val steppedMonths = monthsBetween - (monthsBetween % step.value)
        start + steppedMonths
    }
}

/**
 * Gets the number of months between two dates for the purposes of a progression. This works a little differently than
 * the usual [monthsBetween] since it tries to use the same day as the start date while stepping months, coercing that
 * day as needed to fit the number of days in the current month.
 */
private fun progressionMonthsBetween(start: Date, endInclusive: Date): Months {
    val yearsBetween = endInclusive.year - start.year
    val monthsBetween = yearsBetween * MONTHS_PER_YEAR + (endInclusive.month.ordinal - start.month.ordinal)

    // Deal with variable month lengths
    val coercedStartDay = start.dayOfMonth.coerceAtMost(endInclusive.month.lastDayIn(endInclusive.year))

    val monthAdjustment = when {
        start > endInclusive && endInclusive.dayOfMonth > coercedStartDay -> 1
        endInclusive > start && endInclusive.dayOfMonth < coercedStartDay -> -1
        else -> 0
    }

    return (monthsBetween + monthAdjustment).months
}
