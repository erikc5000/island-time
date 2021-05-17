package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateTimeField
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.*

/**
 * An interval between two date-times, assumed to be at the same offset from UTC.
 *
 * [DateTime.MIN] and [DateTime.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end.
 */
class DateTimeInterval(
    override val start: DateTime = UNBOUNDED.start,
    override val endExclusive: DateTime = UNBOUNDED.endExclusive
) : Interval<DateTime> {

    override val endInclusive: DateTime
        get() = if (hasUnboundedEnd()) endExclusive else endExclusive - 1.nanoseconds

    override fun hasUnboundedStart(): Boolean = start == DateTime.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive == DateTime.MAX

    /**
     * Checks if this interval contains [value].
     * @param value a date-time, assumed to be in the same time zone
     */
    override fun contains(value: DateTime): Boolean {
        return value >= start && (value < endExclusive || hasUnboundedEnd())
    }

    override fun isEmpty(): Boolean {
        return start >= endExclusive
    }

    override fun equals(other: Any?): Boolean {
        return other is DateTimeInterval && (isEmpty() && other.isEmpty() ||
            start == other.start && endExclusive == other.endExclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else (31 * start.hashCode() + endExclusive.hashCode())
    }

    /**
     * Converts this interval to a string in ISO-8601 extended format.
     */
    override fun toString(): String = buildIsoString(
        maxElementSize = MAX_DATE_TIME_STRING_LENGTH,
        inclusive = false,
        appendFunction = StringBuilder::appendDateTime
    )

    /**
     * Converts this interval to the [Duration] between the start and end date-time, assuming they're in the same time
     * zone. In general, it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight
     * savings rules won't be taken into account when working with [DateTime] directly.
     *
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    fun asDuration(): Duration {
        return when {
            isEmpty() -> Duration.ZERO
            isBounded() -> durationBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Converts this interval into a [Period] of the same length.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    fun asPeriod(): Period {
        return when {
            isEmpty() -> Period.ZERO
            isBounded() -> periodBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * The number of whole years in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInYears: Years
        get() = when {
            isEmpty() -> 0.years
            isBounded() -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole months in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMonths: Months
        get() = when {
            isEmpty() -> 0.months
            isBounded() -> monthsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole weeks in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInWeeks: Weeks
        get() = when {
            isEmpty() -> 0L.weeks
            isBounded() -> weeksBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole days in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInDays: Days
        get() = when {
            isEmpty() -> 0L.days
            isBounded() -> daysBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole hours in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInHours: Hours
        get() = when {
            isEmpty() -> 0L.hours
            isBounded() -> hoursBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole minutes in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMinutes: Minutes
        get() = when {
            isEmpty() -> 0L.minutes
            isBounded() -> minutesBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole seconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInSeconds: Seconds
        get() = when {
            isEmpty() -> 0L.seconds
            isBounded() -> secondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole milliseconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMilliseconds: Milliseconds
        get() = when {
            isEmpty() -> 0L.milliseconds
            isBounded() -> millisecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of whole microseconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMicroseconds: Microseconds
        get() = when {
            isEmpty() -> 0L.microseconds
            isBounded() -> microsecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * The number of nanoseconds in this interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInNanoseconds: Nanoseconds
        get() = when {
            isEmpty() -> 0L.nanoseconds
            isBounded() -> nanosecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    companion object {
        /**
         * An empty interval.
         */
        val EMPTY = DateTimeInterval(
            DateTime.fromSecondOfUnixEpoch(0L, 0, UtcOffset.ZERO),
            DateTime.fromSecondOfUnixEpoch(0L, 0, UtcOffset.ZERO)
        )

        /**
         * An unbounded (ie. infinite) interval.
         */
        val UNBOUNDED = DateTimeInterval(DateTime.MIN, DateTime.MAX)

        internal fun withInclusiveEnd(start: DateTime, endInclusive: DateTime): DateTimeInterval {
            val endExclusive = when {
                endInclusive == DateTime.MAX -> endInclusive
                endInclusive > MAX_INCLUSIVE_END_DATE_TIME ->
                    throw DateTimeException("The end of the interval can't be represented")
                else -> endInclusive + 1.nanoseconds
            }

            return DateTimeInterval(start, endExclusive)
        }
    }
}

/**
 * Converts a string to a [DateTimeInterval].
 *
 * The string is assumed to be an ISO-8601 time interval representation in extended format. The output of
 * [DateTimeInterval.toString] can be safely parsed using this method.
 *
 * Examples:
 * - `1990-01-04T03/1991-08-30T15:30:05.123`
 * - `../1991-08-30T15:30:05.123`
 * - `1990-01-04T03/..`
 * - `../..`
 * - (empty string)
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toDateTimeInterval(): DateTimeInterval = toDateTimeInterval(DateTimeParsers.Iso.Extended.DATE_TIME_INTERVAL)

/**
 * Converts a string to a [DateTimeInterval] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed interval is invalid
 */
