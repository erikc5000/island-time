package io.islandtime.ranges

/**
 * A half-open time interval.
 */
interface TimeInterval<T> {
    /**
     * The start of the interval, inclusive.
     */
    val start: T

    /**
     * The end of the interval, exclusive.
     */
    val endExclusive: T

    /**
     * Check if the interval's start is unbounded. In ISO-8601 terminology, this is an "open" start.
     */
    fun hasUnboundedStart(): Boolean

    /**
     * Check if the interval's end is unbounded. In ISO-8601 terminology, this is an "open" end.
     */
    fun hasUnboundedEnd(): Boolean

    /**
     * Check if the interval's start is bounded, meaning it has a finite value.
     */
    fun hasBoundedStart(): Boolean = !hasUnboundedStart()

    /**
     * Check if the interval's end is bounded, meaning it has a finite value.
     */
    fun hasBoundedEnd(): Boolean = !hasUnboundedEnd()

    /**
     * Check if both the start and end of the interval are bounded, meaning it has a finite range.
     */
    fun isBounded(): Boolean = hasBoundedStart() && hasBoundedEnd()

    /**
     * Check if both the start and end of the interval are unbounded, meaning this is an infinite time period in both
     * directions.
     */
    fun isUnbounded(): Boolean = hasUnboundedStart() && hasUnboundedEnd()

    /**
     * Check if [value] is within the interval based on timeline order.
     */
    operator fun contains(value: T): Boolean

    /**
     * Check if the interval is empty.
     */
    fun isEmpty(): Boolean
}
