package io.islandtime.extensions.parcelize.ranges

import android.os.Parcelable
import io.islandtime.IslandTime
import io.islandtime.TimeZone
import io.islandtime.ZonedDateTime
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.ranges.ZonedDateTimeInterval
import io.islandtime.ranges.until
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

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
        @JvmStatic
        @BeforeClass
        fun setUp() {
            IslandTime.reset()
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            IslandTime.reset()
        }
    }
}