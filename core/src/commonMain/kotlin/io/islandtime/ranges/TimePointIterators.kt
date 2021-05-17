package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.measures.Nanoseconds
import io.islandtime.measures.Seconds

internal class TimePointSecondProgressionIterator<T : TimePoint<T>>(
    first: T,
    last: T,
    private val step: Seconds
) : Iterator<T> {

    private val finalElement = last
    private var hasNext = if (step.value > 0) last > first else last < first
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun next(): T {
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
    private val step: Nanoseconds
) : Iterator<T> {

    private val finalElement = last
    private var hasNext = if (step.value > 0) last > first else last < first
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun next(): T {
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
