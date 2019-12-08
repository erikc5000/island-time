package io.islandtime.extensions.parcelize

import android.os.Parcelable
import io.islandtime.Date
import io.islandtime.Month
import io.islandtime.extensions.parcelize.test.testParcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.Test

class DateTest {
    @Parcelize
    @TypeParceler<Date, DateParceler>
    data class TestData(val date: Date) : Parcelable

    @Parcelize
    @TypeParceler<Date?, NullableDateParceler>
    data class TestNullableData(val date: Date?) : Parcelable

    private val testDates = listOf(
        Date.MIN,
        Date.MAX,
        Date(2019, Month.DECEMBER, 5)
    )

    @Test
    fun dateParceler() {
        testDates.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableDateParceler() {
        (testDates + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}