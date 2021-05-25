@file:OptIn(ExperimentalTime::class)

package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

class InstantTest : AbstractIslandTimeTest() {
    @Test
    fun `throws an exception when constructed with values outside the supported range`() {
        listOf(
            -31557014167219201L,
            31556889864403200L
        ).forEach {
            assertFailsWith<DateTimeException> { Instant.fromSecondOfUnixEpoch(it) }
            assertFailsWith<DateTimeException> { Instant.fromSecondOfUnixEpoch(it, 0) }
            assertFailsWith<DateTimeException> { Instant.fromSecondOfUnixEpoch(it, 0L) }
            assertFailsWith<DateTimeException> { Instant(it.seconds) }
            assertFailsWith<DateTimeException> { Instant(it.seconds, 0.nanoseconds) }
            assertFailsWith<DateTimeException> { Instant(it.seconds, 0L.nanoseconds) }
        }

        assertFailsWith<DateTimeException> {
            Instant.fromSecondOfUnixEpoch(-31557014167219200L, -1)
        }

        assertFailsWith<DateTimeException> {
            Instant.fromSecondOfUnixEpoch(-31557014167219200L, -1L)
        }

        assertFailsWith<DateTimeException> {
            Instant.fromSecondOfUnixEpoch(31556889864403199L, 1_000_000_000)
        }

        assertFailsWith<DateTimeException> {
            Instant.fromSecondOfUnixEpoch(31556889864403199L, 1_000_000_000L)
        }
    }

    @Test
    fun `millisecond properties return expected values`() {
        assertEquals(0L, Instant.UNIX_EPOCH.millisecondOfUnixEpoch)
        assertEquals(0L.milliseconds, Instant.UNIX_EPOCH.millisecondsSinceUnixEpoch)

        val instant = Instant.fromMillisecondOfUnixEpoch(1566256047821L)
        assertEquals(1566256047821L, instant.millisecondOfUnixEpoch)
        assertEquals(1566256047821L.milliseconds, instant.millisecondsSinceUnixEpoch)
    }

    @Test
    fun `instants can be compared to each other`() {
        assertTrue { Instant.UNIX_EPOCH < Instant.fromMillisecondOfUnixEpoch(1566256047821L) }
    }

    @Test
    fun `adding zero has no effect`() {
        val instant = Instant(1566256047821L.seconds)
        assertEquals(instant, instant + 0.days)
        assertEquals(instant, instant + 0.hours)
        assertEquals(instant, instant + 0.minutes)
        assertEquals(instant, instant + 0.seconds)
        assertEquals(instant, instant + 0.milliseconds)
        assertEquals(instant, instant + 0.microseconds)
        assertEquals(instant, instant + 0.nanoseconds)
        assertEquals(instant, instant + 0L.days)
        assertEquals(instant, instant + 0L.hours)
        assertEquals(instant, instant + 0L.minutes)
        assertEquals(instant, instant + 0L.seconds)
        assertEquals(instant, instant + 0L.milliseconds)
        assertEquals(instant, instant + 0L.microseconds)
        assertEquals(instant, instant + 0L.nanoseconds)
    }

    @Test
    fun `subtracting zero has no effect`() {
        val instant = Instant(1566256047821L.seconds)
        assertEquals(instant, instant - 0.days)
        assertEquals(instant, instant - 0.hours)
        assertEquals(instant, instant - 0.minutes)
        assertEquals(instant, instant - 0.seconds)
        assertEquals(instant, instant - 0.milliseconds)
        assertEquals(instant, instant - 0.microseconds)
        assertEquals(instant, instant - 0.nanoseconds)
        assertEquals(instant, instant - 0L.days)
        assertEquals(instant, instant - 0L.hours)
        assertEquals(instant, instant - 0L.minutes)
        assertEquals(instant, instant - 0L.seconds)
        assertEquals(instant, instant - 0L.milliseconds)
        assertEquals(instant, instant - 0L.microseconds)
        assertEquals(instant, instant - 0L.nanoseconds)
    }

