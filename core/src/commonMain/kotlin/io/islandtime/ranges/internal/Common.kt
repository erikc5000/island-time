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

internal fun throwUnboundedIntervalException(): Nothing {
    throw UnsupportedOperationException(
        "An interval cannot be represented as a period or duration unless it is bounded"
    )
}

internal inline fun <T> TimeInterval<T>.random(
    random: Random,
    secondGetter: (T) -> Long,
    nanosecondGetter: (T) -> Int,
    creator: (second: Long, nanosecond: Int) -> T
): T {
    return when {
        isEmpty() -> throw NoSuchElementException("The interval is empty")
        isBounded() -> generateRandom(random, secondGetter, nanosecondGetter, creator)
        else -> throwUnboundedIntervalException()
    }
}

internal inline fun <T> TimeInterval<T>.randomOrNull(
    random: Random,
    secondGetter: (T) -> Long,
    nanosecondGetter: (T) -> Int,
    creator: (second: Long, nanosecond: Int) -> T
): T? {
    return if (isEmpty() || !isBounded()) {
        null
    } else {
        generateRandom(random, secondGetter, nanosecondGetter, creator)
    }
}

internal inline fun <T : TimePoint<T>> TimePointInterval<T>.random(
    random: Random,
    creator: (second: Long, nanosecond: Int) -> T
): T {
    return random(random, { it.secondOfUnixEpoch }, { it.nanosecond }, creator)
}

internal inline fun <T : TimePoint<T>> TimePointInterval<T>.randomOrNull(
    random: Random,
    creator: (second: Long, nanosecond: Int) -> T
): T? {
    return randomOrNull(random, { it.secondOfUnixEpoch }, { it.nanosecond }, creator)
}

private inline fun <T> TimeInterval<T>.generateRandom(
    random: Random,
    secondGetter: (T) -> Long,
    nanosecondGetter: (T) -> Int,
    creator: (second: Long, nanosecond: Int) -> T
): T {
    val fromSecond = secondGetter(start)
    val fromNanosecond = nanosecondGetter(start)
    val untilSecond = secondGetter(endExclusive)
    val untilNanosecond = nanosecondGetter(endExclusive)

    val randomSecond = generateRandomSecond(random, fromSecond, untilSecond, untilNanosecond)

    val randomNanosecond = generateRandomNanosecond(
        random,
        randomSecond,
        fromSecond,
        fromNanosecond,
        untilSecond,
        untilNanosecond
    )

    return creator(randomSecond, randomNanosecond)
}

private fun generateRandomSecond(
    random: Random,
    fromSecond: Long,
    untilSecond: Long,
    untilNanosecond: Int
): Long {
    return if (fromSecond == untilSecond) {
        fromSecond
    } else {
        random.nextLong(fromSecond, if (untilNanosecond == 0) untilSecond else untilSecond + 1)
    }
}

private fun generateRandomNanosecond(
    random: Random,
    randomSecond: Long,
    fromSecond: Long,
    fromNanosecond: Int,
    untilSecond: Long,
    untilNanosecond: Int
): Int {
    return when {
        fromSecond == untilSecond -> random.nextInt(fromNanosecond, untilNanosecond)
        randomSecond == fromSecond -> random.nextInt(fromNanosecond, NANOSECONDS_PER_SECOND)
        randomSecond == untilSecond -> random.nextInt(0, untilNanosecond)
        else -> random.nextInt(0, NANOSECONDS_PER_SECOND)
    }
}