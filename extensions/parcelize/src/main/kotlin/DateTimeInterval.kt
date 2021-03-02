package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.ranges.DateTimeInterval
import kotlinx.parcelize.Parceler

object DateTimeIntervalParceler: Parceler<DateTimeInterval> {
    override fun create(parcel: Parcel): DateTimeInterval {
        return DateTimeInterval(parcel.readDateTime(), parcel.readDateTime())
    }

    override fun DateTimeInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeDateTime(start)
        parcel.writeDateTime(endExclusive)
    }
}
