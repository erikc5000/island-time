package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.YearMonth
import kotlinx.parcelize.Parceler

object YearMonthParceler : Parceler<YearMonth> {
    override fun create(parcel: Parcel): YearMonth {
        return YearMonth(parcel.readInt(), parcel.readByte().toInt())
    }

    override fun YearMonth.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(year)
        parcel.writeByte(monthNumber.toByte())
    }
}

object NullableYearMonthParceler : Parceler<YearMonth?> {
    override fun create(parcel: Parcel): YearMonth? {
        return when (val year = parcel.readInt()) {
            Int.MIN_VALUE -> null
            else -> YearMonth(year, parcel.readByte().toInt())
        }
    }

    override fun YearMonth?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeInt(Int.MIN_VALUE)
        } else {
            parcel.writeInt(year)
            parcel.writeByte(monthNumber.toByte())
        }
    }
}
