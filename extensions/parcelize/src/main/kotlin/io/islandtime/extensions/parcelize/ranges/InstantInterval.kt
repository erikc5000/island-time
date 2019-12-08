package io.islandtime.extensions.parcelize.ranges

import android.os.Parcel
import io.islandtime.extensions.parcelize.readInstant
import io.islandtime.extensions.parcelize.writeInstant
import io.islandtime.ranges.InstantInterval
import kotlinx.android.parcel.Parceler

object InstantIntervalParceler: Parceler<InstantInterval> {
    override fun create(parcel: Parcel): InstantInterval {
        return InstantInterval(parcel.readInstant(), parcel.readInstant())
    }

    override fun InstantInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeInstant(start)
        parcel.writeInstant(endExclusive)
    }
}