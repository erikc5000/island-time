package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.Date
import io.islandtime.Time
import kotlinx.android.parcel.Parceler

object TimeParceler : Parceler<Time> {
    override fun create(parcel: Parcel): Time = parcel.readTime()

    override fun Time.write(parcel: Parcel, flags: Int) {
        parcel.writeTime(this)
    }
}

object NullableTimeParceler : Parceler<Time?> {
    override fun create(parcel: Parcel): Time? {
        return when (val hour = parcel.readByte()) {
            Byte.MIN_VALUE -> null
            else -> Time(hour.toInt(), parcel.readByte().toInt(), parcel.readByte().toInt(), parcel.readInt())
        }
    }

    override fun Time?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeByte(Byte.MIN_VALUE)
        } else {
            parcel.writeTime(this)
        }
    }
}

internal fun Parcel.readTime(): Time {
    return Time(
        readByte().toInt(),
        readByte().toInt(),
        readByte().toInt(),
        readInt()
    )
}

internal fun Parcel.writeTime(time: Time) {
    writeByte(time.hour.toByte())
    writeByte(time.minute.toByte())
    writeByte(time.second.toByte())
    writeInt(time.nanosecond)
}
