package io.islandtime.extensions.parcelize.ranges

import android.os.Parcelable
import io.islandtime.Date
import io.islandtime.ranges.DateRange
import io.islandtime.Month
import io.islandtime.extensions.parcelize.test.testParcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.Test

class DateRangeRangeTest {
    @Parcelize
    @TypeParceler<DateRange, DateRangeParceler>
    data class TestData(val dateRange: DateRange) : Parcelable

    private val testDateRanges = listOf(
        DateRange.UNBOUNDED,
        DateRange.EMPTY,
        Date(2019, Month.NOVEMBER, 3)..Date(2019, Month.DECEMBER, 5)
    )

    @Test
    fun dateRangeParceler() {
        testDateRanges.forEach { testParcelable(TestData(it)) }
    }
}