package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@Suppress("PrivatePropertyName")
class TimeZoneTextProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")
    private val de_DE = localeOf("de-DE")

    @Test
    fun `textFor() returns null when given a fixed offset time zone`() {
        listOf(
            TimeZone.FixedOffset("-04:00"),
            TimeZone.FixedOffset("+00:00"),
            TimeZone.FixedOffset("+14:00")
        ).forEach { zone ->
            TimeZoneTextStyle.values().forEach { style ->
                assertNull(TimeZoneTextProvider.textFor(zone, style, en_US))
                assertNull(TimeZoneTextProvider.textFor(zone, style, de_DE))
            }
        }
    }

    @Test
    fun `textFor() returns a localized string when available`() {
        val zone = TimeZone("America/New_York")

        assertEquals(
            "Eastern Standard Time",
            TimeZoneTextProvider.textFor(zone, TimeZoneTextStyle.STANDARD, en_US)
        )
        assertEquals(
            "EST",
            TimeZoneTextProvider.textFor(zone, TimeZoneTextStyle.SHORT_STANDARD, en_US)
        )
        assertEquals(
            "Eastern Daylight Time",
            TimeZoneTextProvider.textFor(zone, TimeZoneTextStyle.DAYLIGHT_SAVING, en_US)
        )
        assertEquals(
            "EDT",
            TimeZoneTextProvider.textFor(zone, TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING, en_US)
        )
    }

    @Test
    fun `textFor() returns null when the zone is invalid`() {
        val zone = TimeZone("America/Boston")
        assertFalse { zone.isValid }

        TimeZoneTextStyle.values().forEach { style ->
            assertNull(TimeZoneTextProvider.textFor(zone, style, en_US))
        }
    }
}