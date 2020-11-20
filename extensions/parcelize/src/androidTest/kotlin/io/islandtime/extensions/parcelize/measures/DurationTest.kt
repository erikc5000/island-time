package io.islandtime.extensions.parcelize.measures

import android.os.Parcelable
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.measures.Duration
import io.islandtime.measures.durationOf
import io.islandtime.measures.hours
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class DurationTest {
    @Parcelize
    @TypeParceler<Duration, DurationParceler>
    data class TestData(val duration: Duration) : Parcelable

    @Parcelize
    @TypeParceler<Duration?, NullableDurationParceler>
    data class TestNullableData(val duration: Duration?) : Parcelable

    private val testDurations = listOf(
        Duration.MIN,
        Duration.MAX,
        Duration.ZERO,
        durationOf(20.hours)
    )

    @Test
    fun durationParceler() {
        testDurations.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableDurationParceler() {
        (testDurations + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
