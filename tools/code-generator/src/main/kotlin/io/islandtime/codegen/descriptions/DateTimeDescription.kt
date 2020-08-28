package io.islandtime.codegen.descriptions

import com.squareup.kotlinpoet.TypeName
import io.islandtime.codegen.base

enum class DateTimeDescription(
    val typeName: TypeName,
    val simpleName: String,
    val smallestUnit: TemporalUnitDescription,
    val interval: IntervalDescription? = null,
    val isDateBased: Boolean = false
) {
    Year(
        typeName = base("Year"),
        simpleName = "year",
        smallestUnit = TemporalUnitDescription.YEARS,
        isDateBased = true
    ),
    YearMonth(
        typeName = base("YearMonth"),
        simpleName = "year-month",
        smallestUnit = TemporalUnitDescription.MONTHS,
        isDateBased = true
    ),
    Date(
        typeName = base("Date"),
        simpleName = "date",
        smallestUnit = TemporalUnitDescription.DAYS,
        interval = IntervalDescription.DateRange,
        isDateBased = true
    ),
    DateTime(
        typeName = base("DateTime"),
        simpleName = "date-time",
        smallestUnit = TemporalUnitDescription.NANOSECONDS,
        interval = IntervalDescription.DateTimeInterval,
        isDateBased = true
    ) {
        override val datePropertyName: String get() = "date"
    },
    OffsetDateTime(
        typeName = base("OffsetDateTime"),
        simpleName = "date-time",
        smallestUnit = TemporalUnitDescription.NANOSECONDS,
        interval = IntervalDescription.OffsetDateTimeInterval,
        isDateBased = true
    ) {
        override val datePropertyName: String get() = "dateTime"
    },
    ZonedDateTime(
        typeName = base("ZonedDateTime"),
        simpleName = "date-time",
        smallestUnit = TemporalUnitDescription.NANOSECONDS,
        interval = IntervalDescription.ZonedDateTimeInterval,
        isDateBased = true
    ) {
        override val datePropertyName: String get() = "dateTime"
    },
    Instant(
        typeName = base("Instant"),
        simpleName = "instant",
        smallestUnit = TemporalUnitDescription.NANOSECONDS
    );

    open val datePropertyName: String get() = throw NotImplementedError()

    open fun convertsDirectlyTo(other: DateTimeDescription): Boolean {
        return isDateBased &&
            other.isDateBased &&
            other.smallestUnit > smallestUnit &&
            other.smallestUnit > TemporalUnitDescription.DAYS
    }
}
