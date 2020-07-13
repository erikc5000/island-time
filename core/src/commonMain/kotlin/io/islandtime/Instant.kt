@file:Suppress("FunctionName")

package io.islandtime

import io.islandtime.base.*
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.measures.internal.plusWithOverflow
import io.islandtime.parser.*
import io.islandtime.ranges.InstantInterval

/**
 * An instant in time with nanosecond precision.
 */
class Instant private constructor(
    override val secondOfUnixEpoch: Long,
    override val nanosecond: Int
) : TimePoint<Instant>,
    Comparable<Instant> {

    init {
        if (secondOfUnixEpoch !in MIN_SECOND..MAX_SECOND) {
            throw DateTimeException("'$secondOfUnixEpoch' is outside the supported second range")
        }
    }

    override val secondsSinceUnixEpoch: LongSeconds
        get() = secondOfUnixEpoch.seconds

    override val additionalNanosecondsSinceUnixEpoch: IntNanoseconds
        get() = nanosecond.nanoseconds

    override val millisecondsSinceUnixEpoch: LongMilliseconds
        get() = secondsSinceUnixEpoch + additionalNanosecondsSinceUnixEpoch.inMilliseconds

    override val millisecondOfUnixEpoch: Long
        get() = millisecondsSinceUnixEpoch.value

    operator fun plus(other: Duration): Instant {
        return when {
            other.isZero() -> this
            else -> plus(other.seconds, other.nanosecondAdjustment)
        }
    }

    operator fun plus(days: IntDays) = plus(days.toLongDays().inSecondsUnchecked, 0.nanoseconds)
    operator fun plus(days: LongDays) = plus(days.inSeconds, 0.nanoseconds)

    override operator fun plus(hours: IntHours) = plus(hours.toLongHours().inSecondsUnchecked, 0.nanoseconds)
    override operator fun plus(hours: LongHours) = plus(hours.inSeconds, 0.nanoseconds)

    override operator fun plus(minutes: IntMinutes) = plus(minutes.toLongMinutes().inSecondsUnchecked, 0.nanoseconds)
    override operator fun plus(minutes: LongMinutes) = plus(minutes.inSeconds, 0.nanoseconds)

    override operator fun plus(seconds: IntSeconds) = plus(seconds.toLongSeconds(), 0.nanoseconds)
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

    override operator fun plus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds())

    override operator fun plus(nanoseconds: LongNanoseconds): Instant {
        return plus(
            nanoseconds.inSeconds,
            (nanoseconds % NANOSECONDS_PER_SECOND).toIntNanosecondsUnchecked()
        )
    }

    operator fun minus(other: Duration): Instant {
        return if (other.seconds.value == Long.MIN_VALUE) {
            plus(Long.MAX_VALUE.seconds, other.nanosecondAdjustment.negateUnchecked())
        } else {
            plus(other.seconds.negateUnchecked(), other.nanosecondAdjustment.negateUnchecked())
        }
    }

    operator fun minus(days: IntDays) = plus(days.toLongDays().negateUnchecked())

    operator fun minus(days: LongDays): Instant {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(days.negateUnchecked())
        }
    }

    override operator fun minus(hours: IntHours) = plus(hours.toLongHours().negateUnchecked())

    override operator fun minus(hours: LongHours): Instant {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(-hours)
        }
    }

    override operator fun minus(minutes: IntMinutes) = plus(minutes.toLongMinutes().negateUnchecked())

    override operator fun minus(minutes: LongMinutes): Instant {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(minutes.negateUnchecked())
        }
    }

    override operator fun minus(seconds: IntSeconds) = plus(seconds.toLongSeconds().negateUnchecked())

    override operator fun minus(seconds: LongSeconds): Instant {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(seconds.negateUnchecked())
        }
    }

    override operator fun minus(milliseconds: IntMilliseconds) =
        plus(milliseconds.toLongMilliseconds().negateUnchecked())

    override operator fun minus(milliseconds: LongMilliseconds): Instant {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(milliseconds.negateUnchecked())
        }
    }

    override operator fun minus(microseconds: IntMicroseconds) =
        plus(microseconds.toLongMicroseconds().negateUnchecked())

    override operator fun minus(microseconds: LongMicroseconds): Instant {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(microseconds.negateUnchecked())
        }
    }

    override operator fun minus(nanoseconds: IntNanoseconds) = plus(nanoseconds.toLongNanoseconds().negateUnchecked())

    override operator fun minus(nanoseconds: LongNanoseconds): Instant {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(nanoseconds.negateUnchecked())
        }
    }

    operator fun rangeTo(other: Instant) = InstantInterval.withInclusiveEnd(this, other)

    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            TimeProperty.MillisecondOfSecond,
            TimeProperty.MicrosecondOfSecond,
            TimeProperty.NanosecondOfSecond,
            is TimePointProperty -> true
            else -> false
        }
    }

    override fun get(property: NumberProperty): Long {
        return when (property) {
            TimeProperty.MillisecondOfSecond -> nanoOfSecond.toLong() / NANOSECONDS_PER_MILLISECOND
            TimeProperty.MicrosecondOfSecond -> nanoOfSecond.toLong() / NANOSECONDS_PER_MICROSECOND
            TimeProperty.NanosecondOfSecond -> nanoOfSecond.toLong()
            TimePointProperty.SecondOfUnixEpoch -> unixEpochSecond
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    override fun <T> get(property: ObjectProperty<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (property) {
            TimePointProperty.Instant -> this as T
            else -> throwUnsupportedTemporalPropertyException(property)
        }
    }

    override fun compareTo(other: Instant): Int {
        val secondsDiff = secondOfUnixEpoch.compareTo(other.secondOfUnixEpoch)

        return if (secondsDiff != 0) {
            secondsDiff
        } else {
            nanosecond - other.nanosecond
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
                secondsSinceUnixEpoch + secondsToAdd,
                additionalNanosecondsSinceUnixEpoch plusWithOverflow nanosecondsToAdd
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is Instant && secondOfUnixEpoch == other.secondOfUnixEpoch && nanosecond == other.nanosecond)
    }

    override fun hashCode(): Int {
        return 31 * secondOfUnixEpoch.hashCode() + nanosecond
    }

    companion object {
        private const val MIN_SECOND = -31557014167219200L
        private const val MAX_SECOND = 31556889864403199L

        /**
         * The smallest supported [Instant], which can be used as a "far past" sentinel.
         */
        val MIN = fromSecondOfUnixEpoch(MIN_SECOND)

        /**
         * The largest supported [Instant], which can be used as a "far future" sentinel.
         */
        val MAX = fromSecondOfUnixEpoch(MAX_SECOND, 999_999_999L)

        /**
         * The [Instant] representing the Unix epoch of 1970-01-01T00:00Z.
         */
        val UNIX_EPOCH = fromSecondOfUnixEpoch(0L)

        /**
         * Create an [Instant] from the second of the Unix epoch.
         */
        fun fromSecondOfUnixEpoch(second: Long): Instant {
            return Instant(second, 0)
        }

        /**
         * Create an [Instant] from the second of the Unix epoch.
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Int): Instant {
            return fromSecondOfUnixEpoch(second, nanosecond.toLong())
        }

        /**
         * Create an [Instant] from the second of the Unix epoch.
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Long): Instant {
            val newSecond = second plusExact (nanosecond floorDiv NANOSECONDS_PER_SECOND)
            val newNanosecond = (nanosecond floorMod NANOSECONDS_PER_SECOND).toInt()

            return Instant(newSecond, newNanosecond)
        }

        /**
         * Create an [Instant] from the millisecond of the Unix epoch.
         */
        fun fromMillisecondOfUnixEpoch(millisecond: Long): Instant {
            val second = millisecond floorDiv MILLISECONDS_PER_SECOND
            val nanosecond = (millisecond floorMod MILLISECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MILLISECOND
            return Instant(second, nanosecond)
        }

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromSecondOfUnixEpoch(second)"),
            DeprecationLevel.WARNING
        )
        fun fromUnixEpochSecond(second: Long): Instant {
            return fromSecondOfUnixEpoch(second)
        }

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromSecondOfUnixEpoch(second, nanosecondAdjustment)"),
            DeprecationLevel.WARNING
        )
        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Int): Instant {
            return fromSecondOfUnixEpoch(second, nanosecondAdjustment)
        }

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromSecondOfUnixEpoch(second, nanosecondAdjustment)"),
            DeprecationLevel.WARNING
        )
        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Long): Instant {
            return fromSecondOfUnixEpoch(second, nanosecondAdjustment)
        }

        @Deprecated(
            "Use fromMillisecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromMillisecondOfUnixEpoch(millisecond)"),
            DeprecationLevel.WARNING
        )
        fun fromUnixEpochMillisecond(millisecond: Long): Instant {
            return fromMillisecondOfUnixEpoch(millisecond)
        }
    }
}

