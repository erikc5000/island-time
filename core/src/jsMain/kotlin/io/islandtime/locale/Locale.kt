package io.islandtime.locale

import io.islandtime.intl.DateTimeFormat

class MLocale(
    val locale: String
)

actual typealias Locale = MLocale

actual fun defaultLocale(): Locale =
    MLocale(DateTimeFormat().resolvedOptions().locale)

internal actual fun localeOf(identifier: String): Locale =
    try {
        DateTimeFormat(identifier).format()
        MLocale(identifier)
    }catch (e : Exception){
        defaultLocale()
    }