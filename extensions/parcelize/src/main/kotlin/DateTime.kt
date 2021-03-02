package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.DateTime
import kotlinx.parcelize.Parceler

object DateTimeParceler : Parceler<DateTime> {
    override fun create(parcel: Parcel): DateTime = parcel.readDateTime()

    override fun DateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeDateTime(this)
    }
}

object NullableDateTimeParceler : Parceler<DateTime?> {
    override fun create(parcel: Parcel): DateTime? {
        return when (val year = parcel.readInt()) {
            Int.MIN_VALUE -> null
            else -> DateTime(
                year,
                parcel.readByte().toInt(),
                parcel.readByte().toInt(),
                parcel.readByte().toInt(),
                parcel.readByte().toInt(),
                parcel.readByte().toInt(),
                parcel.readInt()
            )
        }
    }

    override fun DateTime?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeInt(Int.MIN_VALUE)
        } else {
            parcel.writeDateTime(this)
        }
    }
}

internal fun Parcel.readDateTime(): DateTime {
    return DateTime(readDate(), readTime())
}

internal fun Parcel.writeDateTime(dateTime: DateTime) {
    writeDate(dateTime.date)
    writeTime(dateTime.time)
}
