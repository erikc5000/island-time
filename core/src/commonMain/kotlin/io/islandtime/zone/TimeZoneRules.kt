package io.islandtime.zone

import io.islandtime.*
import io.islandtime.interval.IntSeconds
import io.islandtime.interval.LongMilliseconds

class TimeZoneRulesException(
    message: String? = null,
    cause: Throwable? = null
) : DateTimeException(message, cause)

interface TimeZoneRulesProvider {
    /**
     * The time zone database version, or an empty string if unavailable
     */
    val databaseVersion: String get() = ""

    /**
     * The available time zone region IDs
     */
    val availableRegionIds: Set<String>

    /**
     * Get the rules associated with a particular region ID
     */
    fun rulesFor(regionId: String): TimeZoneRules

    companion object : TimeZoneRulesProvider {
        private val provider get() = IslandTime.timeZoneRulesProvider.get()
                ?: throw TimeZoneRulesException("No time zone rules provider has been initialized")

        override val databaseVersion = provider.databaseVersion
        override val availableRegionIds = provider.availableRegionIds
        override fun rulesFor(regionId: String) = provider.rulesFor(regionId)
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
    val duration: IntSeconds

    val validOffsets: List<UtcOffset>
        get() = if (isGap) emptyList() else listOf(offsetBefore, offsetAfter)
}

interface TimeZoneRules {
    fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset
    fun offsetAt(instant: Instant): UtcOffset
    fun offsetAt(dateTime: DateTime): UtcOffset
    fun validOffsetsAt(dateTime: DateTime): List<UtcOffset>
    fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition?
    fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean = validOffsetsAt(dateTime).contains(offset)

    fun isDaylightSavingsAt(instant: Instant): Boolean
    fun daylightSavingsAt(instant: Instant): IntSeconds
}