package io.islandtime

import io.islandtime.internal.*
import io.islandtime.internal.MICROSECONDS_PER_SECOND
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.NANOSECONDS_PER_MICROSECOND
import io.islandtime.internal.NANOSECONDS_PER_MILLISECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.*
import io.islandtime.measures.plusExact
import io.islandtime.parser.*
import io.islandtime.ranges.InstantInterval

/**
 * An instant in time with nanosecond precision.
 */
class Instant private constructor(
    private val second: Long,
    private val nanoOfSecond: Int
) : TimePoint<Instant>,
    Comparable<Instant> {

    init {
        if (second !in MIN_SECOND..MAX_SECOND) {
            throw DateTimeException("'$second' is outside the supported second range")
        }
    }

    override val secondsSinceUnixEpoch: LongSeconds
        get() = second.seconds

    override val nanoOfSecondsSinceUnixEpoch: IntNanoseconds
        get() = nanoOfSecond.nanoseconds

    override val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = secondsSinceUnixEpoch.inMillisecondsExact() plusExact
            nanoOfSecondsSinceUnixEpoch.inMilliseconds.toLong()

    override val unixEpochSecond: Long
        get() = second

    override val unixEpochNanoOfSecond: Int
        get() = nanoOfSecond

    override val unixEpochMillisecond: Long
        get() = millisecondsSinceUnixEpoch.value

    operator fun plus(other: Duration): Instant {
        return when {
            other.isZero -> this
            else -> plus(other.seconds, other.nanosecondAdjustment)
        }
    }

    operator fun plus(days: IntDays) = plus(days.toLong().inSeconds, 0.nanoseconds)
    operator fun plus(days: LongDays) = plus(days.inSecondsExact(), 0.nanoseconds)

    override operator fun plus(hours: IntHours) = plus(hours.toLong().inSeconds, 0.nanoseconds)
    override operator fun plus(hours: LongHours) = plus(hours.inSecondsExact(), 0.nanoseconds)

    override operator fun plus(minutes: IntMinutes) = plus(minutes.toLong().inSeconds, 0.nanoseconds)
    override operator fun plus(minutes: LongMinutes) = plus(minutes.inSecondsExact(), 0.nanoseconds)

    override operator fun plus(seconds: IntSeconds) = plus(seconds.toLong(), 0.nanoseconds)
    override operator fun plus(seconds: LongSeconds) = plus(seconds, 0.nanoseconds)

    override operator fun plus(milliseconds: IntMilliseconds) = plus(milliseconds.inNanoseconds)

    override operator fun plus(milliseconds: LongMilliseconds): Instant {
        return plus(
            milliseconds.inSeconds,
            ((milliseconds.value % MILLISECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MILLISECOND).nanoseconds
        )
    }

    override operator fun plus(microseconds: IntMicroseconds) = plus(microseconds.inNanoseconds)

    override operator fun plus(microseconds: LongMicroseconds): Instant {
        return plus(
            microseconds.inSeconds,
            ((microseconds.value % MICROSECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MICROSECOND).nanoseconds
        )
    }

    override operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLong())

    override operator fun plus(nanoseconds: LongNanoseconds): Instant {
        return plus(
            nanoseconds.inSeconds,
            (nanoseconds % NANOSECONDS_PER_SECOND).toInt()
        )
    }

    operator fun minus(other: Duration): Instant {
        return if (other.seconds.value == Long.MIN_VALUE) {
            plus(Long.MAX_VALUE.seconds, -other.nanosecondAdjustment)
        } else {
            plus(-other.seconds, -other.nanosecondAdjustment)
        }
    }

    operator fun minus(days: IntDays) = plus(-days.toLong())

    operator fun minus(days: LongDays): Instant {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(-days)
        }
    }

    override operator fun minus(hours: IntHours) = plus(-hours.toLong())

    override operator fun minus(hours: LongHours): Instant {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(-hours)
        }
    }

    override operator fun minus(minutes: IntMinutes) = plus(-minutes.toLong())

    override operator fun minus(minutes: LongMinutes): Instant {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(-minutes)
        }
    }

    override operator fun minus(seconds: IntSeconds) = plus(-seconds.toLong())

    override operator fun minus(seconds: LongSeconds): Instant {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(-seconds)
        }
    }

    override operator fun minus(milliseconds: IntMilliseconds) = plus(-milliseconds.toLong())

    override operator fun minus(milliseconds: LongMilliseconds): Instant {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(-milliseconds)
        }
    }

    override operator fun minus(microseconds: IntMicroseconds) = plus(-microseconds.toLong())

    override operator fun minus(microseconds: LongMicroseconds): Instant {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(-microseconds)
        }
    }

    override operator fun minus(nanoseconds: IntNanoseconds) = plus(-nanoseconds.toLong())

    override operator fun minus(nanoseconds: LongNanoseconds): Instant {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(-nanoseconds)
        }
    }

    operator fun rangeTo(other: Instant) = InstantInterval.withInclusiveEnd(this, other)

    override fun compareTo(other: Instant): Int {
        val secondsDiff = second.compareTo(other.second)

        return if (secondsDiff != 0) {
            secondsDiff
        } else {
            nanoOfSecond - other.nanoOfSecond
        }
    }

    /**
     * Convert this instant to a string in ISO-8601 extended format.
     */
    override fun toString() = buildString(MAX_INSTANT_STRING_LENGTH) { appendInstant(this@Instant) }

    private fun plus(secondsToAdd: LongSeconds, nanosecondsToAdd: IntNanoseconds): Instant {
        return if (secondsToAdd.value == 0L && nanosecondsToAdd.value == 0) {
            this
        } else {
            Instant(
                secondsSinceUnixEpoch plusExact secondsToAdd,
                nanoOfSecondsSinceUnixEpoch plusWithOverflow nanosecondsToAdd
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other ||
            (other is Instant && second == other.second && nanoOfSecond == other.nanoOfSecond)
    }

    override fun hashCode(): Int {
        return 31 * second.hashCode() + nanoOfSecond
    }

    companion object {
        private const val MIN_SECOND = -31557014167219200L
        private const val MAX_SECOND = 31556889864403199L

        /**
         * The smallest supported [Instant], which can be used as a "far past" sentinel.
         */
        val MIN = fromUnixEpochSecond(MIN_SECOND)

        /**
         * The largest supported [Instant], which can be used as a "far future" sentinel.
         */
        val MAX = fromUnixEpochSecond(MAX_SECOND, 999_999_999L)

        /**
         * The [Instant] representing the Unix epoch of 1970-01-01T00:00Z.
         */
        val UNIX_EPOCH = fromUnixEpochSecond(0L)

        /**
         * Create an [Instant] from the second of the Unix epoch.
         */
        fun fromUnixEpochSecond(second: Long): Instant {
            return Instant(second, 0)
        }

        /**
         * Create an [Instant] from the second of the Unix epoch.
         */
        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Int): Instant {
            return fromUnixEpochSecond(second, nanosecondAdjustment.toLong())
        }

        /**
         * Create an [Instant] from the second of the Unix epoch.
         */
        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Long): Instant {
            val newSecond = second plusExact (nanosecondAdjustment floorDiv NANOSECONDS_PER_SECOND)
            val newNanosecond = (nanosecondAdjustment floorMod NANOSECONDS_PER_SECOND).toInt()

            return Instant(newSecond, newNanosecond)
        }

        /**
         * Create an [Instant] from the millisecond of the Unix epoch.
         */
        fun fromUnixEpochMillisecond(millisecond: Long): Instant {
            val second = millisecond floorDiv MILLISECONDS_PER_SECOND
            val nanosecond = (millisecond floorMod MILLISECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MILLISECOND
            return Instant(second, nanosecond)
        }
    }
}

