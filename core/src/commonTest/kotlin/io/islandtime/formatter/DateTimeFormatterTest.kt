package io.islandtime.formatter

import io.islandtime.Date
import io.islandtime.Time
import io.islandtime.TimeZone
import io.islandtime.at
import io.islandtime.base.getOrElse
import io.islandtime.base.getOrNull
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeProperty
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeDateTimeFormatProvider
import io.islandtime.test.FakeDateTimeTextProvider
import io.islandtime.test.FakeTimeZoneNameProvider
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("PrivatePropertyName")
class DateTimeFormatterTest : AbstractIslandTimeTest(
    dateTimeFormatProvider = FakeDateTimeFormatProvider,
    dateTimeTextProvider = FakeDateTimeTextProvider,
    timeZoneNameProvider = FakeTimeZoneNameProvider
) {
    @Test
    fun `can model an iso format`() {
        val formatter = DateTimeFormatter {
            year(4)
            +'-'
            monthNumber(2)
            onlyIf({ temporal.has(DateProperty.DayOfMonth) }) {
                +'-'
                dayOfMonth(2)

                onlyIf({ temporal.has(TimeProperty.HourOfDay) }) {
                    +'T'
                    hourOfDay(2)
                    onlyIf({ temporal.has(TimeProperty.MinuteOfHour) }) {
                        +':'
                        minuteOfHour(2)
                        onlyIf({ temporal.getOrElse(TimeProperty.SecondOfMinute) { 0L } != 0L }) {
                            +':'
                            // FIXME: Change to fractionalSecondOfMinute()
                            secondOfMinute(2)
                            onlyIf({ temporal.getOrElse(TimeProperty.NanosecondOfSecond) { 0L } != 0L }) {
                                +'.'
                                nanosecondOfSecond(3)
                            }
                        }
                    }
                }

                onlyIf({ temporal.has(UtcOffsetProperty.TotalSeconds) }) {
                    offset()
                }

                onlyIf({ temporal.getOrNull(TimeZoneProperty.TimeZoneObject) is TimeZone.Region }) {
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
