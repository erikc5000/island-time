package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.ranges.DateRange
import kotlinx.parcelize.Parceler

object DateRangeParceler : Parceler<DateRange> {
    override fun create(parcel: Parcel): DateRange = DateRange(parcel.readDate(), parcel.readDate())

    override fun DateRange.write(parcel: Parcel, flags: Int) {
        parcel.writeDate(start)
        parcel.writeDate(endInclusive)
    }
}
