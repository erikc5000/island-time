package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.MONTHS_IN_YEAR
import kotlin.jvm.JvmField

/**
 * A day-based period of time
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
        // TODO: Implement
        return super.hashCode()
    }

    override fun toString(): String {
        return buildString {
            if (isZero) {
                append("P0D")
            } else {
                append('P')

                if (years.value != 0) {
                    append(years)
                    append('Y')
                }

                if (months.value != 0) {
                    append(months)
                    append('M')
                }

                if (days.value != 0) {
                    append(days)
                    append('D')
                }
            }
        }
    }

    companion object {
        @JvmField
        val ZERO = Period()

        internal fun create(
            years: YearSpan = 0.years,
            months: MonthSpan = 0.months,
            days: DaySpan = 0.days
        ): Period {
            return if (years.value or months.value or years.value == 0) {
                ZERO
            } else {
                Period(years, months, days)
            }
        }
    }
}

fun periodOfZero() = Period.ZERO

fun periodOf(years: YearSpan, months: MonthSpan = 0.months, days: DaySpan = 0.days): Period {
    return Period.create(years, months, days)
}

fun periodOf(months: MonthSpan, days: DaySpan = 0.days): Period {
    return Period.create(months = months, days = days)
}

fun periodOf(days: DaySpan): Period {
    return Period.create(days = days)
}

val Period.totalMonths: MonthSpan get() = years.asMonths() + months
inline val Period.isZero: Boolean get() = this == Period.ZERO
val Period.isNegative get() = years.value < 0 || months.value < 0 || days.value < 0

fun Period.with(years: YearSpan) = Period.create(years, this.months, this.days)
fun Period.with(months: MonthSpan) = Period.create(this.years, months, this.days)
fun Period.with(days: DaySpan) = Period.create(this.years, this.months, days)

/**
 * Normalize the number of years and months such that 1 year, 15 months becomes 2 years, 3 months.
 */
fun Period.normalized(): Period {
    val monthTotal = totalMonths
    val newYears = monthTotal.asWholeYears()
    val newMonths = monthTotal % MONTHS_IN_YEAR
    return Period.create(newYears, newMonths, days)
}

fun YearSpan.asPeriod() = Period.create(years = this)
fun MonthSpan.asPeriod() = Period.create(months = this)
fun DaySpan.asPeriod() = Period.create(days = this)

operator fun Period.unaryMinus() = Period.create(-years, -months, -days)
operator fun Period.unaryPlus() = Period.create(+years, +months, +days)

operator fun Period.plus(yearsToAdd: YearSpan) = this.with(years + yearsToAdd)
operator fun Period.plus(monthsToAdd: MonthSpan) = this.with(months + monthsToAdd)
operator fun Period.plus(daysToAdd: DaySpan) = this.with(days + daysToAdd)

operator fun Period.minus(yearsToSubtract: YearSpan) = plus(-yearsToSubtract)
operator fun Period.minus(monthsToSubtract: MonthSpan) = plus(-monthsToSubtract)
operator fun Period.minus(daysToSubtract: DaySpan) = plus(-daysToSubtract)

operator fun Period.times(scalar: Int): Period {
    return if (this.isZero || scalar == 1) {
        this
    } else {
        Period.create(years * scalar, months * scalar, days * scalar)
    }
}

operator fun YearSpan.plus(months: MonthSpan) = Period.create(years = this, months = months)
operator fun YearSpan.plus(days: DaySpan) = Period.create(years = this, days = days)
operator fun YearSpan.plus(period: Period) = period.with(this + period.years)

operator fun YearSpan.minus(months: MonthSpan) = plus(-months)
operator fun YearSpan.minus(days: DaySpan) = plus(-days)
operator fun YearSpan.minus(period: Period) = plus(-period)

operator fun MonthSpan.plus(years: YearSpan) = Period.create(months = this, years = years)
operator fun MonthSpan.plus(days: DaySpan) = Period.create(months = this, days = days)
operator fun MonthSpan.plus(period: Period) = period.with(this + period.months)

operator fun MonthSpan.minus(years: YearSpan) = plus(-years)
operator fun MonthSpan.minus(days: DaySpan) = plus(-days)
operator fun MonthSpan.minus(period: Period) = plus(-period)

operator fun DaySpan.plus(years: YearSpan) = Period.create(days = this, years = years)
operator fun DaySpan.plus(months: MonthSpan) = Period.create(days = this, months = months)
operator fun DaySpan.plus(period: Period) = period.with(this + period.days)