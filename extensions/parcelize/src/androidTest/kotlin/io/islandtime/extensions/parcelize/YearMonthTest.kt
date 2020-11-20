package io.islandtime.extensions.parcelize

import android.os.Parcelable
import io.islandtime.YearMonth
import io.islandtime.Month
import io.islandtime.extensions.parcelize.test.testParcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class YearMonthTest {
    @Parcelize
    @TypeParceler<YearMonth, YearMonthParceler>
    data class TestData(val yearMonth: YearMonth) : Parcelable

    @Parcelize
    @TypeParceler<YearMonth?, NullableYearMonthParceler>
    data class TestNullableData(val yearMonth: YearMonth?) : Parcelable

    private val testYearMonths = listOf(
        YearMonth.MIN,
        YearMonth.MAX,
        YearMonth(2019, Month.DECEMBER)
    )

    @Test
    fun yearMonthParceler() {
        testYearMonths.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableYearMonthParceler() {
        (testYearMonths + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
