package io.islandtime

import co.touchlab.stately.concurrency.AtomicReference
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.PlatformDateTimeTextProvider
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import io.islandtime.zone.TimeZoneRulesException
import io.islandtime.zone.TimeZoneRulesProvider

object IslandTime {
    private val _timeZoneRulesProvider = AtomicReference<TimeZoneRulesProvider?>(null)

    internal val timeZoneRulesProvider: TimeZoneRulesProvider
        get() = _timeZoneRulesProvider.get() ?: _timeZoneRulesProvider.run {
            compareAndSet(null, PlatformTimeZoneRulesProvider)
            get() ?: throw IllegalStateException("Failed to initialize the platform time zone rules provider")
        }

    private val _dateTimeTextProvider = AtomicReference<DateTimeTextProvider?>(null)

    internal val dateTimeTextProvider: DateTimeTextProvider
        get() = _dateTimeTextProvider.get() ?: _dateTimeTextProvider.run {
            compareAndSet(null, PlatformDateTimeTextProvider)
            get() ?: throw IllegalStateException("Failed to initialize the platform date-time text provider")
        }

    /**
     * Initialize Island Time with a specific time zone rules provider.
     *
     * This method should be called prior to any of use of the library, usually during an application's initialization
     * process. If Island Time is not explicitly initialized, the [PlatformTimeZoneRulesProvider] will be used.
     *
     * @throws TimeZoneRulesException if a provider has already been initialized
     * @see TimeZoneRulesProvider
     */
    fun initializeWith(provider: TimeZoneRulesProvider) {
        if (!this._timeZoneRulesProvider.compareAndSet(null, provider)) {
            throw TimeZoneRulesException("A time zone rules provider has already been initialized")
        }
    }

    /**
     * Initialize Island Time with a specific date-time text provider.
     *
     * This method should be called prior to any of use of the library, usually during an application's initialization
     * process. If Island Time is not explicitly initialized, the [PlatformDateTimeTextProvider] will be used.
     *
     * @throws DateTimeException if a provider has already been initialized
     * @see DateTimeTextProvider
     */
    fun initializeWith(provider: DateTimeTextProvider) {
        if (!this._dateTimeTextProvider.compareAndSet(null, provider)) {
            throw DateTimeException("A date-time text provider has already been initialized")
        }
    }

    /**
     * Reset Island Time to an uninitialized state.
     *
     * This method is intended to be used to clean up custom providers in tests. It shouldn't be necessary to call this
     * in production.
     */
    fun reset() {
        _timeZoneRulesProvider.set(null)
        _dateTimeTextProvider.set(null)
    }
}