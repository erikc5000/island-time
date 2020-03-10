package io.islandtime.operators

import io.islandtime.Date
import io.islandtime.DateTime
import io.islandtime.OffsetDateTime
import io.islandtime.ZonedDateTime
import io.islandtime.calendar.WeekSettings
import io.islandtime.locale.Locale
import io.islandtime.measures.days
import io.islandtime.ranges.*

/**
 * The date range of the ISO week that this date falls within.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val Date.weekRange: DateRange
    get() = with(startOfWeek) { this..this + 6.days }

/**
 * The date range of the week that this date falls within. The first day of the week will be determined by [settings].
 */
fun Date.weekRange(settings: WeekSettings): DateRange = with(startOfWeek(settings)) { this..this + 6.days }

/**
 * The date range of the week that this date falls within. The first day of the week will be determined by [locale].
 */
fun Date.weekRange(locale: Locale): DateRange = with(startOfWeek(locale)) { this..this + 6.days }

/**
 * The interval of the ISO week that this date-time falls within.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.weekInterval: DateTimeInterval
    get() = with(startOfWeek) { this until this + 7.days }

/**
 * The interval of the week that this date-time falls within. The first day of the week will be determined by
 * [settings].
 */
fun DateTime.weekInterval(settings: WeekSettings): DateTimeInterval {
    return with(startOfWeek(settings)) { this until this + 7.days }
}

/**
 * The interval of the week that this date falls within. The first day of the week will be determined by [locale].
 */
fun DateTime.weekInterval(locale: Locale): DateTimeInterval {
    return with(startOfWeek(locale)) { this until this + 7.days }
}

/**
 * The interval of the ISO week that this date-time falls within. The offset will be preserved in both the start and end
 * date-times
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val OffsetDateTime.weekInterval: OffsetDateTimeInterval
    get() = with(startOfWeek) { this until this + 7.days }

/**
 * The interval of the week that this date-time falls within. The first day of the week will be determined by
 * [settings]. The offset will be preserved in both the start and end date-times.
 */
fun OffsetDateTime.weekInterval(settings: WeekSettings): OffsetDateTimeInterval {
    return with(startOfWeek(settings)) { this until this + 7.days }
}

/**
 * The interval of the week that this date falls within. The first day of the week will be determined by [locale]. The
 * offset will be preserved in both the start and end date-times.
 */
fun OffsetDateTime.weekInterval(locale: Locale): OffsetDateTimeInterval {
    return with(startOfWeek(locale)) { this until this + 7.days }
}

/**
 * The interval of the ISO week that this date-time falls within. The zone will be preserved in both the start and end
 * date-times.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val ZonedDateTime.weekInterval: ZonedDateTimeInterval
    get() = with(startOfWeek) { this until this + 7.days }

/**
 * The interval of the week that this date-time falls within. The first day of the week will be determined by
 * [settings]. The zone will be preserved in both the start and end date-times.
 */
fun ZonedDateTime.weekInterval(settings: WeekSettings): ZonedDateTimeInterval {
    return with(startOfWeek(settings)) { this until this + 7.days }
}

/**
 * The interval of the week that this date falls within. The first day of the week will be determined by [locale]. The
 * zone will be preserved in both the start and end date-times.
 */
fun ZonedDateTime.weekInterval(locale: Locale): ZonedDateTimeInterval {
    return with(startOfWeek(locale)) { this until this + 7.days }
}