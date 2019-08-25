package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
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
    val offset: TimeOffset
) {
    val date: Date get() = dateTime.date
    val time: Time get() = dateTime.time

    operator fun component1() = dateTime
    operator fun component2() = offset

    override fun toString() = buildString(MAX_OFFSET_DATE_TIME_STRING_LENGTH) {
        appendOffsetDateTime(this@OffsetDateTime)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is OffsetDateTime && dateTime == other.dateTime && offset == other.offset)
    }

    override fun hashCode(): Int {
        return 31 * dateTime.hashCode() + offset.hashCode()
    }

    companion object {
        val MIN = DateTime.MIN at TimeOffset.MAX
        val MAX = DateTime.MAX at TimeOffset.MIN

        operator fun invoke(date: Date, time: Time, offset: TimeOffset): OffsetDateTime {
            return OffsetDateTime(DateTime(date, time), offset)
        }
    }
}

infix fun DateTime.at(offset: TimeOffset) = OffsetDateTime(this, offset)

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

fun OffsetDateTime.with(newDateTime: DateTime) = OffsetDateTime(newDateTime, offset)
fun OffsetDateTime.with(newDate: Date) = OffsetDateTime(newDate, dateTime.time, offset)
fun OffsetDateTime.with(newTime: Time) = OffsetDateTime(dateTime.date, newTime, offset)

operator fun OffsetDateTime.plus(daysToAdd: LongDays) = this.with(dateTime + daysToAdd)
operator fun OffsetDateTime.plus(daysToAdd: IntDays) = this.with(dateTime + daysToAdd)
operator fun OffsetDateTime.plus(monthsToAdd: LongMonths) = this.with(dateTime + monthsToAdd)
operator fun OffsetDateTime.plus(monthsToAdd: IntMonths) = this.with(dateTime + monthsToAdd)
operator fun OffsetDateTime.plus(yearsToAdd: LongYears) = this.with(dateTime + yearsToAdd)
operator fun OffsetDateTime.plus(yearsToAdd: IntYears) = this.with(dateTime + yearsToAdd)

operator fun OffsetDateTime.plus(hoursToAdd: LongHours) = this.with(dateTime + hoursToAdd)
operator fun OffsetDateTime.plus(hoursToAdd: IntHours) = plus(hoursToAdd.toLong())
operator fun OffsetDateTime.plus(minutesToAdd: LongMinutes) = this.with(dateTime + minutesToAdd)
operator fun OffsetDateTime.plus(minutesToAdd: IntMinutes) = plus(minutesToAdd.toLong())
operator fun OffsetDateTime.plus(secondsToAdd: LongSeconds) = this.with(dateTime + secondsToAdd)
operator fun OffsetDateTime.plus(secondsToAdd: IntSeconds) = plus(secondsToAdd.toLong())
operator fun OffsetDateTime.plus(nanosecondsToAdd: LongNanoseconds) = this.with(dateTime + nanosecondsToAdd)
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
    val timeOffset = this.toTimeOffset()

    return if (dateTime != null && timeOffset != null) {
        OffsetDateTime(dateTime, timeOffset)
    } else {
        null
    }
}

internal const val MAX_OFFSET_DATE_TIME_STRING_LENGTH = MAX_DATE_TIME_STRING_LENGTH + MAX_TIME_OFFSET_STRING_LENGTH

internal fun StringBuilder.appendOffsetDateTime(offsetDateTime: OffsetDateTime): StringBuilder {
    with(offsetDateTime) {
        appendDateTime(dateTime)

        if (offset.isUtc) {
            append('Z')
        } else {
            appendTimeOffset(offset)
        }
    }
    return this
}