package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.*

class RegionalDateTime private constructor(
    val dateTime: DateTime,
    val timeZone: TimeZone,
    val offset: UtcOffset
) {
    inline val date: Date get() = dateTime.date
    inline val time: Time get() = dateTime.time
    inline val hour: Int get() = dateTime.hour
    inline val minute: Int get() = dateTime.minute
    inline val second: Int get() = dateTime.second
    inline val nanoOfSecond: Int get() = dateTime.nanoOfSecond
    inline val month: Month get() = dateTime.month
    inline val dayOfWeek: DayOfWeek get() = dateTime.dayOfWeek
    inline val dayOfMonth: Int get() = dateTime.dayOfMonth
    inline val dayOfYear: Int get() = dateTime.dayOfYear
    inline val year: Int get() = dateTime.year
    inline val isInLeapYear: Boolean get() = dateTime.isInLeapYear
    inline val isLeapDay: Boolean get() = dateTime.isLeapDay
    inline val lengthOfMonth: IntDays get() = dateTime.lengthOfMonth
    inline val lengthOfYear: IntDays get() = dateTime.lengthOfYear
    inline val yearMonth: YearMonth get() = dateTime.yearMonth

    override fun equals(other: Any?): Boolean {
        return this === other || (other is RegionalDateTime &&
            dateTime == other.dateTime &&
            timeZone == other.timeZone &&
            offset == other.offset)
    }

    override fun hashCode(): Int {
        var result = dateTime.hashCode()
        result = 31 * result + timeZone.hashCode()
        result = 31 * result + offset.hashCode()
        return result
    }

    override fun toString(): String {
        return buildString(MAX_REGIONAL_DATE_TIME_STRING_LENGTH) {
            appendRegionalDateTime(this@RegionalDateTime)
        }
    }

    operator fun plus(yearsToAdd: LongYears) = resolveLocal(dateTime + yearsToAdd)
    operator fun plus(yearsToAdd: IntYears) = resolveLocal(dateTime + yearsToAdd)
    operator fun plus(monthsToAdd: LongMonths) = resolveLocal(dateTime + monthsToAdd)
    operator fun plus(monthsToAdd: IntMonths) = resolveLocal(dateTime + monthsToAdd)
    operator fun plus(daysToAdd: LongDays) = resolveLocal(dateTime + daysToAdd)
    operator fun plus(daysToAdd: IntDays) = resolveLocal(dateTime + daysToAdd)

    //operator fun minus(yearsToSubtract: LongYears)

    fun withEarlierOffsetAtOverlap(): RegionalDateTime {
        val transition = timeZone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val earlierOffset = transition.offsetBefore

            if (earlierOffset != offset) {
                return RegionalDateTime(dateTime, timeZone, earlierOffset)
            }
        }
        return this
    }

    fun withLaterOffsetAtOverlap(): RegionalDateTime {
        val transition = timeZone.rules.transitionAt(dateTime)

        if (transition?.isOverlap == true) {
            val laterOffset = transition.offsetAfter

            if (laterOffset != offset) {
                return RegionalDateTime(dateTime, timeZone, laterOffset)
            }
        }
        return this
    }

    private fun resolveLocal(dateTime: DateTime) = ofLocal(dateTime, timeZone, offset)
    private fun resolveInstant(instant: Instant) = ofInstant(instant, timeZone)

    companion object {

        operator fun invoke(
            year: Int,
            month: Month,
            day: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanoOfSecond: Int,
            timeZone: TimeZone
        ) = ofLocal(DateTime(year, month, day, hour, minute, second, nanoOfSecond), timeZone)

        operator fun invoke(
            year: Int,
            monthNumber: Int,
            day: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanoOfSecond: Int,
            timeZone: TimeZone
        ) = ofLocal(DateTime(year, monthNumber, day, hour, minute, second, nanoOfSecond), timeZone)

        operator fun invoke(
            year: Int,
            dayOfYear: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanoOfSecond: Int,
            timeZone: TimeZone
        ) = ofLocal(DateTime(year, dayOfYear, hour, minute, second, nanoOfSecond), timeZone)

        operator fun invoke(date: Date, time: Time, timeZone: TimeZone) = ofLocal(DateTime(date, time), timeZone)

        operator fun invoke(dateTime: DateTime, timeZone: TimeZone) = ofLocal(dateTime, timeZone)

        operator fun invoke(
            dateTime: DateTime,
            timeZone: TimeZone,
            preferredOffset: UtcOffset
        ) = ofLocal(dateTime, timeZone, preferredOffset)

        private fun ofLocal(
            dateTime: DateTime,
            timeZone: TimeZone,
            preferredOffset: UtcOffset? = null
        ): RegionalDateTime {
            val rules = timeZone.rules
            val validOffsets = rules.validOffsetsAt(dateTime)

            return when (validOffsets.size) {
                1 -> RegionalDateTime(dateTime, timeZone, validOffsets[0])
                0 -> {
                    val transition = rules.transitionAt(dateTime)
                    val adjustedDateTime = dateTime + transition!!.durationInSeconds
                    RegionalDateTime(adjustedDateTime, timeZone, transition.offsetAfter)
                }
                else -> {
                    val offset = if (preferredOffset != null && validOffsets.contains(preferredOffset)) {
                        preferredOffset
                    } else {
                        validOffsets[0]
                    }
                    RegionalDateTime(dateTime, timeZone, offset)
                }
            }
        }

        internal fun ofInstant(instant: Instant, timeZone: TimeZone): RegionalDateTime {
            val offset = timeZone.rules.offsetAt(instant)
            val dateTime = instant.toDateTime(offset)
            return RegionalDateTime(dateTime, timeZone, offset)
        }
    }
}

/**
 * Get the [RegionalDateTime] corresponding to a local date and time in a particular time zone.
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time falls within a
 * gap, it will adjusted forward by the length of the gap. If it falls within an overlap, the earlier offset will be
 * used.
 */
infix fun DateTime.at(timeZone: TimeZone) = RegionalDateTime(this, timeZone)

/**
 * Get the [RegionalDateTime] corresponding to an instant in a particular time zone
 */
infix fun Instant.at(timeZone: TimeZone) = RegionalDateTime.ofInstant(this, timeZone)

fun Date.atStartOfDay(timeZone: TimeZone): RegionalDateTime {
    val dateTime = this at Time.MIDNIGHT
    val transition = timeZone.rules.transitionAt(dateTime)

    return if (transition?.isGap == true) {
        RegionalDateTime(transition.dateTimeAfter, timeZone)
    } else {
        RegionalDateTime(dateTime, timeZone)
    }
}

fun RegionalDateTime.toOffsetDateTime() = OffsetDateTime(dateTime, offset)

fun Clock.now(): RegionalDateTime {
    return instant() at timeZone
}

internal const val MAX_REGIONAL_DATE_TIME_STRING_LENGTH =
    MAX_DATE_TIME_STRING_LENGTH + MAX_UTC_OFFSET_STRING_LENGTH + MAX_TIME_ZONE_STRING_LENGTH + 2

internal fun StringBuilder.appendRegionalDateTime(regionalDateTime: RegionalDateTime): StringBuilder {
    with(regionalDateTime) {
        appendDateTime(dateTime)
        appendUtcOffset(offset)
        append('[')
        append(timeZone)
        append(']')
    }
    return this
}