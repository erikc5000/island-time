package io.islandtime.extensions.parcelize.ranges

import android.os.Parcelable
import io.islandtime.DateTime
import io.islandtime.ranges.DateTimeInterval
import io.islandtime.Month
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.ranges.until
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.Test

class DateTimeIntervalRangeTest {
    @Parcelize
    @TypeParceler<DateTimeInterval, DateTimeIntervalParceler>
    data class TestData(val dateTimeInterval: DateTimeInterval) : Parcelable

    private val testDateTimeIntervals = listOf(
        DateTimeInterval.UNBOUNDED,
        DateTimeInterval.EMPTY,
        DateTime(2019, Month.NOVEMBER, 3, 1, 2, 3, 4) until
            DateTime(2019, Month.DECEMBER, 5, 5, 6, 7, 8)
    )

    @Test
    fun dateTimeIntervalParceler() {
        testDateTimeIntervals.forEach { testParcelable(TestData(it)) }
    }
}