internal const val MAX_INSTANT_STRING_LENGTH = MAX_DATE_TIME_STRING_LENGTH + 1

internal fun StringBuilder.appendInstant(instant: Instant): StringBuilder {
    val dateTime = instant.toDateTimeAt(UtcOffset.ZERO)
    appendDateTime(dateTime)
    append('Z')
    return this
}

/**
 * Create the [Instant] represented by a number of seconds relative to the Unix epoch of 1970-01-01T00:00Z.
 */
@Suppress("FunctionName")
fun Instant(secondsSinceUnixEpoch: LongSeconds) = Instant.fromUnixEpochSecond(secondsSinceUnixEpoch.value)

/**
 * Create the [Instant] represented by a number of seconds and additional nanoseconds relative to the Unix epoch of
 * 1970-01-01T00:00Z.
 */
@Suppress("FunctionName")
fun Instant(secondsSinceUnixEpoch: LongSeconds, nanosecondAdjustment: IntNanoseconds): Instant {
    return Instant.fromUnixEpochSecond(secondsSinceUnixEpoch.value, nanosecondAdjustment.value)
}

/**
 * Create the [Instant] represented by a number of seconds and additional nanoseconds relative to the Unix epoch of
 * 1970-01-01T00:00Z.
 */
@Suppress("FunctionName")
fun Instant(secondsSinceUnixEpoch: LongSeconds, nanosecondAdjustment: LongNanoseconds): Instant {
    return Instant.fromUnixEpochSecond(secondsSinceUnixEpoch.value, nanosecondAdjustment.value)
}

/**
 * Create the [Instant] represented by a number of milliseconds relative to the Unix epoch of 1970-01-01T00:00Z.
 */
@Suppress("FunctionName")
fun Instant(millisecondsSinceUnixEpoch: LongMilliseconds): Instant {
    return Instant.fromUnixEpochMillisecond(millisecondsSinceUnixEpoch.value)
}

/**
 * Convert a string to an [Instant].
 *
 * The string is assumed to be an ISO-8601 UTC date-time representation in extended format. For example,
 * `2010-10-05T18:30Z` or `2010-10-05T18:30:00.123456789Z`. The output of [Instant.toString] can be safely parsed
 * using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toInstant() = toInstant(DateTimeParsers.Iso.Extended.INSTANT)

/**
 * Convert a string to an [Instant] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed time is invalid
 */
fun String.toInstant(parser: DateTimeParser): Instant {
    val result = parser.parse(this)
    return result.toInstant() ?: throwParserFieldResolutionException<Instant>(this)
}

internal fun DateTimeParseResult.toInstant(): Instant? {
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()

    return if (dateTime != null && offset != null) {
        dateTime.instantAt(offset)
    } else {
        null
    }
}