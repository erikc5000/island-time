package io.islandtime.parcelize

import android.os.Parcel
import io.islandtime.measures.*
import kotlinx.parcelize.Parceler

object PeriodParceler : Parceler<Period> {
    override fun create(parcel: Parcel): Period {
        return periodOf(
            parcel.readLong().years,
            parcel.readLong().months,
            parcel.readLong().days
        )
    }

    override fun Period.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(years.value)
        parcel.writeLong(months.value)
        parcel.writeLong(days.value)
    }
}

object NullablePeriodParceler : Parceler<Period?> {
    override fun create(parcel: Parcel): Period? {
        return when {
            parcel.readByte() == NULL_VALUE -> null
            else -> periodOf(
                parcel.readLong().years,
                parcel.readLong().months,
                parcel.readLong().days
            )
        }
    }

    override fun Period?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeByte(NULL_VALUE)
        } else {
            parcel.writeByte(NON_NULL_VALUE)
            parcel.writeLong(years.value)
            parcel.writeLong(months.value)
            parcel.writeLong(days.value)
        }
    }

    private const val NON_NULL_VALUE = 0.toByte()
    private const val NULL_VALUE = 1.toByte()
}
