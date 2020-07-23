package io.islandtime.codegen.descriptions

import com.squareup.kotlinpoet.TypeName
import io.islandtime.codegen.base

enum class DateTimeDescription {
    Date {
        override val typeName: TypeName get() = base("Date")
        override val datePropertyName: String get() = "this"
        override val simpleName: String get() = "date"
    },
    DateTime {
        override val typeName: TypeName get() = base("DateTime")
        override val datePropertyName: String get() = "date"
        override val simpleName: String get() = "date-time"
    },
    OffsetDateTime {
        override val typeName: TypeName get() = base("OffsetDateTime")
        override val datePropertyName: String get() = "dateTime"
        override val simpleName: String get() = "date-time"
    },
    ZonedDateTime {
        override val typeName: TypeName get() = base("ZonedDateTime")
        override val datePropertyName: String get() = "dateTime"
        override val simpleName: String get() = "date-time"
    },
    Instant {
        override val typeName: TypeName get() = base("Instant")
        override val datePropertyName: String get() = ""
        override val simpleName: String get() = "instant"
    };

    abstract val typeName: TypeName
    abstract val datePropertyName: String
    abstract val simpleName: String

    val isDateBased: Boolean get() = datePropertyName.isNotEmpty()
}