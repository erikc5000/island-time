package io.islandtime.format

import io.islandtime.base.Temporal
import io.islandtime.calendar.LocalizationContext
import io.islandtime.calendar.WeekSettings
import io.islandtime.format.internal.FormatContext
import io.islandtime.format.internal.TemporalFormatterBuilderImpl
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale

/**
 * A formatter that converts an Island Time [Temporal] into a string representation.
 */
abstract class TemporalFormatter internal constructor() {
    /**
     * Converts [temporal] to a string representation.
     */
    fun format(temporal: Temporal, settings: Settings = Settings.DEFAULT): String {
        return buildString { formatTo(this, temporal, settings) }
    }

    /**
     * Converts [temporal] to a string representation, populating [stringBuilder] with the result.
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
     * @property numberStyle The set of characters that should be used when formatting numbers.
     * @property locale A function that will be invoked to provide a locale if one is needed during formatting.
     * @property weekSettingsOverride Overrides the [WeekSettings] associated with the [locale].
     */
    data class Settings(
        val numberStyle: NumberStyle = NumberStyle.DEFAULT,
        val locale: () -> Locale = { defaultLocale() },
        val weekSettingsOverride: WeekSettings? = null
    ) {
        constructor(
            numberStyle: NumberStyle = NumberStyle.DEFAULT,
            locale: Locale,
            weekSettingsOverride: WeekSettings? = null
        ) : this(numberStyle, { locale }, weekSettingsOverride)

        companion object {
            /**
             * The default formatter settings.
             */
            val DEFAULT = Settings()
        }
    }

    interface Context : LocalizationContext {
        val temporal: Temporal
    }
}

/**
 * Builds a custom [TemporalFormatter] using an un-opinionated, low-level interface. In most cases, you should use
 * [DateTimeFormatter] instead. This is available primarily as a tool to work around corner cases or build support
 * for new types of formatters.
 * @see DateTimeFormatter
 * @see DateTimeFormatter
 * @see LocalizedTimeFormatter
 */
@Suppress("FunctionName")
inline fun TemporalFormatter(builder: TemporalFormatterBuilder.() -> Unit): TemporalFormatter {
    return TemporalFormatterBuilderImpl().apply(builder).build()
}
