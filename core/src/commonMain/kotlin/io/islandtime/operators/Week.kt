package io.islandtime.operators

import io.islandtime.Date
import io.islandtime.DateTime
import io.islandtime.OffsetDateTime
import io.islandtime.ZonedDateTime
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
 * The date range of the week that this date falls within. The first day of the week will be determined by the system
 * settings. This may differ from the first day of the week associated with the default locale on platforms that allow
 * this to be customized.
 */
val Date.localizedWeekRange: DateRange
    get() = with(localizedStartOfWeek) { this..this + 6.days }

/**
 * The date range of the week that this date falls within. The first day of the week will be determined by [locale].
 */
fun Date.localizedWeekRange(locale: Locale): DateRange {
    return with(localizedStartOfWeek(locale)) { this..this + 6.days }
}

/**
 * The interval of the ISO week that this date-time falls within.
 *
 * The ISO week starts on Monday and ends on Sunday.
 */
val DateTime.weekInterval: DateTimeInterval
    get() = with(startOfWeek) { this until this + 7.days }

/**
 * The interval of the week that this date-time falls within. The first day of the week will be determined by the system
 * settings. This may differ from the first day of the week associated with the default locale on platforms that allow
 * this to be customized.
 */
val DateTime.localizedWeekInterval: DateTimeInterval
    get() = with(localizedStartOfWeek) { this until this + 7.days }

/**
 * The interval of the week that this date falls within. The first day of the week will be determined by [locale].
 */
fun DateTime.localizedWeekInterval(locale: Locale): DateTimeInterval {
    return with(localizedStartOfWeek(locale)) { this until this + 7.days }
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
 * The interval of the week that this date-time falls within. The first day of the week will be determined by the system
 * settings. This may differ from the first day of the week associated with the default locale on platforms that allow
 * this to be customized. The offset will be preserved in both the start and end date-times.
 */
val OffsetDateTime.localizedWeekInterval: OffsetDateTimeInterval
    get() = with(localizedStartOfWeek) { this until this + 7.days }

/**
 * The interval of the week that this date falls within. The first day of the week will be determined by [locale]. The
 * offset will be preserved in both the start and end date-times.
 */
fun OffsetDateTime.localizedWeekInterval(locale: Locale): OffsetDateTimeInterval {
    return with(localizedStartOfWeek(locale)) { this until this + 7.days }
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
 * The interval of the week that this date-time falls within. The first day of the week will be determined by the system
 * settings. This may differ from the first day of the week associated with the default locale on platforms that allow
 * this to be customized. The zone will be preserved in both the start and end date-times.
 */
val ZonedDateTime.localizedWeekInterval: ZonedDateTimeInterval
    get() = with(localizedStartOfWeek) { this until this + 7.days }

/**
 * The interval of the week that this date falls within. The first day of the week will be determined by [locale]. The
 * zone will be preserved in both the start and end date-times.
 */
fun ZonedDateTime.localizedWeekInterval(locale: Locale): ZonedDateTimeInterval {
    return with(localizedStartOfWeek(locale)) { this until this + 7.days }
}