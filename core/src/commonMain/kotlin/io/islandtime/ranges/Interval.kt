package io.islandtime.ranges

/**
 * A half-open or closed interval.
 */
interface Interval<T> {
    /**
     * The start of the interval, inclusive.
     */
    val start: T

    /**
     * The end of the interval, inclusive.
     */
    val endInclusive: T

    /**
     * The end of the interval, exclusive.
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
     * Checks if both the start and end of this interval are unbounded, meaning this is an infinite time period in both
     * directions.
     */
    fun isUnbounded(): Boolean = hasUnboundedStart() && hasUnboundedEnd()

    /**
     * Checks if this interval contains [value].
     */
    operator fun contains(value: T): Boolean

    /**
     * Checks if this interval is empty.
     */
    fun isEmpty(): Boolean
}

/**
 * Checks if this interval contains [value].
 *
 * This will always return `false` if [value] is `null`.
 */
fun <T> Interval<T>.contains(value: T?): Boolean = value != null && contains(value)
