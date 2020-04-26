package io.islandtime.format

import io.islandtime.format.internal.DateTimeFormatterBuilderImpl

/**
 * Define a custom date-time formatter.
 */
inline fun dateTimeFormatter(builder: DateTimeFormatterBuilder.() -> Unit): TemporalFormatter {
    return DateTimeFormatterBuilderImpl().apply(builder).build()
}

/**
 * Create a localized time formatter.
 */
@Suppress("FunctionName")
fun LocalizedDateFormatter(style: FormatStyle): TemporalFormatter {
    return dateTimeFormatter { localizedDate(style) }
}

/**
 * Create a localized time formatter.
 */
@Suppress("FunctionName")
fun LocalizedTimeFormatter(style: FormatStyle): TemporalFormatter {
    return dateTimeFormatter { localizedTime(style) }
}

/**
 * Create a localized date-time formatter.
 */
@Suppress("FunctionName")
fun LocalizedDateTimeFormatter(style: FormatStyle): TemporalFormatter {
    return dateTimeFormatter { localizedDateTime(style) }
}

/**
 * Create a localized date-time formatter.
 */
@Suppress("FunctionName")
fun LocalizedDateTimeFormatter(dateStyle: FormatStyle, timeStyle: FormatStyle): TemporalFormatter {
    return dateTimeFormatter { localizedDateTime(dateStyle, timeStyle) }
}

/**
 * Create a formatter from a date-time format string using patterns defined in Unicode Technical
 * Standard #35.
 */
@Suppress("FunctionName")
fun DateTimeFormatter(pattern: String): TemporalFormatter {
    return dateTimeFormatter { pattern(pattern) }
}

@Suppress("FunctionName")
fun LocalizedDateTimeFormatter(skeleton: String): TemporalFormatter {
    return dateTimeFormatter { localizedPattern(skeleton) }
}