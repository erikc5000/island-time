package io.islandtime.locale

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual typealias Locale = NSLocale

actual fun defaultLocale(): Locale = NSLocale.currentLocale

internal actual fun localeOf(identifier: String): Locale {
    return NSLocale(identifier)
}