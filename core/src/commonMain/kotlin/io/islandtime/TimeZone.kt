package io.islandtime

import io.islandtime.base.BooleanProperty
import io.islandtime.base.Temporal
import io.islandtime.base.TemporalProperty
import io.islandtime.base.TimeZoneProperty
import io.islandtime.format.TimeZoneTextProvider
import io.islandtime.format.TimeZoneTextStyle
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale
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
sealed class TimeZone : Temporal, Comparable<TimeZone> {

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
     * The localized name of the time zone, if available for the locale in the specified style. The result depends on
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
    fun localizedName(style: TimeZoneTextStyle, locale: Locale = defaultLocale()): String? {
        return TimeZoneTextProvider.timeZoneTextFor(this, style, locale)
    }

    /**
     * A textual representation of the time zone, suitable for display purposes. The localized name will be returned, if
     * available for the locale in the specified style. If not, the [id] will be returned instead.
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
    fun displayName(style: TimeZoneTextStyle, locale: Locale = defaultLocale()): String {
        return localizedName(style, locale) ?: id
    }

    /**
     * Get a normalized time zone.
     *
     * Any time zone with a fixed offset will be converted to use a consistent identifier.
     *
     * @throws TimeZoneRulesException if the current time zone rules provider doesn't support [id]
     */
    abstract fun normalized(): TimeZone

    override fun has(property: TemporalProperty<*>): Boolean {
        return property is TimeZoneProperty
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(property: TemporalProperty<T>): T {
        return when (property) {
            TimeZoneProperty.Id -> id as T
            else -> super.get(property)
        }
    }

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

        override fun get(property: BooleanProperty): Boolean {
            return when (property) {
                TimeZoneProperty.IsFixedOffset -> false
                else -> super.get(property)
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

        override fun get(property: BooleanProperty): Boolean {
            return when (property) {
                TimeZoneProperty.IsFixedOffset -> true
                else -> super.get(property)
            }
        }

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