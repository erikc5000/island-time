package io.islandtime.ranges.internal

import io.islandtime.DateTime
import io.islandtime.measures.*
import io.islandtime.ranges.Interval

internal val MAX_INCLUSIVE_END_DATE_TIME: DateTime = DateTime.MAX - 2.nanoseconds

internal fun secondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Seconds {
    val secondDiff = endExclusiveSecond - startSecond
    val nanoDiff = endExclusiveNanosecond - startNanosecond

    return when {
        secondDiff > 0 && nanoDiff < 0 -> secondDiff - 1
        secondDiff < 0 && nanoDiff > 0 -> secondDiff + 1
        else -> secondDiff
    }.seconds
}

internal fun millisecondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Milliseconds {
    return (endExclusiveSecond - startSecond).seconds +
        (endExclusiveNanosecond - startNanosecond).nanoseconds.inWholeMilliseconds
}

internal fun microsecondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Microseconds {
    return (endExclusiveSecond - startSecond).seconds +
        (endExclusiveNanosecond - startNanosecond).nanoseconds.inWholeMicroseconds
}

internal fun nanosecondsBetween(
    startSecond: Long,
    startNanosecond: Int,
    endExclusiveSecond: Long,
    endExclusiveNanosecond: Int
): Nanoseconds {
    return (endExclusiveSecond - startSecond).seconds + (endExclusiveNanosecond - startNanosecond).nanoseconds
}

internal inline fun <T> Interval<T>.buildIsoString(
    maxElementSize: Int,
    inclusive: Boolean,
    appendFunction: StringBuilder.(T) -> StringBuilder
): String {
    return if (isEmpty()) {
        ""
    } else {
        buildString(2 * maxElementSize + 1) {
            if (hasBoundedStart()) {
                appendFunction(start)
            } else {
                append("..")
            }

            append('/')

            if (hasBoundedEnd()) {
                appendFunction(if (inclusive) endInclusive else endExclusive)
            } else {
                append("..")
            }
        }
    }
}

internal fun throwUnboundedIntervalException(): Nothing {
    throw UnsupportedOperationException(
        "An interval cannot be represented as a period or duration unless it is bounded"
    )
}
