package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.parser.DateTimeParseResult
import dev.erikchristensen.islandtime.parser.DateTimeParser
import dev.erikchristensen.islandtime.parser.Iso8601

data class OffsetDateTime(
    val dateTime: DateTime,
    val timeOffset: TimeOffset
) {
    val date: Date get() = dateTime.date
    val time: Time get() = dateTime.time

    override fun toString() = buildString(MAX_OFFSET_DATE_TIME_STRING_LENGTH) {
        appendOffsetDateTime(this@OffsetDateTime)
    }
}

fun String.toOffsetDateTime() = toOffsetDateTime(Iso8601.Extended.OFFSET_DATE_TIME_PARSER)

fun String.toOffsetDateTime(parser: DateTimeParser): OffsetDateTime {
    val result = parser.parse(this)
    return result.toOffsetDateTime()
}

internal fun DateTimeParseResult.toOffsetDateTime(): OffsetDateTime {
    val dateTime = this.toDateTime()
    val timeOffset = this.toTimeOffset()
    return OffsetDateTime(dateTime, timeOffset)
}

internal const val MAX_OFFSET_DATE_TIME_STRING_LENGTH = MAX_DATE_TIME_STRING_LENGTH + MAX_TIME_OFFSET_STRING_LENGTH

internal fun StringBuilder.appendOffsetDateTime(offsetDateTime: OffsetDateTime): StringBuilder {
    with(offsetDateTime) {
        appendDateTime(dateTime)

        if (timeOffset.isUtc) {
            append('Z')
        } else {
            appendTimeOffset(timeOffset)
        }
    }
    return this
}