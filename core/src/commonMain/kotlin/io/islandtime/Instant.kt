@file:Suppress("FunctionName")

package io.islandtime

import dev.erikchristensen.javamath2kmp.floorDiv
import dev.erikchristensen.javamath2kmp.floorMod
import dev.erikchristensen.javamath2kmp.plusExact
import dev.erikchristensen.javamath2kmp.timesExact
import io.islandtime.base.DateTimeField
import io.islandtime.base.TimePoint
import io.islandtime.internal.*
import io.islandtime.measures.*
import io.islandtime.parser.*
import io.islandtime.ranges.InstantInterval

/**
 * A platform-specific representation of an instant in time.
 */
expect class PlatformInstant

/**
 * An instant in time with nanosecond-precision.
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

    override val millisecondsSinceUnixEpoch: Milliseconds
        get() = secondsSinceUnixEpoch + additionalNanosecondsSinceUnixEpoch.inWholeMilliseconds

    override val millisecondOfUnixEpoch: Long
        get() = millisecondsSinceUnixEpoch.value

    operator fun plus(other: Duration): Instant {
        return when {
            other.isZero() -> this
            else -> plus(other.seconds.toLong(), other.nanosecondAdjustment.toIntUnchecked())
        }
    }

    operator fun plus(days: Days): Instant = plus(days.inSeconds.toLong(), 0)
    override operator fun plus(hours: Hours): Instant = plus(hours.inSeconds.toLong(), 0)
    override operator fun plus(minutes: Minutes): Instant = plus(minutes.inSeconds.toLong(), 0)
    override operator fun plus(seconds: Seconds): Instant = plus(seconds.toLong(), 0)

    override operator fun plus(milliseconds: Milliseconds): Instant {
        return plus(
            milliseconds.inWholeSeconds.toLong(),
            (milliseconds.value % MILLISECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MILLISECOND
        )
    }

    override operator fun plus(microseconds: Microseconds): Instant {
        return plus(
            microseconds.inWholeSeconds.toLong(),
            (microseconds.value % MICROSECONDS_PER_SECOND).toInt() * NANOSECONDS_PER_MICROSECOND
        )
    }

    override operator fun plus(nanoseconds: Nanoseconds): Instant {
        return plus(
            nanoseconds.inWholeSeconds.toLong(),
            (nanoseconds % NANOSECONDS_PER_SECOND).toIntUnchecked()
        )
    }

    operator fun minus(other: Duration): Instant {
        return if (other.seconds.value == Long.MIN_VALUE) {
            plus(Long.MAX_VALUE, -other.nanosecondAdjustment.toIntUnchecked())
        } else {
            plus(-other.seconds.toLong(), -other.nanosecondAdjustment.toIntUnchecked())
        }
    }

    operator fun minus(days: Days): Instant {
        return if (days.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.days + 1.days
        } else {
            plus(days.negateUnchecked())
        }
    }

    override operator fun minus(hours: Hours): Instant {
        return if (hours.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.hours + 1.hours
        } else {
            plus(-hours)
        }
    }

    override operator fun minus(minutes: Minutes): Instant {
        return if (minutes.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.minutes + 1.minutes
        } else {
            plus(minutes.negateUnchecked())
        }
    }

    override operator fun minus(seconds: Seconds): Instant {
        return if (seconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.seconds + 1.seconds
        } else {
            plus(seconds.negateUnchecked())
        }
    }

    override operator fun minus(milliseconds: Milliseconds): Instant {
        return if (milliseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.milliseconds + 1.milliseconds
        } else {
            plus(milliseconds.negateUnchecked())
        }
    }

    override operator fun minus(microseconds: Microseconds): Instant {
        return if (microseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.microseconds + 1.microseconds
        } else {
            plus(microseconds.negateUnchecked())
        }
    }

    override operator fun minus(nanoseconds: Nanoseconds): Instant {
        return if (nanoseconds.value == Long.MIN_VALUE) {
            this + Long.MAX_VALUE.nanoseconds + 1.nanoseconds
        } else {
            plus(nanoseconds.negateUnchecked())
        }
    }

    operator fun rangeTo(other: Instant): InstantInterval = InstantInterval.withInclusiveEnd(this, other)

    override fun compareTo(other: Instant): Int {
        val secondsDiff = secondOfUnixEpoch.compareTo(other.secondOfUnixEpoch)

        return if (secondsDiff != 0) {
            secondsDiff
        } else {
            nanosecond - other.nanosecond
        }
    }

    /**
     * Converts this instant to a string in ISO-8601 extended format. For example, `2020-02-13T02:30:05.367Z`.
     */
    override fun toString() = buildString(MAX_INSTANT_STRING_LENGTH) { appendInstant(this@Instant) }

    private fun plus(secondsToAdd: Long, nanosecondsToAdd: Int): Instant {
        return if (secondsToAdd == 0L && nanosecondsToAdd == 0) {
            this
        } else {
            fromSecondOfUnixEpoch(
                second = secondOfUnixEpoch plusExact secondsToAdd,
                nanosecond = nanosecond + nanosecondsToAdd
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other ||
            (other is Instant && secondOfUnixEpoch == other.secondOfUnixEpoch && nanosecond == other.nanosecond)
    }

    override fun hashCode(): Int {
        return 31 * secondOfUnixEpoch.hashCode() + nanosecond
    }

    companion object {
        private const val MIN_SECOND: Long = -31557014167219200L
        private const val MAX_SECOND: Long = 31556889864403199L

        /**
         * The earliest supported [Instant], which can be used as a "far past" sentinel.
         */
        val MIN: Instant = fromSecondOfUnixEpoch(MIN_SECOND)

        /**
         * The latest supported [Instant], which can be used as a "far future" sentinel.
         */
        val MAX: Instant = fromSecondOfUnixEpoch(MAX_SECOND, 999_999_999L)

        /**
         * The [Instant] representing the Unix epoch of 1970-01-01T00:00Z.
         */
        val UNIX_EPOCH: Instant = fromSecondOfUnixEpoch(0L)

        /**
         * Creates an [Instant] from the second of the Unix epoch.
         */
        fun fromSecondOfUnixEpoch(second: Long): Instant {
            return Instant(second, 0)
        }

        /**
         * Creates an [Instant] from the second of the Unix epoch.
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Int): Instant {
            return fromSecondOfUnixEpoch(second, nanosecond.toLong())
        }

        /**
         * Creates an [Instant] from the second of the Unix epoch.
         */
        fun fromSecondOfUnixEpoch(second: Long, nanosecond: Long): Instant {
            val newSecond = second plusExact (nanosecond floorDiv NANOSECONDS_PER_SECOND)
            val newNanosecond = nanosecond floorMod NANOSECONDS_PER_SECOND

            return Instant(newSecond, newNanosecond)
        }

        /**
         * Creates an [Instant] from the millisecond of the Unix epoch.
         */
        fun fromMillisecondOfUnixEpoch(millisecond: Long): Instant {
            val second = millisecond floorDiv MILLISECONDS_PER_SECOND
            val nanosecond = (millisecond floorMod MILLISECONDS_PER_SECOND) * NANOSECONDS_PER_MILLISECOND
            return Instant(second, nanosecond)
        }

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromSecondOfUnixEpoch(second)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER")
        fun fromUnixEpochSecond(second: Long): Instant = deprecatedToError()

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromSecondOfUnixEpoch(second, nanosecondAdjustment)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER")
        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Int): Instant = deprecatedToError()

        @Deprecated(
            "Use fromSecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromSecondOfUnixEpoch(second, nanosecondAdjustment)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER")
        fun fromUnixEpochSecond(second: Long, nanosecondAdjustment: Long): Instant = deprecatedToError()

        @Deprecated(
            "Use fromMillisecondOfUnixEpoch() instead.",
            ReplaceWith("Instant.fromMillisecondOfUnixEpoch(millisecond)"),
            DeprecationLevel.ERROR
        )
        @Suppress("UNUSED_PARAMETER")
        fun fromUnixEpochMillisecond(millisecond: Long): Instant = deprecatedToError()
    }
}

