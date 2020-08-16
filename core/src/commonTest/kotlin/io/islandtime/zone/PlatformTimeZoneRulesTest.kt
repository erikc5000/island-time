package io.islandtime.zone

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlatformTimeZoneRulesTest {
    @Test
    fun `databaseVersion doesn't throw an exception`() {
        PlatformTimeZoneRulesProvider.databaseVersion
    }

    @Test
    fun `availableRegionIds is not empty`() {
        assertTrue { PlatformTimeZoneRulesProvider.availableRegionIds.isNotEmpty() }
    }

    @Test
    fun `each available region ID has time zone rules`() {
        for (regionId in PlatformTimeZoneRulesProvider.availableRegionIds) {
            assertTrue { PlatformTimeZoneRulesProvider.hasRulesFor(regionId) }
            PlatformTimeZoneRulesProvider.rulesFor(regionId)
        }
    }

    @Test
    fun `isFixedOffset returns returns true for Etc entries`() {
        assertTrue { PlatformTimeZoneRulesProvider.rulesFor("Etc/GMT+10").hasFixedOffset }
        assertTrue { PlatformTimeZoneRulesProvider.rulesFor("Etc/GMT-12").hasFixedOffset }
        assertTrue { PlatformTimeZoneRulesProvider.rulesFor("Etc/UTC").hasFixedOffset }
    }

    @Test
    fun `isFixedOffset returns returns false for regions known to have DST`() {
        assertFalse { PlatformTimeZoneRulesProvider.rulesFor("America/New_York").hasFixedOffset }
        assertFalse { PlatformTimeZoneRulesProvider.rulesFor("Europe/London").hasFixedOffset }
    }
}
