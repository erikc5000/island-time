package io.islandtime.extensions.parcelize.ranges

import android.os.Parcelable
import io.islandtime.OffsetDateTime
import io.islandtime.ranges.OffsetDateTimeInterval
import io.islandtime.UtcOffset
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.measures.hours
import io.islandtime.ranges.until
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class OffsetDateTimeIntervalRangeTest {
    @Parcelize
    @TypeParceler<OffsetDateTimeInterval, OffsetDateTimeIntervalParceler>
    data class TestData(val offsetDateTimeInterval: OffsetDateTimeInterval) : Parcelable

    private val testOffset = UtcOffset((-4).hours)

    private val testOffsetDateTimeIntervals = listOf(
        OffsetDateTimeInterval.UNBOUNDED,
        OffsetDateTimeInterval.EMPTY,
        OffsetDateTime(2019, 3, 3, 1, 2, 3, testOffset) until
            OffsetDateTime(2019, 15, 5, 5, 6, 7, testOffset)
    )

    @Test
    fun offsetDateTimeIntervalParceler() {
        testOffsetDateTimeIntervals.forEach { testParcelable(TestData(it)) }
    }
}
