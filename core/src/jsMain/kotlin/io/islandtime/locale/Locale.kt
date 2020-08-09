package io.islandtime.locale

import io.islandtime.js.internal.intl.DateTimeFormat

actual class Locale(
    val locale: String
)

actual fun defaultLocale(): Locale =
    Locale(DateTimeFormat().resolvedOptions().locale)

internal actual fun localeOf(identifier: String): Locale =
    try {
        DateTimeFormat(identifier).format()
        Locale(identifier)
    }catch (e : Exception){
        defaultLocale()
    }