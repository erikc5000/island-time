package io.islandtime

import io.islandtime.measures.days

/**
 * The date at the start of the year that this date falls in.
 */
val Date.startOfYear: Date get() = Date(year, Month.JANUARY, 1)

/**
 * The date at the end of the year that this date falls in.
 */
val Date.endOfYear: Date get() = Date(year, Month.DECEMBER, 31)

/**
 * The date at the start of the month that this date falls in.
 */
val Date.startOfMonth: Date get() = copy(dayOfMonth = 1)

/**
 * The date at the end of the month that this date falls in.
 */
val Date.endOfMonth: Date get() = copy(dayOfMonth = month.lastDayIn(year))

/**
 * The date at the start of the ISO week that this date falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val Date.startOfWeek: Date get() = previousOrSame(DayOfWeek.MIN)

/**
 * The date at the end of the ISO week that this date falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val Date.endOfWeek: Date get() = nextOrSame(DayOfWeek.MAX)

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
 * The date-time at the first instant of the year that this date-time falls in.
 */
val DateTime.startOfYear: DateTime get() = copy(date = date.startOfYear, time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val DateTime.endOfYear: DateTime get() = copy(date = date.endOfYear, time = Time.MAX)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val DateTime.startOfMonth: DateTime get() = copy(date = date.startOfMonth, time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val DateTime.endOfMonth: DateTime get() = copy(date = date.endOfMonth, time = Time.MAX)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.startOfWeek: DateTime get() = copy(date = date.previousOrSame(DayOfWeek.MIN), time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.endOfWeek: DateTime get() = copy(date = date.nextOrSame(DayOfWeek.MAX), time = Time.MAX)

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
 * The date-time at the first instant of the year that this date-time falls in.
 */
val OffsetDateTime.startOfYear: OffsetDateTime get() = copy(dateTime = dateTime.startOfYear)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val OffsetDateTime.endOfYear: OffsetDateTime get() = copy(dateTime = dateTime.endOfYear)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val OffsetDateTime.startOfMonth: OffsetDateTime get() = copy(dateTime = dateTime.startOfMonth)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val OffsetDateTime.endOfMonth: OffsetDateTime get() = copy(dateTime = dateTime.endOfMonth)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val OffsetDateTime.startOfWeek: OffsetDateTime get() = copy(dateTime = dateTime.startOfWeek)

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val OffsetDateTime.endOfWeek: OffsetDateTime get() = copy(dateTime = dateTime.endOfWeek)

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
 * The date-time at the first instant of the year that this date-time falls in.
 */
val ZonedDateTime.startOfYear: ZonedDateTime get() = date.startOfYear.startOfDayAt(zone)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val ZonedDateTime.endOfYear: ZonedDateTime get() = date.endOfYear.endOfDayAt(zone)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val ZonedDateTime.startOfMonth: ZonedDateTime get() = date.startOfMonth.startOfDayAt(zone)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val ZonedDateTime.endOfMonth: ZonedDateTime get() = date.endOfMonth.endOfDayAt(zone)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val ZonedDateTime.startOfWeek: ZonedDateTime get() = date.previousOrSame(DayOfWeek.MIN).startOfDayAt(zone)

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val ZonedDateTime.endOfWeek: ZonedDateTime get() = date.nextOrSame(DayOfWeek.MAX).endOfDayAt(zone)

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