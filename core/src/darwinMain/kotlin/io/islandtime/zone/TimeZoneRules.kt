package io.islandtime.zone

import co.touchlab.stately.isolate.IsolateState
import io.islandtime.*
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.darwin.toIslandDateTimeAt
import io.islandtime.darwin.toNSDate
import io.islandtime.darwin.toNSDateComponents
import io.islandtime.measures.*
import kotlinx.cinterop.convert
import platform.Foundation.*

/**
 * A time zone rules provider that draws from the database included on Darwin platforms.
 */
actual object PlatformTimeZoneRulesProvider : TimeZoneRulesProvider {
    private val timeZoneRules = IsolateState { hashMapOf<String, TimeZoneRules>() }

    @Suppress("UNCHECKED_CAST")
    private val cachedRegionIds = (NSTimeZone.knownTimeZoneNames as List<String>).toSet()

    override val databaseVersion: String get() = NSTimeZone.timeZoneDataVersion
    override val availableRegionIds get() = cachedRegionIds
    override fun hasRulesFor(regionId: String) = NSTimeZone.timeZoneWithName(regionId) != null

    override fun rulesFor(regionId: String): TimeZoneRules {
        // FIXME: This is a workaround for an issue with Stately
        val tzRules = DarwinTimeZoneRules(
            NSTimeZone.timeZoneWithName(regionId)
                ?: throw TimeZoneRulesException("No time zone exists with region ID '$regionId'")
        )

        return timeZoneRules.access {
            it.getOrPut(regionId) {
                tzRules
//                DarwinTimeZoneRules(
//                    NSTimeZone.timeZoneWithName(regionId)
//                        ?: throw TimeZoneRulesException("No time zone exists with region ID '$regionId'")
//                )
            }
        }
    }
}

private class DarwinTimeZoneRules(timeZone: NSTimeZone) : TimeZoneRules {

    private val calendar = NSCalendar(NSCalendarIdentifierISO8601).also { it.timeZone = timeZone }

    private val timeZone: NSTimeZone get() = calendar.timeZone

    private val transitionsInYear = IsolateState { hashMapOf<Int, List<TimeZoneOffsetTransition>>() }

    override fun offsetAt(dateTime: DateTime): UtcOffset {
        val date = dateTime.toNSDateOrNull(calendar)
            ?: throw IllegalStateException("Failed to convert '$dateTime' to an NSDate")

        return offsetAt(date)
    }

    override fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanoOfSeconds: IntNanoseconds): UtcOffset {
        val date = NSDate.dateWithTimeIntervalSince1970(
            secondsSinceUnixEpoch.value.toDouble() + nanoOfSeconds.value.toDouble() / NANOSECONDS_PER_SECOND
        )
        return offsetAt(date)
    }

    override fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset {
        return offsetAt(NSDate.fromMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch))
    }

    override fun offsetAt(instant: Instant) = offsetAt(instant.toNSDate())

    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
        return transitionsInYear.access { map ->
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

    override fun daylightSavingsAt(instant: Instant): IntSeconds {
        return timeZone.daylightSavingTimeOffsetForDate(instant.toNSDate()).toInt().seconds
    }

    override val hasFixedOffset: Boolean
        get() = timeZone.nextDaylightSavingTimeTransitionAfterDate(Instant.MIN.toNSDate()) == null

    private fun offsetAt(date: NSDate): UtcOffset {
        return timeZone.secondsFromGMTForDate(date).convert<Int>().seconds.asUtcOffset()
    }

    private fun findTransitionsIn(year: Int): List<TimeZoneOffsetTransition> {
        var currentDate = calendar.dateFromComponents(
            NSDateComponents().also {
                it.year = (year - 1).convert()
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

            transitionList += DarwinTimeZoneOffsetTransition(dateTimeBefore, offsetBefore, offsetAfter)

            currentDate = nextTransition
            nextTransition = timeZone.nextDaylightSavingTimeTransitionAfterDate(currentDate)
        }

        return transitionList
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

    override val dateTimeAfter get() = dateTimeBefore + duration
    override val duration get() = offsetAfter.totalSeconds - offsetBefore.totalSeconds
    override val isGap get() = offsetAfter > offsetBefore
    override val isOverlap get() = offsetAfter < offsetBefore

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

private fun NSDate.Companion.fromMillisecondsSinceUnixEpoch(milliseconds: LongMilliseconds): NSDate {
    return NSDate.dateWithTimeIntervalSince1970(milliseconds.value.toDouble() / MILLISECONDS_PER_SECOND)
}

private fun DateTime.toNSDateOrNull(calendar: NSCalendar) = calendar.dateFromComponents(toNSDateComponents())

private fun NSDate.yearIn(calendar: NSCalendar) = calendar.component(NSCalendarUnitYear, this).convert<Int>()