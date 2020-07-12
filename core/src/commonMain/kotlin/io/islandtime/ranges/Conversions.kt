@file:JvmMultifileClass
@file:JvmName("RangesKt")

package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.TimePoint
import io.islandtime.measures.days
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Returns this interval with the precision reduced to just the date.
 */
fun DateTimeInterval.toDateRange(): DateRange = toDateRange { this }

/**
 * Returns this interval with the precision reduced to just the date.
 */
fun OffsetDateTimeInterval.toDateRange(): DateRange = toDateRange(OffsetDateTime::dateTime)

/**
 * Returns this interval with the precision reduced to just the date.
 */
fun ZonedDateTimeInterval.toDateRange(): DateRange = toDateRange(ZonedDateTime::dateTime)

/**
 * Converts this interval to the equivalent [DateRange] when both endpoints are in [zone].
 */
fun InstantInterval.toDateRangeAt(zone: TimeZone): DateRange = toDateRange { toDateTimeOrUnboundedAt(zone) }

/**
 * Returns this interval with the precision reduced to only the local date and time.
 */
fun OffsetDateTimeInterval.toDateTimeInterval(): DateTimeInterval = toDateTimeInterval(OffsetDateTime::dateTime)

/**
 * Returns this interval with the precision reduced to only the local date and time.
 */
fun ZonedDateTimeInterval.toDateTimeInterval(): DateTimeInterval = toDateTimeInterval(ZonedDateTime::dateTime)

/**
 * Converts this interval to the equivalent [DateTimeInterval] when both endpoints are in [zone].
 */
fun InstantInterval.toDateTimeIntervalAt(zone: TimeZone): DateTimeInterval {
    return toDateTimeInterval { toDateTimeOrUnboundedAt(zone) }
}

/**
 * Converts this interval to an [OffsetDateTimeInterval].
 *
 * While similar to `ZonedDateTime`, an `OffsetDateTime` representation is unaffected by time zone rule changes or
 * database differences between systems, making it better suited for use cases involving persistence or network
 * transfer.
 */
fun ZonedDateTimeInterval.toOffsetDateTimeInterval(): OffsetDateTimeInterval {
    return mapEndpointsOdt { it.toOffsetDateTime() }
}

/**
 * Converts this interval to an equivalent [ZonedDateTimeInterval] where both endpoints are given a fixed-offset time
 * zone.
 *
 * This comes with the caveat that a fixed-offset zone lacks knowledge of any region and will not respond to daylight
 * savings time changes. To convert each endpoint to a region-based zone, use [toZonedDateTimeInterval] instead.
 *
 * @see toZonedDateTimeInterval
 */
fun OffsetDateTimeInterval.asZonedDateTimeInterval(): ZonedDateTimeInterval {
    return mapEndpointsZdt { it.asZonedDateTime() }
}

/**
 * Converts this interval to a [ZonedDateTimeInterval] using the specified [strategy] to adjust each endpoint to a valid
 * date, time, and offset in [zone].
 *
 * - [OffsetConversionStrategy.PRESERVE_INSTANT] - Preserve the instant captured by the date, time, and offset,
 * ignoring the local time.
 *
 * - [OffsetConversionStrategy.PRESERVE_LOCAL_TIME] - Preserve the local date and time in the new time zone, adjusting
 * the offset if needed.
 *
 * Alternatively, you can use [asZonedDateTimeInterval] to convert each endpoint to a [ZonedDateTime] with an equivalent
 * fixed-offset zone. However, this comes with the caveat that a fixed-offset zone lacks knowledge of any region and
 * will not respond to daylight savings time changes.
 *
 * @see asZonedDateTimeInterval
 */
fun OffsetDateTimeInterval.toZonedDateTimeInterval(
    zone: TimeZone,
    strategy: OffsetConversionStrategy
): ZonedDateTimeInterval {
    return mapEndpointsZdt {
        when (it.dateTime) {
            // FIXME: Need to move away from using MIN and MAX in ranges. This is awkward.
            DateTime.MIN -> DateTime.MIN at zone
            DateTime.MAX -> DateTime.MAX at zone
            else -> it.toZonedDateTime(zone, strategy)
        }
    }
}

/**
 * Converts this range to an [InstantInterval] between the start of the first day and the end of the last day in [zone].
 */
