package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.toLocale
import io.islandtime.test.AbstractIslandTimeTest
import org.junit.Test
import kotlin.test.assertNull

@Suppress("PrivatePropertyName")
class JvmTimeZoneTextProviderTest : AbstractIslandTimeTest() {
    private val en_US = "en-US".toLocale()
    private val de_DE = "de-DE".toLocale()

    @Test
    fun `timeZoneTextFor() returns null for generic styles - limited by Android currently`() {
        val zone = TimeZone("America/New_York")

        assertNull(TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.GENERIC, en_US))
        assertNull(TimeZoneTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_GENERIC, de_DE))
    }
}