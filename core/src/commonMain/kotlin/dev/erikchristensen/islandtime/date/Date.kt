package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.internal.*
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.*

class Date(
    val year: Int,
    val month: Month,
    private val day: Int
) : Comparable<Date> {

    init {
        checkYear(year)
        require(day in month.dayRangeIn(year)) { "The day '$day' doesn't exist in $month of $year" }
    }

    /**
     * The day of the month
     */
    val dayOfMonth: Int get() = day

    operator fun component1() = year
    operator fun component2() = month
    operator fun component3() = day

    override fun compareTo(other: Date): Int {
        val yearDiff = year - other.year

        return if (yearDiff != 0) {
            yearDiff
        } else {
            val monthDiff = month.ordinal - other.month.ordinal

            if (monthDiff != 0) {
                monthDiff
            } else {
                day - other.day
            }
        }
    }

    override fun toString() = buildString(MAX_DATE_STRING_LENGTH) { appendDate(this@Date) }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is Date && year == other.year && month == other.month && day == other.day)
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month.hashCode()
        result = 31 * result + day
        return result
    }

    /**
     * Return a new Date, replacing the year, month, and day of month with new values, as desired
     */
    fun copy(
        year: Int = this.year,
        month: Month = this.month,
        dayOfMonth: Int = this.day
    ) = Date(year, month, dayOfMonth)

    /**
     * Return a new Date, replacing the year and day of the year with new values, as desired
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear
    ) = Date(year, dayOfYear)

    companion object {
        val MIN = Date(Year.MIN_VALUE, Month.MIN, 1)
        val MAX = Date(Year.MAX_VALUE, Month.MAX, Month.MAX.lastDayIn(Year.MAX_VALUE))

        //
        // Adapted from https://github.com/ThreeTen/threetenbp/blob/master/src/main/java/org/threeten/bp/LocalDate.java
        //
        fun ofUnixEpochDays(unixEpochDays: LongDays): Date {
            var zeroDay = unixEpochDays.value + DAYS_FROM_0000_TO_1970
            // find the march-based year
            zeroDay -= 60  // adjust to 0000-03-01 so leap day is at end of four year cycle
            var adjust: Long = 0
            if (zeroDay < 0) {
                // adjust negative years to positive for calculation
                val adjustCycles = (zeroDay + 1) / DAYS_PER_400_YEAR_CYCLE - 1
                adjust = adjustCycles * 400
                zeroDay += -adjustCycles * DAYS_PER_400_YEAR_CYCLE
            }
            var yearEst = (400 * zeroDay + 591) / DAYS_PER_400_YEAR_CYCLE
            var doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400)
            if (doyEst < 0) {
                // fix estimate
                yearEst--
                doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400)
            }
            yearEst += adjust  // reset any negative year
            val marchDoy0 = doyEst.toInt()

            // convert march-based values back to january-based
            val marchMonth0 = (marchDoy0 * 5 + 2) / 153
            val month = (marchMonth0 + 2) % 12 + 1
            val dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1
            yearEst += (marchMonth0 / 10).toLong()

            return Date(yearEst.toInt(), month.toMonth(), dom)
        }
    }
}

/**
 * Create a [Date] from a year and day of year
 * @param year the year
 * @param dayOfYear the day of the calendar year
 * @return a new [Date]
 */
@Suppress("FunctionName")
fun Date(year: Int, dayOfYear: Int): Date {
    checkYear(year)
    require(dayOfYear in 1..366) { "'$dayOfYear' is not a valid day of the year" }

    if (dayOfYear > Year(year).length.value) {
        throw DateTimeException("Day of year '$dayOfYear' is invalid since '$year' isn't a leap year")
    }

    val testMonth = ((dayOfYear - 1) / 31 + 1).toMonth()
    val month = if (dayOfYear > testMonth.lastDayOfYearIn(year)) testMonth + 1.months else testMonth
    val dayOfMonth = dayOfYear - month.firstDayOfYearIn(year) + 1
    return Date(year, month, dayOfMonth)
}

/**
 * The day of the week
 */
val Date.dayOfWeek: DayOfWeek
    get() {
        val zeroIndexedDayOfWeek = (this.asUnixEpochDays().value + 3) floorMod 7
        return DayOfWeek.values()[zeroIndexedDayOfWeek.toInt()]
    }

/**
 * The day of the year
 */
val Date.dayOfYear: Int get() = month.firstDayOfYearIn(year) + dayOfMonth - 1

/**
 * true if this date falls within a leap year
 */
val Date.isInLeapYear: Boolean get() = Year(year).isLeap

/**
 * true if this is a leap day
 */
val Date.isLeapDay: Boolean get() = isLeapDay(month, dayOfMonth)

/**
 * The length of this date's month in days
 */
val Date.lengthOfMonth: IntDays get() = month.lengthIn(year)

/**
 * The length of this date's year in days
 */
val Date.lengthOfYear: IntDays get() = Year(year).length

operator fun Date.plus(daysToAdd: LongDays): Date {
    return if (daysToAdd.value == 0L) {
        this
    } else {
        Date.ofUnixEpochDays(this.asUnixEpochDays() + daysToAdd)
    }
}

operator fun Date.plus(daysToAdd: IntDays) = plus(daysToAdd.toLong())

