package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR

/**
 * A date-based period of time, such as "2 years, 5 months, 16 days". Unlike [Duration], which uses exact increments,
 * a [Period] works with conceptual days, months, and years, ignoring daylight savings and length differences.
 * @property years number of years in this period
 * @property months number of months in this period
 * @property days number of days in this period
 */
class Period private constructor(
    val years: YearSpan = 0.years,
    val months: MonthSpan = 0.months,
    val days: DaySpan = 0.days
) {

    override fun equals(other: Any?): Boolean {
        return other is Period &&
            other.years == years &&
            other.months == months &&
            other.days == days
    }

    override fun hashCode(): Int {
        var result = years.hashCode()
        result = 31 * result + months.hashCode()
        result = 31 * result + days.hashCode()
        return result
    }

    /**
     * Returns an ISO-8601 period representation, such as "P1Y10M3D"
     */
    override fun toString(): String {
        return if (isZero) {
            "P0D"
        } else {
            buildString {
                append('P')

                if (years.value != 0) {
                    append(years.value)
                    append('Y')
                }

                if (months.value != 0) {
                    append(months.value)
                    append('M')
                }

                if (days.value != 0) {
                    append(days.value)
                    append('D')
                }
            }
        }
    }

    companion object {
        /**
         * A period of no length
         */
        val ZERO = Period()

        internal fun create(
            years: YearSpan = 0.years,
            months: MonthSpan = 0.months,
            days: DaySpan = 0.days
        ): Period {
            return if (years.value or months.value or days.value == 0) {
                ZERO
            } else {
                Period(years, months, days)
            }
        }
    }
}

/**
 * A period of no length
 */
fun periodOfZero() = Period.ZERO

/**
 * Create a [Period]
 */
fun periodOf(years: YearSpan, months: MonthSpan = 0.months, days: DaySpan = 0.days): Period {
    return Period.create(years, months, days)
}

/**
 * Create a [Period]
 */
fun periodOf(years: YearSpan, days: DaySpan): Period {
    return Period.create(years = years, days = days)
}

/**
 * Create a [Period]
 */
fun periodOf(months: MonthSpan, days: DaySpan = 0.days): Period {
    return Period.create(months = months, days = days)
}

/**
 * Create a [Period]
 */
fun periodOf(days: DaySpan): Period {
    return Period.create(days = days)
}

/**
 * The total number of months in this period, including years
 */
val Period.totalMonths: MonthSpan get() = years.asMonths() + months

/**
 * true if this period has no length
 */
inline val Period.isZero: Boolean get() = this == Period.ZERO

/**
 * true if any component of this period is negative
 */
val Period.isNegative get() = years.value < 0 || months.value < 0 || days.value < 0

/**
 * Return a new period, replacing the years component with the provided value
 * @param years the new years value
 */
fun Period.with(years: YearSpan) = Period.create(years, this.months, this.days)

/**
 * Return a new period, replacing the months component with the provided value
 * @param months the new months value
 */
fun Period.with(months: MonthSpan) = Period.create(this.years, months, this.days)

/**
 * Return a new period, replacing the days component with the provided value
 * @param days the new days value
 */
fun Period.with(days: DaySpan) = Period.create(this.years, this.months, days)

/**
 * Normalize the number of years and months such that "1 year, 15 months" becomes "2 years, 3 months".  Only the
 * months and years components are combined.  Days are never adjusted.
 */
fun Period.normalized(): Period {
    val monthTotal = totalMonths
    val newYears = (monthTotal.value / MONTHS_IN_YEAR).years
    val newMonths = monthTotal % MONTHS_IN_YEAR
    return Period.create(newYears, newMonths, days)
}

/**
 * Convert a [YearSpan] into a [Period] with the same number of years
 */
fun YearSpan.asPeriod() = Period.create(years = this)

/**
 * Convert a [MonthSpan] into a [Period] with the same number of months
 */
fun MonthSpan.asPeriod() = Period.create(months = this)

/**
 * Convert a [DaySpan] into a [Period] with the same number of days
 */
fun DaySpan.asPeriod() = Period.create(days = this)

// TODO: Revisit including these since it may be better to make the conversion explicit
fun LongYearSpan.asPeriod() = this.toInt().asPeriod()
fun LongMonthSpan.asPeriod() = this.toInt().asPeriod()
fun LongDaySpan.asPeriod() = this.toInt().asPeriod()

/**
 * Reverse the sign of each component in the period
 */
operator fun Period.unaryMinus() = Period.create(-years, -months, -days)

/**
 * Returns the period
 */
operator fun Period.unaryPlus() = this

/**
 * Add each component of another period to each component of this period
 */
operator fun Period.plus(other: Period) = Period.create(
    years + other.years,
    months + other.months,
    days + other.days
)

/**
 * Subtract each component of another period from this period
 */
operator fun Period.minus(other: Period) = Period.create(
    years - other.years,
    months - other.months,
    days - other.days
)

operator fun Period.plus(yearsToAdd: YearSpan) = this.with(years + yearsToAdd)
operator fun Period.plus(monthsToAdd: MonthSpan) = this.with(months + monthsToAdd)
operator fun Period.plus(daysToAdd: DaySpan) = this.with(days + daysToAdd)

operator fun Period.plus(yearsToAdd: LongYearSpan) = this.with((years.toLong() + yearsToAdd).toInt())
operator fun Period.plus(monthsToAdd: LongMonthSpan) = this.with((months.toLong() + monthsToAdd).toInt())
operator fun Period.plus(daysToAdd: LongDaySpan) = this.with((days.toLong() + daysToAdd).toInt())

operator fun Period.minus(yearsToSubtract: YearSpan) = plus(-yearsToSubtract)
operator fun Period.minus(monthsToSubtract: MonthSpan) = plus(-monthsToSubtract)
operator fun Period.minus(daysToSubtract: DaySpan) = plus(-daysToSubtract)

operator fun Period.minus(yearsToSubtract: LongYearSpan) = plus(-yearsToSubtract)
operator fun Period.minus(monthsToSubtract: LongMonthSpan) = plus(-monthsToSubtract)
operator fun Period.minus(daysToSubtract: LongDaySpan) = plus(-daysToSubtract)

/**
 * Multiply each component of this period by a scalar value
 */
operator fun Period.times(scalar: Int): Period {
    return if (this.isZero || scalar == 1) {
        this
    } else {
        Period.create(years * scalar, months * scalar, days * scalar)
    }
}

operator fun YearSpan.plus(period: Period) = period.with(this + period.years)
operator fun MonthSpan.plus(period: Period) = period.with(this + period.months)
operator fun DaySpan.plus(period: Period) = period.with(this + period.days)

operator fun LongYearSpan.plus(period: Period) = period.with((this + period.years.toLong()).toInt())
operator fun LongMonthSpan.plus(period: Period) = period.with((this + period.months.toLong()).toInt())
operator fun LongDaySpan.plus(period: Period) = period.with((this + period.days.toLong()).toInt())

operator fun YearSpan.minus(period: Period) = Period.create(
    this - period.years,
    -period.months,
    -period.days
)

operator fun MonthSpan.minus(period: Period) = Period.create(
    -period.years,
    this - period.months,
    -period.days
)

operator fun DaySpan.minus(period: Period) = Period.create(
    -period.years,
    -period.months,
    this - period.days
)