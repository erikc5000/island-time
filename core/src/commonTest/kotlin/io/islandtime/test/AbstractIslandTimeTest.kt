package io.islandtime.test

import io.islandtime.IslandTime
import io.islandtime.format.*
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import io.islandtime.zone.TimeZoneRulesProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractIslandTimeTest(
    private val testTimeZoneRulesProvider: TimeZoneRulesProvider = PlatformTimeZoneRulesProvider,
    private val testDateTimeFormatProvider: DateTimeFormatProvider = PlatformDateTimeFormatProvider,
    private val testDateTimeTextProvider: DateTimeTextProvider = PlatformDateTimeTextProvider,
    private val testTimeZoneTextProvider: TimeZoneTextProvider = PlatformTimeZoneTextProvider
) {
    @BeforeTest
    fun setUp() {
        IslandTime.reset()

        IslandTime.initialize {
            timeZoneRulesProvider = testTimeZoneRulesProvider
            dateTimeFormatProvider = testDateTimeFormatProvider
            dateTimeTextProvider = testDateTimeTextProvider
            timeZoneTextProvider = testTimeZoneTextProvider
        }
    }

    @AfterTest
    fun tearDown() {
        IslandTime.reset()
    }
}