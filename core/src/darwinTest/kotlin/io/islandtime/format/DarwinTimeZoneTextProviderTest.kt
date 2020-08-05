package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.toLocale
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DarwinTimeZoneTextProviderTest : AbstractIslandTimeTest() {
    @Suppress("PrivatePropertyName")
    private val en_US = "en-US".toLocale()

    @Test
    fun `timeZoneTextFor() returns a localized string for generic styles`() {
        val zone = TimeZone("America/New_York")

        assertEquals(
            "Eastern Time",
            TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.GENERIC, en_US)
        )
        assertEquals(
            "ET",
            TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_GENERIC, en_US)
        )
    }
}
