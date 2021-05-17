package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.measures.*
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
     * The number of 24-hour days in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    open val lengthInDays: Days
        get() = when {
            isEmpty() -> 0L.days
            isBounded() -> daysBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole hours in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInHours: Hours
        get() = when {
            isEmpty() -> 0L.hours
            isBounded() -> hoursBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole minutes in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMinutes: Minutes
        get() = when {
            isEmpty() -> 0L.minutes
            isBounded() -> minutesBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole seconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInSeconds: Seconds
        get() = when {
            isEmpty() -> 0L.seconds
            isBounded() -> secondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole milliseconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMilliseconds: Milliseconds
        get() = when {
            isEmpty() -> 0L.milliseconds
            isBounded() -> millisecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole microseconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMicroseconds: Microseconds
        get() = when {
            isEmpty() -> 0L.microseconds
            isBounded() -> microsecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole nanoseconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInNanoseconds: Nanoseconds
        get() = when {
            isEmpty() -> 0L.nanoseconds
            isBounded() -> nanosecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
}

/**
 * Checks if this interval contains [value]. This will always return `false` if [value] is `null`.
 */
operator fun <T : TimePoint<T>> TimePointInterval<T>.contains(value: TimePoint<*>?): Boolean {
    return value != null &&
        (value >= start || hasUnboundedStart()) &&
        (value < endExclusive || hasUnboundedEnd())
}

/**
 * Gets the [Duration] between two time points.
 */
fun durationBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Duration {
    val secondDiff = endExclusive.secondOfUnixEpoch - start.secondOfUnixEpoch
    val nanoDiff = endExclusive.nanosecond - start.nanosecond
    return durationOf(secondDiff.seconds, nanoDiff.nanoseconds)
}

/**
 * Gets the number of 24-hour days between two time points.
 */
fun daysBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Days {
    return secondsBetween(start, endExclusive).inWholeDays
}

/**
 * Gets the number of whole hours between two time points.
 */
fun hoursBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Hours {
    return secondsBetween(start, endExclusive).inWholeHours
}

/**
 * Gets the number of whole minutes between two time points.
 */
fun minutesBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Minutes {
    return secondsBetween(start, endExclusive).inWholeMinutes
}

/**
 * Gets the number of whole seconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun secondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Seconds {
    return secondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}

/**
 * Gets the number of whole milliseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun millisecondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Milliseconds {
    return millisecondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}

/**
 * Gets the number of whole microseconds between two time points.
 *  @throws ArithmeticException if the result overflows
 */
fun microsecondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Microseconds {
    return microsecondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}

/**
 * Gets the number of nanoseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun nanosecondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Nanoseconds {
    return nanosecondsBetween(
        start.secondOfUnixEpoch,
        start.nanosecond,
        endExclusive.secondOfUnixEpoch,
        endExclusive.nanosecond
    )
}
