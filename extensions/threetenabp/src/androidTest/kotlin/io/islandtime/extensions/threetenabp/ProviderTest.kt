package io.islandtime.extensions.threetenabp

import org.junit.Test
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import io.islandtime.IslandTime
import io.islandtime.zone.TimeZoneRulesProvider
import org.junit.After

class AndroidThreeTenProviderTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext

    @After
    fun tearDown() {
        IslandTime.reset()
    }

    @Test
    fun sanityCheck() {
        IslandTime.initializeWith(AndroidThreeTenProvider(context))

        assertThat(TimeZoneRulesProvider.databaseVersion).isNotEmpty()
        assertThat(TimeZoneRulesProvider.availableRegionIds).isNotEmpty()
    }

    @Test(expected = IllegalStateException::class)
    fun doubleInitializationCausesException() {
        IslandTime.initializeWith(AndroidThreeTenProvider(context))
        IslandTime.initializeWith(AndroidThreeTenProvider(context))
    }
}