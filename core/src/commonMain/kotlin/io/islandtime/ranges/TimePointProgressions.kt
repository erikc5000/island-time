package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.between
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
    val step: Seconds
) : TimePointProgressionBuilder<T>,
    Iterable<T> {

    init {
        require(step != 0L.seconds) { "Step must be non-zero" }
    }

    override val first: T = start
    override val last: T = getLastTimePointInProgression(start, endInclusive, step)

    fun isEmpty(): Boolean = if (step.value > 0) first > last else first < last

    override fun iterator(): Iterator<T> = TimePointSecondProgressionIterator(first, last, step)

    override fun toString(): String {
        return if (step.value > 0) {
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
            step: Seconds
        ): TimePointSecondProgression<T> = TimePointSecondProgression(rangeStart, rangeEnd, step)
    }
}

class TimePointNanosecondProgression<T : TimePoint<T>> private constructor(
    start: T,
    endInclusive: T,
    val step: Nanoseconds
) : TimePointProgressionBuilder<T>,
    Iterable<T> {

    init {
        require(step != 0L.nanoseconds) { "Step must be non-zero" }
    }

    override val first: T = start
    override val last: T = getLastTimePointInProgression(start, endInclusive, step)

    fun isEmpty(): Boolean = if (step.value > 0) first > last else first < last

    override fun iterator(): Iterator<T> = TimePointNanosecondProgressionIterator(first, last, step)

    override fun toString(): String {
        return if (step.value > 0) {
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
            step: Nanoseconds
        ): TimePointNanosecondProgression<T> = TimePointNanosecondProgression(rangeStart, rangeEnd, step)
    }
}

/**
 * Creates a progression of time points in descending order.
 */
infix fun <T : TimePoint<T>> T.downTo(to: T): TimePointProgressionBuilder<T> {
    return DefaultTimePointProgressionBuilder(this, to)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: Days): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val secondStep = (if (last > first) step else -step).inSeconds
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: Hours): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val secondStep = (if (last > first) step else -step).inSeconds
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: Minutes): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val secondStep = (if (last > first) step else -step).inSeconds
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(step: Seconds): TimePointSecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val secondStep = (if (last > first) step else -step)
    return TimePointSecondProgression.fromClosedRange(first, last, secondStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: Milliseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val nanoStep = (if (last > first) step else -step).inNanoseconds
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: Microseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val nanoStep = (if (last > first) step else -step).inNanoseconds
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

infix fun <T : TimePoint<T>> TimePointProgressionBuilder<T>.step(
    step: Nanoseconds
): TimePointNanosecondProgression<T> {
    require(step.value > 0) { "step must be positive" }
    val nanoStep = if (last > first) step else -step
    return TimePointNanosecondProgression.fromClosedRange(first, last, nanoStep)
}

/**
 * Assumes step is non-zero
 */
private fun <T : TimePoint<T>> getLastTimePointInProgression(start: T, end: T, step: Seconds): T {
    return if ((step.value > 0 && start >= end) || (step.value < 0 && start <= end)) {
        end
    } else {
        val secondsBetween = Seconds.between(start, end)
        val steppedSeconds = secondsBetween - (secondsBetween % step.value)
        start + steppedSeconds
    }
}

/**
 * Assumes step is non-zero
 */
private fun <T : TimePoint<T>> getLastTimePointInProgression(start: T, end: T, step: Nanoseconds): T {
    return if ((step.value > 0 && start >= end) || (step.value < 0 && start <= end)) {
        end
    } else {
        val nanosecondsBetween = Nanoseconds.between(start, end)
        val steppedNanoseconds = nanosecondsBetween - (nanosecondsBetween % step.value)
        start + steppedNanoseconds
    }
}
