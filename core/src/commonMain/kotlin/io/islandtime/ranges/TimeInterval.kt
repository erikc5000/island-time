package io.islandtime.ranges

/**
 * A half-open time interval.
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