package io.islandtime.extensions.parcelize

import android.os.Parcelable
import io.islandtime.TimeZone
import io.islandtime.extensions.parcelize.test.testParcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.Test

class TimeZoneTest {
    @Parcelize
    @TypeParceler<TimeZone, TimeZoneParceler>
    data class TestData(val timeZone: TimeZone) : Parcelable

    @Parcelize
    @TypeParceler<TimeZone?, NullableTimeZoneParceler>
    data class TestNullableData(val timeZone: TimeZone?) : Parcelable

    private val testTimeZones = listOf(
        TimeZone.UTC,
        TimeZone("-04:00"),
        TimeZone("America/New_York"),
        TimeZone("America/Denver")
    )

    @Test
    fun timeZoneParceler() {
        testTimeZones.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableTimeZoneParceler() {
        (testTimeZones + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
