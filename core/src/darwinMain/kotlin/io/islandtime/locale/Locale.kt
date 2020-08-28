package io.islandtime.locale

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual typealias Locale = NSLocale

actual fun defaultLocale(): Locale = NSLocale.currentLocale
actual fun String.toLocale(): Locale = NSLocale(this)
