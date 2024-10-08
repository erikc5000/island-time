@file:OptIn(UnsafeNumber::class, ExperimentalForeignApi::class)

package io.islandtime.zone

import io.islandtime.*
import io.islandtime.darwin.toIslandDateTimeAt
import io.islandtime.darwin.toNSDate
import io.islandtime.darwin.toNSDateComponents
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.Milliseconds
import io.islandtime.measures.Nanoseconds
import io.islandtime.measures.Seconds
import io.islandtime.measures.seconds
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.Foundation.*
import kotlin.native.concurrent.ThreadLocal

/**
 * A time zone rules provider that draws from the database included on Darwin platforms.
 */
@ThreadLocal
actual object PlatformTimeZoneRulesProvider : TimeZoneRulesProvider {
    private val timeZoneRules = hashMapOf<String, TimeZoneRules>()

    @Suppress("UNCHECKED_CAST")
    private val cachedRegionIds = (NSTimeZone.knownTimeZoneNames as List<String>).toSet()

    override val databaseVersion: String get() = NSTimeZone.timeZoneDataVersion
    actual override val availableRegionIds: Set<String> get() = cachedRegionIds

    actual override fun hasRulesFor(regionId: String): Boolean {
        return cachedRegionIds.contains(regionId) || NSTimeZone.timeZoneWithName(regionId) != null
    }

    actual override fun rulesFor(regionId: String): TimeZoneRules {
        return timeZoneRules.getOrPut(regionId) {
            DarwinTimeZoneRules(
                NSTimeZone.timeZoneWithName(regionId)
                    ?: throw TimeZoneRulesException("No time zone exists with region ID '$regionId'")
            )
        }
    }
}

private class DarwinTimeZoneRules(timeZone: NSTimeZone) : TimeZoneRules {
    private val calendar = NSCalendar(NSCalendarIdentifierISO8601).also { it.timeZone = timeZone }
    private val timeZone: NSTimeZone get() = calendar.timeZone
    private val transitionsInYear = hashMapOf<Int, List<TimeZoneOffsetTransition>>()

    override fun offsetAt(dateTime: DateTime): UtcOffset {
        val date = checkNotNull(dateTime.toNSDateOrNull(calendar)) { "Failed to convert '$dateTime' to an NSDate" }
        return offsetAt(date)
    }

    override fun offsetAt(secondsSinceUnixEpoch: Seconds, nanoOfSeconds: Nanoseconds): UtcOffset {
        val date = NSDate.dateWithTimeIntervalSince1970(
            secondsSinceUnixEpoch.toDouble() + nanoOfSeconds.toDouble() / NANOSECONDS_PER_SECOND
        )
        return offsetAt(date)
    }

    override fun offsetAt(millisecondsSinceUnixEpoch: Milliseconds): UtcOffset {
        return offsetAt(NSDate.fromMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch))
    }

    override fun offsetAt(instant: Instant): UtcOffset = offsetAt(instant.toNSDate())

    override fun offsetAt(instant: PlatformInstant): UtcOffset {
        return timeZone.secondsFromGMTForDate(instant).convert<Int>().seconds.asUtcOffset()
    }

    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
        return transitionsInYear.let { map ->
            map.getOrPut(dateTime.year) {
                findTransitionsIn(dateTime.year)
            }.singleOrNull {
                if (it.isGap) {
                    dateTime >= it.dateTimeBefore && dateTime < it.dateTimeAfter
                } else {
                    dateTime >= it.dateTimeAfter && dateTime < it.dateTimeBefore
                }
            }
        }
    }

    override fun validOffsetsAt(dateTime: DateTime): List<UtcOffset> {
        return transitionAt(dateTime)?.validOffsets ?: listOf(offsetAt(dateTime))
    }

    override fun isDaylightSavingsAt(instant: Instant): Boolean {
        return timeZone.isDaylightSavingTimeForDate(instant.toNSDate())
    }

    override fun daylightSavingsAt(instant: Instant): Seconds {
        return timeZone.daylightSavingTimeOffsetForDate(instant.toNSDate()).toInt().seconds
    }

    override val hasFixedOffset: Boolean
        get() = timeZone.nextDaylightSavingTimeTransitionAfterDate(NSDate.distantPast) == null

    private fun findTransitionsIn(year: Int): List<TimeZoneOffsetTransition> {
        val startDateComponents = NSDateComponents().also {
            it.year = (year - 1).convert()
            it.month = 12
            it.day = 31
        }

        var currentDate = checkNotNull(calendar.dateFromComponents(startDateComponents)) {
            "Failed to build an NSDate from $startDateComponents"
        }

        return buildList {
            var nextTransition = timeZone.nextDaylightSavingTimeTransitionAfterDate(currentDate)

            while (nextTransition != null) {
                val yearOfNextDate = nextTransition.yearIn(calendar)

                if (yearOfNextDate < year) continue
                if (yearOfNextDate > year) break

                val offsetBefore = offsetAt(currentDate)
                val offsetAfter = offsetAt(nextTransition)
                val dateTimeBefore = nextTransition.toIslandDateTimeAt(offsetBefore)

                add(DarwinTimeZoneOffsetTransition(dateTimeBefore, offsetBefore, offsetAfter))

                currentDate = nextTransition
                nextTransition = timeZone.nextDaylightSavingTimeTransitionAfterDate(currentDate)
            }
        }
    }
}

private class DarwinTimeZoneOffsetTransition(
    override val dateTimeBefore: DateTime,
    override val offsetBefore: UtcOffset,
    override val offsetAfter: UtcOffset
) : TimeZoneOffsetTransition {

    init {
        require(offsetBefore != offsetAfter) { "Offsets must be different" }
        require(dateTimeBefore.nanosecond == 0) { "Nanosecond must be zero" }
    }

    override val dateTimeAfter: DateTime get() = dateTimeBefore + duration
    override val duration: Seconds get() = offsetAfter.totalSeconds - offsetBefore.totalSeconds
    override val isGap: Boolean get() = offsetAfter > offsetBefore
    override val isOverlap: Boolean get() = offsetAfter < offsetBefore

    override fun equals(other: Any?): Boolean {
        return this === other || (other is DarwinTimeZoneOffsetTransition &&
            dateTimeBefore == other.dateTimeBefore &&
            offsetBefore == other.offsetBefore &&
            offsetAfter == other.offsetAfter)
    }

    override fun hashCode(): Int {
        var result = dateTimeBefore.hashCode()
        result = 31 * result + offsetBefore.hashCode()
        result = 31 * result + offsetAfter.hashCode()
        return result
    }
}

private fun NSDate.Companion.fromMillisecondsSinceUnixEpoch(milliseconds: Milliseconds): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(milliseconds.toDouble() / MILLISECONDS_PER_SECOND)
}

private fun DateTime.toNSDateOrNull(calendar: NSCalendar) = calendar.dateFromComponents(toNSDateComponents())

private fun NSDate.yearIn(calendar: NSCalendar) = calendar.component(NSCalendarUnitYear, this).convert<Int>()
