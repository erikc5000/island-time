package io.islandtime.parser.dsl

import io.islandtime.format.dsl.DateTimeFormatBuilder
import io.islandtime.format.dsl.IslandTimeFormatDsl

@IslandTimeFormatDsl
interface DateTimeParserBuilder : DateTimeFormatBuilder, BaseParserBuilder<DateTimeParserBuilder> {
    fun twoDigitYearOfEra(builder: TwoDigitYearParserBuilder.() -> Unit)

    fun twoDigitWeekBasedYear(builder: TwoDigitYearParserBuilder.() -> Unit)
}

@IslandTimeFormatDsl
class TwoDigitYearParserBuilder {
    var earliestYear: () -> Int = { 2000 }
}
