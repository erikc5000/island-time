package io.islandtime.format

@DslMarker
annotation class IslandTimeFormatDsl

@IslandTimeFormatDsl
interface LiteralFormatBuilder {
    /**
     * Appends a character literal.
     */
    operator fun Char.unaryPlus() {
        literal(this)
    }

    /**
     * Appends a string literal.
     */
    operator fun String.unaryPlus() {
        literal(this)
    }

    /**
     * Appends a character literal.
     */
    fun literal(char: Char)

    /**
     * Appends a string literal.
     */
    fun literal(string: String)
}

/**
 * Indicates when a particular component of a format should be present.
 */
enum class FormatOption {
    NEVER,
    OPTIONAL,
    ALWAYS
}