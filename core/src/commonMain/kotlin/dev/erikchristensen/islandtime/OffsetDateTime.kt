package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.IntDays
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.parser.raiseParserFieldResolutionException

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
}

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