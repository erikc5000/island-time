package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.toLocale
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@Suppress("PrivatePropertyName")
class TimeZoneNameProviderTest : AbstractIslandTimeTest() {
    private val en_US = "en-US".toLocale()
    private val de_DE = "de-DE".toLocale()

    @Test
    fun `returns null when given a fixed offset time zone`() {
        listOf(
            "-04:00",
            "+00:00",
            "+14:00"
        ).forEach { offsetId ->
            TimeZoneNameStyle.values().forEach { style ->
                assertNull(TimeZoneNameProvider.getNameFor(offsetId, style, en_US))
                assertNull(TimeZoneNameProvider.getNameFor(offsetId, style, de_DE))
            }
        }
    }

    @Test
    fun `returns a localized string when available`() {
        val regionId = "America/New_York"

        listOf(
            TimeZoneNameStyle.LONG_STANDARD to "Eastern Standard Time",
            TimeZoneNameStyle.SHORT_STANDARD to "EST",
            TimeZoneNameStyle.LONG_DAYLIGHT to "Eastern Daylight Time",
            TimeZoneNameStyle.SHORT_DAYLIGHT to "EDT",
            TimeZoneNameStyle.LONG_GENERIC to "Eastern Time",
            TimeZoneNameStyle.SHORT_GENERIC to "ET"
        ).forEach { (style, expectedResult) ->
            assertEquals(expectedResult, TimeZoneNameProvider.getNameFor(regionId, style, en_US), message = "$style")
        }
    }

    @Test
    fun `returns null when the zone is invalid`() {
        val zone = TimeZone("America/Boston")
        assertFalse { zone.isValid }

        TimeZoneNameStyle.values().forEach { style ->
            assertNull(TimeZoneNameProvider.getNameFor(zone.id, style, en_US))
        }
    }
}
