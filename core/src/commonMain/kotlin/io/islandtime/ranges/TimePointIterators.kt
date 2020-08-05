package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.measures.*

abstract class TimePointIterator<T : TimePoint<T>> : Iterator<T> {
    override fun next() = nextTimePoint()

    abstract fun nextTimePoint(): T
}

internal class TimePointSecondProgressionIterator<T : TimePoint<T>>(
    first: T,
    last: T,
    private val step: LongSeconds
) : TimePointIterator<T>() {

    private val finalElement = last
    private var hasNext = if (step.isPositive()) last > first else last < first
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun nextTimePoint(): T {
        val value = next

        if (value.isSameInstantAs(finalElement)) {
            if (!hasNext) {
                throw NoSuchElementException()
            }

            hasNext = false
        } else {
            next += step
        }

        return value
    }
}

internal class TimePointNanosecondProgressionIterator<T : TimePoint<T>>(
    first: T,
    last: T,
    private val step: LongNanoseconds
) : TimePointIterator<T>() {

    private val finalElement = last
    private var hasNext = if (step.isPositive()) last > first else last < first
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun nextTimePoint(): T {
        val value = next

        if (value.isSameInstantAs(finalElement)) {
            if (!hasNext) {
                throw NoSuchElementException()
            }

            hasNext = false
        } else {
            next += step
        }

        return value
    }
}
