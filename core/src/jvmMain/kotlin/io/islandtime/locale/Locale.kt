package io.islandtime.locale

actual typealias Locale = java.util.Locale

actual fun defaultLocale(): Locale = Locale.getDefault()
actual fun String.toLocale(): Locale = Locale.forLanguageTag(this)