/**
 * Creates the [Instant] represented by a number of seconds relative to the Unix epoch of 1970-01-01T00:00Z.
 */
fun Instant(secondsSinceUnixEpoch: Seconds): Instant = Instant.fromSecondOfUnixEpoch(secondsSinceUnixEpoch.value)

/**
 * Creates the [Instant] represented by a number of seconds and additional nanoseconds relative to the Unix epoch of
 * 1970-01-01T00:00Z.
 */
fun Instant(secondsSinceUnixEpoch: Seconds, nanosecondAdjustment: Nanoseconds): Instant {
    return Instant.fromSecondOfUnixEpoch(secondsSinceUnixEpoch.value, nanosecondAdjustment.value)
}

/**
 * Creates the [Instant] represented by a number of milliseconds relative to the Unix epoch of 1970-01-01T00:00Z.
 */
fun Instant(millisecondsSinceUnixEpoch: Milliseconds): Instant {
    return Instant.fromMillisecondOfUnixEpoch(millisecondsSinceUnixEpoch.value)
}

/**
 * Converts a string to an [Instant].
 *
 * The string is assumed to be an ISO-8601 UTC date-time representation in extended format. For example,
 * `2010-10-05T18:30Z` or `2010-10-05T18:30:00.123456789Z`. The output of [Instant.toString] can be safely parsed
 * using this method.
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date or time is invalid
 */
fun String.toInstant(): Instant = toInstant(DateTimeParsers.Iso.Extended.INSTANT)

/**
 * Converts a string to an [Instant] using a specific parser.
 *
 * A set of predefined parsers can be found in [DateTimeParsers].
 *
 * @throws DateTimeParseException if parsing fails
 * @throws DateTimeException if the parsed date or time is invalid
 */
fun String.toInstant(
    parser: DateTimeParser,
    settings: DateTimeParserSettings = DateTimeParserSettings.DEFAULT
): Instant {
    val result = parser.parse(this, settings)
    return result.toInstant() ?: throwParserFieldResolutionException<Instant>(this)
}

private const val SECONDS_PER_10000_YEARS = 146097L * 25L * 86400L

internal fun DateTimeParseResult.toInstant(): Instant? {
    // FIXME: Require the year field here for now and make it fit within DateTime's supported range
    val parsedYear = fields[DateTimeField.YEAR] ?: return null

    fields[DateTimeField.YEAR] = parsedYear % 10_000
    val dateTime = this.toDateTime()
    val offset = this.toUtcOffset()

    // Restore the original parsed year
    fields[DateTimeField.YEAR] = parsedYear

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
    val secondsSinceStartOfDay = (secondOfUnixEpoch floorMod SECONDS_PER_DAY).seconds

    return withComponentizedDayOfUnixEpoch(dayOfUnixEpoch) { year, monthNumber, dayOfMonth ->
        secondsSinceStartOfDay.toComponentValues { hours, minutes, seconds ->
            block(year, monthNumber, dayOfMonth, hours.toInt(), minutes, seconds)
        }
    }
}
