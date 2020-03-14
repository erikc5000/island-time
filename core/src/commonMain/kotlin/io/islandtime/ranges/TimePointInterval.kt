package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.measures.*
import io.islandtime.measures.internal.minusWithOverflow
import io.islandtime.ranges.internal.*
import kotlin.jvm.JvmName

/**
 * A half-open interval of time points.
 */
abstract class TimePointInterval<T : TimePoint<T>> internal constructor(
    private val _start: T,
    private val _endExclusive: T
) : TimeInterval<T> {

    override val start: T get() = _start

    /**
     * The last representable time point within the interval.
     */
    val endInclusive: T get() = if (hasUnboundedEnd()) _endExclusive else _endExclusive - 1.nanoseconds

    override val endExclusive: T get() = _endExclusive

    override fun equals(other: Any?): Boolean {
        return other is TimePointInterval<*> && (isEmpty() && other.isEmpty() ||
            ((hasUnboundedStart() && other.hasUnboundedStart()) ||  _start == other._start) &&
            ((hasUnboundedEnd() && other.hasUnboundedEnd()) || _endExclusive == other._endExclusive))
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * _start.hashCode() + _endExclusive.hashCode())
    }

    override fun contains(value: T): Boolean {
        return (value >= _start || hasUnboundedStart()) && (value < _endExclusive || hasUnboundedEnd())
    }

    @JvmName("containsOther")
    operator fun contains(value: TimePoint<*>): Boolean {
        return (value >= _start || hasUnboundedStart()) && (value < _endExclusive || hasUnboundedEnd())
    }

    override fun isEmpty(): Boolean = _start >= _endExclusive

    /**
     * Convert the interval into a [Duration] of the same length.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    fun asDuration(): Duration {
        return when {
            isEmpty() -> Duration.ZERO
            isBounded() -> durationBetween(_start, _endExclusive)
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
            isBounded() -> daysBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole hours in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInHours: LongHours
        get() = when {
            isEmpty() -> 0L.hours
            isBounded() -> hoursBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole minutes in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMinutes: LongMinutes
        get() = when {
            isEmpty() -> 0L.minutes
            isBounded() -> minutesBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole seconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInSeconds: LongSeconds
        get() = when {
            isEmpty() -> 0L.seconds
            isBounded() -> secondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole milliseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMilliseconds: LongMilliseconds
        get() = when {
            isEmpty() -> 0L.milliseconds
            isBounded() -> millisecondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole microseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMicroseconds: LongMicroseconds
        get() = when {
            isEmpty() -> 0L.microseconds
            isBounded() -> microsecondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole nanoseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInNanoseconds: LongNanoseconds
        get() = when {
            isEmpty() -> 0L.nanoseconds
            isBounded() -> nanosecondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }
}

/**
 * Get the [Duration] between two time points.
 */
fun <T1, T2> durationBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): Duration {
    val secondDiff = endExclusive.secondsSinceUnixEpoch - start.secondsSinceUnixEpoch
    val nanoDiff = endExclusive.nanoOfSecondsSinceUnixEpoch minusWithOverflow start.nanoOfSecondsSinceUnixEpoch
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
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}

/**
 * Get the number of whole milliseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun <T1, T2> millisecondsBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongMilliseconds {
    return millisecondsBetween(
        start.secondsSinceUnixEpoch,
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}

/**
 * Get the number of whole microseconds between two time points.
 *  @throws ArithmeticException if the result overflows
 */
fun <T1, T2> microsecondsBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongMicroseconds {
    return microsecondsBetween(
        start.secondsSinceUnixEpoch,
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}

/**
 * Get the number of nanoseconds between two time points.
 * @throws ArithmeticException if the result overflows
 */
fun <T1, T2> nanosecondsBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongNanoseconds {
    return nanosecondsBetween(
        start.secondsSinceUnixEpoch,
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpoch,
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}