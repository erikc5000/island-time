package io.islandtime.extensions.parcelize

import android.os.Parcelable
import io.islandtime.Time
import io.islandtime.extensions.parcelize.test.testParcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class TimeTest {
    @Parcelize
    @TypeParceler<Time, TimeParceler>
    data class TestData(val time: Time) : Parcelable

    @Parcelize
    @TypeParceler<Time?, NullableTimeParceler>
    data class TestNullableData(val time: Time?) : Parcelable

    private val testTimes = listOf(
        Time.MIN,
        Time.MAX,
        Time(1, 2, 3, 4)
    )

    @Test
    fun timeParceler() {
        testTimes.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableTimeParceler() {
        (testTimes + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
