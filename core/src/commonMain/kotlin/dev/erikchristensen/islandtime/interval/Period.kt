package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR
import dev.erikchristensen.islandtime.parser.DateTimeField
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601

/**
 * A date-based period of time, such as "2 years, 5 months, 16 days". Unlike [Duration], which uses exact increments,
 * a [Period] works with conceptual days, months, and years, ignoring daylight savings and length differences.
 * @property years number of years in this period
 * @property months number of months in this period
 * @property days number of days in this period
 */
class Period private constructor(
    val years: IntYears = 0.years,
    val months: IntMonths = 0.months,
    val days: IntDays = 0.days
) {

    /**
     * The total number of months in this period, including years
     */
    val totalMonths: IntMonths get() = years + months

    /**
     * true if this period has no length
     */
    inline val isZero: Boolean get() = this == ZERO

    /**
     * true if any component of this period is negative
     */
    val isNegative get() = years.value < 0 || months.value < 0 || days.value < 0

    /**
     * Reverse the sign of each component in the period
     */
    operator fun unaryMinus() = create(-years, -months, -days)

    /**
     * Add each component of another period to each component of this period
     */
    operator fun plus(other: Period) = create(
        years + other.years,
        months + other.months,
        days + other.days
    )

    /**
     * Subtract each component of another period from this period
     */
    operator fun minus(other: Period) = create(
        years - other.years,
        months - other.months,
        days - other.days
    )

    operator fun plus(yearsToAdd: IntYears) = copy(years = years + yearsToAdd)
    operator fun plus(monthsToAdd: IntMonths) = copy(months = months + monthsToAdd)
    operator fun plus(daysToAdd: IntDays) = copy(days = days + daysToAdd)

    operator fun plus(yearsToAdd: LongYears) = copy(years = (years.toLong() + yearsToAdd).toInt())
    operator fun plus(monthsToAdd: LongMonths) = copy(months = (months.toLong() + monthsToAdd).toInt())
    operator fun plus(daysToAdd: LongDays) = copy(days = (days.toLong() + daysToAdd).toInt())

    operator fun minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)
    operator fun minus(monthsToSubtract: IntMonths) = plus(-monthsToSubtract)
    operator fun minus(daysToSubtract: IntDays) = plus(-daysToSubtract)

    operator fun minus(yearsToSubtract: LongYears) = plus(-yearsToSubtract)
    operator fun minus(monthsToSubtract: LongMonths) = plus(-monthsToSubtract)
    operator fun minus(daysToSubtract: LongDays) = plus(-daysToSubtract)

    /**
     * Multiply each component of this period by a scalar value
     */
    operator fun times(scalar: Int): Period {
        return if (this.isZero || scalar == 1) {
            this
        } else {
            create(years * scalar, months * scalar, days * scalar)
        }
    }
    
    operator fun component1() = years
    operator fun component2() = months
    operator fun component3() = days

    /**
     * Normalize the number of years and months such that "1 year, 15 months" becomes "2 years, 3 months".  Only the
     * months and years components are combined.  Days are never adjusted.
     */
    fun normalized(): Period {
        val monthTotal = totalMonths
        val newYears = monthTotal.toWholeYears()
        val newMonths = monthTotal % MONTHS_IN_YEAR
        return create(newYears, newMonths, days)
    }

    override fun equals(other: Any?): Boolean {
        return this === other ||
            (other is Period &&
                other.years == years &&
                other.months == months &&
                other.days == days)
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

    /**
     * Return a new Period, replacing the years, months, and days components with new values, as desired
     * @param years new years value
     * @param months new months value
     * @param days new days value
     * @return a new Period with the supplied values
     */
    fun copy(
        years: IntYears = this.years,
        months: IntMonths = this.months,
        days: IntDays = this.days
    ) = create(years, months, days)

    companion object {
        /**
         * A period of no length
         */
        val ZERO = Period()

        internal fun create(
            years: IntYears = 0.years,
            months: IntMonths = 0.months,
            days: IntDays = 0.days
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
 * Create a [Period]
 */
fun periodOf(years: IntYears, months: IntMonths = 0.months, days: IntDays = 0.days): Period {
    return Period.create(years, months, days)
}

/**
 * Create a [Period]
 */
fun periodOf(years: IntYears, days: IntDays): Period {
    return Period.create(years = years, days = days)
}

/**
 * Create a [Period]
 */
fun periodOf(months: IntMonths, days: IntDays = 0.days): Period {
    return Period.create(months = months, days = days)
}

/**
 * Create a [Period]
 */
fun periodOf(days: IntDays): Period {
    return Period.create(days = days)
}

/**
 * Convert a [IntYears] into a [Period] with the same number of years
 */
fun IntYears.asPeriod() = Period.create(years = this)

/**
 * Convert a [IntMonths] into a [Period] with the same number of months
 */
fun IntMonths.asPeriod() = Period.create(months = this)

/**
 * Convert a [IntDays] into a [Period] with the same number of days
 */
fun IntDays.asPeriod() = Period.create(days = this)

// TODO: Revisit including these since it may be better to make the conversion explicit
fun LongYears.asPeriod() = this.toInt().asPeriod()
fun LongMonths.asPeriod() = this.toInt().asPeriod()
fun LongDays.asPeriod() = this.toInt().asPeriod()

operator fun IntYears.plus(period: Period) = period.copy(years = this + period.years)
operator fun IntMonths.plus(period: Period) = period.copy(months = this + period.months)
operator fun IntDays.plus(period: Period) = period.copy(days = this + period.days)

operator fun LongYears.plus(period: Period) = period.copy(years = (this + period.years.toLong()).toInt())
operator fun LongMonths.plus(period: Period) = period.copy(months = (this + period.months.toLong()).toInt())
operator fun LongDays.plus(period: Period) = period.copy(days = (this + period.days.toLong()).toInt())

operator fun IntYears.minus(period: Period) = Period.create(
    this - period.years,
    -period.months,
    -period.days
)

operator fun IntMonths.minus(period: Period) = Period.create(
    -period.years,
    this - period.months,
    -period.days
)

operator fun IntDays.minus(period: Period) = Period.create(
    -period.years,
    -period.months,
    this - period.days
)

operator fun LongYears.minus(period: Period) = Period.create(
    (this - period.years.toLong()).toInt(),
    -period.months,
    -period.days
)

operator fun LongMonths.minus(period: Period) = Period.create(
    -period.years,
    (this - period.months.toLong()).toInt(),
    -period.days
)

operator fun LongDays.minus(period: Period) = Period.create(
    -period.years,
    -period.months,
    (this - period.days.toLong()).toInt()
)

fun String.toPeriod() = toPeriod(Iso8601.PERIOD_PARSER)

fun String.toPeriod(parser: DateTimeParser): Period {
    val result = parser.parse(this)
    return result.toPeriod()
}

internal fun DateTimeParseResult.toPeriod(): Period {
    val years = (this[DateTimeField.PERIOD_OF_YEARS]?.toInt() ?: 0).years
    val months = (this[DateTimeField.PERIOD_OF_MONTHS]?.toInt() ?: 0).months
    val days = (this[DateTimeField.PERIOD_OF_DAYS]?.toInt() ?: 0).days

    return periodOf(years, months, days)
}