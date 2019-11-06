package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InstantTest : AbstractIslandTimeTest() {
    @Test
    fun `millisecond properties return expected values`() {
        assertEquals(0L, Instant.UNIX_EPOCH.unixEpochMillisecond)
        assertEquals(0L.milliseconds, Instant.UNIX_EPOCH.millisecondsSinceUnixEpoch)

        val instant = Instant.fromUnixEpochMillisecond(1566256047821L)
        assertEquals(1566256047821L, instant.unixEpochMillisecond)
        assertEquals(1566256047821L.milliseconds, instant.millisecondsSinceUnixEpoch)
    }

    @Test
    fun `instants can be compared to each other`() {
        assertTrue { Instant.UNIX_EPOCH < Instant.fromUnixEpochMillisecond(1566256047821L) }
    }

    @Test
    fun `toString() returns an ISO-8601 extended date-time with UTC offset`() {
        assertEquals(
            "1970-01-01T00:00Z",
            Instant.UNIX_EPOCH.toString()
        )

        assertEquals(
            "1970-01-01T00:00:00.000001Z",
            Instant.fromUnixEpochSecond(0L, 1_000).toString()
        )

        assertEquals(
            "1969-12-31T23:59:59.999999999Z",
            Instant.fromUnixEpochSecond(0L, -1).toString()
        )

        assertEquals(
            "2019-08-19T23:07:27.821Z",
            Instant.fromUnixEpochMillisecond(1566256047821L).toString()
        )
    }

    @Test
    fun `String_toInstant() throws an exception when parsing an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toInstant() }
        assertFailsWith<DateTimeParseException> { "".toInstant(DateTimeParsers.Iso.INSTANT) }
    }

    @Test
    fun `String_toInstant() throws an exception when format is unexpected`() {
        assertFailsWith<DateTimeParseException> { "20191205 0304".toInstant() }
        assertFailsWith<DateTimeParseException> { "2019-12-05T03:04".toInstant() }
        assertFailsWith<DateTimeParseException> { "2019-12-05T12:00+00".toInstant() }
    }

    @Test
    fun `String_toInstant() throws an exception when fields are out of range`() {
        assertFailsWith<DateTimeException> { "2000-01-01T24:00Z".toInstant() }
        assertFailsWith<DateTimeException> { "2000-01-01T08:60Z".toInstant() }
        assertFailsWith<DateTimeException> { "2000-13-01T08:59Z".toInstant() }
        assertFailsWith<DateTimeException> { "2000-01-32T08:59Z".toInstant() }
    }

    @Test
    fun `String_toInstant() parses ISO-8601 calendar date time strings in extended format by default`() {
        assertEquals(
            Instant.fromUnixEpochMillisecond(0L),
            "1970-01-01T00:00Z".toInstant()
        )

        assertEquals(
            Instant.fromUnixEpochMillisecond(1566256047821L),
            "2019-08-19T23:07:27.821Z".toInstant()
        )
    }

    @Test
    fun `String_toInstant() parses ISO-8601 calendar date time strings in basic format with explicit parser`() {
        assertEquals(
            Instant.fromUnixEpochMillisecond(0L),
            "19700101 0000Z".toInstant(DateTimeParsers.Iso.INSTANT)
        )

        assertEquals(
            Instant.fromUnixEpochMillisecond(1566256047821L),
            "20190819T230727.821Z".toInstant(DateTimeParsers.Iso.INSTANT)
        )
    }
}