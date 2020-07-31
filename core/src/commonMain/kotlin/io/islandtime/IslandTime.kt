package io.islandtime

import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.PlatformDateTimeTextProvider
import io.islandtime.format.PlatformTimeZoneTextProvider
import io.islandtime.format.TimeZoneTextProvider
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

    internal val timeZoneTextProvider: TimeZoneTextProvider
        get() = settings.timeZoneTextProvider

    @Suppress("ObjectPropertyName")
    private val _settings = atomic<Settings?>(null)

    private var settings: Settings
        set(value) {
            if (!_settings.compareAndSet(null, value)) {
                throw IllegalStateException("Island Time has already been initialized")
            }
        }
        get() = _settings.value ?: run {
            _settings.compareAndSet(null, Settings())
            _settings.value ?: throw IllegalStateException("Failed to initialize Island Time")
        }

    /**
     * Initializes Island Time.
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
     * Initializes Island Time with a specific time zone rules provider, leaving all other settings in their default
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
     * Resets Island Time to an uninitialized state.
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
         * The time zone text provider to use.
         */
        var timeZoneTextProvider: TimeZoneTextProvider
    }

    private class InitializerImpl : Initializer {
        override var timeZoneRulesProvider: TimeZoneRulesProvider = PlatformTimeZoneRulesProvider
        override var dateTimeTextProvider: DateTimeTextProvider = PlatformDateTimeTextProvider
        override var timeZoneTextProvider: TimeZoneTextProvider = PlatformTimeZoneTextProvider

        fun build(): Settings {
            return Settings(timeZoneRulesProvider, dateTimeTextProvider, timeZoneTextProvider)
        }
    }

    private data class Settings(
        val timeZoneRulesProvider: TimeZoneRulesProvider = PlatformTimeZoneRulesProvider,
        val dateTimeTextProvider: DateTimeTextProvider = PlatformDateTimeTextProvider,
        val timeZoneTextProvider: TimeZoneTextProvider = PlatformTimeZoneTextProvider
    )
}