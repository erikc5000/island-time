package io.islandtime.test

import io.islandtime.format.DateTimeFormatProvider
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.TimeZoneNameProvider
import io.islandtime.zone.TimeZoneRulesProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractIslandTimeTest(
    private val timeZoneRulesProvider: TimeZoneRulesProvider? = null,
    private val dateTimeFormatProvider: DateTimeFormatProvider? = null,
    private val dateTimeTextProvider: DateTimeTextProvider? = null,
    private val timeZoneNameProvider: TimeZoneNameProvider? = null
) {
    @BeforeTest
    fun setUp() {
        timeZoneRulesProvider?.let { TimeZoneRulesProvider.set(it) }
        dateTimeTextProvider?.let { DateTimeTextProvider.set(it) }
        dateTimeFormatProvider?.let { DateTimeFormatProvider.set(it) }
        timeZoneNameProvider?.let { TimeZoneNameProvider.set(it) }
    }

    @AfterTest
    fun tearDown() {
        TimeZoneRulesProvider.reset()
        DateTimeTextProvider.reset()
        DateTimeFormatProvider.reset()
        TimeZoneNameProvider.reset()
    }
}
