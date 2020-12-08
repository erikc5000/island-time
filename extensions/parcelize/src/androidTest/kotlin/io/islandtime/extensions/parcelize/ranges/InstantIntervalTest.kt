package io.islandtime.extensions.parcelize.ranges

import android.os.Parcelable
import io.islandtime.Instant
import io.islandtime.ranges.InstantInterval
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.measures.seconds
import io.islandtime.ranges.until
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class InstantIntervalRangeTest {
    @Parcelize
    @TypeParceler<InstantInterval, InstantIntervalParceler>
    data class TestData(val instantInterval: InstantInterval) : Parcelable

    private val testInstantIntervals = listOf(
        InstantInterval.UNBOUNDED,
        InstantInterval.EMPTY,
        Instant((-1L).seconds) until Instant(1L.seconds)
    )

    @Test
    fun instantIntervalParceler() {
        testInstantIntervals.forEach { testParcelable(TestData(it)) }
    }
}
