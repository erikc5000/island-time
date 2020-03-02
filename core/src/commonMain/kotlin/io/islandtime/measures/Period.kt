package io.islandtime.measures

import io.islandtime.base.*
import io.islandtime.base.throwUnsupportedTemporalPropertyException
import io.islandtime.internal.MONTHS_PER_YEAR
import io.islandtime.internal.timesExact
import io.islandtime.internal.toIntExact
import io.islandtime.parser.*
import io.islandtime.parser.throwParserPropertyResolutionException

/**
 * A date-based period of time, such as "2 years, 5 months, 16 days". Unlike [Duration], which uses exact increments,
 * a [Period] works with conceptual days, months, and years, ignoring daylight savings and length differences.
 *
 * @property years The number of years in this period.
 * @property months The number of months in this period.
 * @property days The number of days in this period.
 */
class Period private constructor(
    val years: IntYears = 0.years,
    val months: IntMonths = 0.months,
    val days: IntDays = 0.days
) : Temporal {

    /**
     * The total number of months in this period, including years.
     */
    val totalMonths: LongMonths
        get() = (years.toLongYears().inMonthsUnchecked.value + months.value).months

    /**
     * Check if this period has no length.
     */
    fun isZero(): Boolean = this == ZERO

    /**
     * Check if any component of this period is negative.
     */
    fun isNegative(): Boolean = years.value < 0 || months.value < 0 || days.value < 0

    /**
     * Reverse the sign of each component in the period.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun unaryMinus() = create(-years, -months, -days)

    /**
     * Add each component of another period to each component of this period.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun plus(other: Period) = create(
        years + other.years,
        months + other.months,
        days + other.days
    )

    /**
     * Subtract each component of another period from this period.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun minus(other: Period) = create(
        years - other.years,
        months - other.months,
        days - other.days
    )

    operator fun plus(years: IntYears) = copy(years = this.years + years)
    operator fun plus(months: IntMonths) = copy(months = this.months + months)
    operator fun plus(weeks: IntWeeks) = plus(weeks.toLongWeeks().inDaysUnchecked)
    operator fun plus(days: IntDays) = copy(days = this.days + days)

    operator fun plus(years: LongYears) = copy(years = (this.years.toLong() + years.value).toIntExact().years)
    operator fun plus(months: LongMonths) = copy(months = (this.months.toLong() + months.value).toIntExact().months)
    operator fun plus(weeks: LongWeeks) = plus(weeks.inDays)
    operator fun plus(days: LongDays) = copy(days = (this.days.toLong() + days.value).toIntExact().days)

    operator fun minus(years: IntYears) = copy(years = this.years - years)
    operator fun minus(months: IntMonths) = copy(months = this.months - months)
    operator fun minus(weeks: IntWeeks) = minus(weeks.toLongWeeks().inDaysUnchecked)
    operator fun minus(days: IntDays) = copy(days = this.days - days)

    operator fun minus(years: LongYears) = copy(years = (this.years.toLong() - years.value).toIntExact().years)
    operator fun minus(months: LongMonths) = copy(months = (this.months.toLong() - months.value).toIntExact().months)
    operator fun minus(weeks: LongWeeks) = minus(weeks.inDays)
    operator fun minus(days: LongDays) = copy(days = (this.days.toLong() - days.value).toIntExact().days)

    /**
     * Multiply each component of this period by a scalar value.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun times(scalar: Int): Period {
        return if (this.isZero() || scalar == 1) {
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
        val newYears = monthTotal.inYears.toIntYears()
        val newMonths = (monthTotal % MONTHS_PER_YEAR).toIntMonthsUnchecked()

        return if (newYears == years && newMonths == months) {
            this
        } else {
            create(newYears, newMonths, days)
        }
    }

    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            DurationProperty.IsZero,
            DurationProperty.Years,
            DurationProperty.Months,
            DurationProperty.Days -> true
            else -> false
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (property) {
            DurationProperty.IsZero -> isZero()
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            DurationProperty.Years -> years.value
            DurationProperty.Months -> months.value
            DurationProperty.Days -> days.value
            else -> throwUnsupportedTemporalPropertyException(property)
        }.toLong()
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
        return if (isZero()) {
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
 * Create a [Period].
 */
fun periodOf(years: IntYears, months: IntMonths = 0.months, days: IntDays = 0.days): Period {
    return Period.create(years, months, days)
}

/**
 * Create a [Period].
 */
fun periodOf(years: IntYears, days: IntDays) = Period.create(years = years, days = days)

/**
 * Create a [Period].
 */
fun periodOf(months: IntMonths, days: IntDays = 0.days) = Period.create(months = months, days = days)

/**
 * Create a [Period].
 * @throws ArithmeticException if overflow occurs
 */
fun periodOf(weeks: IntWeeks) = Period.create(days = weeks.inDays)

/**
 * Create a [Period].
 */
fun periodOf(days: IntDays) = Period.create(days = days)

/**
 * Convert [IntYears] into a [Period] with the same number of years
 */
fun IntYears.asPeriod() = Period.create(years = this)

/**
 * Convert [IntMonths] into a [Period] with the same number of months
 */
fun IntMonths.asPeriod() = Period.create(months = this)

/**
 * Convert [IntWeeks] into a [Period] with the same number of weeks
 * @throws ArithmeticException if the resulting [Period] would overflow
 */
fun IntWeeks.asPeriod() = Period.create(days = this.inDays)

/**
 * Convert [IntDays] into a [Period] with the same number of days
 */
fun IntDays.asPeriod() = Period.create(days = this)

/**
 * Convert [LongYears] into a [Period] with the same number of years
 * @throws ArithmeticException if the resulting [Period] would overflow
 */
fun LongYears.asPeriod() = this.toIntYears().asPeriod()

/**
 * Convert [LongMonths] into a [Period] with the same number of months
 * @throws ArithmeticException if the resulting [Period] would overflow
 */
fun LongMonths.asPeriod() = this.toIntMonths().asPeriod()

/**
 * Convert [LongWeeks] into a [Period] with the same number of weeks
 * @throws ArithmeticException if the resulting [Period] would overflow
 */
fun LongWeeks.asPeriod() = this.inDays.asPeriod()

/**
 * Convert [LongDays] into a [Period] with the same number of days
 * @throws ArithmeticException if the resulting [Period] would overflow
 */
fun LongDays.asPeriod() = this.toIntDays().asPeriod()

operator fun IntYears.plus(period: Period) = period.copy(years = this + period.years)
operator fun IntMonths.plus(period: Period) = period.copy(months = this + period.months)
operator fun IntWeeks.plus(period: Period) = this.toLongWeeks().inDaysUnchecked + period
operator fun IntDays.plus(period: Period) = period.copy(days = this + period.days)

operator fun LongYears.plus(period: Period) = period.copy(years = (this + period.years).toIntYears())
operator fun LongMonths.plus(period: Period) = period.copy(months = (this + period.months).toIntMonths())
operator fun LongWeeks.plus(period: Period) = this.inDays + period
operator fun LongDays.plus(period: Period) = period.copy(days = (this + period.days).toIntDays())

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

operator fun IntWeeks.minus(period: Period) = this.toLongWeeks().inDaysUnchecked - period

operator fun IntDays.minus(period: Period) = Period.create(
    -period.years,
    -period.months,
    this - period.days
)

operator fun LongYears.minus(period: Period) = Period.create(
    (this - period.years).toIntYears(),
    -period.months,
    -period.days
)

operator fun LongMonths.minus(period: Period) = Period.create(
    -period.years,
    (this - period.months).toIntMonths(),
    -period.days
)

operator fun LongWeeks.minus(period: Period) = this.inDays - period

operator fun LongDays.minus(period: Period) = Period.create(
    -period.years,
    -period.months,
    (this - period.days).toIntDays()
)

operator fun Int.times(period: Period) = period * this

fun String.toPeriod() = toPeriod(DateTimeParsers.Iso.PERIOD)

fun String.toPeriod(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): Period {
    val result = parser.parse(this, settings)
    return result.toPeriod() ?: throwParserPropertyResolutionException<Period>(this)
}

internal fun DateTimeParseResult.toPeriod(): Period? {
    val sign = this[DurationProperty.Sign]?.toInt() ?: 1
    val yearsValue = this[DurationProperty.Years]
    val monthsValue = this[DurationProperty.Months]
    val weeksValue = this[DurationProperty.Weeks]
    val daysValue = this[DurationProperty.Days]

    // Make sure we got at least one supported field out of the parser
    return if (yearsValue == null && monthsValue == null && weeksValue == null && daysValue == null) {
        null
    } else {
        val years = yearsValue?.toIntExact()?.timesExact(sign)?.years ?: 0.years
        val months = monthsValue?.toIntExact()?.timesExact(sign)?.months ?: 0.months
        var days = daysValue?.toIntExact()?.timesExact(sign)?.days ?: 0.days

        if (weeksValue != null) {
            days += weeksValue.toIntExact().timesExact(sign).weeks.inDays
        }

        periodOf(years, months, days)
    }
}