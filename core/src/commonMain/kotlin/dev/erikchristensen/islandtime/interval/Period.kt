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
 * The total number of months in this period, including years
 */
val Period.totalMonths: IntMonths get() = years.asMonths() + months

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
fun Period.with(years: IntYears) = Period.create(years, this.months, this.days)

/**
 * Return a new period, replacing the months component with the provided value
 * @param months the new months value
 */
fun Period.with(months: IntMonths) = Period.create(this.years, months, this.days)

/**
 * Return a new period, replacing the days component with the provided value
 * @param days the new days value
 */
fun Period.with(days: IntDays) = Period.create(this.years, this.months, days)

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

operator fun Period.plus(yearsToAdd: IntYears) = this.with(years + yearsToAdd)
operator fun Period.plus(monthsToAdd: IntMonths) = this.with(months + monthsToAdd)
operator fun Period.plus(daysToAdd: IntDays) = this.with(days + daysToAdd)

operator fun Period.plus(yearsToAdd: LongYears) = this.with((years.toLong() + yearsToAdd).toInt())
operator fun Period.plus(monthsToAdd: LongMonths) = this.with((months.toLong() + monthsToAdd).toInt())
operator fun Period.plus(daysToAdd: LongDays) = this.with((days.toLong() + daysToAdd).toInt())

operator fun Period.minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)
operator fun Period.minus(monthsToSubtract: IntMonths) = plus(-monthsToSubtract)
operator fun Period.minus(daysToSubtract: IntDays) = plus(-daysToSubtract)

operator fun Period.minus(yearsToSubtract: LongYears) = plus(-yearsToSubtract)
operator fun Period.minus(monthsToSubtract: LongMonths) = plus(-monthsToSubtract)
operator fun Period.minus(daysToSubtract: LongDays) = plus(-daysToSubtract)

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

operator fun IntYears.plus(period: Period) = period.with(this + period.years)
operator fun IntMonths.plus(period: Period) = period.with(this + period.months)
operator fun IntDays.plus(period: Period) = period.with(this + period.days)

operator fun LongYears.plus(period: Period) = period.with((this + period.years.toLong()).toInt())
operator fun LongMonths.plus(period: Period) = period.with((this + period.months.toLong()).toInt())
operator fun LongDays.plus(period: Period) = period.with((this + period.days.toLong()).toInt())

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