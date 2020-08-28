package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.OffsetTime
import io.islandtime.UtcOffset
import io.islandtime.measures.seconds
import kotlinx.android.parcel.Parceler

object OffsetTimeParceler : Parceler<OffsetTime> {
    override fun create(parcel: Parcel): OffsetTime {
        return OffsetTime(
            parcel.readByte().toInt(),
            parcel.readByte().toInt(),
            parcel.readByte().toInt(),
            parcel.readInt(),
            UtcOffset(parcel.readInt().seconds)
        )
    }

    override fun OffsetTime.write(parcel: Parcel, flags: Int) {
        parcel.writeByte(hour.toByte())
        parcel.writeByte(minute.toByte())
        parcel.writeByte(second.toByte())
        parcel.writeInt(nanosecond)
        parcel.writeInt(offset.totalSeconds.value)
    }
}

object NullableOffsetTimeParceler : Parceler<OffsetTime?> {
    override fun create(parcel: Parcel): OffsetTime? {
        return when (val hour = parcel.readByte()) {
            Byte.MIN_VALUE -> null
            else -> OffsetTime(
                hour.toInt(),
                parcel.readByte().toInt(),
                parcel.readByte().toInt(),
                parcel.readInt(),
                UtcOffset(parcel.readInt().seconds)
            )
        }
    }

    override fun OffsetTime?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeByte(Byte.MIN_VALUE)
        } else {
            parcel.writeByte(hour.toByte())
            parcel.writeByte(minute.toByte())
            parcel.writeByte(second.toByte())
            parcel.writeInt(nanosecond)
            parcel.writeInt(offset.totalSeconds.value)
        }
    }
}
