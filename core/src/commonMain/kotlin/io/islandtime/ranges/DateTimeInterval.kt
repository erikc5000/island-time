package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.MAX_DATE_TIME_STRING_LENGTH
import io.islandtime.interval.*

/**
 * An interval between two date-times, assumed to be in the same time zone
 */
class DateTimeInterval(
    override val start: DateTime = UNBOUNDED.start,
    override val endExclusive: DateTime = UNBOUNDED.endExclusive
) : TimeInterval<DateTime> {

    override val hasUnboundedStart: Boolean get() = start == DateTime.MIN
    override val hasUnboundedEnd: Boolean get() = endExclusive == DateTime.MAX

    /**
     * Check if this interval contains the given value
     * @param value a date-time, assumed to be in the same time zone
     */
    override fun contains(value: DateTime): Boolean {
        return value >= start && (value < endExclusive || hasUnboundedEnd)
    }

    override fun isEmpty(): Boolean {
        return start >= endExclusive
    }

    override fun toString() = buildIsoString(MAX_DATE_TIME_STRING_LENGTH, StringBuilder::appendDateTime)

    /**
     * Get the [Duration] between the start and end date-time, assuming they're in the same time zone. In general, it's
     * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
     * taken into account when working with [DateTime] directly.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    fun asDuration(): Duration {
        return when {
            isEmpty() -> Duration.ZERO
            isBounded -> durationBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the period between the date-times in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    fun asPeriod(): Period {
        return when {
            isEmpty() -> Period.ZERO
            isBounded -> periodBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of whole years in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val years
        get() = when {
            isEmpty() -> 0.years
            isBounded -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole months in the interval.
     *
     * A month is considered to have passed if the day of the end month is greater than or equal to the day of the start
     * month and the time of day of the end date-time is greater than or equal to that of the start date-time.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val months
        get() = when {
            isEmpty() -> 0.months
            isBounded -> monthsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole days in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val days: LongDays
        get() = when {
            isEmpty() -> 0L.days
            isBounded -> daysBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole hours in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val hours: LongHours
        get() = when {
            isEmpty() -> 0L.hours
            isBounded -> hoursBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole minutes in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val minutes: LongMinutes
        get() = when {
            isEmpty() -> 0L.minutes
            isBounded -> minutesBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole seconds in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val seconds: LongSeconds
        get() = when {
            isEmpty() -> 0L.seconds
            isBounded -> secondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole milliseconds in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val milliseconds: LongMilliseconds
        get() = when {
            isEmpty() -> 0L.milliseconds
            isBounded -> millisecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole microseconds in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val microseconds: LongMicroseconds
        get() = when {
            isEmpty() -> 0L.microseconds
            isBounded -> microsecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of nanoseconds in the interval
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val nanoseconds: LongNanoseconds
        get() = when {
            isEmpty() -> 0L.nanoseconds
            isBounded -> nanosecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    companion object {
        /**
         * An empty interval
         */
        val EMPTY = DateTimeInterval(
            DateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO),
            DateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO)
        )

        /**
         * An unbounded (ie. infinite) interval
         */
        val UNBOUNDED = DateTimeInterval(DateTime.MIN, DateTime.MAX)

        fun withInclusiveEnd(start: DateTime, endInclusive: DateTime): DateTimeInterval {
            val endExclusive = if (endInclusive == DateTime.MAX) {
                endInclusive
            } else {
                endInclusive + 1.nanoseconds
            }

            return DateTimeInterval(start, endExclusive)
        }
    }
}

/**
 * Get the [Period] between two date-times, assuming they're in the same time zone
 */
fun periodBetween(start: DateTime, endExclusive: DateTime): Period {
    return periodBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Get the number of whole years between two date-times, assuming they're in the same time zone
 */
fun yearsBetween(start: DateTime, endExclusive: DateTime): IntYears {
    return yearsBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Get the number of whole months between two date-times, assuming they're in the same time zone
 */
fun monthsBetween(start: DateTime, endExclusive: DateTime): IntMonths {
    return monthsBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Get the number whole days between two date-times, assuming they're in the same time zone.
 */
fun daysBetween(start: DateTime, endExclusive: DateTime): LongDays {
    return secondsBetween(start, endExclusive).inWholeDays
}

/**
 * Get the [Duration] between two date-times, assuming they're in the same time zone. In general, it's more appropriate
 * to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken into account
 * when working with [DateTime] directly.
 */
fun durationBetween(start: DateTime, endExclusive: DateTime): Duration {
    val secondDiff = endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO) minusExact
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO)

    val nanoDiff = endExclusive.nanosecond.nanoseconds minusWithOverflow start.nanosecond.nanoseconds
    return durationOf(secondDiff, nanoDiff)
}

/**
 * Get the number of whole hours between two date-times, assuming they're in the same time zone. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 */
fun hoursBetween(start: DateTime, endExclusive: DateTime): LongHours {
    return secondsBetween(start, endExclusive).inWholeHours
}

/**
 * Get the number of whole minutes between two date-times, assuming they're in the same time zone. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 */
fun minutesBetween(start: DateTime, endExclusive: DateTime): LongMinutes {
    return secondsBetween(start, endExclusive).inWholeMinutes
}

/**
 * Get the number of whole seconds between two date-times, assuming they're in the same time zone. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 * @throws ArithmeticException if the result overflows
 */
fun secondsBetween(start: DateTime, endExclusive: DateTime): LongSeconds {
    return secondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond.nanoseconds,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond.nanoseconds
    )
}

/**
 * Get the number of whole milliseconds between two date-times, assuming they're in the same time zone. In general, it's
 * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 * @throws ArithmeticException if the result overflows
 */
fun millisecondsBetween(start: DateTime, endExclusive: DateTime): LongMilliseconds {
    return millisecondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond.nanoseconds,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond.nanoseconds
    )
}

/**
 * Get the number of whole microseconds between two date-times, assuming they're in the same time zone. In general, it's
 * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 *  @throws ArithmeticException if the result overflows
 */
fun microsecondsBetween(start: DateTime, endExclusive: DateTime): LongMicroseconds {
    return microsecondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond.nanoseconds,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond.nanoseconds
    )
}

/**
 * Get the number of nanoseconds between two date-times, assuming they're in the same time zone. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 * @throws ArithmeticException if the result overflows
 */
fun nanosecondsBetween(start: DateTime, endExclusive: DateTime): LongNanoseconds {
    return nanosecondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond.nanoseconds,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond.nanoseconds
    )
}

internal fun adjustedEndDate(start: DateTime, endExclusive: DateTime): Date {
    return when {
        endExclusive.date > start.date && endExclusive.time < start.time -> endExclusive.date - 1.days
        endExclusive.date < start.date && endExclusive.time > start.time -> endExclusive.date + 1.days
        else -> endExclusive.date
    }
}