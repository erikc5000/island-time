package io.islandtime.operators

import io.islandtime.*
import io.islandtime.measures.days

/**
 * The next date after this one that falls on a particular day of the week.
 */
fun Date.next(dayOfWeek: DayOfWeek): Date {
    val dayDiff = this.dayOfWeek.ordinal - dayOfWeek.ordinal

    return if (dayDiff >= 0) {
        this + (7 - dayDiff).days
    } else {
        this + (-dayDiff).days
    }
}

/**
 * The next date that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun Date.nextOrSame(dayOfWeek: DayOfWeek): Date {
    return if (dayOfWeek == this.dayOfWeek) {
        this
    } else {
        next(dayOfWeek)
    }
}

/**
 * The last date before this one that falls on a particular day of the week.
 */
fun Date.previous(dayOfWeek: DayOfWeek): Date {
    val dayDiff = dayOfWeek.ordinal - this.dayOfWeek.ordinal

    return if (dayDiff >= 0) {
        this - (7 - dayDiff).days
    } else {
        this - (-dayDiff).days
    }
}

/**
 * The last date that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun Date.previousOrSame(dayOfWeek: DayOfWeek): Date {
    return if (dayOfWeek == this.dayOfWeek) {
        this
    } else {
        previous(dayOfWeek)
    }
}

/**
 * The next date-time after this one that falls on a particular day of the week.
 */
fun DateTime.next(dayOfWeek: DayOfWeek) = copy(date = date.next(dayOfWeek))

/**
 * The next date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun DateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(date = date.nextOrSame(dayOfWeek))

/**
 * The last date-time before this one that falls on a particular day of the week.
 */
fun DateTime.previous(dayOfWeek: DayOfWeek) = copy(date = date.previous(dayOfWeek))

/**
 * The last date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun DateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(date = date.previousOrSame(dayOfWeek))

/**
 * The next date-time after this one that falls on a particular day of the week.
 */
fun OffsetDateTime.next(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.next(dayOfWeek))

/**
 * The next date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun OffsetDateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.nextOrSame(dayOfWeek))

/**
 * The last date-time before this one that falls on a particular day of the week.
 */
fun OffsetDateTime.previous(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previous(dayOfWeek))

/**
 * The last date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun OffsetDateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previousOrSame(dayOfWeek))

/**
 * The next date-time after this one that falls on a particular day of the week.
 */
fun ZonedDateTime.next(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.next(dayOfWeek))

/**
 * The next date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun ZonedDateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.nextOrSame(dayOfWeek))

/**
 * The last date-time before this one that falls on a particular day of the week.
 */
fun ZonedDateTime.previous(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previous(dayOfWeek))

/**
 * The last date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun ZonedDateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previousOrSame(dayOfWeek))
