package io.islandtime.parser.dsl

import io.islandtime.calendar.WeekProperty
import io.islandtime.format.dsl.DateTimeFormatBuilder
import io.islandtime.format.dsl.IslandTimeFormatDsl
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty

@IslandTimeFormatDsl
interface DateTimeParserBuilder : DateTimeFormatBuilder, BaseParserBuilder<DateTimeParserBuilder> {
    /**
     * Appends the two-digit year of the era.
     *
     * The result will be associated with [DateProperty.YearOfEra].
     */
    fun twoDigitYearOfEra(builder: TwoDigitYearParserBuilder.() -> Unit)

    /**
     * Appends the two-digit week-based year.
     *
     * The result will be associated with [WeekProperty.LocalizedWeekBasedYear].
     */
    fun twoDigitWeekBasedYear(builder: TwoDigitYearParserBuilder.() -> Unit)

    /**
     * Appends the localized time zone name.
     *
     * During parsing, [TimeZoneProperty.Id] will be populated for region-based zones while
     * [UtcOffsetProperty.TotalSeconds] or the combination of [UtcOffsetProperty.Sign], [UtcOffsetProperty.Hours],
     * [UtcOffsetProperty.Minutes], and [UtcOffsetProperty.Seconds] will be populated for fixed-offset zones.
     */
    fun timeZoneName(builder: TimeZoneNameParserBuilder.() -> Unit)
}

@IslandTimeFormatDsl
class TwoDigitYearParserBuilder {
    /**
     * The earliest two-digit year, by default, `2000`.
     */
    var earliestYear: () -> Int = { 2000 }
}
