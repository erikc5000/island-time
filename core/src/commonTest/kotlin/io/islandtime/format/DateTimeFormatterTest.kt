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
    private val en_US = localeOf("en-US")

    @Test
    fun `localized date only`() {
        val date = Date(2020, Month.FEBRUARY, 1)
        val formatter = LocalizedDateFormatter(FormatStyle.FULL)

        assertEquals(
            "2020-02-01 (FULL)",
            formatter.format(date, TemporalFormatter.Settings(locale = en_US))
        )
    }

    @Test
    fun `localized time only`() {
        val time = Time(13, 30, 30, 1)
        val formatter = LocalizedTimeFormatter(FormatStyle.MEDIUM)

        assertEquals(
            "13:30:30 (MEDIUM)",
            formatter.format(time, TemporalFormatter.Settings(locale = en_US))
        )
    }

    @Test
    fun `localized date-time`() {
        val zonedDateTime = Date(2020, Month.FEBRUARY, 1) at
            Time(13, 30, 30, 1) at
            TimeZone("America/New_York")

        val formatter = LocalizedDateTimeFormatter(FormatStyle.SHORT, FormatStyle.LONG)

        assertEquals(
            "2020-02-01 (SHORT) 13:30:30 (LONG)",
            formatter.format(zonedDateTime, TemporalFormatter.Settings(locale = en_US))
        )
    }

    @Test
    fun `LocalizedDateTimeFormatter() does nothing when given an empty skeleton`() {
        val formatter = LocalizedDateTimeFormatter("")

        assertEquals(
            "",
            formatter.format(temporalWith(DateProperty.Year to 2000))
        )
    }

    @Test
    fun `throws an exception when the Temporal can't provide required properties`() {
        val date = Date(2020, Month.FEBRUARY, 1)
        val formatter = LocalizedDateTimeFormatter(FormatStyle.FULL)

        assertFailsWith<TemporalPropertyException> {
            formatter.format(date, TemporalFormatter.Settings(locale = en_US))
        }
    }

    @Test
    fun `empty onlyIf() does nothing`() {
        val formatter = dateTimeFormatter { onlyIf({ true }) {} }

        assertEquals(
            "",
            formatter.format(temporalWith(DateProperty.Year to 2000))
        )
    }

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