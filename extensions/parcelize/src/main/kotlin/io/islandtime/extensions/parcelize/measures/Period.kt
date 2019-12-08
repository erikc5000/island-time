package io.islandtime.extensions.parcelize.measures

import android.os.Parcel
import io.islandtime.measures.*
import kotlinx.android.parcel.Parceler

object PeriodParceler : Parceler<Period> {
    override fun create(parcel: Parcel): Period {
        return periodOf(
            parcel.readInt().years,
            parcel.readInt().months,
            parcel.readInt().days
        )
    }

    override fun Period.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(years.value)
        parcel.writeInt(months.value)
        parcel.writeInt(days.value)
    }
}

object NullablePeriodParceler : Parceler<Period?> {
    override fun create(parcel: Parcel): Period? {
        return when {
            parcel.readByte() == NULL_VALUE -> null
            else -> periodOf(
                parcel.readInt().years,
                parcel.readInt().months,
                parcel.readInt().days
            )
        }
    }

    override fun Period?.write(parcel: Parcel, flags: Int) {
        if (this == null) {
            parcel.writeByte(NULL_VALUE)
        } else {
            parcel.writeByte(NON_NULL_VALUE)
            parcel.writeInt(years.value)
            parcel.writeInt(months.value)
            parcel.writeInt(days.value)
        }
    }

    private const val NON_NULL_VALUE = 0.toByte()
    private const val NULL_VALUE = 1.toByte()
}