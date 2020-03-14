package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.MAX_DATE_TIME_STRING_LENGTH
import io.islandtime.base.DateProperty
import io.islandtime.measures.*
import io.islandtime.measures.internal.minusWithOverflow
import io.islandtime.parser.*
import io.islandtime.ranges.internal.*
import kotlin.random.Random

/**
 * An interval between two arbitrary date-times.
 *
 * As no UTC offset or time zone is associated with either date-time, it's up to the application to interpret the
 * meaning.
 *
 * [DateTime.MIN] and [DateTime.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end.
 */
class DateTimeInterval(
    override val start: DateTime = UNBOUNDED.start,
    override val endExclusive: DateTime = UNBOUNDED.endExclusive
) : TimeInterval<DateTime> {

    override fun hasUnboundedStart(): Boolean = start == DateTime.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive == DateTime.MAX

    /**
     * Check if this interval contains the given value.
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
     * Convert this interval to a string in ISO-8601 extended format.
     */
    override fun toString() = buildIsoString(MAX_DATE_TIME_STRING_LENGTH, StringBuilder::appendDateTime)

    /**
     * Get the [Duration] between the start and end date-time, assuming they're in the same time zone. In general, it's
     * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
     * taken into account when working with [DateTime] directly.
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
     * Convert the interval into a [Period] of the same length.
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
     * Get the number of whole years in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInYears
        get() = when {
            isEmpty() -> 0.years
            isBounded() -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole months in the interval.
     *
     * A month is considered to have passed if the day of the end month is greater than or equal to the day of the start
     * month and the time of day of the end date-time is greater than or equal to that of the start date-time.
     *
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMonths
        get() = when {
            isEmpty() -> 0.months
            isBounded() -> monthsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole weeks in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInWeeks: LongWeeks
        get() = when {
            isEmpty() -> 0L.weeks
            isBounded() -> weeksBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole days in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInDays: LongDays
        get() = when {
            isEmpty() -> 0L.days
            isBounded() -> daysBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole hours in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInHours: LongHours
        get() = when {
            isEmpty() -> 0L.hours
            isBounded() -> hoursBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole minutes in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMinutes: LongMinutes
        get() = when {
            isEmpty() -> 0L.minutes
            isBounded() -> minutesBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole seconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInSeconds: LongSeconds
        get() = when {
            isEmpty() -> 0L.seconds
            isBounded() -> secondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole milliseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMilliseconds: LongMilliseconds
        get() = when {
            isEmpty() -> 0L.milliseconds
            isBounded() -> millisecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole microseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInMicroseconds: LongMicroseconds
        get() = when {
            isEmpty() -> 0L.microseconds
            isBounded() -> microsecondsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of nanoseconds in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInNanoseconds: LongNanoseconds
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
            DateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO),
            DateTime.fromUnixEpochSecond(0L, 0, UtcOffset.ZERO)
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
 * Convert a string to a [DateTimeInterval].
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
fun String.toDateTimeInterval() = toDateTimeInterval(DateTimeParsers.Iso.Extended.DATE_TIME_INTERVAL)

/**
 * Convert a string to a [DateTimeInterval] using a specific parser.
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
        results[0][DateProperty.IsFarPast] == true -> DateTimeInterval.UNBOUNDED.start
        else -> results[0].toDateTime() ?: throwParserPropertyResolutionException<DateTimeInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1][DateProperty.IsFarFuture] == true -> DateTimeInterval.UNBOUNDED.endExclusive
        else -> results[1].toDateTime() ?: throwParserPropertyResolutionException<DateTimeInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> DateTimeInterval.EMPTY
        else -> throw DateTimeParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Return a random date-time within the interval using the default random number generator.
 */
fun DateTimeInterval.random(): DateTime = random(Random)

/**
 * Return a random date-time within the interval using the supplied random number generator.
 */
fun DateTimeInterval.random(random: Random): DateTime {
    try {
        return DateTime.fromUnixEpochSecond(
            random.nextLong(start.unixEpochSecondAt(UtcOffset.ZERO), endExclusive.unixEpochSecondAt(UtcOffset.ZERO)),
            random.nextInt(start.unixEpochNanoOfSecond, endExclusive.unixEpochNanoOfSecond),
            UtcOffset.ZERO
        )
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

/**
 * Get an interval containing all of the date-times up to, but not including the nanosecond represented by [to].
 */
infix fun DateTime.until(to: DateTime) = DateTimeInterval(this, to)

/**
 * Get the [Period] between two date-times, assuming they're in the same time zone.
 */
fun periodBetween(start: DateTime, endExclusive: DateTime): Period {
    return periodBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Get the number of whole years between two date-times, assuming they're in the same time zone.
 */
fun yearsBetween(start: DateTime, endExclusive: DateTime): IntYears {
    return yearsBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Get the number of whole months between two date-times, assuming they're in the same time zone.
 */
fun monthsBetween(start: DateTime, endExclusive: DateTime): IntMonths {
    return monthsBetween(start.date, adjustedEndDate(start, endExclusive))
}

/**
 * Get the number whole weeks between two date-times, assuming they're in the same time zone.
 */
fun weeksBetween(start: DateTime, endExclusive: DateTime): LongWeeks {
    return daysBetween(start, endExclusive).inWeeks
}

/**
 * Get the number whole days between two date-times, assuming they're in the same time zone.
 */
fun daysBetween(start: DateTime, endExclusive: DateTime): LongDays {
    return secondsBetween(start, endExclusive).inDays
}

/**
 * Get the [Duration] between two date-times, assuming they have the same UTC offset. In general, it's more appropriate
 * to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken into account
 * when working with [DateTime] directly.
 */
fun durationBetween(start: DateTime, endExclusive: DateTime): Duration {
    val secondDiff = endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO) -
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO)

    val nanoDiff = endExclusive.nanoOfSecondsSinceUnixEpoch minusWithOverflow start.nanoOfSecondsSinceUnixEpoch
    return durationOf(secondDiff, nanoDiff)
}

/**
 * Get the number of whole hours between two date-times, assuming they have the same UTC offset. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 */
fun hoursBetween(start: DateTime, endExclusive: DateTime): LongHours {
    return secondsBetween(start, endExclusive).inHours
}

/**
 * Get the number of whole minutes between two date-times, assuming they have the same UTC offset. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 */
fun minutesBetween(start: DateTime, endExclusive: DateTime): LongMinutes {
    return secondsBetween(start, endExclusive).inMinutes
}

/**
 * Get the number of whole seconds between two date-times, assuming they have the same UTC offset. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun secondsBetween(start: DateTime, endExclusive: DateTime): LongSeconds {
    return secondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}

/**
 * Get the number of whole milliseconds between two date-times, assuming they have the same UTC offset. In general, it's
 * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun millisecondsBetween(start: DateTime, endExclusive: DateTime): LongMilliseconds {
    return millisecondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}

/**
 * Get the number of whole microseconds between two date-times, assuming they have the same UTC offset. In general, it's
 * more appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be
 * taken into account when working with [DateTime] directly.
 *
 *  @throws ArithmeticException if the result overflows
 */
fun microsecondsBetween(start: DateTime, endExclusive: DateTime): LongMicroseconds {
    return microsecondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}

/**
 * Get the number of nanoseconds between two date-times, assuming they have the same UTC offset. In general, it's more
 * appropriate to calculate duration using [Instant] or [ZonedDateTime] as any daylight savings rules won't be taken
 * into account when working with [DateTime] directly.
 *
 * @throws ArithmeticException if the result overflows
 */
fun nanosecondsBetween(start: DateTime, endExclusive: DateTime): LongNanoseconds {
    return nanosecondsBetween(
        start.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        start.nanoOfSecondsSinceUnixEpoch,
        endExclusive.secondsSinceUnixEpochAt(UtcOffset.ZERO),
        endExclusive.nanoOfSecondsSinceUnixEpoch
    )
}

internal fun adjustedEndDate(start: DateTime, endExclusive: DateTime): Date {
    return when {
        endExclusive.date > start.date && endExclusive.time < start.time -> endExclusive.date - 1.days
        endExclusive.date < start.date && endExclusive.time > start.time -> endExclusive.date + 1.days
        else -> endExclusive.date
    }
}