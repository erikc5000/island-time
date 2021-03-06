package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.ranges.ZonedDateTimeInterval
import kotlinx.parcelize.Parceler

object ZonedDateTimeIntervalParceler : Parceler<ZonedDateTimeInterval> {
    override fun create(parcel: Parcel): ZonedDateTimeInterval {
        return ZonedDateTimeInterval(parcel.readZonedDateTime(), parcel.readZonedDateTime())
    }

    override fun ZonedDateTimeInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeZonedDateTime(start)
        parcel.writeZonedDateTime(endExclusive)
    }
}
