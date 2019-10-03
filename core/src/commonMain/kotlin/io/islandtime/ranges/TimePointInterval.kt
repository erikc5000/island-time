package io.islandtime.ranges

import io.islandtime.Instant
import io.islandtime.OffsetDateTime
import io.islandtime.TimePoint
import io.islandtime.ZonedDateTime
import io.islandtime.interval.*
import kotlin.jvm.JvmName

/**
 * A half-open time interval
 */
interface TimeInterval<T> {
    val start: T
    val endExclusive: T

    val hasUnboundedStart: Boolean
    val hasUnboundedEnd: Boolean

    val hasBoundedStart: Boolean get() = !hasUnboundedStart
    val hasBoundedEnd: Boolean get() = !hasUnboundedEnd
    val isBounded: Boolean get() = hasBoundedStart && hasBoundedEnd
    val isUnbounded: Boolean get() = hasUnboundedStart && hasUnboundedEnd

    operator fun contains(value: T): Boolean

    fun isEmpty(): Boolean
}

//interface DayInterval<T> {
//    val start: T
//    val endInclusive: T
//    operator fun contains(value: T): Boolean
//    fun isEmpty(): Boolean
//}

/**
 * A half-open interval of time points
 */
abstract class TimePointInterval<T : TimePoint<T>> internal constructor(
    private val _start: T,
    private val _endExclusive: T
) : TimeInterval<T>,
    TimePointProgressionBuilder<T> {

    override val start: T get() = _start
    override val endExclusive: T get() = _endExclusive
    override val first: T get() = _start
    override val last: T get() = _endExclusive - 1.nanoseconds

    override fun equals(other: Any?): Boolean {
        return other is TimePointInterval<*> && (isEmpty() && other.isEmpty() ||
            _start == other._start && _endExclusive == other._endExclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * _start.hashCode() + _endExclusive.hashCode())
    }

    override fun contains(value: T): Boolean {
        return (value >= _start || hasUnboundedStart) && (value < _endExclusive || hasUnboundedEnd)
    }

    @JvmName("containsTimePoint")
    operator fun <U : TimePoint<U>> contains(value: U): Boolean {
        return (value >= _start || hasUnboundedStart) && (value < _endExclusive || hasUnboundedEnd)
    }

    override fun isEmpty(): Boolean = _start >= _endExclusive

    /**
     * Convert a range of time points into a duration containing each nanosecond in the range. As a range is inclusive,
     * if the start and end instant are the same, the resulting duration will contain one nanosecond.
     */
    fun asDuration(): Duration {
        return when {
            isEmpty() -> Duration.ZERO
            isBounded -> durationBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of 24-hour days in a range of time points
     */
    open val days: LongDays
        get() = when {
            isEmpty() -> 0L.days
            isBounded -> daysBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of hours in a range of time points
     */
    val hours: LongHours
        get() = when {
            isEmpty() -> 0L.hours
            isBounded -> hoursBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of hours in a range of time points
     */
    val minutes: LongMinutes
        get() = when {
            isEmpty() -> 0L.minutes
            isBounded -> minutesBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of seconds in a range of time points
     */
    val seconds: LongSeconds
        get() = when {
            isEmpty() -> 0L.seconds
            isBounded -> secondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of milliseconds in a range of time points
     */
    val milliseconds: LongMilliseconds
        get() = when {
            isEmpty() -> 0L.milliseconds
            isBounded -> millisecondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of microseconds in a range of time points
     */
    val microseconds: LongMicroseconds
        get() = when {
            isEmpty() -> 0L.microseconds
            isBounded -> microsecondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of nanoseconds in a range of time points
     */
    val nanoseconds: LongNanoseconds
        get() = when {
            isEmpty() -> 0L.nanoseconds
            isBounded -> nanosecondsBetween(_start, _endExclusive)
            else -> throwUnboundedIntervalException()
        }
}

//inline fun <reified T: TimePoint<T>> emptyTimePointInterval(): TimePointInterval<T> {
//    @Suppress("UNCHECKED_CAST")
//    return when (T::class) {
//        ZonedDateTime::class -> ZonedDateTimeInterval.EMPTY as TimePointInterval<T>
//        OffsetDateTime::class -> OffsetDateTimeInterval.EMPTY as TimePointInterval<T>
//        Instant::class -> InstantInterval.EMPTY as TimePointInterval<T>
//        else -> throw IllegalArgumentException()
//    }
//}

//fun <T : TimePoint<T>> TimePointInterval<T>.encloses(interval: TimePointInterval<T>): Boolean {
//    return (start <= interval.start || (hasUnboundedStart && interval.hasBoundedStart)) &&
//        (endExclusive >= interval.endExclusive || (hasUnboundedEnd && interval.hasBoundedEnd))
//}

/**
 * Get the [Duration] between two points in time
 */
fun <T1, T2> durationBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): Duration {
    val secondDiff = endExclusive.secondsSinceUnixEpoch minusExact start.secondsSinceUnixEpoch
    val nanoDiff = endExclusive.nanoOfSecondsSinceUnixEpoch minusWithOverflow start.nanoOfSecondsSinceUnixEpoch
    return durationOf(secondDiff, nanoDiff)
}

/**
 * Get the number of 24-hour days between two points in time
 */
fun <T1, T2> daysBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongDays {
    return secondsBetween(start, endExclusive).inWholeDays
}

/**
 * Get the number of whole hours between two points in time
 */
fun <T1, T2> hoursBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongHours {
    return secondsBetween(start, endExclusive).inWholeHours
}

/**
 * Get the number of whole minutes between two points in time
 */
fun <T1, T2> minutesBetween(start: TimePoint<T1>, endExclusive: TimePoint<T2>): LongMinutes {
    return secondsBetween(start, endExclusive).inWholeMinutes
}

/**
 * Get the number of whole seconds between two points in time
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
 * Get the number of whole milliseconds between two points in time
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
 * Get the number of whole microseconds between two points in time
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
 * Get the number of nanoseconds between two points in time
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