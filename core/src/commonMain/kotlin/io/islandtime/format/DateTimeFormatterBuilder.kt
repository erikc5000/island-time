package io.islandtime.format

@IslandTimeFormatDsl
interface DateTimeFormatterBuilder :
    DateTimeFormatBuilder,
    ConditionalFormatterBuilder<DateTimeFormatterBuilder>,
    ComposableFormatterBuilder {

    /**
     * Append a localized date format.
     */
    fun localizedDate(style: FormatStyle)

    /**
     * Append a localized time format.
     */
    fun localizedTime(style: FormatStyle)

    /**
     * Append a localized date-time format.
     */
    fun localizedDateTime(style: FormatStyle) = localizedDateTime(style, style)

    /**
     * Append a localized format with the specified date and time styles.
     */
    fun localizedDateTime(dateStyle: FormatStyle, timeStyle: FormatStyle)

    /**
     * Append the best localized date-time format based on a skeleton, which defines only which
     * components should be included in the final pattern.
     *
     * For more information on acceptable patterns, see
     * [Unicode Technical Standard #35](https://unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table).
     * @see pattern
     */
    fun localizedPattern(skeleton: String)
}