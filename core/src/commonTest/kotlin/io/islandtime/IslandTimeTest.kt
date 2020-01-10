package io.islandtime

import io.islandtime.format.PlatformDateTimeTextProvider
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class IslandTimeTest {
    @BeforeTest
    fun setUp() {
        IslandTime.reset()
    }

    @AfterTest
    fun tearDown() {
        IslandTime.reset()
    }

    @Test
    fun `double initialization causes an exception`() {
        IslandTime.initialize {
            dateTimeTextProvider = PlatformDateTimeTextProvider
        }

        assertFailsWith<IllegalStateException> {
            IslandTime.initialize {
                timeZoneRulesProvider = PlatformTimeZoneRulesProvider
            }
        }
    }
}