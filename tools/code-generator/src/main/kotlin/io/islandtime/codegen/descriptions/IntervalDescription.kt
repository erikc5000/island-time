package io.islandtime.codegen.descriptions

import com.squareup.kotlinpoet.TypeName
import io.islandtime.codegen.ranges

enum class IntervalDescription(
    val typeName: TypeName,
    val simpleName: String,
    val isInclusive: Boolean
) {
    DateRange(
        typeName = ranges("DateRange"),
        simpleName = "range",
        isInclusive = true
    ) {
        override val elementDescription get() = DateTimeDescription.Date
    },
    DateTimeInterval(
        typeName = ranges("DateTimeInterval"),
        simpleName = "interval",
        isInclusive = false
    ) {
        override val elementDescription get() = DateTimeDescription.DateTime
    },
    OffsetDateTimeInterval(
        typeName = ranges("OffsetDateTimeInterval"),
        simpleName = "interval",
        isInclusive = false
    ) {
        override val elementDescription get() = DateTimeDescription.OffsetDateTime
    },
    ZonedDateTimeInterval(
        typeName = ranges("ZonedDateTimeInterval"),
        simpleName = "interval",
        isInclusive = false
    ) {
        override val elementDescription get() = DateTimeDescription.ZonedDateTime
    },
    InstantInterval(
        typeName = ranges("InstantInterval"),
        simpleName = "interval",
        isInclusive = false
    ) {
        override val elementDescription get() = DateTimeDescription.Instant
    };

    abstract val elementDescription: DateTimeDescription

    val isDateBased: Boolean get() = elementDescription.isDateBased
    val isTimeBased: Boolean get() = elementDescription.isTimeBased
    val isTimePointInterval: Boolean get() = elementDescription.isTimePoint
}
