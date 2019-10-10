package io.islandtime

import io.islandtime.measures.days

fun Date.startOfYear() = Year(year).startDate
fun Date.endOfYear() = Year(year).endDate
fun Date.startOfMonth() = copy(dayOfMonth = 1)
fun Date.endOfMonth() = copy(dayOfMonth = month.lastDayIn(year))
fun Date.startOfWeek() = previousOrSame(DayOfWeek.MIN)
fun Date.endOfWeek() = nextOrSame(DayOfWeek.MAX)

/**
 * Return the next date after this one that falls on a particular day of the week
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
 * Return the next date that falls on a particular day of the week or this one if it falls on the same day
 */
fun Date.nextOrSame(dayOfWeek: DayOfWeek): Date {
    return if (dayOfWeek == this.dayOfWeek) {
        this
    } else {
        next(dayOfWeek)
    }
}

/**
 * Return the last date before this one that falls on a particular day of the week
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
 * Return the last date that falls on a particular day of the week or this one if it falls on the same day
 */
fun Date.previousOrSame(dayOfWeek: DayOfWeek): Date {
    return if (dayOfWeek == this.dayOfWeek) {
        this
    } else {
        previous(dayOfWeek)
    }
}

fun DateTime.startOfYear() = copy(date = date.startOfYear(), time = Time.MIDNIGHT)
fun DateTime.endOfYear() = copy(date = date.endOfYear(), time = Time.MAX)
fun DateTime.startOfMonth() = copy(date = date.startOfMonth(), time = Time.MIDNIGHT)
fun DateTime.endOfMonth() = copy(date = date.endOfMonth(), time = Time.MAX)
fun DateTime.startOfWeek() = copy(date = date.previousOrSame(DayOfWeek.MIN), time = Time.MIDNIGHT)
fun DateTime.endOfWeek() = copy(date = date.nextOrSame(DayOfWeek.MAX), time = Time.MAX)

/**
 * Return the next date-time after this one that falls on a particular day of the week
 */
fun DateTime.next(dayOfWeek: DayOfWeek) = copy(date = date.next(dayOfWeek))

/**
 * Return the next date-time that falls on a particular day of the week or this one if it falls on the same day
 */
fun DateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(date = date.nextOrSame(dayOfWeek))

/**
 * Return the last date-time before this one that falls on a particular day of the week
 */
fun DateTime.previous(dayOfWeek: DayOfWeek) = copy(date = date.previous(dayOfWeek))

/**
 * Return the last date-time that falls on a particular day of the week or this one if it falls on the same day
 */
fun DateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(date = date.previousOrSame(dayOfWeek))

fun OffsetDateTime.startOfYear() = copy(dateTime = dateTime.startOfYear())
fun OffsetDateTime.endOfYear() = copy(dateTime = dateTime.endOfYear())
fun OffsetDateTime.startOfMonth() = copy(dateTime = dateTime.startOfMonth())
fun OffsetDateTime.endOfMonth() = copy(dateTime = dateTime.endOfMonth())
fun OffsetDateTime.startOfWeek() = copy(dateTime = dateTime.startOfWeek())
fun OffsetDateTime.endOfWeek() = copy(dateTime = dateTime.endOfWeek())

/**
 * Return the next date-time after this one that falls on a particular day of the week
 */
fun OffsetDateTime.next(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.next(dayOfWeek))

/**
 * Return the next date-time that falls on a particular day of the week or this one if it falls on the same day
 */
fun OffsetDateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.nextOrSame(dayOfWeek))

/**
 * Return the last date-time before this one that falls on a particular day of the week
 */
fun OffsetDateTime.previous(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previous(dayOfWeek))

/**
 * Return the last date-time that falls on a particular day of the week or this one if it falls on the same day
 */
fun OffsetDateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previousOrSame(dayOfWeek))

fun ZonedDateTime.startOfYear() = date.startOfYear().startOfDayAt(zone)
fun ZonedDateTime.endOfYear() = date.endOfYear().endOfDayAt(zone)
fun ZonedDateTime.startOfMonth() = date.startOfMonth().startOfDayAt(zone)
fun ZonedDateTime.endOfMonth() = date.endOfMonth().endOfDayAt(zone)
fun ZonedDateTime.startOfWeek() = date.previousOrSame(DayOfWeek.MIN).startOfDayAt(zone)
fun ZonedDateTime.endOfWeek() = date.nextOrSame(DayOfWeek.MAX).endOfDayAt(zone)

/**
 * Return the next date-time after this one that falls on a particular day of the week
 */
fun ZonedDateTime.next(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.next(dayOfWeek))

/**
 * Return the next date-time that falls on a particular day of the week or this one if it falls on the same day
 */
fun ZonedDateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.nextOrSame(dayOfWeek))

/**
 * Return the last date-time before this one that falls on a particular day of the week
 */
fun ZonedDateTime.previous(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previous(dayOfWeek))

/**
 * Return the last date-time that falls on a particular day of the week or this one if it falls on the same day
 */
fun ZonedDateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previousOrSame(dayOfWeek))