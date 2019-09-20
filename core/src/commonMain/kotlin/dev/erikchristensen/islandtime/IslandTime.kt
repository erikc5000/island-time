package dev.erikchristensen.islandtime

import co.touchlab.stately.concurrency.AtomicReference
import dev.erikchristensen.islandtime.zone.TimeZoneRulesException
import dev.erikchristensen.islandtime.zone.TimeZoneRulesProvider

object IslandTime {
    internal val timeZoneRulesProvider = AtomicReference<TimeZoneRulesProvider?>(null)

    /**
     * Initialize the time zone rules provider to be used by Island Time
     */
    fun initialize(provider: TimeZoneRulesProvider) {
        if (!timeZoneRulesProvider.compareAndSet(null, provider)) {
            throw TimeZoneRulesException("A time zone rules provider has already been initialized")
        }
    }

    internal fun tearDown() {
        timeZoneRulesProvider.set(null)
    }
}