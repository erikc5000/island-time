package dev.erikchristensen.islandtime.tz

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.interval.IntSeconds

class TimeZoneRulesException(
    message: String? = null,
    cause: Throwable? = null
) : DateTimeException(message, cause)

interface TimeZoneRulesProvider {
    fun getAvailableRegionIds(): Set<String>
    fun getRules(regionId: String): TimeZoneRules

    companion object : TimeZoneRulesProvider {
        private val provider get() = IslandTime.timeZoneRulesProvider.get()
                ?: throw TimeZoneRulesException("No time zone rules provider has been initialized")

        override fun getAvailableRegionIds() = provider.getAvailableRegionIds()
        override fun getRules(regionId: String) = provider.getRules(regionId)
    }
}

expect object PlatformDefault : TimeZoneRulesProvider

interface TimeZoneOffsetTransition {
    val dateTimeBefore: DateTime
    val dateTimeAfter: DateTime
    val isGap: Boolean
    val isOverlap: Boolean
    val offsetBefore: UtcOffset
    val offsetAfter: UtcOffset
    val durationInSeconds: IntSeconds
}

interface TimeZoneRules {
    fun offsetAt(instant: Instant): UtcOffset
    fun offsetAt(dateTime: DateTime): UtcOffset
    fun validOffsetsAt(dateTime: DateTime): List<UtcOffset>
    fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition?
    fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean
}