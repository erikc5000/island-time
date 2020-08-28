package io.islandtime.calendar

import io.islandtime.*
import io.islandtime.base.DateProperty
import io.islandtime.base.Temporal

enum class WeekProperty : LocalizedNumberProperty {
    LocalizedDayOfWeek {
        override val valueRange: LongRange = 1L..7L

        override fun LocalizationContext.deriveValueFrom(temporal: Temporal): Long {
            return temporal.get(DateProperty.DayOfWeek).toInt().toDayOfWeek().number(weekSettings).toLong()
        }
    },
    LocalizedWeekOfMonth {
        override val valueRange: LongRange = 0L..6L

        override fun LocalizationContext.deriveValueFrom(temporal: Temporal): Long {
            return temporal.get(DateProperty.Date).weekOfMonth(weekSettings).toLong()
        }
    },
    LocalizedWeekOfYear {
        override val valueRange: LongRange = 0L..54L

        override fun LocalizationContext.deriveValueFrom(temporal: Temporal): Long {
            return temporal.get(DateProperty.Date).weekOfYear(weekSettings).toLong()
        }
    },
    LocalizedWeekBasedYear {
        override fun LocalizationContext.deriveValueFrom(temporal: Temporal): Long {
            return temporal.get(DateProperty.Date).weekBasedYear(weekSettings).toLong()
        }
    },
    LocalizedWeekOfWeekBasedYear {
        override val valueRange: LongRange = 1L..53L

        override fun LocalizationContext.deriveValueFrom(temporal: Temporal): Long {
            return temporal.get(DateProperty.Date).weekOfWeekBasedYear(weekSettings).toLong()
        }
    };

    override fun toString(): String = name
}
