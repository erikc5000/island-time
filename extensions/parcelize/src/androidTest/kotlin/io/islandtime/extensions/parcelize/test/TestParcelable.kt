package io.islandtime.extensions.parcelize.test

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.common.truth.Truth.assertThat

inline fun <reified T : Parcelable> testParcelable(parcelable: T) {
    val inBundle = Bundle().apply { putParcelable("data", parcelable) }

    val outBundle = Parcel.obtain().run {
        writeBundle(inBundle)
        setDataPosition(0)
        readBundle()
    }!!.apply {
        classLoader = T::class.java.classLoader
    }

    val result = outBundle.getParcelable<T>("data")
    assertThat(result).isEqualTo(parcelable)
}