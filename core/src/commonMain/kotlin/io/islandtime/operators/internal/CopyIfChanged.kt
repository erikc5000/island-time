package io.islandtime.operators.internal

import io.islandtime.*

internal fun Time.copyIfChanged(nanosecond: Int): Time {
    return if (nanosecond == this.nanosecond) this else copy(nanosecond = nanosecond)
}

internal fun Date.copyIfChanged(dayOfMonth: Int): Date {
    return if (dayOfMonth == this.dayOfMonth) this else copy(dayOfMonth = dayOfMonth)
}

internal fun DateTime.copyIfChanged(nanosecond: Int): DateTime {
    return if (nanosecond == this.nanosecond) this else copy(nanosecond = nanosecond)
}

internal fun DateTime.copyIfChanged(time: Time): DateTime {
    return if (time === this.time) {
        this
    } else {
        copy(time = time)
    }
}

internal fun DateTime.copyIfChanged(date: Date, time: Time): DateTime {
    return if (date === this.date && time === this.time) {
        this
    } else {
        copy(date = date, time = time)
    }
}

internal fun OffsetTime.copyIfChanged(time: Time): OffsetTime {
    return if (time === this.time) this else copy(time = time)
}

internal fun OffsetDateTime.copyIfChanged(dateTime: DateTime): OffsetDateTime {
    return if (dateTime === this.dateTime) this else copy(dateTime = dateTime)
}

internal fun ZonedDateTime.copyIfChanged(dateTime: DateTime): ZonedDateTime {
    return if (dateTime === this.dateTime) this else copy(dateTime = dateTime)
}