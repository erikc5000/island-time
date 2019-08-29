package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.date.asUnixEpochDays
import dev.erikchristensen.islandtime.interval.*
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.parser.raiseParserFieldResolutionException

/**
 * A calendar date and time combined with a fixed UTC offset
 */
class OffsetDateTime(
    val dateTime: DateTime,
    val offset: UtcOffset
) : Comparable<OffsetDateTime> {

    val date: Date get() = dateTime.date
    val time: Time get() = dateTime.time

    operator fun component1() = dateTime
    operator fun component2() = offset

    override fun toString() = buildString(MAX_OFFSET_DATE_TIME_STRING_LENGTH) {
        appendOffsetDateTime(this@OffsetDateTime)
    }

    override fun compareTo(other: OffsetDateTime): Int {
        return if (offset == other.offset) {
            dateTime.compareTo(other.dateTime)
        } else {
            val secondDiff = asUnixEpochSeconds().value.compareTo(other.asUnixEpochSeconds().value)

            if (secondDiff != 0) {
                secondDiff
            } else {
                val nanoDiff = nanoOfSecond - other.nanoOfSecond

                if (nanoDiff != 0) {
                    nanoDiff
                } else {
                    dateTime.compareTo(other.dateTime)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is OffsetDateTime && dateTime == other.dateTime && offset == other.offset)
    }

    override fun hashCode(): Int {
        return 31 * dateTime.hashCode() + offset.hashCode()
    }

    /**
     * Return a new [OffsetDateTime], replacing any of the components with new values
     */
    fun copy(
        year: Int = this.year,
        dayOfYear: Int = this.dayOfYear,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanoOfSecond: Int = this.nanoOfSecond
    ) = OffsetDateTime(date.copy(year, dayOfYear), time.copy(hour, minute, second, nanoOfSecond), offset)

    /**
     * Return a new OffsetDateTime, replacing any of the components with new values
     */
    fun copy(
        year: Int = this.year,
        month: Month = this.month,
        dayOfMonth: Int = this.dayOfMonth,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        nanoOfSecond: Int = this.nanoOfSecond
    ) = OffsetDateTime(date.copy(year, month, dayOfMonth), time.copy(hour, minute, second, nanoOfSecond), offset)

    companion object {
        val MIN = DateTime.MIN at UtcOffset.MAX
        val MAX = DateTime.MAX at UtcOffset.MIN

        /**
         * Create an [OffsetDateTime]
         */
        operator fun invoke(date: Date, time: Time, offset: UtcOffset): OffsetDateTime {
            return OffsetDateTime(DateTime(date, time), offset)
        }

        /**
         * Create an [OffsetDateTime]
         */
        operator fun invoke(
            year: Int,
            month: Month,
            dayOfMonth: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanoOfSecond: Int,
            offset: UtcOffset
        ): OffsetDateTime {
            return OffsetDateTime(
                DateTime(year, month, dayOfMonth, hour, minute, second, nanoOfSecond),
                offset
            )
        }
    }
}

infix fun DateTime.at(offset: UtcOffset) = OffsetDateTime(this, offset)

inline val OffsetDateTime.hour: Int get() = dateTime.hour
inline val OffsetDateTime.minute: Int get() = dateTime.minute
inline val OffsetDateTime.second: Int get() = dateTime.second
inline val OffsetDateTime.nanoOfSecond: Int get() = dateTime.nanoOfSecond
inline val OffsetDateTime.month: Month get() = dateTime.month
inline val OffsetDateTime.dayOfWeek: DayOfWeek get() = dateTime.dayOfWeek
inline val OffsetDateTime.dayOfMonth: Int get() = dateTime.dayOfMonth
inline val OffsetDateTime.dayOfYear: Int get() = dateTime.dayOfYear
inline val OffsetDateTime.year: Int get() = dateTime.year
inline val OffsetDateTime.isInLeapYear: Boolean get() = dateTime.isInLeapYear
inline val OffsetDateTime.isLeapDay: Boolean get() = dateTime.isLeapDay
inline val OffsetDateTime.lengthOfMonth: IntDays get() = dateTime.lengthOfMonth
inline val OffsetDateTime.lengthOfYear: IntDays get() = dateTime.lengthOfYear

fun OffsetDateTime.asUnixEpochSeconds(): LongSeconds {
    val epochDays = date.asUnixEpochDays()
    return epochDays + time.secondOfDay.seconds - offset.totalSeconds
}

operator fun OffsetDateTime.plus(daysToAdd: LongDays): OffsetDateTime {
    return if (daysToAdd == 0L.days) {
        this
    } else {
        (dateTime + daysToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(daysToAdd: IntDays): OffsetDateTime {
    return if (daysToAdd == 0.days) {
        this
    } else {
        (dateTime + daysToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(monthsToAdd: LongMonths): OffsetDateTime {
    return if (monthsToAdd == 0L.months) {
        this
    } else {
        (dateTime + monthsToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(monthsToAdd: IntMonths): OffsetDateTime {
    return if (monthsToAdd == 0.months) {
        this
    } else {
        (dateTime + monthsToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(yearsToAdd: LongYears): OffsetDateTime {
    return if (yearsToAdd == 0L.years) {
        this
    } else {
        (dateTime + yearsToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(yearsToAdd: IntYears): OffsetDateTime {
    return if (yearsToAdd == 0.years) {
        this
    } else {
        (dateTime + yearsToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(hoursToAdd: LongHours): OffsetDateTime {
    return if (hoursToAdd == 0L.hours) {
        this
    } else {
        (dateTime + hoursToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())

operator fun OffsetDateTime.plus(minutesToAdd: LongMinutes): OffsetDateTime {
    return if (minutesToAdd == 0L.minutes) {
        this
    } else {
        (dateTime + minutesToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())

operator fun OffsetDateTime.plus(secondsToAdd: LongSeconds): OffsetDateTime {
    return if (secondsToAdd == 0L.seconds) {
        this
    } else {
        (dateTime + secondsToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())

operator fun OffsetDateTime.plus(nanosecondsToAdd: LongNanoseconds): OffsetDateTime {
    return if (nanosecondsToAdd == 0L.nanoseconds) {
        this
    } else {
        (dateTime + nanosecondsToAdd) at offset
    }
}

operator fun OffsetDateTime.plus(nanosecondsToAdd: IntNanoseconds) = plus(nanosecondsToAdd.toLong())

operator fun OffsetDateTime.minus(daysToSubtract: LongDays) = plus(-daysToSubtract)
operator fun OffsetDateTime.minus(monthsToSubtract: IntMonths) = plus(-monthsToSubtract)
operator fun OffsetDateTime.minus(yearsToSubtract: IntYears) = plus(-yearsToSubtract)
operator fun OffsetDateTime.minus(hoursToSubtract: IntHours) = plus(-hoursToSubtract)
operator fun OffsetDateTime.minus(minutesToSubtract: LongMinutes) = plus(-minutesToSubtract)
operator fun OffsetDateTime.minus(minutesToSubtract: IntMinutes) = plus(-minutesToSubtract)
operator fun OffsetDateTime.minus(secondsToSubtract: LongSeconds) = plus(-secondsToSubtract)
operator fun OffsetDateTime.minus(secondsToSubtract: IntSeconds) = plus(-secondsToSubtract)
operator fun OffsetDateTime.minus(nanosecondsToSubtract: LongNanoseconds) = plus(-nanosecondsToSubtract)
operator fun OffsetDateTime.minus(nanosecondsToSubtract: IntNanoseconds) = plus(-nanosecondsToSubtract)

fun String.toOffsetDateTime() = toOffsetDateTime(Iso8601.Extended.OFFSET_DATE_TIME_PARSER)

fun String.toOffsetDateTime(parser: DateTimeParser): OffsetDateTime {
    val result = parser.parse(this)
    return result.toOffsetDateTime() ?: raiseParserFieldResolutionException("OffsetDateTime", this)
}

internal fun DateTimeParseResult.toOffsetDateTime(): OffsetDateTime? {
    val dateTime = this.toDateTime()
    val utcOffset = this.toUtcOffset()

    return if (dateTime != null && utcOffset != null) {
        OffsetDateTime(dateTime, utcOffset)
    } else {
        null
    }
}

internal const val MAX_OFFSET_DATE_TIME_STRING_LENGTH = MAX_DATE_TIME_STRING_LENGTH + MAX_UTC__OFFSET_STRING_LENGTH

internal fun StringBuilder.appendOffsetDateTime(offsetDateTime: OffsetDateTime): StringBuilder {
    with(offsetDateTime) {
        appendDateTime(dateTime)

        if (offset.isZero) {
            append('Z')
        } else {
            appendUtcOffset(offset)
        }
    }
    return this
}