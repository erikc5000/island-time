package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.Instant
import kotlinx.android.parcel.Parceler

object InstantParceler : Parceler<Instant> {
    override fun create(parcel: Parcel): Instant = parcel.readInstant()

    override fun Instant.write(parcel: Parcel, flags: Int) {
        parcel.writeInstant(this)
    }
}

object NullableInstantParceler : Parceler<Instant?> {
    override fun create(parcel: Parcel): Instant? {
        return when (val second = parcel.readLong()) {
            Long.MIN_VALUE -> null
            else -> Instant.fromSecondOfUnixEpoch(second, parcel.readInt())
        }
    }

    override fun Instant?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeLong(Long.MIN_VALUE)
        } else {
            parcel.writeInstant(this)
        }
    }
}

internal fun Parcel.readInstant(): Instant {
    return Instant.fromSecondOfUnixEpoch(readLong(), readInt())
}

internal fun Parcel.writeInstant(instant: Instant) {
    writeLong(instant.secondOfUnixEpoch)
    writeInt(instant.nanosecond)
}