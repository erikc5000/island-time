package io.islandtime.codegen.descriptions

import com.squareup.kotlinpoet.TypeName
import io.islandtime.codegen.root

enum class DateTimeDescription(
    val typeName: TypeName,
    val simpleName: String,
    val smallestUnit: TemporalUnitDescription,
    val interval: IntervalDescription? = null,
    val isDateBased: Boolean = false,
    val isTimeBased: Boolean = false,
    val isTimePoint: Boolean = false
) {
    Year(
        typeName = root("Year"),
        simpleName = "year",
        smallestUnit = TemporalUnitDescription.YEARS,
        isDateBased = true
    ),
    YearMonth(
        typeName = root("YearMonth"),
        simpleName = "year-month",
        smallestUnit = TemporalUnitDescription.MONTHS,
        isDateBased = true
    ),
    Date(
        typeName = root("Date"),
        simpleName = "date",
        smallestUnit = TemporalUnitDescription.DAYS,
        interval = IntervalDescription.DateRange,
        isDateBased = true
    ),
    DateTime(
        typeName = root("DateTime"),
        simpleName = "date-time",
        smallestUnit = TemporalUnitDescription.NANOSECONDS,
        interval = IntervalDescription.DateTimeInterval,
        isDateBased = true,
        isTimeBased = true
    ) {
        override val datePropertyName: String get() = "date"
    },
    OffsetDateTime(
        typeName = root("OffsetDateTime"),
        simpleName = "date-time",
        smallestUnit = TemporalUnitDescription.NANOSECONDS,
        interval = IntervalDescription.OffsetDateTimeInterval,
        isDateBased = true,
        isTimeBased = true,
        isTimePoint = true
    ) {
        override val datePropertyName: String get() = "dateTime"
    },
    ZonedDateTime(
        typeName = root("ZonedDateTime"),
        simpleName = "date-time",
        smallestUnit = TemporalUnitDescription.NANOSECONDS,
        interval = IntervalDescription.ZonedDateTimeInterval,
        isDateBased = true,
        isTimeBased = true,
        isTimePoint = true
    ) {
        override val datePropertyName: String get() = "dateTime"
    },
    Instant(
        typeName = root("Instant"),
        simpleName = "instant",
        smallestUnit = TemporalUnitDescription.NANOSECONDS,
        isTimeBased = true,
        isTimePoint = true
    );

    open val datePropertyName: String get() = throw NotImplementedError()

    open fun convertsDirectlyTo(other: DateTimeDescription): Boolean {
        return isDateBased &&
            other.isDateBased &&
            other.smallestUnit > smallestUnit &&
            other.smallestUnit > TemporalUnitDescription.DAYS
    }
}
