package io.islandtime.test

import io.islandtime.IslandTime
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import io.islandtime.zone.TimeZoneRulesProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractIslandTimeTest(
    private val timeZoneRulesProvider: TimeZoneRulesProvider = PlatformTimeZoneRulesProvider
) {
    @BeforeTest
    fun setUp() {
        IslandTime.reset()
        IslandTime.initializeWith(timeZoneRulesProvider)
    }

    @AfterTest
    fun tearDown() {
        IslandTime.reset()
    }
}
