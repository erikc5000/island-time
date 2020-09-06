package io.islandtime.formatter.dsl

import io.islandtime.format.FormatStyle
import io.islandtime.format.dsl.DateTimeFormatBuilder
import io.islandtime.format.dsl.IslandTimeFormatDsl

@IslandTimeFormatDsl
interface DateTimeFormatterBuilder :
    DateTimeFormatBuilder,
    ConditionalFormatterBuilder<DateTimeFormatterBuilder>,
    ComposableFormatterBuilder {

    /**
     * Appends a localized date format.
     */
    fun localizedDate(style: FormatStyle)

    /**
     * Appends a localized time format.
     */
    fun localizedTime(style: FormatStyle)

    /**
     * Appends a localized date-time format.
     */
    fun localizedDateTime(style: FormatStyle) = localizedDateTime(style, style)

    /**
     * Appends a localized format with the specified date and time styles.
     */
    fun localizedDateTime(dateStyle: FormatStyle, timeStyle: FormatStyle)

    /**
     * Appends the best localized date-time format based on a [skeleton], which defines only the components that should
     * be included in the final pattern. Support for this feature depends on the configured [DateTimeFormatProvider],
     * but isn't available on all platforms.
     *
     * For more information on acceptable patterns, see
     * [Unicode Technical Standard #35](https://unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table).
     * @see pattern
     */
    fun localizedPattern(skeleton: String)
}
