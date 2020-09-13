package io.islandtime.format

/**
 * A time zone name style.
 *
 * Standard versions indicate the name for standard time, like "Eastern Standard Time". Daylight versions indicate the
 * name of daylight savings time, like "Eastern Daylight Time". Generic is agnostic to daylight savings -- ie.
 * "Eastern Time".
 */
enum class TimeZoneNameStyle {
    LONG_STANDARD,
    SHORT_STANDARD,
    LONG_DAYLIGHT,
    SHORT_DAYLIGHT,
    LONG_GENERIC,
    SHORT_GENERIC;

    /**
     * Checks if this is a short style.
     */
    val isShort: Boolean get() = this == SHORT_STANDARD || this == SHORT_DAYLIGHT || this == SHORT_GENERIC

    /**
     * Checks if this is a long style.
     */
    val isLong: Boolean get() = !isShort

    /**
     * Checks if this is a standard style.
     */
    val isStandard: Boolean get() = this == LONG_STANDARD || this == SHORT_STANDARD

    /**
     * Checks if this is a daylight savings time style.
     */
    val isDaylight: Boolean get() = this == LONG_DAYLIGHT || this == SHORT_DAYLIGHT

    /**
     * Checks if this is a generic style.
     */
    val isGeneric: Boolean get() = this == LONG_GENERIC || this == SHORT_GENERIC

    /**
     * Checks if this is either a standard or daylight savings time style.
     */
    val isSpecific: Boolean get() = !isGeneric

    /**
     * Converts this style to [TextStyle.SHORT] or [TextStyle.FULL].
     */
    fun toTextStyle(): TextStyle = if (isShort) TextStyle.SHORT else TextStyle.FULL
}

/**
 * A time zone name style that can be resolved in the context of a specific instant.
 */
enum class ContextualTimeZoneNameStyle {
    LONG_SPECIFIC,
    SHORT_SPECIFIC,
    LONG_GENERIC,
    SHORT_GENERIC;

    /**
     * Checks if this is a short style.
     */
    val isShort: Boolean get() = this == SHORT_SPECIFIC || this == SHORT_GENERIC

    /**
     * Checks if this is a long style.
     */
    val isLong: Boolean get() = !isShort

    /**
     * Checks if this is a generic style.
     */
    val isGeneric: Boolean get() = this == SHORT_GENERIC || this == LONG_GENERIC

    /**
     * Checks if this is a specific style.
     */
    val isSpecific: Boolean get() = !isGeneric

    /**
     * Converts this contextual style to the corresponding regular style, throwing an exception if this is a specific
     * style that requires the presence or absence of daylight savings to be indicated.
     */
    fun toTimeZoneNameStyle(): TimeZoneNameStyle = when (this) {
        LONG_GENERIC -> TimeZoneNameStyle.LONG_GENERIC
        SHORT_GENERIC -> TimeZoneNameStyle.SHORT_GENERIC
        else -> throw UnsupportedOperationException(
            "Cannot convert '$this' to a regular style without indicating whether or not daylight savings is in effect"
        )
    }

    /**
     * Converts this style to [TextStyle.SHORT] or [TextStyle.FULL].
     */
    fun toTextStyle(): TextStyle = if (isShort) TextStyle.SHORT else TextStyle.FULL

    /**
     * Converts this contextual style to the corresponding regular style, taking daylight savings into account.
     */
    fun toTimeZoneNameStyle(daylight: Boolean): TimeZoneNameStyle = when (this) {
        LONG_SPECIFIC -> if (daylight) {
            TimeZoneNameStyle.LONG_DAYLIGHT
        } else {
            TimeZoneNameStyle.LONG_STANDARD
        }
        SHORT_SPECIFIC -> if (daylight) {
            TimeZoneNameStyle.SHORT_DAYLIGHT
        } else {
            TimeZoneNameStyle.SHORT_STANDARD
        }
        LONG_GENERIC -> TimeZoneNameStyle.LONG_GENERIC
        SHORT_GENERIC -> TimeZoneNameStyle.SHORT_GENERIC
    }

    /**
     * Converts this contextual style to the set of all possible regular styles.
     */
    fun toTimeZoneNameStyleSet(): Set<TimeZoneNameStyle> = when {
        isSpecific -> setOf(toTimeZoneNameStyle(daylight = false), toTimeZoneNameStyle(daylight = true))
        else -> setOf(toTimeZoneNameStyle())
    }

    /**
     * Converts a specific style to a generic style of the same length.
     */
    fun asGeneric(): ContextualTimeZoneNameStyle = if (isLong) LONG_GENERIC else SHORT_GENERIC
}