fun DateRange.toInstantIntervalAt(zone: TimeZone): InstantInterval {
    return when {
        isEmpty() -> InstantInterval.EMPTY
        isUnbounded() -> InstantInterval.UNBOUNDED
        else -> {
            val start = if (hasUnboundedStart()) Instant.MIN else start.startOfDayAt(zone).toInstant()
            val end = if (hasUnboundedEnd()) Instant.MAX else endInclusive.endOfDayAt(zone).toInstant()
            start..end
        }
    }
}

/**
 * Converts this interval to an [InstantInterval] where both endpoints are in [zone].
 *
 * Due to daylight savings time transitions, there a few complexities to be aware of. If the local time of either
 * endpoint falls within a gap (meaning it doesn't exist), it will be adjusted forward by the length of the gap. If it
 * falls within an overlap (meaning the local time exists twice), the earlier offset will be used.
 */
fun DateTimeInterval.toInstantIntervalAt(zone: TimeZone): InstantInterval {
    return when {
        isEmpty() -> InstantInterval.EMPTY
        isUnbounded() -> InstantInterval.UNBOUNDED
        else -> {
            val start = if (hasUnboundedStart()) Instant.MIN else (start at zone).toInstant()
            val end = if (hasUnboundedEnd()) Instant.MAX else (endExclusive at zone).toInstant()
            start until end
        }
    }
}

/**
 * Converts this interval to an [InstantInterval].
 */
fun OffsetDateTimeInterval.toInstantInterval(): InstantInterval {
    return (this as TimePointInterval<*>).toInstantInterval()
}

/**
 * Converts this interval to an [InstantInterval].
 */
fun ZonedDateTimeInterval.toInstantInterval(): InstantInterval {
    return (this as TimePointInterval<*>).toInstantInterval()
}

private inline fun <T> TimeInterval<T>.toDateRange(toDateTime: T.() -> DateTime): DateRange {
    return when {
        isEmpty() -> DateRange.EMPTY
        isUnbounded() -> DateRange.UNBOUNDED
        else -> {
            val endDateTime = toDateTime(endExclusive)

            val endDate = if (endDateTime.time == Time.MIDNIGHT) {
                endDateTime.date - 1.days
            } else {
                endDateTime.date
            }

            toDateTime(start).date..endDate
        }
    }
}

private inline fun <T : TimePoint<T>> TimePointInterval<T>.toDateTimeInterval(
    toDateTime: T.() -> DateTime
): DateTimeInterval {
    return when {
        isEmpty() -> DateTimeInterval.EMPTY
        isUnbounded() -> DateTimeInterval.UNBOUNDED
        else -> toDateTime(start) until toDateTime(endExclusive)
    }
}

private fun TimePointInterval<*>.toInstantInterval(): InstantInterval {
    return when {
        isEmpty() -> InstantInterval.EMPTY
        isUnbounded() -> InstantInterval.UNBOUNDED
        else -> {
            val startInstant = if (hasUnboundedStart()) Instant.MIN else start.toInstant()
            val endInstant = if (hasUnboundedEnd()) Instant.MAX else endExclusive.toInstant()
            startInstant until endInstant
        }
    }
}

private inline fun <T : TimePoint<T>> TimePointInterval<T>.mapEndpointsZdt(
    transform: (T) -> ZonedDateTime
): ZonedDateTimeInterval {
    return when {
        isEmpty() -> ZonedDateTimeInterval.EMPTY
        isUnbounded() -> ZonedDateTimeInterval.UNBOUNDED
        else -> transform(start) until transform(endExclusive)
    }
}

private inline fun <T : TimePoint<T>> TimePointInterval<T>.mapEndpointsOdt(
    transform: (T) -> OffsetDateTime
): OffsetDateTimeInterval {
    return when {
        isEmpty() -> OffsetDateTimeInterval.EMPTY
        isUnbounded() -> OffsetDateTimeInterval.UNBOUNDED
        else -> transform(start) until transform(endExclusive)
    }
}

private fun Instant.toDateTimeOrUnboundedAt(zone: TimeZone): DateTime {
    return when (this) {
        Instant.MIN -> DateTime.MIN
        Instant.MAX -> DateTime.MAX
        else -> toDateTimeAt(zone)
    }
}