/**
 * Create the [Instant] represented by a number of seconds relative to the Unix epoch of 1970-01-01T00:00Z.
 */
fun Instant(secondsSinceUnixEpoch: LongSeconds) = Instant.fromSecondOfUnixEpoch(secondsSinceUnixEpoch.value)

/**
 * Create the [Instant] represented by a number of seconds and additional nanoseconds relative to the Unix epoch of
 * 1970-01-01T00:00Z.
 */
fun Instant(secondsSinceUnixEpoch: LongSeconds, nanosecondAdjustment: IntNanoseconds): Instant {
    return Instant.fromSecondOfUnixEpoch(secondsSinceUnixEpoch.value, nanosecondAdjustment.value)
}

/**
 * Create the [Instant] represented by a number of seconds and additional nanoseconds relative to the Unix epoch of
 * 1970-01-01T00:00Z.
 */
fun Instant(secondsSinceUnixEpoch: LongSeconds, nanosecondAdjustment: LongNanoseconds): Instant {
    return Instant.fromSecondOfUnixEpoch(secondsSinceUnixEpoch.value, nanosecondAdjustment.value)
}

/**
 * Create the [Instant] represented by a number of milliseconds relative to the Unix epoch of 1970-01-01T00:00Z.
 */
fun Instant(millisecondsSinceUnixEpoch: LongMilliseconds): Instant {
    return Instant.fromMillisecondOfUnixEpoch(millisecondsSinceUnixEpoch.value)
}

