package io.islandtime.extensions.parcelize.ranges

import android.os.Parcel
import io.islandtime.extensions.parcelize.readZonedDateTime
import io.islandtime.extensions.parcelize.writeZonedDateTime
import io.islandtime.ranges.ZonedDateTimeInterval
import kotlinx.android.parcel.Parceler

object ZonedDateTimeIntervalParceler: Parceler<ZonedDateTimeInterval> {
    override fun create(parcel: Parcel): ZonedDateTimeInterval {
        return ZonedDateTimeInterval(parcel.readZonedDateTime(), parcel.readZonedDateTime())
    }

    override fun ZonedDateTimeInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeZonedDateTime(start)
        parcel.writeZonedDateTime(endExclusive)
    }
}
