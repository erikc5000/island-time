package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.ranges.OffsetDateTimeInterval
import kotlinx.parcelize.Parceler

object OffsetDateTimeIntervalParceler : Parceler<OffsetDateTimeInterval> {
    override fun create(parcel: Parcel): OffsetDateTimeInterval {
        return OffsetDateTimeInterval(parcel.readOffsetDateTime(), parcel.readOffsetDateTime())
    }

    override fun OffsetDateTimeInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeOffsetDateTime(start)
        parcel.writeOffsetDateTime(endExclusive)
    }
}
