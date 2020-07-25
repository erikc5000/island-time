package io.islandtime.codegen.descriptions

import com.squareup.kotlinpoet.TypeName
import io.islandtime.codegen.base

enum class DateTimeDescription {
    Year {
        override val typeName: TypeName get() = base("Year")
        override val smallestUnit: TemporalUnitDescription get() = TemporalUnitDescription.YEARS
        override val simpleName: String get() = "year"
        override val isDateBased: Boolean = true
    },
    YearMonth {
        override val typeName: TypeName get() = base("YearMonth")
        override val smallestUnit: TemporalUnitDescription get() = TemporalUnitDescription.MONTHS
        override val simpleName: String get() = "year-month"
        override val isDateBased: Boolean = true
    },
    Date {
        override val typeName: TypeName get() = base("Date")
        override val smallestUnit: TemporalUnitDescription get() = TemporalUnitDescription.DAYS
        override val simpleName: String get() = "date"
        override val isDateBased: Boolean = true
    },
    DateTime {
        override val typeName: TypeName get() = base("DateTime")
        override val smallestUnit: TemporalUnitDescription get() = TemporalUnitDescription.NANOSECONDS
        override val datePropertyName: String get() = "date"
        override val simpleName: String get() = "date-time"
        override val isDateBased: Boolean = true
    },
    OffsetDateTime {
        override val typeName: TypeName get() = base("OffsetDateTime")
        override val smallestUnit: TemporalUnitDescription get() = TemporalUnitDescription.NANOSECONDS
        override val datePropertyName: String get() = "dateTime"
        override val simpleName: String get() = "date-time"
        override val isDateBased: Boolean = true
    },
    ZonedDateTime {
        override val typeName: TypeName get() = base("ZonedDateTime")
        override val smallestUnit: TemporalUnitDescription get() = TemporalUnitDescription.NANOSECONDS
        override val datePropertyName: String get() = "dateTime"
        override val simpleName: String get() = "date-time"
        override val isDateBased: Boolean = true
    },
    Instant {
        override val typeName: TypeName get() = base("Instant")
        override val smallestUnit: TemporalUnitDescription get() = TemporalUnitDescription.NANOSECONDS
        override val simpleName: String get() = "instant"
    };

    abstract val typeName: TypeName
    abstract val smallestUnit: TemporalUnitDescription
    abstract val simpleName: String

    open val isDateBased: Boolean = false

    open val datePropertyName: String get() = throw NotImplementedError()

    open fun convertsDirectlyTo(other: DateTimeDescription): Boolean {
        return isDateBased &&
            other.isDateBased &&
            other.smallestUnit > smallestUnit &&
            other.smallestUnit > TemporalUnitDescription.DAYS
    }
}