package io.islandtime.operators

import io.islandtime.Date
import io.islandtime.DateTime
import io.islandtime.OffsetDateTime
import io.islandtime.ZonedDateTime
import io.islandtime.calendar.WeekSettings
import io.islandtime.internal.deprecatedToError
import io.islandtime.locale.Locale
import io.islandtime.ranges.DateRange
import io.islandtime.ranges.DateTimeInterval
import io.islandtime.ranges.OffsetDateTimeInterval
import io.islandtime.ranges.ZonedDateTimeInterval

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
val Date.weekRange: DateRange
    get() = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun Date.weekRange(settings: WeekSettings): DateRange = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun Date.weekRange(locale: Locale): DateRange = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
val DateTime.weekInterval: DateTimeInterval
    get() = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun DateTime.weekInterval(settings: WeekSettings): DateTimeInterval = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun DateTime.weekInterval(locale: Locale): DateTimeInterval = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
val OffsetDateTime.weekInterval: OffsetDateTimeInterval
    get() = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.weekInterval(settings: WeekSettings): OffsetDateTimeInterval = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.weekInterval(locale: Locale): OffsetDateTimeInterval = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
val ZonedDateTime.weekInterval: ZonedDateTimeInterval
    get() = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.weekInterval(settings: WeekSettings): ZonedDateTimeInterval = deprecatedToError()

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.weekInterval(locale: Locale): ZonedDateTimeInterval = deprecatedToError()
