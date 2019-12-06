package io.islandtime.extensions.parcelize.ranges

import android.os.Parcel
import io.islandtime.extensions.parcelize.readDateTime
import io.islandtime.extensions.parcelize.writeDateTime
import io.islandtime.ranges.DateTimeInterval
import kotlinx.android.parcel.Parceler

object DateTimeIntervalParceler: Parceler<DateTimeInterval> {
    override fun create(parcel: Parcel): DateTimeInterval {
        return DateTimeInterval(parcel.readDateTime(), parcel.readDateTime())
    }

    override fun DateTimeInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeDateTime(start)
        parcel.writeDateTime(endExclusive)
    }
}