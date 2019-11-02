package io.islandtime.ranges.internal

import io.islandtime.measures.*
import io.islandtime.measures.minusExact
import io.islandtime.measures.minusWithOverflow
import io.islandtime.measures.plusExact
import io.islandtime.ranges.TimeInterval

internal fun secondsBetween(
    startSeconds: LongSeconds,
    startNanoseconds: IntNanoseconds,
    endExclusiveSeconds: LongSeconds,
    endExclusiveNanoseconds: IntNanoseconds
): LongSeconds {
    val secondDiff = endExclusiveSeconds minusExact startSeconds
    val nanoDiff = endExclusiveNanoseconds minusWithOverflow startNanoseconds

    return when {
        secondDiff.value > 0 && nanoDiff.value < 0 -> secondDiff - 1.seconds
        secondDiff.value < 0 && nanoDiff.value > 0 -> secondDiff + 1.seconds
        else -> secondDiff
    }
}

internal fun millisecondsBetween(
    startSeconds: LongSeconds,
    startNanoseconds: IntNanoseconds,
    endExclusiveSeconds: LongSeconds,
    endExclusiveNanoseconds: IntNanoseconds
): LongMilliseconds {
    return (endExclusiveSeconds minusExact startSeconds).inMillisecondsExact() plusExact
        (endExclusiveNanoseconds - startNanoseconds).inMilliseconds
}

/**
 * Get the number of whole microseconds between two instants
 *  @throws ArithmeticException if the result overflows
 */
internal fun microsecondsBetween(
    startSeconds: LongSeconds,
    startNanoseconds: IntNanoseconds,
    endExclusiveSeconds: LongSeconds,
    endExclusiveNanoseconds: IntNanoseconds
): LongMicroseconds {
    return (endExclusiveSeconds minusExact startSeconds).inMicrosecondsExact() plusExact
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
    return (endExclusiveSeconds minusExact startSeconds).inNanosecondsExact() plusExact
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
            if (hasBoundedStart) {
                appendFunction(start)
            } else {
                append("..")
            }

            append('/')

            if (hasBoundedEnd) {
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