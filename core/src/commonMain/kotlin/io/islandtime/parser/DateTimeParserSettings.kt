package io.islandtime.parser

import io.islandtime.format.NumberStyle
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale

/**
 * Settings that control the parsing behavior.
 * @property numberStyle Defines the set of characters that should be used when parsing numbers.
 * @property locale A function that will be invoked to provide a locale if one is needed during parsing.
 */
data class DateTimeParserSettings(
    val numberStyle: NumberStyle = NumberStyle.DEFAULT,
    val locale: () -> Locale = { defaultLocale() }
) {
    constructor(
        numberStyle: NumberStyle = NumberStyle.DEFAULT,
        locale: Locale
    ) : this(numberStyle, { locale })

    companion object {
        /**
         * The default parser settings.
         */
        val DEFAULT = DateTimeParserSettings()
    }
}