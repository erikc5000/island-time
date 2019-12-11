package io.islandtime.extensions.parcelize

import android.os.Parcelable
import androidx.test.platform.app.InstrumentationRegistry
import io.islandtime.*
import io.islandtime.extensions.parcelize.test.testParcelable
import io.islandtime.extensions.threetenabp.AndroidThreeTenProvider
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import org.junit.*

class ZonedDateTimeTimeTest {
    @Parcelize
    @TypeParceler<ZonedDateTime, ZonedDateTimeParceler>
    data class TestData(val zonedDateTime: ZonedDateTime) : Parcelable

    @Parcelize
    @TypeParceler<ZonedDateTime?, NullableZonedDateTimeParceler>
    data class TestNullableData(val zonedDateTime: ZonedDateTime?) : Parcelable

    private val testZonedDateTimes = listOf(
        DateTime.MIN at TimeZone.UTC,
        DateTime.MIN at TimeZone("America/New_York"),
        DateTime.MAX at TimeZone.UTC,
        DateTime.MAX at TimeZone("America/New_York"),
        DateTime(2019, Month.DECEMBER, 5, 1, 2, 3, 4)
                at TimeZone("America/Denver")
    )

    @Test
    fun zonedDateTimeParceler() {
        testZonedDateTimes.forEach { testParcelable(TestData(it)) }
    }

    @Test
    fun nullableZonedDateTimeParceler() {
        (testZonedDateTimes + listOf(null)).forEach { testParcelable(TestNullableData(it)) }
    }

    companion object {
        private val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext

        @JvmStatic
        @BeforeClass
        fun setUp() {
            IslandTime.initializeWith(AndroidThreeTenProvider(context))
        }


        @JvmStatic
        @AfterClass
        fun tearDown() {
            IslandTime.reset()
        }
    }
}