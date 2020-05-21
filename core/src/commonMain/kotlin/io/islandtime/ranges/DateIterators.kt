package io.islandtime.ranges

import io.islandtime.Date
import io.islandtime.measures.IntDays
import io.islandtime.measures.IntMonths

internal class DateDayProgressionIterator(
    first: Date,
    last: Date,
    private val step: IntDays
) : Iterator<Date> {

    private val finalElement = last
    private var hasNext = if (step.value > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun next(): Date {
        val value = next

        if (value == finalElement) {
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

internal class DateMonthProgressionIterator(
    first: Date,
    last: Date,
    private val step: IntMonths
) : Iterator<Date> {

    private val finalElement = last
    private var hasNext = if (step.value > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun next(): Date {
        val value = next

        if (value == finalElement) {
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