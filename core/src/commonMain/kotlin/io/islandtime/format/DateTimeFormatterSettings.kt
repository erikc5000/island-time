package io.islandtime.format

import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale

/**
 * Settings that control the formatting behavior.
 * @property numberStyle Defines the set of characters that should be used when formatting numbers.
 * @property locale A function that will be invoked to provide a locale if one is needed during formatting.
 */
data class DateTimeFormatterSettings(
    val numberStyle: NumberStyle = NumberStyle.DEFAULT,
    val locale: () -> Locale = { defaultLocale() }
) {
    constructor(
        numberStyle: NumberStyle = NumberStyle.DEFAULT,
        locale: Locale
    ) : this(numberStyle, { locale })

    companion object {
        /**
         * The default formatter settings.
         */
        val DEFAULT = DateTimeFormatterSettings()
    }
}