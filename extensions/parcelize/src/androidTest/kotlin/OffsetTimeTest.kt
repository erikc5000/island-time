package io.islandtime.parcelize

import android.os.Parcelable
import io.islandtime.OffsetTime
import io.islandtime.UtcOffset
import io.islandtime.parcelize.test.testParcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class OffsetOffsetTimeTest {
    @Parcelize
    @TypeParceler<OffsetTime, OffsetTimeParceler>
    data class TestData(val offsetTime: OffsetTime) : Parcelable

    @Parcelize
    @TypeParceler<OffsetTime?, NullableOffsetTimeParceler>
    data class TestNullableData(val offsetTime: OffsetTime?) : Parcelable

    private val testOffsetTimes = listOf(
        OffsetTime.MIN,
        OffsetTime.MAX,
        OffsetTime(1, 2, 3, 4, UtcOffset.MIN),
        OffsetTime(1, 2, 3, 4, UtcOffset.MAX),
        OffsetTime(1, 2, 3, 4, UtcOffset.ZERO)
    )

    @Test
    fun offsetTimeParceler() {
        testOffsetTimes.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableOffsetTimeParceler() {
        (testOffsetTimes + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
