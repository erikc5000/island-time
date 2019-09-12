package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.tz.TimeZoneRules
import dev.erikchristensen.islandtime.tz.TimeZoneRulesProvider

inline class TimeZone(val regionId: String) : Comparable<TimeZone> {

    val isValid: Boolean
        get() = TimeZoneRulesProvider.getAvailableRegionIds().contains(regionId)

    val rules: TimeZoneRules
        get() = TimeZoneRulesProvider.getRules(regionId)

    override fun compareTo(other: TimeZone): Int {
        return regionId.compareTo(other.regionId)
    }

    override fun toString(): String {
        return regionId
    }

    companion object {
        val UTC = TimeZone("Etc/UTC")
    }
}

internal const val MAX_TIME_ZONE_STRING_LENGTH = 50