package io.islandtime.zone

import io.islandtime.*
import io.islandtime.measures.IntNanoseconds
import io.islandtime.measures.IntSeconds
import io.islandtime.measures.LongMilliseconds
import io.islandtime.measures.LongSeconds

class TimeZoneRulesException(
    message: String? = null,
    cause: Throwable? = null
) : DateTimeException(message, cause)

/**
 * An abstraction that allows time zone rules to be supplied from any source.
 *
 * The set of supported region IDs is expected to vary depending on the source, but the IDs themselves should be valid
 * IANA Time Zone Database region IDs.
 */
interface TimeZoneRulesProvider {
    /**
     * The time zone database version, or an empty string if unavailable.
     */
    val databaseVersion: String get() = ""

    /**
     * The available time zone region IDs.
     */
    val availableRegionIds: Set<String>

    /**
     * Get the rules associated with a particular region ID.
     */
    fun rulesFor(regionId: String): TimeZoneRules

    companion object : TimeZoneRulesProvider {
        private val provider get() = IslandTime.timeZoneRulesProvider

        override val databaseVersion get() = provider.databaseVersion
        override val availableRegionIds get() = provider.availableRegionIds
        override fun rulesFor(regionId: String) = provider.rulesFor(regionId)
    }
}

/**
 * The default time zone rules provider implementation for the current platform.
 */
expect object PlatformTimeZoneRulesProvider : TimeZoneRulesProvider

/**
 * A discontinuity in the local timeline, usually caused by daylight savings time changes.
 */
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

/**
 * The set of rules for a particular time zone.
 */
interface TimeZoneRules {
    fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset
    fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanosecondAdjustment: IntNanoseconds): UtcOffset
    fun offsetAt(instant: Instant): UtcOffset
    fun offsetAt(dateTime: DateTime): UtcOffset
    fun validOffsetsAt(dateTime: DateTime): List<UtcOffset>
    fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition?
    fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean = validOffsetsAt(dateTime).contains(offset)

    fun isDaylightSavingsAt(instant: Instant): Boolean
    fun daylightSavingsAt(instant: Instant): IntSeconds
}