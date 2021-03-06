package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.ranges.InstantInterval
import kotlinx.parcelize.Parceler

object InstantIntervalParceler : Parceler<InstantInterval> {
    override fun create(parcel: Parcel): InstantInterval {
        return InstantInterval(parcel.readInstant(), parcel.readInstant())
    }

    override fun InstantInterval.write(parcel: Parcel, flags: Int) {
        parcel.writeInstant(start)
        parcel.writeInstant(endExclusive)
    }
}
