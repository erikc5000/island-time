package io.islandtime.locale

actual typealias Locale = java.util.Locale

actual fun defaultLocale(): Locale = Locale.getDefault()

@Suppress("NewApi")
internal actual fun localeOf(identifier: String): Locale {
    return Locale.forLanguageTag(identifier)
}