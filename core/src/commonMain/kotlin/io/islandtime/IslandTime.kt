package io.islandtime

import co.touchlab.stately.concurrency.AtomicReference
import io.islandtime.zone.PlatformTimeZoneRulesProvider
import io.islandtime.zone.TimeZoneRulesException
import io.islandtime.zone.TimeZoneRulesProvider

object IslandTime {
    private val provider = AtomicReference<TimeZoneRulesProvider?>(null)

    internal val timeZoneRulesProvider: TimeZoneRulesProvider
        get() = provider.get() ?: provider.run {
            compareAndSet(null, PlatformTimeZoneRulesProvider)
            get() ?: throw IllegalStateException("Failed to initialize the platform time zone rules provider")
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
        if (!this.provider.compareAndSet(null, provider)) {
            throw TimeZoneRulesException("A time zone rules provider has already been initialized")
        }
    }

    /**
     * Reset Island Time to an uninitialized state.
     *
     * This method is intended to be used to clean up custom time zone rules providers in tests. It shouldn't be
     * necessary to call this in production.
     */
    fun reset() {
        provider.set(null)
    }
}