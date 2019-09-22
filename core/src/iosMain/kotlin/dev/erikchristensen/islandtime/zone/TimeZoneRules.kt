package dev.erikchristensen.islandtime.zone

import co.touchlab.stately.collections.SharedHashMap
import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.internal.MILLISECONDS_PER_SECOND
import dev.erikchristensen.islandtime.interval.IntSeconds
import dev.erikchristensen.islandtime.interval.LongMilliseconds
import dev.erikchristensen.islandtime.interval.seconds
import dev.erikchristensen.islandtime.ios.toIslandInstant
import dev.erikchristensen.islandtime.ios.toNSDate
import dev.erikchristensen.islandtime.ios.toNSDateComponents
import platform.Foundation.*

actual object PlatformDefault : TimeZoneRulesProvider {
    private val timeZoneRules = SharedHashMap<String, TimeZoneRules>()

    @Suppress("UNCHECKED_CAST")
    private val cachedRegionIds = (NSTimeZone.knownTimeZoneNames as List<String>).toSet()

    override val databaseVersion: String get() = NSTimeZone.timeZoneDataVersion
    override val availableRegionIds = cachedRegionIds

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

    override fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset {
        val date = NSDate.dateWithTimeIntervalSince1970(
            millisecondsSinceUnixEpoch.value.toDouble() / MILLISECONDS_PER_SECOND
        )
        return offsetAt(date)
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
            val dateTimeBefore = nextTransition.toIslandInstant().toDateTimeAt(offsetBefore)

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

    val secondsSinceUnixEpoch get() = dateTimeBefore.secondsSinceUnixEpochAt(offsetBefore)
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

private fun DateTime.toNSDateOrNull(calendar: NSCalendar) = calendar.dateFromComponents(toNSDateComponents())

private fun NSDate.yearIn(calendar: NSCalendar) = calendar.component(NSCalendarUnitYear, this).toInt()