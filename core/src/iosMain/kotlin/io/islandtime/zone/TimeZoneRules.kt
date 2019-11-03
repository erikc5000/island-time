package io.islandtime.zone

import co.touchlab.stately.collections.SharedHashMap
import io.islandtime.*
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.ios.toIslandDateTimeAt
import io.islandtime.ios.toNSDate
import io.islandtime.ios.toNSDateComponents
import io.islandtime.measures.*
import platform.Foundation.*

actual object PlatformTimeZoneRulesProvider : TimeZoneRulesProvider {
    private val timeZoneRules = SharedHashMap<String, TimeZoneRules>()

    @Suppress("UNCHECKED_CAST")
    private val cachedRegionIds = (NSTimeZone.knownTimeZoneNames as List<String>).toSet()

    override val databaseVersion: String get() = NSTimeZone.timeZoneDataVersion
    override val availableRegionIds get() = cachedRegionIds
    override fun hasRulesFor(regionId: String) = NSTimeZone.timeZoneWithName(regionId) != null

    override fun rulesFor(regionId: String): TimeZoneRules {
        return timeZoneRules.getOrPut(regionId) {
            // TODO: May overwrite -- putIfAbsent() analog would be better here
            IosTimeZoneRules(
                NSTimeZone.timeZoneWithName(regionId)
                    ?: throw TimeZoneRulesException("No time zone exists with region ID '$regionId'")
            )
        }
    }
}

private class IosTimeZoneRules(timeZone: NSTimeZone) : TimeZoneRules {

    private val calendar = NSCalendar(NSCalendarIdentifierISO8601).also { it.timeZone = timeZone }
    private inline val timeZone: NSTimeZone get() = calendar.timeZone

    private val transitionsInYear = SharedHashMap<Int, List<TimeZoneOffsetTransition>>()

    override fun offsetAt(dateTime: DateTime): UtcOffset {
        val date = dateTime.toNSDateOrNull(calendar)
            ?: throw IllegalStateException("Failed to convert '$dateTime' to an NSDate")

        return offsetAt(date)
    }

    override fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanosecondAdjustment: IntNanoseconds): UtcOffset {
        val date = NSDate.dateWithTimeIntervalSince1970(
            secondsSinceUnixEpoch.value.toDouble() + nanosecondAdjustment.value.toDouble() / NANOSECONDS_PER_SECOND
        )
        return offsetAt(date)
    }

    override fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset {
        return offsetAt(NSDate.fromMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch))
    }

    override fun offsetAt(instant: Instant) = offsetAt(instant.toNSDate())

    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
        return transitionsInYear
            .getOrPut(dateTime.year) {
                // TODO: May overwrite -- putIfAbsent() analog would be better here
                findTransitionsIn(dateTime.year)
            }
            .singleOrNull {
                if (it.isGap) {
                    dateTime >= it.dateTimeBefore && dateTime < it.dateTimeAfter
                } else {
                    dateTime >= it.dateTimeAfter && dateTime < it.dateTimeBefore
                }
            }
    }

    override fun validOffsetsAt(dateTime: DateTime): List<UtcOffset> {
        return transitionAt(dateTime)?.validOffsets ?: listOf(offsetAt(dateTime))
    }

    override fun isDaylightSavingsAt(instant: Instant): Boolean {
        return timeZone.isDaylightSavingTimeForDate(instant.toNSDate())
    }

    override fun daylightSavingsAt(instant: Instant): IntSeconds {
        return timeZone.daylightSavingTimeOffsetForDate(instant.toNSDate()).toInt().seconds
    }

    override val hasFixedOffset: Boolean
        get() = timeZone.nextDaylightSavingTimeTransitionAfterDate(Instant.MIN.toNSDate()) == null

    private fun offsetAt(date: NSDate): UtcOffset {
        return timeZone.secondsFromGMTForDate(date).toInt().seconds.asUtcOffset()
    }

    private fun findTransitionsIn(year: Int): List<TimeZoneOffsetTransition> {
        var currentDate = calendar.dateFromComponents(
            NSDateComponents().also {
                it.year = (year - 1).toLong()
                it.month = 12
                it.day = 31
            }
        ) ?: throw IllegalStateException("Failed to build an NSDate")

        val transitionList = mutableListOf<TimeZoneOffsetTransition>()
        var nextTransition = timeZone.nextDaylightSavingTimeTransitionAfterDate(currentDate)

        while (nextTransition != null) {
            val yearOfNextDate = nextTransition.yearIn(calendar)

            if (yearOfNextDate < year) continue
            if (yearOfNextDate > year) break

            val offsetBefore = offsetAt(currentDate)
            val offsetAfter = offsetAt(nextTransition)
            val dateTimeBefore = nextTransition.toIslandDateTimeAt(offsetBefore)

            transitionList += IosTimeZoneOffsetTransition(dateTimeBefore, offsetBefore, offsetAfter)

            currentDate = nextTransition
            nextTransition = timeZone.nextDaylightSavingTimeTransitionAfterDate(currentDate)
        }

        return transitionList
    }
}

private class IosTimeZoneOffsetTransition(
    override val dateTimeBefore: DateTime,
    override val offsetBefore: UtcOffset,
    override val offsetAfter: UtcOffset
) : TimeZoneOffsetTransition {

    init {
        require(offsetBefore != offsetAfter) { "Offsets must be different" }
        require(dateTimeBefore.nanosecond == 0) { "Nanosecond must be zero" }
    }

    override val dateTimeAfter get() = dateTimeBefore + duration
    override val duration get() = offsetAfter.totalSeconds - offsetBefore.totalSeconds
    override val isGap get() = offsetAfter > offsetBefore
    override val isOverlap get() = offsetAfter < offsetBefore

    override fun equals(other: Any?): Boolean {
        return this === other || (other is IosTimeZoneOffsetTransition &&
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

private fun NSDate.Companion.fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(milliseconds.value.toDouble() / MILLISECONDS_PER_SECOND)
}

private fun DateTime.toNSDateOrNull(calendar: NSCalendar) = calendar.dateFromComponents(toNSDateComponents())

private fun NSDate.yearIn(calendar: NSCalendar) = calendar.component(NSCalendarUnitYear, this).toInt()