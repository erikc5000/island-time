package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.DateTime
import io.islandtime.TimeZone
import io.islandtime.UtcOffset
import io.islandtime.ZonedDateTime
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
            else -> ZonedDateTime.fromLocal(
                DateTime(
                    year,
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readInt()
                ),
                TimeZone(parcel.readString().orEmpty()),
                UtcOffset(parcel.readInt().seconds)
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
    return ZonedDateTime.fromLocal(
        readDateTime(),
        TimeZone(readString().orEmpty()),
        UtcOffset(readInt().seconds)
    )
}

internal fun Parcel.writeZonedDateTime(zonedDateTime: ZonedDateTime) {
    writeDateTime(zonedDateTime.dateTime)
    writeString(zonedDateTime.zone.id)
    writeInt(zonedDateTime.offset.totalSeconds.value)
}