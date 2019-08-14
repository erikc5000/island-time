package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.interval.*

abstract class DateIterator : Iterator<Date> {
    override fun next() = nextDate()

    abstract fun nextDate(): Date
}

internal class DateDayProgressionIterator(
    first: LongDaySpan,
    last: LongDaySpan,
    private val step: DaySpan
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

        return Date.ofUnixEpochDays(value)
    }
}

internal class DateMonthProgressionIterator(
    first: Date,
    last: Date,
    private val step: MonthSpan
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