package io.islandtime.extensions.parcelize.ranges

import android.os.Parcel
import io.islandtime.extensions.parcelize.readOffsetDateTime
import io.islandtime.extensions.parcelize.writeOffsetDateTime
import io.islandtime.ranges.OffsetDateTimeInterval
import kotlinx.android.parcel.Parceler

object OffsetDateTimeIntervalParceler: Parceler<OffsetDateTimeInterval> {
    override fun create(parcel: Parcel): OffsetDateTimeInterval {
        return OffsetDateTimeInterval(parcel.readOffsetDateTime(), parcel.readOffsetDateTime())
    }

    override fun OffsetDateTimeInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeOffsetDateTime(start)
        parcel.writeOffsetDateTime(endExclusive)
    }
}