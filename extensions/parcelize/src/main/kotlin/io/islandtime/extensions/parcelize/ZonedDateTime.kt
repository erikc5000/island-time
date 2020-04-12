package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.*
import io.islandtime.measures.seconds
import kotlinx.android.parcel.Parceler

object ZonedDateTimeParceler : Parceler<ZonedDateTime> {
    override fun create(parcel: Parcel): ZonedDateTime = parcel.readZonedDateTime()

    override fun ZonedDateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeZonedDateTime(this)
    }
}

object NullableZonedDateTimeParceler : Parceler<ZonedDateTime?> {
    override fun create(parcel: Parcel): ZonedDateTime? {
        return when (val year = parcel.readInt()) {
            Int.MIN_VALUE -> null
            else -> ZonedDateTime.fromInstant(
                DateTime(
                    year,
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readInt()
                ),
                UtcOffset(parcel.readInt().seconds),
                TimeZone(parcel.readString().orEmpty())
            )
        }
    }

    override fun ZonedDateTime?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeInt(Int.MIN_VALUE)
        } else {
            parcel.writeZonedDateTime(this)
        }
    }
}

internal fun Parcel.readZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.fromInstant(
        readDateTime(),
        UtcOffset(readInt().seconds),
        TimeZone(readString().orEmpty())
    )
}

internal fun Parcel.writeZonedDateTime(zonedDateTime: ZonedDateTime) {
    writeDateTime(zonedDateTime.dateTime)
    writeInt(zonedDateTime.offset.totalSeconds.value)
    writeString(zonedDateTime.zone.id)
}