package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.measures.*

/**
 * A time point progression builder.
 *
 * Without a step, a time-based progression can't be created. This interface represents an intermediate state in the
 * process of creating a time point progression.
 */
interface TimePointProgressionBuilder<T : TimePoint<T>> {
    val first: T
    val last: T
}

/**
 * A progression builder that stores just the first and last elements.
 */
private class DefaultTimePointProgressionBuilder<T : TimePoint<T>>(
    override val first: T,
    override val last: T
) : TimePointProgressionBuilder<T>

class TimePointSecondProgression<T : TimePoint<T>> private constructor(
    start: T,
    endInclusive: T,
    val step: LongSeconds
) : TimePointProgressionBuilder<T>,
    Iterable<T> {

    init {
        require(!step.isZero()) { "Step must be non-zero" }
    }

    override val first: T = start
    override val last: T = getLastTimePointInProgression(start, endInclusive, step)

    fun isEmpty(): Boolean = if (step.isPositive()) first > last else first < last

    override fun iterator(): Iterator<T> = TimePointSecondProgressionIterator(first, last, step)

    override fun toString(): String {
        return if (step.isPositive()) {
            "$first..$last step $step"
        } else {
            "$first downTo $last step ${-step}"
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is TimePointSecondProgression<*> &&
            (isEmpty() && other.isEmpty() ||
                first == other.first &&
                last == other.last &&
                step == other.step)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) {
            -1
        } else {
            31 * (31 * first.hashCode() + last.hashCode()) + step.hashCode()
        }
    }

    /**
     * Reverses this progression such that it counts down instead of up, or vice versa.
     */
    fun reversed(): TimePointSecondProgression<T> {
        return fromClosedRange(last, first, -step)
    }

    companion object {
        fun <T : TimePoint<T>> fromClosedRange(
            rangeStart: T,
            rangeEnd: T,
            step: LongSeconds
        ) = TimePointSecondProgression(rangeStart, rangeEnd, step)
    }
}

class TimePointNanosecondProgression<T : TimePoint<T>> private constructor(
    start: T,
    endInclusive: T,
    val step: LongNanoseconds
) : TimePointProgressionBuilder<T>,
    Iterable<T> {

    init {
        require(!step.isZero()) { "Step must be non-zero" }
    }

    override val first: T = start
    override val last: T = getLastTimePointInProgression(start, endInclusive, step)

    fun isEmpty(): Boolean = if (step.isPositive()) first > last else first < last

    override fun iterator(): Iterator<T> = TimePointNanosecondProgressionIterator(first, last, step)

    override fun toString(): String {
        return if (step.isPositive()) {
            "$first..$last step $step"
        } else {
            "$first downTo $last step ${-step}"
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is TimePointNanosecondProgression<*> &&
            (isEmpty() && other.isEmpty() ||
                first == other.first &&
                last == other.last &&
                step == other.step)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) {
            -1
        } else {
            31 * (31 * first.hashCode() + last.hashCode()) + step.hashCode()
        }
    }

    /**
     * Reverses this progression such that it counts down instead of up, or vice versa.
     */
    fun reversed(): TimePointNanosecondProgression<T> {
        return fromClosedRange(last, first, -step)
    }

    companion object {
        fun <T : TimePoint<T>> fromClosedRange(
            rangeStart: T,
            rangeEnd: T,
            step: LongNanoseconds
        ) = TimePointNanosecondProgression(rangeStart, rangeEnd, step)
    }
}

/**
 * Creates a progression of time points in descending order.
 */
infix fun <T : TimePoint<T>> T.downTo(to: T): TimePointProgressionBuilder<T> {
    return DefaultTimePointProgressionBuilder(this, to)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: IntDays): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val longStep = step.toLongDays()
    val secondStep = (if (last > first) longStep else longStep.negateUnchecked()).inSecondsUnchecked
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: IntHours): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val longStep = step.toLongHours()
    val secondStep = (if (last > first) longStep else longStep.negateUnchecked()).inSecondsUnchecked
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: IntMinutes): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val longStep = step.toLongMinutes()
    val secondStep = (if (last > first) longStep else longStep.negateUnchecked()).inSecondsUnchecked
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: IntSeconds): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val longStep = step.toLongSeconds()
    val secondStep = (if (last > first) longStep else longStep.negateUnchecked())
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: IntMilliseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val longStep = step.toLongMilliseconds()
    val nanoStep = (if (last > first) longStep else longStep.negateUnchecked()).inNanosecondsUnchecked
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: LongMilliseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val nanoStep = (if (last > first) step else -step).inNanoseconds
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: IntMicroseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val longStep = step.toLongMicroseconds()
    val nanoStep = (if (last > first) longStep else longStep.negateUnchecked()).inNanosecondsUnchecked
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: LongMicroseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val nanoStep = (if (last > first) step else -step).inNanoseconds
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: IntNanoseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val longStep = step.toLongNanoseconds()
    val nanoStep = if (last > first) longStep else longStep.negateUnchecked()
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: LongNanoseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val nanoStep = if (last > first) step else -step
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

/**
 * Assumes step is non-zero
 */
private fun <T : TimePoint<T>> getLastTimePointInProgression(start: T, end: T, step: LongSeconds): T {
    return if ((step.isPositive() && start >= end) || (step.isNegative() && start <= end)) {
        end
    } else {
        val secondsBetween = secondsBetween(start, end)
        val steppedSeconds = secondsBetween - (secondsBetween % step.value)
        start + steppedSeconds
    }
}

/**
 * Assumes step is non-zero
 */
private fun <T : TimePoint<T>> getLastTimePointInProgression(start: T, end: T, step: LongNanoseconds): T {
    return if ((step.isPositive() && start >= end) || (step.isNegative() && start <= end)) {
        end
    } else {
        val nanosecondsBetween = nanosecondsBetween(start, end)
        val steppedNanoseconds = nanosecondsBetween - (nanosecondsBetween % step.value)
        start + steppedNanoseconds
    }
}
