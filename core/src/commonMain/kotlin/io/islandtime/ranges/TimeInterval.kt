package io.islandtime.ranges

/**
 * A half-open time interval.
 */
interface TimeInterval<T> {
    /**
     * The start of this interval, inclusive.
     */
    val start: T

    /**
     * The end of this interval, exclusive.
     */
    val endExclusive: T

    /**
     * Checks if this interval's start is unbounded. In ISO-8601 terminology, this is an "open" start.
     */
    fun hasUnboundedStart(): Boolean

    /**
     * Checks if this interval's end is unbounded. In ISO-8601 terminology, this is an "open" end.
     */
    fun hasUnboundedEnd(): Boolean

    /**
     * Checks if this interval's start is bounded, meaning it has a finite value.
     */
    fun hasBoundedStart(): Boolean = !hasUnboundedStart()

    /**
     * Checks if this interval's end is bounded, meaning it has a finite value.
     */
    fun hasBoundedEnd(): Boolean = !hasUnboundedEnd()

    /**
     * Checks if both the start and end of this interval are bounded, meaning it has a finite range.
     */
    fun isBounded(): Boolean = hasBoundedStart() && hasBoundedEnd()

    /**
     * Checks if both the start and end of the interval are unbounded, meaning this is an infinite time period in both
     * directions.
     */
    fun isUnbounded(): Boolean = hasUnboundedStart() && hasUnboundedEnd()

    /**
     * Checks if [value] is within this interval based on timeline order.
     */
    operator fun contains(value: T): Boolean

    /**
     * Checks if this interval is empty.
     */
    fun isEmpty(): Boolean
}
