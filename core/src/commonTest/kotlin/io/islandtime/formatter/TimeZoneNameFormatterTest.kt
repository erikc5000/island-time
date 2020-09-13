package io.islandtime.formatter

import io.islandtime.Date
import io.islandtime.Month
import io.islandtime.Time.Companion.NOON
import io.islandtime.TimeZone
import io.islandtime.at
import io.islandtime.format.ContextualTimeZoneNameStyle
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeTimeZoneNameProvider
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeZoneNameFormatterTest : AbstractIslandTimeTest(
    testTimeZoneNameProvider = FakeTimeZoneNameProvider
) {
    private val standardTimeDate =
        Date(2020, Month.FEBRUARY, 15) at NOON at TimeZone("America/New_York")

    private val daylightTimeDate =
        Date(2020, Month.MAY, 15) at NOON at TimeZone("America/New_York")

    @Test
    fun `when no text is available, the zone ID is used`() {
        val formatter = TemporalFormatter {
            timeZoneName(ContextualTimeZoneNameStyle.SHORT_GENERIC)
        }

        // Note: America/Boston doesn't exist
        assertEquals(
            "America/Boston",
            formatter.format(
                temporalWith(TimeZoneProperty.TimeZoneObject to TimeZone("America/Boston"))
            )
        )
    }

    @Test
    fun `formats long specific styles`() {
        val formatter = TemporalFormatter {
            timeZoneName(ContextualTimeZoneNameStyle.LONG_SPECIFIC)
        }

        assertEquals("Eastern Standard Time", formatter.format(standardTimeDate))
        assertEquals("Eastern Daylight Time", formatter.format(daylightTimeDate))
        assertEquals("GMT-05:00", formatter.format(standardTimeDate.withFixedOffsetZone()))
        assertEquals("GMT-04:00", formatter.format(daylightTimeDate.withFixedOffsetZone()))
    }

    @Test
    fun `formats short specific styles`() {
        val formatter = TemporalFormatter {
            timeZoneName(ContextualTimeZoneNameStyle.SHORT_SPECIFIC)
        }

        assertEquals("EST", formatter.format(standardTimeDate))
        assertEquals("EDT", formatter.format(daylightTimeDate))
        assertEquals("GMT-5", formatter.format(standardTimeDate.withFixedOffsetZone()))
        assertEquals("GMT-4", formatter.format(daylightTimeDate.withFixedOffsetZone()))
    }

    @Test
    fun `formats long generic styles`() {
        val formatter = TemporalFormatter {
            timeZoneName(ContextualTimeZoneNameStyle.LONG_GENERIC)
        }

        assertEquals("Eastern Time", formatter.format(standardTimeDate))
        assertEquals("Eastern Time", formatter.format(daylightTimeDate))
        assertEquals("GMT-05:00", formatter.format(standardTimeDate.withFixedOffsetZone()))
        assertEquals("GMT-04:00", formatter.format(daylightTimeDate.withFixedOffsetZone()))
    }

    @Test
    fun `formats short generic styles`() {
        val formatter = TemporalFormatter {
            timeZoneName(ContextualTimeZoneNameStyle.SHORT_GENERIC)
        }

        assertEquals("ET", formatter.format(standardTimeDate))
        assertEquals("ET", formatter.format(daylightTimeDate))
        assertEquals("GMT-5", formatter.format(standardTimeDate.withFixedOffsetZone()))
        assertEquals("GMT-4", formatter.format(daylightTimeDate.withFixedOffsetZone()))
    }

    @Test
    fun `uses generic name when specific and the temporal can't supply an instant`() {
        val formatter = TemporalFormatter {
            timeZoneName(ContextualTimeZoneNameStyle.LONG_SPECIFIC)
        }

        val zone = TimeZone("America/New_York")
        assertEquals("Eastern Time", formatter.format(zone))
    }
}
