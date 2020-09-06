@file:Suppress("FunctionName")

package io.islandtime.formatter

import io.islandtime.format.FormatStyle
import io.islandtime.formatter.dsl.DateTimeFormatterBuilder
import io.islandtime.format.dsl.pattern
import io.islandtime.formatter.internal.DateTimeFormatterBuilderImpl

/**
 * Builds a custom date-time formatter.
 */
inline fun DateTimeFormatter(builder: DateTimeFormatterBuilder.() -> Unit): TemporalFormatter {
    return DateTimeFormatterBuilderImpl().apply(builder).build()
}

/**
 * Creates a localized time formatter.
 */
fun LocalizedDateFormatter(style: FormatStyle): TemporalFormatter {
    return DateTimeFormatter { localizedDate(style) }
}

/**
 * Creates a localized time formatter.
 */
fun LocalizedTimeFormatter(style: FormatStyle): TemporalFormatter {
    return DateTimeFormatter { localizedTime(style) }
}

/**
 * Creates a localized date-time formatter.
 */
fun LocalizedDateTimeFormatter(style: FormatStyle): TemporalFormatter {
    return DateTimeFormatter { localizedDateTime(style) }
}

/**
 * Creates a localized date-time formatter.
 */
fun LocalizedDateTimeFormatter(dateStyle: FormatStyle, timeStyle: FormatStyle): TemporalFormatter {
    return DateTimeFormatter { localizedDateTime(dateStyle, timeStyle) }
}

/**
 * Creates a formatter from a date-time format string using patterns defined in Unicode Technical Standard #35.
 */
fun DateTimeFormatter(pattern: String): TemporalFormatter {
    return DateTimeFormatter { pattern(pattern) }
}

/**
 * Creates a localized date-time formatter using a [skeleton] pattern as defined in Unicode Technical Standard #35.
 */
fun LocalizedDateTimeFormatter(skeleton: String): TemporalFormatter {
    return DateTimeFormatter { localizedPattern(skeleton) }
}
