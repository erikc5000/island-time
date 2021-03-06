package io.islandtime.base

import io.islandtime.measures.*

/**
 * An object that can be placed exactly in time.
 *
 * An implementor of this interface contains enough information to represent an instant in time. As such, any time
 * point can be compared to another on the timeline and duration units can be added or subtracted.
 */
interface TimePoint<T> {
    /**
     * The number of seconds since the Unix epoch of 1970-01-01T00:00Z.
     */
    val secondsSinceUnixEpoch: LongSeconds

    @Deprecated(
        "Use additionalNanosecondsSinceUnixEpoch instead.",
        ReplaceWith("this.additionalNanosecondsSinceUnixEpoch"),
        DeprecationLevel.ERROR
    )
    val nanoOfSecondsSinceUnixEpoch: IntNanoseconds get() = additionalNanosecondsSinceUnixEpoch

    /**
     * The number of additional nanoseconds on top of [secondsSinceUnixEpoch].
     */
    val additionalNanosecondsSinceUnixEpoch: IntNanoseconds

    /**
     * The number of milliseconds since the Unix epoch of 1970-01-01T00:00Z.
     */
    val millisecondsSinceUnixEpoch: LongMilliseconds

    @Deprecated(
        "Use secondOfUnixEpoch instead.",
        ReplaceWith("this.secondOfUnixEpoch"),
        DeprecationLevel.ERROR
    )
    val unixEpochSecond: Long get() = secondOfUnixEpoch

    /**
     * The second of the Unix epoch.
     */
    val secondOfUnixEpoch: Long get() = secondsSinceUnixEpoch.value

    @Deprecated(
        "Use nanosecond instead.",
        ReplaceWith("this.nanosecond"),
        DeprecationLevel.ERROR
    )
    val unixEpochNanoOfSecond: Int get() = nanosecond

    /**
     * The nanosecond of the second.
     */
    val nanosecond: Int get() = additionalNanosecondsSinceUnixEpoch.value

    @Deprecated(
        "Use millisecondOfUnixEpoch instead.",
        ReplaceWith("this.millisecondOfUnixEpoch"),
        DeprecationLevel.ERROR
    )
    val unixEpochMillisecond: Long get() = millisecondOfUnixEpoch

    /**
     * The millisecond of the Unix epoch.
     */
    val millisecondOfUnixEpoch: Long get() = millisecondsSinceUnixEpoch.value

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

    operator fun plus(hours: IntHours): T
    operator fun plus(hours: LongHours): T
    operator fun plus(minutes: IntMinutes): T
    operator fun plus(minutes: LongMinutes): T
    operator fun plus(seconds: IntSeconds): T
    operator fun plus(seconds: LongSeconds): T
    operator fun plus(milliseconds: IntMilliseconds): T
    operator fun plus(milliseconds: LongMilliseconds): T
    operator fun plus(microseconds: IntMicroseconds): T
    operator fun plus(microseconds: LongMicroseconds): T
    operator fun plus(nanoseconds: IntNanoseconds): T
    operator fun plus(nanoseconds: LongNanoseconds): T

    operator fun minus(hours: IntHours): T
    operator fun minus(hours: LongHours): T
    operator fun minus(minutes: IntMinutes): T
    operator fun minus(minutes: LongMinutes): T
    operator fun minus(seconds: IntSeconds): T
    operator fun minus(seconds: LongSeconds): T
    operator fun minus(milliseconds: IntMilliseconds): T
    operator fun minus(milliseconds: LongMilliseconds): T
    operator fun minus(microseconds: IntMicroseconds): T
    operator fun minus(microseconds: LongMicroseconds): T
    operator fun minus(nanoseconds: IntNanoseconds): T
    operator fun minus(nanoseconds: LongNanoseconds): T

    companion object {
        /**
         * A [Comparator] that compares by timeline order.
         */
        val TIMELINE_ORDER: Comparator<TimePoint<*>> =
            compareBy<TimePoint<*>> { it.secondOfUnixEpoch }.thenBy { it.nanosecond }
    }
}
