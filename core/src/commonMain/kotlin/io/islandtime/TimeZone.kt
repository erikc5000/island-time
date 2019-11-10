package io.islandtime

import io.islandtime.measures.nanoseconds
import io.islandtime.measures.seconds
import io.islandtime.parser.*
import io.islandtime.zone.FixedTimeZoneRules
import io.islandtime.zone.TimeZoneRules
import io.islandtime.zone.TimeZoneRulesException
import io.islandtime.zone.TimeZoneRulesProvider

/**
 * A time zone.
 */
sealed class TimeZone : Comparable<TimeZone> {

    /**
     * An ID that uniquely identifies the time zone.
     */
    abstract val id: String

    /**
     * Check if this is a valid time zone according to the current time zone rules provider.
     */
    abstract val isValid: Boolean

    /**
     * Get the rules associated with this time zone.
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     */
    abstract val rules: TimeZoneRules

    /**
     * Check if the time zone is valid and throw an exception if it isn't.
     *
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     * @see isValid
     */
    fun validate() {
        if (!isValid) {
            throw TimeZoneRulesException("'$id' is not supported by the current time zone rules provider")
        }
    }

    /**
     * Ensure that the time zone is valid, throwing an exception if it isn't.
     *
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     * @see isValid
     */
    fun validated(): TimeZone = apply { validate() }

    /**
     * Get a normalized time zone.
     *
     * Any time zone with a fixed offset will be converted to use a consistent identifier.
     *
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     */
    abstract fun normalized(): TimeZone

    override fun compareTo(other: TimeZone): Int {
        return id.compareTo(other.id)
    }

    override fun toString() = id

    /**
     * A named time zone, typically corresponding to a region identifier in the IANA Time Zone Database, but may be any
     * name that can be understood by a [TimeZoneRulesProvider].
     *
     * @param id an ID that is understood by a time zone rules provider
     */
    class Region internal constructor(
        override val id: String
    ) : TimeZone() {

        override val isValid: Boolean
            get() = TimeZoneRulesProvider.hasRulesFor(id)

        override val rules: TimeZoneRules
            get() = TimeZoneRulesProvider.rulesFor(id)

        override fun normalized(): TimeZone = rules.run {
            if (hasFixedOffset) {
                FixedOffset(offsetAt(0L.seconds, 0.nanoseconds))
            } else {
                this@Region
            }
        }

        override fun equals(other: Any?): Boolean {
            return this === other || (other is Region && id == other.id)
        }

        override fun hashCode(): Int = id.hashCode()
    }

    /**
     * A time zone defined by a fixed offset from UTC.
     *
     * In general, region-based time zones are preferred, but there are situations where only a fixed offset may be
     * available.
     *
     * @param offset a valid UTC offset
     * @throws DateTimeException if [offset] is outside the valid range
     */
    class FixedOffset internal constructor(
        val offset: UtcOffset
    ) : TimeZone() {

        init {
            offset.validate()
        }

        override val id: String get() = offset.toString()
        override val isValid: Boolean get() = true
        override val rules: TimeZoneRules get() = FixedTimeZoneRules(offset)
        override fun normalized() = this

        override fun equals(other: Any?): Boolean {
            return this === other || (other is FixedOffset && offset == other.offset)
        }

        override fun hashCode() = offset.hashCode()
    }

    companion object {
        /**
         * A fixed time zone representing UTC.
         */
        val UTC: TimeZone = FixedOffset(UtcOffset.ZERO)

        @Suppress("FunctionName")
        fun FixedOffset(id: String): TimeZone {
            return try {
                FixedOffset(id.toUtcOffset(FIXED_TIME_ZONE_PARSER))
            } catch (e: DateTimeParseException) {
                throw TimeZoneRulesException("'$id' is an invalid ID", e)
            }
        }
    }
}

/**
 * Create a [TimeZone] from an identifier.
 */
@Suppress("FunctionName")
fun TimeZone(id: String): TimeZone {
    return when {
        id == "Z" -> TimeZone.UTC
        id.length < 2 -> throw DateTimeException("'$id' is an invalid ID")
        id.startsWith('-') || id.startsWith('+') -> TimeZone.FixedOffset(id)
        else -> TimeZone.Region(id)
    }
}

/**
 * Convert a UTC offset into a [TimeZone] with a fixed offset.
 */
fun UtcOffset.asTimeZone(): TimeZone = TimeZone.FixedOffset(this)

/**
 * Convert a string to a [TimeZone].
 */
fun String.toTimeZone() = TimeZone(this)

internal const val MAX_TIME_ZONE_STRING_LENGTH = 50

/**
 * A strict parser that expects an offset identical to Island Time's string representation for [UtcOffset].
 */
private val FIXED_TIME_ZONE_PARSER = dateTimeParser {
    utcOffsetSign()
    utcOffsetHours(2)
    +':'
    utcOffsetMinutes(2)
    optional {
        +':'
        utcOffsetSeconds(2)
    }
}