package io.islandtime

import io.islandtime.date.Date
import io.islandtime.interval.days
import io.islandtime.interval.hours
import io.islandtime.interval.minutes
import io.islandtime.interval.seconds
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.Iso8601
import kotlin.test.*

class OffsetDateTimeTest {
    private val testOffset = OffsetDateTime(
        2019, Month.FEBRUARY, 1, 1, 2, 3, 4, UtcOffset.MIN
    )

    @Test
    fun `compareTo() compares based on date and time when the offset for both times is the same`() {
        assertTrue {
            Date(1970, Month.JANUARY, 1) at Time(0, 0) at UtcOffset.ZERO <
                Date(1970, Month.JANUARY, 1) at
                Time(0, 0, 0, 1) at
                UtcOffset.ZERO
        }
    }

    @Test
    fun `compareTo() compares based on instant, then date and time when there are differing offsets`() {
        assertTrue {
            Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours) <
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
        }
    }

    @Test
    fun `equality is based on date, time, and offset`() {
        assertFalse {
            Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours) ==
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
        }
        assertTrue {
            Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO ==
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
        }
    }

    @Test
    fun `can be destructured into date-time and offset components`() {
        val (dateTime, offset) = testOffset

        assertEquals(
            DateTime(2019, Month.FEBRUARY, 1, 1, 2, 3, 4),
            dateTime
        )
        assertEquals(UtcOffset.MIN, offset)
    }

    @Test
    fun `copy() can be used to replace individual date or time components`() {
        assertEquals(
            (Date(2019, Month.FEBRUARY, 1) at Time.NOON at UtcOffset.ZERO),
            (Date(2019, Month.JANUARY, 1) at Time.NOON at UtcOffset.ZERO).copy(dayOfYear = 32)
        )

        assertEquals(
            (Date(2019, Month.JANUARY, 12) at Time(23, 0) at UtcOffset(4.hours)),
            (Date(2019, Month.JANUARY, 1) at Time.NOON at UtcOffset(4.hours))
                .copy(dayOfMonth = 12, hour = 23)
        )
    }

    @Test
    fun `at operator can be used to create an OffsetDateTime from a Date and OffsetTime`() {
        assertEquals(
            testOffset,
            Date(2019, 32) at
                OffsetTime(1, 2, 3, 4, UtcOffset.MIN)
        )
    }

    @Test
    fun `DateTime properties work as expected`() {
        assertEquals(2019, testOffset.year)
        assertEquals(Month.FEBRUARY, testOffset.month)
        assertEquals(1, testOffset.dayOfMonth)
        assertEquals(32, testOffset.dayOfYear)
        assertEquals(DayOfWeek.FRIDAY, testOffset.dayOfWeek)
        assertFalse(testOffset.isInLeapYear)
        assertFalse(testOffset.isLeapDay)
        assertEquals(28.days, testOffset.lengthOfMonth)
        assertEquals(365.days, testOffset.lengthOfYear)
        assertEquals(1, testOffset.hour)
        assertEquals(2, testOffset.minute)
        assertEquals(3, testOffset.second)
        assertEquals(4, testOffset.nanosecond)
    }

    @Test
    fun `adjustedTo() changes the offset while preserving the instant represented by it`() {
        assertEquals(
            DateTime(2000, Month.APRIL, 4, 7, 0) at UtcOffset.MAX,
            DateTime(2000, Month.APRIL, 5, 1, 0).at(UtcOffset.ZERO).adjustedTo(UtcOffset.MAX)
        )
        assertEquals(
            DateTime(2001, Month.MAY, 5, 19, 1) at UtcOffset.MIN,
            DateTime(2001, Month.MAY, 5, 1, 1).at(UtcOffset.ZERO).adjustedTo(UtcOffset.MIN)
        )
    }

    @Test
    fun `toString() returns ISO-8601 extended representation of the date, time, and offset`() {
        assertEquals(
            "2018-05-05T12:00Z",
            OffsetDateTime(
                DateTime(Date(2018, Month.MAY, 5), Time.NOON),
                UtcOffset.ZERO
            ).toString()
        )

        assertEquals(
            "2018-05-05T12:00+05:00",
            OffsetDateTime(
                DateTime(Date(2018, Month.MAY, 5), Time.NOON),
                5.hours.asUtcOffset()
            ).toString()
        )

        assertEquals(
            "2018-05-05T12:00-12:30",
            OffsetDateTime(
                DateTime(Date(2018, Month.MAY, 5), Time.NOON),
                UtcOffset((-12).hours, (-30).minutes)
            ).toString()
        )
    }

    @Test
    fun `String_toOffsetDateTime() throws an exception when the string is empty`() {
        assertFailsWith<DateTimeParseException> { "".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime() throws an exception when format is unexpected`() {
        assertFailsWith<DateTimeParseException> { "20191205 12:00+00".toOffsetDateTime() }
        assertFailsWith<DateTimeParseException> { "2019-12-05T1200+00".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime() throws an exception when fields are out of range`() {
        assertFailsWith<DateTimeException> { "2000-01-01T24:00+01:00".toOffsetDateTime() }
        assertFailsWith<DateTimeException> { "2000-01-01T08:60-01:00".toOffsetDateTime() }
        assertFailsWith<DateTimeException> { "2000-13-01T08:59-01:00".toOffsetDateTime() }
        assertFailsWith<DateTimeException> { "2000-01-32T08:59-01:00".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime() parses valid ISO-8601 calendar date time strings in extended format by default`() {
        assertEquals(
            OffsetDateTime(
                DateTime(Date(2019, Month.MAY, 5), Time.NOON),
                5.hours.asUtcOffset()
            ),
            "2019-05-05T12:00+05:00".toOffsetDateTime()
        )

        assertEquals(
            OffsetDateTime(
                DateTime(Date(2019, Month.MAY, 5), Time(5, 0, 3, 500)),
                UtcOffset(2.hours, 30.minutes, 23.seconds)
            ),
            "2019-05-05T05:00:03.0000005+02:30:23".toOffsetDateTime()
        )
    }

    @Test
    fun `String_toOffsetDateTime() parses valid ISO-8601 strings with explicit parser`() {
        assertEquals(
            OffsetDateTime(
                DateTime(Date(2019, Month.MAY, 5), Time.NOON),
                5.hours.asUtcOffset()
            ),
            "20190505 1200+05".toOffsetDateTime(Iso8601.OFFSET_DATE_TIME_PARSER)
        )

        assertEquals(
            OffsetDateTime(
                DateTime(Date(2019, Month.MAY, 5), Time(5, 0, 3, 500)),
                UtcOffset(2.hours, 30.minutes, 23.seconds)
            ),
            "20190505T050003.0000005+023023".toOffsetDateTime(Iso8601.OFFSET_DATE_TIME_PARSER)
        )
    }
}