fun String.toDateTimeInterval(
    parser: GroupedDateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): DateTimeInterval {
    val results = parser.parse(this, settings).expectingGroupCount<DateTimeInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0].fields[DateTimeField.IS_UNBOUNDED] == 1L -> DateTimeInterval.UNBOUNDED.start
        else -> results[0].toDateTime() ?: throwParserFieldResolutionException<DateTimeInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1].fields[DateTimeField.IS_UNBOUNDED] == 1L -> DateTimeInterval.UNBOUNDED.endExclusive
        else -> results[1].toDateTime() ?: throwParserFieldResolutionException<DateTimeInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> DateTimeInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Creates a [DateTimeInterval] from this date-time up to, but not including the nanosecond represented by [to].
 */
infix fun DateTime.until(to: DateTime): DateTimeInterval = DateTimeInterval(this, to)

/**
 * Gets the [Period] between two date-times, assuming they're in the same time zone.
 */
fun periodBetween(start: DateTime, endExclusive: DateTime): Period {
    return periodBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Gets the number of whole years between two date-times, assuming they're in the same time zone.
 */
fun yearsBetween(start: DateTime, endExclusive: DateTime): Years {
    return yearsBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Gets the number of whole months between two date-times, assuming they're in the same time zone.
 */
fun monthsBetween(start: DateTime, endExclusive: DateTime): Months {
    return monthsBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Gets the number whole weeks between two date-times, assuming they're in the same time zone.
 */
fun weeksBetween(start: DateTime, endExclusive: DateTime): Weeks {
    return daysBetween(start, endExclusive).inWholeWeeks
}

/**
 * Gets the number whole days between two date-times, assuming they're in the same time zone.
 */
fun daysBetween(start: DateTime, endExclusive: DateTime): Days {
    return secondsBetween(start, endExclusive).inWholeDays
}

/**
 * Gets the [Duration] between two date-times, assuming they have the same UTC offset. In general, it's more appropriate
 * to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken into account
 * when working with [DateTime] directly.
 */
fun durationBetween(start: DateTime, endExclusive: DateTime): Duration {
    val secondDiff = endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO) - start.secondOfUnixEpochAt(UtcOffset.ZERO)
    val nanoDiff = endExclusive.nanosecond - start.nanosecond
    return durationOf(secondDiff.seconds, nanoDiff.nanoseconds)
}

/**
 * Gets the number of whole hours between two date-times, assuming they have the same UTC offset. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 */
fun hoursBetween(start: DateTime, endExclusive: DateTime): Hours {
    return secondsBetween(start, endExclusive).inWholeHours
}

/**
 * Gets the number of whole minutes between two date-times, assuming they have the same UTC offset. In general, it's
 * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 */
fun minutesBetween(start: DateTime, endExclusive: DateTime): Minutes {
    return secondsBetween(start, endExclusive).inWholeMinutes
}

/**
 * Gets the number of whole seconds between two date-times, assuming they have the same UTC offset. In general, it's
 * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun secondsBetween(start: DateTime, endExclusive: DateTime): Seconds {
    return secondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

/**
 * Gets the number of whole milliseconds between two date-times, assuming they have the same UTC offset. In general,
 * it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun millisecondsBetween(start: DateTime, endExclusive: DateTime): Milliseconds {
    return millisecondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

/**
 * Gets the number of whole microseconds between two date-times, assuming they have the same UTC offset. In general,
 * it's more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 *
 *  @throws ArithmeticException if the result overflows
 */
fun microsecondsBetween(start: DateTime, endExclusive: DateTime): Microseconds {
    return microsecondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

/**
 * Gets the number of nanoseconds between two date-times, assuming they have the same UTC offset. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun nanosecondsBetween(start: DateTime, endExclusive: DateTime): Nanoseconds {
    return nanosecondsBetween(
        start.secondOfUnixEpochAt(UtcOffset.ZERO),
        start.nanosecond,
        endExclusive.secondOfUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanosecond
    )
}

internal fun adjustedEndDate(start: DateTime, endExclusive: DateTime): Date {
    return when {
        endExclusive.date > start.date && endExclusive.time < start.time -> endExclusive.date - 1.days
        endExclusive.date < start.date && endExclusive.time > start.time -> endExclusive.date + 1.days
        else -> endExclusive.date
    }
}
