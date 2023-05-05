package io.islandtime.parcelize.test

import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.common.truth.Truth.assertThat

inline fun <reified T : Parcelable> testParcelable(parcelable: T) {
    val inBundle = Bundle().apply { putParcelable("data", parcelable) }

    val outBundle = withParcel {
        writeBundle(inBundle)
        setDataPosition(0)
        readBundle(T::class.java.classLoader)!!
    }

    val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        outBundle.getParcelable("data", T::class.java)
    } else {
        @Suppress("DEPRECATION")
        outBundle.getParcelable("data")
    }

    assertThat(result).isEqualTo(parcelable)
}

inline fun <T> withParcel(block: Parcel.() -> T): T = Parcel.obtain().run {
    val returnValue = block()
    recycle()
    returnValue
}
