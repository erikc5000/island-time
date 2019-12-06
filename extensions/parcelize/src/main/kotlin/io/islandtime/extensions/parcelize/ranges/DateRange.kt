package io.islandtime.extensions.parcelize.ranges

import android.os.Parcel
import io.islandtime.extensions.parcelize.readDate
import io.islandtime.extensions.parcelize.writeDate
import io.islandtime.ranges.DateRange
import kotlinx.android.parcel.Parceler

object DateRangeParceler: Parceler<DateRange> {
    override fun create(parcel: Parcel): DateRange = DateRange(parcel.readDate(), parcel.readDate())

    override fun DateRange.write(parcel: Parcel, flags: Int) {
        parcel.writeDate(start)
        parcel.writeDate(endInclusive)
    }
}