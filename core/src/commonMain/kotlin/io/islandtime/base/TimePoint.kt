package io.islandtime.base

import io.islandtime.measures.*

/**
 * A property of a time point.
 */
sealed class TimePointProperty {
    /**
     * The second of the Unix epoch. `0` corresponds to `1970-01-01T00:00Z`.
     */
    object SecondOfUnixEpoch : TimePointProperty(), NumberProperty

    /**
     * The time point represented as an [io.islandtime.Instant].
     */
    object Instant : TimePointProperty(), ObjectProperty<io.islandtime.Instant>
}

/**
 * An object that can be placed exactly in time.
 *
 * An implementor of this interface contains enough information to represent an instant in time. As such, any time
 * point can be compared to another on the timeline and duration units can be added or subtracted.
 */
interface TimePoint<T> : Temporal {
    /**
     * The number of seconds since the Unix epoch of 1970-01-01T00:00Z
     */
    val secondsSinceUnixEpoch: LongSeconds

    /**
     * The number of additional nanoseconds on top of [secondsSinceUnixEpoch]
     */
    val nanoOfSecondsSinceUnixEpoch: IntNanoseconds

    /**
     * The number of milliseconds since the Unix epoch of 1970-01-01T00:00Z
     */
    val millisecondsSinceUnixEpoch: LongMilliseconds

    /**
     * The second of the Unix epoch
     */
    val unixEpochSecond: Long get() = secondsSinceUnixEpoch.value

    /**
     * The nanosecond of the second of the Unix epoch
     */
    val unixEpochNanoOfSecond: Int get() = nanoOfSecondsSinceUnixEpoch.value

    /**
     * The millisecond of the Unix epoch
     */
    val unixEpochMillisecond: Long get() = millisecondsSinceUnixEpoch.value

    /**
     * Check if this time point represent the same instant as [other]. Unlike the equals operator, equality is
     * determined solely by timeline order.
     */
    fun isSameInstantAs(other: TimePoint<*>): Boolean {
        return unixEpochSecond == other.unixEpochSecond && unixEpochNanoOfSecond == other.unixEpochNanoOfSecond
    }

    /**
     * Time points can be compared to other time points based on timeline order, but aren't required to implement the
     * [Comparable] interface since they don't necessarily have a natural order that's consistent with equals.
     */
    operator fun compareTo(other: TimePoint<*>): Int {
        val second = unixEpochSecond
        val otherSecond = other.unixEpochSecond

        val secondDiff = second.compareTo(otherSecond)

        return if (secondDiff != 0) {
            secondDiff
        } else {
            unixEpochNanoOfSecond - other.unixEpochNanoOfSecond
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
         * Compare by timeline order.
         */
        val TIMELINE_ORDER = compareBy<TimePoint<*>> { it.unixEpochSecond }.thenBy { it.unixEpochNanoOfSecond }
    }
}