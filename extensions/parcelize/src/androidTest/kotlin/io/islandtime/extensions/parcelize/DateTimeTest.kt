package io.islandtime.extensions.parcelize

import android.os.Parcelable
import io.islandtime.DateTime
import io.islandtime.Month
import io.islandtime.extensions.parcelize.test.testParcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.Test

class DateTimeTimeTest {
    @Parcelize
    @TypeParceler<DateTime, DateTimeParceler>
    data class TestData(val dateTime: DateTime) : Parcelable

    @Parcelize
    @TypeParceler<DateTime?, NullableDateTimeParceler>
    data class TestNullableData(val dateTime: DateTime?) : Parcelable

    private val testDateTimes = listOf(
        DateTime.MIN,
        DateTime.MAX,
        DateTime(2019, Month.DECEMBER, 5, 1, 2, 3, 4)
    )

    @Test
    fun dateTimeParceler() {
        testDateTimes.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableDateTimeParceler() {
        (testDateTimes + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
