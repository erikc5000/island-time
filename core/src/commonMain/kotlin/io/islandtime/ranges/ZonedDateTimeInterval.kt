package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.interval.*
import kotlin.random.Random

/**
 * A half-open interval of zoned date-times based on timeline order.
 */
class ZonedDateTimeInterval(
    start: ZonedDateTime = UNBOUNDED.start,
    endExclusive: ZonedDateTime = UNBOUNDED.endExclusive
) : TimePointInterval<ZonedDateTime>(start, endExclusive) {

    override val hasUnboundedStart: Boolean get() = start.dateTime == DateTime.MIN
    override val hasUnboundedEnd: Boolean get() = endExclusive.dateTime == DateTime.MAX

    override fun toString() = buildIsoString(MAX_ZONED_DATE_TIME_STRING_LENGTH, StringBuilder::appendZonedDateTime)

    /**
     * Convert the range into a period containing each day in the range. As a range is inclusive, if the start and end
     * date are the same, the resulting period will contain one day.
     */
    fun asPeriod(): Period {
        return when {
            isEmpty() -> Period.ZERO
            isBounded -> periodBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }
    }

    /**
     * Get the number of years the range. A year is considered to have passed if twelve full months have passed between
     * the start date and end date, according to the definition of 'month' in [months].
     */
    val years
        get() = when {
            isEmpty() -> 0.years
            isBounded -> yearsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of months in the range. A month is considered to have passed if the day of the end month is
     * greater than or equal to the day of the start month.
     */
    val months
        get() = when {
            isEmpty() -> 0.months
            isBounded -> monthsBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    /**
     * Get the number of days in the range. As a range is inclusive, if the start and end date are the same, the
     * result will be one day. Daylight savings time differences are taken into account when calculating the number of
     * days.
     */
    override val days
        get() = when {
            isEmpty() -> 0L.days
            isBounded -> daysBetween(start, endExclusive)
            else -> throwUnboundedIntervalException()
        }

    companion object {
        /**
         * An empty interval
         */
        val EMPTY = ZonedDateTimeInterval(
            Instant.UNIX_EPOCH at TimeZone.UTC,
            Instant.UNIX_EPOCH at TimeZone.UTC
        )

        /**
         * An unbounded (ie. infinite) interval
         */
        val UNBOUNDED = ZonedDateTimeInterval(
            DateTime.MIN at TimeZone.UTC,
            DateTime.MAX at TimeZone.UTC
        )

        fun withInclusiveEnd(
            start: ZonedDateTime,
            endInclusive: ZonedDateTime
        ): ZonedDateTimeInterval {
            val endExclusive = if (endInclusive.dateTime == DateTime.MAX) {
                endInclusive
            } else {
                endInclusive + 1.nanoseconds
            }

            return ZonedDateTimeInterval(start, endExclusive)
        }
    }
}

//infix fun <T : DayBased<T>> DayInterval<T>.step(step: IntMonths): MonthProgression<T> {
//    require(step > 0.months) { "step must be positive" }
//    return MonthProgression.fromClosedRange(start, endInclusive, step)
//}
//
//infix fun <T : DayBased<T>> DayInterval<T>.step(step: IntYears): MonthProgression<T> {
//    require(step > 0.years) { "step must be positive" }
//    return MonthProgression.fromClosedRange(start, endInclusive, step.inMonthsExact())
//}

/**
 * Return a random date-time within the range using the default random number generator
 */
fun ZonedDateTimeInterval.random(): ZonedDateTime = random(Random)

/**
 * Return a random date-time within the range using the supplied random number generator
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
 * Get an interval containing all of the representable time points up to, but not including [to]. If the start and end
 * date-times are in different time zones, the end will be adjusted to match the starting zone while preserving the
 * instant.
 */
infix fun ZonedDateTime.until(to: ZonedDateTime) = ZonedDateTimeInterval(this, to)

fun periodBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): Period {
    return periodBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}

fun yearsBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): IntYears {
    return yearsBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}

fun monthsBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): IntMonths {
    return monthsBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}

fun daysBetween(start: ZonedDateTime, endExclusive: ZonedDateTime): LongDays {
    return daysBetween(start.dateTime, endExclusive.adjustedTo(start.zone).dateTime)
}