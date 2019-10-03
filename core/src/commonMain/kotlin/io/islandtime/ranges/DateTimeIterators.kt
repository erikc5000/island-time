package io.islandtime.ranges

import io.islandtime.DateTime
import io.islandtime.measures.IntDays
import io.islandtime.measures.IntMonths

abstract class DateTimeIterator : Iterator<DateTime> {
    override fun next() = nextDateTime()

    abstract fun nextDateTime(): DateTime
}

internal class DateTimeDayProgressionIterator(
    first: DateTime,
    last: DateTime,
    private val step: IntDays
) : DateTimeIterator() {

    private val finalElement = last
    private var hasNext = if (step.value > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun nextDateTime(): DateTime {
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


internal class DateTimeMonthProgressionIterator(
    first: DateTime,
    last: DateTime,
    private val step: IntMonths
) : DateTimeIterator() {

    private val finalElement = last
    private var hasNext = if (step.value > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun nextDateTime(): DateTime {
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