package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.UtcOffset
import io.islandtime.Year
import io.islandtime.base.*
import io.islandtime.measures.hours
import io.islandtime.measures.minutes
import io.islandtime.measures.seconds
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UtcOffsetFormatterTest : AbstractIslandTimeTest() {
    private val testOffsets = listOf(
        UtcOffset.ZERO,
        UtcOffset(1.hours),
        UtcOffset((-1).hours),
        UtcOffset(4.hours, 30.minutes),
        UtcOffset((-4).hours, (-30).minutes),
        UtcOffset(0.hours, 0.minutes, 1.seconds),
        UtcOffset(0.hours, 0.minutes, (-1).seconds),
        UtcOffset(5.hours, 0.minutes, 1.seconds),
        UtcOffset((-5).hours, 0.minutes, (-1).seconds),
        UtcOffset(4.hours, 30.minutes, 15.seconds),
        UtcOffset((-4).hours, (-30).minutes, (-15).seconds),
        UtcOffset.MAX,
        UtcOffset.MIN
    )

    @Test
    fun `default output matches UtcOffset_toString()`() {
        val formatter = dateTimeFormatter {
            offset()
        }

        testOffsets.forEach {
            assertEquals(it.toString(), formatter.format(it))
        }
    }

    @Test
    fun `minutes = never causes an exception`() {
        assertFailsWith<IllegalArgumentException> {
            dateTimeFormatter {
                offset(minutes = FormatOption.NEVER)
            }
        }

        assertFailsWith<IllegalArgumentException> {
            dateTimeFormatter {
                offset(format = IsoFormat.BASIC, minutes = FormatOption.NEVER)
            }
        }

        assertFailsWith<IllegalArgumentException> {
            dateTimeFormatter {
                offset(minutes = FormatOption.NEVER, seconds = FormatOption.ALWAYS)
            }
        }
    }

    @Test
    fun `throws an exception when the temporal can't supply the offset's total seconds`() {
        val formatter = dateTimeFormatter { offset() }
        assertFailsWith<TemporalPropertyException> { formatter.format(Year(2020)) }
    }

    @Test
    fun `throws an exception when the total seconds overflows a 32-bit integer`() {
        val intOverflowInducingTemporal = temporalWith(
            UtcOffsetProperty.TotalSeconds to Long.MAX_VALUE
        )

        val formatter = dateTimeFormatter { offset() }
        assertFailsWith<ArithmeticException> { formatter.format(intOverflowInducingTemporal) }
    }

    @Test
    fun `throws an exception when the offset is invalid`() {
        val formatter = dateTimeFormatter { offset() }

        assertFailsWith<DateTimeException> {
            formatter.format(UtcOffset(UtcOffset.MAX_TOTAL_SECONDS + 1.seconds))
        }
    }

    @Test
    fun `extended format, utc designator turned off`() {
        val formatter = dateTimeFormatter {
            offset(useUtcDesignatorWhenZero = false)
        }

        listOf(
            UtcOffset.ZERO to "+00:00",
            UtcOffset(1.hours) to "+01:00",
            UtcOffset((-1).hours) to "-01:00",
            UtcOffset(1.seconds) to "+00:00:01",
            UtcOffset((-1).seconds) to "-00:00:01"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }

    @Test
    fun `extended format, seconds = never`() {
        val formatter = dateTimeFormatter {
            offset(seconds = FormatOption.NEVER)
        }

        listOf(
            UtcOffset.ZERO to "Z",
            UtcOffset(1.hours) to "+01:00",
            UtcOffset((-1).hours) to "-01:00",
            UtcOffset(4.hours, 30.minutes) to "+04:30",
            UtcOffset((-4).hours, (-30).minutes) to "-04:30",
            UtcOffset(0.hours, 0.minutes, 1.seconds) to "+00:00",
            UtcOffset(0.hours, 0.minutes, (-1).seconds) to "-00:00",
            UtcOffset(5.hours, 0.minutes, 1.seconds) to "+05:00",
            UtcOffset((-5).hours, 0.minutes, (-1).seconds) to "-05:00",
            UtcOffset(10.hours, 5.minutes, 15.seconds) to "+10:05",
            UtcOffset((-10).hours, (-5).minutes, (-15).seconds) to "-10:05",
            UtcOffset.MAX to "+18:00",
            UtcOffset.MIN to "-18:00"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }

    @Test
    fun `extended format, seconds = always`() {
        val formatter = dateTimeFormatter {
            offset(seconds = FormatOption.ALWAYS)
        }

        listOf(
            UtcOffset.ZERO to "Z",
            UtcOffset(1.hours) to "+01:00:00",
            UtcOffset((-1).hours) to "-01:00:00",
            UtcOffset(4.hours, 30.minutes) to "+04:30:00",
            UtcOffset((-4).hours, (-30).minutes) to "-04:30:00",
            UtcOffset(0.hours, 0.minutes, 1.seconds) to "+00:00:01",
            UtcOffset(0.hours, 0.minutes, (-1).seconds) to "-00:00:01",
            UtcOffset(5.hours, 0.minutes, 1.seconds) to "+05:00:01",
            UtcOffset((-5).hours, 0.minutes, (-1).seconds) to "-05:00:01",
            UtcOffset(10.hours, 5.minutes, 15.seconds) to "+10:05:15",
            UtcOffset((-10).hours, (-5).minutes, (-15).seconds) to "-10:05:15",
            UtcOffset.MAX to "+18:00:00",
            UtcOffset.MIN to "-18:00:00"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }

    @Test
    fun `basic format`() {
        val formatter = dateTimeFormatter {
            offset(format = IsoFormat.BASIC)
        }

        listOf(
            UtcOffset.ZERO to "Z",
            UtcOffset(1.hours) to "+0100",
            UtcOffset((-1).hours) to "-0100",
            UtcOffset(4.hours, 30.minutes) to "+0430",
            UtcOffset((-4).hours, (-30).minutes) to "-0430",
            UtcOffset(0.hours, 0.minutes, 1.seconds) to "+000001",
            UtcOffset(0.hours, 0.minutes, (-1).seconds) to "-000001",
            UtcOffset(5.hours, 0.minutes, 1.seconds) to "+050001",
            UtcOffset((-5).hours, 0.minutes, (-1).seconds) to "-050001",
            UtcOffset(10.hours, 5.minutes, 15.seconds) to "+100515",
            UtcOffset((-10).hours, (-5).minutes, (-15).seconds) to "-100515",
            UtcOffset.MAX to "+1800",
            UtcOffset.MIN to "-1800"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }

    @Test
    fun `basic format, utc designator turned off`() {
        val formatter = dateTimeFormatter {
            offset(format = IsoFormat.BASIC, useUtcDesignatorWhenZero = false)
        }

        listOf(
            UtcOffset.ZERO to "+0000",
            UtcOffset(1.hours) to "+0100",
            UtcOffset((-1).hours) to "-0100",
            UtcOffset(1.seconds) to "+000001",
            UtcOffset((-1).seconds) to "-000001"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }

    @Test
    fun `basic format, minutes = optional`() {
        val formatter = dateTimeFormatter {
            offset(format = IsoFormat.BASIC, minutes = FormatOption.OPTIONAL)
        }

        listOf(
            UtcOffset.ZERO to "Z",
            UtcOffset(1.hours) to "+01",
            UtcOffset((-1).hours) to "-01",
            UtcOffset(4.hours, 30.minutes) to "+0430",
            UtcOffset((-4).hours, (-30).minutes) to "-0430",
            UtcOffset(0.hours, 0.minutes, 1.seconds) to "+000001",
            UtcOffset(0.hours, 0.minutes, (-1).seconds) to "-000001",
            UtcOffset(5.hours, 0.minutes, 1.seconds) to "+050001",
            UtcOffset((-5).hours, 0.minutes, (-1).seconds) to "-050001",
            UtcOffset(10.hours, 5.minutes, 15.seconds) to "+100515",
            UtcOffset((-10).hours, (-5).minutes, (-15).seconds) to "-100515",
            UtcOffset.MAX to "+18",
            UtcOffset.MIN to "-18"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }

    @Test
    fun `basic format, seconds = never`() {
        val formatter = dateTimeFormatter {
            offset(format = IsoFormat.BASIC, seconds = FormatOption.NEVER)
        }

        listOf(
            UtcOffset.ZERO to "Z",
            UtcOffset(1.hours) to "+0100",
            UtcOffset((-1).hours) to "-0100",
            UtcOffset(4.hours, 30.minutes) to "+0430",
            UtcOffset((-4).hours, (-30).minutes) to "-0430",
            UtcOffset(0.hours, 0.minutes, 1.seconds) to "+0000",
            UtcOffset(0.hours, 0.minutes, (-1).seconds) to "-0000",
            UtcOffset(5.hours, 0.minutes, 1.seconds) to "+0500",
            UtcOffset((-5).hours, 0.minutes, (-1).seconds) to "-0500",
            UtcOffset(10.hours, 5.minutes, 15.seconds) to "+1005",
            UtcOffset((-10).hours, (-5).minutes, (-15).seconds) to "-1005",
            UtcOffset.MAX to "+1800",
            UtcOffset.MIN to "-1800"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }

    @Test
    fun `basic format, seconds = always`() {
        val formatter = dateTimeFormatter {
            offset(format = IsoFormat.BASIC, seconds = FormatOption.ALWAYS)
        }

        listOf(
            UtcOffset.ZERO to "Z",
            UtcOffset(1.hours) to "+010000",
            UtcOffset((-1).hours) to "-010000",
            UtcOffset(4.hours, 30.minutes) to "+043000",
            UtcOffset((-4).hours, (-30).minutes) to "-043000",
            UtcOffset(0.hours, 0.minutes, 1.seconds) to "+000001",
            UtcOffset(0.hours, 0.minutes, (-1).seconds) to "-000001",
            UtcOffset(5.hours, 0.minutes, 1.seconds) to "+050001",
            UtcOffset((-5).hours, 0.minutes, (-1).seconds) to "-050001",
            UtcOffset(10.hours, 5.minutes, 15.seconds) to "+100515",
            UtcOffset((-10).hours, (-5).minutes, (-15).seconds) to "-100515",
            UtcOffset.MAX to "+180000",
            UtcOffset.MIN to "-180000"
        ).forEach { (offset, string) ->
            assertEquals(string, formatter.format(offset))
        }
    }
}