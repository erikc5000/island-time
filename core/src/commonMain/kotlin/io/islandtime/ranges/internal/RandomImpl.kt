package io.islandtime.ranges.internal

import io.islandtime.*
import io.islandtime.TimePoint
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.ranges.*
import kotlin.random.Random

internal fun DateRange.randomImpl(random: Random): Date {
    if (!isBounded()) throwUnboundedIntervalException()

    return try {
        Date.fromDayOfUnixEpoch(random.nextLong(start.dayOfUnixEpoch, endInclusive.dayOfUnixEpoch + 1))
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

internal fun DateRange.randomOrNullImpl(random: Random): Date? {
    return if (isEmpty() || !isBounded()) {
        null
    } else {
        Date.fromDayOfUnixEpoch(random.nextLong(start.dayOfUnixEpoch, endInclusive.dayOfUnixEpoch + 1))
    }
}

internal fun DateTimeInterval.randomImpl(random: Random): DateTime {
    return randomImpl(
        random,
        secondGetter = { it.secondOfUnixEpochAt(UtcOffset.ZERO) },
        nanosecondGetter = { it.nanosecond },
        creator = { second, nanosecond -> DateTime.fromSecondOfUnixEpoch(second, nanosecond, UtcOffset.ZERO) }
    )
}

internal fun DateTimeInterval.randomOrNullImpl(random: Random): DateTime? {
    return randomOrNullImpl(
        random,
        secondGetter = { it.secondOfUnixEpochAt(UtcOffset.ZERO) },
        nanosecondGetter = { it.nanosecond },
        creator = { second, nanosecond -> DateTime.fromSecondOfUnixEpoch(second, nanosecond, UtcOffset.ZERO) }
    )
}

internal fun OffsetDateTimeInterval.randomImpl(random: Random): OffsetDateTime {
    return randomImpl(random) { second, nanosecond ->
        OffsetDateTime.fromSecondOfUnixEpoch(second, nanosecond, start.offset)
    }
}

internal fun OffsetDateTimeInterval.randomOrNullImpl(random: Random): OffsetDateTime? {
    return randomOrNullImpl(random) { second, nanosecond ->
        OffsetDateTime.fromSecondOfUnixEpoch(second, nanosecond, start.offset)
    }
}

internal fun ZonedDateTimeInterval.randomImpl(random: Random): ZonedDateTime {
    return randomImpl(random) { second, nanosecond ->
        ZonedDateTime.fromSecondOfUnixEpoch(second, nanosecond, start.zone)
    }
}

internal fun ZonedDateTimeInterval.randomOrNullImpl(random: Random): ZonedDateTime? {
    return randomOrNullImpl(random) { second, nanosecond ->
        ZonedDateTime.fromSecondOfUnixEpoch(second, nanosecond, start.zone)
    }
}

internal fun InstantInterval.randomImpl(random: Random): Instant {
    return randomImpl(random, Instant.Companion::fromSecondOfUnixEpoch)
}

internal fun InstantInterval.randomOrNullImpl(random: Random): Instant? {
    return randomOrNullImpl(random, Instant.Companion::fromSecondOfUnixEpoch)
}

private inline fun <T> Interval<T>.randomImpl(
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

private inline fun <T> Interval<T>.randomOrNullImpl(
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

private inline fun <T : TimePoint<T>> TimePointInterval<T>.randomImpl(
    random: Random,
    creator: (second: Long, nanosecond: Int) -> T
): T {
    return randomImpl(random, { it.secondOfUnixEpoch }, { it.nanosecond }, creator)
}

private inline fun <T : TimePoint<T>> TimePointInterval<T>.randomOrNullImpl(
    random: Random,
    creator: (second: Long, nanosecond: Int) -> T
): T? {
    return randomOrNullImpl(random, { it.secondOfUnixEpoch }, { it.nanosecond }, creator)
}

private inline fun <T> Interval<T>.generateRandom(
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
