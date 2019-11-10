package io.islandtime

import io.islandtime.measures.days

/**
 * Return the date at the start of the year that this date falls in.
 */
fun Date.startOfYear() = Year(year).startDate

/**
 * Return the date at the end of the year that this date falls in.
 */
fun Date.endOfYear() = Year(year).endDate

/**
 * Return the date at the start of the month that this date falls in.
 */
fun Date.startOfMonth() = copy(dayOfMonth = 1)

/**
 * Return the date at the end of the month that this date falls in.
 */
fun Date.endOfMonth() = copy(dayOfMonth = month.lastDayIn(year))

/**
 * Return the date at the start of the ISO week that this date falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun Date.startOfWeek() = previousOrSame(DayOfWeek.MIN)

/**
 * Return the date at the end of the ISO week that this date falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun Date.endOfWeek() = nextOrSame(DayOfWeek.MAX)

/**
 * Return the next date after this one that falls on a particular day of the week.
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
 * Return the next date that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun Date.nextOrSame(dayOfWeek: DayOfWeek): Date {
    return if (dayOfWeek == this.dayOfWeek) {
        this
    } else {
        next(dayOfWeek)
    }
}

/**
 * Return the last date before this one that falls on a particular day of the week.
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
 * Return the last date that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun Date.previousOrSame(dayOfWeek: DayOfWeek): Date {
    return if (dayOfWeek == this.dayOfWeek) {
        this
    } else {
        previous(dayOfWeek)
    }
}

/**
 * Return the date-time at the first instant of the year that this date-time falls in.
 */
fun DateTime.startOfYear() = copy(date = date.startOfYear(), time = Time.MIDNIGHT)

/**
 * Return the date-time at the last representable instant of the year that this date-time falls in.
 */
fun DateTime.endOfYear() = copy(date = date.endOfYear(), time = Time.MAX)

/**
 * Return the date-time at the first instant of the month that this date-time falls in.
 */
fun DateTime.startOfMonth() = copy(date = date.startOfMonth(), time = Time.MIDNIGHT)

/**
 * Return the date-time at the last representable instant of the month that this date-time falls in.
 */
fun DateTime.endOfMonth() = copy(date = date.endOfMonth(), time = Time.MAX)

/**
 * Return the date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun DateTime.startOfWeek() = copy(date = date.previousOrSame(DayOfWeek.MIN), time = Time.MIDNIGHT)

/**
 * Return the date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun DateTime.endOfWeek() = copy(date = date.nextOrSame(DayOfWeek.MAX), time = Time.MAX)

/**
 * Return the next date-time after this one that falls on a particular day of the week.
 */
fun DateTime.next(dayOfWeek: DayOfWeek) = copy(date = date.next(dayOfWeek))

/**
 * Return the next date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun DateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(date = date.nextOrSame(dayOfWeek))

/**
 * Return the last date-time before this one that falls on a particular day of the week.
 */
fun DateTime.previous(dayOfWeek: DayOfWeek) = copy(date = date.previous(dayOfWeek))

/**
 * Return the last date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun DateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(date = date.previousOrSame(dayOfWeek))

/**
 * Return the date-time at the first instant of the year that this date-time falls in.
 */
fun OffsetDateTime.startOfYear() = copy(dateTime = dateTime.startOfYear())

/**
 * Return the date-time at the last representable instant of the year that this date-time falls in.
 */
fun OffsetDateTime.endOfYear() = copy(dateTime = dateTime.endOfYear())

/**
 * Return the date-time at the first instant of the month that this date-time falls in.
 */
fun OffsetDateTime.startOfMonth() = copy(dateTime = dateTime.startOfMonth())

/**
 * Return the date-time at the last representable instant of the month that this date-time falls in.
 */
fun OffsetDateTime.endOfMonth() = copy(dateTime = dateTime.endOfMonth())

/**
 * Return the date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun OffsetDateTime.startOfWeek() = copy(dateTime = dateTime.startOfWeek())

/**
 * Return the date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun OffsetDateTime.endOfWeek() = copy(dateTime = dateTime.endOfWeek())

/**
 * Return the next date-time after this one that falls on a particular day of the week.
 */
fun OffsetDateTime.next(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.next(dayOfWeek))

/**
 * Return the next date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun OffsetDateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.nextOrSame(dayOfWeek))

/**
 * Return the last date-time before this one that falls on a particular day of the week.
 */
fun OffsetDateTime.previous(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previous(dayOfWeek))

/**
 * Return the last date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun OffsetDateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previousOrSame(dayOfWeek))

/**
 * Return the date-time at the first instant of the year that this date-time falls in.
 */
fun ZonedDateTime.startOfYear() = date.startOfYear().startOfDayAt(zone)

/**
 * Return the date-time at the last representable instant of the year that this date-time falls in.
 */
fun ZonedDateTime.endOfYear() = date.endOfYear().endOfDayAt(zone)

/**
 * Return the date-time at the first instant of the month that this date-time falls in.
 */
fun ZonedDateTime.startOfMonth() = date.startOfMonth().startOfDayAt(zone)

/**
 * Return the date-time at the last representable instant of the month that this date-time falls in.
 */
fun ZonedDateTime.endOfMonth() = date.endOfMonth().endOfDayAt(zone)

/**
 * Return the date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun ZonedDateTime.startOfWeek() = date.previousOrSame(DayOfWeek.MIN).startOfDayAt(zone)

/**
 * Return the date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
fun ZonedDateTime.endOfWeek() = date.nextOrSame(DayOfWeek.MAX).endOfDayAt(zone)

/**
 * Return the next date-time after this one that falls on a particular day of the week.
 */
fun ZonedDateTime.next(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.next(dayOfWeek))

/**
 * Return the next date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun ZonedDateTime.nextOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.nextOrSame(dayOfWeek))

/**
 * Return the last date-time before this one that falls on a particular day of the week.
 */
fun ZonedDateTime.previous(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previous(dayOfWeek))

/**
 * Return the last date-time that falls on a particular day of the week, or this one if it falls on the same day.
 */
fun ZonedDateTime.previousOrSame(dayOfWeek: DayOfWeek) = copy(dateTime = dateTime.previousOrSame(dayOfWeek))