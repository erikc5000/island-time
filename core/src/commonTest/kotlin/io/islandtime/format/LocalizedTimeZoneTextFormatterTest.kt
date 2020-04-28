package io.islandtime.format

import io.islandtime.*
import io.islandtime.Time.Companion.NOON
import io.islandtime.base.TimeZoneProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeTimeZoneTextProvider
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedTimeZoneTextFormatterTest : AbstractIslandTimeTest(
    testTimeZoneTextProvider = FakeTimeZoneTextProvider
) {
    private val standardTimeDate =
        Date(2020, Month.FEBRUARY, 15) at NOON at TimeZone("America/New_York")

    private val daylightTimeDate =
        Date(2020, Month.MAY, 15) at NOON at TimeZone("America/New_York")

    @Test
    fun `when no text is available, the zone ID is used`() {
        val formatter = temporalFormatter {
            localizedTimeZoneText(TextStyle.SHORT, generic = true)
        }

        assertEquals(
            "America/Boston",
            formatter.format(
                temporalWith(TimeZoneProperty.TimeZone to TimeZone("America/Boston"))
            )
        )
    }

    @Test
    fun `formats full specific styles`() {
        listOf(
            TextStyle.FULL,
            TextStyle.FULL_STANDALONE
        ).forEach { style ->
            val formatter = temporalFormatter {
                localizedTimeZoneText(style, generic = false)
            }

            assertEquals("Eastern Standard Time", formatter.format(standardTimeDate))
            assertEquals("Eastern Daylight Time", formatter.format(daylightTimeDate))
        }
    }

    @Test
    fun `formats short specific styles`() {
        listOf(
            TextStyle.SHORT,
            TextStyle.SHORT_STANDALONE
        ).forEach { style ->
            val formatter = temporalFormatter {
                localizedTimeZoneText(style, generic = false)
            }

            assertEquals("EST", formatter.format(standardTimeDate))
            assertEquals("EDT", formatter.format(daylightTimeDate))
        }
    }

    @Test
    fun `formats full generic styles`() {
        listOf(
            TextStyle.FULL,
            TextStyle.FULL_STANDALONE
        ).forEach { style ->
            val formatter = temporalFormatter {
                localizedTimeZoneText(style, generic = true)
            }

            assertEquals("Eastern Time", formatter.format(standardTimeDate))
            assertEquals("Eastern Time", formatter.format(daylightTimeDate))
        }
    }

    @Test
    fun `formats short generic styles`() {
        listOf(
            TextStyle.SHORT,
            TextStyle.SHORT_STANDALONE
        ).forEach {
            val formatter = temporalFormatter {
                localizedTimeZoneText(it, generic = true)
            }

            assertEquals("ET", formatter.format(standardTimeDate))
            assertEquals("ET", formatter.format(daylightTimeDate))
        }
    }

    @Test
    fun `the zone ID is returned when a narrow text style is used`() {
        listOf(
            TextStyle.NARROW,
            TextStyle.NARROW_STANDALONE
        ).forEach { style ->
            listOf(
                temporalFormatter { localizedTimeZoneText(style, generic = false) },
                temporalFormatter { localizedTimeZoneText(style, generic = true) }
            ).forEach { formatter ->
                assertEquals("America/New_York", formatter.format(standardTimeDate))
                assertEquals("America/New_York", formatter.format(daylightTimeDate))
            }
        }
    }

    @Test
    fun `uses generic name when specific and the temporal can't supply an instant`() {
        val formatter = temporalFormatter {
            localizedTimeZoneText(TextStyle.FULL, generic = false)
        }

        val zone = TimeZone("America/New_York")
        assertEquals("Eastern Time", formatter.format(zone))
    }
}