package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

@Suppress("PrivatePropertyName")
class TimeZoneTextProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")
    private val de_DE = localeOf("de-DE")

    @Test
    fun `timeZoneTextFor() returns null when given a fixed offset time zone`() {
        listOf(
            TimeZone.FixedOffset("-04:00"),
            TimeZone.FixedOffset("+00:00"),
            TimeZone.FixedOffset("+14:00")
        ).forEach { zone ->
            TimeZoneTextStyle.values().forEach { style ->
                assertNull(TimeZoneTextProvider.timeZoneTextFor(zone, style, en_US))
                assertNull(TimeZoneTextProvider.timeZoneTextFor(zone, style, de_DE))
            }
        }
    }

    @Test
    fun `timeZoneTextFor() returns a localized string when available`() {
        val zone = TimeZone("America/New_York")

        assertEquals(
            "Eastern Standard Time",
            TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.STANDARD, en_US)
        )
        assertEquals(
            "EST",
            TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_STANDARD, en_US)
        )
        assertEquals(
            "Eastern Daylight Time",
            TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.DAYLIGHT_SAVING, en_US)
        )
        assertEquals(
            "EDT",
            TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING, en_US)
        )
    }

    @Test
    fun `timeZoneTextFor() returns null when the zone is invalid`() {
        val zone = TimeZone("America/Boston")
        assertFalse { zone.isValid }

        TimeZoneTextStyle.values().forEach { style ->
            assertNull(TimeZoneTextProvider.timeZoneTextFor(zone, style, en_US))
        }
    }
}