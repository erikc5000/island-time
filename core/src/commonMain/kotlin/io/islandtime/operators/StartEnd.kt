package io.islandtime.operators

import io.islandtime.*
import io.islandtime.calendar.WeekSettings
import io.islandtime.calendar.firstDayOfWeek
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import io.islandtime.operators.internal.copyIfChanged

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
val Date.startOfMonth: Date get() = copyIfChanged(dayOfMonth = 1)

/**
 * The date at the end of the month that this date falls in.
 */
val Date.endOfMonth: Date get() = copyIfChanged(dayOfMonth = month.lastDayIn(year))

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
 * The date at the start of the week that this date falls in. The first day of the week will be determined by
 * [settings].
 */
fun Date.startOfWeek(settings: WeekSettings): Date = previousOrSame(settings.firstDayOfWeek)

/**
 * The date at the start of the week that this date falls in. The first day of the week will be determined by [locale].
 */
fun Date.startOfWeek(locale: Locale): Date = previousOrSame(locale.firstDayOfWeek)

/**
 * The date at the end of the week that this date falls in. The first day of the week will be determined by [settings]. The first day of the week will be determined by the
 * system settings. The first day of the week will be determined by the system settings. This may differ from the first
 * day of the week associated with the default locale on platforms that allow this to be customized.
 */
fun Date.endOfWeek(settings: WeekSettings): Date {
    return nextOrSame(settings.firstDayOfWeek + 6.days)
}

/**
 * The date at the end of the week that this date falls in. The first day of the week will be determined by [locale].
 */
fun Date.endOfWeek(locale: Locale): Date = nextOrSame(locale.firstDayOfWeek + 6.days)

/**
 * The date-time at the first instant of the year that this date-time falls in.
 */
val DateTime.startOfYear: DateTime
    get() = copyIfChanged(date = date.startOfYear, time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val DateTime.endOfYear: DateTime
    get() = copyIfChanged(date = date.endOfYear, time = Time.MAX)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val DateTime.startOfMonth: DateTime
    get() = copyIfChanged(date = date.startOfMonth, time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val DateTime.endOfMonth: DateTime
    get() = copyIfChanged(date = date.endOfMonth, time = Time.MAX)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.startOfWeek: DateTime
    get() = copyIfChanged(date = date.startOfWeek, time = Time.MIDNIGHT)

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by the system settings. This may differ from the first day of the week associated with the default locale
 * on platforms that allow this to be customized.
 */
fun DateTime.startOfWeek(settings: WeekSettings): DateTime {
    return copyIfChanged(date = date.startOfWeek(settings), time = Time.MIDNIGHT)
}

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by [locale].
 */
fun DateTime.startOfWeek(locale: Locale): DateTime {
    return copyIfChanged(date = date.startOfWeek(locale), time = Time.MIDNIGHT)
}

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.endOfWeek: DateTime
    get() = copyIfChanged(date = date.endOfWeek, time = Time.MAX)

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by the system settings. This may differ from the first day of the week associated with the default
 * locale on platforms that allow this to be customized.
 */
fun DateTime.endOfWeek(settings: WeekSettings): DateTime {
    return copyIfChanged(date = date.endOfWeek(settings), time = Time.MAX)
}

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by [locale].
 */
fun DateTime.endOfWeek(locale: Locale): DateTime {
    return copyIfChanged(date = date.endOfWeek(locale), time = Time.MAX)
}

/**
 * The date-time at the first instant of the year that this date-time falls in.
 */
val OffsetDateTime.startOfYear: OffsetDateTime
    get() = copyIfChanged(dateTime = dateTime.startOfYear)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val OffsetDateTime.endOfYear: OffsetDateTime
    get() = copyIfChanged(dateTime = dateTime.endOfYear)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val OffsetDateTime.startOfMonth: OffsetDateTime
    get() = copyIfChanged(dateTime = dateTime.startOfMonth)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val OffsetDateTime.endOfMonth: OffsetDateTime
    get() = copyIfChanged(dateTime = dateTime.endOfMonth)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val OffsetDateTime.startOfWeek: OffsetDateTime
    get() = copyIfChanged(dateTime = dateTime.startOfWeek)

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by the user's system settings. This may differ from the first day of the week associated with the default
 * locale on platforms that allow the user to customize this.
 */
fun OffsetDateTime.startOfWeek(settings: WeekSettings): OffsetDateTime {
    return copyIfChanged(dateTime = dateTime.startOfWeek(settings))
}

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by [locale].
 */
fun OffsetDateTime.startOfWeek(locale: Locale): OffsetDateTime {
    return copyIfChanged(dateTime = dateTime.startOfWeek(locale))
}

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val OffsetDateTime.endOfWeek: OffsetDateTime
    get() = copyIfChanged(dateTime = dateTime.endOfWeek)

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by the user's system settings. This may differ from the first day of the week associated with the
 * default locale on platforms that allow the user to customize this.
 */
fun OffsetDateTime.endOfWeek(settings: WeekSettings): OffsetDateTime {
    return copyIfChanged(dateTime = dateTime.endOfWeek(settings))
}

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by [locale].
 */
fun OffsetDateTime.endOfWeek(locale: Locale): OffsetDateTime {
    return copyIfChanged(dateTime = dateTime.endOfWeek(locale))
}

/**
 * The date-time at the first instant of the year that this date-time falls in.
 */
val ZonedDateTime.startOfYear: ZonedDateTime
    get() = date.startOfYear.startOfDayAt(zone)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val ZonedDateTime.endOfYear: ZonedDateTime
    get() = date.endOfYear.endOfDayAt(zone)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val ZonedDateTime.startOfMonth: ZonedDateTime
    get() = date.startOfMonth.startOfDayAt(zone)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val ZonedDateTime.endOfMonth: ZonedDateTime
    get() = date.endOfMonth.endOfDayAt(zone)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val ZonedDateTime.startOfWeek: ZonedDateTime get() = date.startOfWeek.startOfDayAt(zone)

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by the user's system settings. This may differ from the first day of the week associated with the default
 * locale on platforms that allow the user to customize this.
 */
fun ZonedDateTime.startOfWeek(settings: WeekSettings): ZonedDateTime = date.startOfWeek(settings).startOfDayAt(zone)

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by [locale].
 */
fun ZonedDateTime.startOfWeek(locale: Locale): ZonedDateTime = date.startOfWeek(locale).startOfDayAt(zone)

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val ZonedDateTime.endOfWeek: ZonedDateTime get() = date.endOfWeek.endOfDayAt(zone)

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by the user's system settings. This may differ from the first day of the week associated with the
 * default locale on platforms that allow the user to customize this.
 */
fun ZonedDateTime.endOfWeek(settings: WeekSettings): ZonedDateTime = date.endOfWeek(settings).endOfDayAt(zone)

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by [locale].
 */
fun ZonedDateTime.endOfWeek(locale: Locale): ZonedDateTime = date.endOfWeek(locale).endOfDayAt(zone)