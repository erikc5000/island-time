package io.islandtime.extensions.parcelize.ranges

import android.os.Parcelable
import androidx.test.platform.app.InstrumentationRegistry
import io.islandtime.IslandTime
import io.islandtime.ZonedDateTime
import io.islandtime.ranges.ZonedDateTimeInterval
import io.islandtime.TimeZone
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.extensions.threetenabp.AndroidThreeTenProvider
import io.islandtime.ranges.until
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.*

class ZonedDateTimeIntervalRangeTest {
    @Parcelize
    @TypeParceler<ZonedDateTimeInterval, ZonedDateTimeIntervalParceler>
    data class TestData(val zonedDateTimeInterval: ZonedDateTimeInterval) : Parcelable

    private val testZone = TimeZone("America/New_York")

    private val testZonedDateTimeIntervals = listOf(
        ZonedDateTimeInterval.UNBOUNDED,
        ZonedDateTimeInterval.EMPTY,
        ZonedDateTime(2019, 3, 3, 1, 2, 3, testZone) until
            ZonedDateTime(2019, 15, 5, 5, 6, 7, testZone)
    )

    @Test
    fun zonedDateTimeIntervalParceler() {
        testZonedDateTimeIntervals.forEach { testParcelable(TestData(it)) }
    }

    companion object {
        private val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext

        @JvmStatic
        @BeforeClass
        fun setUp() {
            IslandTime.initializeWith(AndroidThreeTenProvider(context))
        }


        @JvmStatic
        @AfterClass
        fun tearDown() {
            IslandTime.reset()
        }
    }
}