    @Test
    fun `throws an exception when adding or subtracting days would cause overflow`() {
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) + Long.MAX_VALUE.days }
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) + Long.MIN_VALUE.days }
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) - Long.MAX_VALUE.days }
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) - Long.MIN_VALUE.days }
    }

    @Test
    fun `throws an exception when adding or subtracting hours would cause overflow`() {
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) + Long.MAX_VALUE.hours }
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) + Long.MIN_VALUE.hours }
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) - Long.MAX_VALUE.hours }
        assertFailsWith<ArithmeticException> { Instant(1L.seconds) - Long.MIN_VALUE.hours }
    }

    @Test
    fun `add seconds`() {
        assertEquals(
            Instant(1L.seconds, 1.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) + 1.seconds
        )

        assertEquals(
            Instant(1L.seconds, 1.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) + 1L.seconds
        )

        assertEquals(
            Instant((-2L).seconds, 1.nanoseconds),
            Instant((-1L).seconds, 1.nanoseconds) + (-1).seconds
        )

        assertEquals(
            Instant((-2L).seconds, 1.nanoseconds),
            Instant((-1L).seconds, 1.nanoseconds) + (-1L).seconds
        )
    }

    @Test
    fun `subtract seconds`() {
        assertEquals(
            Instant(1L.seconds, 1.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) - (-1).seconds
        )

        assertEquals(
            Instant(1L.seconds, 1.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) - (-1L).seconds
        )

        assertEquals(
            Instant((-2L).seconds, 1.nanoseconds),
            Instant((-1L).seconds, 1.nanoseconds) - 1.seconds
        )

        assertEquals(
            Instant((-2L).seconds, 1.nanoseconds),
            Instant((-1L).seconds, 1.nanoseconds) - 1L.seconds
        )
    }

    @Test
    fun `add nanoseconds`() {
        assertEquals(
            Instant(0L.seconds, 2.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) + 1.nanoseconds
        )

        assertEquals(
            Instant(0L.seconds, 2.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) + 1L.nanoseconds
        )

        assertEquals(
            Instant((-1L).seconds, 999_999_999.nanoseconds),
            Instant(0L.seconds, 0.nanoseconds) + (-1).nanoseconds
        )

        assertEquals(
            Instant((-1L).seconds, 999_999_999.nanoseconds),
            Instant(0L.seconds, 0.nanoseconds) + (-1L).nanoseconds
        )
    }

    @Test
    fun `subtract nanoseconds`() {
        assertEquals(
            Instant(0L.seconds, 2.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) - (-1).nanoseconds
        )

        assertEquals(
            Instant(0L.seconds, 2.nanoseconds),
            Instant(0L.seconds, 1.nanoseconds) - (-1L).nanoseconds
        )

        assertEquals(
            Instant((-1L).seconds, 999_999_999.nanoseconds),
            Instant(0L.seconds, 0.nanoseconds) - 1.nanoseconds
        )

        assertEquals(
            Instant((-1L).seconds, 999_999_999.nanoseconds),
            Instant(0L.seconds, 0.nanoseconds) - 1L.nanoseconds
        )
    }

    @Test
    fun `adding a kotlin duration of zero doesn't affect the instant`() {
        val instant = Instant(0.seconds, 1.nanoseconds)
        assertEquals(instant, instant + kotlin.time.Duration.ZERO)
    }

    @Test
    fun `throws an exception when adding an infinite kotlin duration`() {
        assertFailsWith<IllegalArgumentException> { Instant.UNIX_EPOCH + kotlin.time.Duration.INFINITE }
    }

    @Test
    fun `add a kotlin duration`() {
        assertEquals(
            Instant(1.hours.inSeconds, 2.nanoseconds),
            Instant(0.seconds, 1.nanoseconds) +
                (kotlin.time.Duration.hours(1) +
                    kotlin.time.Duration.nanoseconds(1))
        )
    }

    @Test
    fun `subtracting a kotlin duration of zero doesn't affect the instant`() {
        val instant = Instant(0.seconds, 1.nanoseconds)
        assertEquals(instant, instant - kotlin.time.Duration.ZERO)
    }

    @Test
    fun `throws an exception when subtracting an infinite kotlin duration`() {
        assertFailsWith<IllegalArgumentException> { Instant.UNIX_EPOCH - kotlin.time.Duration.INFINITE }
    }

    @Test
    fun `subtract a kotlin duration`() {
        assertEquals(
            Instant((-1).hours.inSeconds, 0.nanoseconds),
            Instant(0.seconds, 1.nanoseconds) -
                (kotlin.time.Duration.hours(1) +
                    kotlin.time.Duration.nanoseconds(1))
        )
    }

    @Test
    fun `toString() returns an ISO-8601 extended date-time with UTC offset`() {
        assertEquals(
            "1970-01-01T00:00Z",
            Instant.UNIX_EPOCH.toString()
        )

        assertEquals(
            "1970-01-01T00:00:00.000001Z",
            Instant.fromSecondOfUnixEpoch(0L, 1_000).toString()
        )

        assertEquals(
            "1969-12-31T23:59:59.999999999Z",
            Instant.fromSecondOfUnixEpoch(0L, -1).toString()
        )

        assertEquals(
            "2019-08-19T23:07:27.821Z",
            Instant.fromMillisecondOfUnixEpoch(1566256047821L).toString()
        )

        assertEquals(
            "+1000000000-12-31T23:59:59.999999999Z",
            Instant.MAX.toString()
        )

        assertEquals(
            "-1000000000-01-01T00:00Z",
            Instant.MIN.toString()
        )
    }

    @Test
    fun `String_toInstant() throws an exception when parsing an empty string`() {
        assertFailsWith<DateTimeParseException> { "".toInstant() }
        assertFailsWith<DateTimeParseException> { "".toInstant(DateTimeParsers.Iso.INSTANT) }
    }

    @Test
    fun `String_toInstant() throws an exception when the format is unexpected`() {
        listOf(
            "20191205 0304",
            "2019-12-05T03:04",
            "2019-12-05T12:00+00"
        ).forEach {
            assertFailsWith<DateTimeParseException> { it.toInstant() }
        }
    }

    @Test
    fun `String_toInstant() throws an exception when fields are out of range`() {
        listOf(
            "2000-01-01T24:00Z",
            "2000-01-01T08:60Z",
            "2000-13-01T08:59Z",
            "2000-01-32T08:59Z",
            "+1000000001-01-01T00:00Z",
            "-1000000001-12-31T23:59:59.999999999Z"
        ).forEach {
            assertFailsWith<DateTimeException> { it.toInstant() }
        }
    }

    @Test
    fun `String_toInstant() parses ISO-8601 calendar date time strings in extended format by default`() {
        assertEquals(
            Instant.fromMillisecondOfUnixEpoch(0L),
            "1970-01-01T00:00Z".toInstant()
        )

        assertEquals(
            Instant.fromMillisecondOfUnixEpoch(1566256047821L),
            "2019-08-19T23:07:27.821Z".toInstant()
        )

        assertEquals(
            Instant.MAX,
            "+1000000000-12-31T23:59:59.999999999Z".toInstant()
        )

        assertEquals(
            Instant.MIN,
            "-1000000000-01-01T00:00Z".toInstant()
        )
    }

    @Test
    fun `String_toInstant() parses ISO-8601 calendar date time strings in basic format with explicit parser`() {
        assertEquals(
            Instant.fromMillisecondOfUnixEpoch(0L),
            "19700101 0000Z".toInstant(DateTimeParsers.Iso.INSTANT)
        )

        assertEquals(
            Instant.fromMillisecondOfUnixEpoch(1566256047821L),
            "20190819T230727.821Z".toInstant(DateTimeParsers.Iso.INSTANT)
        )
    }

    @Test
    fun `String_toInstant() throws an exception when required fields are missing`() {
        val parser1 = dateTimeParser {
            monthNumber(2)
            +'-'
            dayOfMonth(2)
            +'T'
            childParser(DateTimeParsers.Iso.Extended.TIME)
            childParser(DateTimeParsers.Iso.Extended.UTC_OFFSET)
        }

        val exception1 = assertFailsWith<DateTimeParseException> {
            "01-01T03:30+01".toInstant(parser1)
        }
        assertEquals(0, exception1.errorIndex)

        val exception2 = assertFailsWith<DateTimeParseException> {
            "2001-01-01T03:30".toInstant(DateTimeParsers.Iso.DATE_TIME)
        }
        assertEquals(0, exception2.errorIndex)

        val exception3 = assertFailsWith<DateTimeParseException> {
            "PT5S".toInstant(DateTimeParsers.Iso.DURATION)
        }
        assertEquals(0, exception3.errorIndex)
    }
}
