package io.islandtime.operators.internal

import io.islandtime.DateTime
import io.islandtime.Time
import io.islandtime.internal.*
import io.islandtime.measures.IntSeconds
import io.islandtime.measures.LongNanoseconds
import io.islandtime.measures.days
import io.islandtime.startOfDay

private const val INCREMENT_OUT_OF_RANGE_MESSAGE = "The increment must be greater than zero and no more than 24 hours"
private const val INCREMENT_NOT_EVEN_MESSAGE = "The increment must multiply evenly into a 24-hour day"

internal fun checkRoundingIncrement(increment: IntSeconds) {
    require(increment.value in 1..SECONDS_PER_DAY) { INCREMENT_OUT_OF_RANGE_MESSAGE }
    require(SECONDS_PER_DAY % increment.value == 0) { INCREMENT_NOT_EVEN_MESSAGE }
}

internal fun checkRoundingIncrement(increment: LongNanoseconds) {
    require(increment.value in 1..NANOSECONDS_PER_DAY) { INCREMENT_OUT_OF_RANGE_MESSAGE }
    require(NANOSECONDS_PER_DAY % increment.value == 0L) { INCREMENT_NOT_EVEN_MESSAGE }
}

internal fun Time.nextWholeHour(): Time = nextWholeHourOrNull() ?: Time.MIDNIGHT
internal fun Time.nextWholeMinute(): Time = nextWholeMinuteOrNull() ?: Time.MIDNIGHT
internal fun Time.nextWholeSecond(): Time = nextWholeSecondOrNull() ?: Time.MIDNIGHT

internal fun Time.previousWholeHour(): Time {
    return if (minute > 0 || second > 0 || nanosecond > 0) {
        copy(minute = 0, second = 0, nanosecond = 0)
    } else {
        this
    }
}

internal fun Time.previousWholeMinute(): Time {
    return if (second > 0 || nanosecond > 0) {
        copy(second = 0, nanosecond = 0)
    } else {
        this
    }
}

internal fun Time.previousWholeSecond(): Time {
    return if (nanosecond > 0) {
        copy(nanosecond = 0)
    } else {
        this
    }
}

internal fun Time.previousWholeMillisecond(): Time {
    return copyIfChanged(nanosecond = nanosecond / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND)
}

internal fun Time.previousWholeMicrosecond(): Time {
    return copyIfChanged(nanosecond = nanosecond / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND)
}

internal fun DateTime.nextWholeHour(): DateTime = nextTimeBy(Time::nextWholeHourOrNull)
internal fun DateTime.nextWholeMinute(): DateTime = nextTimeBy(Time::nextWholeMinuteOrNull)
internal fun DateTime.nextWholeSecond(): DateTime = nextTimeBy(Time::nextWholeSecondOrNull)

internal fun DateTime.previousWholeHour(): DateTime {
    return if (minute > 0 || second > 0 || nanosecond > 0) {
        copy(minute = 0, second = 0, nanosecond = 0)
    } else {
        this
    }
}

internal fun DateTime.previousWholeMinute(): DateTime {
    return if (second > 0 || nanosecond > 0) {
        copy(second = 0, nanosecond = 0)
    } else {
        this
    }
}

internal fun DateTime.previousWholeSecond(): DateTime {
    return if (nanosecond > 0) {
        copy(nanosecond = 0)
    } else {
        this
    }
}

internal fun DateTime.previousWholeMillisecond(): DateTime {
    return copyIfChanged(nanosecond = nanosecond / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND)
}

internal fun DateTime.previousWholeMicrosecond(): DateTime {
    return copyIfChanged(nanosecond = nanosecond / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND)
}

private fun Time.nextWholeHourOrNull(): Time? {
    val newHour = hour + 1

    return if (newHour == HOURS_PER_DAY) {
        null
    } else {
        Time(hour = newHour, minute = 0)
    }
}

private fun Time.nextWholeMinuteOrNull(): Time? {
    var newMinute = minute + 1
    var newHour = hour

    if (newMinute == MINUTES_PER_HOUR) {
        newMinute = 0
        newHour = hour + 1

        if (newHour == HOURS_PER_DAY) {
            return null
        }
    }

    return Time(newHour, newMinute)
}

private fun Time.nextWholeSecondOrNull(): Time? {
    var newSecond = second + 1
    var newMinute = minute
    var newHour = hour

    if (newSecond == SECONDS_PER_MINUTE) {
        newSecond = 0
        newMinute = minute + 1

        if (newMinute == MINUTES_PER_HOUR) {
            newMinute = 0
            newHour = hour + 1

            if (newHour == HOURS_PER_DAY) {
                return null
            }
        }
    }

    return Time(newHour, newMinute, newSecond)
}

private inline fun DateTime.nextTimeBy(next: Time.() -> Time?): DateTime {
    return time.next()?.let { copy(time = it) } ?: (date + 1.days).startOfDay
}