package io.islandtime.operators

import io.islandtime.Date
import io.islandtime.DateTime
import io.islandtime.OffsetDateTime
import io.islandtime.ZonedDateTime
import io.islandtime.calendar.WeekSettings
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import io.islandtime.ranges.*

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
val Date.weekRange: DateRange
    get() = startOfWeek.let { it..it + 6.days }

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun Date.weekRange(settings: WeekSettings): DateRange = startOfWeek(settings).let { it..it + 6.days }

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun Date.weekRange(locale: Locale): DateRange = startOfWeek(locale).let { it..it + 6.days }

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
val DateTime.weekInterval: DateTimeInterval
    get() = startOfWeek.let { it until it + 7.days }

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun DateTime.weekInterval(settings: WeekSettings): DateTimeInterval {
    return startOfWeek(settings).let { it until it + 7.days }
}

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun DateTime.weekInterval(locale: Locale): DateTimeInterval {
    return startOfWeek(locale).let { it until it + 7.days }
}

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
val OffsetDateTime.weekInterval: OffsetDateTimeInterval
    get() = startOfWeek.let { it until it + 7.days }

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.weekInterval(settings: WeekSettings): OffsetDateTimeInterval {
    return startOfWeek(settings).let { it until it + 7.days }
}

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.weekInterval(locale: Locale): OffsetDateTimeInterval {
    return startOfWeek(locale).let { it until it + 7.days }
}

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
val ZonedDateTime.weekInterval: ZonedDateTimeInterval
    get() = startOfWeek.let { it until it + 7.days }

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(settings)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun ZonedDateTime.weekInterval(settings: WeekSettings): ZonedDateTimeInterval {
    return startOfWeek(settings).let { it until it + 7.days }
}

@Deprecated(
    "Renamed to 'week'.",
    ReplaceWith("this.week(locale)", "io.islandtime.week"),
    DeprecationLevel.WARNING
)
fun ZonedDateTime.weekInterval(locale: Locale): ZonedDateTimeInterval {
    return startOfWeek(locale).let { it until it + 7.days }
}