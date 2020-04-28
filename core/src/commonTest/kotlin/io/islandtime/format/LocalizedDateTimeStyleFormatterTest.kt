package io.islandtime.format

import io.islandtime.*
import io.islandtime.base.TemporalPropertyException
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeDateTimeFormatProvider
import io.islandtime.test.FakeDateTimeTextProvider
import io.islandtime.test.FakeTimeZoneTextProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalizedDateTimeStyleFormatterTest : AbstractIslandTimeTest(
    testDateTimeFormatProvider = FakeDateTimeFormatProvider,
    testDateTimeTextProvider = FakeDateTimeTextProvider,
    testTimeZoneTextProvider = FakeTimeZoneTextProvider
) {
    @Test
    fun `localized date only`() {
        val date = Date(2020, Month.FEBRUARY, 1)
        val formatter = LocalizedDateFormatter(FormatStyle.FULL)
        assertEquals("2020-02-01 (FULL)", formatter.format(date))
    }

    @Test
    fun `localized time only`() {
        val time = Time(13, 30, 30, 1)
        val formatter = LocalizedTimeFormatter(FormatStyle.MEDIUM)
        assertEquals("13:30:30 (MEDIUM)", formatter.format(time))
    }

    @Test
    fun `localized date-time`() {
        val zonedDateTime = Date(2020, Month.FEBRUARY, 1) at
            Time(13, 30, 30, 1) at
            TimeZone("America/New_York")

        val formatter = LocalizedDateTimeFormatter(FormatStyle.SHORT, FormatStyle.LONG)

        assertEquals(
            "2020-02-01 (SHORT) 13:30:30 (LONG)",
            formatter.format(zonedDateTime)
        )
    }

    @Test
    fun `throws an exception when the Temporal can't provide required properties`() {
        val date = Date(2020, Month.FEBRUARY, 1)
        val formatter = LocalizedDateTimeFormatter(FormatStyle.FULL)
        assertFailsWith<TemporalPropertyException> { formatter.format(date) }
    }
}