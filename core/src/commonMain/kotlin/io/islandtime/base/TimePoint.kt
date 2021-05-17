package io.islandtime.base

import io.islandtime.internal.deprecatedToError
import io.islandtime.measures.*

/**
 * An object that can be placed exactly in time.
 *
 * An implementor of this interface contains enough information to represent an instant in time. As such, any time
 * point can be compared to another on the timeline and duration units can be added or subtracted.
 */
interface TimePoint<T> {
    /**
     * The second of the Unix epoch.
     */
    val secondOfUnixEpoch: Long

    /**
     * The nanosecond of the second.
     */
    val nanosecond: Int

    /**
     * The millisecond of the Unix epoch.
     */
    val millisecondOfUnixEpoch: Long
    
    /**
     * The number of seconds since the Unix epoch of 1970-01-01T00:00Z.
     */
    val secondsSinceUnixEpoch: Seconds get() = secondOfUnixEpoch.seconds

    /**
     * The number of additional nanoseconds on top of [secondsSinceUnixEpoch].
     */
    val additionalNanosecondsSinceUnixEpoch: Nanoseconds get() = nanosecond.nanoseconds

    /**
     * The number of milliseconds since the Unix epoch of 1970-01-01T00:00Z.
     */
    val millisecondsSinceUnixEpoch: Milliseconds get() = millisecondOfUnixEpoch.milliseconds

    @Deprecated(
        "Use secondOfUnixEpoch instead.",
        ReplaceWith("this.secondOfUnixEpoch"),
        DeprecationLevel.ERROR
    )
    val unixEpochSecond: Long get() = deprecatedToError()

    @Deprecated(
        "Use nanosecond instead.",
        ReplaceWith("this.nanosecond"),
        DeprecationLevel.ERROR
    )
    val unixEpochNanoOfSecond: Int get() = deprecatedToError()

    @Deprecated(
        "Use millisecondOfUnixEpoch instead.",
        ReplaceWith("this.millisecondOfUnixEpoch"),
        DeprecationLevel.ERROR
    )
    val unixEpochMillisecond: Long get() = deprecatedToError()

    /**
     * Checks if this time point represents the same instant as [other]. Unlike the equals operator, equality is
     * determined solely by timeline order.
     */
    fun isSameInstantAs(other: TimePoint<*>): Boolean {
        return secondOfUnixEpoch == other.secondOfUnixEpoch && nanosecond == other.nanosecond
    }

    /**
     *
     * Compares this time point with another time point.
     *
     * Time points can be compared to other time points based on timeline order, but aren't required to implement the
     * [Comparable] interface since they don't necessarily have a natural order that's consistent with equals.
     */
    operator fun compareTo(other: TimePoint<*>): Int {
        val second = secondOfUnixEpoch
        val otherSecond = other.secondOfUnixEpoch

        val secondDiff = second.compareTo(otherSecond)

        return if (secondDiff != 0) {
            secondDiff
        } else {
            nanosecond - other.nanosecond
        }
    }

    operator fun plus(hours: Hours): T
    operator fun plus(minutes: Minutes): T
    operator fun plus(seconds: Seconds): T
    operator fun plus(milliseconds: Milliseconds): T
    operator fun plus(microseconds: Microseconds): T
    operator fun plus(nanoseconds: Nanoseconds): T

    operator fun minus(hours: Hours): T
    operator fun minus(minutes: Minutes): T
    operator fun minus(seconds: Seconds): T
    operator fun minus(milliseconds: Milliseconds): T
    operator fun minus(microseconds: Microseconds): T
    operator fun minus(nanoseconds: Nanoseconds): T

    companion object {
        /**
         * A [Comparator] that compares by timeline order.
         */
        val TIMELINE_ORDER: Comparator<TimePoint<*>> =
            compareBy<TimePoint<*>> { it.secondOfUnixEpoch }.thenBy { it.nanosecond }
    }
}
