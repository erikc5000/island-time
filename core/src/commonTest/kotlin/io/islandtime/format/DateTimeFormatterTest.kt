package io.islandtime.format

import io.islandtime.*
import io.islandtime.base.*
import io.islandtime.locale.localeOf
import io.islandtime.test.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("PrivatePropertyName")
class DateTimeFormatterTest : AbstractIslandTimeTest(
    testDateTimeFormatProvider = FakeDateTimeFormatProvider,
    testDateTimeTextProvider = FakeDateTimeTextProvider,
    testTimeZoneTextProvider = FakeTimeZoneTextProvider
) {
    @Test
    fun `can model an iso format`() {
        val formatter = dateTimeFormatter {
            year(4)
            +'-'
            monthNumber(2)
            onlyIf({ it.has(DateProperty.DayOfMonth) }) {
                +'-'
                dayOfMonth(2)

                onlyIf({ it.has(TimeProperty.HourOfDay) }) {
                    +'T'
                    hourOfDay(2)
                    onlyIf({ it.has(TimeProperty.MinuteOfHour) }) {
                        +':'
                        minuteOfHour(2)
                        onlyIf({ it.getOrElse(TimeProperty.SecondOfMinute) { 0L } != 0L }) {
                            +':'
                            // FIXME: Change to fractionalSecondOfMinute()
                            secondOfMinute(2)
                            onlyIf({ it.getOrElse(TimeProperty.NanosecondOfSecond) { 0L } != 0L }) {
                                +'.'
                                nanosecondOfSecond(3)
                            }
                        }
                    }
                }

                onlyIf({ it.has(UtcOffsetProperty.TotalSeconds) }) {
                    offset()
                }

                onlyIf({ it.getOrNull(TimeZoneProperty.TimeZone) is TimeZone.Region }) {
                    +'['
                    timeZoneId()
                    +']'
                }
            }
        }

        assertEquals(
            "2010-10-08",
            formatter.format(Date(2010, 10, 8))
        )
        assertEquals(
            "2010-10-08T12:00",
            formatter.format(Date(2010, 10, 8) at Time.NOON)
        )
        assertEquals(
            "2010-10-08T12:00-04:00[America/New_York]",
            formatter.format(
                Date(2010, 10, 8) at
                    Time.NOON at
                    TimeZone("America/New_York")
            )
        )
    }
}