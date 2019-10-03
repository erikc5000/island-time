package io.islandtime.ranges

import io.islandtime.DateTime
import io.islandtime.measures.IntDays
import io.islandtime.measures.IntMonths

class DateTimeDayProgression private constructor(
    start: DateTime,
    endInclusive: DateTime,
    val step: IntDays
) : Iterable<DateTime> {

    init {
        require(step.value != 0) { "Step must be non-zero" }
        require(step.value != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation"
        }
    }

    val first: DateTime = start
    val last: DateTime = getLastDateTimeInProgression(start, endInclusive, step)

    /** Is the progression empty? */
    fun isEmpty(): Boolean = if (step.value > 0) first > last else first < last

    override fun iterator(): DateTimeIterator = DateTimeDayProgressionIterator(first, last, step)

    override fun toString() = if (step.value > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"

    override fun equals(other: Any?): Boolean {
        return other is DateTimeDayProgression &&
            (isEmpty() && other.isEmpty() ||
                first == other.first &&
                last == other.last &&
                step == other.step)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) {
            -1
        } else {
            31 * (31 * first.hashCode() + last.hashCode()) + step.value
        }
    }

    companion object {
        fun fromClosedRange(rangeStart: DateTime, rangeEnd: DateTime, step: IntDays): DateTimeDayProgression {
            return DateTimeDayProgression(rangeStart, rangeEnd, step)
        }
    }
}

class DateTimeMonthProgression private constructor(
    val first: DateTime,
    endInclusive: DateTime,
    val step: IntMonths
) : Iterable<DateTime> {

    init {
        require(step.value != 0) { "Step must be non-zero" }
        require(step.value != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation"
        }
    }

    val last = getLastDateTimeInProgression(first, endInclusive, step)

    /** Is the progression empty? */
    fun isEmpty(): Boolean = if (step.value > 0) first > last else first < last

    override fun iterator(): DateTimeIterator = DateTimeMonthProgressionIterator(first, last, step)

    override fun toString() = if (step.value > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DateTimeMonthProgression

        if (first != other.first) return false
        if (step != other.step) return false
        if (last != other.last) return false

        return true
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else 31 * (31 * first.hashCode() + last.hashCode()) + step.value
    }

    companion object {
        fun fromClosedRange(rangeStart: DateTime, rangeEnd: DateTime, step: IntMonths): DateTimeMonthProgression {
            return DateTimeMonthProgression(rangeStart, rangeEnd, step)
        }
    }
}

/**
 * Assumes step is non-zero
 */
private fun getLastDateTimeInProgression(start: DateTime, end: DateTime, step: IntDays): DateTime {
    return if ((step.value > 0 && start >= end) || (step.value < 0 && start <= end)) {
        end
    } else {
        val daysBetween = daysBetween(start, end)
        val steppedDays = daysBetween - (daysBetween % step.value)
        start + steppedDays
    }
}

private fun getLastDateTimeInProgression(start: DateTime, end: DateTime, step: IntMonths): DateTime {
    return if ((step.value > 0 && start >= end) ||
        (step.value < 0 && start <= end)
    ) {
        end
    } else {
        val monthsBetween = progressionMonthsBetween(start.date, adjustedEndDate(start, end))
        val steppedMonths = monthsBetween - (monthsBetween % step.value)
        start + steppedMonths
    }
}