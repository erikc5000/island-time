package io.islandtime.zone

import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeZoneRulesProviderTest : AbstractIslandTimeTest() {
    @Test
    fun `databaseVersion doesn't throw an exception`() {
        TimeZoneRulesProvider.databaseVersion
    }

    @Test
    fun `availableRegionIds is not empty`() {
        assertTrue { TimeZoneRulesProvider.availableRegionIds.isNotEmpty() }
    }

    @Test
    fun `each available region ID has time zone rules`() {
        for (regionId in TimeZoneRulesProvider.availableRegionIds) {
            assertTrue { TimeZoneRulesProvider.hasRulesFor(regionId) }
            TimeZoneRulesProvider.getRulesFor(regionId)
        }
    }

    @Test
    fun `isFixedOffset returns returns true for Etc entries`() {
        assertTrue { TimeZoneRulesProvider.getRulesFor("Etc/GMT+10").hasFixedOffset }
        assertTrue { TimeZoneRulesProvider.getRulesFor("Etc/GMT-12").hasFixedOffset }
        assertTrue { TimeZoneRulesProvider.getRulesFor("Etc/UTC").hasFixedOffset }
    }

    @Test
    fun `isFixedOffset returns returns false for regions known to have DST`() {
        assertFalse { TimeZoneRulesProvider.getRulesFor("America/New_York").hasFixedOffset }
        assertFalse { TimeZoneRulesProvider.getRulesFor("Europe/London").hasFixedOffset }
    }
}
