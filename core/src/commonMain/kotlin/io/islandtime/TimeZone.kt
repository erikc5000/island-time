@file:Suppress("FunctionName")

package io.islandtime

import io.islandtime.format.TimeZoneTextProvider
import io.islandtime.format.TimeZoneTextStyle
import io.islandtime.internal.systemDefaultTimeZone
import io.islandtime.locale.Locale
import io.islandtime.measures.nanoseconds
import io.islandtime.measures.seconds
import io.islandtime.parser.*
import io.islandtime.serialization.TimeZoneSerializer
import io.islandtime.zone.FixedTimeZoneRules
import io.islandtime.zone.TimeZoneRules
import io.islandtime.zone.TimeZoneRulesException
import io.islandtime.zone.TimeZoneRulesProvider
import kotlinx.serialization.Serializable

/**
 * A time zone.
 */
@Serializable(with = TimeZoneSerializer::class)
sealed class TimeZone : Comparable<TimeZone> {

    /**
     * An ID that uniquely identifies the time zone.
     */
    abstract val id: String

    /**
     * Checks if this is a valid time zone according to the current time zone rules provider.
     */
    abstract val isValid: Boolean

    /**
     * The rules associated with this time zone.
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     */
    abstract val rules: TimeZoneRules

    /**
     * Checks if this time zone is valid and throws an exception if it isn't.
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
     * Ensures that this time zone is valid, throwing an exception if it isn't.
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     * @see isValid
     */
    fun validated(): TimeZone = apply { validate() }

    /**
     * The localized name of this time zone, if available for the [locale] in the specified style. The result depends on
     * the configured [TimeZoneTextProvider] and may differ between platforms.
     *
     * Example output for the "America/New_York" ID and "en-US" locale:
     * - Standard: "Eastern Standard Time"
     * - Short standard: "EST"
     * - Daylight Saving: "Eastern Daylight Time"
     * - Short daylight saving: "EDT"
     * - Generic: "Eastern Time"
     * - Short generic: "ET"
     *
     * @see displayName
     */
    fun localizedName(style: TimeZoneTextStyle, locale: Locale): String? {
        return TimeZoneTextProvider.timeZoneTextFor(this, style, locale)
    }

    /**
     * A textual representation of this time zone, suitable for display purposes. The localized name will be returned,
     * if available for the [locale] in the specified style. If not, the [id] will be returned instead.
     *
     * The result depends on the configured [TimeZoneTextProvider] and may differ between platforms.
     *
     * Example output for the "America/New_York" ID and "en-US" locale:
     * - Standard: "Eastern Standard Time"
     * - Short standard: "EST"
     * - Daylight Saving: "Eastern Daylight Time"
     * - Short daylight saving: "EDT"
     * - Generic: "Eastern Time"
     * - Short generic: "ET"
     *
     * @see localizedName
     * @see id
     */
    fun displayName(style: TimeZoneTextStyle, locale: Locale): String {
        return localizedName(style, locale) ?: id
    }

    /**
     * Returns a normalized time zone, converting any zone with a fixed offset to use a consistent identifier.
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     */
    abstract fun normalized(): TimeZone

    override fun compareTo(other: TimeZone): Int {
        return id.compareTo(other.id)
    }

    /**
     * Returns the [id] of this time zone.
     */
    override fun toString(): String = id

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

        override val id: String get() = offset.toString()
        override val isValid: Boolean get() = true
        override val rules: TimeZoneRules get() = FixedTimeZoneRules(offset)
        override fun normalized(): FixedOffset = this

        override fun equals(other: Any?): Boolean {
            return this === other || (other is FixedOffset && offset == other.offset)
        }

        override fun hashCode(): Int = offset.hashCode()
    }

    companion object {
        /**
         * A fixed time zone representing UTC.
         */
        val UTC: TimeZone = FixedOffset(UtcOffset.ZERO)

        /**
         * Returns the system's current [TimeZone].
         */
        fun systemDefault(): TimeZone = systemDefaultTimeZone()

        /**
         * Creates a fixed-offset [TimeZone] from an identifier in the form of `+01:00`.
         */
        fun FixedOffset(id: String): FixedOffset {
            return try {
                FixedOffset(id.toUtcOffset(FIXED_TIME_ZONE_PARSER))
            } catch (e: DateTimeParseException) {
                throw TimeZoneRulesException("'$id' is an invalid ID", e)
            }
        }
    }
}

/**
 * Creates a [TimeZone] from an identifier.
 */
fun TimeZone(id: String): TimeZone {
    return when {
        id == "Z" -> TimeZone.UTC
        id.length < 2 -> throw DateTimeException("'$id' is an invalid ID")
        id.startsWith('-') || id.startsWith('+') -> TimeZone.FixedOffset(id)
        else -> TimeZone.Region(id)
    }
}

@Deprecated(
    "Use TimeZone() instead.",
    ReplaceWith("TimeZone(this)"),
    DeprecationLevel.ERROR
)
fun String.toTimeZone(): TimeZone = TimeZone(this)

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
