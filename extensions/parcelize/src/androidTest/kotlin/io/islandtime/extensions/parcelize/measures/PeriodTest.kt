package io.islandtime.extensions.parcelize.measures

import android.os.Parcelable
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.measures.*
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.junit.Test

class PeriodTest {
    @Parcelize
    @TypeParceler<Period, PeriodParceler>
    data class TestData(val period: Period) : Parcelable

    @Parcelize
    @TypeParceler<Period?, NullablePeriodParceler>
    data class TestNullableData(val period: Period?) : Parcelable

    private val testPeriods = listOf(
        periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months, Int.MIN_VALUE.days),
        periodOf(Int.MAX_VALUE.years, Int.MAX_VALUE.months, Int.MAX_VALUE.days),
        Period.ZERO,
        periodOf(1.years),
        periodOf(1.months),
        periodOf(1.days)
    )

    @Test
    fun periodParceler() {
        testPeriods.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullablePeriodParceler() {
        (testPeriods + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }
}
