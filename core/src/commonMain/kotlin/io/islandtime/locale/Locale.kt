package io.islandtime.locale

/**
 * A locale.
 *
 * On the JVM, this maps to `java.util.Locale`. On Apple platforms, this maps to `NSLocale`.
 */
expect class Locale

/**
 * Get the current [Locale].
 */
expect fun defaultLocale(): Locale

internal expect fun localeOf(identifier: String): Locale