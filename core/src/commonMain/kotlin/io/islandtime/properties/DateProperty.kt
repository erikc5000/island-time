package io.islandtime.properties

import io.islandtime.Date
import io.islandtime.base.*
import io.islandtime.internal.dayOfWeekInMonth

/**
 * A calendar-related property.
 */
sealed class DateProperty {
    /**
     * The proleptic year.
     *
     * In the ISO calendar system, positive values indicate years in the current era, a value of 0 is equivalent to 1
     * BCE, -1 to 2 BCE, and so forth.
     */
    object Year : DateProperty(), DerivableNumberProperty {
        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.hasAll(YearOfEra, Era)

        override fun deriveValueFrom(temporal: Temporal): Long {
            val yearOfEra = temporal.get(YearOfEra)

            return when (temporal.get(Era)) {
                0L -> 1 - yearOfEra
                else -> yearOfEra
            }
        }
    }

    /**
     * The year of the era.
     */
    object YearOfEra : DateProperty(), DerivableNumberProperty {
        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(Year)

        override fun deriveValueFrom(temporal: Temporal): Long {
            val year = temporal.get(Year)
            return if (year >= 1) year else 1 - year
        }
    }

    /**
     * The era.
     *
     * In the ISO calendar system, this will be 0 (`BCE`) or 1 (`CE`).
     */
    object Era : DateProperty(), DerivableNumberProperty {
        override val valueRange: LongRange = 0L..1L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(Year)

        override fun deriveValueFrom(temporal: Temporal): Long {
            return if (temporal.get(Year) >= 1) 1 else 0
        }
    }

    /**
     * The month of the year, such as `January`
     *
     * In the ISO calendar system, this will be from 1 (`January`) to 12 (`December`).
     */
    object MonthOfYear : DateProperty(), NumberProperty {
        override val valueRange: LongRange = 1L..12L
    }

    /**
     * The day of the Unix epoch. `0` corresponds to `1970-01-01`.
     */
    object DayOfUnixEpoch : DateProperty(), NumberProperty

    /**
     * The day of the year.
     *
     * In the ISO calendar system, this will be from 1-366.
     */
    object DayOfYear : DateProperty(), DerivableNumberProperty {
        override val valueRange: LongRange = 1L..366L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DateObject)

        override fun deriveValueFrom(temporal: Temporal): Long {
            return temporal.get(DateObject).dayOfYear.toLong()
        }
    }

    /**
     * The day of the month.
     *
     * In the ISO calendar system, this will be from 1-31.
     */
    object DayOfMonth : DateProperty(), NumberProperty {
        override val valueRange: LongRange = 1L..31L
    }

    /**
     * The day of the week, such as `Sunday`.
     *
     * In the ISO calendar system, this will be from 1 (`Monday`) to 7 (`Sunday`).
     */
    object DayOfWeek : DateProperty(), DerivableNumberProperty {
        override val valueRange: LongRange = 1L..7L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DateObject)

        override fun deriveValueFrom(temporal: Temporal): Long {
            return temporal.get(DateObject).dayOfWeek.number.toLong()
        }
    }

    /**
     * The day of the week in the month, such as `2` if this is the 2nd Monday of the month.
     */
    object DayOfWeekInMonth : DateProperty(), DerivableNumberProperty {
        override val valueRange: LongRange get() = 1L..7L

        override fun isDerivableFrom(temporal: Temporal): Boolean = temporal.has(DayOfMonth)

        override fun deriveValueFrom(temporal: Temporal): Long {
            return dayOfWeekInMonth(temporal.get(DayOfMonth).toInt()).toLong()
        }
    }

    /**
     * Does this represent the "far past" sentinel value, which should treated as unbounded (in ISO-8601 parlance,
     * "open") in the context of an interval.
     */
    object IsFarPast : DateProperty(), BooleanProperty

    /**
     * Does this represent the "far future" sentinel value, which should treated as unbounded (in ISO-8601 parlance,
     * "open") in the context of an interval.
     */
    object IsFarFuture : DateProperty(), BooleanProperty

    object DateObject : DateProperty(), ObjectProperty<Date>
}
