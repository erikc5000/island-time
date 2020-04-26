package io.islandtime

import io.islandtime.format.*
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import io.islandtime.zone.TimeZoneRulesProvider
import kotlinx.atomicfu.atomic

/**
 * Global configuration for Island Time.
 */
object IslandTime {
    internal val timeZoneRulesProvider: TimeZoneRulesProvider
        get() = settings.timeZoneRulesProvider

    internal val dateTimeTextProvider: DateTimeTextProvider
        get() = settings.dateTimeTextProvider

    internal val dateTimeFormatProvider: DateTimeFormatProvider
        get() = settings.dateTimeFormatProvider

    internal val timeZoneTextProvider: TimeZoneTextProvider
        get() = settings.timeZoneTextProvider

    @Suppress("ObjectPropertyName")
    private val _settings = atomic<Settings?>(null)

    private var settings: Settings
        set(value) {
            check(_settings.compareAndSet(null, value)) { "Island Time has already been initialized" }
        }
        get() = _settings.value ?: run {
            _settings.compareAndSet(null, Settings())
            checkNotNull(_settings.value) { "Failed to initialize Island Time" }
        }

    /**
     * Initialize Island Time.
     *
     * This method should be called prior to any of use of the library, usually during an application's initialization
     * process. If Island Time is not explicitly initialized, the [PlatformTimeZoneRulesProvider] and all other default
     * settings will be used.
     *
     * @throws IllegalStateException if Island Time has already been initialized
     */
    fun initialize(block: Initializer.() -> Unit) {
        settings = InitializerImpl().apply(block).build()
    }

    /**
     * Initialize Island Time with a specific time zone rules provider, leaving all other settings in their default
     * state.
     *
     * This method should be called prior to any of use of the library, usually during an application's initialization
     * process. If Island Time is not explicitly initialized, the [PlatformTimeZoneRulesProvider] and all other default
     * settings will be used.
     *
     * @throws IllegalStateException if Island Time has already been initialized
     * @see initialize
     */
    fun initializeWith(provider: TimeZoneRulesProvider) {
        settings = Settings(timeZoneRulesProvider = provider)
    }

    /**
     * Reset Island Time to an uninitialized state.
     *
     * This method is intended to be used to clean up custom providers in tests. It shouldn't be necessary to call this
     * in production.
     */
    fun reset() {
        _settings.getAndSet(null)
    }

    /**
     * Controls the settings that Island Time is initialized with.
     */
    interface Initializer {
        /**
         * The time zone rules provider to use.
         */
        var timeZoneRulesProvider: TimeZoneRulesProvider

        /**
         * The date-time text provider to use.
         */
        var dateTimeTextProvider: DateTimeTextProvider

        /**
         * The date-time format style provider to use.
         */
        var dateTimeFormatProvider: DateTimeFormatProvider

        /**
         * The time zone text provider to use.
         */
        var timeZoneTextProvider: TimeZoneTextProvider
    }

    private class InitializerImpl : Initializer {
        override var timeZoneRulesProvider: TimeZoneRulesProvider = PlatformTimeZoneRulesProvider
        override var dateTimeTextProvider: DateTimeTextProvider = PlatformDateTimeTextProvider
        override var dateTimeFormatProvider: DateTimeFormatProvider = PlatformDateTimeFormatProvider
        override var timeZoneTextProvider: TimeZoneTextProvider = PlatformTimeZoneTextProvider

        fun build(): Settings {
            return Settings(
                timeZoneRulesProvider,
                dateTimeTextProvider,
                dateTimeFormatProvider,
                timeZoneTextProvider
            )
        }
    }

    private data class Settings(
        val timeZoneRulesProvider: TimeZoneRulesProvider = PlatformTimeZoneRulesProvider,
        val dateTimeTextProvider: DateTimeTextProvider = PlatformDateTimeTextProvider,
        val dateTimeFormatProvider: DateTimeFormatProvider = PlatformDateTimeFormatProvider,
        val timeZoneTextProvider: TimeZoneTextProvider = PlatformTimeZoneTextProvider
    )
}