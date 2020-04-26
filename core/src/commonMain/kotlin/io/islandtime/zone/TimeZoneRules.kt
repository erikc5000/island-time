package io.islandtime.zone

import io.islandtime.*
import io.islandtime.measures.*

class TimeZoneRulesException(
    message: String? = null,
    cause: Throwable? = null
) : DateTimeException(message, cause)

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
     * Check if [regionId] has rules associated with it.
     */
    fun hasRulesFor(regionId: String): Boolean

    /**
     * Get the rules associated with a particular region ID.
     * @throws TimeZoneRulesException if the region ID isn't supported
     */
    fun rulesFor(regionId: String): TimeZoneRules

    companion object : TimeZoneRulesProvider {
        override val databaseVersion: String
            get() = IslandTime.timeZoneRulesProvider.databaseVersion

        override val availableRegionIds: Set<String>
            get() = IslandTime.timeZoneRulesProvider.availableRegionIds

        override fun hasRulesFor(regionId: String): Boolean {
            return IslandTime.timeZoneRulesProvider.hasRulesFor(regionId)
        }

        override fun rulesFor(regionId: String): TimeZoneRules {
            return IslandTime.timeZoneRulesProvider.rulesFor(regionId)
        }
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
    /**
     * The date and time of day at the start of the transition.
     */
    val dateTimeBefore: DateTime

    /**
     * The date and time of day at the end of the transition.
     */
    val dateTimeAfter: DateTime

    /**
     * Check if this is a gap, meaning that there are clock times that go "missing".
     */
    val isGap: Boolean

    /**
     * Check if this is an overlap, meaning that there are clock times that exist twice.
     */
    val isOverlap: Boolean

    /**
     * The UTC offset before the transition.
     */
    val offsetBefore: UtcOffset

    /**
     * The UTC offset after the transition.
     */
    val offsetAfter: UtcOffset

    /**
     * The duration of the transition period in seconds.
     */
    val duration: IntSeconds

    /**
     * Get a list of the valid offsets during this transition. If this is gap, the list will be empty. If this is an
     * overlap, the list will contain both the earlier and later offsets.
     */
    val validOffsets: List<UtcOffset>
        get() = if (isGap) emptyList() else listOf(offsetBefore, offsetAfter)
}

/**
 * The set of rules for a particular time zone.
 */
interface TimeZoneRules {
    /**
     * Check if the time zone has a fixed offset from UTC.
     */
    val hasFixedOffset: Boolean

    /**
     * Get the offset in effect at a certain number of milliseconds since the Unix epoch.
     */
    fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset

    /**
     * Get the offset in effect at a certain number of seconds since the Unix epoch.
     */
    fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanoOfSeconds: IntNanoseconds): UtcOffset

    /**
     * Get the offset in effect at a particular instant.
     */
    fun offsetAt(instant: Instant): UtcOffset

    /**
     * Get the offset in effect at a particular date and time.
     */
    fun offsetAt(dateTime: DateTime): UtcOffset

    /**
     * Get a list of the valid offsets at a particular date and time.
     */
    fun validOffsetsAt(dateTime: DateTime): List<UtcOffset>

    /**
     * Get the transition at a particular date and time, if one exists.
     */
    fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition?

    /**
     * Check if [offset] is valid at particular date and time.
     */
    fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean = validOffsetsAt(dateTime).contains(offset)

    /**
     * Check if daylight savings time is in effect at a particular instant.
     */
    fun isDaylightSavingsAt(instant: Instant): Boolean

    /**
     * Get the amount of daylight savings time in effect at a particular instant. This is the amount of time added to
     * the standard offset.
     */
    fun daylightSavingsAt(instant: Instant): IntSeconds
}

/**
 * A time zone rules implementation for a fixed offset from UTC.
 */
internal class FixedTimeZoneRules(private val offset: UtcOffset) : TimeZoneRules {
    override val hasFixedOffset: Boolean get() = true

    override fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds) = offset

    override fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanoOfSeconds: IntNanoseconds): UtcOffset {
        return offset
    }

    override fun offsetAt(instant: Instant) = offset
    override fun offsetAt(dateTime: DateTime) = offset
    override fun validOffsetsAt(dateTime: DateTime) = listOf(offset)
    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? = null
    override fun isValidOffset(dateTime: DateTime, offset: UtcOffset) = offset == this.offset
    override fun isDaylightSavingsAt(instant: Instant) = false
    override fun daylightSavingsAt(instant: Instant) = 0.seconds
}