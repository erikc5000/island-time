package io.islandtime.locale

/**
 * A locale.
 *
 * On the JVM, this maps to `java.util.Locale`. On Apple platforms, this maps to `NSLocale`.
 */
expect class Locale

/**
 * Gets the current [Locale].
 *
 * On the JVM, the `Category` is not used in order to support older Android versions.
 */
expect fun defaultLocale(): Locale

/**
 * Converts an IETF BCP 47 language tag, such as "en-US" or "de-DE", to a [Locale].
 */
expect fun String.toLocale(): Locale
