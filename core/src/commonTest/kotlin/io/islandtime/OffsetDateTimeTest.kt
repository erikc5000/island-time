package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class OffsetDateTimeTest : AbstractIslandTimeTest() {
    private val testOffsetDateTime = OffsetDateTime(
        2019, Month.FEBRUARY, 1, 1, 2, 3, 4, UtcOffset.MIN
    )

    @Test
    fun `throws an exception if constructed with an offset that's invalid`() {
        assertFailsWith<DateTimeException> {
            DateTime(2018, 1, 8, 1, 0) at UtcOffset(19.hours)
        }

        assertFailsWith<DateTimeException> {
            DateTime(2018, 1, 8, 1, 0) at UtcOffset((-19).hours)
        }
    }

    @Test
    fun `can be constructed with day of year`() {
        OffsetDateTime(2019, 18, 1, 2, 3, 4, UtcOffset.ZERO).run {
            assertEquals(2019, year)
            assertEquals(18, dayOfYear)
            assertEquals(1, hour)
            assertEquals(2, minute)
            assertEquals(3, second)
            assertEquals(4, nanosecond)
            assertEquals(UtcOffset.ZERO, offset)
        }
    }

    @Test
    fun `can be constructed from seconds since unix epoch`() {
        OffsetDateTime.fromSecondsSinceUnixEpoch((-1L).seconds, offset = UtcOffset.ZERO).run {
            assertEquals(1969, year)
            assertEquals(365, dayOfYear)
            assertEquals(23, hour)
            assertEquals(59, minute)
            assertEquals(59, second)
            assertEquals(0, nanosecond)
            assertEquals(UtcOffset.ZERO, offset)
        }

        OffsetDateTime.fromSecondsSinceUnixEpoch((-1L).seconds, 1.nanoseconds, UtcOffset.ZERO).run {
            assertEquals(1969, year)
            assertEquals(365, dayOfYear)
            assertEquals(23, hour)
            assertEquals(59, minute)
            assertEquals(59, second)
            assertEquals(1, nanosecond)
            assertEquals(UtcOffset.ZERO, offset)
        }
    }

    @Test
    fun `can be constructed from milliseconds since unix epoch`() {
        OffsetDateTime.fromMillisecondsSinceUnixEpoch(1L.milliseconds, (-1).hours.asUtcOffset()).run {
            assertEquals(1969, year)
            assertEquals(365, dayOfYear)
            assertEquals(23, hour)
            assertEquals(0, minute)
            assertEquals(0, second)
            assertEquals(1_000_000, nanosecond)
            assertEquals((-1).hours.asUtcOffset(), offset)
        }
    }

    @Test
    fun `can be constructed from second of unix epoch`() {
        OffsetDateTime.fromSecondOfUnixEpoch(-1L, offset = UtcOffset.ZERO).run {
            assertEquals(1969, year)
            assertEquals(365, dayOfYear)
            assertEquals(23, hour)
            assertEquals(59, minute)
            assertEquals(59, second)
            assertEquals(0, nanosecond)
            assertEquals(UtcOffset.ZERO, offset)
        }

        OffsetDateTime.fromSecondOfUnixEpoch(0L, 1, UtcOffset.ZERO).run {
            assertEquals(1970, year)
            assertEquals(1, dayOfYear)
            assertEquals(0, hour)
            assertEquals(0, minute)
            assertEquals(0, second)
            assertEquals(1, nanosecond)
            assertEquals(UtcOffset.ZERO, offset)
        }
    }

    @Test
    fun `can be constructed from millisecond of unix epoch`() {
        OffsetDateTime.fromMillisecondOfUnixEpoch(1L, UtcOffset.ZERO).run {
            assertEquals(1970, year)
            assertEquals(1, dayOfYear)
            assertEquals(0, hour)
            assertEquals(0, minute)
            assertEquals(0, second)
            assertEquals(1_000_000, nanosecond)
            assertEquals(UtcOffset.ZERO, offset)
        }
    }

    @Test
    fun `equality is based on date time and offset`() {
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
    fun `compareTo compares based on instant only`() {
        assertTrue {
            (Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours))
                .compareTo(Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO) == 0
        }

        assertTrue {
            Date(1969, 365) at Time(22, 0) at UtcOffset((-1).hours) <
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
        }
    }

    @Test
    fun `DefaultSortOrder compares based on instant then date and time when there are differing offsets`() {
        assertTrue {
            OffsetDateTime.DefaultSortOrder.compare(
                Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours),
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
            ) < 0
        }

        assertTrue {
            OffsetDateTime.DefaultSortOrder.compare(
                Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours),
                Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours)
            ) == 0
        }
    }

    @Test
    fun `TimelineOrder compares based on instant only`() {
        assertTrue {
            OffsetDateTime.TimelineOrder.compare(
                Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours),
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
            ) == 0
        }

        assertTrue {
            Date(1969, 365) at Time(22, 0) at UtcOffset((-1).hours) <
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
        }
    }

    @Test
    fun `copy can be used to replace individual date or time components`() {
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
    fun `DateTime properties work as expected`() {
        assertEquals(2019, testOffsetDateTime.year)
        assertEquals(Month.FEBRUARY, testOffsetDateTime.month)
        assertEquals(1, testOffsetDateTime.dayOfMonth)
        assertEquals(32, testOffsetDateTime.dayOfYear)
        assertEquals(DayOfWeek.FRIDAY, testOffsetDateTime.dayOfWeek)
        assertEquals(1, testOffsetDateTime.hour)
        assertEquals(2, testOffsetDateTime.minute)
        assertEquals(3, testOffsetDateTime.second)
        assertEquals(4, testOffsetDateTime.nanosecond)
    }

    @Test
    fun `adjustedTo changes the offset while preserving the instant represented by it`() {
        assertEquals(
            DateTime(2000, Month.APRIL, 4, 7, 0) at UtcOffset.MIN,
            DateTime(2000, Month.APRIL, 5, 1, 0).at(UtcOffset.ZERO).adjustedTo(UtcOffset.MIN)
        )
        assertEquals(
            DateTime(2001, Month.MAY, 5, 19, 1) at UtcOffset.MAX,
            DateTime(2001, Month.MAY, 5, 1, 1).at(UtcOffset.ZERO).adjustedTo(UtcOffset.MAX)
        )
    }

    @Test
    fun `toString returns ISO-8601 extended representation of the date + time + offset`() {
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
    fun `String_toOffsetDateTime throws an exception when the string is empty`() {
        assertFailsWith<DateTimeParseException> { "".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime throws an exception when format is unexpected`() {
        assertFailsWith<DateTimeParseException> { "20191205 12:00+00".toOffsetDateTime() }
        assertFailsWith<DateTimeParseException> { "2019-12-05T1200+00".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime throws an exception when fields are out of range`() {
        assertFailsWith<DateTimeException> { "2000-01-01T24:00+01:00".toOffsetDateTime() }
        assertFailsWith<DateTimeException> { "2000-01-01T08:60-01:00".toOffsetDateTime() }
        assertFailsWith<DateTimeException> { "2000-13-01T08:59-01:00".toOffsetDateTime() }
        assertFailsWith<DateTimeException> { "2000-01-32T08:59-01:00".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime parses valid ISO-8601 calendar date time strings in extended format by default`() {
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
    fun `String_toOffsetDateTime parses valid ISO-8601 strings with explicit parser`() {
        assertEquals(
            OffsetDateTime(
                DateTime(Date(2019, Month.MAY, 5), Time.NOON),
                5.hours.asUtcOffset()
            ),
            "20190505 1200+05".toOffsetDateTime(DateTimeParsers.Iso.OFFSET_DATE_TIME)
        )

        assertEquals(
            OffsetDateTime(
                DateTime(Date(2019, Month.MAY, 5), Time(5, 0, 3, 500)),
                UtcOffset(2.hours, 30.minutes, 23.seconds)
            ),
            "20190505T050003.0000005+023023".toOffsetDateTime(DateTimeParsers.Iso.OFFSET_DATE_TIME)
        )
    }
}