/**
 * Convert a string to an [Instant].
 *
 * The string is assumed to be an ISO-8601 UTC date-time representation in extended format. For example,
 * `2010-10-05T18:30Z` or `2010-10-05T18:30:00.123456789Z`. The output of [Instant.toString] can be safely parsed
 * using this method.
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed date or time is invalid
 */
fun String.toInstant() = toInstant(DateTimeParsers.Iso.Extended.INSTANT)

/**
 * Convert a string to an [Instant] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws TemporalParseException if parsing fails
 * @throws DateTimeException if the parsed date or time is invalid
 */
fun String.toInstant(
    parser: TemporalParser,
    settings: TemporalParser.Settings = TemporalParser.Settings.DEFAULT
): Instant {
    val result = parser.parse(this, settings)
    return result.toInstant() ?: throwParserPropertyResolutionException<Instant>(this)
}

private const val SECONDS_PER_10000_YEARS = 146097L * 25L * 86400L

internal fun TemporalParseResult.toInstant(): Instant? {
    // FIXME: Require the year field here for now and make it fit within DateTime's supported range
    val parsedYear = this[DateProperty.Year] ?: return null

    this[DateProperty.Year] = parsedYear % 10_000
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()

    // Restore the original parsed year
    this[DateProperty.Year] = parsedYear

    return if (dateTime != null && offset != null) {
        val secondOfEpoch = dateTime.secondOfUnixEpochAt(offset) +
            ((parsedYear / 10_000L) timesExact SECONDS_PER_10000_YEARS)
        Instant.fromSecondOfUnixEpoch(secondOfEpoch, dateTime.nanosecond)
    } else {
        null
    }
}

internal const val MAX_INSTANT_STRING_LENGTH = MAX_DATE_TIME_STRING_LENGTH + 1

internal fun StringBuilder.appendInstant(instant: Instant): StringBuilder {
    val secondOfUnixEpoch = instant.secondOfUnixEpoch
    val nanosecond = instant.nanosecond

    return withComponentizedSecondOfUnixEpoch(secondOfUnixEpoch) { year, monthNumber, day, hour, minute, second ->
        appendDate(year, monthNumber, day)
        append('T')
        appendTime(hour, minute, second, nanosecond)
        append('Z')
    }
}

private inline fun <T> withComponentizedSecondOfUnixEpoch(
    secondOfUnixEpoch: Long,
    block: (year: Int, monthNumber: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int) -> T
): T {
    val dayOfUnixEpoch = secondOfUnixEpoch floorDiv SECONDS_PER_DAY
    val secondsSinceStartOfDay = (secondOfUnixEpoch floorMod SECONDS_PER_DAY).toInt().seconds

    return withComponentizedDayOfUnixEpoch(dayOfUnixEpoch) { year, monthNumber, dayOfMonth ->
        secondsSinceStartOfDay.toComponents { hours, minutes, seconds ->
            block(year, monthNumber, dayOfMonth, hours.value, minutes.value, seconds.value)
        }
    }
}

internal class ComponentizedInstant(
    val year: Int,
    val month: Month,
    val dayOfMonth: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val nanosecond: Int
) : Temporal {
    override fun has(property: TemporalProperty<*>): Boolean {
        return when (property) {
            is DateProperty, is TimeProperty, is UtcOffsetProperty -> true
            else -> false
        }
    }
}