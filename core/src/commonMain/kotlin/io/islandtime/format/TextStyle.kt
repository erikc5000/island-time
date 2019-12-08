package io.islandtime.format

// TODO: Make this an expect class typealiased to java.time.TextStyle when Android desugaring is stable
enum class TextStyle {
    FULL,
    FULL_STANDALONE,
    SHORT,
    SHORT_STANDALONE,
    NARROW,
    NARROW_STANDALONE;

    fun isStandalone(): Boolean = (ordinal and 1) == 1

    fun asStandalone(): TextStyle = values()[ordinal or 1]

    fun asNormal(): TextStyle = values()[ordinal and 1.inv()]
}