operator fun Date.plus(monthsToAdd: IntMonths): Date {
    return if (monthsToAdd.value == 0) {
        this
    } else {
        val monthsRelativeTo0 = year * MONTHS_IN_YEAR + month.ordinal
        val newMonthsRelativeTo0 = monthsRelativeTo0 + monthsToAdd.value
        val newYear = newMonthsRelativeTo0 / MONTHS_IN_YEAR
        val newMonth = Month.values()[newMonthsRelativeTo0 % MONTHS_IN_YEAR]

        Date(newYear, newMonth, dayOfMonth.coerceAtMost(newMonth.lastDayIn(newYear)))
    }
}

operator fun Date.plus(monthsToAdd: LongMonths) = plus(monthsToAdd.toInt())

operator fun Date.plus(yearsToAdd: IntYears): Date {
    return if (yearsToAdd.value == 0) {
        this
    } else {
        val newYear = year + yearsToAdd.value
        copy(year = newYear, dayOfMonth = dayOfMonth.coerceAtMost(month.lastDayIn(newYear)))
    }
}

operator fun Date.plus(yearsToAdd: LongYears) = plus(yearsToAdd.toInt())

operator fun Date.minus(daysToSubtract: LongDays) = plus(-daysToSubtract)
operator fun Date.minus(daysToSubtract: IntDays) = plus(-(daysToSubtract.toLong()))
operator fun Date.minus(monthsToSubtract: LongMonths) = plus(-monthsToSubtract)
operator fun Date.minus(monthsToSubtract: IntMonths) = plus(-monthsToSubtract)
operator fun Date.minus(yearsToSubtract: LongYears) = plus(-yearsToSubtract)
operator fun Date.minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)

operator fun Date.rangeTo(other: Date) = DateRange(this, other)

fun Date.atStartOfDay() = DateTime(this, Time.MIDNIGHT)

//
// Adapted from https://github.com/ThreeTen/threetenbp/blob/master/src/main/java/org/threeten/bp/LocalDate.java
//
fun Date.asUnixEpochDays(): LongDays {
    var total = DAYS_IN_COMMON_YEAR * year

    if (year >= 0) {
        total += (year + 3) / 4 - (year + 99) / 100 + (year + 399) / 400
    } else {
        total -= year / -4 - year / -100 + year / -400
    }

    total += ((367 * month.number - 362) / MONTHS_IN_YEAR)
    total += dayOfMonth - 1

    if (month.number > 2) {
        total -= if (isInLeapYear) 1 else 2
    }

    return (total - DAYS_FROM_0000_TO_1970).days
}

fun String.toDate() = toDate(Iso8601.Extended.CALENDAR_DATE_PARSER)

fun String.toDate(parser: DateTimeParser): Date {
    val result = parser.parse(this)
    return result.toDate() ?: raiseParserFieldResolutionException("Date", this)
}

internal fun DateTimeParseResult.toDate(): Date? {
    val year = this[DateTimeField.YEAR]

    if (year != null) {
        val month = this[DateTimeField.MONTH_OF_YEAR]
        val dayOfMonth = this[DateTimeField.DAY_OF_MONTH]

        if (month != null && dayOfMonth != null) {
            return Date(year.toInt(), month.toInt().toMonth(), dayOfMonth.toInt())
        }

        val dayOfYear = this[DateTimeField.DAY_OF_YEAR]

        if (dayOfYear != null) {
            return Date(year.toInt(), dayOfYear.toInt())
        }
    }

    return null
}

fun periodBetween(start: Date, endExclusive: Date): Period {
    var totalMonths = endExclusive.asMonthsRelativeToYear0() - start.asMonthsRelativeToYear0()
    val dayDiff = (endExclusive.dayOfMonth - start.dayOfMonth).days

    val days = when {
        totalMonths > 0 && dayDiff.value < 0 -> {
            totalMonths--
            val testDate = start + totalMonths.months
            daysBetween(testDate, endExclusive).toInt()
        }
        totalMonths < 0 && dayDiff.value > 0 -> {
            totalMonths++
            dayDiff - endExclusive.lengthOfMonth
        }
        else -> dayDiff
    }
    val years = (totalMonths / MONTHS_IN_YEAR).years.toInt()
    val months = (totalMonths % MONTHS_IN_YEAR).months.toInt()

    return periodOf(years, months, days)
}

fun daysBetween(start: Date, endExclusive: Date): LongDays {
    return endExclusive.asUnixEpochDays() - start.asUnixEpochDays()
}

fun monthsBetween(start: Date, endExclusive: Date): IntMonths {
    val startDays = start.asMonthsRelativeToYear0() * 32L + start.dayOfMonth
    val endDays = endExclusive.asMonthsRelativeToYear0() * 32L + endExclusive.dayOfMonth
    return ((endDays - startDays) / 32).toInt().months
}

fun yearsBetween(start: Date, endExclusive: Date): IntYears {
    return monthsBetween(start, endExclusive).toWholeYears()
}

internal const val MAX_DATE_STRING_LENGTH = 10

internal fun StringBuilder.appendDate(date: Date): StringBuilder {
    with(date) {
        appendZeroPadded(year, 4)
        append('-')
        appendZeroPadded(month.number, 2)
        append('-')
        appendZeroPadded(dayOfMonth, 2)
    }
    return this
}

private fun Date.asMonthsRelativeToYear0() = year * 12L + month.ordinal