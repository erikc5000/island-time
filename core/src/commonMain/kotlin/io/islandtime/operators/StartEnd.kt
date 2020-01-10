package io.islandtime.operators

import io.islandtime.*
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale

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
 * The date at the start of the week that this date falls in. The first day of the week will be determined by [locale].
 */
fun Date.localizedStartOfWeek(locale: Locale = defaultLocale()): Date = previousOrSame(locale.firstDayOfWeek)

/**
 * The date at the end of the week that this date falls in. The last day of the week will be determined by [locale].
 */
fun Date.localizedEndOfWeek(locale: Locale = defaultLocale()): Date = nextOrSame(locale.lastDayOfWeek)

/**
 * The date-time at the first instant of the year that this date-time falls in.
 */
val DateTime.startOfYear: DateTime
    get() = copy(date = date.startOfYear, time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val DateTime.endOfYear: DateTime
    get() = copy(date = date.endOfYear, time = Time.MAX)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val DateTime.startOfMonth: DateTime
    get() = copy(date = date.startOfMonth, time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val DateTime.endOfMonth: DateTime
    get() = copy(date = date.endOfMonth, time = Time.MAX)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.startOfWeek: DateTime
    get() = copy(date = date.startOfWeek, time = Time.MIDNIGHT)

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by [locale].
 */
fun DateTime.localizedStartOfWeek(locale: Locale = defaultLocale()): DateTime =
    copy(date = date.localizedStartOfWeek(locale), time = Time.MIDNIGHT)

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.endOfWeek: DateTime
    get() = copy(date = date.endOfWeek, time = Time.MAX)

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by [locale].
 */
fun DateTime.localizedEndOfWeek(locale: Locale = defaultLocale()): DateTime =
    copy(date = date.localizedEndOfWeek(locale), time = Time.MAX)

/**
 * The date-time at the first instant of the year that this date-time falls in.
 */
val OffsetDateTime.startOfYear: OffsetDateTime
    get() = copy(dateTime = dateTime.startOfYear)

/**
 * The date-time at the last representable instant of the year that this date-time falls in.
 */
val OffsetDateTime.endOfYear: OffsetDateTime
    get() = copy(dateTime = dateTime.endOfYear)

/**
 * The date-time at the first instant of the month that this date-time falls in.
 */
val OffsetDateTime.startOfMonth: OffsetDateTime
    get() = copy(dateTime = dateTime.startOfMonth)

/**
 * The date-time at the last representable instant of the month that this date-time falls in.
 */
val OffsetDateTime.endOfMonth: OffsetDateTime
    get() = copy(dateTime = dateTime.endOfMonth)

/**
 * The date-time at the first instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val OffsetDateTime.startOfWeek: OffsetDateTime
    get() = copy(dateTime = dateTime.startOfWeek)

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by [locale].
 */
fun OffsetDateTime.localizedStartOfWeek(locale: Locale = defaultLocale()): OffsetDateTime =
    copy(dateTime = dateTime.localizedStartOfWeek(locale))

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val OffsetDateTime.endOfWeek: OffsetDateTime
    get() = copy(dateTime = dateTime.endOfWeek)

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by [locale].
 */
fun OffsetDateTime.localizedEndOfWeek(locale: Locale): OffsetDateTime =
    copy(dateTime = dateTime.localizedEndOfWeek(locale))

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
val ZonedDateTime.startOfWeek: ZonedDateTime
    get() = date.startOfWeek.startOfDayAt(zone)

/**
 * The date-time at the first instant of the week that this date-time falls in. The first day of the week will be
 * determined by [locale].
 */
fun ZonedDateTime.localizedStartOfWeek(locale: Locale = defaultLocale()): ZonedDateTime =
    date.localizedStartOfWeek(locale).startOfDayAt(zone)

/**
 * The date-time at the last representable instant of the ISO week that this date-time falls in.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val ZonedDateTime.endOfWeek: ZonedDateTime
    get() = date.endOfWeek.endOfDayAt(zone)

/**
 * The date-time at the last representable instant of the week that this date-time falls in. The first day of the week
 * will be determined by [locale].
 */
fun ZonedDateTime.localizedEndOfWeek(locale: Locale = defaultLocale()): ZonedDateTime =
    date.localizedEndOfWeek(locale).endOfDayAt(zone)