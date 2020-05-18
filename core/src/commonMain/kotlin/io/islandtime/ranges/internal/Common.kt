package io.islandtime.ranges.internal

import io.islandtime.DateTime
import io.islandtime.base.TimePoint
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.*
import io.islandtime.measures.internal.minusWithOverflow
import io.islandtime.ranges.TimeInterval
import io.islandtime.ranges.TimePointInterval
import kotlin.random.Random

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

internal inline fun <T> TimeInterval<T>.randomInternal(
    random: Random,
    secondGetter: (T) -> Long,
    nanosecondGetter: (T) -> Int,
    creator: (second: Long, nanosecond: Int) -> T
): T {
    val fromSecond = secondGetter(start)
    val fromNanosecond = nanosecondGetter(start)
    val untilSecond = secondGetter(endExclusive)
    val untilNanosecond = nanosecondGetter(endExclusive)

    val second =
        if (fromSecond == untilSecond) {
            fromSecond
        } else {
            random.nextLong(fromSecond, if (untilNanosecond == 0) untilSecond else untilSecond + 1)
        }

    val nanosecond = when {
        fromSecond == untilSecond -> random.nextInt(fromNanosecond, untilNanosecond)
        second == fromSecond -> random.nextInt(fromNanosecond, NANOSECONDS_PER_SECOND)
        second == untilSecond -> random.nextInt(0, untilNanosecond)
        else -> random.nextInt(0, NANOSECONDS_PER_SECOND)
    }

    return creator(second, nanosecond)
}

internal inline fun <T : TimePoint<T>> TimePointInterval<T>.randomInternal(
    random: Random,
    creator: (second: Long, nanosecond: Int) -> T
): T = randomInternal(random, { it.unixEpochSecond }, { it.unixEpochNanoOfSecond }, creator)

internal fun throwUnboundedIntervalException(): Nothing {
    throw UnsupportedOperationException(
        "An interval cannot be represented as a period or duration unless it is bounded"
    )
}