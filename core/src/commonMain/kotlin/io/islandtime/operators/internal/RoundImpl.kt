package io.islandtime.operators.internal

import io.islandtime.DateTime
import io.islandtime.Time
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.startOfDay

internal fun checkRoundingIncrement(increment: IntHours) {
    require(increment.value in 1..HOURS_PER_DAY) {
        "The increment must be greater than zero and no more than 24 hours"
    }

    require(HOURS_PER_DAY % increment.value == 0) {
        "The increment must multiply evenly into a 24-hour day"
    }
}

internal fun checkRoundingIncrement(increment: IntMinutes) {
    require(increment.value in 1..MINUTES_PER_HOUR) {
        "The increment must be greater than zero and no more than an hour"
    }

    require(MINUTES_PER_HOUR % increment.value == 0) {
        "The increment must multiply evenly into an hour"
    }
}

internal fun checkRoundingIncrement(increment: IntSeconds) {
    require(increment.value in 1..SECONDS_PER_MINUTE) {
        "The increment must be greater than zero and no more than a minute"
    }

    require(SECONDS_PER_MINUTE % increment.value == 0) {
        "The increment must multiply evenly into a minute"
    }
}

internal fun checkRoundingIncrement(increment: IntMilliseconds) {
    checkFractionalSecondRoundingIncrement(increment.value, MILLISECONDS_PER_SECOND)
}

internal fun checkRoundingIncrement(increment: IntMicroseconds) {
    checkFractionalSecondRoundingIncrement(increment.value, MICROSECONDS_PER_SECOND)
}

internal fun checkRoundingIncrement(increment: IntNanoseconds) {
    checkFractionalSecondRoundingIncrement(increment.value, NANOSECONDS_PER_SECOND)
}

internal fun checkRoundingIncrement(increment: LongNanoseconds) {
    require(increment.value in 1..NANOSECONDS_PER_SECOND) {
        "The increment must be greater than zero and no more than a second"
    }

    require(NANOSECONDS_PER_SECOND % increment.value == 0L) {
        "The increment must multiply evenly into a second"
    }
}

private fun checkFractionalSecondRoundingIncrement(value: Int, maxValue: Int) {
    require(value in 1..maxValue) { "The increment must be greater than zero and no more than a second" }
    require(maxValue % value == 0) { "The increment must multiply evenly into a second" }
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