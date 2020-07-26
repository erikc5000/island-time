package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.measures.*
import io.islandtime.measures.internal.minusWithOverflow
import io.islandtime.ranges.internal.*

/**
 * A half-open interval of time points.
 */
abstract class TimePointInterval<T : TimePoint<T>> internal constructor(
    override val start: T,
    override val endExclusive: T
) : Interval<T> {

    override val endInclusive: T
        get() = if (hasUnboundedEnd()) endExclusive else endExclusive - 1.nanoseconds

    override fun equals(other: Any?): Boolean {
        return other is TimePointInterval<*> && (isEmpty() && other.isEmpty() ||
            ((hasUnboundedStart() && other.hasUnboundedStart()) || start == other.start) &&
            ((hasUnboundedEnd() && other.hasUnboundedEnd()) || endExclusive == other.endExclusive))
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * start.hashCode() + endExclusive.hashCode())
    }

    override fun contains(value: T): Boolean {
        return (value >= start || hasUnboundedStart()) && (value < endExclusive || hasUnboundedEnd())
    }

    override fun isEmpty(): Boolean = start >= endExclusive

    /**
     * Converts this interval into a [Duration] of the same length.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    fun asDuration(): Duration {
        return when {
            isEmpty() -> Duration.ZERO
            isBounded() -> durationBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of 24-hour days in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    open val lengthInDays: LongDays
        get() = when {
            isEmpty() -> 0L.days
            isBounded() -> daysBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole hours in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInHours: LongHours
        get() = when {
            isEmpty() -> 0L.hours
            isBounded() -> hoursBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole minutes in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMinutes: LongMinutes
        get() = when {
            isEmpty() -> 0L.minutes
            isBounded() -> minutesBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole seconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInSeconds: LongSeconds
        get() = when {
            isEmpty() -> 0L.seconds
            isBounded() -> secondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole milliseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMilliseconds: LongMilliseconds
        get() = when {
            isEmpty() -> 0L.milliseconds
            isBounded() -> millisecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole microseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMicroseconds: LongMicroseconds
        get() = when {
            isEmpty() -> 0L.microseconds
            isBounded() -> microsecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole nanoseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInNanoseconds: LongNanoseconds
        get() = when {
            isEmpty() -> 0L.nanoseconds
            isBounded() -> nanosecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
}

/**
 * Check if this interval contains [value].
 *
 * This will always return `false` if [value] is `null`.
 */
operator fun <T : TimePoint<T>> TimePointInterval<T>.contains(value: TimePoint<*>?): Boolean {
    return value != null &&
        (value >= start || hasUnboundedStart()) &&
        (value < endExclusive || hasUnboundedEnd())
}

/**
 * Get the [Duration] between two time points.
 */
fun <T1, T2> durationBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): Duration {
    val secondDiff = endExclusive.secondsSinceUnixEpoch - start.secondsSinceUnixEpoch
    val nanoDiff =
        endExclusive.additionalNanosecondsSinceUnixEpoch minusWithOverflow start.additionalNanosecondsSinceUnixEpoch
    return durationOf(secondDiff, nanoDiff)
}

/**
 * Get the number of 24-hour days between two time points.
 */
fun <T1, T2> daysBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongDays {
    return secondsBetween(start, endExclusive).inDays
}

/**
 * Get the number of whole hours between two time points.
 */
fun <T1, T2> hoursBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongHours {
    return secondsBetween(start, endExclusive).inHours
}

/**
 * Get the number of whole minutes between two time points.
 */
fun <T1, T2> minutesBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongMinutes {
    return secondsBetween(start, endExclusive).inMinutes
}

/**
 * Get the number of whole seconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun <T1, T2> secondsBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongSeconds {
    return secondsBetween(
        start.secondsSinceUnixEpoch,
        start.additionalNanosecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.additionalNanosecondsSinceUnixEpoch
    )
}

/**
 * Get the number of whole milliseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun <T1, T2> millisecondsBetween(
    start: TimePoint<T1>,
    endExclusive: TimePoint<T2>
): LongMilliseconds {
    return millisecondsBetween(
        start.secondsSinceUnixEpoch,
        start.additionalNanosecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.additionalNanosecondsSinceUnixEpoch
    )
}

/**
 * Get the number of whole microseconds between two time points.
 *  @throws ArithmeticException if the result overflows
 */
fun <T1, T2> microsecondsBetween(
    start: TimePoint<T1>,
    endExclusive: TimePoint<T2>
): LongMicroseconds {
    return microsecondsBetween(
        start.secondsSinceUnixEpoch,
        start.additionalNanosecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.additionalNanosecondsSinceUnixEpoch
    )
}

/**
 * Get the number of nanoseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun <T1, T2> nanosecondsBetween(
    start: TimePoint<T1>,
    endExclusive: TimePoint<T2>
): LongNanoseconds {
    return nanosecondsBetween(
        start.secondsSinceUnixEpoch,
        start.additionalNanosecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.additionalNanosecondsSinceUnixEpoch
    )
}