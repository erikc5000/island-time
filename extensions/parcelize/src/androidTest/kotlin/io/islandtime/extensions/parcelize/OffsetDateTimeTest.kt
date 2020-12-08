package io.islandtime.extensions.parcelize

import android.os.Parcelable
import io.islandtime.*
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.measures.hours
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class OffsetDateTimeTimeTest {
    @Parcelize
    @TypeParceler<OffsetDateTime, OffsetDateTimeParceler>
    data class TestData(val offsetDateTime: OffsetDateTime) : Parcelable

    @Parcelize
    @TypeParceler<OffsetDateTime?, NullableOffsetDateTimeParceler>
    data class TestNullableData(val offsetDateTime: OffsetDateTime?) : Parcelable

    private val testOffsetDateTimes = listOf(
        OffsetDateTime.MIN,
        OffsetDateTime.MAX,
        DateTime(2019, Month.DECEMBER, 5, 1, 2, 3, 4)
            at UtcOffset((-4).hours),
        DateTime(1919, Month.DECEMBER, 5, 1, 2, 3, 4)
            at UtcOffset.ZERO
    )

    @Test
    fun offsetDateTimeParceler() {
        testOffsetDateTimes.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableOffsetDateTimeParceler() {
        (testOffsetDateTimes + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
