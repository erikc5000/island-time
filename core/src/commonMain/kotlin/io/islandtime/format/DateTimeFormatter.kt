package io.islandtime.format

import io.islandtime.base.Temporal
import io.islandtime.format.internal.DateTimeFormatterBuilderImpl
import io.islandtime.format.internal.PrintContext
import kotlin.jvm.JvmName

/**
 * A formatter that converts an Island Time [Temporal] into a string representation.
 */
abstract class DateTimeFormatter internal constructor() {
    fun format(
        temporal: Temporal,
        settings: DateTimeFormatterSettings = DateTimeFormatterSettings.DEFAULT
    ): String {
        return buildString { formatTo(this, temporal, settings) }
    }

    fun formatTo(
        stringBuilder: StringBuilder,
        temporal: Temporal,
        settings: DateTimeFormatterSettings = DateTimeFormatterSettings.DEFAULT
    ): StringBuilder {
        val context = PrintContext(temporal, settings)
        format(context, stringBuilder)
        return stringBuilder
    }

    internal abstract fun format(context: PrintContext, stringBuilder: StringBuilder)
}

/**
 * Define a custom [DateTimeFormatter].
 * @see DateTimeFormatters
 */
inline fun dateTimeFormatter(builder: DateTimeFormatterBuilder.() -> Unit): DateTimeFormatter {
    return DateTimeFormatterBuilderImpl().apply(builder).build()
}

/**
 *
 */
@Suppress("FunctionName")
@JvmName("DateTimeFormatterForDate")
fun DateTimeFormatter(
    dateStyle: FormatStyle,
    timeStyle: FormatStyle? = null
): DateTimeFormatter {
    return DateTimeFormatStyleProvider.formatterFor(dateStyle, timeStyle)
}

/**
 *
 */
@Suppress("FunctionName")
@JvmName("DateTimeFormatterForTime")
fun DateTimeFormatter(
    dateStyle: FormatStyle? = null,
    timeStyle: FormatStyle
): DateTimeFormatter {
    return DateTimeFormatStyleProvider.formatterFor(dateStyle, timeStyle)
}

/**
 *
 */
@Suppress("FunctionName")
fun DateTimeFormatter(
    dateStyle: FormatStyle,
    timeStyle: FormatStyle
): DateTimeFormatter {
    return DateTimeFormatStyleProvider.formatterFor(dateStyle, timeStyle)
}