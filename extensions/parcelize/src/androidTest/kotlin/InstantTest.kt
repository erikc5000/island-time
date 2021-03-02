package io.islandtime.parcelize

import android.os.Parcelable
import io.islandtime.Instant
import io.islandtime.parcelize.test.testParcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class InstantTest {
    @Parcelize
    @TypeParceler<Instant, InstantParceler>
    data class TestData(val instant: Instant) : Parcelable

    @Parcelize
    @TypeParceler<Instant?, NullableInstantParceler>
    data class TestNullableData(val instant: Instant?) : Parcelable

    private val testInstants = listOf(
        Instant.MIN,
        Instant.MAX,
        Instant.UNIX_EPOCH
    )

    @Test
    fun instantParceler() {
        testInstants.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableInstantParceler() {
        (testInstants + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
