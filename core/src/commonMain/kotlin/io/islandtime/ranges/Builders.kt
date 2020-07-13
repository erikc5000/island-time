@file:JvmMultifileClass
@file:JvmName("RangesKt")

package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.measures.days
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Combines this [DateRange] with a [TimeZone] to create a [ZonedDateTimeInterval] between the start of the first day
 * and the end of the last day in [zone].
 */
infix fun DateRange.at(zone: TimeZone): ZonedDateTimeInterval {
    return when {
        isEmpty() -> ZonedDateTimeInterval.EMPTY
        isUnbounded() -> ZonedDateTimeInterval.UNBOUNDED
        start == endInclusive -> {
            val zonedStart = start.startOfDayAt(zone)
            val zonedEnd = zonedStart + 1.days
            zonedStart until zonedEnd
        }
        else -> {
            val start = if (hasUnboundedStart()) {
                DateTime.MIN at zone
            } else {
                start.startOfDayAt(zone)
            }

            val end = if (hasUnboundedEnd()) {
                DateTime.MAX at zone
            } else {
                endInclusive.endOfDayAt(zone)
            }

            start..end
        }
    }
}

/**
 * Combines this [DateTimeInterval] with a [TimeZone] to create a [ZonedDateTimeInterval] where both endpoints are in
 * [zone].
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time of either
 * endpoint falls within a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it
 * falls within an overlap (meaning the local time exists twice), the earlier offset will be used.
 */
infix fun DateTimeInterval.at(zone: TimeZone): ZonedDateTimeInterval {
    return when {
        isEmpty() -> ZonedDateTimeInterval.EMPTY
        isUnbounded() -> ZonedDateTimeInterval.UNBOUNDED
        else -> {
            val start = if (hasUnboundedStart()) {
                DateTime.MIN at zone
            } else {
                start at zone
            }

            val end = if (hasUnboundedEnd()) {
                DateTime.MAX at zone
            } else {
                endExclusive at zone
            }

            start until end
        }
    }
}

/**
 * Combines this [InstantInterval] with a [TimeZone] to create an equivalent [ZonedDateTimeInterval] where both
 * endpoints are in [zone].
 */
infix fun InstantInterval.at(zone: TimeZone): ZonedDateTimeInterval {
    return when {
        isEmpty() -> ZonedDateTimeInterval.EMPTY
        isUnbounded() -> ZonedDateTimeInterval.UNBOUNDED
        else -> {
            val start = if (hasUnboundedStart()) {
                DateTime.MIN at zone
            } else {
                start at zone
            }

            val end = if (hasUnboundedEnd()) {
                DateTime.MAX at zone
            } else {
                endExclusive at zone
            }

            start until end
        }
    }
}