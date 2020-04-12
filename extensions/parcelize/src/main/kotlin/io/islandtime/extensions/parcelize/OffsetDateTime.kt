package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.DateTime
import io.islandtime.UtcOffset
import io.islandtime.OffsetDateTime
import io.islandtime.measures.seconds
import kotlinx.android.parcel.Parceler

object OffsetDateTimeParceler : Parceler<OffsetDateTime> {
    override fun create(parcel: Parcel): OffsetDateTime = parcel.readOffsetDateTime()

    override fun OffsetDateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeOffsetDateTime(this)
    }
}

object NullableOffsetDateTimeParceler : Parceler<OffsetDateTime?> {
    override fun create(parcel: Parcel): OffsetDateTime? {
        return when (val year = parcel.readInt()) {
            Int.MIN_VALUE -> null
            else -> OffsetDateTime(
                DateTime(
                    year,
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readByte().toInt(),
                    parcel.readInt()
                ),
                UtcOffset(parcel.readInt().seconds)
            )
        }
    }

    override fun OffsetDateTime?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeInt(Int.MIN_VALUE)
        } else {
            parcel.writeOffsetDateTime(this)
        }
    }
}

internal fun Parcel.readOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime(readDateTime(), UtcOffset(readInt().seconds))
}

internal fun Parcel.writeOffsetDateTime(offsetDateTime: OffsetDateTime) {
    writeDateTime(offsetDateTime.dateTime)
    writeInt(offsetDateTime.offset.totalSeconds.value)
}