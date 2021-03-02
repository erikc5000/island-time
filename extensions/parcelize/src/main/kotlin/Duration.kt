package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.measures.Duration
import io.islandtime.measures.durationOf
import io.islandtime.measures.nanoseconds
import io.islandtime.measures.seconds
import kotlinx.parcelize.Parceler

object DurationParceler : Parceler<Duration> {
    override fun create(parcel: Parcel): Duration {
        return durationOf(parcel.readLong().seconds, parcel.readInt().nanoseconds)
    }

    override fun Duration.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(seconds.value)
        parcel.writeInt(nanosecondAdjustment.value)
    }
}

object NullableDurationParceler : Parceler<Duration?> {
    override fun create(parcel: Parcel): Duration? {
        return when {
            parcel.readByte() == NULL_VALUE -> null
            else -> durationOf(parcel.readLong().seconds, parcel.readInt().nanoseconds)
        }
    }

    override fun Duration?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeByte(NULL_VALUE)
        } else {
            parcel.writeByte(NON_NULL_VALUE)
            parcel.writeLong(seconds.value)
            parcel.writeInt(nanosecondAdjustment.value)
        }
    }

    private const val NON_NULL_VALUE = 0.toByte()
    private const val NULL_VALUE = 1.toByte()
}
