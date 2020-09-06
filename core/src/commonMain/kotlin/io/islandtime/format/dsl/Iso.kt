package io.islandtime.format.dsl

enum class IsoFormat {
    BASIC,
    EXTENDED
}

enum class IsoTimeDesignator(val char: Char?) {
    T('T'),
    NONE(null),
    SPACE(' ') // Non-standard extension
}
