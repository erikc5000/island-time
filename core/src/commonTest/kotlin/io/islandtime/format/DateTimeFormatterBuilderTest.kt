package io.islandtime.format

import io.islandtime.Date
import io.islandtime.Time
import io.islandtime.TimeZone
import io.islandtime.at
import io.islandtime.base.DateProperty
import io.islandtime.base.TimeProperty
import io.islandtime.base.TimeZoneProperty
import io.islandtime.base.UtcOffsetProperty
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeFormatterBuilderTest : AbstractIslandTimeTest() {
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
                        onlyIf({ it.has(TimeProperty.SecondOfMinute) && it.get(TimeProperty.SecondOfMinute) != 0L }) {
                            +':'
                            // FIXME: Change to fractionalSecondOfMinute()
                            secondOfMinute(2)
                            onlyIf({ it.has(TimeProperty.NanosecondOfSecond) && it.get(TimeProperty.NanosecondOfSecond) != 0L }) {
                                +'.'
                                nanosecondOfSecond(3)
                            }
                        }
                    }
                }

                onlyIf({ it.has(UtcOffsetProperty.TotalSeconds) }) {
                    offset()
                }

                // FIXME: Change fixed offset to getOrElse()
                onlyIf({ it.has(TimeZoneProperty.Id) && !it.get(TimeZoneProperty.IsFixedOffset) }) {
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