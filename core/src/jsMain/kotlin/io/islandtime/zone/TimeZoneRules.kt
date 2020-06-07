@file:Suppress("NewApi")

package io.islandtime.zone

actual object PlatformTimeZoneRulesProvider : TimeZoneRulesProvider {

    override val databaseVersion: String
        get() = "1"

    override val availableRegionIds: Set<String>
        get() = setOf()

    override fun hasRulesFor(regionId: String): Boolean {
        return false
    }

    override fun rulesFor(regionId: String): TimeZoneRules {
        return TODO()
    }
}