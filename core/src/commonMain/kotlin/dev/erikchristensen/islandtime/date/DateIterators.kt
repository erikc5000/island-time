package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.interval.*

abstract class DateIterator : Iterator<Date> {
    override fun next() = nextDate()

    abstract fun nextDate(): Date
}

internal class DateDayProgressionIterator(
    first: LongDays,
    last: LongDays,
    private val step: IntDays
) : DateIterator() {

    private val finalElement = last
    private var hasNext = if (step.value > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun nextDate(): Date {
        val value = next

        if (value == finalElement) {
            if (!hasNext) {
                throw NoSuchElementException()
            }

            hasNext = false
        } else {
            next += step.toLong()
        }

        return Date.fromDaysSinceUnixEpoch(value)
    }
}

internal class DateMonthProgressionIterator(
    first: Date,
    last: Date,
    private val step: IntMonths
) : DateIterator() {

    private val finalElement = last
    private var hasNext = if (step.value > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement

    override fun hasNext() = hasNext

    override fun nextDate(): Date {
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