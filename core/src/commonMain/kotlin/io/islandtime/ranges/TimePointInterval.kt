package io.islandtime.ranges

import io.islandtime.base.TimePoint
import io.islandtime.between
import io.islandtime.internal.deprecatedToError
import io.islandtime.measures.*

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

    @Deprecated(
        message = "Replace with toDuration()",
        replaceWith = ReplaceWith("this.toDuration()", "io.islandtime.ranges.toDuration"),
        level = DeprecationLevel.WARNING
    )
    fun asDuration(): Duration = toDuration()
}

/**
 * Checks if this interval contains [value]. This will always return `false` if [value] is `null`.
 */
operator fun <T : TimePoint<T>> TimePointInterval<T>.contains(value: TimePoint<*>?): Boolean {
    return value != null &&
        (value >= start || hasUnboundedStart()) &&
        (value < endExclusive || hasUnboundedEnd())
}

@Deprecated(
    message = "Replace with Duration.between()",
    replaceWith = ReplaceWith(
        "Duration.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Duration"
    ),
    level = DeprecationLevel.WARNING
)
fun durationBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Duration = Duration.between(start, endExclusive)

@Deprecated(
    message = "Replace with Days.between() or Hours.between().inWholeDays as appropriate",
    replaceWith = ReplaceWith(""),
    level = DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER", "unused")
fun daysBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Days = deprecatedToError()

@Deprecated(
    message = "Replace with Hours.between()",
    replaceWith = ReplaceWith(
        "Hours.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Hours"
    ),
    level = DeprecationLevel.WARNING
)
fun hoursBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Hours = Hours.between(start, endExclusive)

@Deprecated(
    message = "Replace with Minutes.between()",
    replaceWith = ReplaceWith(
        "Minutes.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Minutes"
    ),
    level = DeprecationLevel.WARNING
)
fun minutesBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Minutes =
    Minutes.between(start, endExclusive)

@Deprecated(
    message = "Replace with Seconds.between()",
    replaceWith = ReplaceWith(
        "Seconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Seconds"
    ),
    level = DeprecationLevel.WARNING
)
fun secondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Seconds = Seconds.between(start, endExclusive)

@Deprecated(
    message = "Replace with Milliseconds.between()",
    replaceWith = ReplaceWith(
        "Milliseconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Milliseconds"
    ),
    level = DeprecationLevel.WARNING
)
fun millisecondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Milliseconds =
    Milliseconds.between(start, endExclusive)

@Deprecated(
    message = "Replace with Microseconds.between()",
    replaceWith = ReplaceWith(
        "Microseconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Microseconds"
    ),
    level = DeprecationLevel.WARNING
)
fun microsecondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Microseconds =
    Microseconds.between(start, endExclusive)

@Deprecated(
    message = "Replace with Nanoseconds.between()",
    replaceWith = ReplaceWith(
        "Nanoseconds.between(start, endExclusive)",
        "io.islandtime.between",
        "io.islandtime.measures.Nanoseconds"
    ),
    level = DeprecationLevel.WARNING
)
fun nanosecondsBetween(start: TimePoint<*>, endExclusive: TimePoint<*>): Nanoseconds =
    Nanoseconds.between(start, endExclusive)
