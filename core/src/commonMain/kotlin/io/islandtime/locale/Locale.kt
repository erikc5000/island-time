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

internal expect fun localeFor(identifier: String): Locale

internal expect val Locale.firstDayOfWeek: DayOfWeek
internal expect val Locale.lastDayOfWeek: DayOfWeek