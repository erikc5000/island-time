package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.base.DateProperty
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.internal.MAX_INCLUSIVE_END_DATE_TIME
import io.islandtime.ranges.internal.buildIsoString
import io.islandtime.ranges.internal.throwUnboundedIntervalException
import kotlin.random.Random

/**
 * A half-open interval of zoned date-times based on timeline order.
 *
 * [DateTime.MIN] and [DateTime.MAX] are used as sentinels to indicate an unbounded (ie. infinite) start or end. A
 * [ZonedDateTime] with either as the date-time component will be treated accordingly, regardless of the offset or
 * time zone.
 */
class ZonedDateTimeInterval(
    start: ZonedDateTime = UNBOUNDED.start,
    endExclusive: ZonedDateTime = UNBOUNDED.endExclusive
) : TimePointInterval<ZonedDateTime>(start, endExclusive) {

    override fun hasUnboundedStart(): Boolean = start.dateTime == DateTime.MIN
    override fun hasUnboundedEnd(): Boolean = endExclusive.dateTime == DateTime.MAX

    /**
     * Convert this interval to a string in ISO-8601 extended format.
     */
    override fun toString() = buildIsoString(MAX_ZONED_DATE_TIME_STRING_LENGTH, StringBuilder::appendZonedDateTime)

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
     * Get the number of years between the start and end of the interval. A year is considered to have passed if twelve
     * full months have passed between the start date and end date.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    val lengthInYears
        get() = when {
            isEmpty() -> 0.years
            isBounded() -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of months between the start and end of the interval. A month is considered to have passed if the
     * day of the end month is greater than or equal to the day of the start month.
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
    val lengthInWeeks
        get() = when {
            isEmpty() -> 0L.weeks
            isBounded() -> weeksBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of whole days in the interval.
     * @throws UnsupportedOperationException if the interval isn't bounded
     */
    override val lengthInDays
        get() = when {
            isEmpty() -> 0L.days
            isBounded() -> daysBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    companion object {
        /**
         * An empty interval.
         */
        val EMPTY = ZonedDateTimeInterval(
            Instant.UNIX_EPOCH at TimeZone.UTC,
            Instant.UNIX_EPOCH at TimeZone.UTC
        )

        /**
         * An unbounded (ie. infinite) interval.
         */
        val UNBOUNDED = ZonedDateTimeInterval(
            DateTime.MIN at TimeZone.UTC,
            DateTime.MAX at TimeZone.UTC
        )

        internal fun withInclusiveEnd(
            start: ZonedDateTime,
            endInclusive: ZonedDateTime
        ): ZonedDateTimeInterval {
            val endExclusive = when {
                endInclusive.dateTime == DateTime.MAX -> endInclusive
                endInclusive.dateTime > MAX_INCLUSIVE_END_DATE_TIME ->
                    throw DateTimeException("The end of the interval can't be represented")
                else -> endInclusive + 1.nanoseconds
            }

            return ZonedDateTimeInterval(start, endExclusive)
        }
    }
}

/**
 * Convert a string to a [ZonedDateTimeInterval].
 *
 * The string is assumed to be an ISO-8601 time interval representation in extended format. The output of
 * [ZonedDateTimeInterval.toString] can be safely parsed using this method.
 *
 * Examples:
 * - `1990-01-04T03-05[America/New_York]/1991-08-30T15:30:05.123-04:00`
 * - `../1991-08-30T15:30:05.123-04:00`
 * - `1990-01-04T03-05[Europe/London]/..`
 * - `../..`
 * - (empty string)
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toZonedDateTimeInterval() = toZonedDateTimeInterval(DateTimeParsers.Iso.Extended.ZONED_DATE_TIME_INTERVAL)

/**
 * Convert a string to a [ZonedDateTimeInterval] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed interval is invalid
 */
fun String.toZonedDateTimeInterval(
    parser: GroupedTemporalParser,
    settings: TemporalParser.Settings = TemporalParser.Settings.DEFAULT
): ZonedDateTimeInterval {
    val results = parser.parse(this, settings)
        .expectingGroupCount<ZonedDateTimeInterval>(2, this)

    val start = when {
        results[0].isEmpty() -> null
        results[0][DateProperty.IsFarPast] == true -> ZonedDateTimeInterval.UNBOUNDED.start
        else -> results[0].toZonedDateTime() ?: throwParserPropertyResolutionException<ZonedDateTimeInterval>(this)
    }

    val end = when {
        results[1].isEmpty() -> null
        results[1][DateProperty.IsFarFuture] == true -> ZonedDateTimeInterval.UNBOUNDED.endExclusive
        else -> results[1].toZonedDateTime() ?: throwParserPropertyResolutionException<ZonedDateTimeInterval>(this)
    }

    return when {
        start != null && end != null -> start until end
        start == null && end == null -> ZonedDateTimeInterval.EMPTY
        else -> throw TemporalParseException("Intervals with unknown start or end are not supported")
    }
}

/**
 * Return a random date-time within the interval using the default random number generator.
 */
fun ZonedDateTimeInterval.random(): ZonedDateTime = random(Random)

/**
 * Return a random date-time within the interval using the supplied random number generator.
 */
fun ZonedDateTimeInterval.random(random: Random): ZonedDateTime {
    try {
        return ZonedDateTime.fromUnixEpochSecond(
            random.nextLong(start.unixEpochSecond, endExclusive.unixEpochSecond),
            random.nextInt(start.unixEpochNanoOfSecond, endExclusive.unixEpochNanoOfSecond),
            start.zone
        )
    } catch (e: IllegalArgumentException) {
        throw NoSuchElementException(e.message)
    }
}

/**
 * Get an interval containing all of the representable time points up to, but not including [to].
 */
infix fun ZonedDateTime.until(to: ZonedDateTime) = ZonedDateTimeInterval(this, to)

/**
 * Convert a range of dates into a [ZonedDateTimeInterval] between the starting and ending instants in a particular
 * time zone.
 */
fun DateRange.toZonedDateTimeIntervalAt(zone: TimeZone): ZonedDateTimeInterval {
    return when {
        isEmpty() -> ZonedDateTimeInterval.EMPTY
        isUnbounded() -> ZonedDateTimeInterval.UNBOUNDED
        start == endInclusive -> {
            val zonedStart = start.startOfDayAt(zone)
            val zonedEnd = zonedStart + 1.days
            zonedStart until zonedEnd
        }
        else -> {
            val start = if (hasUnboundedStart()) {
                DateTime.MIN at zone
            } else {
                start.startOfDayAt(zone)
            }

            val end = if (hasUnboundedEnd()) {
                DateTime.MAX at zone
            } else {
                endInclusive.endOfDayAt(zone)
            }

            start..end
        }
    }
}

/**
 * Get the [Period] between two zoned date-times, adjusting the time zone of [endExclusive] if necessary to match the
 * starting date-time.
 */
fun periodBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): Period {
    return periodBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}

/**
 * Get the number of whole years between two zoned date-times, adjusting the time zone of [endExclusive] if necessary to
 * match the starting date-time.
 */
fun yearsBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): IntYears {
    return yearsBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}

/**
 * Get the number of whole months between two zoned date-times, adjusting the time zone of [endExclusive] if necessary
 * to match the starting date-time.
 */
fun monthsBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): IntMonths {
    return monthsBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}

/**
 * Get the number of whole weeks between two zoned date-times, adjusting the time zone of [endExclusive] if necessary to
 * match the starting date-time.
 */
fun weeksBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): LongWeeks {
    return weeksBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}

/**
 * Get the number of whole days between two zoned date-times, adjusting the time zone of [endExclusive] if necessary to
 * match the starting date-time.
 */
fun daysBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): LongDays {
    return daysBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}