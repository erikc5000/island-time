package dev.erikchristensen.islandtime.date

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.parser.*
import dev.erikchristensen.islandtime.internal.*
import dev.erikchristensen.islandtime.interval.*
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

data class Date(
    val year: Int,
    val month: Month,
    private val day: Int
) : Comparable<Date> {

    /**
     * The day of the month
     */
    val dayOfMonth: Int get() = day

    init {
        require(isValidYear(year)) { "The year '$year' is outside the supported range" }
        require(day in month.dayRangeIn(year)) { "The day '$day' doesn't exist in $month of $year" }
    }

    override fun compareTo(other: Date): Int {
        val yearDiff = year.compareTo(other.year)

        return if (yearDiff != 0) {
            yearDiff
        } else {
            val monthDiff = month.compareTo(other.month)

            if (monthDiff != 0) {
                monthDiff
            } else {
                day.compareTo(other.day)
            }
        }
    }

    override fun toString() = buildString(MAX_DATE_STRING_LENGTH) { appendDate(this@Date) }

    companion object {
        @JvmField
        val MIN = Date(Year.MIN_VALUE, Month.MIN, 1)

        @JvmField
        val MAX = Date(Year.MAX_VALUE, Month.MAX, Month.MAX.lastDayIn(Year.MAX_VALUE))

        //
        // Adapted from https://github.com/ThreeTen/threetenbp/blob/master/src/main/java/org/threeten/bp/LocalDate.java
        //
        @JvmStatic
        fun ofUnixEpochDays(unixEpochDays: LongDaySpan): Date {
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
val Date.isInLeapYear: Boolean
    get() = isLeapYear(
        year
    )

/**
 * true if this is a leap day
 */
val Date.isLeapDay: Boolean
    get() = isLeapDay(
        month,
        dayOfMonth
    )

operator fun Date.plus(daysToAdd: LongDaySpan): Date {
    return if (daysToAdd.value == 0L) {
        this
    } else {
        Date.ofUnixEpochDays(this.asUnixEpochDays() + daysToAdd)
    }
}

operator fun Date.plus(daysToAdd: DaySpan) = plus(daysToAdd.toLong())

operator fun Date.plus(monthsToAdd: MonthSpan): Date {
    return if (monthsToAdd.value == 0) {
        this
    } else {
        val monthsRelativeTo0 = year * MONTHS_IN_YEAR + month.ordinal
        val newMonthsRelativeTo0 = monthsRelativeTo0 + monthsToAdd.value
        val newYear = newMonthsRelativeTo0 / MONTHS_IN_YEAR
        val newMonth = Month.values()[newMonthsRelativeTo0 % MONTHS_IN_YEAR]

        Date(
            month = newMonth,
            day = dayOfMonth.coerceAtMost(newMonth.lastDayIn(newYear)),
            year = newYear
        )
    }
}

operator fun Date.plus(monthsToAdd: LongMonthSpan) = plus(monthsToAdd.toInt())

operator fun Date.plus(yearsToAdd: YearSpan): Date {
    return if (yearsToAdd.value == 0) {
        this
    } else {
        val newYear = year + yearsToAdd.value
        copy(day = dayOfMonth.coerceAtMost(month.lastDayIn(newYear)), year = newYear)
    }
}

operator fun Date.plus(yearsToAdd: LongYearSpan) = plus(yearsToAdd.toInt())

operator fun Date.minus(daysToSubtract: LongDaySpan) = plus(-daysToSubtract)
operator fun Date.minus(daysToSubtract: DaySpan) = plus(-(daysToSubtract.toLong()))
operator fun Date.minus(monthsToSubtract: LongMonthSpan) = plus(-monthsToSubtract)
operator fun Date.minus(monthsToSubtract: MonthSpan) = plus(-monthsToSubtract)
operator fun Date.minus(yearsToSubtract: LongYearSpan) = plus(-yearsToSubtract)
operator fun Date.minus(yearsToSubtract: YearSpan) = plus(-yearsToSubtract)

operator fun Date.rangeTo(other: Date) = DateRange(this, other)

fun Date.atStartOfDay() = DateTime(this, Time.MIDNIGHT)

//
// Adapted from https://github.com/ThreeTen/threetenbp/blob/master/src/main/java/org/threeten/bp/LocalDate.java
//
fun Date.asUnixEpochDays(): LongDaySpan {
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

fun String.toDate() = toDate(Iso8601.Extended.DATE_PARSER)

fun String.toDate(parser: DateTimeParser): Date {
    val result = parser.parse(this)
    return result.toDate()
}

internal fun DateTimeParseResult.toDate(): Date {
    val year = this[DateTimeField.YEAR]?.toInt()
        ?: throw DateTimeException("Missing year")
    val month = this[DateTimeField.MONTH_OF_YEAR]?.toInt()?.toMonth()
        ?: throw DateTimeException("Missing month")
    val day = this[DateTimeField.DAY_OF_MONTH]?.toInt()
        ?: throw DateTimeException("Missing day")

    return Date(year, month, day)
}

fun periodBetween(start: Date, endInclusive: Date): Period {
    val totalMonths = monthsBetween(start, endInclusive)
    val years = totalMonths.asWholeYears()
    val months = totalMonths % MONTHS_IN_YEAR

    val testDate = start + totalMonths
    val days = daysBetween(testDate, endInclusive)

    return periodOf(years, months, days.toInt())
}

fun daysBetween(start: Date, endInclusive: Date): LongDaySpan {
    return endInclusive.asUnixEpochDays() - start.asUnixEpochDays()
}

fun monthsBetween(start: Date, endInclusive: Date): MonthSpan {
    val yearsBetween = endInclusive.year - start.year
    val monthsBetween = yearsBetween * MONTHS_IN_YEAR + (endInclusive.month.ordinal - start.month.ordinal)

    // Deal with variable month lengths
    val coercedStartDay = start.dayOfMonth.coerceAtMost(endInclusive.month.lastDayIn(endInclusive.year))
    val monthAdjustment = if (endInclusive.dayOfMonth < coercedStartDay) -1 else 0
    return (monthsBetween + monthAdjustment).months
}

fun yearsBetween(start: Date, endInclusive: Date): YearSpan {
    return monthsBetween(start, endInclusive).asWholeYears()
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