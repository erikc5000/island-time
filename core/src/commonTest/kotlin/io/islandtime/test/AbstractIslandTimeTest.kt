package io.islandtime.test

import io.islandtime.IslandTime
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.PlatformDateTimeTextProvider
import io.islandtime.format.PlatformTimeZoneTextProvider
import io.islandtime.format.TimeZoneTextProvider
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import io.islandtime.zone.TimeZoneRulesProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractIslandTimeTest(
    private val testTimeZoneRulesProvider: TimeZoneRulesProvider = PlatformTimeZoneRulesProvider,
    private val testDateTimeTextProvider: DateTimeTextProvider = PlatformDateTimeTextProvider,
    private val testTimeZoneTextProvider: TimeZoneTextProvider = PlatformTimeZoneTextProvider
) {
    @BeforeTest
    fun setUp() {
        IslandTime.reset()

        IslandTime.initialize {
            timeZoneRulesProvider = testTimeZoneRulesProvider
            dateTimeTextProvider = testDateTimeTextProvider
            timeZoneTextProvider = testTimeZoneTextProvider
        }
    }

    @AfterTest
    fun tearDown() {
        IslandTime.reset()
    }
}