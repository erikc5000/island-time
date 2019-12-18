package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DarwinDateTimeTextProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")

    @Test
    fun `timeZoneTextFor() returns a localized string for generic styles`() {
        val zone = TimeZone("America/New_York")

        assertEquals(
            "Eastern Time",
            DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.GENERIC, en_US)
        )
        assertEquals(
            "ET",
            DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_GENERIC, en_US)
        )
    }
}