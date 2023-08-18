package io.islandtime.format

// TODO: Make this an expect class typealiased to java.time.TextStyle when Android desugaring is stable
/**
 * A style of text. The meaning may vary depending on the context. Standalone styles should be used when displaying text
 * by itself since some languages have different names in the context of a date and time.
 */
enum class TextStyle {
    FULL,
    FULL_STANDALONE,
    SHORT,
    SHORT_STANDALONE,
    NARROW,
    NARROW_STANDALONE;

    /**
     * Is this a standalone style?
     */
    fun isStandalone(): Boolean = (ordinal and 1) == 1

    /**
     * Convert to a standalone style, if normal.
     */
    fun asStandalone(): TextStyle = entries[ordinal or 1]

    /**
     * Convert to a normal style, if standalone.
     */
    fun asNormal(): TextStyle = entries[ordinal and 1.inv()]
}

/**
 * A time zone text style.
 *
 * Standard versions indicate the name for standard time, like "Eastern Standard Time". Daylight versions indicate the
 * name of daylight savings time, like "Eastern Daylight Time". Generic is agnostic to daylight savings -- ie.
 * "Eastern Time".
 */
enum class TimeZoneTextStyle {
    STANDARD,
    SHORT_STANDARD,
    DAYLIGHT_SAVING,
    SHORT_DAYLIGHT_SAVING,
    GENERIC,
    SHORT_GENERIC;

    /**
     * Is this a short style?
     */
    fun isShort(): Boolean = this == SHORT_STANDARD || this == SHORT_DAYLIGHT_SAVING || this == SHORT_GENERIC

    /**
     * Is this a standard style?
     */
    fun isStandard(): Boolean = this == STANDARD || this == SHORT_STANDARD

    /**
     * Is this a daylight savings style?
     */
    fun isDaylightSaving(): Boolean = this == DAYLIGHT_SAVING || this == SHORT_DAYLIGHT_SAVING

    /**
     * Is this a generic style?
     */
    fun isGeneric(): Boolean = this == GENERIC || this == SHORT_GENERIC
}
