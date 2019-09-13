package dev.erikchristensen.islandtime.tz

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.interval.IntSeconds
import dev.erikchristensen.islandtime.interval.minus
import dev.erikchristensen.islandtime.jvm.toIslandDateTime
import dev.erikchristensen.islandtime.jvm.toIslandUtcOffset
import dev.erikchristensen.islandtime.jvm.toJavaLocalDateTime
import dev.erikchristensen.islandtime.jvm.toJavaZoneOffset
import java.time.zone.ZoneOffsetTransition
import java.time.zone.ZoneRules
import java.time.zone.ZoneRulesException
import java.time.zone.ZoneRulesProvider

/**
 * A time zone rules provider that draws from the database built into the java.time library
 */
actual object PlatformDefault : TimeZoneRulesProvider {
    override fun getAvailableRegionIds(): Set<String> = ZoneRulesProvider.getAvailableZoneIds()

    override fun getRules(regionId: String): TimeZoneRules {
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
        val offset = javaZoneRules.getOffset(java.time.Instant.ofEpochMilli(instant.millisecondsSinceUnixEpoch.value))
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
}

private class JavaTimeZoneOffsetTransition(
    private val javaZoneOffsetTransition: ZoneOffsetTransition
) : TimeZoneOffsetTransition {

    override val dateTimeBefore: DateTime
        get() = javaZoneOffsetTransition.dateTimeBefore.toIslandDateTime()

    override val dateTimeAfter: DateTime
        get() = dateTimeBefore + durationInSeconds

    override val offsetBefore: UtcOffset
        get() = javaZoneOffsetTransition.offsetBefore.toIslandUtcOffset()

    override val offsetAfter: UtcOffset
        get() = javaZoneOffsetTransition.offsetAfter.toIslandUtcOffset()

    override val durationInSeconds: IntSeconds
        get() = offsetAfter.totalSeconds - offsetBefore.totalSeconds

    override val isGap: Boolean
        get() = javaZoneOffsetTransition.isGap

    override val isOverlap: Boolean
        get() = javaZoneOffsetTransition.isOverlap
}