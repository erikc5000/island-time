package io.islandtime.calendar

import io.islandtime.*
import io.islandtime.properties.DateProperty
import io.islandtime.base.Temporal

enum class WeekProperty : LocalizedNumberProperty {
    LocalizedDayOfWeek {
        override val valueRange: LongRange = 1L..7L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DateProperty.DayOfWeek)

        override fun deriveValueFrom(temporal: Temporal, context: LocalizationContext): Long {
            return temporal.get(DateProperty.DayOfWeek).toInt().toDayOfWeek().number(context.weekSettings).toLong()
        }
    },
    LocalizedWeekOfMonth {
        override val valueRange: LongRange = 0L..6L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DateProperty.DateObject)

        override fun deriveValueFrom(temporal: Temporal, context: LocalizationContext): Long {
            return temporal.get(DateProperty.DateObject).weekOfMonth(context.weekSettings).toLong()
        }
    },
    LocalizedWeekOfYear {
        override val valueRange: LongRange = 0L..54L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DateProperty.DateObject)

        override fun deriveValueFrom(temporal: Temporal, context: LocalizationContext): Long {
            return temporal.get(DateProperty.DateObject).weekOfYear(context.weekSettings).toLong()
        }
    },
    LocalizedWeekBasedYear {
        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DateProperty.DateObject)

        override fun deriveValueFrom(temporal: Temporal, context: LocalizationContext): Long {
            return temporal.get(DateProperty.DateObject).weekBasedYear(context.weekSettings).toLong()
        }
    },
    LocalizedWeekOfWeekBasedYear {
        override val valueRange: LongRange = 1L..53L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DateProperty.DateObject)

        override fun deriveValueFrom(temporal: Temporal, context: LocalizationContext): Long {
            return temporal.get(DateProperty.DateObject).weekOfWeekBasedYear(context.weekSettings).toLong()
        }
    };

    override fun toString(): String = name
}
