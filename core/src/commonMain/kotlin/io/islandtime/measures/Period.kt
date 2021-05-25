package io.islandtime.measures

import dev.erikchristensen.javamath2kmp.timesExact
import io.islandtime.base.DateTimeField
import io.islandtime.internal.MONTHS_PER_YEAR
import io.islandtime.parser.*
import io.islandtime.serialization.PeriodSerializer
import kotlinx.serialization.Serializable

/**
 * A date-based period of time, such as "2 years, 5 months, 16 days". Unlike [Duration], which uses exact increments,
 * a [Period] works with conceptual days, months, and years, ignoring daylight savings and length differences.
 *
 * @property years The number of years in this period.
 * @property months The number of months in this period.
 * @property days The number of days in this period.
 */
@Serializable(with = PeriodSerializer::class)
class Period private constructor(
    val years: Years = 0.years,
    val months: Months = 0.months,
    val days: Days = 0.days
) {

    /**
     * The total number of months in this period, including years.
     */
    val totalMonths: Months get() = years + months

    /**
     * Checks if this period is zero.
     */
    fun isZero(): Boolean = this == ZERO

    /**
     * Checks if any component of this period is negative.
     */
    fun isNegative(): Boolean = years.value < 0 || months.value < 0 || days.value < 0

    /**
     * Reverses the sign of each component in the period.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun unaryMinus(): Period = create(-years, -months, -days)

    /**
     * Adds each component of another period to each component of this period.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun plus(other: Period): Period = create(
        years + other.years,
        months + other.months,
        days + other.days
    )

    /**
     * Subtracts each component of another period from this period.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun minus(other: Period): Period = create(
        years - other.years,
        months - other.months,
        days - other.days
    )

    operator fun plus(years: Years): Period = copy(years = this.years + years)
    operator fun plus(months: Months): Period = copy(months = this.months + months)
    operator fun plus(weeks: Weeks): Period = plus(weeks.inDays)
    operator fun plus(days: Days): Period = copy(days = this.days + days)

    operator fun minus(years: Years): Period = copy(years = this.years - years)
    operator fun minus(months: Months): Period = copy(months = this.months - months)
    operator fun minus(weeks: Weeks): Period = minus(weeks.inDays)
    operator fun minus(days: Days): Period = copy(days = this.days - days)

    /**
     * Multiplies each component of this period by a scalar value.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun times(scalar: Int): Period = times(scalar.toLong())

    /**
     * Multiplies each component of this period by a scalar value.
     * @throws ArithmeticException if overflow occurs
     */
    operator fun times(scalar: Long): Period {
        return if (this.isZero() || scalar == 1L) {
            this
        } else {
            create(years * scalar, months * scalar, days * scalar)
        }
    }

    operator fun component1(): Years = years
    operator fun component2(): Months = months
    operator fun component3(): Days = days

    /**
     * Normalizes the number of years and months such that "1 year, 15 months" becomes "2 years, 3 months". Only the
     * months and years components are combined. Days are never adjusted.
     */
    fun normalized(): Period {
        val monthTotal = totalMonths
        val newYears = monthTotal.inWholeYears
        val newMonths = monthTotal % MONTHS_PER_YEAR

        return if (newYears == years && newMonths == months) {
            this
        } else {
            create(newYears, newMonths, days)
        }
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
     * Returns an ISO-8601 duration representation, such as "P1Y10M3D".
     */
    override fun toString(): String {
        return if (isZero()) {
            "P0D"
        } else {
            buildString {
                append('P')

                if (years.value != 0L) {
                    append(years.value)
                    append('Y')
                }

                if (months.value != 0L) {
                    append(months.value)
                    append('M')
                }

                if (days.value != 0L) {
                    append(days.value)
                    append('D')
                }
            }
        }
    }

    /**
     * Returns a new Period, replacing the years, months, and days components with new values, as desired
     * @param years new years value
     * @param months new months value
     * @param days new days value
     * @return a new Period with the supplied values
     */
    fun copy(
        years: Years = this.years,
        months: Months = this.months,
        days: Days = this.days
    ): Period = create(years, months, days)

    companion object {
        /**
         * A [Period] of zero length.
         */
        val ZERO: Period = Period()

        internal fun create(
            years: Years = 0.years,
            months: Months = 0.months,
            days: Days = 0.days
        ): Period {
            return if (years.value or months.value or days.value == 0L) {
                ZERO
            } else {
                Period(years, months, days)
            }
        }
    }
}

/**
 * Creates a [Period].
 */
fun periodOf(years: Years, months: Months = 0.months, days: Days = 0.days): Period {
    return Period.create(years, months, days)
}

/**
 * Creates a [Period].
 */
fun periodOf(years: Years, days: Days): Period = Period.create(years = years, days = days)

/**
 * Creates a [Period].
 */
fun periodOf(months: Months, days: Days = 0.days): Period = Period.create(months = months, days = days)

/**
 * Creates a [Period].
 * @throws ArithmeticException if overflow occurs
 */
fun periodOf(weeks: Weeks): Period = Period.create(days = weeks.inDays)

/**
 * Creates a [Period].
 */
fun periodOf(days: Days): Period = Period.create(days = days)

/**
 * Converts this duration into a [Period] with the same number of years.
 */
fun Years.asPeriod(): Period = Period.create(years = this)

/**
 * Converts this duration into a [Period] with the same number of months.
 */
fun Months.asPeriod(): Period = Period.create(months = this)

/**
 * Converts this duration into a [Period] with the same number of weeks.
 * @throws ArithmeticException if the resulting [Period] would overflow
 */
fun Weeks.asPeriod(): Period = Period.create(days = this.inDays)

/**
 * Converts this duration into a [Period] with the same number of days.
 */
fun Days.asPeriod(): Period = Period.create(days = this)

operator fun Years.plus(period: Period): Period = period.copy(years = this + period.years)
operator fun Months.plus(period: Period): Period = period.copy(months = this + period.months)
operator fun Weeks.plus(period: Period): Period = this.inDays + period
operator fun Days.plus(period: Period): Period = period.copy(days = this + period.days)

operator fun Years.minus(period: Period): Period = Period.create(
    this - period.years,
    -period.months,
    -period.days
)

operator fun Months.minus(period: Period): Period = Period.create(
    -period.years,
    this - period.months,
    -period.days
)

operator fun Weeks.minus(period: Period): Period = this.inDays - period

operator fun Days.minus(period: Period): Period = Period.create(
    -period.years,
    -period.months,
    this - period.days
)

operator fun Int.times(period: Period): Period = period * this
operator fun Long.times(period: Period): Period = period * this

fun String.toPeriod(): Period = toPeriod(DateTimeParsers.Iso.PERIOD)

fun String.toPeriod(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): Period {
    val result = parser.parse(this, settings)
    return result.toPeriod() ?: throwParserFieldResolutionException<Period>(this)
}

internal fun DateTimeParseResult.toPeriod(): Period? {
    val sign = fields[DateTimeField.PERIOD_SIGN]?.toInt() ?: 1
    val yearsValue = fields[DateTimeField.PERIOD_OF_YEARS]
    val monthsValue = fields[DateTimeField.PERIOD_OF_MONTHS]
    val weeksValue = fields[DateTimeField.PERIOD_OF_WEEKS]
    val daysValue = fields[DateTimeField.PERIOD_OF_DAYS]

    // Make sure we got at least one supported field out of the parser
    return if (yearsValue == null && monthsValue == null && weeksValue == null && daysValue == null) {
        null
    } else {
        val years = yearsValue?.timesExact(sign)?.years ?: 0.years
        val months = monthsValue?.timesExact(sign)?.months ?: 0.months
        var days = daysValue?.timesExact(sign)?.days ?: 0.days

        if (weeksValue != null) {
            days += weeksValue.timesExact(sign).weeks
        }

        periodOf(years, months, days)
    }
}
