package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.hours
import dev.erikchristensen.islandtime.interval.minutes
import dev.erikchristensen.islandtime.interval.seconds
import dev.erikchristensen.islandtime.parser.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OffsetDateTimeTest {
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
    fun `String_toOffsetDateTime() throws an exception when string is empty`() {
        assertFailsWith<DateTimeParseException> { "".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime() throws an exception when parsing mixed ISO-8601 basic and extended formats`() {
        assertFailsWith<DateTimeParseException> { "20191205 12:00+00".toOffsetDateTime() }
        assertFailsWith<DateTimeParseException> { "2019-12-05T1200+00".toOffsetDateTime() }
    }

    @Test
    fun `String_toOffsetDateTime() parses valid ISO-8601 calendar date time strings in extended format`() {
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
}