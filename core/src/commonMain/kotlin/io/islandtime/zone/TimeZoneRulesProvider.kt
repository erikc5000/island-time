package io.islandtime.zone

import io.islandtime.base.ProviderProxy
import io.islandtime.internal.deprecatedToError

/**
 * An abstraction that allows time zone rules to be supplied from any data source.
 *
 * The set of supported identifiers is expected to vary depending on the source, but should typically represent regions
 * defined in the IANA Time Zone Database.
 */
interface TimeZoneRulesProvider {
    /**
     * The time zone database version or an empty string if unavailable.
     */
    val databaseVersion: String get() = ""

    /**
     * The available time zone region IDs as reported by the underlying provider.
     *
     * In some cases, this may be only a subset of those actually supported. To check if a particular region ID can be
     * handled, use [hasRulesFor].
     *
     * @see hasRulesFor
     */
    val availableRegionIds: Set<String>

    /**
     * Checks if [regionId] has rules associated with it.
     */
    fun hasRulesFor(regionId: String): Boolean

    /**
     * Gets the rules associated with a particular region ID.
     * @throws TimeZoneRulesException if the region ID isn't supported
     */
    fun getRulesFor(regionId: String): TimeZoneRules

    @Deprecated(
        message = "Renamed to getRulesFor().",
        replaceWith = ReplaceWith("getRulesFor(regionId)"),
        level = DeprecationLevel.ERROR
    )
    fun rulesFor(regionId: String): TimeZoneRules = deprecatedToError()

    companion object : ProviderProxy<TimeZoneRulesProvider>(), TimeZoneRulesProvider {
        override val databaseVersion: String get() = provider.databaseVersion
        override val availableRegionIds: Set<String> get() = provider.availableRegionIds
        override fun hasRulesFor(regionId: String): Boolean = provider.hasRulesFor(regionId)
        override fun getRulesFor(regionId: String): TimeZoneRules = provider.getRulesFor(regionId)

        override fun createDefault(): TimeZoneRulesProvider = createDefaultTimeZoneRulesProvider()
    }
}

internal expect fun createDefaultTimeZoneRulesProvider(): TimeZoneRulesProvider