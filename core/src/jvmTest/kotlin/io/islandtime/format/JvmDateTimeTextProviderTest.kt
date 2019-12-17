package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.localeFor
import io.islandtime.test.AbstractIslandTimeTest
import org.junit.Test
import kotlin.test.assertNull

class JvmDateTimeTextProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeFor("en-US")
    private val de_DE = localeFor("de-DE")

    @Test
    fun `timeZoneTextFor() returns null for generic styles - limited by Android currently`() {
        val zone = TimeZone("America/New_York")

        assertNull(DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.GENERIC, en_US))
        assertNull(DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_GENERIC, de_DE))
    }
}