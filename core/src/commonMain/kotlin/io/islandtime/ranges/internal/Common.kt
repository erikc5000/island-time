package io.islandtime.ranges.internal

import io.islandtime.DateTime
import io.islandtime.measures.*
import io.islandtime.measures.internal.minusWithOverflow
import io.islandtime.ranges.TimeInterval

internal val MAX_INCLUSIVE_END_DATE_TIME = DateTime.MAX - 2.nanoseconds

internal fun secondsBetween(
    startSeconds: LongSeconds,
    startNanoseconds: IntNanoseconds,
    endExclusiveSeconds: LongSeconds,
    endExclusiveNanoseconds: IntNanoseconds
): LongSeconds {
    val secondDiff = endExclusiveSeconds - startSeconds
    val nanoDiff = endExclusiveNanoseconds minusWithOverflow startNanoseconds

    return when {
        secondDiff.value > 0 && nanoDiff.value < 0 -> secondDiff - 1.seconds
        secondDiff.value < 0 && nanoDiff.value > 0 -> secondDiff + 1.seconds
        else -> secondDiff
    }
}

/**
 * Get the number of whole milliseconds between two instants
 * @throws ArithmeticException if the result overflows
 */
internal fun millisecondsBetween(
    startSeconds: LongSeconds,
    startNanoseconds: IntNanoseconds,
    endExclusiveSeconds: LongSeconds,
    endExclusiveNanoseconds: IntNanoseconds
): LongMilliseconds {
    return (endExclusiveSeconds - startSeconds).inMilliseconds +
        (endExclusiveNanoseconds - startNanoseconds).inMilliseconds
}

/**
 * Get the number of whole microseconds between two instants
 * @throws ArithmeticException if the result overflows
 */
internal fun microsecondsBetween(
    startSeconds: LongSeconds,
    startNanoseconds: IntNanoseconds,
    endExclusiveSeconds: LongSeconds,
    endExclusiveNanoseconds: IntNanoseconds
): LongMicroseconds {
    return (endExclusiveSeconds - startSeconds).inMicroseconds +
        (endExclusiveNanoseconds - startNanoseconds).inMicroseconds
}

/**
 * Get the number of nanoseconds between two instants
 * @throws ArithmeticException if the result overflows
 */
internal fun nanosecondsBetween(
    startSeconds: LongSeconds,
    startNanoseconds: IntNanoseconds,
    endExclusiveSeconds: LongSeconds,
    endExclusiveNanoseconds: IntNanoseconds
): LongNanoseconds {
    return (endExclusiveSeconds - startSeconds).inNanoseconds +
        (endExclusiveNanoseconds - startNanoseconds)
}

internal inline fun <T> TimeInterval<T>.buildIsoString(
    baseCapacity: Int,
    appendFunction: StringBuilder.(T) -> StringBuilder
): String {
    return if (isEmpty()) {
        ""
    } else {
        buildString(2 * baseCapacity + 1) {
            if (hasBoundedStart()) {
                appendFunction(start)
            } else {
                append("..")
            }

            append('/')

            if (hasBoundedEnd()) {
                appendFunction(endExclusive)
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