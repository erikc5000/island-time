package io.islandtime.extensions.parcelize

import android.os.Parcel
import io.islandtime.Date
import kotlinx.android.parcel.Parceler

object DateParceler : Parceler<Date> {
    override fun create(parcel: Parcel): Date = parcel.readDate()

    override fun Date.write(parcel: Parcel, flags: Int) {
        parcel.writeDate(this)
    }
}

object NullableDateParceler : Parceler<Date?> {
    override fun create(parcel: Parcel): Date? {
        return when (val year = parcel.readInt()) {
            Int.MIN_VALUE -> null
            else -> Date(year, parcel.readByte().toInt(), parcel.readByte().toInt())
        }
    }

    override fun Date?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeInt(Int.MIN_VALUE)
        } else {
            parcel.writeDate(this)
        }
    }
}

internal fun Parcel.readDate(): Date {
    return Date(readInt(), readByte().toInt(), readByte().toInt())
}

internal fun Parcel.writeDate(date: Date) {
    writeInt(date.year)
    writeByte(date.monthNumber.toByte())
    writeByte(date.dayOfMonth.toByte())
}
