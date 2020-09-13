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
    fun asStandalone(): TextStyle = values()[ordinal or 1]

    /**
     * Convert to a normal style, if standalone.
     */
    fun asNormal(): TextStyle = values()[ordinal and 1.inv()]
}

