package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.TimeZone
import kotlinx.parcelize.Parceler

object TimeZoneParceler : Parceler<TimeZone> {
    override fun create(parcel: Parcel): TimeZone {
        return TimeZone(parcel.readString().orEmpty())
    }

    override fun TimeZone.write(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
    }
}

object NullableTimeZoneParceler : Parceler<TimeZone?> {
    override fun create(parcel: Parcel): TimeZone? {
        return parcel.readString()?.let { TimeZone(it) }
    }

    override fun TimeZone?.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this?.id)
    }
}
