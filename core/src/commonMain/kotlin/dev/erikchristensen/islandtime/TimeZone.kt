package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.tz.TimeZoneRules
import dev.erikchristensen.islandtime.tz.TimeZoneRulesProvider

inline class TimeZone(val regionId: String) {

    val isValid: Boolean
        get() = TimeZoneRulesProvider.getAvailableRegionIds().contains(regionId)

    val rules: TimeZoneRules
        get() = TimeZoneRulesProvider.getRules(regionId)

    override fun toString(): String {
        return regionId
    }

    companion object {
        val UTC = TimeZone("Etc/UTC")
    }
}

internal const val MAX_TIME_ZONE_STRING_LENGTH = 14