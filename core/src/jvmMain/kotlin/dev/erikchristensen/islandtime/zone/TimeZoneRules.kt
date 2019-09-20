@file:Suppress("NewApi")

package dev.erikchristensen.islandtime.zone

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.interval.IntSeconds
import dev.erikchristensen.islandtime.interval.seconds
import dev.erikchristensen.islandtime.jvm.*
import java.time.zone.ZoneOffsetTransition
import java.time.zone.ZoneRules
import java.time.zone.ZoneRulesException
import java.time.zone.ZoneRulesProvider

/**
 * A time zone rules provider that draws from the database built into the java.time library
 */
actual object PlatformDefault : TimeZoneRulesProvider {

    override val databaseVersion: String
        get() = try {
            ZoneRulesProvider.getVersions("UTC")?.lastEntry()?.key.orEmpty()
        } catch (e: ZoneRulesException) {
            throw TimeZoneRulesException(e.message, e)
        }

    override val availableRegionIds: Set<String> = ZoneRulesProvider.getAvailableZoneIds()

    override fun rulesFor(regionId: String): TimeZoneRules {
        return try {
            JavaTimeZoneRules(ZoneRulesProvider.getRules(regionId, false))
        } catch (e: ZoneRulesException) {
            throw TimeZoneRulesException(e.message, e)
        }
    }
}

private class JavaTimeZoneRules(
    private val javaZoneRules: ZoneRules
) : TimeZoneRules {

    override fun offsetAt(instant: Instant): UtcOffset {
        val offset = javaZoneRules.getOffset(instant.toJavaInstant())
        return offset.toIslandUtcOffset()
    }

    override fun offsetAt(dateTime: DateTime): UtcOffset {
        val offset = javaZoneRules.getOffset(dateTime.toJavaLocalDateTime())
        return offset.toIslandUtcOffset()
    }

    override fun validOffsetsAt(dateTime: DateTime): List<UtcOffset> {
        val offsets = javaZoneRules.getValidOffsets(dateTime.toJavaLocalDateTime())
        return offsets.map { it.toIslandUtcOffset() }
    }

    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
        val transition = javaZoneRules.getTransition(dateTime.toJavaLocalDateTime())
        return if (transition != null) JavaTimeZoneOffsetTransition(transition) else null
    }

    override fun isValidOffset(
        dateTime: DateTime,
        offset: UtcOffset
    ): Boolean {
        return javaZoneRules.isValidOffset(dateTime.toJavaLocalDateTime(), offset.toJavaZoneOffset())
    }

    override fun isDaylightSavingsAt(instant: Instant): Boolean {
        return javaZoneRules.isDaylightSavings(instant.toJavaInstant())
    }

    override fun daylightSavingsAt(instant: Instant): IntSeconds {
        return javaZoneRules.getDaylightSavings(instant.toJavaInstant()).seconds.toInt().seconds
    }
}

private class JavaTimeZoneOffsetTransition(
    private val javaZoneOffsetTransition: ZoneOffsetTransition
) : TimeZoneOffsetTransition {

    override val dateTimeBefore: DateTime
        get() = javaZoneOffsetTransition.dateTimeBefore.toIslandDateTime()

    override val dateTimeAfter: DateTime
        get() = dateTimeBefore + duration

    override val offsetBefore: UtcOffset
        get() = javaZoneOffsetTransition.offsetBefore.toIslandUtcOffset()

    override val offsetAfter: UtcOffset
        get() = javaZoneOffsetTransition.offsetAfter.toIslandUtcOffset()

    override val duration: IntSeconds
        get() = offsetAfter.totalSeconds - offsetBefore.totalSeconds

    override val isGap: Boolean
        get() = javaZoneOffsetTransition.isGap

    override val isOverlap: Boolean
        get() = javaZoneOffsetTransition.isOverlap
}