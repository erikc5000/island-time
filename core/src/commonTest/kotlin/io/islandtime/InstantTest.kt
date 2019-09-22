package io.islandtime

import io.islandtime.interval.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.Iso8601
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InstantTest {
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
        assertFailsWith<DateTimeParseException> { "".toInstant(Iso8601.INSTANT_PARSER) }
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
            "19700101 0000Z".toInstant(Iso8601.INSTANT_PARSER)
        )

        assertEquals(
            Instant.fromUnixEpochMillisecond(1566256047821L),
            "20190819T230727.821Z".toInstant(Iso8601.INSTANT_PARSER)
        )
    }

    @Test
    fun `durationBetween() returns the duration between two instants`() {
        assertEquals(
            0.milliseconds.asDuration(),
            durationBetween(
                Instant.fromMillisecondsSinceUnixEpoch(1L.milliseconds),
                Instant.fromMillisecondsSinceUnixEpoch(1L.milliseconds)
            )
        )

        assertEquals(
            1.milliseconds.asDuration(),
            durationBetween(Instant.UNIX_EPOCH, Instant.fromMillisecondsSinceUnixEpoch(1L.milliseconds))
        )

        assertEquals(
            (-1).milliseconds.asDuration(),
            durationBetween(Instant.fromMillisecondsSinceUnixEpoch(1L.milliseconds), Instant.UNIX_EPOCH)
        )

        assertEquals(
            (-3).milliseconds.asDuration(),
            durationBetween(
                Instant.fromMillisecondsSinceUnixEpoch(1L.milliseconds),
                Instant.fromMillisecondsSinceUnixEpoch((-2L).milliseconds)
            )
        )
    }

    @Test
    fun `daysBetween returns the number of 24-hour days between two instants`() {
        assertEquals(
            0L.days,
            daysBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(86400L.seconds)
            )
        )

        assertEquals(
            0L.days,
            daysBetween(
                Instant.fromSecondsSinceUnixEpoch(86400L.seconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1L.days,
            daysBetween(Instant.UNIX_EPOCH, Instant.fromSecondsSinceUnixEpoch(86400L.seconds))
        )
    }

    @Test
    fun `hoursBetween() returns the number of whole hours between two instants`() {
        assertEquals(
            0L.hours,
            hoursBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(3600L.seconds)
            )
        )

        assertEquals(
            0L.hours,
            hoursBetween(
                Instant.fromSecondsSinceUnixEpoch(3600L.seconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1L.hours,
            hoursBetween(Instant.UNIX_EPOCH, Instant.fromSecondsSinceUnixEpoch(3600L.seconds))
        )
    }

    @Test
    fun `minutesBetween() returns the number of whole minutes between two instants`() {
        assertEquals(
            0L.minutes,
            minutesBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(60L.seconds)
            )
        )

        assertEquals(
            0L.minutes,
            minutesBetween(
                Instant.fromSecondsSinceUnixEpoch(60L.seconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1L.minutes,
            minutesBetween(Instant.UNIX_EPOCH, Instant.fromSecondsSinceUnixEpoch(60L.seconds))
        )
    }

    @Test
    fun `secondsBetween() returns the number of whole seconds between two instants`() {
        assertEquals(
            0L.seconds,
            secondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(1L.seconds)
            )
        )

        assertEquals(
            0L.seconds,
            secondsBetween(
                Instant.fromSecondsSinceUnixEpoch(1L.seconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            0L.seconds,
            secondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 999_999_999.nanoseconds),
                Instant.UNIX_EPOCH
            )
        )

        assertEquals(
            0L.seconds,
            secondsBetween(
                Instant.UNIX_EPOCH,
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 999_999_999.nanoseconds)
            )
        )

        assertEquals(
            1L.seconds,
            secondsBetween(Instant.UNIX_EPOCH, Instant.fromSecondsSinceUnixEpoch(1L.seconds))
        )

        assertEquals(
            (-1L).seconds,
            secondsBetween(Instant.fromSecondsSinceUnixEpoch(1L.seconds), Instant.UNIX_EPOCH)
        )

        assertEquals(
            1L.seconds,
            secondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 500_000_000.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(1L.seconds, 500_000_000.nanoseconds)
            )
        )
        assertEquals(
            1L.seconds,
            secondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 500_000_000.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(2L.seconds, 499_999_999.nanoseconds)
            )
        )
    }

    @Test
    fun `millisecondsBetween() returns the number of whole milliseconds between two instants`() {
        assertEquals(
            0L.milliseconds,
            millisecondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1_000_000.nanoseconds)
            )
        )

        assertEquals(
            0L.milliseconds,
            millisecondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1_000_000.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1L.milliseconds,
            millisecondsBetween(
                Instant.UNIX_EPOCH,
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1_000_000.nanoseconds)
            )
        )
    }

    @Test
    fun `microsecondsBetween() returns the number of whole microseconds between two instants`() {
        assertEquals(
            0L.microseconds,
            microsecondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1_000.nanoseconds)
            )
        )

        assertEquals(
            0L.microseconds,
            microsecondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1_000.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            1L.microseconds,
            microsecondsBetween(
                Instant.UNIX_EPOCH,
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1_000.nanoseconds)
            )
        )
    }

    @Test
    fun `nanosecondsBetween() returns zero when both instants are the same`() {
        assertEquals(
            0L.nanoseconds,
            nanosecondsBetween(
                Instant.fromSecondsSinceUnixEpoch(1L.seconds, 1.nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(1L.seconds, 1.nanoseconds)
            )
        )
    }

    @Test
    fun `nanosecondsBetween() returns the number of nanoseconds between two instants`() {
        assertEquals(
            2L.nanoseconds,
            nanosecondsBetween(
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, (-1).nanoseconds),
                Instant.fromSecondsSinceUnixEpoch(0L.seconds, 1.nanoseconds)
            )
        )

        assertEquals(
            (-1_000_000_000L).nanoseconds,
            nanosecondsBetween(
                Instant.UNIX_EPOCH,
                Instant.fromSecondsSinceUnixEpoch((-1L).seconds)
            )
        )
    }

    @Test
    fun `nanosecondsBetween() throws an exception when the result overflows`() {
        assertFailsWith<ArithmeticException> { nanosecondsBetween(Instant.MIN, Instant.UNIX_EPOCH) }
    }
}