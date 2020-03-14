package io.islandtime.format

import io.islandtime.base.Temporal
import io.islandtime.format.internal.DateTimeFormatterBuilderImpl
import io.islandtime.format.internal.PrintContext

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
        print(context, stringBuilder)
        return stringBuilder
    }

    internal abstract fun print(context: PrintContext, stringBuilder: StringBuilder)
}

/**
 * Define a custom [DateTimeFormatter].
 * @see DateTimeFormatters
 */
inline fun dateTimeFormatter(builder: DateTimeFormatterBuilder.() -> Unit): DateTimeFormatter {
    return DateTimeFormatterBuilderImpl().apply(builder).build()
}