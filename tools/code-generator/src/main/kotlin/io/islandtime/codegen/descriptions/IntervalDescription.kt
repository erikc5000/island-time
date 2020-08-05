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
    ),
    DateTimeInterval(
        typeName = ranges("DateTimeInterval"),
        simpleName = "interval",
        isInclusive = false
    ),
    OffsetDateTimeInterval(
        typeName = ranges("OffsetDateTimeInterval"),
        simpleName = "interval",
        isInclusive = false
    ),
    ZonedDateTimeInterval(
        typeName = ranges("ZonedDateTimeInterval"),
        simpleName = "interval",
        isInclusive = false
    );
}
