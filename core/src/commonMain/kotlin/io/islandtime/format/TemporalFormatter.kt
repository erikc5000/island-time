package io.islandtime.format

import io.islandtime.base.Temporal
import io.islandtime.format.internal.TemporalFormatterBuilderImpl
import io.islandtime.format.internal.FormatContext
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale

/**
 * A formatter that converts an Island Time [Temporal] into a string representation.
 */
abstract class TemporalFormatter internal constructor() {
    /**
     * Convert [temporal] to a string representation.
     */
    fun format(temporal: Temporal, settings: Settings = Settings.DEFAULT): String {
        return buildString { formatTo(this, temporal, settings) }
    }

    /**
     * Convert [temporal] to a string representation, populating [stringBuilder] with the result.
     */
    fun formatTo(
        stringBuilder: StringBuilder,
        temporal: Temporal,
        settings: Settings = Settings.DEFAULT
    ): StringBuilder {
        val context = FormatContext(temporal, settings)
        format(context, stringBuilder)
        return stringBuilder
    }

    internal abstract fun format(context: FormatContext, stringBuilder: StringBuilder)

    /**
     * Settings that control the formatting behavior.
     * @property numberStyle Defines the set of characters that should be used when formatting
     * numbers.
     * @property locale A function that will be invoked to provide a locale if one is needed during
     * formatting.
     */
    data class Settings(
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
            val DEFAULT = Settings()
        }
    }
}

/**
 * Define a custom [TemporalFormatter] using an un-opinionated, low-level interface. In most cases,
 * you should use [dateTimeFormatter] instead. This is available mostly as a tool to work around
 * corner cases or build support for new types of formatters.
 * @see dateTimeFormatter
 * @see LocalizedTimeFormatter
 */
inline fun temporalFormatter(builder: TemporalFormatterBuilder.() -> Unit): TemporalFormatter {
    return TemporalFormatterBuilderImpl().apply(builder).build()
}