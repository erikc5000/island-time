package io.islandtime.zone

import io.islandtime.DateTime
import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.UtcOffset
import io.islandtime.measures.*

/**
 * The set of rules for a particular time zone.
 */
interface TimeZoneRules {
    /**
     * Checks if the time zone has a fixed offset from UTC.
     */
    val hasFixedOffset: Boolean

    /**
     * Gets the offset in effect at a certain number of milliseconds since the Unix epoch.
     */
    fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset

    /**
     * Gets the offset in effect at a certain number of seconds since the Unix epoch.
     */
    fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanoOfSeconds: IntNanoseconds): UtcOffset

    /**
     * Gets the offset in effect at a particular instant.
     */
    fun offsetAt(instant: Instant): UtcOffset

    /**
     * Gets the offset in effect at a particular instant.
     */
    fun offsetAt(instant: PlatformInstant): UtcOffset

    /**
     * Gets the offset in effect at a particular date and time.
     */
    fun offsetAt(dateTime: DateTime): UtcOffset

    /**
     * Gets a list of the valid offsets at a particular date and time.
     */
    fun validOffsetsAt(dateTime: DateTime): List<UtcOffset>

    /**
     * Gets the transition at a particular date and time, if one exists.
     */
    fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition?

    /**
     * Checks if [offset] is valid at particular date and time.
     */
    fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean = validOffsetsAt(dateTime).contains(offset)

    /**
     * Checks if daylight savings time is in effect at a particular instant.
     */
    fun isDaylightSavingsAt(instant: Instant): Boolean

    /**
     * Gets the amount of daylight savings time in effect at a particular instant. This is the amount of time added to
     * the standard offset.
     */
    fun daylightSavingsAt(instant: Instant): IntSeconds
}

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
     * Checks if this is a gap, meaning that there are clock times that go "missing".
     */
    val isGap: Boolean

    /**
     * Checks if this is an overlap, meaning that there are clock times that exist twice.
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
     * Gets a list of the valid offsets during this transition. If this is gap, the list will be empty. If this is an
     * overlap, the list will contain both the earlier and later offsets.
     */
    val validOffsets: List<UtcOffset>
        get() = if (isGap) emptyList() else listOf(offsetBefore, offsetAfter)
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
    override fun offsetAt(instant: PlatformInstant) = offset
    override fun offsetAt(dateTime: DateTime) = offset
    override fun validOffsetsAt(dateTime: DateTime) = listOf(offset)
    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? = null
    override fun isValidOffset(dateTime: DateTime, offset: UtcOffset) = offset == this.offset
    override fun isDaylightSavingsAt(instant: Instant) = false
    override fun daylightSavingsAt(instant: Instant) = 0.seconds
}
